package com.shopoholic.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.activities.CommonActivity;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.activities.HuntBuddyListActivity;
import com.shopoholic.adapters.ProductHuntAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.activities.SingleChatActivity;
import com.shopoholic.models.producthuntlistresponse.ProductHuntListResponse;
import com.shopoholic.models.producthuntlistresponse.Result;
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
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;

public class ProductHuntListingFragment extends Fragment implements NetworkListener {

    private Unbinder unbinder;

    @BindView(R.id.rv_posted)
    RecyclerView rvPosted;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.srl_deal_post)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;

    private boolean isLoading;
    private List<Result> huntList;
    private ProductHuntAdapter productHuntAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean isMoreData;
    private int count = 0;
    private AppCompatActivity mActivity;
    private boolean isPagination;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_block_deals, container, false);
        unbinder = ButterKnife.bind(this, view);
        initVariables();
        setAdapter();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading)
            hitProductHuntListApi();

        return view;
    }

    /**
     * This method initializes the variables used
     */
    private void initVariables() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        mActivity = (AppCompatActivity) getActivity();
        huntList = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(false);
        if (((HomeActivity) mActivity).getIntent() != null)
            ((HomeActivity) mActivity).setIntent(null);
    }

    /**
     * This method sets the adapter for the recycler view
     */
    private void setAdapter() {
        productHuntAdapter = new ProductHuntAdapter(mActivity, huntList, this);
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rvPosted.setLayoutManager(linearLayoutManager);
        rvPosted.setAdapter(productHuntAdapter);
    }

    /**
     * method to set listeners in views
     */
    public void setListeners() {
        rvPosted.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= huntList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(getActivity())) {
                            isPagination = true;
                            hitProductHuntListApi();
                        }
                    }
                }
            }

        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                count = 0;
                hitProductHuntListApi();
            }
        });
    }

    /**
     * method to get post deals
     */
    private void hitProductHuntListApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(getActivity(), ApiInterface.class);
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));

        Call<ResponseBody> call = apiInterface.hitProductHuntListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_PRODUCT_HUNT);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_PRODUCT_HUNT:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            ProductHuntListResponse dealsResponse = new Gson().fromJson(response, ProductHuntListResponse.class);
                            if (isPagination) {
                                isPagination = false;
                            } else {
                                huntList.clear();
                            }
                            huntList.addAll(dealsResponse.getResult());
                            productHuntAdapter.notifyDataSetChanged();
                            isMoreData = dealsResponse.getNext() != -1;
                            if (isMoreData) count = dealsResponse.getNext();
                            if (huntList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;

                        case Constants.NetworkConstant.NO_DATA:
                            if (isAdded()) {
                                isLoading = false;
                                isPagination = false;
                                progressBar.setVisibility(View.GONE);
                                if (huntList.size() == 0) {
                                    layoutNoDataFound.setVisibility(View.VISIBLE);
                                } else {
                                    layoutNoDataFound.setVisibility(View.GONE);
                                }
                                if (swipeRefreshLayout.isRefreshing())
                                    swipeRefreshLayout.setRefreshing(false);
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
            if (huntList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            if (huntList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * method to get the click
     * @param position
     */
    public void onPopupItemClick(final int type, final int position) {
        switch (type) {
            case 1:
                ProductHuntListingFragment.this.startActivityForResult(new Intent(mActivity, CommonActivity.class)
                                .putExtra(Constants.IntentConstant.FRAGMENT_TYPE, 2)
                                .putExtra(Constants.IntentConstant.IS_PRODUCT, huntList.get(position).getProductType().equals("1"))
                                .putExtra(Constants.IntentConstant.PRODUCT_DETAILS, huntList.get(position))
                                .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.EDIT_HUNT)
                        , Constants.IntentConstant.REQUEST_ORDER);
                break;
            case 2:
                new AlertDialog.Builder(mActivity, R.style.DatePickerTheme)
                        .setMessage(getString(R.string.sure_to_delete_hunt))
                        .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                                hitDeleteHuntApi(huntList.get(position).getId());
                                huntList.remove(position);
                                productHuntAdapter.notifyItemRemoved(position);
                                productHuntAdapter.notifyItemRangeChanged(position, huntList.size() - position);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                            // do nothing
                        })
                        .show();
                break;
        }
    }

    /**
     * method to get post deals
     * @param huntId
     */
    private void hitDeleteHuntApi(String huntId) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(getActivity(), ApiInterface.class);
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntId);

        Call<ResponseBody> call = apiInterface.hitDeleteHuntApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_DELETE_HUNT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.IntentConstant.REQUEST_ORDER && resultCode == RESULT_OK){
            if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                count = 0;
                hitProductHuntListApi();
            }
            /*if (data != null && data.getExtras() != null){
                WalletDetail hunt = (WalletDetail) data.getExtras().getSerializable(Constants.IntentConstant.PRODUCT_DETAILS);
                if (hunt != null){
                    for (int i = 0; i< huntList.size(); i++){
                        if (huntList.get(i).getId().equals(hunt.getId())){
                            huntList.set(i, hunt);
                            productHuntAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }*/
        }
        if (requestCode == 1001 && resultCode == RESULT_OK  && data != null && data.getExtras() != null) {
            String userId = data.getExtras().getString(Constants.IntentConstant.USER_ID, "");
            String huntId = data.getExtras().getString(Constants.IntentConstant.HUNT_ID, "");
            for (int i=0; i<huntList.size(); i++) {
                if (huntList.get(i).getId().equals(huntId)) {
                    huntList.get(i).setUserId(userId);
                    break;
                }
            }
            productHuntAdapter.notifyDataSetChanged();
        }
    }

    /**
     * function to open chat activity
     * @param position
     */
    public void openBuddyListActivity(int position) {
        AppUtils.getInstance().hitUpdateCountApi(mActivity, huntList.get(position).getId());
        huntList.get(position).setIsRead("1");
        productHuntAdapter.notifyItemChanged(position);
        Intent intent = new Intent(mActivity, HuntBuddyListActivity.class);
        intent.putExtra(Constants.IntentConstant.HUNT_DEAL, huntList.get(position));
        ProductHuntListingFragment.this.startActivityForResult(intent, 1001);
    }
}
