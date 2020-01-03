package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dnitinverma.fblibrary.FBSignInAI;
import com.dnitinverma.fblibrary.interfaces.FBSignCallback;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.googlelibrary.GoogleSignInAI;
import com.googlelibrary.interfaces.GoogleSignCallback;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.customviews.CustomTypefaceSpan;
import com.shopoholic.dialogs.CustomDialogForSelectCountry;
import com.shopoholic.firebasechat.interfaces.FirebaseAuthListener;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.interfaces.SelectCountryDialogCallback;
import com.shopoholic.models.UserSocialModel;
import com.shopoholic.models.countrymodel.CountryBean;
import com.shopoholic.models.loginresopnse.LoginResponse;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static com.shopoholic.utils.Constants.IntentConstant.REQUEST_LOCATION;

/**
 * Activity used for login
 */

public class LoginActivity extends BaseActivity implements FBSignCallback, GoogleSignCallback, NetworkListener, View.OnTouchListener {

    private final int FB_LOGIN_REQUEST_CODE = 64206;  //Fb Default request code
    private final int GOOGLE_LOGIN_REQUEST_CODE = 1001;
    private final int FACEBOOK_LOGIN = 101;
    private final int GOOGLE_LOGIN = 102;
    private final int NORMAL_LOGIN = 103;
    @BindView(R.id.et_email_and_phone)
    CustomEditText etEmailAndPhone;
    @BindView(R.id.view_email_and_phone)
    View viewEmailAndPhone;
    @BindView(R.id.tv_country_code)
    CustomTextView tvCountryCode;
    @BindView(R.id.iv_down)
    ImageView ivDown;
    @BindView(R.id.view_country_code)
    View viewCountryCode;
    @BindView(R.id.rl_country_code)
    RelativeLayout rlCountryCode;
    @BindView(R.id.rl_phone_no)
    RelativeLayout rlPhoneNo;
    @BindView(R.id.et_password)
    CustomEditText etPassword;
    @BindView(R.id.view_password)
    View viewPassword;
    @BindView(R.id.tv_forgot_password)
    CustomTextView tvForgotPassword;
    @BindView(R.id.iv_human)
    ImageView ivHuman;
    @BindView(R.id.tv_human)
    CustomTextView tvHuman;
    @BindView(R.id.iv_circle_bg)
    ImageView ivCircleBg;
    @BindView(R.id.rl_switch)
    FrameLayout rlSwitch;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.iv_facebook)
    ImageView ivFacebook;
    @BindView(R.id.iv_google)
    ImageView ivGoogle;
    @BindView(R.id.tv_sign_up)
    CustomTextView tvSignUp;
    @BindView(R.id.btn_guest)
    CustomTextView btnGuest;


    private FBSignInAI mFBSignInAI;
    private GoogleSignInAI mGoogleSignInAI;
    private boolean isLoading;
    private boolean isShowing;
    private int loginType;
    private int toggleAnimationStatus = 0;
    private UserSocialModel userSocialModel;
    private Animation animation;
    private boolean isPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initializeFB();
        initializeGoogle();
        initVariablesAndSetListener();
        setSpannableString();
        etEmailAndPhone.requestFocus();
        ActivityCompat.requestPermissions(this, new String[] {ACCESS_COARSE_LOCATION, READ_CONTACTS}, REQUEST_LOCATION);
    }

    /**
     * function to initialize variables and set listeners on views
     */
    private void initVariablesAndSetListener() {
        btnAction.setText(getString(R.string.log_in));
        etPassword.setOnTouchListener(this);
        etEmailAndPhone.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        tvCountryCode.addTextChangedListener(this);
        tvCountryCode.setText(AppUtils.getInstance().getUserCountryCode(this));
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        Drawable drawable;
        if (etPassword.getCompoundDrawables()[DRAWABLE_RIGHT] != null) drawable = etPassword.getCompoundDrawables()[DRAWABLE_RIGHT];
        else drawable = etPassword.getCompoundDrawables()[DRAWABLE_LEFT];
        if (drawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (etPassword.getRight() - drawable.getBounds().width())) {
                // your action here
                if (!isShowing) {
                    isShowing = true;
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_login_password_visible, 0);
                    etPassword.setTransformationMethod(null);
                    etPassword.setSelection(etPassword.getText().length());
                } else {
                    isShowing = false;
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_login_password_not_visible, 0);
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    etPassword.setSelection(etPassword.getText().length());
                }
                etPassword.setSelection(etPassword.getText().length());
                etPassword.requestFocus();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        AppUtils.getInstance().changeSeparatorViewColor(etEmailAndPhone, viewEmailAndPhone);
        AppUtils.getInstance().changeSeparatorViewColor(etPassword, viewPassword);
        AppUtils.getInstance().changeSeparatorViewColor(tvCountryCode, viewCountryCode);

        InputFilter[] filterArray = new InputFilter[1];
        try{
            Double.parseDouble(etEmailAndPhone.getText().toString().trim());
            rlCountryCode.setVisibility(View.VISIBLE);
            filterArray[0] = new InputFilter.LengthFilter(15);
            isPhone = true;
        }catch (Exception e) {
            rlCountryCode.setVisibility(View.GONE);
            filterArray[0] = new InputFilter.LengthFilter(45);
            isPhone = false;
        }
        etEmailAndPhone.setFilters(filterArray);

    }

    /**
     * method to create a spannable string
     */
    private void setSpannableString() {
        SpannableString spannableString = new SpannableString(getString(R.string.don_t_have_an_account_sign_up));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (!isLoading) {
                    etEmailAndPhone.setText("");
                    etPassword.setText("");
                    etEmailAndPhone.requestFocus();
                    if (getIntent() != null && getIntent().getExtras() != null) {
                        if (getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "")
                                .equals(Constants.AppConstant.SIGNUP)) {
                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                            intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.LOGIN);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                        }
                    }
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        int start, end;
        switch (AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE)) {
            case Constants.AppConstant.CHINES_TRAD:
            case Constants.AppConstant.CHINES_SIMPLE:
                start = 7;
                end = spannableString.length() ;
                break;
            default:
                start = 23;
                end = spannableString.length();
        }
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorSignupLogin)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomTypefaceSpan("", Typeface.createFromAsset(getAssets(), getString(R.string.orkney_bold))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignUp.setText(spannableString);
        tvSignUp.setMovementMethod(LinkMovementMethod.getInstance());
        tvSignUp.setHighlightColor(Color.TRANSPARENT);
    }


    @OnClick({R.id.btn_action, R.id.iv_facebook, R.id.iv_google, R.id.btn_guest, R.id.tv_forgot_password, R.id.iv_circle_bg, R.id.rl_switch, R.id.rl_country_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_circle_bg:
            case R.id.rl_switch:
                switchSelection();
                break;
            case R.id.btn_action:
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    if (!isLoading && isValidate()) {
                        isLoading = true;
                        loginType = NORMAL_LOGIN;
                        hitLoginApi();
                    }
                } else {
                    AppUtils.getInstance().showToast(this, getString(R.string.no_internet_connection));
                }
                break;
            case R.id.iv_facebook:
                if (!isLoading) {
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        AppUtils.getInstance().bounceAnimation(this, ivFacebook);
                        isLoading = true;
                        loginType = FACEBOOK_LOGIN;
                        doLogin(ivFacebook);
                    } else {
                        AppUtils.getInstance().showToast(this, getString(R.string.no_internet_connection));
                    }
                }
                break;
            case R.id.iv_google:
                if (!isLoading) {
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        AppUtils.getInstance().bounceAnimation(this, ivGoogle);
                        isLoading = true;
                        loginType = GOOGLE_LOGIN;
                        doLogin(ivGoogle);
                    } else {
                        AppUtils.getInstance().showToast(this, getString(R.string.no_internet_connection));
                    }
                }
                break;
            case R.id.btn_guest:
                if (!isLoading) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    AppUtils.getInstance().openNewActivity(this, intent);
//                    finish();
                }
                break;
            case R.id.tv_forgot_password:
                if (!isLoading) {
                    startActivity(new Intent(this, ForgotPasswordActivity.class));
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                break;
            case R.id.rl_country_code:
                AppUtils.getInstance().hideKeyboard(this);
                CustomDialogForSelectCountry dialogForSelectCountry = new CustomDialogForSelectCountry(this, "", tvCountryCode.getText().toString(), new SelectCountryDialogCallback() {
                    @Override
                    public void onOkClick(String country) {
                        for (CountryBean countryBean : AppUtils.getInstance().getAllCountries(LoginActivity.this)) {
                            if ((countryBean.getCountryEnglishName() + " (" + countryBean.getCountryCode() + ")").equalsIgnoreCase(country)) {
                                tvCountryCode.setText(countryBean.getCountryCode());
                            }
                        }
                    }
                });
                dialogForSelectCountry.show();
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
                    AppUtils.getInstance().changeImageWithAnimation(ivHuman, R.drawable.ic_login_bot, R.drawable.ic_login_human, true);
                    AppUtils.getInstance().changeTextWithAnimation(tvHuman, getString(R.string.i_am_not_a_robot), getString(R.string.i_am_a_human), true);
                    rlSwitch.setBackgroundResource(R.drawable.round_corner_green_bg);
//                    ivHuman.setImageResource(R.drawable.ic_login_human);
//                    tvHuman.setText(getString(R.string.i_am_a_human));
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
                    AppUtils.getInstance().changeImageWithAnimation(ivHuman, R.drawable.ic_login_bot, R.drawable.ic_login_human, false);
                    AppUtils.getInstance().changeTextWithAnimation(tvHuman, getString(R.string.i_am_not_a_robot), getString(R.string.i_am_a_human), false);
                    rlSwitch.setBackgroundResource(R.drawable.round_corner_white_stroke_transparent_bg);
//                    ivHuman.setImageResource(R.drawable.ic_login_bot);
//                    tvHuman.setText(getString(R.string.i_am_not_a_robot));
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

    /**
     * Method to check the validation on fields
     *
     * @return true if every entry is right, else false
     */
    private boolean isValidate() {
        if (etEmailAndPhone.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_email));
            return false;
        } else if (etEmailAndPhone.getText().toString().contains("@") && !Patterns.EMAIL_ADDRESS.matcher(etEmailAndPhone.getText().toString()).matches()) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_email));
            return false;
        } else if (isPhone && tvCountryCode.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_country_code));
            return false;
        } else  if (isPhone && etEmailAndPhone.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_phone_no));
            return false;
        } else if (isPhone && etEmailAndPhone.getText().toString().trim().length() < 7) {
            AppUtils.getInstance().showToast(this, getString(R.string.phone_no_must_be_7_15_digits));
            return false;
        } else if (etPassword.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_password));
            return false;
        } else if (etPassword.getText().toString().trim().length() < 6) {
            AppUtils.getInstance().showToast(this, getString(R.string.password_must_be_6_15_chars));
            return false;
        } else if (toggleAnimationStatus == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_check_not_a_robot));
            return false;
        }

        return true;
    }


    /*
     *  Initialize FB instance
     */
    private void initializeFB() {
        mFBSignInAI = new FBSignInAI();
        mFBSignInAI.setActivity(this);
        mFBSignInAI.setCallback(this);
    }

    /*
     *  method to do social sign in
     */
    public void doLogin(View view) {
        if (mFBSignInAI != null && loginType == FACEBOOK_LOGIN) {
            mFBSignInAI.doSignIn();
        }
        if (mGoogleSignInAI != null && loginType == GOOGLE_LOGIN) {
            mGoogleSignInAI.doSignIn();
        }
    }

    /*
     *  method to do social sign out
     */
    public void doLogout(View view) {
        if (mFBSignInAI != null && loginType == FACEBOOK_LOGIN) {
            mFBSignInAI.doSignOut();
        }
        if (mGoogleSignInAI != null && loginType == GOOGLE_LOGIN) {
            mGoogleSignInAI.doSignout();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (FB_LOGIN_REQUEST_CODE == requestCode) {
            mFBSignInAI.setActivityResult(requestCode, resultCode, data);
        } else if (GOOGLE_LOGIN_REQUEST_CODE == requestCode) {
            mGoogleSignInAI.onActivityResult(data);
        }
    }

    @Override
    public void fbSignInSuccessResult(JSONObject jsonObject) {
        String name, email = null, gender = null, social_id, userProfilePicUrl = null;

        userSocialModel = new UserSocialModel();
        try {
            name = jsonObject.getString("name");
            userSocialModel.setFirstName(name.split(" ")[0]);
            userSocialModel.setLastName(name.split(" ").length > 1 ? name.split(" ")[1] : "");
            if (jsonObject.has("email")) {
                email = jsonObject.getString("email");
                userSocialModel.setEmail(email);
            } else {
                userSocialModel.setEmail("");
            }
            social_id = jsonObject.getString("id");
            userSocialModel.setSocialId(social_id);

            if (jsonObject.has("picture")) {
                userProfilePicUrl = "https://graph.facebook.com/" + jsonObject.getString("id") + "/picture?width=2000";
                userSocialModel.setUserPic(userProfilePicUrl);
            } else {
                userSocialModel.setUserPic("");
            }
            doLogout(ivFacebook);
            userSocialModel.setSocialType("1");
            hitSocialLoginApi(userSocialModel, Constants.NetworkConstant.FACEBOOK_LOGIN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fbSignOutSuccessResult() {
    }

    @Override
    public void fbSignInFailure(Exception exception) {
        isLoading = false;
        AppUtils.getInstance().showToast(this, exception.getMessage());
    }

    @Override
    public void fbSignInCancel() {
        isLoading = false;
        AppUtils.getInstance().showToast(this, getString(R.string.login_cancel));
        try {
            mFBSignInAI.doSignOut();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fbFriends(JSONArray data) {
    }


    /*
     *  Initialize Google instance
     */
    private void initializeGoogle() {
        mGoogleSignInAI = new GoogleSignInAI();
        mGoogleSignInAI.setActivity(this);
        mGoogleSignInAI.setCallback(this);
        mGoogleSignInAI.setRequestCode(GOOGLE_LOGIN_REQUEST_CODE);
        mGoogleSignInAI.setUpGoogleClientForGoogleLogin();
    }

    @Override
    public void googleSignInSuccessResult(GoogleSignInAccount googleSignInAccount) {
        System.out.print(googleSignInAccount);
        userSocialModel = new UserSocialModel();
        userSocialModel.setSocialId(googleSignInAccount.getId());
        userSocialModel.setFirstName(googleSignInAccount.getGivenName());
        userSocialModel.setLastName(googleSignInAccount.getFamilyName());
        userSocialModel.setEmail(googleSignInAccount.getEmail());
        userSocialModel.setUserPic(googleSignInAccount.getPhotoUrl() != null ? googleSignInAccount.getPhotoUrl().toString() : "");
        doLogout(ivGoogle);
        userSocialModel.setSocialType("2");
        hitSocialLoginApi(userSocialModel, Constants.NetworkConstant.GOOGLE_LOGIN);

    }

    @Override
    public void googleSignInFailureResult(String message) {
        isLoading = false;
        AppUtils.getInstance().showToast(this, getString(R.string.google_login_cancel));
    }

    @Override
    public void googleSignOutSuccessResult(String message) {
    }

    @Override
    public void googleSignOutFailureResult(String message) {
        isLoading = false;
        AppUtils.getInstance().showToast(this, getString(R.string.network_issue));
    }


    /**
     * method to hit login api
     */
    private void hitLoginApi() {
        String deviceToken = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.DEVICE_TOKEN);
        if (deviceToken == null || deviceToken.equals("")) {
            AppUtils.getInstance().getDeviceToken(this);
            deviceToken = FirebaseInstanceId.getInstance().getToken();
        }

        AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        String language = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE_CODE);

        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_EMAIL, etEmailAndPhone.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_PASSWORD, etPassword.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_DEVICE_ID, AppUtils.getInstance().getDeviceId(this));
        params.put(Constants.NetworkConstant.PARAM_DEVICE_TOKEN, deviceToken);
        params.put(Constants.NetworkConstant.PARAM_PLATFORM, Constants.NetworkConstant.ANDROID_PLATFORM);
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, Constants.NetworkConstant.SHOPPER);
        params.put(Constants.NetworkConstant.PARAM_COUNTRY_ID, isPhone ? tvCountryCode.getText().toString().trim() : "");
        params.put(Constants.NetworkConstant.PARAM_USER_LANGUAGE, language.equals("") ? "1" : language);
        Call<ResponseBody> call = apiInterface.hitLoginApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_LOGIN);
    }


    /**
     * method to hit social login api
     *
     * @param userSocialModel
     * @param accountType
     */
    private void hitSocialLoginApi(UserSocialModel userSocialModel, String accountType) {
        String deviceToken = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.DEVICE_TOKEN);
        if (deviceToken == null || deviceToken.equals("")) {
            AppUtils.getInstance().getDeviceToken(this);
            deviceToken = FirebaseInstanceId.getInstance().getToken();
        }

        AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        String language = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE_CODE);

        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_SOCIAL_ID, userSocialModel.getSocialId());
        params.put(Constants.NetworkConstant.PARAM_EMAIL, userSocialModel.getEmail());
        params.put(Constants.NetworkConstant.PARAM_FIRST_NAME, userSocialModel.getFirstName());
        params.put(Constants.NetworkConstant.PARAM_LAST_NAME, userSocialModel.getLastName());
        params.put(Constants.NetworkConstant.PARAM_IMAGE, userSocialModel.getUserPic());
        params.put(Constants.NetworkConstant.PARAM_DEVICE_ID, AppUtils.getInstance().getDeviceId(this));
        params.put(Constants.NetworkConstant.PARAM_PLATFORM, Constants.NetworkConstant.ANDROID_PLATFORM);
        params.put(Constants.NetworkConstant.PARAM_DEVICE_TOKEN, deviceToken);
        params.put(Constants.NetworkConstant.PARAM_ACCOUNT_TYPE, accountType);
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, Constants.NetworkConstant.SHOPPER);
        params.put(Constants.NetworkConstant.PARAM_USER_LANGUAGE, language.equals("") ? "1" : language);
        Call<ResponseBody> call = apiInterface.hitSocialLoginApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_SOCIAL_LOGIN);
    }

    @Override
    public void onSuccess(int responseCode, final String response, int requestCode) {
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_LOGIN:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        final LoginResponse loginResponse = new Gson().fromJson(response, LoginResponse.class);
                        FirebaseDatabaseQueries.getInstance().signInFirebaseDatabase(loginResponse.getResult().getUserId(), loginResponse.getResult().getEmail(), Constants.AppConstant.FIREBASE_PASSWORD, new FirebaseAuthListener() {
                            @Override
                            public void onAuthSuccess(Task<AuthResult> task, FirebaseUser user) {
                                AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                                isLoading = false;
                                setUserSuccessData(loginResponse);
                            }

                            @Override
                            public void onAuthError(Task<AuthResult> task) {
                                AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                                isLoading = false;
                                AppUtils.getInstance().showToast(LoginActivity.this, getResources().getString(R.string.network_issue));
                            }
                        });
//                        AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewLoader, viewDot, false);
//                        isLoading = false;
//                        setUserSuccessData(loginResponse);
                        break;
                    case Constants.NetworkConstant.USER_NOT_VERIFIED:
                        AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                        isLoading = false;
                        setUserNotVerifiedAction(response);
                        break;
                    default:
                        AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                        isLoading = false;
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
                break;
            case Constants.NetworkConstant.REQUEST_SOCIAL_LOGIN:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                    case Constants.NetworkConstant.USER_NOT_VERIFIED:
                        final LoginResponse loginResponse = new Gson().fromJson(response, LoginResponse.class);
                        FirebaseDatabaseQueries.getInstance().signInFirebaseDatabase(loginResponse.getResult().getUserId(), loginResponse.getResult().getEmail(), Constants.AppConstant.FIREBASE_PASSWORD, new FirebaseAuthListener() {
                            @Override
                            public void onAuthSuccess(Task<AuthResult> task, FirebaseUser user) {
                                AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                                isLoading = false;
                                setUserSuccessData(loginResponse);
                            }

                            @Override
                            public void onAuthError(Task<AuthResult> task) {
                                AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                                isLoading = false;
                                AppUtils.getInstance().showToast(LoginActivity.this, getResources().getString(R.string.network_issue));
                            }
                        });
//                        AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewLoader, viewDot, false);
//                        isLoading = false;
//                        setUserSuccessData(loginResponse);
                        break;
                    case Constants.NetworkConstant.USER_NOT_REGISTER:
                        AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                        isLoading = false;
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(this, SignupActivity.class);
                        intent.putExtra(Constants.IntentConstant.USER_SOCIAL_MODEL, userSocialModel);
                        startActivity(intent);
                        break;
                    default:
                        AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
                        isLoading = false;
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        Intent intent;
        intent = new Intent(this, OtherInformationActivity.class);
        if (loginResponse.getResult().getSignupStatus() != null) {
            switch (loginResponse.getResult().getSignupStatus()) {
                case "4":
                    intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    break;
                case "3":
                    intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    break;
                case "2":
//                    intent = new Intent(this, CardDetailsActivity.class);
                    intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    break;
                default:
                    intent = new Intent(this, OtherInformationActivity.class);
                    break;
            }
        }
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        startActivity(intent);
        finish();

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

    /**
     * method to do action when user not verify
     *
     * @param response
     */
    private void setUserNotVerifiedAction(String response) {
        try {
            Intent intent = new Intent(this, EnterOtpActivity.class);
            JSONObject object = new JSONObject(response);
            JSONObject data = object.optJSONObject(Constants.NetworkConstant.RESULT);
            intent.putExtra(Constants.IntentConstant.USER_ID, data.optString(Constants.NetworkConstant.USER_ID));
            intent.putExtra(Constants.IntentConstant.COUNTRY_CODE, data.optString(Constants.NetworkConstant.PARAM_COUNTRY_ID));
            intent.putExtra(Constants.IntentConstant.PHONE_NUMBER, data.optString(Constants.NetworkConstant.PHONE_NUMBER));
            intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.LOGIN);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        isLoading = false;
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        AppUtils.getInstance().setButtonLoaderAnimation(LoginActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        isLoading = false;

    }
}
