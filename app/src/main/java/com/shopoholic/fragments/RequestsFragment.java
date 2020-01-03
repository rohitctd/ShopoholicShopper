package com.shopoholic.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.shopoholic.R;

import com.shopoholic.adapters.RequestsAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.requestresponse.RequestResponse;
import com.shopoholic.models.requestresponse.Result;
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


public class RequestsFragment extends Fragment implements NetworkListener{

    Unbinder unbinder;
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

    private RequestsAdapter requestsAdapter;
    private List<Result> requestList;
    private AppCompatActivity mActivity;
    private boolean isLoading;
    private boolean isMoreData;
    private int count = 0;
    private LinearLayoutManager linearLayoutManager;
    private boolean isPagination;

    public RequestsFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_recycle_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        requestList = new ArrayList<>();
        initViews();
        setAdapters();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            progressBar.setVisibility(View.VISIBLE);
            hitGetRequestsListApi();
        }
        return view;
    }

    /**
     * function to intialize the variables
     */
    private void initViews() {
        requestList = new ArrayList<>();
        mActivity = (AppCompatActivity) getActivity();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
    }

    /**
     * method to set adapters in views
     */
    private void setAdapters() {
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        requestsAdapter = new RequestsAdapter(mActivity, requestList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()){
                    case R.id.tv_request_cancel:
                        hitAcceptRejectApi(2,position);
                        break;
                    case R.id.tv_request_accept:
                        hitAcceptRejectApi(2,position);
                        break;
                    case R.id.tv_request_reject:
                        hitAcceptRejectApi(3,position);
                        break;

                }
                requestList.remove(position);
                requestsAdapter.notifyItemRemoved(position);
                requestsAdapter.notifyItemRangeChanged(position, requestList.size());
                if (requestList.size() == 0) {
                    layoutNoDataFound.setVisibility(View.VISIBLE);
                } else {
                    layoutNoDataFound.setVisibility(View.GONE);
                }

            }
        });
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(requestsAdapter);

    }

    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                    count = 0;
                    hitGetRequestsListApi();
                }
            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= requestList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            isPagination = true;
                            hitGetRequestsListApi();
                        }
                    }
                }
            }

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * method to get the request list
     */
    private void hitGetRequestsListApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.DEVICE_ID, Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID));
        params.put(Constants.NetworkConstant.TYPE, "1");
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.contacts(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_FRIENDS);
    }

    /**
     * method to accept or reject request
     * @param requestStatus
     * @param position
     */
    private void hitAcceptRejectApi(int requestStatus, final int position) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call;
        if (!requestList.get(position).getRequestedId().equals(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID))) {
            params.put(Constants.NetworkConstant.REQUESTED_ID, requestList.get(position).getFriendId());
            params.put(Constants.NetworkConstant.REQUESTED_STATUS, String.valueOf(requestStatus));
            call = apiInterface.hitAcceptRejectApi(AppUtils.getInstance().encryptData(params));
        }else {
            params.put(Constants.NetworkConstant.FRIEND_ID, requestList.get(position).getFriendId());
            params.put(Constants.NetworkConstant.PARAM_STATUS, "3");
            call = apiInterface.hitRequestFriendApi(AppUtils.getInstance().encryptData(params));
        }
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                if (responseCode == Constants.NetworkConstant.SUCCESS_CODE){
                   /* requestList.remove(position);
                    requestsAdapter.notifyItemChanged(position);
                    if (requestList.size() == 0) {
                        layoutNoDataFound.setVisibility(View.VISIBLE);
                    } else {
                        layoutNoDataFound.setVisibility(View.GONE);
                    }*/
                }
            }

            @Override
            public void onError(String response, int requestCode) {

            }

            @Override
            public void onFailure() {

            }
        }, Constants.NetworkConstant.ACCEPT_REJECT_REQUEST);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_FRIENDS:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            RequestResponse requestResponse = new Gson().fromJson(response, RequestResponse.class);
                            if (!isPagination){
                                requestList.clear();
                            }else {
                                isPagination = false;
                            }
                            requestList.addAll(requestResponse.getResult());
                            requestsAdapter.notifyDataSetChanged();
                            isMoreData = requestResponse.getNext() != -1;
                            if (isMoreData) count = requestResponse.getNext();
                            if (requestList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;

                        case Constants.NetworkConstant.NO_DATA:
                            requestList.clear();
                            requestsAdapter.notifyDataSetChanged();
                            if (requestList.size() == 0) {
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
            isPagination=false;
            progressBar.setVisibility(View.GONE);
            requestsAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (requestList.size() == 0) {
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
            isPagination=false;
            progressBar.setVisibility(View.GONE);
            requestsAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (requestList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
        }
    }
}
