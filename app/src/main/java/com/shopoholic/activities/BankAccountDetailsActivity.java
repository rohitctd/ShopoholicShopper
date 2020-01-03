package com.shopoholic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Class created by Sachin on 30-Mar-18.
 */

public class BankAccountDetailsActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_choose_bank)
    CustomTextView tvChooseBank;
    @BindView(R.id.view_choose_bank)
    View viewChooseBank;
    @BindView(R.id.et_account_number)
    CustomEditText etAccountNumber;
    @BindView(R.id.view_account_number)
    View viewAccountNumber;
    @BindView(R.id.et_name)
    CustomEditText etName;
    @BindView(R.id.view_name)
    View viewName;
    @BindView(R.id.et_bank_ifsc_code)
    CustomEditText etBankIfscCode;
    @BindView(R.id.view_bank_ifsc_code)
    View viewBankIfscCode;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.tv_skip)
    CustomTextView tvSkip;
    private String fromClass = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account_details);
        ButterKnife.bind(this);
        initVariablesAndSetData();
    }

    /**
     * method to initialize the variables and set data
     */
    private void initVariablesAndSetData() {
        btnAction.setText(getString(R.string.save_and_next));
        if (getIntent() != null && getIntent().getExtras() != null) {
            fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
            if (fromClass.equals(Constants.AppConstant.SETTING)) {
                btnAction.setText(getString(R.string.save));
                tvSkip.setVisibility(View.GONE);
            }
        }
        tvChooseBank.requestFocus();

    }

    @OnClick({R.id.iv_back, R.id.tv_choose_bank, R.id.btn_action, R.id.tv_skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_choose_bank:
                AppUtils.getInstance().hideKeyboard(this);
                break;
            case R.id.btn_action:
                if (!fromClass.equals(Constants.AppConstant.SETTING)) {
                    AppSharedPreference.getInstance().putBoolean(this, AppSharedPreference.PREF_KEY.IS_BANK_DETAIL_SET, true);
                    startActivity(new Intent(this, HomeActivity.class));
                }
                finish();
                break;
            case R.id.tv_skip:
                AppSharedPreference.getInstance().putBoolean(this, AppSharedPreference.PREF_KEY.IS_BANK_DETAIL_SET, true);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
        }
    }
}
