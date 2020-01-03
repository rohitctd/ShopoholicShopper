package com.shopoholic.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.StoresAdapter;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.storelistresponse.Result;
import com.shopoholic.models.storelistresponse.StoreListResponse;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class to select preferred categories
 */

public class StoreListActivity extends BaseActivity implements NetworkListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.et_search)
    CustomEditText etSearch;
    @BindView(R.id.iv_cross)
    ImageView ivCross;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;

    private ArrayList<Result> storesList;
    private StoresAdapter storesAdapter;
    private boolean isLoading;
    private LinearLayoutManager layoutManager;
    private boolean isMoreData;
    private boolean isPagination;
    private int count = 0;
    public Location currentLocation;
    private ArrayList<Result> selectedStoresList;
    private String latitude;
    private String longitude;
    private String range;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_list);
        ButterKnife.bind(this);
        initVariables();
        setListener();
        setAdapters();
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            swipeRefreshLayout.setRefreshing(false);
            isPagination = false;
            hitStoresListApi();
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_cross, R.id.tv_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_cross:
                etSearch.setText("");
                break;
            case R.id.tv_clear:
//                if (selectedStoresList.size() != 0) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.IntentConstant.STORE, selectedStoresList);
                    setResult(RESULT_OK, intent);
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                    finish();
//                } else {
//                    AppUtils.getInstance().showToast(this, getString(R.string.select_one_store));
//                }
                break;
        }
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        storesList = new ArrayList<Result>();
        ivBack.setVisibility(View.VISIBLE);
        tvClear.setVisibility(View.VISIBLE);
        tvClear.setText(getString(R.string.save));
        tvTitle.setText(getString(R.string.stores));
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        storesAdapter = new StoresAdapter(this, storesList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                if (position >= 0 && storesList.get(position).isSelected()){
                    for (int i=0; i<selectedStoresList.size(); i++){
                        if (selectedStoresList.get(i).getStoreId().equals(storesList.get(position).getStoreId())){
                            selectedStoresList.remove(i);
                        }
                    }
                }else {
                    selectedStoresList.add(storesList.get(position));
                }
                storesList.get(position).setSelected(!storesList.get(position).isSelected());
                storesAdapter.notifyDataSetChanged();
            }
        });
        if (getIntent() != null && getIntent().getExtras() != null) {
            LatLng latlng = getIntent().getExtras().getParcelable(Constants.IntentConstant.LOCATION);
            currentLocation = new Location("origin");
            currentLocation.setLatitude(latlng == null ? 0 : latlng.latitude);
            currentLocation.setLongitude(latlng == null ? 0 : latlng.longitude);
            selectedStoresList = (ArrayList<Result>) getIntent().getExtras().getSerializable(Constants.IntentConstant.STORE);
            range =  getIntent().getExtras().getString(Constants.NetworkConstant.PARAM_RANGE, "");
            latitude =  getIntent().getExtras().getString(Constants.NetworkConstant.PARAM_LATITUDE, "");
            longitude =  getIntent().getExtras().getString(Constants.NetworkConstant.PARAM_LONGITUDE, "");
        }
        if (selectedStoresList == null) selectedStoresList = new ArrayList<>();
    }

    /**
     * method to set listener on views
     */
    private void setListener() {
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = layoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= storesList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(StoreListActivity.this)) {
                            isPagination = true;
                            hitStoresListApi();
                        }
                    }
                }
            }

        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(StoreListActivity.this)) {
                    isPagination = false;
                    count = 0;
                    hitStoresListApi();

                }
            }
        });

        etSearch.addTextChangedListener(this);


    }

    @Override
    public void afterTextChanged(Editable s) {
        ivCross.setVisibility(etSearch.getText().toString().length() > 0 ? View.VISIBLE : View.GONE);
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            count = 0;
            hitStoresListApi();
        }
    }

    /**
     * method to set adapter in views
     */
    private void setAdapters() {
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(storesAdapter);
    }

    /**
     * method to hit login api
     */
    private void hitStoresListApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_SEARCH, etSearch.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_LATITUDE, latitude);
        params.put(Constants.NetworkConstant.PARAM_LONGITUDE, longitude);
        params.put(Constants.NetworkConstant.PARAM_RANGE, range);
        params.put(Constants.NetworkConstant.COUNTRY_CODE, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.COUNTRY_CODE));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitStoreListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_STORES);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_STORES:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        StoreListResponse storeListResponse = new Gson().fromJson(response, StoreListResponse.class);
                        if (isPagination) {
                            isPagination = false;
                        } else {
                            storesList.clear();
                        }
                        storesList.addAll(storeListResponse.getResult());
                        for (Result selectStore : selectedStoresList) {
                            for (int i = 0; i < storesList.size(); i++) {
                                if (storesList.get(i).getStoreId().equals(selectStore.getStoreId())) {
                                    storesList.get(i).setSelected(true);
                                }
                            }
                        }
                        isMoreData = storeListResponse.getNext() != -1;
                        if (isMoreData) count = storeListResponse.getNext();
                        storesAdapter.notifyDataSetChanged();
                        if (storesList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;
                    case Constants.NetworkConstant.NO_DATA:
                        storesList.clear();
                        storesAdapter.notifyDataSetChanged();
                        if (storesList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        isPagination = false;
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        isPagination = false;
    }


    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }
}
