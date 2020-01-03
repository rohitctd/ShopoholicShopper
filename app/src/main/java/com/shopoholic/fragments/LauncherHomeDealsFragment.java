package com.shopoholic.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shopoholic.R;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.activities.ProductServiceDetailsActivity;
import com.shopoholic.adapters.BannerAdapter;
import com.shopoholic.adapters.ProductsAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.launcherhomeresponse.BannerArr;
import com.shopoholic.models.productdealsresponse.Result;
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
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 2/4/18.
 */

public class LauncherHomeDealsFragment extends Fragment {
    Unbinder unbinder;
    @BindView(R.id.rv_banners)
    RecyclerView rvBanners;
    @BindView(R.id.tv_view_all)
    CustomTextView tvViewAll;
    @BindView(R.id.rv_popular_deals)
    RecyclerView rvPopularDeals;
    @BindView(R.id.fl_popular_deals_title)
    FrameLayout flPopularDealsTitle;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    private View rootView;
    private AppCompatActivity mActivity;
    private List<Result> productList;
    private ProductsAdapter productsAdapter;
    private BannerAdapter bannerAdapter;
    private List<BannerArr> bannerList;
    public String type = "";
    private GridLayoutManager gridLayoutManager;
    private Handler handler;
    private Runnable runnable;
    private LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_launcher_home_deals, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        setAdapters();
        setListener();
        return rootView;
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        productList = new ArrayList<>();
        bannerList = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(mActivity, 2);
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        productsAdapter = new ProductsAdapter(mActivity, this, productList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.iv_like:
                        if (AppUtils.getInstance().isLoggedIn(mActivity) && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            AppUtils.getInstance().bounceAnimation(mActivity, view);
                            productList.get(position).setIsFavourite(productList.get(position).getIsFavourite().equals("1") ? "2" : "1");
                            productsAdapter.notifyItemChanged(position);
                            hitLikeProductsApi(position);
                            Fragment fragment = getParentFragment();
                            if (fragment instanceof LauncherHomeFragment){
                                ((LauncherHomeFragment)fragment).changeFavouriteIcon(type, productList.get(position).getId(), productList.get(position).getIsFavourite());
                            }
                        }
                        break;
                    case R.id.civ_user_image:
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.under_development));
                        break;
                    case R.id.rl_root_view:
//                        AppUtils.getInstance().showToast(mActivity, getString(R.string.under_development));
                        String dealId = productList.get(position).getId();
                        Intent intent = new Intent(mActivity, ProductServiceDetailsActivity.class);
                        intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
                        LauncherHomeDealsFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_PRODUCT_DEAL);
                        break;
                }
            }
        });
/*
        bannerAdapter = new BannerAdapter(mActivity, bannerList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.iv_banner:
                    case R.id.rl_root_view:
                        if (!bannerList.get(position).getDealId().equals("0")) {
                            String dealId = bannerList.get(position).getDealId();
                            Intent intent = new Intent(mActivity, ProductServiceDetailsActivity.class);
                            intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
                            LauncherHomeDealsFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_PRODUCT_DEAL);
                        }else {
                            if (bannerList.get(position).getType().equals("1")) {
                                if (mActivity instanceof HomeActivity) {
                                    com.shopoholic.models.preferredcategorymodel.WalletDetail cat = new com.shopoholic.models.preferredcategorymodel.WalletDetail();
                                    cat.setCatId(bannerList.get(position).getCategoryId());
                                    ((HomeActivity)mActivity).getFilterData().setCategoryDetails(cat);
                                }
                            }else {
                                if (mActivity instanceof HomeActivity) {
                                    int discount = Integer.parseInt(bannerList.get(position).getPercentage());
                                    ((HomeActivity)mActivity).getFilterData().setDiscountPercentage(discount);
                                }
                            }

                            if (mActivity instanceof HomeActivity) {
                                ((HomeActivity) mActivity).type = type;
                                ((HomeActivity) mActivity).resetSideMenu(-101);
                                ((HomeActivity) mActivity).setFragmentOnContainer(-101);
                            }
                        }
                        break;
                }
            }
        });
*/
    }


    /**
     * Method to hit the signup api
     *
     * @param position
     */
    public void hitLikeProductsApi(final int position) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_METHOD, Constants.NetworkConstant.SAVE_FAVOURITES_DEALS);
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, productList.get(position).getId());
        params.put(Constants.NetworkConstant.PARAM_IS_FAVOURITE, productList.get(position).getIsFavourite());
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        Call<ResponseBody> call = apiInterface.hitLikeDealsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
            }

            @Override
            public void onError(String response, int requestCode) {
                productList.get(position).setIsFavourite(productList.get(position).getIsFavourite().equals("1") ? "2" : "1");
                productsAdapter.notifyItemChanged(position);
                Fragment fragment = getParentFragment();
                if (fragment instanceof LauncherHomeFragment){
                    ((LauncherHomeFragment)fragment).changeFavouriteIcon(type, productList.get(position).getId(), productList.get(position).getIsFavourite());
                }
            }

            @Override
            public void onFailure() {
                productList.get(position).setIsFavourite(productList.get(position).getIsFavourite().equals("1") ? "2" : "1");
                productsAdapter.notifyItemChanged(position);
                Fragment fragment = getParentFragment();
                if (fragment instanceof LauncherHomeFragment){
                    ((LauncherHomeFragment)fragment).changeFavouriteIcon(type, productList.get(position).getId(), productList.get(position).getIsFavourite());
                }
            }
        }, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }

    /**
     * method to set adapters in views
     */
    private void setAdapters() {
        rvBanners.setLayoutManager(linearLayoutManager);
        rvBanners.setAdapter(bannerAdapter);
        rvPopularDeals.setLayoutManager(gridLayoutManager);
        rvPopularDeals.setAdapter(productsAdapter);
    }

    /**
     * method to set listeners in views
     */
    private void setListener() {
        rvPopularDeals.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                if (isMoreData && !isMapShow && !isLoading && !isPagination) {
//                    int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
//                    int totalVisibleItems = gridLayoutManager.getItemCount();
//                    if (firstVisibleItemPosition + totalVisibleItems >= productList.size() - 6) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
//                            isPagination = true;
//                            hitProductsDealsApi();
//                        }
//                    }
                }
            }

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_view_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_view_all:
                if (mActivity instanceof HomeActivity) {
                    ((HomeActivity) mActivity).type = type;
                    ((HomeActivity) mActivity).resetSideMenu(-101);
                    ((HomeActivity) mActivity).setFragmentOnContainer(-101);
                }
                break;
        }
    }

    /**
     * method to get data from api
     *
     * @param productList
     * @param bannerList
     * @param type
     */
    public void getPageData(List<Result> productList, List<BannerArr> bannerList, String type) {
        this.type = type;
        flPopularDealsTitle.setVisibility(View.VISIBLE);
        this.productList.addAll(productList);
        productsAdapter.notifyDataSetChanged();
        this.bannerList.addAll(bannerList);
        bannerAdapter.notifyDataSetChanged();
        layoutNoDataFound.setVisibility(productList.size() == 0 ? View.VISIBLE : View.GONE);
        setTimer();
    }

    /**
     * method to set the timer
     */
    private void setTimer() {
        if (bannerList.size() > 1) {
            handler = new Handler();
            runnable = () -> {
                if (isAdded()) {
                    int position = linearLayoutManager.findFirstVisibleItemPosition();
                    rvBanners.smoothScrollToPosition(position < bannerList.size() - 1 ? position + 1 : 0);
                    handler.postDelayed(runnable, Constants.AppConstant.BANNER_TIME_OUT);
                }
            };
            if (isAdded()) {
                handler.postDelayed(runnable, Constants.AppConstant.BANNER_TIME_OUT);
            }
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

    /**
     * method to change the favourite icon of other fragment
     * @param id
     * @param isFavourite
     */
    public void changeFavouriteIcon(String id, String isFavourite) {
        for (int i=0; i<productList.size(); i++){
            if (productList.get(i).getId().equals(id)){
                productList.get(i).setIsFavourite(isFavourite);
                productsAdapter.notifyItemChanged(i);
                break;
            }
        }
    }
}