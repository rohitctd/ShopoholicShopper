package com.shopoholic.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.activities.ProductServiceDetailsActivity;
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
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class FavouritesFragment extends Fragment implements NetworkListener {


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
    Unbinder unbinder;
    private View rootView;
    private boolean isPosted;
    private ProductsAdapter productsAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<Result> productList;
    private boolean isMoreData;
    private String buddyId;
    private boolean isLoading;
    private int count = 0;
    private boolean isPagination;
    private AppCompatActivity mActivity;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_recycle_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariable();

        setAdapter();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(getActivity())) {
            count = 0;
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            hitFavouriteDealApi();
        }
        return rootView;
    } @Override
    public void onStop() {
        super.onStop();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     *  method to initialize Variable
     */
    private void initVariable() {
        mActivity = (AppCompatActivity) getActivity();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        productList = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(mActivity, 2);
        productsAdapter = new ProductsAdapter(mActivity, null, productList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.iv_like:
                        if (AppUtils.getInstance().isLoggedIn(mActivity) && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            hitUnlikeProductsApi(position);
                            AppUtils.getInstance().bounceAnimation(mActivity, view);
                            productList.remove(position);
                            productsAdapter.notifyItemRemoved(position);
                            productsAdapter.notifyItemRangeChanged(position, productList.size());
                        }
                        break;
                    case R.id.civ_user_image:
                        break;
                    case R.id.iv_product_pic:
                        String dealId = productList.get(position).getId();
                        Intent intent = new Intent(mActivity, ProductServiceDetailsActivity.class);
                        intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
                        intent.putExtra(Constants.IntentConstant.DEAL_IMAGE, productList.get(position).getDealImage());
                        ActivityOptionsCompat options = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(mActivity, view, ViewCompat.getTransitionName(view));
                        FavouritesFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_PRODUCT_DEAL, options.toBundle());
                        break;
                }
            }
        });

    }

    /**
     * method to set adapter in views
     */
    private void setAdapter() {

        recycleView.setLayoutManager(gridLayoutManager);
        recycleView.setAdapter(productsAdapter);
    }
    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    count = 0;
                    hitFavouriteDealApi();
                }
            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = gridLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= productList.size() - 6) {
                        if (AppUtils.getInstance().isInternetAvailable(getActivity())) {
                            isPagination = true;
                            hitFavouriteDealApi();
                        }
                    }
                }
            }

        });

    }

    /**
     * method to hit favourites deals api
     */
    private void hitFavouriteDealApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_METHOD, Constants.NetworkConstant.GET_ALL_DEALS);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_FAVOURITE_DEALS, "1");
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));

        Call<ResponseBody> call = apiInterface.hitProductsDealsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.BUDDY_DEAL);

    }

    /**
     * Method to hit the unlike deal api
     *
     * @param position
     */
    public void hitUnlikeProductsApi(final int position) {
        try {
            ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
            final HashMap<String, String> params = new HashMap<>();
            params.put(Constants.NetworkConstant.PARAM_METHOD, Constants.NetworkConstant.SAVE_FAVOURITES_DEALS);
            params.put(Constants.NetworkConstant.PARAM_DEAL_ID, productList.get(position).getId());
            params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
            params.put(Constants.NetworkConstant.PARAM_IS_FAVOURITE, "2");
            params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
            Call<ResponseBody> call = apiInterface.hitLikeDealsApi(AppUtils.getInstance().encryptData(params));
            ApiCall.getInstance().hitService(mActivity, call, null, 1);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
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
                            } else {
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
                                AppUtils.getInstance().showToast(getActivity(), new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
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
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
                    if (productList.size() == 0) {
                        layoutNoDataFound.setVisibility(View.VISIBLE);
                    } else {
                        layoutNoDataFound.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        }
        if (requestCode == Constants.IntentConstant.REQUEST_PRODUCT_DEAL && resultCode == Activity.RESULT_CANCELED && data != null && data.getExtras() != null) {
            String productId = data.getExtras().getString(Constants.IntentConstant.PRODUCT_ID, "");
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId().equals(productId)) {
                    productList.get(i).setIsFavourite(data.getExtras().getString(Constants.IntentConstant.IS_FAVOURITE));
                    if (!productList.get(i).getIsFavourite().equals("1")) {
                        productList.remove(i);
                        productsAdapter.notifyItemRemoved(i);
                        productsAdapter.notifyItemRangeChanged(i, productList.size());
                        if (productList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                    }
                    break;
                }
            }
        }
    }
}
