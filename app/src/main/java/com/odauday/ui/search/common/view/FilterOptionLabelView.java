package com.odauday.ui.search.common.view;

import android.content.Context;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.odauday.R;
import com.odauday.ui.search.common.TextAndMoreTextValue;
import com.odauday.utils.ObjectUtils;
import com.odauday.utils.TextUtils;

/**
 * Created by infamouSs on 4/1/18.
 */
@BindingMethods(
    {
        @BindingMethod(type = FilterOptionLabelView.class,
            attribute = "onToggle", method = "setListener"),
        @BindingMethod(type = FilterOptionLabelView.class,
            attribute = "txtHeader", method = "setTextHeader"),
        @BindingMethod(type = FilterOptionLabelView.class,
            attribute = "txtValue", method = "setTextValue"),
        @BindingMethod(type = FilterOptionLabelView.class,
            attribute = "txtMoreValue", method = "setMoreValue")
    }
)
public class FilterOptionLabelView extends LinearLayout {
    
    private TextView mTextViewLabel;
    
    private TextView mTextViewValue;
    
    private TextView mTextViewMoreValue;
    
    private OnCLickFilterOption mListener;
    
    
    public FilterOptionLabelView(Context context) {
        super(context);
        init(context);
    }
    
    public FilterOptionLabelView(Context context,
        @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public FilterOptionLabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.layout_filter_option_header_value, this, true);
        
        RelativeLayout mainLayout = rootView.findViewById(R.id.main_layout);
        mTextViewLabel = rootView.findViewById(R.id.txt_label);
        mTextViewValue = rootView.findViewById(R.id.txt_value);
        mTextViewMoreValue = rootView.findViewById(R.id.txt_more_value);
        mainLayout.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onClick();
            }
        });
    }
    
    public TextView getTextLabel() {
        return mTextViewLabel;
    }
    
    public TextView getTextValue() {
        return mTextViewValue;
    }
    
    public void setTextValue(int value) {
        mTextViewValue.setText(value);
    }
    
    public void setTextValue(String value) {
        if (TextUtils.isEmpty(value)) {
            if (this.getId() == R.id.filter_location) {
                setTextValue(R.string.txt_map_area);
            } else {
                setTextValue(R.string.txt_any);
            }
            return;
        }
        mTextViewValue.setText(value);
    }
    
    public TextView getTextMoreValue() {
        return mTextViewMoreValue;
    }
    
    public void setTextHeader(String text) {
        this.mTextViewLabel.setText(text);
    }
    
    public void setMoreValue(String moreValue) {
        this.mTextViewMoreValue.setText(moreValue);
    }
    
    public void setMoreValue(int moreValue) {
        this.mTextViewMoreValue.setText(moreValue);
    }
    
    public void setText(TextAndMoreTextValue value) {
        if (ObjectUtils.isNull(value) && this.getId() != R.id.filter_location) {
            setTextValue(R.string.txt_any);
            setMoreValue("");
            return;
        }
        
        if (ObjectUtils.isNull(value) && this.getId() == R.id.filter_location) {
            setTextValue(R.string.txt_map_area);
            setMoreValue("");
            return;
        }
        
        if (TextUtils.isEmpty(value.getText())) {
            if (this.getId() == R.id.filter_location) {
                setTextValue(R.string.txt_map_area);
            } else {
                setTextValue(R.string.txt_any);
            }
            setMoreValue("");
        } else {
            setTextValue(value.getText());
            if (TextUtils.isEmpty(value.getMoreText())) {
                setMoreValue("");
            } else {
                setMoreValue(value.getMoreText());
            }
        }
    }
    
    public void setListener(OnCLickFilterOption listener) {
        mListener = listener;
    }
    
    public void reset() {
        if (this.getId() == R.id.filter_location) {
            getTextValue().setText(R.string.txt_map_area);
        } else {
            getTextValue().setText(R.string.txt_any);
        }
        setMoreValue("");
    }
    
    public interface OnCLickFilterOption {
        
        void onClick();
    }
}
