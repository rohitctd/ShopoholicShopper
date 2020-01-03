package com.shopoholic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shopoholic.R;
import com.shopoholic.activities.ChangeLanguageActivity;
import com.shopoholic.activities.ChangePasswordActivity;
import com.shopoholic.activities.CommonActivity;
import com.shopoholic.activities.ContactUsActivity;
import com.shopoholic.activities.TutorialActivity;
import com.shopoholic.activities.WebViewActivity;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForLogout;
import com.shopoholic.interfaces.MessageDialogCallback;
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
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 23/3/18.
 */


public class SettingsFragment extends Fragment {


    Unbinder unbinder;
    @BindView(R.id.tv_notifications)
    CustomTextView tvNotifications;
    @BindView(R.id.iv_circle_bg)
    ImageView ivCircleBg;
    @BindView(R.id.rl_switch)
    FrameLayout rlSwitch;
    @BindView(R.id.tv_change_password)
    CustomTextView tvChangePassword;
    @BindView(R.id.tv_manage_bank_details)
    CustomTextView tvManageBankDetails;
    @BindView(R.id.tv_change_language)
    CustomTextView tvChangeLanguage;
    @BindView(R.id.tv_invite_friends)
    CustomTextView tvInviteFriends;
    @BindView(R.id.tv_help)
    CustomTextView tvHelp;
    @BindView(R.id.tv_feedback_to_admin)
    CustomTextView tvFeedbackToAdmin;
    @BindView(R.id.tv_user_guide)
    CustomTextView tvUserGuide;
    @BindView(R.id.tv_faq)
    CustomTextView tvFaq;
    @BindView(R.id.tv_legal)
    CustomTextView tvLegal;
    @BindView(R.id.tv_terms_and_conditions)
    CustomTextView tvTermsAndConditions;
    @BindView(R.id.tv_privacy_policy)
    CustomTextView tvPrivacyPolicy;
    @BindView(R.id.tv_copyright)
    CustomTextView tvCopyright;
    @BindView(R.id.tv_local_information)
    CustomTextView tvLocalInformation;
    @BindView(R.id.iv_local_circle_bg)
    ImageView ivLocalCircleBg;
    @BindView(R.id.rl_local_switch)
    FrameLayout rlLocalSwitch;
    @BindView(R.id.tv_logout)
    CustomTextView tvLogout;
    @BindView(R.id.ll_notification)
    LinearLayout llNotification;
    @BindView(R.id.view_notification)
    View viewNotification;
    @BindView(R.id.view_change_password)
    View viewChangePassword;
    @BindView(R.id.view_manage_bank_details)
    View viewManageBankDetails;
    @BindView(R.id.view_change_language)
    View viewChangeLanguage;
    @BindView(R.id.view_feedback_to_admin)
    View viewFeedbackToAdmin;
    @BindView(R.id.ll_local_information)
    LinearLayout llLocalInformation;
    @BindView(R.id.view_local_information)
    View viewLocalInformation;
    @BindView(R.id.tv_blocked_deals)
    CustomTextView tvBlockedDeals;
    @BindView(R.id.view_blocked_deals)
    View viewBlockedDeals;


    private View rootView;
    private int notificationToggleStatus = 0, locationToggleStatus = 0;
    private CustomDialogForLogout dialogForLogout;
    private AppCompatActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setData();
        return rootView;

    }

    /**
     * method to set data
     */
    private void setData() {
        mActivity = (AppCompatActivity) getActivity();
        if (AppSharedPreference.getInstance().getBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_NOTIFICATION_ON)) {
            switchSelection(1, false);
        }
        if (AppSharedPreference.getInstance().getBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_LOCATION_ON)) {
            switchSelection(2, false);
        }
        if (!AppSharedPreference.getInstance().getBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_SIGN_UP)) {
            llNotification.setVisibility(View.GONE);
            viewNotification.setVisibility(View.GONE);
            tvChangePassword.setVisibility(View.GONE);
            viewChangePassword.setVisibility(View.GONE);
            tvManageBankDetails.setVisibility(View.GONE);
            viewManageBankDetails.setVisibility(View.GONE);
            tvChangeLanguage.setVisibility(View.GONE);
            viewChangeLanguage.setVisibility(View.GONE);
            tvFeedbackToAdmin.setVisibility(View.GONE);
            viewFeedbackToAdmin.setVisibility(View.GONE);
            llLocalInformation.setVisibility(View.GONE);
            viewLocalInformation.setVisibility(View.GONE);
            tvLogout.setVisibility(View.GONE);
            tvBlockedDeals.setVisibility(View.GONE);
            viewBlockedDeals.setVisibility(View.GONE);
        }


        tvManageBankDetails.setVisibility(View.GONE);
        viewManageBankDetails.setVisibility(View.GONE);
    }


    @OnClick({R.id.tv_notifications, R.id.iv_circle_bg, R.id.rl_switch, R.id.tv_change_password, R.id.tv_manage_bank_details, R.id.tv_change_language, R.id.tv_invite_friends,
            R.id.tv_help, R.id.tv_feedback_to_admin, R.id.tv_user_guide, R.id.tv_faq, R.id.tv_legal, R.id.tv_terms_and_conditions, R.id.tv_privacy_policy, R.id.tv_copyright,
            R.id.tv_local_information, R.id.tv_logout, R.id.iv_local_circle_bg, R.id.rl_local_switch, R.id.tv_blocked_deals})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_circle_bg:
            case R.id.rl_switch:
                switchSelection(1, true);
                break;

            case R.id.iv_local_circle_bg:
            case R.id.rl_local_switch:
                switchSelection(2, true);
                break;

            case R.id.tv_change_password:
                startActivity(new Intent(mActivity, ChangePasswordActivity.class)
                        .putExtra(Constants.TERM_AND_CONDITION, ""));
                break;

            case R.id.tv_manage_bank_details:
//                startActivity(new Intent(mActivity, BankAccountDetailsActivity.class)
//                        .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.SETTING));
                break;

            case R.id.tv_change_language:
                startActivity(new Intent(mActivity, ChangeLanguageActivity.class));
                break;

            case R.id.tv_invite_friends:
                openSharingApplications();
                break;

            case R.id.tv_feedback_to_admin:
                startActivity(new Intent(mActivity, ContactUsActivity.class));
                break;

            case R.id.tv_user_guide:
                startActivity(new Intent(mActivity, TutorialActivity.class)
                        .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.NOTIFICATION));
                break;

            case R.id.tv_faq:
                startActivity(new Intent(mActivity, WebViewActivity.class)
                        .putExtra(Constants.FAQ, ""));
                break;

            case R.id.tv_terms_and_conditions:
                startActivity(new Intent(mActivity, WebViewActivity.class)
                        .putExtra(Constants.TERM_AND_CONDITION, ""));
                break;

            case R.id.tv_privacy_policy:
                startActivity(new Intent(mActivity, WebViewActivity.class)
                        .putExtra(Constants.PRIVACY_POLICY, ""));
                break;
            case R.id.tv_copyright:
                startActivity(new Intent(mActivity, WebViewActivity.class)
                        .putExtra(Constants.COPY_RIGHT, ""));

                break;

            case R.id.tv_local_information:
                break;

            case R.id.tv_blocked_deals:
                startActivity(new Intent(mActivity, CommonActivity.class).putExtra(Constants.IntentConstant.FRAGMENT_TYPE, 1));
                break;

            case R.id.tv_logout:
                showLogoutDialog();
                break;
        }
    }

    /**
     * function to show the logout dialog
     */
    private void showLogoutDialog() {
        dialogForLogout = new CustomDialogForLogout(mActivity, new MessageDialogCallback() {
            @Override
            public void onSubmitClick() {
                if (mActivity != null) {
                    hitLogoutApi();
                }
            }
        });
        dialogForLogout.show();
    }

    /**
     * method to open share kit
     */
    private void openSharingApplications() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message));
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_app)));
    }

    /**
     * set animation on notification
     */
    private void switchSelection(final int type, boolean hitApi) {
        Animation leftToRight = AnimationUtils.loadAnimation(mActivity, R.anim.left_to_right);
        leftToRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (type == 1) {
                    AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_NOTIFICATION_ON, true);
                    rlSwitch.setBackgroundResource(R.drawable.round_corner_green_bg);
                } else {
                    AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_LOCATION_ON, true);
                    rlLocalSwitch.setBackgroundResource(R.drawable.round_corner_green_bg);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

        });
        Animation rightToLeft = AnimationUtils.loadAnimation(mActivity, R.anim.right_to_left);
        rightToLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (type == 1) {
                    AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_NOTIFICATION_ON, false);
                    rlSwitch.setBackgroundResource(R.drawable.round_corner_white_stroke_transparent_bg);
                } else {
                    AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_LOCATION_ON, false);
                    rlLocalSwitch.setBackgroundResource(R.drawable.round_corner_white_stroke_transparent_bg);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        if (type == 1) {
            if (notificationToggleStatus == 0) {
                ivCircleBg.startAnimation(leftToRight);
                leftToRight.setFillAfter(true);
                notificationToggleStatus = 1;
            } else {
                ivCircleBg.startAnimation(rightToLeft);
                rightToLeft.setFillAfter(true);
                notificationToggleStatus = 0;
            }
        } else if (type == 2) {
            if (locationToggleStatus == 0) {
                ivLocalCircleBg.startAnimation(leftToRight);
                leftToRight.setFillAfter(true);
                locationToggleStatus = 1;
            } else {
                ivLocalCircleBg.startAnimation(rightToLeft);
                rightToLeft.setFillAfter(true);
                locationToggleStatus = 0;
            }
        }
        if (hitApi) hitChangeStatusApi(type);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();

    }


    /**
     * Method to hit the notification and  api
     *
     * @param type
     */
    private void hitChangeStatusApi(final int type) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = AppUtils.getInstance().getUserMap(mActivity);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        if (type == 1) {
            params.put(Constants.NetworkConstant.PARAM_NOTIFICATION_STATUS, String.valueOf(notificationToggleStatus == 0 ? 2 : 1));
        } else {
            params.put(Constants.NetworkConstant.PARAM_LOCATION_STATUS, String.valueOf(locationToggleStatus == 0 ? 2 : 1));
        }
        Call<ResponseBody> call = apiInterface.hitEditProfileDataApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {

            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                if (isAdded()) {
                    AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            AppSharedPreference.getInstance().putBoolean(mActivity,
                                    type == 1 ? AppSharedPreference.PREF_KEY.IS_NOTIFICATION_ON : AppSharedPreference.PREF_KEY.IS_LOCATION_ON,
                                    type == 1 ? notificationToggleStatus == 1 : locationToggleStatus == 1);
                            break;
                        default:
                            switchSelection(type, false);
                    }
                }
            }

            @Override
            public void onError(String response, int requestCode) {
                if (isAdded()) {
                    AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                    switchSelection(type, false);
                }
            }

            @Override
            public void onFailure() {
                if (isAdded()) {
                    switchSelection(type, false);
                }
            }
        }, 1);
    }


    /**
     * Method to hit the logout api
     *
     */
    private void hitLogoutApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_ACCESS_TOKEN, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.ACCESS_TOKEN));
        Call<ResponseBody> call = apiInterface.hitLogoutApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {

            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                if (isAdded()) {
                    AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            if (dialogForLogout!= null){
                                dialogForLogout.stopLoader(true);
                            }
                            AppUtils.getInstance().logoutUser(mActivity);
                            if (!mActivity.isFinishing() || !mActivity.isDestroyed()) mActivity.finish();
                            break;
                        default:
                            if (dialogForLogout!= null){
                                dialogForLogout.stopLoader(false);
                            }
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            }

            @Override
            public void onError(String response, int requestCode) {
                if (isAdded()) {
                    if (dialogForLogout!= null){
                        dialogForLogout.stopLoader(false);
                    }
                    AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                }
            }

            @Override
            public void onFailure() {
                if (dialogForLogout!= null){
                    dialogForLogout.stopLoader(false);
                }
            }
        }, 1);
    }



}
