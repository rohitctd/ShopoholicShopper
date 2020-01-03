package com.shopoholic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.PreferredCategoriesAdapter;
import com.shopoholic.adapters.SubCategoriesAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.preferredcategorymodel.PreferredCategoriesResponse;
import com.shopoholic.models.preferredcategorymodel.Result;
import com.shopoholic.models.subcategoryresponse.SubCategoryResponse;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
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

public class PreferredCategoriesActivity extends BaseActivity implements NetworkListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.recycle_view)
    RecyclerView rvCategories;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private List<Result> preferredCategoriesList;
    private PreferredCategoriesAdapter preferredCategoriesAdapter;
    private List<com.shopoholic.models.subcategoryresponse.Result> subCategoriesList;
    private SubCategoriesAdapter subCategoriesAdapter;
    private List<Result> selectedCategories;
    public String fromClass = "";
    private String categoryIds = "";
    private LinearLayoutManager linearLayoutManager;
    private int count = 0;
    private boolean isMoreData;
    private boolean isLoading;
    private boolean isPagination;
    private boolean isCategory;
    private String catId;
    private int check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred_categories);
        ButterKnife.bind(this);
        initVariables();
        setAdapters();
        setListeners();
        progressBar.setVisibility(View.VISIBLE);
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            hitGetCategoryListApi();
        } else {
            AppUtils.getInstance().showToast(this, getString(R.string.no_internet_connection));
        }
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        onBackPressed();
    }

    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(PreferredCategoriesActivity.this)) {
                    count = 0;
                    hitGetCategoryListApi();
                }
            }
        });
        rvCategories.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= preferredCategoriesList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(PreferredCategoriesActivity.this)) {
                            isPagination = true;
                            hitGetCategoryListApi();
                        }
                    }
                }
            }

        });

    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        preferredCategoriesList = new ArrayList<>();
        subCategoriesList = new ArrayList<>();
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.preferred_categories));
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        preferredCategoriesAdapter = new PreferredCategoriesAdapter(this, preferredCategoriesList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                if (fromClass.equals(Constants.AppConstant.FILTER)) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.IntentConstant.CATEGORY, preferredCategoriesList.get(position));
                    setResult(RESULT_OK, intent);
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                    finish();
                }
            }
        });
        subCategoriesAdapter = new SubCategoriesAdapter(this, subCategoriesList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                if (fromClass.equals(Constants.AppConstant.FILTER)) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.IntentConstant.CATEGORY, subCategoriesList.get(position));
                    setResult(RESULT_OK, intent);
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                    finish();
                }
            }
        });
        if (getIntent() != null && getIntent().getExtras() != null) {
            fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
            isCategory = getIntent().getExtras().getBoolean(Constants.IntentConstant.IS_CATEGORY, true);
            catId = getIntent().getExtras().getString(Constants.IntentConstant.CATEGORY, "");
            check = getIntent().getExtras().getInt(Constants.IntentConstant.CHECK, 0);
        }
        if (fromClass.equals(Constants.AppConstant.FILTER)) {
            tvTitle.setText(getString(R.string.preferred_category));
        }
        if (!isCategory){
            tvTitle.setText(getString(R.string.sub_category));
        }
    }

    /**
     * method to set adapter in views
     */
    private void setAdapters() {
        rvCategories.setLayoutManager(linearLayoutManager);
        rvCategories.setAdapter(preferredCategoriesAdapter);
    }


    /**
     * method to hit login api
     */
    private void hitGetCategoryListApi() {
//        progressBar.setVisibility(View.VISIBLE);
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call;
        if (isCategory) {
            call = apiInterface.hitGetPreferredCategoryListApi(AppUtils.getInstance().encryptData(params));
        }else {
            params.put(Constants.NetworkConstant.PARAM_DEAL_CATEGORY_ID, catId);
            call = apiInterface.hitGetSubCategoryListApi(AppUtils.getInstance().encryptData(params));
        }
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_CATEGORIES:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        if (isCategory) {
                            PreferredCategoriesResponse categoriesResponse = new Gson().fromJson(response, PreferredCategoriesResponse.class);
                            if (isPagination) {
                                isPagination = false;
                            } else {
                                preferredCategoriesList.clear();
                            }
                            if (check == 0) {
                                preferredCategoriesList.addAll(categoriesResponse.getResult());
                            }else {
                                for (Result category : categoriesResponse.getResult()){
                                    if (category.getCategoryType().equals(String.valueOf(check))){
                                        preferredCategoriesList.add(category);
                                    }
                                }
                            }
                            if (selectedCategories != null && selectedCategories.size() > 0) {
                                for (Result category : selectedCategories) {
                                    for (int i = 0; i < preferredCategoriesList.size(); i++) {
                                        if (category.getCatId().equals(preferredCategoriesList.get(i).getCatId())) {
                                            preferredCategoriesList.get(i).setSelected(true);
                                            break;
                                        }
                                    }
                                }
                            }
                            rvCategories.setAdapter(preferredCategoriesAdapter);
                            preferredCategoriesAdapter.notifyDataSetChanged();
                            isMoreData = categoriesResponse.getNext() != -1;
                            if (isMoreData) count = categoriesResponse.getNext();
                            if (preferredCategoriesList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                        } else {
                            SubCategoryResponse subCategoriesResponse = new Gson().fromJson(response, SubCategoryResponse.class);
                            if (isPagination) {
                                isPagination = false;
                            } else {
                                subCategoriesList.clear();
                            }
                            subCategoriesList.addAll(subCategoriesResponse.getResult());
                            rvCategories.setAdapter(subCategoriesAdapter);
                            subCategoriesAdapter.notifyDataSetChanged();
                            isMoreData = subCategoriesResponse.getNext() != -1;
                            if (isMoreData) count = subCategoriesResponse.getNext();
                            if (subCategoriesList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        isPagination = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        isPagination = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

    }
}
