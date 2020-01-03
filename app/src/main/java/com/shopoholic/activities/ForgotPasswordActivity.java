package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 21/3/18.
 */

public class ForgotPasswordActivity extends BaseActivity implements NetworkListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_circle_bg)
    ImageView ivCircleBg;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_phone_no)
    TextView tvPhoneNo;
    @BindView(R.id.fl_email_phone)
    FrameLayout flEmailPhone;
    @BindView(R.id.et_email)
    CustomEditText etEmail;
    @BindView(R.id.view_email)
    View viewEmail;
    @BindView(R.id.ll_email)
    LinearLayout llEmail;
    @BindView(R.id.tv_country_code)
    CustomTextView tvCountryCode;
    @BindView(R.id.iv_down)
    ImageView ivDown;
    @BindView(R.id.view_country_code)
    View viewCountryCode;
    @BindView(R.id.rl_country_code)
    RelativeLayout rlCountryCode;
    @BindView(R.id.et_phone_no)
    CustomEditText etPhoneNo;
    @BindView(R.id.view_phone_no)
    View viewPhoneNo;
    @BindView(R.id.rl_phone_no)
    RelativeLayout rlPhoneNo;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.et_search)
    CustomEditText etSearch;
    @BindView(R.id.rv_country_code)
    RecyclerView rvCountryCode;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    @BindView(R.id.tv_message)
    CustomTextView tvMessage;
    private List<CountryBean> allCountriesList;
    private List<CountryBean> selectedCountriesList;
    private CountryCodeAdapter countryAdapter;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private int toggleAnimationStatus = 0;
    private Animation animation;
    private boolean isLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        initVariables();
        setListenersAndSetAdapter();
    }


    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        btnAction.setText(getString(R.string.send_otp));
        allCountriesList = new ArrayList<>();
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

    /**
     * method used to set listeners on views and set adapter on views
     */
    private void setListenersAndSetAdapter() {
//        etSearch.addTextChangedListener(this);
        etEmail.addTextChangedListener(this);
        etPhoneNo.addTextChangedListener(this);


        countryAdapter = new CountryCodeAdapter(this, selectedCountriesList, new RecyclerCallBack() {
            @Override
            public void onClick(final int position, View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        bottomSheetBehavior.setPeekHeight(0);
                        tvCountryCode.setText(selectedCountriesList.get(position).getCountryCode());
                        etSearch.setText("");
                    }
                }, 500);
            }
        });
        rvCountryCode.setLayoutManager(new LinearLayoutManager(this));
        rvCountryCode.setAdapter(countryAdapter);
    }


    @Override
    public void afterTextChanged(Editable s) {
//        search(s.toString());
        AppUtils.getInstance().changeSeparatorViewColor(etEmail, viewEmail);
        AppUtils.getInstance().changeSeparatorViewColor(etPhoneNo, viewPhoneNo);
        AppUtils.getInstance().changeSeparatorViewColor(tvCountryCode, viewCountryCode);
    }

    @SuppressLint("DefaultLocale")
    private void search(String text) {
        selectedCountriesList.clear();
        for (CountryBean country : allCountriesList) {
            if (country.getCountryEnglishName().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase())) {
                selectedCountriesList.add(country);
            }
        }
        countryAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.iv_back, R.id.rl_country_code, R.id.iv_circle_bg, R.id.tv_email, R.id.tv_phone_no, R.id.btn_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
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
                        for (CountryBean countryBean : AppUtils.getInstance().getAllCountries(ForgotPasswordActivity.this)) {
                            if ((countryBean.getCountryEnglishName() + " (" + countryBean.getCountryCode() + ")").equalsIgnoreCase(country)) {
                                tvCountryCode.setText(countryBean.getCountryCode());
                            }
                        }
                    }
                });
                dialogForSelectCountry.show();
                break;
            case R.id.iv_circle_bg:
                switchSelection();
                break;
            case R.id.tv_email:
                if (toggleAnimationStatus == 1) {
                    switchSelection();
                }
                break;
            case R.id.tv_phone_no:
                if (toggleAnimationStatus == 0) {
                    switchSelection();
                }
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


    //method to switch selection
    private void switchSelection() {
        if (toggleAnimationStatus == 0) {
            Animation leftToRight = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
            leftToRight.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    tvEmail.setTextColor(ContextCompat.getColor(ForgotPasswordActivity.this, R.color.colorTabUnselected));
                    tvPhoneNo.setTextColor(Color.WHITE);
                    AppUtils.getInstance().changeViewWithAnimation(llEmail, rlPhoneNo, 400);
                    tvMessage.setText(getString(R.string.forgot_password_phone_message));
//                    AppUtils.getInstance().changeTextWithAnimation(btnSendOtp,getString(R.string.send_reset_link), getString(R.string.send_otp), true);
//                    rlPhoneNo.setVisibility(View.VISIBLE);
//                    llEmail.setVisibility(View.INVISIBLE);
//                    btnSendOtp.setText(getString(R.string.send_otp));
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            });

            ivCircleBg.startAnimation(leftToRight);
            leftToRight.setFillAfter(true);
            toggleAnimationStatus = 1;
        } else {
            Animation rightToLeft = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
            rightToLeft.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    tvPhoneNo.setTextColor(ContextCompat.getColor(ForgotPasswordActivity.this, R.color.colorTabUnselected));
                    tvEmail.setTextColor(Color.WHITE);
                    AppUtils.getInstance().changeViewWithAnimation(rlPhoneNo, llEmail, 400);
                    tvMessage.setText(getString(R.string.forgot_password_email_message));

//                    AppUtils.getInstance().changeTextWithAnimation(btnSendOtp,getString(R.string.send_reset_link), getString(R.string.send_otp), false);
//                    llEmail.setVisibility(View.VISIBLE);
//                    rlPhoneNo.setVisibility(View.INVISIBLE);
//                    btnSendOtp.setText(getString(R.string.send_reset_link));
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            ivCircleBg.startAnimation(rightToLeft);
            rightToLeft.setFillAfter(true);
            toggleAnimationStatus = 0;
        }
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

    /**
     * Method to check the validation on fields
     *
     * @return true if every entry is right, else false
     */
    private boolean isValidate() {
        if (toggleAnimationStatus == 0) {
            if (etEmail.getText().toString().trim().length() == 0) {
                AppUtils.getInstance().showToast(this, getString(R.string.please_enter_email));
                return false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_email));
                return false;
            }
        } else {
            if (tvCountryCode.getText().toString().trim().length() == 0) {
                AppUtils.getInstance().showToast(this, getString(R.string.please_enter_country_code));
                return false;
            } else if (etPhoneNo.getText().toString().trim().length() == 0) {
                AppUtils.getInstance().showToast(this, getString(R.string.please_enter_phone_no));
                return false;
            } else if (etPhoneNo.getText().toString().trim().length() < 7) {
                AppUtils.getInstance().showToast(this, getString(R.string.phone_no_must_be_7_15_digits));
                return false;
            }/* else if (!AppUtils.getInstance().isValidPhoneNumber(tvCountryCode.getText().toString(), etPhoneNo.getText().toString().trim())) {
                AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_phone_no));
                return false;
            }*/
        }

        return true;
    }


    /**
     * Method to hit the signup api
     */
    private void hitSendOtpApi() {
        AppUtils.getInstance().setButtonLoaderAnimation(ForgotPasswordActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        isLoading = true;

        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_EMAIL, toggleAnimationStatus == 1 ? etPhoneNo.getText().toString().trim() : etEmail.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_COUNTRY_ID, toggleAnimationStatus == 1 ? tvCountryCode.getText().toString().trim() : "");
        params.put(Constants.NetworkConstant.PARAM_FORGOT_TYPE, toggleAnimationStatus == 1 ? Constants.NetworkConstant.REQUEST_PHONE : Constants.NetworkConstant.REQUEST_EMAIL);
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, Constants.NetworkConstant.SHOPPER);
        Call<ResponseBody> call = apiInterface.hitForgotPasswordApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_FORGOT_PASSWORD);

    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(ForgotPasswordActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        isLoading = false;
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_FORGOT_PASSWORD:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        if (toggleAnimationStatus == 1) {
                            try {
                                AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                                Intent intent = new Intent(this, EnterOtpActivity.class);
                                JSONObject object = new JSONObject(response);
                                JSONObject data = object.optJSONObject(Constants.NetworkConstant.RESULT);
                                intent.putExtra(Constants.IntentConstant.USER_ID, data.optString(Constants.NetworkConstant.USER_ID));
                                intent.putExtra(Constants.IntentConstant.COUNTRY_CODE, tvCountryCode.getText().toString().trim());
                                intent.putExtra(Constants.IntentConstant.PHONE_NUMBER, etPhoneNo.getText().toString().trim());
                                intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.FORGOT_PASSWORD);
                                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
//                                finish();

                                Intent intent = new Intent(this, EnterOtpActivity.class);
                                JSONObject object = new JSONObject(response);
                                JSONObject data = object.optJSONObject(Constants.NetworkConstant.RESULT);
                                intent.putExtra(Constants.IntentConstant.USER_ID, data.optString(Constants.NetworkConstant.USER_ID));
                                intent.putExtra(Constants.IntentConstant.COUNTRY_CODE, "");
                                intent.putExtra(Constants.IntentConstant.PHONE_NUMBER, etEmail.getText().toString().trim());
                                intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.FORGOT_PASSWORD);
                                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
        AppUtils.getInstance().setButtonLoaderAnimation(ForgotPasswordActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(ForgotPasswordActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);

    }
}
