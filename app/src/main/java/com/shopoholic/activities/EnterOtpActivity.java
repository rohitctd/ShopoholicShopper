package com.shopoholic.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.interfaces.FirebaseAuthListener;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.models.loginresopnse.LoginResponse;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class for request otp and verify it
 */

public class EnterOtpActivity extends BaseActivity implements NetworkListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_message_text)
    CustomTextView tvMessageText;
    @BindView(R.id.et_otp)
    CustomEditText etOtp;
    @BindView(R.id.view_otp)
    View viewOtp;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.tv_timer)
    CustomTextView tvTimer;
    private String countryCode;
    private String phoneNumber;
    private String fromClass;
    private boolean isTicking;
    private CountDownTimer timer;
    private String userId;
    private boolean doubleBackToExitPressedOnce;
    private boolean isLoading;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        ButterKnife.bind(this);
        setListener();
        getIntentData();
        setCountDownTimer();
    }

    /**
     * Method to set listener on views
     */
    private void setListener() {
        btnAction.setText(getString(R.string.submit));
        etOtp.addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        AppUtils.getInstance().changeSeparatorViewColor(etOtp, viewOtp);
    }

    /**
     * method to get data from intent
     */
    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            userId = getIntent().getExtras().getString(Constants.IntentConstant.USER_ID, "");
            countryCode = getIntent().getExtras().getString(Constants.IntentConstant.COUNTRY_CODE, "");
            phoneNumber = getIntent().getExtras().getString(Constants.IntentConstant.PHONE_NUMBER, "");
            fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
        }
        tvTitle.setText(fromClass.equals(Constants.AppConstant.SIGNUP) ? getString(R.string.account_verification) :
                (fromClass.equals(Constants.AppConstant.CHANGE_PHONE_NUMBER) ? getString(R.string.verify_phone_no) : getString(R.string.enter_otp)));
//        ivBack.setVisibility(fromClass.equals(Constants.AppConstant.SIGNUP) ? View.GONE : View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvMessageText.setText(TextUtils.concat(getString(R.string.enter_otp_message) + " " + countryCode + " " + phoneNumber));
    }


    /**
     * method to set the timer
     */
    private void setCountDownTimer() {
        timer = new CountDownTimer(60000, 990) {
            public void onTick(long millisUntilFinished) {
                isTicking = true;
                String time = AppUtils.getInstance().milliSecondsToTimer(millisUntilFinished);
                tvTimer.setText(time);
            }

            public void onFinish() {
                isTicking = false;
                tvTimer.setText(getString(R.string.resend_otp));
            }
        };
        timer.start();
    }


    @OnClick({R.id.iv_back, R.id.btn_action, R.id.tv_timer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_action:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(this)) {
                    if (isValidate()) {
                        hitVerifyOtpApi();
                    }
                }
                break;
            case R.id.tv_timer:
                if (!isTicking && !isLoading) {
                    if (AppUtils.getInstance().isInternetAvailable(EnterOtpActivity.this)) {
                        hitResendOtpApi();
                    } else {
                        AppUtils.getInstance().showToast(EnterOtpActivity.this, getString(R.string.no_internet_connection));
                    }
                }
                break;
        }
    }


    /**
     * set validation on views
     *
     * @return      phone no valid or not
     */
    private boolean isValidate() {
        if (etOtp.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_otp));
            return false;
        } else if (etOtp.getText().toString().trim().length() < 4) {
            AppUtils.getInstance().showToast(this, getString(R.string.incorrect_otp));
            return false;
        }
        return true;
    }


    /**
     * method to hit resend otp api
     */
    private void hitResendOtpApi() {


        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        if (fromClass.equals(Constants.AppConstant.SIGNUP)){
            params.put(Constants.NetworkConstant.PARAM_TEMP_ID, userId);
            params.put(Constants.NetworkConstant.PARAM_USER_ID, "");
        }else {
            params.put(Constants.NetworkConstant.PARAM_USER_ID, userId);
        }
        params.put(Constants.NetworkConstant.PARAM_MOBILE_NO, phoneNumber);
        params.put(Constants.NetworkConstant.PARAM_COUNTRY_ID, countryCode);
        Call<ResponseBody> call = apiInterface.hitResendOtpApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_RESEND_OTP);

//        AppUtils.getInstance().showToast(EnterOtpActivity.this, getString(R.string.otp_resend_successfully));
        timer.start();


    }

    /**
     * method to hit verify otp api
     */
    private void hitVerifyOtpApi() {
        isLoading = true;
        AppUtils.getInstance().setButtonLoaderAnimation(EnterOtpActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        if (fromClass.equals(Constants.AppConstant.SIGNUP)){
            params.put(Constants.NetworkConstant.PARAM_TEMP_ID, userId);
            params.put(Constants.NetworkConstant.PARAM_USER_ID, "");
        }else {
            params.put(Constants.NetworkConstant.PARAM_USER_ID, userId);
        }
        params.put(Constants.NetworkConstant.PARAM_OTP, etOtp.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_MOBILE_NO, phoneNumber);
        params.put(Constants.NetworkConstant.PARAM_COUNTRY_ID, countryCode);
        Call<ResponseBody> call = apiInterface.hitVerifyOtpApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_VERIFY_OTP);


    }

    @Override
    public void onSuccess(int responseCode, final String response, int requestCode) {
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_RESEND_OTP:
                AppUtils.getInstance().setButtonLoaderAnimation(EnterOtpActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                isLoading = false;
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
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
            case Constants.NetworkConstant.REQUEST_VERIFY_OTP:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        switch (fromClass) {
                            case Constants.AppConstant.CHANGE_PHONE_NUMBER:
                                AppUtils.getInstance().setButtonLoaderAnimation(EnterOtpActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                                isLoading = false;
                                setResult(RESULT_OK);
                                AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.PHONE_NO, phoneNumber);
                                AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.COUNTRY_CODE, countryCode);
                                AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.IS_PHONE_VALIDATE, "1");
                                finish();
                                break;
                            case Constants.AppConstant.FORGOT_PASSWORD:
                                AppUtils.getInstance().setButtonLoaderAnimation(EnterOtpActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                                isLoading = false;
                                Intent intent = new Intent(this, ResetPasswordActivity.class);
                                intent.putExtra(Constants.NetworkConstant.USER_ID, userId);
                                AppUtils.getInstance().openNewActivity(this, intent);
//                                finish();
                                break;
                            default:
                                final LoginResponse loginResponse = new Gson().fromJson(response, LoginResponse.class);
                                FirebaseDatabaseQueries.getInstance().signInFirebaseDatabase(loginResponse.getResult().getUserId(), loginResponse.getResult().getEmail(), Constants.AppConstant.FIREBASE_PASSWORD, new FirebaseAuthListener() {
                                    @Override
                                    public void onAuthSuccess(Task<AuthResult> task, FirebaseUser user) {
                                        try {
                                            AppUtils.getInstance().showToast(EnterOtpActivity.this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        AppUtils.getInstance().setButtonLoaderAnimation(EnterOtpActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                                        isLoading = false;
                                        setUserSuccessData(loginResponse);
                                    }

                                    @Override
                                    public void onAuthError(Task<AuthResult> task) {
                                        AppUtils.getInstance().setButtonLoaderAnimation(EnterOtpActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                                        isLoading = false;
                                        AppUtils.getInstance().showToast(EnterOtpActivity.this, getResources().getString(R.string.network_issue));
                                    }
                                });
//                                AppUtils.getInstance().setButtonLoaderAnimation(EnterOtpActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
//                                isLoading = false;
//                                setUserSuccessData(loginResponse);
                                break;
                        }
                        break;
                    default:
                        AppUtils.getInstance().setButtonLoaderAnimation(EnterOtpActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                        isLoading = false;
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

    /**
     * method to set user data on success
     *
     * @param loginResponse
     */
    private void setUserSuccessData(LoginResponse loginResponse) {

        AppUtils.getInstance().showToast(this, loginResponse.getMsg());

        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.USER_ID, loginResponse.getResult().getUserId());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.FIRST_NAME, loginResponse.getResult().getFirstName());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.LAST_NAME, loginResponse.getResult().getLastName());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.EMAIL, loginResponse.getResult().getEmail());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.PHONE_NO, loginResponse.getResult().getMobileNo());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.COUNTRY_CODE, loginResponse.getResult().getCountryId());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE, loginResponse.getResult().getIsEmailVerified());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.IS_PHONE_VALIDATE, loginResponse.getResult().getIsMobileVerified());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.ADDRESS, loginResponse.getResult().getAddress());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.USER_IMAGE, loginResponse.getResult().getImage());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.STATUS, loginResponse.getResult().getStatus());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.GENDER, loginResponse.getResult().getGender());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.DOB, loginResponse.getResult().getDateOfBirth());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.ANNVERSARY, loginResponse.getResult().getAnniversary());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.ACCESS_TOKEN, loginResponse.getResult().getAccesstoken());
        AppSharedPreference.getInstance().putBoolean(this, AppSharedPreference.PREF_KEY.IS_SIGN_UP, true);
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.LATITUDE, loginResponse.getResult().getLatitude());
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.LONGITUDE, loginResponse.getResult().getLongitude());
        AppSharedPreference.getInstance().putBoolean(this, AppSharedPreference.PREF_KEY.IS_NOTIFICATION_ON, loginResponse.getResult().getNotificationStatus().equals("1"));
        AppSharedPreference.getInstance().putBoolean(this, AppSharedPreference.PREF_KEY.IS_LOCATION_ON, loginResponse.getResult().getLocationStatus().equals("1"));
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE, getLanguageName(loginResponse.getResult().getUserLanguage()));
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.WALLET_POINTS, "0");

        FirebaseDatabaseQueries.getInstance().createUser(this);
        Intent intent = new Intent(this, OtherInformationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(EnterOtpActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(EnterOtpActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);

    }


    /**
     * method to get the language from type
     *
     * @param notificationStatus
     * @return
     */
    private String getLanguageName(String notificationStatus) {
        switch (notificationStatus) {
            case "2":
                return Constants.AppConstant.ARABIC;
            case "3":
                return Constants.AppConstant.CHINES_TRAD;
            case "4":
                return Constants.AppConstant.CHINES_SIMPLE;
            default:
                return Constants.AppConstant.ENGLISH;
        }
    }


    @Override
    public void onBackPressed() {
        switch (fromClass) {
            case Constants.AppConstant.SIGNUP:
                /*if (doubleBackToExitPressedOnce) {
                    timer.cancel();
                    Intent intent = new Intent(this, LoginActivity.class);
                    AppUtils.getInstance().openNewActivity(this, intent);
                    finish();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                AppUtils.getInstance().showToast(this, getString(R.string.s_click_back_again_msg));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, Constants.AppConstant.TIME_OUT);*/
                timer.cancel();
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            case Constants.AppConstant.CHANGE_PHONE_NUMBER:
                timer.cancel();
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            default:
                timer.cancel();
                setResult(Activity.RESULT_OK);
                finish();
                break;
        }
    }

}
