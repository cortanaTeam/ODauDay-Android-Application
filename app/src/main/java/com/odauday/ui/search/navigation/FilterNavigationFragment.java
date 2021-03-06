package com.odauday.ui.search.navigation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.odauday.R;
import com.odauday.config.AppConfig;
import com.odauday.data.SearchPropertyRepository;
import com.odauday.data.local.cache.MapPreferenceHelper;
import com.odauday.data.remote.property.model.SearchRequest;
import com.odauday.databinding.FragmentFilterBinding;
import com.odauday.model.Tag;
import com.odauday.ui.base.BaseMVVMFragment;
import com.odauday.ui.search.common.MinMaxObject;
import com.odauday.ui.search.common.SearchCriteria;
import com.odauday.ui.search.common.TextAndMoreTextValue;
import com.odauday.ui.search.common.event.OnUpdateCriteriaEvent;
import com.odauday.ui.search.common.view.FilterNumberPickerDialog.OnCompletePickedNumberListener;
import com.odauday.ui.search.common.view.OnCompletePickedType;
import com.odauday.ui.search.common.view.PickerMinMaxReturnObject;
import com.odauday.ui.search.common.view.tagdialog.TagChip;
import com.odauday.utils.TextUtils;
import com.odauday.viewmodel.BaseViewModel;
import com.pchmn.materialchips.model.ChipInterface;
import java.util.List;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import timber.log.Timber;

/**
 * Created by infamouSs on 4/1/18.
 */

public class FilterNavigationFragment extends BaseMVVMFragment<FragmentFilterBinding> implements
                                                                                      OnCompletePickedNumberListener,
                                                                                      OnCompletePickedType,
                                                                                      FilterFragmentContract {
    
    //====================== Variable =========================//
    public static final String TAG = FilterNavigationFragment.class.getSimpleName();
    
    @Inject
    FilterNavigationViewModel mFilterNavigationViewModel;
    
    @Inject
    SearchPropertyRepository mSearchPropertyRepository;
    
    @Inject
    MapPreferenceHelper mMapPreferenceHelper;
    
    @Inject
    EventBus mBus;
    
    private OnCompleteRefineFilter mOnCompleteRefineFilter;
    
    private SearchCriteria mSearchCriteria;
    
    private boolean mIsFirstTime = true;
    
    //====================== Constructor =========================//
    public static FilterNavigationFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FilterNavigationFragment fragment = new FilterNavigationFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    //====================== Override Base Method =========================//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus.register(this);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mBinding.get().setViewModel(mFilterNavigationViewModel);
        
        mFilterNavigationViewModel.setFragment(this);
        int lastSearchMode = mMapPreferenceHelper.getLastSearchMode();
        
        if (mSearchPropertyRepository.getCurrentSearchRequest() == null) {
            SearchCriteria searchCriteria = mMapPreferenceHelper
                .getRecentSearchCriteria(lastSearchMode);
            this.mSearchPropertyRepository
                .setCurrentSearchRequest(
                    new SearchRequest(searchCriteria));
        }
        setSearchCriteria(mMapPreferenceHelper.getRecentSearchCriteria(lastSearchMode));
    }
    
    @Override
    public int getLayoutId() {
        return R.layout.fragment_filter;
    }
    
    @Override
    protected BaseViewModel getViewModel(String tag) {
        return mFilterNavigationViewModel;
    }
    
    @Override
    protected void processingTaskFromViewModel() {
        mFilterNavigationViewModel.response().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case ERROR:
                        onErrorCreateTag((Exception) resource.data);
                        break;
                    case SUCCESS:
                        onSuccessCreateTag((long) (resource.data));
                        break;
                    case LOADING:
                        break;
                    default:
                        break;
                }
            }
        });
    }
    
    @Override
    public void onDestroy() {
        mBus.unregister(this);
        mOnCompleteRefineFilter = null;
        mSearchCriteria = null;
        super.onDestroy();
    }
    
    //====================== Implement method =========================//
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedUpdateCriteria(OnUpdateCriteriaEvent event) {
        SearchCriteria searchCriteria = mSearchPropertyRepository.getCurrentSearchRequest()
            .getCriteria();
        
        setSearchCriteria(searchCriteria);
    }
    
    @Override
    public void onCompletePickedNumber(int requestCode,
        PickerMinMaxReturnObject minMaxReturnObject) {
        FilterOption option = FilterOption.getByRequestCode(requestCode);
        switch (option) {
            case PRICE:
//                MinMaxObject<Integer> newValueWithRate = new MinMaxObject<>();
//                newValueWithRate.setMin(minMaxReturnObject.getValue().getMin() * AppConfig.RATE_VND);
//                newValueWithRate.setMin(minMaxReturnObject.getValue().getMax() * AppConfig.RATE_VND);
//                minMaxReturnObject.setValue(newValueWithRate);
                mSearchCriteria.setPrice(minMaxReturnObject.getValue());
                String textPrice = mFilterNavigationViewModel
                    .getMaxMinText(option, minMaxReturnObject);
                if (textPrice.split("-").length == 2) {
                    String[] textPricePart = textPrice.split("-");
                    String text = TextUtils.build(
                        getString(R.string.txt_from),
                        " ",
                        textPricePart[0].trim());
                    
                    String moreText = TextUtils.build(
                        getString(R.string.txt_to),
                        " ",
                        textPricePart[1].trim());
                    mBinding.get().filterPrice.setTextValue(text);
                    mBinding.get().filterPrice.setMoreValue(moreText);
                    
                    mSearchCriteria.getDisplay()
                        .setDisplayPrice(new TextAndMoreTextValue(text, moreText));
                    return;
                }
                mBinding.get().filterPrice.setMoreValue("");
                mBinding.get().filterPrice.setTextValue(textPrice);
                mSearchCriteria.getDisplay()
                    .setDisplayPrice(new TextAndMoreTextValue(textPrice, ""));
                
                break;
            case SIZE:
                mSearchCriteria.setSize(minMaxReturnObject.getValue());
                String textSize = mFilterNavigationViewModel
                    .getMaxMinText(option, minMaxReturnObject);
                mBinding.get().filterSize.setTextValue(textSize);
                mSearchCriteria.getDisplay().setDisplaySize(textSize);
                break;
            case BEDROOMS:
                mSearchCriteria.setBedrooms(minMaxReturnObject.getValue());
                String textBed = mFilterNavigationViewModel
                    .getMaxMinText(option, minMaxReturnObject);
                mBinding.get().filterBedrooms.setTextValue(textBed);
                mSearchCriteria.getDisplay().setDisplayBedroom(textBed);
                break;
            case BATHROOMS:
                mSearchCriteria.setBathrooms(minMaxReturnObject.getValue());
                String textBath = mFilterNavigationViewModel
                    .getMaxMinText(option, minMaxReturnObject);
                mBinding.get().filterBathRooms.setTextValue(textBath);
                mSearchCriteria.getDisplay().setDisplayBathroom(textBath);
                break;
            case PARKING:
                mSearchCriteria.setParking(minMaxReturnObject.getValue());
                String textPark = mFilterNavigationViewModel
                    .getMaxMinText(option, minMaxReturnObject);
                mBinding.get().filterParking.setTextValue(textPark);
                mSearchCriteria.getDisplay().setDisplayParking(textPark);
                break;
            default:
                break;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onCompletePickedType(int requestCode, Object value) {
        FilterOption option = FilterOption.getByRequestCode(requestCode);
        switch (option) {
            case PROPERTY_TYPE:
                List<Integer> listSelectedPropertyType = (List<Integer>) value;
                mSearchCriteria.setPropertyTypeByListInteger(listSelectedPropertyType);
                TextAndMoreTextValue displayValueProperty = TextAndMoreTextValue
                    .build(getContext(), option, mSearchCriteria.getPropertyType());
                
                mBinding.get().filterPropertyType.setTextValue(displayValueProperty.getText());
                mBinding.get().filterPropertyType.setMoreValue(displayValueProperty.getMoreText());
                mSearchCriteria.getDisplay().setDisplayPropertyType(displayValueProperty);
                break;
            case TAGS:
                List<Tag> selectedTag = TagChip.convertToTag((List<ChipInterface>) value);
                mSearchCriteria.setTags(selectedTag);
                TextAndMoreTextValue displayValueTags = TextAndMoreTextValue
                    .build(getContext(), option, mSearchCriteria.getTags());
                mBinding.get().filterTag.setTextValue(displayValueTags.getText());
                mBinding.get().filterTag.setMoreValue(displayValueTags.getMoreText());
                
                mFilterNavigationViewModel.insertCurrentTags(selectedTag);
                
                mSearchCriteria.getDisplay().setDisplayTag(displayValueTags);
                break;
            default:
                break;
        }
    }
    
    //====================== Contract method =========================//
    
    @Override
    public void onSuccessCreateTag(long data) {
        Timber.tag(TAG).i("Insert tags successfully");
    }
    
    @Override
    public void onErrorCreateTag(Exception ex) {
        Timber.tag(TAG).i(ex.getMessage());
    }
    //====================== Helper method =========================//
    
    public SearchCriteria getSearchCriteria() {
        return mSearchCriteria;
    }
    
    public void setSearchCriteria(SearchCriteria searchCriteria) {
        mSearchCriteria = searchCriteria;
        refreshViewWithSearchCriteria();
    }
    
    private void refreshViewWithSearchCriteria() {
        if (mBinding.get().filterPrice.getVisibility() == View.VISIBLE) {
            TextAndMoreTextValue displayPrice = mSearchCriteria.getDisplay().getDisplayPrice();
            mBinding.get().filterPrice.setText(displayPrice);
        }
        String displayLocation = mSearchCriteria.getDisplay().getDisplayLocation();
        mBinding.get().filterLocation.setTextValue(displayLocation);
        
        String displaySize = mSearchCriteria.getDisplay().getDisplaySize();
        mBinding.get().filterSize.setTextValue(displaySize);
        
        TextAndMoreTextValue displayPropertyType = mSearchCriteria.getDisplay()
            .getDisplayPropertyType();
        mBinding.get().filterPropertyType.setText(displayPropertyType);
        
        String displayBedroom = mSearchCriteria.getDisplay().getDisplayBedroom();
        mBinding.get().filterBedrooms.setTextValue(displayBedroom);
        
        String displayBathroom = mSearchCriteria.getDisplay().getDisplayBathroom();
        mBinding.get().filterBathRooms.setTextValue(displayBathroom);
        
        String displayParking = mSearchCriteria.getDisplay().getDisplayParking();
        mBinding.get().filterParking.setTextValue(displayParking);
        
        TextAndMoreTextValue displayTag = mSearchCriteria.getDisplay().getDisplayTag();
        mBinding.get().filterTag.setText(displayTag);
        
        mBinding.get().filterSearchType.getSpinner().setSelection(mSearchCriteria.getSearchType());
    }
    
    private void resetFilter() {
        mBinding.get().filterLocation.reset();
        mBinding.get().filterPrice.reset();
        mBinding.get().filterSize.reset();
        mBinding.get().filterPropertyType.reset();
        mBinding.get().filterBedrooms.reset();
        mBinding.get().filterBathRooms.reset();
        mBinding.get().filterParking.reset();
        mBinding.get().filterTag.reset();
    }
    
    public OnCompleteRefineFilter getOnCompleteRefineFilter() {
        return mOnCompleteRefineFilter;
        
    }
    
    public void setOnCompleteRefineFilter(
        OnCompleteRefineFilter onCompleteRefineFilter) {
        mOnCompleteRefineFilter = onCompleteRefineFilter;
    }
    
    public boolean isFirstTime() {
        return mIsFirstTime;
    }
    
    public void setFirstTime(boolean firstTime) {
        mIsFirstTime = firstTime;
    }
    
    public MapPreferenceHelper getMapPreferenceHelper() {
        return mMapPreferenceHelper;
    }
    
    
    public interface OnCompleteRefineFilter {
        
        void onCompleteRefineFilter(SearchCriteria searchCriteria);
    }
}
