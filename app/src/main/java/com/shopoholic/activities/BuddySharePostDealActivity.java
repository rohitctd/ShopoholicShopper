package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.ProductsAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.productdealsresponse.ProductDealsResponse;
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
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class BuddySharePostDealActivity extends AppCompatActivity implements NetworkListener {

    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.rv_buddy_shared_posted)
    RecyclerView rvBuddySharedPosted;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    private boolean isPosted;
    private ProductsAdapter productsAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<Result> productList;
    private boolean isMoreData;
    private String buddyId;
    private boolean isLoading;
    private int count = 0;
    private boolean isPagination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_share_post_deal);
        ButterKnife.bind(this);
        initVariable();
        getDataFromIntent();
        setAdapter();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            count = 0;
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            hitBuddyDealApi();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getDataFromIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            buddyId = getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_ID);
            isPosted = getIntent().getExtras().getBoolean(Constants.IntentConstant.BUDDY_DEAL);
            if (isPosted) {
                tvTitle.setText(R.string.deals_posted);
            } else {
                tvTitle.setText(R.string.deals_shared);
            }
        }
    }

    private void initVariable() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        productList = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(this, 2);
        productsAdapter = new ProductsAdapter(this, null, productList, new RecyclerCallBack() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.iv_like:
                        if (AppUtils.getInstance().isLoggedIn(BuddySharePostDealActivity.this) && AppUtils.getInstance().isInternetAvailable(BuddySharePostDealActivity.this)) {
                            AppUtils.getInstance().bounceAnimation(BuddySharePostDealActivity.this, view);
                            productList.get(position).setIsFavourite(productList.get(position).getIsFavourite().equals("1") ? "2" : "1");
                            productsAdapter.notifyItemChanged(position);
                            hitLikeProductsApi(position);
                        }
                        break;
                    case R.id.civ_user_image:
                        break;
                    case R.id.iv_product_pic:
                        String dealImage = productList.get(position).getDealImage();
                        dealImage = dealImage.contains(",") ? dealImage.split(",")[0] : dealImage;
                        String dealId = productList.get(position).getId();
                        Intent intent = new Intent(BuddySharePostDealActivity.this, ProductServiceDetailsActivity.class);
                        intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
                        intent.putExtra(Constants.IntentConstant.DEAL_IMAGE, dealImage);
                        if (!isPosted) intent.putExtra(Constants.IntentConstant.IS_SHARED, true);
                        if (!isPosted) intent.putExtra(Constants.IntentConstant.BUDDY_ID, buddyId);
                        ActivityOptionsCompat options = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(BuddySharePostDealActivity.this, view, ViewCompat.getTransitionName(view));
                        startActivityForResult(intent, Constants.IntentConstant.REQUEST_PRODUCT_DEAL, options.toBundle());
                        break;
                }
            }
        });
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
    }

    private void setAdapter() {

        rvBuddySharedPosted.setLayoutManager(gridLayoutManager);
        rvBuddySharedPosted.setAdapter(productsAdapter);
    }


    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(BuddySharePostDealActivity.this)) {
                    count = 0;
                    hitBuddyDealApi();
                }
            }
        });
        rvBuddySharedPosted.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = gridLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= productList.size() - 6) {
                        if (AppUtils.getInstance().isInternetAvailable(BuddySharePostDealActivity.this)) {
                            isPagination = true;
                            hitBuddyDealApi();
                        }
                    }
                }
            }

        });

    }


    /**
     * Method to hit the like deal api
     *
     * @param position
     */
    public void hitLikeProductsApi(final int position) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_METHOD, Constants.NetworkConstant.SAVE_FAVOURITES_DEALS);
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, productList.get(position).getId());
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        params.put(Constants.NetworkConstant.PARAM_IS_FAVOURITE, productList.get(position).getIsFavourite());
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitLikeDealsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
            }

            @Override
            public void onError(String response, int requestCode) {
                productList.get(position).setIsFavourite(productList.get(position).getIsFavourite().equals("1") ? "2" : "1");
                productsAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure() {
                productList.get(position).setIsFavourite(productList.get(position).getIsFavourite().equals("1") ? "2" : "1");
                productsAdapter.notifyItemChanged(position);
            }
        }, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }

    /**
     * method to get buddy deals
     */
    private void hitBuddyDealApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, buddyId);
        params.put(Constants.NetworkConstant.PARAM_IS_SHARED, isPosted ? "0" : "1");
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        Call<ResponseBody> call = apiInterface.hitBuddyDealsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.BUDDY_DEAL);

    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        switch (requestCode) {
            case Constants.NetworkConstant.BUDDY_DEAL:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        ProductDealsResponse dealsResponse = new Gson().fromJson(response, ProductDealsResponse.class);
                        if (!isPagination) {
                            productList.clear();
                        }else {
                            isPagination = false;
                        }
                        productList.addAll(dealsResponse.getResult());
                        productsAdapter.notifyDataSetChanged();
                        isMoreData = dealsResponse.getNext() != -1;
                        if (isMoreData) count = dealsResponse.getNext();
                        if (productList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;
                    case Constants.NetworkConstant.NO_DATA:
                        productList.clear();
                        productsAdapter.notifyDataSetChanged();
                        if (productList.size() > 0) {
                            layoutNoDataFound.setVisibility(View.GONE);
                        } else {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
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
        isPagination = false;
        progressBar.setVisibility(View.GONE);
        productsAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (productList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        isPagination = false;
        productsAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (productList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.iv_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_PRODUCT_DEAL && resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            String productId = data.getExtras().getString(Constants.IntentConstant.PRODUCT_ID, "");
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId().equals(productId)) {
                    productList.remove(i);
                    productsAdapter.notifyItemRemoved(i);
                    productsAdapter.notifyItemRangeChanged(i, productList.size());
                    break;
                }
            }
        }
        if (requestCode == Constants.IntentConstant.REQUEST_PRODUCT_DEAL && resultCode == Activity.RESULT_CANCELED && data != null && data.getExtras() != null) {
            String productId = data.getExtras().getString(Constants.IntentConstant.PRODUCT_ID, "");
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId().equals(productId)) {
                    productList.get(i).setIsFavourite(data.getExtras().getString(Constants.IntentConstant.IS_FAVOURITE));
                    productsAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }
}
