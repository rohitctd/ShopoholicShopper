package com.shopoholic.fragments;

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
import com.shopoholic.adapters.BlockDealsAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.models.blockdealresponse.BlockDealResponse;
import com.shopoholic.models.blockdealresponse.Result;
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

public class BlockDealsFragment extends Fragment implements NetworkListener {

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
    private List<Result> productList;
    private BlockDealsAdapter blockDealsAdapter;
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
            hitBlockDealsApi();

        return view;
    }

    /**
     * This method initializes the variables used
     */
    private void initVariables() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        mActivity = (AppCompatActivity) getActivity();
        productList = new ArrayList<com.shopoholic.models.blockdealresponse.Result>();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * This method sets the adapter for the recycler view
     */
    private void setAdapter() {
        blockDealsAdapter = new BlockDealsAdapter(mActivity, productList, this);
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rvPosted.setLayoutManager(linearLayoutManager);
        rvPosted.setAdapter(blockDealsAdapter);
    }

    public void setListeners() {
        rvPosted.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= productList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(getActivity())) {
                            isPagination = true;
                            hitBlockDealsApi();
                        }
                    }
                }
            }

        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                    count = 0;
                    hitBlockDealsApi();
                }
            }
        });
    }

    /**
     * method to get post deals
     */
    private void hitBlockDealsApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(getActivity(), ApiInterface.class);
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(getActivity(), AppSharedPreference.PREF_KEY.USER_ID));
//        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, AppSharedPreference.getInstance().getString(getActivity(), AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitBlockDealsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_DEAL);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_DEAL:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            BlockDealResponse dealsResponse = new Gson().fromJson(response, BlockDealResponse.class);
                            if (isPagination) {
                                isPagination = false;
                            } else {
                                productList.clear();
                            }
                            productList.addAll(dealsResponse.getResult());
                            blockDealsAdapter.notifyDataSetChanged();
                            isMoreData = dealsResponse.getNext() != -1;
                            if (isMoreData) count = dealsResponse.getNext();
                            if (productList.size() == 0) {
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
                                if (productList.size() == 0) {
                                    layoutNoDataFound.setVisibility(View.VISIBLE);
                                } else {
                                    layoutNoDataFound.setVisibility(View.GONE);
                                }
                                if (swipeRefreshLayout.isRefreshing())
                                    swipeRefreshLayout.setRefreshing(false);
                                //AppUtils.getInstance().showToast(mActivity, response);
                            }
                            break;
                    }
                    break;
                case Constants.NetworkConstant.REQUEST_BLOCK_DEAL:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            if (productList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
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
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            if (productList.size() == 0) {
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
            if (productList.size() == 0) {
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
    public void onPopupItemClick(final int position) {
        new AlertDialog.Builder(mActivity, R.style.DatePickerTheme).setTitle(getString(R.string.unblock_deal))
                .setMessage(getString(R.string.sure_to_unblock_deal))
                .setPositiveButton(getString(R.string.unblock), (dialog, which) -> {
                    if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                        hitUnblockDealsApi(productList.get(position).getDealId());
                        productList.remove(position);
                        blockDealsAdapter.notifyItemRemoved(position);
                        blockDealsAdapter.notifyItemRangeChanged(position, productList.size());
                        if (productList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    // do nothing
                })
                .show();

    }




    /**
     * method to delete specialization skills
     * @param id
     */
    private void hitUnblockDealsApi(String id) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, id);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitBlockDealApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_BLOCK_DEAL);
    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_EDIT_DEAL && resultCode == RESULT_OK){
            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                count = 0;
                hitBlockDealsApi();
            }
        }
    }*/
}
