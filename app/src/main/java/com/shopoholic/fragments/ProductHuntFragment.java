package com.shopoholic.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopoholic.R;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.adapters.ViewPagerAdapter;
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
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class ProductHuntFragment extends Fragment {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    Unbinder unbinder;

    private AppCompatActivity mActivity;
    private ViewPagerAdapter adapter;
    private ProductFragment productFragment;
    private ProductFragment serviceFragment;
    private boolean isProduct = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_hunt, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        setUpViewPager();
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.IS_PHONE_VALIDATE).equals("1")) {
            if (AppUtils.getInstance().isLoggedIn(mActivity)) {
                if (AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.PHONE_NO).equals("")) {
                    String phoneNo = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.PHONE_NO);
                    new AlertDialog.Builder(mActivity, R.style.DatePickerTheme)
                            .setMessage(getString(phoneNo.equals("") ? R.string.please_enter_phone_number : R.string.phone_no_required))
                            .setPositiveButton(getString(phoneNo.equals("") ? R.string.ok : R.string.resend_link), (dialog, which) -> {
                                if (mActivity instanceof HomeActivity) {
                                    ((HomeActivity)mActivity).updateFragment(-1);
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                if (mActivity instanceof HomeActivity) {
                                    ((HomeActivity)mActivity).updateFragment(0);
                                }
                            }).setCancelable(false)
                            .show();
                }
            }
        } else if (!AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")) {
            if (AppUtils.getInstance().isLoggedIn(mActivity)) {
                if (!AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.EMAIL).equals("")) {
                    hitEmailValidateApi();
                } else {
                    AppUtils.getInstance().showToast(mActivity, getString(R.string.please_provide_email_address));
                }
            }
        }
    }

    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        productFragment = new ProductFragment();
        serviceFragment = new ProductFragment();
        if (getArguments() != null && getArguments().containsKey(Constants.IntentConstant.IS_PRODUCT))
            isProduct = getArguments().getBoolean(Constants.IntentConstant.IS_PRODUCT, true);

    }

    /**
     * Method to set viewPager
     */
    private void setUpViewPager() {
        Bundle productBundle = new Bundle();
        productBundle.putBoolean(Constants.IntentConstant.IS_PRODUCT, true);
        Bundle serviceBundle = new Bundle();
        serviceBundle.putBoolean(Constants.IntentConstant.IS_PRODUCT, false);
        productFragment.setArguments(productBundle);
        serviceFragment.setArguments(serviceBundle);

        adapter.addFragment(productFragment, getString(R.string.product));
        adapter.addFragment(serviceFragment, getString(R.string.service));
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(2);//ths is done to make sure that fragment does not load again
        tabLayout.setupWithViewPager(viewPager);
//          AppUtils.getInstance().setCustomFont(mActivity, tabLayout);
        viewPager.setCurrentItem(isProduct ? 0 : 1);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



    /**
     * Method to hit the signup api
     *
     */
    public void hitEmailValidateApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitEmailValidateApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE, "1");
                        break;
                    case Constants.NetworkConstant.EMAIL_NOT_VERIFIED:
                        String email = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.EMAIL);
                        new AlertDialog.Builder(mActivity, R.style.DatePickerTheme)
                                .setMessage(getString(email.equals("") ? R.string.please_enter_email_address : R.string.email_not_verified))
                                .setPositiveButton(getString(email.equals("") ? R.string.ok : R.string.resend_link), (dialog, which) -> {
                                    if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                                        hitResendLinkApi();
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                    // do nothing
                                })
                                .show();
                        break;
                    default:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }

            @Override
            public void onError(String response, int requestCode) {
                AppUtils.getInstance().showToast(mActivity, response);
            }

            @Override
            public void onFailure() {
            }
        }, 1);
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
