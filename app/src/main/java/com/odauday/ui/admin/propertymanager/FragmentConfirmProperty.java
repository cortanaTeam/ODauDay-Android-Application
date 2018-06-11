package com.odauday.ui.admin.propertymanager;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.widget.PopupMenu;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.odauday.R;
import com.odauday.data.NotificationManagerRepository;
import com.odauday.data.local.cache.PrefKey;
import com.odauday.data.local.cache.PreferencesHelper;
import com.odauday.databinding.FragmentConfirmPropertyBinding;
import com.odauday.exception.RetrofitException;
import com.odauday.model.Property;
import com.odauday.ui.ClearMemory;
import com.odauday.ui.admin.propertymanager.ConfirmPropertyAdapter.OnClickMenuListener;
import com.odauday.ui.alert.service.Notification;
import com.odauday.ui.base.BaseMVVMFragment;
import com.odauday.ui.favorite.ServiceUnavailableAdapter;
import com.odauday.ui.propertymanager.EmptyPropertyAdapter;
import com.odauday.ui.propertymanager.status.Status;
import com.odauday.utils.SnackBarUtils;
import com.odauday.viewmodel.BaseViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by kunsubin on 5/3/2018.
 */

public class FragmentConfirmProperty extends
                                     BaseMVVMFragment<FragmentConfirmPropertyBinding> implements
                                                                                      ConfirmPropertyContract,ClearMemory {
    
    public static final String TAG = FragmentConfirmProperty.class.getSimpleName();
    
    
    @Inject
    ConfirmPropertyModel mConfirmPropertyModel;
    @Inject
    NotificationManagerRepository mNotificationManagerRepository;
    @Inject
    PreferencesHelper mPreferencesHelper;
    
    private ConfirmPropertyAdapter mConfirmPropertyAdapter;
    private ServiceUnavailableAdapter mServiceUnavailableAdapter;
    private EmptyPropertyAdapter mEmptyPropertyAdapter;
    private RecyclerView mRecyclerView;
    private View mRelativeLayoutLoadMore;
    AlertDialog.Builder mBuilderAlertDialog;
    private int mLimit;
    private int numPage;
    private LinearLayoutManager mLayoutManager;
    private boolean propertyScrolled = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean mStatusGetProperty;
    private PopupMenu mPopupMenu;
    private String mStatus;
    private String mLikeName;
    private Property mPropertyChange;
    private ProgressDialog mProgressDialog;
    
    
    enum Action {
        DELETE, MARK_THE_END, CONFIRM, NONE
    }
    
    private Action mAction;
    
    public static FragmentConfirmProperty newInstance() {
        
        Bundle args = new Bundle();
        
        FragmentConfirmProperty fragment = new FragmentConfirmProperty();
        fragment.setArguments(args);
        return fragment;
    }
    
    private RecyclerView.OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == recyclerView.SCROLL_STATE_SETTLING) {
                propertyScrolled = true;
            }
        }
        
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            
            if (mStatusGetProperty) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager
                    .findFirstVisibleItemPosition();
                
                if (propertyScrolled
                    && (visibleItemCount + pastVisibleItems) == totalItemCount) {
                    
                    propertyScrolled = false;
                    
                    numPage++;
                    getData();
                    
                }
            }
        }
    };
    private ConfirmPropertyAdapter.OnClickMenuListener mOnClickMenuListener = new OnClickMenuListener() {
        @Override
        public void deleteProperty(Property property) {
            Timber.tag(TAG).d("Click: delete");
            actionAdmin(property, getString(R.string.message_delele_property), 1);
        }
        
        @Override
        public void markTheEndProperty(Property property) {
            Timber.tag(TAG).d("Click: mark the end");
            actionAdmin(property, getString(R.string.message_mark_the_end), 2);
        }
        
        @Override
        public void confirmProperty(Property property) {
            Timber.tag(TAG).d("Click: confirm");
            actionAdmin(property, getString(R.string.message_confirm), 3);
        }
    };
    ServiceUnavailableAdapter.OnClickTryAgain mOnClickTryAgain = () -> {
        getData();
    };
    
    @Override
    public int getLayoutId() {
        return R.layout.fragment_confirm_property;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }
    
    private void init() {
        mLimit = 10;
        numPage = 1;
        mStatus = "all";
        mLikeName = mBinding.get().editTextSearch.getText().toString().trim();
        mStatusGetProperty = true;
        mAction = Action.NONE;
        mConfirmPropertyModel.setConfirmPropertyContract(this);
        
        mBuilderAlertDialog = new Builder(getActivity());
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage(getString(R.string.txt_progress));
        
        mBinding.get().setHandler(this);
        mConfirmPropertyAdapter = new ConfirmPropertyAdapter();
        mConfirmPropertyAdapter.setOnClickMenuListener(mOnClickMenuListener);
        mServiceUnavailableAdapter = new ServiceUnavailableAdapter();
        mServiceUnavailableAdapter.setOnClickTryAgain(mOnClickTryAgain);
        mEmptyPropertyAdapter = new EmptyPropertyAdapter();
        mRecyclerView = mBinding.get().recycleViewProperty;
        mRelativeLayoutLoadMore = mBinding.get().relativeLoadMore;
        
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mConfirmPropertyAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }
    
    @Override
    protected BaseViewModel getViewModel(String tag) {
        return null;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        getData();
        setUpSearch();
    }
    
    private void getData() {
        mConfirmPropertyModel
            .getPropertyByAdmin(String.valueOf(numPage), String.valueOf(mLimit), mStatus,
                mLikeName);
    }
    
    private void setUpSearch() {
        RxTextView.afterTextChangeEvents(mBinding.get().editTextSearch)
            .debounce(500, TimeUnit.MILLISECONDS).observeOn(
            AndroidSchedulers.mainThread()).subscribe(success -> {
            updateUI();
        }, throwable -> {
            Timber.tag(TAG).d("Error RxTextView");
        });
    }
    
    private void updateUI() {
        mLimit = 10;
        numPage = 1;
        mStatusGetProperty = true;
        mLikeName = mBinding.get().editTextSearch.getText().toString().trim();
        mConfirmPropertyAdapter.clearData();
        
        getData();
    }
    
    @Override
    protected void processingTaskFromViewModel() {
        mConfirmPropertyModel.response().observe(this, resource -> {
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
    }
    
    @Override
    public void loading(boolean isLoading) {
        Timber.tag(TAG).d("Loading");
        if (isLoading) {
            mRelativeLayoutLoadMore.setVisibility(View.VISIBLE);
        } else {
            mRelativeLayoutLoadMore.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onSuccess(Object object) {
        Timber.tag(TAG).d("Success");
        
        List<Property> list = (List<Property>) object;
        if (list != null && list.size() > 0) {
            if (!(mRecyclerView.getAdapter() instanceof ConfirmPropertyAdapter)) {
                mRecyclerView.setAdapter(mConfirmPropertyAdapter);
            }
            if (mConfirmPropertyAdapter.getItemCount() > 0) {
                mConfirmPropertyAdapter.update(list);
            } else {
                mConfirmPropertyAdapter.setData(list);
            }
            mStatusGetProperty = true;
        } else {
            mStatusGetProperty = false;
        }
        if (mConfirmPropertyAdapter.getItemCount() == 0) {
            mRecyclerView.setAdapter(mEmptyPropertyAdapter);
        }
    }
    
    @Override
    public void onFailure(Exception ex) {
        
        if (mConfirmPropertyAdapter.getItemCount() < 1) {
            if (ex instanceof RetrofitException) {
                mRecyclerView.setAdapter(mServiceUnavailableAdapter);
                SnackBarUtils.showSnackBar(mBinding.get().confirmProperty,
                    getString(R.string.message_service_unavailable));
            } else {
                mRecyclerView.setAdapter(mEmptyPropertyAdapter);
            }
        }
        Timber.tag(TAG).d("Error");
        Timber.tag(TAG).d(ex.getMessage());
    }
    
    @Override
    public void onSuccessAction(Object object) {
        if (mAction == Action.DELETE) {
            if (mPropertyChange != null) {
                mConfirmPropertyAdapter.removeItem(mPropertyChange);
            }
            mAction = Action.NONE;
            return;
        }
        if (mAction == Action.MARK_THE_END) {
            if (mPropertyChange != null) {
                if (mStatus.equals("all")) {
                    mConfirmPropertyAdapter.changeStatusItem(mPropertyChange, Status.EXPIRED);
                } else {
                    mConfirmPropertyAdapter.removeItem(mPropertyChange);
                }
            }
            mAction = Action.NONE;
            return;
        }
        if (mAction == Action.CONFIRM) {
            if (mPropertyChange != null) {
                if (mStatus.equals("all")) {
                    mConfirmPropertyAdapter.changeStatusItem(mPropertyChange, Status.ACTIVE);
                } else {
                    mConfirmPropertyAdapter.removeItem(mPropertyChange);
                }
                sendNotificationToUser(mPropertyChange);
            }
            mAction = Action.NONE;
            return;
        }
    }
    
    @Override
    public void onErrorAction(Object object) {
        mAction = Action.NONE;
        Exception ex = (Exception) object;
        if (ex instanceof RetrofitException) {
            SnackBarUtils.showSnackBar(mBinding.get().confirmProperty,
                getString(R.string.message_service_unavailable));
        } else {
            SnackBarUtils.showSnackBar(mBinding.get().confirmProperty, ex.getMessage());
        }
    }
    
    @Override
    public void onLoadingAction(boolean isLoading) {
        if (isLoading) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }
    
    @Override
    public void onStop() {
        clearMemory();
        super.onStop();
    }
    
    @Override
    public void clearMemory() {
        mBuilderAlertDialog =null;
        mProgressDialog=null;
        mConfirmPropertyAdapter=null;
        mServiceUnavailableAdapter =null;
        mEmptyPropertyAdapter = null;
        mRecyclerView = null;
        mRelativeLayoutLoadMore = null;
        mLayoutManager = null;
    }
    public void onClickSelectStatus(View view) {
        mPopupMenu = new PopupMenu(view.getContext(), view);
        mPopupMenu.getMenuInflater()
            .inflate(R.menu.popup_status_property, mPopupMenu.getMenu());
        
        mPopupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.all:
                    mStatus = "all";
                    mBinding.get().txtStatus.setText(item.getTitle());
                    clear();
                    getData();
                    break;
                case R.id.pending:
                    mStatus = "pending";
                    mBinding.get().txtStatus.setText(item.getTitle());
                    clear();
                    getData();
                    break;
                case R.id.active:
                    clear();
                    mStatus = "active";
                    mBinding.get().txtStatus.setText(item.getTitle());
                    getData();
                    break;
                case R.id.expired:
                    mStatus = "expired";
                    mBinding.get().txtStatus.setText(item.getTitle());
                    clear();
                    getData();
                    break;
                default:
                    break;
            }
            
            return false;
        });
        
        mPopupMenu.show();
    }
    
    public void clear() {
        mLimit = 10;
        numPage = 1;
        mStatusGetProperty = true;
        mBinding.get().editTextSearch.setText("");
        mLikeName = mBinding.get().editTextSearch.getText().toString().trim();
        mConfirmPropertyAdapter.clearData();
    }
    
    public void actionAdmin(Property property, String message, int status) {
        if (property != null) {
            mBuilderAlertDialog.setMessage(message);
            mBuilderAlertDialog.setCancelable(true);
            mBuilderAlertDialog.setIcon(getResources().getDrawable(R.drawable.ic_warning));
            mBuilderAlertDialog
                .setPositiveButton(getString(R.string.txt_ok), (dialogInterface, i) -> {
                    switch (status) {
                        case 1:
                            mPropertyChange = property;
                            mConfirmPropertyModel.deleteProperty(property.getId());
                            mAction = Action.DELETE;
                            break;
                        case 2:
                            mPropertyChange = property;
                            mConfirmPropertyModel
                                .changeStatusProperty(property.getId(), Status.EXPIRED);
                            mAction = Action.MARK_THE_END;
                            break;
                        case 3:
                            mPropertyChange = property;
                            mConfirmPropertyModel
                                .changeStatusProperty(property.getId(), Status.ACTIVE);
                            mAction = Action.CONFIRM;
                            break;
                        default:
                            break;
                        
                    }
                });
            mBuilderAlertDialog
                .setNegativeButton(getString(R.string.txt_cancel), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
            AlertDialog alert11 = mBuilderAlertDialog.create();
            alert11.show();
        }
    }
    
    public void sendNotificationToUser(Property property) {
        Notification notification = new Notification();
        notification.setTitle(getString(R.string.txt_title_notifi_confirm_property));
        notification.setBody(
            getString(R.string.txt_body_notifi_confirm_property) + ": " + property.getAddress());
        notification.setUserID(property.getUser_id_created());
        notification.setPropertyID(property.getId());
        if(property.getImages()!=null){
            notification.setImage(property.getImages().get(0).getUrl());
        }else {
            notification.setImage("");
        }
        
        notification.setType("1");
        
        mNotificationManagerRepository.sendNotification(notification)
            .doOnSubscribe(onSubscribe -> {
                Timber.tag(TAG).d("OnLoadingSendNotification");
            })
            .subscribe(success -> {
                Timber.tag(TAG).d("OnSuccessSendNotification: " + success.getMessage());
            }, error -> {
                Timber.tag(TAG).d("OnErrorSendNotification: " + error.getMessage());
            });
    }
    
}