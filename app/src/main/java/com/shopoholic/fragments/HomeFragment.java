package com.shopoholic.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.adapters.FilterBean;
import com.shopoholic.adapters.ViewPagerAdapter;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.firebasechat.utils.FirebaseEventListeners;
import com.shopoholic.models.bannerlistresponse.BannerArr;
import com.shopoholic.models.bannerlistresponse.BannerListResponse;
import com.shopoholic.models.preferredcategorymodel.PreferredCategoriesResponse;
import com.shopoholic.models.preferredcategorymodel.Result;
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
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc
 * on 23/3/18.
 */

public class HomeFragment extends Fragment implements NetworkListener {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ViewPagerAdapter adapter;
    public HomeProductDealsFragment homeProductDealsFragment;
    private HomeProductDealsFragment homeServiceDealsFragment;
    private HomePercentageDealsFragment homePercentageDealsFragment;
    private AppCompatActivity mActivity;
    private Unbinder unbinder;
    private FilterBean filterData;

    private BroadcastReceiver filterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mActivity instanceof HomeActivity) {
                filterData = ((HomeActivity) mActivity).getFilterData();
                if (filterData != null) {
                    String dealType = filterData.getTypeOfDeals() == 0 ? "" : String.valueOf(filterData.getTypeOfDeals());
                    if (viewPager != null && dealType.equals("")) viewPager.setCurrentItem(1);
                    if (viewPager != null && dealType.equals("1")) viewPager.setCurrentItem(0);
                    if (viewPager != null && dealType.equals("2")) viewPager.setCurrentItem(2);
                }
            }
        }
    };
    private BroadcastReceiver mapGridReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (viewPager.getCurrentItem()){
                case 0:
                    homeProductDealsFragment.changeMapGrid();
                    break;
                case 1:
                    homePercentageDealsFragment.changeMapGrid();
                    break;
                case 2:
                    homeServiceDealsFragment.changeMapGrid();
                    break;
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(filterReceiver, new IntentFilter(Constants.IntentConstant.FILTER_DATA));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mapGridReceiver, new IntentFilter(Constants.IntentConstant.MAP_GRID));
    }

    @Override
    public void onStop() {
        super.onStop();
//        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(filterReceiver);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mapGridReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        setUpViewPager();
        resetStatus();
        return rootView;
    }

    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        homeProductDealsFragment = new HomeProductDealsFragment();
        homeServiceDealsFragment = new HomeProductDealsFragment();
        homePercentageDealsFragment = new HomePercentageDealsFragment();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            hitCategoryListApi();
            hitBannerApi();
        }
        if (((HomeActivity) mActivity).getIntent() != null)
            ((HomeActivity) mActivity).setIntent(null);
    }


    /**
     * Method to set viewPager
     */
    private void setUpViewPager() {
        Bundle productBundle = new Bundle();
        productBundle.putString(Constants.IntentConstant.TYPE, Constants.IntentConstant.CATEGORY);
        Bundle serviceBundle = new Bundle();
        serviceBundle.putString(Constants.IntentConstant.TYPE, Constants.IntentConstant.SERVICE);
        homeProductDealsFragment.setArguments(productBundle);
        homeServiceDealsFragment.setArguments(serviceBundle);
        adapter.addFragment(homeProductDealsFragment, getString(R.string.product_based_deals));
        adapter.addFragment(homePercentageDealsFragment, getString(R.string.special_offers));
        adapter.addFragment(homeServiceDealsFragment, getString(R.string.service_based_deals));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        AppUtils.getInstance().setCustomFont(mActivity, tabLayout);
        if (getArguments() != null && getArguments().getString(Constants.IntentConstant.FROM_CLASS) != null){
            if (getArguments().getString(Constants.IntentConstant.FROM_CLASS).equals(Constants.IntentConstant.PERCENTAGE)) {
                viewPager.setCurrentItem(1);
            }
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mActivity instanceof HomeActivity){
                    String address = "";
                    double latitude = 0, longitude = 0;
                    filterData = ((HomeActivity)mActivity).getFilterData();
                    if (filterData != null) {
                        address = filterData.getAddress();
                        latitude = filterData.getLatitude();
                        longitude = filterData.getLongitude();
                    }
                    filterData = new FilterBean();
                    filterData.setTypeOfDeals(position == 0 ? 1 : position == 1 ? 0 : 2);
                    if (!address.equals("") && latitude != 0 && longitude != 0) {
                        filterData.setLatitude(latitude);
                        filterData.setLongitude(longitude);
                        filterData.setAddress(address);
                        ((HomeActivity)mActivity).setFilterBean(filterData);
                        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Constants.IntentConstant.FILTER_DATA));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * method to set user status
     * @param statusDrawable
     */
    public void setUserStatus(int statusDrawable, String status) {
        AppSharedPreference.getInstance().putInt(mActivity, AppSharedPreference.PREF_KEY.USER_ONLINE_STATUS, statusDrawable);
        FirebaseDatabaseQueries.getInstance().setUserStatus(mActivity, statusDrawable);

        if (homeProductDealsFragment != null){
            homeProductDealsFragment.setStatus(statusDrawable, status);
        }
        if (homePercentageDealsFragment != null){
            homePercentageDealsFragment.setStatus(statusDrawable, status);
        }
        if (homeServiceDealsFragment != null){
            homeServiceDealsFragment.setStatus(statusDrawable, status);
        }
    }


    /**
     * method to set user status
     */
    public void resetStatus() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE)
                .child(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID))
                .child(FirebaseConstants.STATUS_NODE).addListenerForSingleValueEvent(new FirebaseEventListeners(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    int status = 0;
                    if (dataSnapshot.getValue() != null) {
                        status = Integer.parseInt(dataSnapshot.getValue().toString());
                    }
                    AppSharedPreference.getInstance().putInt(mActivity, AppSharedPreference.PREF_KEY.USER_ONLINE_STATUS, status);
                    if (homeProductDealsFragment != null) {
                        homeProductDealsFragment.resetStatus();
                    }
                    if (homePercentageDealsFragment != null) {
                        homePercentageDealsFragment.resetStatus();
                    }
                    if (homeServiceDealsFragment != null) {
                        homeServiceDealsFragment.resetStatus();
                    }
                }
            }
        });
    }


    /**
     * method to change the favourite icon of other fragment
     * @param type
     * @param id
     * @param isFavourite
     */
    public void changeFavouriteIcon(String type, String id, String isFavourite) {
        if (type.equals(Constants.IntentConstant.CATEGORY)) {
            homePercentageDealsFragment.changeFavouriteIcon(id, isFavourite);
            homeServiceDealsFragment.changeFavouriteIcon(id, isFavourite);
        }else if (type.equals(Constants.IntentConstant.PERCENTAGE)) {
            homeProductDealsFragment.changeFavouriteIcon(id, isFavourite);
            homeServiceDealsFragment.changeFavouriteIcon(id, isFavourite);
        }else {
            homeProductDealsFragment.changeFavouriteIcon(id, isFavourite);
            homePercentageDealsFragment.changeFavouriteIcon(id, isFavourite);
        }
    }

    /**
     * Method to hit the banner api
     */
    private void hitBannerApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        params.put(Constants.NetworkConstant.COUNTRY_CODE, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.COUNTRY_CODE));
        params.put(Constants.NetworkConstant.PARAM_TYPE, "1");
        Call<ResponseBody> call = apiInterface.hitBannerApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_LAUNCHER_HOME);
    }

    /**
     * Method to hit the category list api
     */
    public void hitCategoryListApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        Call<ResponseBody> call = apiInterface.hitCategoriesListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_CATEGORIES:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            PreferredCategoriesResponse categoriesResponse = new Gson().fromJson(response, PreferredCategoriesResponse.class);
                            List<Result> productCategory = new ArrayList<>();
                            List<Result> serviceCategory = new ArrayList<>();
                            for (Result cat : categoriesResponse.getResult()){
                                if (cat.getCategoryType().equals("1")){
                                    productCategory.add(cat);
                                }else {
                                    serviceCategory.add(cat);
                                }
                            }
                            if (homeProductDealsFragment != null) homeProductDealsFragment.setCategories(productCategory);
                            if (homeServiceDealsFragment != null) homeServiceDealsFragment.setCategories(serviceCategory);
                            break;
                        default:
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                    break;
                case Constants.NetworkConstant.REQUEST_LAUNCHER_HOME:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            BannerListResponse bannerListResponse = new Gson().fromJson(response, BannerListResponse.class);
                            List<BannerArr> productBanners = new ArrayList<>();
                            List<BannerArr> percentageBanners = new ArrayList<>();
                            List<BannerArr> serviceBanners = new ArrayList<>();
                            for (BannerArr banner : bannerListResponse.getResult().getBannerArr()){
                                if (banner.getType().equals(Constants.NetworkConstant.CATEGORY)){
                                    if (banner.getProductType().equals("1")){
                                        productBanners.add(banner);
                                    }else {
                                        serviceBanners.add(banner);
                                    }
                                }else {
                                    percentageBanners.add(banner);
                                }
                            }

                            if (homeProductDealsFragment != null) homeProductDealsFragment.setBanners(productBanners);
                            if (homePercentageDealsFragment != null) homePercentageDealsFragment.setBanners(percentageBanners);
                            if (homeServiceDealsFragment != null) homeServiceDealsFragment.setBanners(serviceBanners);
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
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {

    }

    /**
     * function to set coachmarks
     * @param tvLocation
     * @param ivShowCategory
     */
    public void setCoachMarks(View tvLocation, View ivShowCategory) {
        if (mActivity instanceof HomeActivity) {
            ((HomeActivity)mActivity).setCoachMarks((ViewGroup) tabLayout.getChildAt(0), tvLocation, ivShowCategory);
        }
    }
}
