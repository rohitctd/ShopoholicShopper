package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
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
 * Created by appinventiv-pc on 21/3/18.
 */

public class ChangePasswordActivity extends BaseActivity implements View.OnTouchListener, NetworkListener {


    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.layout_toolbar_reset_password)
    LinearLayout layoutToolbarResetPassword;
    @BindView(R.id.et_old_password)
    CustomEditText etOldPassword;
    @BindView(R.id.view_old_password)
    View viewOldPassword;
    @BindView(R.id.et_new_password)
    CustomEditText etNewPassword;
    @BindView(R.id.et_confirm_password)
    CustomEditText etConfirmPassword;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    private boolean isOldPasswordShowing;
    private boolean isNewPasswordShowing;
    private boolean isConfirmPasswordShowing;
    private boolean isLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_or_change_password);
        ButterKnife.bind(this);
        setListenersAndToolbar();
    }

    /**
     * method to set listener and toolbar
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setListenersAndToolbar() {
        etOldPassword.performClick();
        etNewPassword.performClick();
        etConfirmPassword.performClick();
        etOldPassword.setOnTouchListener(this);
        etNewPassword.setOnTouchListener(this);
        etConfirmPassword.setOnTouchListener(this);

        layoutToolbarResetPassword.setVisibility(View.GONE);
        layoutToolbar.setVisibility(View.VISIBLE);
        etOldPassword.setVisibility(View.VISIBLE);
        viewOldPassword.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        btnAction.setText(getString(R.string.update));
        tvTitle.setText(getString(R.string.change_password));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        CustomEditText viewEditText;
        if (v.getId() == R.id.et_old_password) {
            viewEditText = etOldPassword;
        } else if (v.getId() == R.id.et_new_password) {
            viewEditText = etNewPassword;
        } else {
            viewEditText = etConfirmPassword;
        }
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        Drawable drawable;
        if (viewEditText.getCompoundDrawables()[DRAWABLE_RIGHT] != null) drawable = viewEditText.getCompoundDrawables()[DRAWABLE_RIGHT];
        else drawable = viewEditText.getCompoundDrawables()[DRAWABLE_LEFT];
        if (drawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (v.getRight() - drawable.getBounds().width())) {
                // your action here
                if (v.getId() == R.id.et_old_password ? !isOldPasswordShowing : v.getId() == R.id.et_new_password ? !isNewPasswordShowing : !isConfirmPasswordShowing) {
                    if (v.getId() == R.id.et_old_password) isOldPasswordShowing = true;
                    else if (v.getId() == R.id.et_new_password) isNewPasswordShowing = true;
                    else isConfirmPasswordShowing = true;
                    viewEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_login_password_visible, 0);
                    viewEditText.setTransformationMethod(null);
                    viewEditText.setSelection(viewEditText.getText().length());
                } else {
                    if (v.getId() == R.id.et_old_password) isOldPasswordShowing = false;
                    else if (v.getId() == R.id.et_password) isNewPasswordShowing = false;
                    else isConfirmPasswordShowing = false;
                    viewEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_login_password_not_visible, 0);
                    viewEditText.setTransformationMethod(new PasswordTransformationMethod());
                    viewEditText.setSelection(viewEditText.getText().length());
                }
                viewEditText.setSelection(viewEditText.getText().length());
                viewEditText.requestFocus();
                return true;
            }
        }
        return false;
    }


    @OnClick({R.id.iv_back, R.id.btn_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_action:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(this)) {
                    if (isValidate()) {
                        hitChangePasswordApi();
                    }
                }
                break;
        }
    }



    /**
     * Method to check the validation on fields
     *
     * @return true if every entry is right, else false
     */
    private boolean isValidate() {
        if (etOldPassword.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_password));
            return false;
        } else if (etOldPassword.getText().toString().length() < 6) {
            AppUtils.getInstance().showToast(this, getString(R.string.password_must_be_6_15_chars));
            return false;
        } else if (etNewPassword.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_password));
            return false;
        } else if (etNewPassword.getText().toString().length() < 6) {
            AppUtils.getInstance().showToast(this, getString(R.string.password_must_be_6_15_chars));
            return false;
        } else if (etConfirmPassword.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_confir_password));
            return false;
        } else if (!etNewPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            AppUtils.getInstance().showToast(this, getString(R.string.password_not_match));
            return false;
        }

        return true;
    }


    /**
     * Method to hit the reset password api
     */
    private void hitChangePasswordApi() {
        isLoading = true;
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, true);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_ACCESS_TOKEN, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.ACCESS_TOKEN));
        params.put(Constants.NetworkConstant.PARAM_OLD_PASSWORD, etOldPassword.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_PASSWORD, etNewPassword.getText().toString().trim());
        Call<ResponseBody> call = apiInterface.hitChangePasswordApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_CHANGE_PASSWORD);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_CHANGE_PASSWORD:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                    default:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);

    }
}
