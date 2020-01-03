package com.shopoholic.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.ViewPagerAdapter;
import com.shopoholic.dialogs.CustomDialogForMessage;
import com.shopoholic.interfaces.MessageDialogCallback;
import com.shopoholic.models.launcherhomeresponse.BannerArr;
import com.shopoholic.models.launcherhomeresponse.LauncherHomeResponse;
import com.shopoholic.models.productdealsresponse.Result;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class LauncherHomeFragment extends Fragment implements NetworkListener {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    Unbinder unbinder;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    private ViewPagerAdapter adapter;
    private LauncherHomeDealsFragment launcherHomeCategoryDealsFragment;
    private LauncherHomeDealsFragment launcherHomePercentageDealsFragment;
    private AppCompatActivity mActivity;
    private List<BannerArr> categoryBannerList;
    private List<BannerArr> percentageBannerList;
    private List<Result> categoryProductList;
    private List<Result> percentageProductList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_launcher_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        setUpViewPager();
        progressBar.setVisibility(View.VISIBLE);
        if (AppUtils.getInstance().internetAvailable(mActivity)) {
            hitLauncherHomeApi();
        } else {
            progressBar.setVisibility(View.GONE);
            showRetryDialog();
        }
        return rootView;
    }

    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        mActivity = (AppCompatActivity) getActivity();
        categoryBannerList = new ArrayList<>();
        percentageBannerList = new ArrayList<>();
        categoryProductList = new ArrayList<>();
        percentageProductList = new ArrayList<>();
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        launcherHomeCategoryDealsFragment = new LauncherHomeDealsFragment();
        launcherHomePercentageDealsFragment = new LauncherHomeDealsFragment();
    }


    /**
     * Method to set viewPager
     */
    private void setUpViewPager() {
        adapter.addFragment(launcherHomeCategoryDealsFragment, getString(R.string.category_based_deals));
        adapter.addFragment(launcherHomePercentageDealsFragment, getString(R.string.percentage_based_deals));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);//ths is done to make sure that fragment does not load again
        tabLayout.setupWithViewPager(viewPager);
        AppUtils.getInstance().setCustomFont(mActivity, tabLayout);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * Method to hit the signup api
     */
    private void hitLauncherHomeApi() {

        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        params.put(Constants.NetworkConstant.COUNTRY_CODE, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.COUNTRY_CODE));
//        params.put(Constants.NetworkConstant.COUNTRY_CODE, "852");
        Call<ResponseBody> call = apiInterface.hitBannerApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_LAUNCHER_HOME);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
            AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_LAUNCHER_HOME:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            LauncherHomeResponse homeResponse = new Gson().fromJson(response, LauncherHomeResponse.class);
                            for (BannerArr banner : homeResponse.getResult().getBannerArr()) {
                                if (banner.getType().equals(Constants.NetworkConstant.CATEGORY)) {
                                    categoryBannerList.add(banner);
                                } else {
                                    percentageBannerList.add(banner);
                                }
                            }
                            for (Result product : homeResponse.getResult().getPopularDeals()) {
//                                if (product.getType().equals(Constants.NetworkConstant.CATEGORY)) {
                                categoryProductList.add(product);
//                                } else {
                                percentageProductList.add(product);
//                                }
                            }
                            sendDataInChildFragment();
                            break;
                        default:
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }
                    break;
            }
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
            showRetryDialog();
        }
    }

    /**
     * Method to show success dialog after signup complete
     */
    private void showRetryDialog() {
        CustomDialogForMessage messageDialog = new CustomDialogForMessage(mActivity, getString(R.string.error), getString(R.string.network_issue),
                getString(R.string.retry),false,  new MessageDialogCallback() {
            @Override
            public void onSubmitClick() {
                progressBar.setVisibility(View.VISIBLE);
                if (AppUtils.getInstance().internetAvailable(mActivity)) {
                    hitLauncherHomeApi();
                } else {
                    progressBar.setVisibility(View.GONE);
                    showRetryDialog();
                }
            }
        });
        messageDialog.show();
    }

    /**
     * method to send data to child fragment
     */
    public void sendDataInChildFragment() {
        launcherHomeCategoryDealsFragment.getPageData(categoryProductList, categoryBannerList, Constants.IntentConstant.CATEGORY);
        launcherHomePercentageDealsFragment.getPageData(percentageProductList, percentageBannerList, Constants.IntentConstant.PERCENTAGE);
    }

    /**
     * method to change the favourite icon of other fragment
     * @param type
     * @param id
     * @param isFavourite
     */
    public void changeFavouriteIcon(String type, String id, String isFavourite) {
        if (!type.equals(Constants.IntentConstant.CATEGORY)) {
            launcherHomeCategoryDealsFragment.changeFavouriteIcon(id, isFavourite);
        }else {
            launcherHomePercentageDealsFragment.changeFavouriteIcon(id, isFavourite);
        }
    }
}
