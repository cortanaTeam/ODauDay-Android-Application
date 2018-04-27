package com.odauday.ui.search.common.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.odauday.R;

/**
 * Created by infamouSs on 4/1/18.
 */

public class FilterOptionLabelView extends LinearLayout {
    
    private TextView mTextViewLabel;
    
    private TextView mTextViewValue;
    
    private TextView mTextViewMoreValue;
    
    private OnCLickFilterOption mListener;
    
    private Object mValue;
    
    
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
    
    public void setTextValue(String value) {
        mTextViewValue.setText(value);
    }
    
    public TextView getTextMoreValue() {
        return mTextViewMoreValue;
    }
    
    public void setTextHeader(String text) {
        this.mTextViewLabel.setText(text);
    }
    
    public Object getValue() {
        return mValue;
    }
    
    public void setValue(Object object) {
        this.mValue = object;
    }
    
    public void setMoreValue(String moreValue) {
        this.mTextViewMoreValue.setText(moreValue);
    }
    
    public void setListener(OnCLickFilterOption listener) {
        mListener = listener;
    }
    
    public interface OnCLickFilterOption {
        
        void onClick();
    }
}
