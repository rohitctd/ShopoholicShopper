package com.shopoholic.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shopoholic.R;
import com.shopoholic.adapters.CountryCodeAdapter;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForSelectCountry;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.interfaces.SelectCountryDialogCallback;
import com.shopoholic.models.countrymodel.CountryBean;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ChangePhoneNumberActivity extends BaseActivity implements NetworkListener {

    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.tv_country_code)
    CustomTextView tvCountryCode;
    @BindView(R.id.et_phone_no)
    CustomEditText etPhoneNo;
    @BindView(R.id.view_phone_no)
    View viewPhoneNo;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.rv_country_code)
    RecyclerView rvCountryCode;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private ArrayList<CountryBean> selectedCountriesList;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_number);
        ButterKnife.bind(this);
        initVariables();
        setListenersAndSetAdapter();
    }


    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        tvTitle.setText(getString(R.string.change_phone_no));
        ivBack.setVisibility(View.VISIBLE);
        btnAction.setText(getString(R.string.save));
        ArrayList<CountryBean> allCountriesList = new ArrayList<>();
        selectedCountriesList = new ArrayList<>();
        allCountriesList = AppUtils.getInstance().getAllCountries(this);
        selectedCountriesList.addAll(allCountriesList);
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(0);

        tvCountryCode.setText(AppUtils.getInstance().getUserCountryCode(this));
    }


    @Override
    public void afterTextChanged(Editable s) {
        AppUtils.getInstance().changeSeparatorViewColor(etPhoneNo, viewPhoneNo);
    }

    /**
     * method used to set listeners on views and set adapter on views
     */
    private void setListenersAndSetAdapter() {
        etPhoneNo.addTextChangedListener(this);

        CountryCodeAdapter countryAdapter = new CountryCodeAdapter(this, selectedCountriesList, new RecyclerCallBack() {
            @Override
            public void onClick(final int position, View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        bottomSheetBehavior.setPeekHeight(0);
                        tvCountryCode.setText(selectedCountriesList.get(position).getCountryCode());
                    }
                }, 500);
            }
        });
        rvCountryCode.setLayoutManager(new LinearLayoutManager(this));
        rvCountryCode.setAdapter(countryAdapter);
    }

    /**
     * Method to check the validation on fields
     *
     * @return true if every entry is right, else false
     */
    private boolean isValidate() {
        if (tvCountryCode.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_country_code));
            return false;
        } else if (etPhoneNo.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_phone_no));
            return false;
        } else if (etPhoneNo.getText().toString().trim().length() < 7) {
            AppUtils.getInstance().showToast(this, getString(R.string.phone_no_must_be_7_15_digits));
            return false;
        } /*else if (!AppUtils.getInstance().isValidPhoneNumber(tvCountryCode.getText().toString(), etPhoneNo.getText().toString().trim())) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_phone_no));
            return false;
        }*/
        return true;
    }

    @OnClick({R.id.iv_back, R.id.rl_country_code, R.id.btn_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_country_code:
/*
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    }
                }, 500);
*/
                AppUtils.getInstance().hideKeyboard(this);
                CustomDialogForSelectCountry dialogForSelectCountry = new CustomDialogForSelectCountry(this, "", tvCountryCode.getText().toString(), new SelectCountryDialogCallback() {
                    @Override
                    public void onOkClick(String country) {
                        for (CountryBean countryBean : AppUtils.getInstance().getAllCountries(ChangePhoneNumberActivity.this)) {
                            if ((countryBean.getCountryEnglishName() + " (" + countryBean.getCountryCode() + ")").equalsIgnoreCase(country)) {
                                tvCountryCode.setText(countryBean.getCountryCode());
                            }
                        }
                    }
                });
                dialogForSelectCountry.show();
                break;
            case R.id.btn_action:
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    if (isValidate() && !isLoading) {
                        hitSendOtpApi();
                    }
                } else {
                    AppUtils.getInstance().showToast(this, getString(R.string.no_internet_connection));
                }
                break;
        }
    }


    /**
     * Method to hit the signup api
     */
    private void hitSendOtpApi() {
        AppUtils.getInstance().setButtonLoaderAnimation(ChangePhoneNumberActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        isLoading = true;

        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_MOBILE_NO, etPhoneNo.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_COUNTRY_ID, tvCountryCode.getText().toString().trim());
        Call<ResponseBody> call = apiInterface.hitResendOtpApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_FORGOT_PASSWORD);

    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(ChangePhoneNumberActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        isLoading = false;
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_FORGOT_PASSWORD:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            Intent intent = new Intent(this, EnterOtpActivity.class);
                            JSONObject object = new JSONObject(response);
                            JSONObject data = object.optJSONObject(Constants.NetworkConstant.RESULT);
                            intent.putExtra(Constants.IntentConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
                            intent.putExtra(Constants.IntentConstant.COUNTRY_CODE, tvCountryCode.getText().toString().trim());
                            intent.putExtra(Constants.IntentConstant.PHONE_NUMBER, etPhoneNo.getText().toString().trim());
                            intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.CHANGE_PHONE_NUMBER);
                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                            startActivityForResult(intent, Constants.IntentConstant.REQUEST_CHANGE_PHONE_NUMBER);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
        }

    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(ChangePhoneNumberActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(ChangePhoneNumberActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        return super.dispatchTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.IntentConstant.REQUEST_CHANGE_PHONE_NUMBER && resultCode == RESULT_OK) {
            finish();
        }
    }
}
