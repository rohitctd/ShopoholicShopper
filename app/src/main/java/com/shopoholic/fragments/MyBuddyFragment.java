package com.shopoholic.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dnitinverma.locationlibrary.RCLocation;
import com.dnitinverma.locationlibrary.interfaces.LocationsCallback;
import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.activities.BuddyProfileActivity;
import com.shopoholic.activities.CommonActivity;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.adapters.BuddyAdapter;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.buddylistresponse.BuddyListResponse;
import com.shopoholic.models.buddylistresponse.Result;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class MyBuddyFragment extends Fragment implements NetworkListener, LocationsCallback {
    @BindView(R.id.et_search)
    CustomEditText etSearch;
    @BindView(R.id.iv_cross)
    ImageView ivCross;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
    @BindView(R.id.recycle_view)
    RecyclerView rvBuddies;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private AppCompatActivity mActivity;
    Unbinder unbinder;
    private View rootView;
    private BuddyAdapter buddyAdapter;
    private List<Result> buddyList;
    private boolean isMoreData;
    private boolean isLoading;
    private LinearLayoutManager linearLayoutManager;
    private int count = 0;
    private boolean isPagination;
    private String rateSort = "", name = "";
    private String orderId = "";
    private String dealId = "";
    private String huntId = "";
    private boolean isHunt;
    private RCLocation location;
    private String countryCode;
    private String price = "";
    private String date = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_buddy, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mActivity = (AppCompatActivity) getActivity();
        initVariable();
        setAdapters();
        setListeners();
        initializeLocation();
        return rootView;
    }


    /**
     * method to initialize the location
     */
    private void initializeLocation() {
        location = new RCLocation();
        location.setActivity(mActivity);
        location.setCallback(this);
        location.startLocation();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * method to initialize the variables
     */
    private void initVariable() {
        buddyList = new ArrayList<>();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        countryCode = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.COUNTRY_CODE);
        buddyAdapter = new BuddyAdapter(mActivity, buddyList, (position, view) -> {
            switch (view.getId()) {
                case R.id.rl_buddy_assigned:
                    Intent buddyProfileIntent = new Intent(mActivity, BuddyProfileActivity.class);
                    buddyProfileIntent.putExtra(Constants.IntentConstant.BUDDY, buddyList.get(position));
                    MyBuddyFragment.this.startActivityForResult(buddyProfileIntent, Constants.IntentConstant.REQUEST_BUDDY);
                    break;
                case R.id.tv_buddy_request:
                    if (mActivity instanceof HomeActivity || (mActivity instanceof CommonActivity && !buddyList.get(position).getRequestStatus().equals("1"))) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            buddyList.get(position).setRequestStatus("1");
                            buddyAdapter.notifyDataSetChanged();
                            hitRequestBuddyApi(buddyList.get(position).getUserId(), orderId, dealId);
                        }
                    }
                    break;
            }
        });
        if (mActivity instanceof CommonActivity) {
            Intent intent = ((CommonActivity) mActivity).getIntent();
            if (intent != null && intent.getExtras() != null) {
                orderId = intent.getExtras().getString(Constants.IntentConstant.ORDER_ID, "");
                dealId = intent.getExtras().getString(Constants.IntentConstant.DEAL_ID, "");
                huntId = intent.getExtras().getString(Constants.IntentConstant.HUNT_ID, "");
                isHunt = intent.getExtras().getBoolean(Constants.IntentConstant.IS_HUNT, false);
                price = intent.getExtras().getString(Constants.NetworkConstant.PARAM_PRICE, "");
                date = intent.getExtras().getString(Constants.NetworkConstant.PARAM_EXPECTED_DATE, "");
            }
        }
    }


    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                count = 0;
                hitGetBuddyListApi();
            }
        });
        rvBuddies.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= buddyList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            isPagination = true;
                            hitGetBuddyListApi();
                        }
                    }
                }
            }

        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                ivCross.setVisibility(etSearch.getText().toString().length() > 0 ? View.VISIBLE : View.GONE);
                if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    count = 0;
                    hitGetBuddyListApi();
                }
            }
        });

    }

    /**
     * method to set adapter in views
     */
    private void setAdapters() {
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rvBuddies.setLayoutManager(linearLayoutManager);
        rvBuddies.setAdapter(buddyAdapter);
    }

    /**
     * method to get buddy list
     */
    private void hitGetBuddyListApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.COUNTRY_CODE, countryCode);
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        params.put(Constants.NetworkConstant.PARAM_RATESORT, rateSort);
        params.put(Constants.NetworkConstant.PARAM_NAME, name);
        if (!isHunt) {
            params.put(Constants.NetworkConstant.PARAM_ORDER_ID, orderId);
            params.put(Constants.NetworkConstant.PARAM_DEAL_ID, dealId);
        }else {
            params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntId);
        }
        params.put(Constants.NetworkConstant.PARAM_SEARCH, etSearch.getText().toString().trim());
        Call<ResponseBody> call = apiInterface.hitBuddyListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_BUDDY);
    }

    /**
     * method to get buddy list
     *
     * @param userId
     * @param orderId
     */
    private void hitRequestBuddyApi(String userId, String orderId, String dealId) {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        if (!isHunt) {
            params.put(Constants.NetworkConstant.PARAM_ORDER_ID, orderId);
            params.put(Constants.NetworkConstant.PARAM_DEAL_ID, dealId);
        }else {
            params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntId);
        }
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, userId);
        params.put(Constants.NetworkConstant.PARAM_SHOPPER_DELIVERY_CHARGE, price);
        params.put(Constants.NetworkConstant.PARAM_SHOPPER_DELIVERY_DATE, AppUtils.getInstance().formatDate(date, DATE_FORMAT, SERVICE_DATE_FORMAT));
        Call<ResponseBody> call = apiInterface.hitRequestBuddyApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, 1);
    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_BUDDY:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            BuddyListResponse buddyListResponse = new Gson().fromJson(response, BuddyListResponse.class);
                            if (!isPagination) {
                                buddyList.clear();
                            } else {
                                isPagination = false;
                            }
                            buddyList.addAll(buddyListResponse.getResult());
                            buddyAdapter.notifyDataSetChanged();
                            isMoreData = buddyListResponse.getNext() != -1;
                            if (isMoreData) count = buddyListResponse.getNext();
                            if (buddyList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.NetworkConstant.NO_DATA:
                            buddyList.clear();
                            buddyAdapter.notifyDataSetChanged();
                            if (buddyList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            buddyAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (buddyList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            buddyAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (buddyList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * method to sort the buddy list
     *
     * @param type
     */
    public void sortBuddyList(int type) {
        switch (type) {
            case 1:
                rateSort = "desc";
                name = "";
                break;
            case 2:
                rateSort = "";
                name = "asc";
                break;
        }
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            count = 0;
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            hitGetBuddyListApi();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.IntentConstant.REQUEST_BUDDY && resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            Result buddyDetail = (Result) data.getExtras().getSerializable(Constants.IntentConstant.BUDDY);
            if (buddyDetail != null) {
                for (int i = 0; i < buddyList.size(); i++) {
                    if (buddyList.get(i).getUserId().equals(buddyDetail.getUserId())) {
                        buddyList.set(i, buddyDetail);
                        buddyAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    @OnClick(R.id.iv_cross)
    public void onViewClicked() {
        if (etSearch.getText().toString().trim().length() > 0) {
            etSearch.setText("");
        }
    }

    @Override
    public void setLocation(Location mCurrentLocation) {
        if (isAdded()) {
            double latitude = 0.0, longitude = 0.0;
            if (mCurrentLocation != null) {
                latitude = mCurrentLocation.getLatitude();
                longitude = mCurrentLocation.getLongitude();
            }
            location.getAddress();

        }
    }

    @Override
    public void setAddress(Address address) {
        if (isAdded()) {
            location.disconnect();
            String countryListJsonData = "";
            try {
                InputStream is = mActivity.getAssets().open("countryData.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                countryListJsonData = new String(buffer, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (address != null) {
                countryCode = AppUtils.getInstance().checkCountryCodeFromIsoCode(countryListJsonData, address.getCountryCode());
            }
            if (AppUtils.getInstance().isInternetAvailable(mActivity) && buddyList.size() == 0) {
                count = 0;
                rateSort = "desc";
                name = "";
                progressBar.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                hitGetBuddyListApi();
            }
        }
    }

    @Override
    public void setLatAndLong(Address location) {

    }

    @Override
    public void disconnect() {

    }
}
