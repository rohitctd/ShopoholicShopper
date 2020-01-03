package com.shopoholic.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shopoholic.R;
import com.shopoholic.activities.SignupActivity;
import com.shopoholic.interfaces.SelectCountryDialogCallback;
import com.shopoholic.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Dialog for image selection
 */

public class CustomDialogForSelectCountry extends Dialog {

    private final String selectedCountry;
    private final String selectedCountryCode;
    @BindView(R.id.rb_macau)
    RadioButton rbMacau;
    @BindView(R.id.rb_india)
    RadioButton rbIndia;
    @BindView(R.id.rb_singapore)
    RadioButton rbSingapore;
    @BindView(R.id.rb_hong_kong)
    RadioButton rbHongKong;
    @BindView(R.id.rb_uae)
    RadioButton rbUae;
    @BindView(R.id.rg_country)
    RadioGroup rgCountry;
    private Context mContext;
    private SelectCountryDialogCallback selectCountryDialogCallback;

    public CustomDialogForSelectCountry(Context mContext, String selectedCountry, String selectedCountryCode, SelectCountryDialogCallback selectCountryDialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.selectedCountry = selectedCountry + " (" +selectedCountryCode + ")";
        this.selectedCountryCode = selectedCountryCode;
        this.selectCountryDialogCallback = selectCountryDialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_country);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(R.color.colorDialogBackground);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().getAttributes().windowAnimations = R.style.DialogBottomAnimation;
        }

        if (mContext instanceof SignupActivity) {
            if (rbMacau.getText().toString().equals(selectedCountry)) rbMacau.setChecked(true);
            else if (rbIndia.getText().toString().equals(selectedCountry)) rbIndia.setChecked(true);
            else if (rbSingapore.getText().toString().equals(selectedCountry))
                rbSingapore.setChecked(true);
            else if (rbHongKong.getText().toString().equals(selectedCountry))
                rbHongKong.setChecked(true);
            else if (rbUae.getText().toString().equals(selectedCountry)) rbUae.setChecked(true);
        }else {
            switch (selectedCountryCode) {
                case "+853":
                    rbMacau.setChecked(true);
                    break;
                case "+91":
                    rbIndia.setChecked(true);
                    break;
                case "+65":
                    rbSingapore.setChecked(true);
                    break;
                case "+852":
                    rbHongKong.setChecked(true);
                    break;
                case "+971":
                    rbUae.setChecked(true);
                    break;
            }
        }
    }


    @OnClick({R.id.tv_done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_done:
                RadioButton rbCountry = ((RadioButton) findViewById(rgCountry.getCheckedRadioButtonId()));
                if (rbCountry != null) {
                    String country = rbCountry.getText().toString();
                    dismiss();
                    selectCountryDialogCallback.onOkClick(country);
                } else {
                    AppUtils.getInstance().showToast(mContext, mContext.getString(R.string.please_select_one_country));
                }
                break;
        }
    }
}
