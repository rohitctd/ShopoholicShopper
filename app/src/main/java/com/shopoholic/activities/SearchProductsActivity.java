package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.SearchListAdapter;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.searchresponse.SearchResponse;
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
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class created by Sachin on 26-Apr-18.
 */
public class SearchProductsActivity extends BaseActivity implements NetworkListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.et_search)
    CustomEditText etSearch;
    @BindView(R.id.iv_cross)
    ImageView ivCross;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
    @BindView(R.id.tv_search)
    CustomTextView tvSearch;
    @BindView(R.id.recycle_view)
    RecyclerView rvSearchProducts;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private List<String> searchList;
    private List<String> selectedSearchList;
    private SearchListAdapter searchAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        ButterKnife.bind(this);
        initVariables();
        setListeners();
        setAdapter();
        if (AppUtils.getInstance().isInternetAvailable(this) &&
                AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_SIGN_UP))
            hitSearchListApi();
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        ivBack.setVisibility(View.VISIBLE);
        tvClear.setVisibility(View.VISIBLE);
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setEnabled(false);
        searchList = new ArrayList<>();
        selectedSearchList = new ArrayList<>();
        searchAdapter = new SearchListAdapter(this, selectedSearchList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View clickedView) {
                LocalBroadcastManager.getInstance(SearchProductsActivity.this).sendBroadcast(new Intent(Constants.IntentConstant.SEARCH)
                        .putExtra(Constants.IntentConstant.SEARCH, selectedSearchList.get(position)));
                finish();
            }
        });

    }

    /**
     * set listener on views
     */
    private void setListeners() {
        etSearch.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable s) {
        ivCross.setVisibility(etSearch.getText().toString().length() > 0 ? View.VISIBLE : View.GONE);
        search(s.toString());
    }

    @SuppressLint("DefaultLocale")
    private void search(String text) {
        selectedSearchList.clear();
        for (String search : searchList) {
            if (search.toLowerCase(Locale.ENGLISH).contains(text.toLowerCase())) {
                selectedSearchList.add(search);
            }
        }
        searchAdapter.notifyDataSetChanged();
        if (selectedSearchList.size() > 0){
            layoutNoDataFound.setVisibility(View.GONE);
        }else {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        }
    }

    /**
     * set adapters on views
     */
    private void setAdapter() {
        rvSearchProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSearchProducts.setAdapter(searchAdapter);
    }

    @OnClick({R.id.iv_back, R.id.tv_clear, R.id.iv_cross, R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_clear:
                if (AppUtils.getInstance().isInternetAvailable(this) &&
                        AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_SIGN_UP))
                    hitClearListApi();
                break;
            case R.id.iv_cross:
                etSearch.setText("");
                break;
            case R.id.tv_search:
//                if (etSearch.getText().toString().trim().length() > 0) {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.SEARCH).putExtra(Constants.IntentConstant.SEARCH, etSearch.getText().toString().trim()));
                    finish();
//                } else{
//                    AppUtils.getInstance().showToast(this, getString(R.string.empty_search_message));
//                }
                break;
        }
    }


    /**
     * Method to hit the like/unlike product api
     */
    public void hitSearchListApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitSearchListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_SEARCH);
    }


    /**
     * Method to hit the like/unlike product api
     */
    public void hitClearListApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitClearListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_CLEAR);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        progressBar.setVisibility(View.GONE);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_SEARCH:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        SearchResponse searchResponse = new Gson().fromJson(response, SearchResponse.class);
                        searchList.addAll(searchResponse.getResult());
                        selectedSearchList.addAll(searchResponse.getResult());
                        searchAdapter.notifyDataSetChanged();
                        break;
                    case Constants.NetworkConstant.NO_DATA:
                        searchList.clear();
                        searchAdapter.notifyDataSetChanged();
                        break;
                }
                break;
            case Constants.NetworkConstant.REQUEST_CLEAR:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        searchList.clear();
                        selectedSearchList.clear();
                        searchAdapter.notifyDataSetChanged();
                        etSearch.setText("");
                        break;
                }
                break;
        }
        if (selectedSearchList.size() > 0){
            layoutNoDataFound.setVisibility(View.GONE);
        }else {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        progressBar.setVisibility(View.GONE);
    }
}
