package com.odauday.ui.favorite;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import com.google.gson.Gson;
import com.odauday.R;
import com.odauday.config.Type;
import com.odauday.data.local.cache.PrefKey;
import com.odauday.data.local.cache.PreferencesHelper;
import com.odauday.data.remote.model.FavoriteResponse;
import com.odauday.data.remote.model.MessageResponse;
import com.odauday.databinding.FragmentFavoriteTabMainBinding;
import com.odauday.exception.RetrofitException;
import com.odauday.model.Property;
import com.odauday.model.PropertyID;
import com.odauday.model.User;
import com.odauday.ui.ClearMemory;
import com.odauday.ui.base.BaseMVVMFragment;
import com.odauday.ui.favorite.FavoriteAdapter.OnClickStarListener;
import com.odauday.ui.favorite.sharefavorite.FragmentDialogFavoriteShare;
import com.odauday.ui.view.HeaderFavoriteView;
import com.odauday.ui.view.bottomnav.NavigationTab;
import com.odauday.utils.SnackBarUtils;
import com.odauday.utils.SortAndFilterUtils;
import com.odauday.viewmodel.BaseViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by infamouSs on 3/31/18.
 */

public class FavoriteTabMainFragment extends
                                     BaseMVVMFragment<FragmentFavoriteTabMainBinding> implements
                                                                                      FavoriteContract,ClearMemory {
    
    
    public static final String TAG = NavigationTab.FAVORITE_TAB.getNameTab();
    
    @Inject
    FavoriteViewModel mFavoriteViewModel;
    @Inject
    PreferencesHelper mPreferencesHelper;
    HeaderFavoriteView mHeaderFavoriteView;
    private List<Property> mProperties;
    private ProgressDialog mProgressDialog;
    private List<PropertyID> mPropertiesIdNeedUnCheck;
    private FavoriteAdapter mFavoriteAdapter;
    private EmptyFavoriteAdapter mEmptyFavoriteAdapter;
    private ServiceUnavailableAdapter mServiceUnavailableAdapter;
    private FragmentDialogFavoriteShare mFragmentDialogFavoriteShare;
    
    enum SortType {
        LAST_ADDED, LOWEST_PRICE, HIGHEST_PRICE
    }
    
    enum FilterType {
        ALL, BUY, RENT
    }
    
    enum STATUS {
        GET, UN_CHECK
    }
    private STATUS mSTATUS = STATUS.GET;
    private SortType SORT_TYPE = SortType.LAST_ADDED;
    private FilterType FILTER_TYPE = FilterType.ALL;
    //Event click open map
    HeaderFavoriteView.OnClickMapListener mOnClickMapListener = view -> {
        Timber.tag(TAG).d("Map click");
    };
    //Call back click send email
    FragmentDialogFavoriteShare.OnClickSendEmailListener mOnClickSendEmailListener = shareFavorite -> {
        if (shareFavorite != null) {
            shareFavorite.setProperties(mProperties);
            mFavoriteViewModel.setFavoriteContract(this);
            mFavoriteViewModel.shareFavorite(shareFavorite);
        }
    };
    //Event click share
    HeaderFavoriteView.OnClickShareListener mOnClickShareListener = view -> {
        Timber.tag(TAG).d("Share click");
        
        String struser=mPreferencesHelper.get(PrefKey.CURRENT_USER,"");
        User user=new Gson().fromJson(struser,User.class);
        if(user==null){
            return;
        }
        mFragmentDialogFavoriteShare = new FragmentDialogFavoriteShare(user.getDisplayName(),
            user.getEmail());
        mFragmentDialogFavoriteShare.setOnClickSendEmailListener(mOnClickSendEmailListener);
        mFragmentDialogFavoriteShare.show(getActivity().getFragmentManager(), "");
        
    };
    FavoriteAdapter.OnClickStarListener mOnClickStarListener = new OnClickStarListener() {
        @Override
        public void onCheckStar(Property property) {
            Timber.tag(TAG).d("Check: " + property.getAddress());
            if (mPropertiesIdNeedUnCheck != null && mPropertiesIdNeedUnCheck.size() > 0) {
                int pos = -1;
                for (int i = 0; i < mPropertiesIdNeedUnCheck.size(); i++) {
                    if (mPropertiesIdNeedUnCheck.get(i).getPropertyId().equals(property.getId())) {
                        pos = i;
                        break;
                    }
                }
                mPropertiesIdNeedUnCheck.remove(pos);
                
            }
            Timber.tag(TAG).d("Check: " + mPropertiesIdNeedUnCheck.size());
        }
        
        @Override
        public void onUnCheckStar(Property property) {
            Timber.tag(TAG).d("UnCheck: " + property.getAddress());
            mPropertiesIdNeedUnCheck.add(new PropertyID(property.getId()));
            Timber.tag(TAG).d("UnCheck: " + mPropertiesIdNeedUnCheck.size());
            
        }
    };
    HeaderFavoriteView.OnItemClickMenuSort mOnItemClickMenuSort = item -> {
        Timber.tag(TAG).d(item.getTitle().toString());
        switch (item.getItemId()) {
            case R.id.last_added:
                SORT_TYPE = SortType.LAST_ADDED;
                showDataView();
                break;
            case R.id.lowest_price:
                SORT_TYPE = SortType.LOWEST_PRICE;
                showDataView();
                break;
            case R.id.highest_price:
                SORT_TYPE = SortType.HIGHEST_PRICE;
                showDataView();
                break;
        }
        mHeaderFavoriteView.setTextViewSort(item.getTitle().toString());
    };
    HeaderFavoriteView.OnItemClickMenuFilter mOnItemClickMenuFilter = item -> {
        Timber.tag(TAG).d(item.getTitle().toString());
        switch (item.getItemId()) {
            case R.id.all:
                FILTER_TYPE = FilterType.ALL;
                showDataView();
                break;
            case R.id.buy:
                FILTER_TYPE = FilterType.BUY;
                showDataView();
                break;
            case R.id.rent:
                FILTER_TYPE = FilterType.RENT;
                showDataView();
                break;
        }
        mHeaderFavoriteView.setTextViewFilter(item.getTitle().toString());
    };
    
    public static FavoriteTabMainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FavoriteTabMainFragment fragment = new FavoriteTabMainFragment();
        fragment.setArguments(args);
        
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_favorite_tab_main;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initHeaderView();
    }

    private void initHeaderView() {
        mProperties = new ArrayList<>();
        mPropertiesIdNeedUnCheck = new ArrayList<>();
        mHeaderFavoriteView = mBinding.get().headerFavorite;
        
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage(getActivity().getString(R.string.txt_progress));
        mProgressDialog.show();
        
        mHeaderFavoriteView.setTitle(getResources().getString(R.string.txt_shortlist));
        mHeaderFavoriteView.setOnClickShareListener(mOnClickShareListener);
        mHeaderFavoriteView.setOnClickMapListener(mOnClickMapListener);
        mHeaderFavoriteView.setOnItemClickMenuSort(mOnItemClickMenuSort);
        mHeaderFavoriteView.setOnItemClickMenuFilter(mOnItemClickMenuFilter);
    }
    
    @Override
    protected BaseViewModel getViewModel(String tag) {
        return mFavoriteViewModel;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        getFavorite();
    }
    
    private void getFavorite() {
        mSTATUS = STATUS.GET;
        //mPreferencesHelper.put(PrefKey.USER_ID, "a88211ba-3077-40e2-9685-5ab450abb114");
        //mPreferencesHelper.put(PrefKey.ACCESS_TOKEN, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImlkIjoiYTg4MjExYmEtMzA3Ny00MGUyLTk2ODUtNWFiNDUwYWJiMTE0IiwiZW1haWwiOiJkYW9odXVsb2M5NDE5QGdtYWlsLmNvbSIsImRpc3BsYXlfbmFtZSI6ImluZmFtb3VTcyIsInBob25lIjpudWxsLCJhdmF0YXIiOm51bGwsInJvbGUiOiJ1c2VyIiwic3RhdHVzIjoicGVuZGluZyIsImZhY2Vib29rX2lkIjpudWxsLCJhbW91bnQiOm51bGx9LCJpYXQiOjE1MjIyMTM4NzN9.kbl64zvlPOFlc8NdFqlcbAc-I5I7D1WVC_BwUYearjs");
        mFavoriteViewModel.getFavoritePropertyByUser(mPreferencesHelper.get(PrefKey.USER_ID, ""));
    }
    
    @Override
    protected void processingTaskFromViewModel() {
        mFavoriteViewModel.response().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case ERROR:
                        onFailure((Exception) resource.data);
                        loading(false);
                        break;
                    case SUCCESS:
                        onSuccess(resource.data);
                        loading(false);
                        break;
                    case LOADING:
                        loading(true);
                        break;
                    default:
                        break;
                }
            }
        });
        mBinding.get().recycleViewFavorite
            .setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mBinding.get().recycleViewFavorite.setNestedScrollingEnabled(false);
        mFavoriteAdapter = new FavoriteAdapter();
        mFavoriteAdapter.setOnClickStarListeners(mOnClickStarListener);
        
        mEmptyFavoriteAdapter = new EmptyFavoriteAdapter();
        mServiceUnavailableAdapter = new ServiceUnavailableAdapter();
        
    }
    
    private void showDataView() {
        List<Property> propertyList = getPropertyFilter(mProperties);
        if (propertyList != null && propertyList.size() > 0) {
            if (!(mBinding.get().recycleViewFavorite.getAdapter() instanceof FavoriteAdapter)) {
                mBinding.get().recycleViewFavorite.setAdapter(mFavoriteAdapter);
            }
            mFavoriteAdapter.setData(propertyList);
        } else {
            mBinding.get().recycleViewFavorite.setAdapter(mEmptyFavoriteAdapter);
        }
    }
    
    private List<Property> getPropertyFilter(List<Property> propertyList) {
        List<Property> list = propertyList;
        switch (FILTER_TYPE) {
            case ALL:
                break;
            case BUY:
                list = SortAndFilterUtils.getListPropertyByType(list, Type.BUY);
                break;
            case RENT:
                list = SortAndFilterUtils.getListPropertyByType(list, Type.RENT);
                break;
            default:
                break;
        }
        if (list == null || list.size()<1) {
            return list;
        }
        switch (SORT_TYPE) {
            case LAST_ADDED:
                break;
            case LOWEST_PRICE:
                list = SortAndFilterUtils.sortListPropertyLowestPrice(list);
                break;
            case HIGHEST_PRICE:
                list = SortAndFilterUtils.sortListPropertyHighestPrice(list);
                break;
            default:
                break;
        }
        return list;
    }
    @Override
    public void loading(boolean isLoading) {
        if (isLoading) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
        Timber.tag(TAG).d("Loading");
    }
    
    @Override
    public void onSuccess(Object object) {
        if (mSTATUS == STATUS.UN_CHECK) {
            mSTATUS = STATUS.GET;
            return;
        }
        FavoriteResponse favoriteResponse = (FavoriteResponse) object;
        if (favoriteResponse != null) {
            List<Property> list = favoriteResponse.getProperties();
            if (list != null&&list.size()>0) {
                mProperties = SortAndFilterUtils.sortFavoriteLastAdded(list);
                showDataView();
            }
            
        } else {
            Timber.tag(TAG).d("Null Favorite");
            mBinding.get().recycleViewFavorite.setAdapter(mEmptyFavoriteAdapter);
        }
    }
    
    @Override
    public void onFailure(Exception ex) {
        if (mSTATUS == STATUS.UN_CHECK) {
            mSTATUS = STATUS.GET;
            String message;
            message = getActivity().getString(R.string.cannot_uncheck_favorites);
            SnackBarUtils.showSnackBar(mBinding.get().shortlist, message);
            return;
        }
        Timber.tag(TAG).d(ex.getMessage());
        
        if (ex instanceof RetrofitException) {
            mBinding.get().recycleViewFavorite.setAdapter(mServiceUnavailableAdapter);
        } else {
            mBinding.get().recycleViewFavorite.setAdapter(mEmptyFavoriteAdapter);
        }
    }
    
    @Override
    public void shareFavoriteSuccess(Object object) {
        MessageResponse messageResponse = (MessageResponse) object;
        Timber.tag(TAG).d("Success share: " + messageResponse.getMessage());
        SnackBarUtils.showSnackBar(mBinding.get().shortlist, messageResponse.getMessage());
    }
    
    @Override
    public void shareFavoriteError(Object object) {
        Exception exception = (Exception) object;
        Timber.tag(TAG).d("Error share: " + exception.getMessage());
        String message;
        message = getActivity().getString(R.string.error_share_favorite);
        SnackBarUtils.showSnackBar(mBinding.get().shortlist, message);
    }
    
    @Override
    public void onStop() {
        unCheckFavorites();
        clearMemory();
        super.onStop();
       
    }
    
    public void unCheckFavorites() {
        mSTATUS = STATUS.UN_CHECK;
        if (mPropertiesIdNeedUnCheck != null && mPropertiesIdNeedUnCheck.size() > 0) {
            mFavoriteViewModel.unCheckFavorites(mPropertiesIdNeedUnCheck);
        }
    }
    
    @Override
    public void clearMemory() {
        mHeaderFavoriteView=null;
        mProperties=null;
        mProgressDialog=null;
        mPropertiesIdNeedUnCheck=null;
        mFavoriteAdapter=null;
        mEmptyFavoriteAdapter=null;
        mServiceUnavailableAdapter=null;
        mFragmentDialogFavoriteShare=null;
    }
}
