package com.odauday.ui.addeditproperty.step1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import com.odauday.R;
import com.odauday.data.remote.property.model.GeoLocation;
import com.odauday.databinding.FragmentAddEditStep1Binding;
import com.odauday.model.MyEmail;
import com.odauday.model.MyPhone;
import com.odauday.model.MyProperty;
import com.odauday.ui.addeditproperty.AddEditPropertyActivity;
import com.odauday.ui.addeditproperty.BaseStepFragment;
import com.odauday.ui.base.BaseDialogFragment;
import com.odauday.ui.search.common.view.OnCompletePickedType;
import com.odauday.ui.search.common.view.propertydialog.PropertyTypeDialog;
import com.odauday.ui.search.navigation.FilterOption;
import com.odauday.ui.search.navigation.PropertyType;
import com.odauday.ui.selectlocation.AddressAndLocationObject;
import com.odauday.ui.selectlocation.OnCompleteSelectLocationEvent;
import com.odauday.ui.selectlocation.SelectLocationActivity;
import com.odauday.ui.view.RowItemView;
import com.odauday.ui.view.RowItemView.RowAddedCallBack;
import com.odauday.ui.view.currencyedittext.CurrencyEditText;
import com.odauday.utils.NetworkUtils;
import com.odauday.utils.SnackBarUtils;
import com.odauday.utils.TextUtils;
import com.odauday.utils.ViewUtils;
import com.odauday.viewmodel.BaseViewModel;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

/**
 * Created by infamouSs on 4/24/18.
 */
public class Step1Fragment extends BaseStepFragment<FragmentAddEditStep1Binding> implements
                                                                                 RowAddedCallBack,
                                                                                 OnCompletePickedType {
    
    public static final String TAG = Step1Fragment.class.getSimpleName();
    
    public static final int STEP = 1;
    
    public static Step1Fragment newInstance(MyProperty myProperty) {
        
        Bundle args = new Bundle();
        
        Step1Fragment fragment = new Step1Fragment();
        args.putParcelable(AddEditPropertyActivity.EXTRA_PROPERTY, myProperty);
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Inject
    EventBus mBus;
    
    private MyProperty mProperty;
    
    private Step1Helper mStep1Helper;
    
    @Override
    public int getLayoutId() {
        return R.layout.fragment_add_edit_step_1;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            mProperty = getArguments().getParcelable(AddEditPropertyActivity.EXTRA_PROPERTY);
        }
        if (mProperty == null) {
            mProperty = new MyProperty();
        }
        mStep1Helper = new Step1Helper();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNextButton = mBinding.get().buttonNav.btnNextStep;
        mBackButton = mBinding.get().buttonNav.btnBackStep;
        super.onViewCreated(view, savedInstanceState);
        
        initListener();
        
        mBackButton.setVisibility(View.GONE);
        mBackButton = null;
        
        addRow(PhoneAndEmailEnum.PHONE);
        
        addRow(PhoneAndEmailEnum.EMAIL);
    }
    
    @Override
    public void onDestroy() {
        mStep1Helper = null;
        
        super.onDestroy();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onNextStep(View view) {
        EditText selectLocation = mBinding.get().selectLocation;
        EditText selectCategory = mBinding.get().selectCategory;
        CurrencyEditText txtPrice = mBinding.get().txtPrice;
        RowItemView phoneContainer = mBinding.get().phoneContainer;
        RowItemView emailContainer = mBinding.get().emailContainer;
        
        boolean isValid = mStep1Helper
            .validate(selectLocation, selectCategory, txtPrice, phoneContainer,
                emailContainer, mProperty);
        double price = txtPrice.getRawValue().doubleValue();
        if (price >= 1000000000000d) {
            SnackBarUtils
                .showSnackBar(txtPrice, R.string.message_price_too_large);
            return;
        }
        if (!isValid) {
            return;
        }
        
        String typeId = null;
        if (mBinding.get().radioBtnBuy.isChecked()) {
            typeId = "BUY";
        } else if (mBinding.get().radioBtnRent.isChecked()) {
            typeId = "RENT";
        }
        
        mProperty.setPrice(price);
        
        mProperty.setEmails(
            (List<MyEmail>) emailContainer.getRawValue(PhoneAndEmailEnum.EMAIL.getId()));
        mProperty.setPhones(
            (List<MyPhone>) phoneContainer.getRawValue(PhoneAndEmailEnum.PHONE.getId()));
        
        mProperty.setType_id(typeId);
        mProperty.setTime_contact(mBinding.get().selectTimeContact.getText().toString());
        
        mBus.post(new OnCompleteStep1Event(mProperty));
        
        mNavigationStepListener.navigate(getStep(), getNextStep());
    }
    
    @Override
    public int getStep() {
        return STEP;
    }
    
    @Override
    public int getBackStep() {
        return -1;
    }
    
    @Override
    public void rowItemAddedCallBack() {
        mBinding.get().scrollView
            .postDelayed(() -> mBinding.get().scrollView.fullScroll(View.FOCUS_DOWN), 100);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OnCompleteSelectLocationEvent.REQUEST_CODE && resultCode ==
                                                                         Activity.RESULT_OK &&
            data != null) {
            
            AddressAndLocationObject addressAndLocationObject = data
                .getParcelableExtra(SelectLocationActivity.EXTRA_ADDRESS_AND_LOCATION);
            
            updateSelectLocation(addressAndLocationObject);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onCompletePickedType(int requestCode, Object value) {
        if (requestCode == FilterOption.PROPERTY_TYPE.getRequestCode()) {
            List<Integer> integerList = (List<Integer>) value;
            Timber.d(integerList.toString());
            mProperty.setCategoriesByIntegerList(integerList);
            String text = buildStringCategory(integerList);
            mBinding.get().selectCategory.setText(text);
        }
    }
    
    private void initListener() {
        mBinding.get().selectLocation.setOnClickListener(this::openActivitySelectAddress);
        
        mBinding.get().selectCategory.setOnClickListener(this::openChoosePropertyTypeDialog);
        
        mBinding.get().selectTimeContact.setOnClickListener(this::openDialogSelectContactTime);
        
    }
    
    public void openActivitySelectAddress(View view) {
        if (!NetworkUtils.isNetworkAvailable(this.getContext())) {
            String message = getString(R.string.message_no_internet_access);
            SnackBarUtils.showSnackBar(this.getView(), message);
            return;
        }
        Intent intent = new Intent(this.getContext(), SelectLocationActivity.class);
        if (mProperty.getLocation() != null) {
            intent.putExtra(SelectLocationActivity.EXTRA_LAST_LOCATION,
                new AddressAndLocationObject(TextUtils.formatAddress(mProperty.getAddress()),
                    mProperty.getLocation().toLatLng()));
        }
        this.startActivityForResult(intent, OnCompleteSelectLocationEvent.REQUEST_CODE);
        if (getActivity() == null) {
            return;
        }
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    
    
    private void updateSelectLocation(AddressAndLocationObject object) {
        mProperty.setAddress(object.getAddress());
        mProperty.setLocation(GeoLocation.fromLatLng(object.getLocation()));
        mBinding.get().selectLocation.setText(TextUtils.formatAddress(object.getAddress()));
    }
    
    public void openChoosePropertyTypeDialog(View view) {
        if (getFragmentManager() == null) {
            return;
        }
        List<Integer> selectedPropertyType = PropertyType
            .convertToArrayInt(mProperty.getCategories());
        BaseDialogFragment dialog = PropertyTypeDialog
            .newInstance(selectedPropertyType);
        
        if (dialog == null) {
            return;
        }
        
        dialog.setTargetFragment(this, FilterOption.PROPERTY_TYPE.getRequestCode());
        dialog.show(getFragmentManager(), FilterOption.PROPERTY_TYPE.getTag());
    }
    
    public void openDialogSelectContactTime(View view) {
        
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        
        String[] listData = new String[]{getString(R.string.txt_all_time),
            getString(R.string.txt_select_time)};
        
        dialog.setSingleChoiceItems(listData, 0, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mBinding.get().selectTimeContact.setText(listData[which]);
                        dialog.dismiss();
                        break;
                    case 1:
                        ViewUtils.showDateTimePicker(getContext(), new Date(), new Date(), date -> {
                            mBinding.get().selectTimeContact
                                .setText(TextUtils.formatDateTime(date));
                        });
                        dialog.dismiss();
                        break;
                }
            }
        });
        
        dialog.create().show();
    }
    
    
    private void addRow(PhoneAndEmailEnum type) {
        if (type == PhoneAndEmailEnum.EMAIL) {
            this.mBinding.get().emailContainer
                .addRow(getContext(), type.mTypeInput, type.mText, type.mImage, this);
        } else {
            this.mBinding.get().phoneContainer
                .addRow(getContext(), type.mTypeInput, type.mText, type.mImage, this);
        }
    }
    
    
    private String buildStringCategory(List<Integer> list) {
        StringBuilder builder = new StringBuilder();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                builder.append(
                    getString(PropertyType.getById(list.get(i)).getDisplayStringResource()));
                break;
            }
            builder.append(getString(PropertyType.getById(list.get(i)).getDisplayStringResource()))
                .append(", ");
        }
        
        return builder.toString();
    }
    
    @Override
    protected BaseViewModel getViewModel(String tag) {
        return null;
    }
    
    @Override
    protected void processingTaskFromViewModel() {
    
    }
}
