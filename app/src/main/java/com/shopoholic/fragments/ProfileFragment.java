package com.shopoholic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.activities.ChangePhoneNumberActivity;
import com.shopoholic.activities.EditProfileActivity;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.models.profileresponse.ProfileResponse;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class ProfileFragment extends Fragment implements NetworkListener {

    Unbinder unbinder;
    @BindView(R.id.tv_change)
    CustomTextView tvChange;
    @BindView(R.id.civ_profile_pic)
    CircleImageView civProfilePic;
    @BindView(R.id.tv_user_name)
    CustomTextView tvUserName;
    @BindView(R.id.tv_address)
    CustomTextView tvAddress;
    @BindView(R.id.tv_phone_no)
    CustomTextView tvPhoneNo;
    @BindView(R.id.tv_email)
    CustomTextView tvEmail;
    @BindView(R.id.iv_verify_email)
    ImageView ivVerifyEmail;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    private View rootView;
    private AppCompatActivity mActivity;
    private int count = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mActivity = (AppCompatActivity) getActivity();
        setDataOnViews();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) hitProfileDataApi();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setDataOnViews();
    }

    /**
     * method to set data on views from share preference
     */
    private void setDataOnViews() {
        btnAction.setText(getString(R.string.edit));
        tvUserName.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.FIRST_NAME) + " "
                + AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.LAST_NAME)));
        AppUtils.getInstance().setCircularImages(mActivity, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_IMAGE),
                civProfilePic, R.drawable.ic_side_menu_user_placeholder);
        tvAddress.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.ADDRESS) + " "
                + AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.ADDRESS2)));
        tvPhoneNo.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.COUNTRY_CODE) + " "
                + AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.PHONE_NO)));
        tvEmail.setText(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.EMAIL));
        if (tvAddress.getText().toString().trim().equals(""))
            tvAddress.setText(getString(R.string.na));
        if (tvPhoneNo.getText().toString().trim().equals(""))
            tvPhoneNo.setText(getString(R.string.na));
        if (tvEmail.getText().toString().trim().equals("")) {
            tvEmail.setText(getString(R.string.na));
            ivVerifyEmail.setVisibility(View.GONE);
        } else if (!AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")) {
            ivVerifyEmail.setVisibility(View.VISIBLE);
        } else {
            ivVerifyEmail.setVisibility(View.GONE);
        }
        if (mActivity instanceof HomeActivity) {
            ((HomeActivity) mActivity).setToolbar();
        }
    }

    /**
     * Method to hit the profile data api
     */
    public void hitProfileDataApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitProfileDataApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_PROFILE);
    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_PROFILE:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            ProfileResponse profileResponse = new Gson().fromJson(response, ProfileResponse.class);
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.FIRST_NAME, profileResponse.getResult().getFirstName());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.LAST_NAME, profileResponse.getResult().getLastName());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.EMAIL, profileResponse.getResult().getEmail());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.USER_IMAGE, profileResponse.getResult().getImage());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.COUNTRY_CODE, profileResponse.getResult().getCountryId());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.PHONE_NO, profileResponse.getResult().getMobileNo());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.ADDRESS, profileResponse.getResult().getAddress());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.ADDRESS2, profileResponse.getResult().getAddress2());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.DOB, profileResponse.getResult().getDateOfBirth());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.LATITUDE, profileResponse.getResult().getLatitude());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.LONGITUDE, profileResponse.getResult().getLongitude());
                            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE, profileResponse.getResult().getIsEmailVerified());
                            setDataOnViews();
                            if (!profileResponse.getResult().getActiveOrder().equals("")) {
                                count = Integer.parseInt(profileResponse.getResult().getActiveOrder());
                            }
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onError(String response, int requestCode) {
    }

    @Override
    public void onFailure() {
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_change, R.id.btn_action, R.id.iv_verify_email})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_change:
                startActivity(new Intent(mActivity, ChangePhoneNumberActivity.class));
                break;
            case R.id.btn_action:
                startActivity(new Intent(mActivity, EditProfileActivity.class)
                        .putExtra(Constants.IntentConstant.COUNT, count));
                break;
            case R.id.iv_verify_email:
                new AlertDialog.Builder(mActivity, R.style.DatePickerTheme)
                        .setCancelable(false)
                        .setMessage(getString(R.string.email_not_verified))
                        .setPositiveButton(getString(R.string.resend_link), (dialog, which) -> {
                            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                                hitResendLinkApi();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                            // do nothing
                        })
                        .show();
                break;
        }
    }

    /**
     * method to hit resend link api
     */
    private void hitResendLinkApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitResendLinkApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, null, 1001);
    }
}
