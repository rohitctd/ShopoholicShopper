package com.shopoholic.fragments;


import android.os.Bundle;
import android.provider.Settings;
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
import com.shopoholic.adapters.FriendsAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.friendsresponse.FriendsResponse;
import com.shopoholic.models.friendsresponse.Result;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements NetworkListener {
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
    private FriendsAdapter friendsAdapter;
    private List<Result> friendList;
    private AppCompatActivity mActivity;
    private boolean isLoading;
    private boolean isMoreData;
    private int count = 0;
    private LinearLayoutManager linearLayoutManager;
    private boolean isPagination;

    @Override
    public void onStop() {
        super.onStop();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_recycle_view, container, false);
        friendList = new ArrayList<>();
        unbinder = ButterKnife.bind(this, view);
        initViews();
        setAdapter();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)){
            hitGetFriendsListApi();
        }
        return view;

    }

    /**
     * method to initialize the views
     */
    private void initViews() {
        friendList = new ArrayList<>();
        mActivity = (AppCompatActivity) getActivity();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
    }

    /**
     * method to set adapters in views
     */
    private void setAdapter() {
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        friendsAdapter = new FriendsAdapter(mActivity, friendList, (position, view) -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                hitRequestApi(position, friendList.get(position).getRequestStatus());
                switch (friendList.get(position).getRequestStatus()){
                    case "0":
                        friendList.get(position).setRequestStatus("1");
                        break;
                    case "1":
                        friendList.get(position).setRequestStatus("2");
                        break;
                    case "2":
                        friendList.get(position).setRequestStatus("0");
                        break;
                    case "3":
                        friendList.get(position).setRequestStatus("1");
                        break;
                }
                friendsAdapter.notifyItemChanged(position);
            }
        });
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(friendsAdapter);
    }



    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {

            if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                count = 0;
                hitGetFriendsListApi();
            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= friendList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            isPagination = true;
                            hitGetFriendsListApi();
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
     * method to hit friend list api
     */
    private void hitGetFriendsListApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.DEVICE_ID, Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID));
        params.put(Constants.NetworkConstant.TYPE, "2");
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.contacts(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_FRIENDS);
    }

    /**
     * This method hits the api to send request to a user or remove a friend
     * @param position
     */
    private void hitRequestApi(final int position, final String status) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.FRIEND_ID, friendList.get(position).getFriendId());
        Call<ResponseBody> call;
        if (status.equals("2")){
            call = apiInterface.hitRemoveFriendApi(AppUtils.getInstance().encryptData(params));
        }else {
            params.put(Constants.NetworkConstant.PARAM_STATUS, status.equals("1") ? "3" : "1");
            call = apiInterface.hitRequestFriendApi(AppUtils.getInstance().encryptData(params));
        }
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                if (isAdded()) {
                    if (status.equals("2")) {
                        friendList.get(position).setRequestStatus("0");
                        friendsAdapter.notifyItemChanged(position);
                    } else {
                        friendList.remove(position);
                        friendsAdapter.notifyItemRemoved(position);
                        friendsAdapter.notifyItemRangeChanged(position, friendList.size());
                    }
                    if (friendList.size() == 0) {
                        layoutNoDataFound.setVisibility(View.VISIBLE);
                    } else {
                        layoutNoDataFound.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(String response, int requestCode) {
                if (isAdded()) {
                    if (friendList.size() > position) {
                        friendList.get(position).setRequestStatus(status);
                    }
                    friendsAdapter.notifyItemChanged(position);
                    if (friendList.size() == 0) {
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
                    if (friendList.size() > position) {
                        friendList.get(position).setRequestStatus(status);
                    }
                    friendsAdapter.notifyItemChanged(position);
                    if (friendList.size() == 0) {
                        layoutNoDataFound.setVisibility(View.VISIBLE);
                    } else {
                        layoutNoDataFound.setVisibility(View.GONE);
                    }

                }
            }
        }, Constants.NetworkConstant.SEND_FRIEND_REQUEST);

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
                            FriendsResponse friendsResponse = new Gson().fromJson(response, FriendsResponse.class);
                            if (!isPagination){
                                friendList.clear();
                            }else {
                                isPagination = false;
                            }
                            friendList.addAll(friendsResponse.getResult());
                            friendsAdapter.notifyDataSetChanged();
                            isMoreData = friendsResponse.getNext() != -1;
                            if (isMoreData) count = friendsResponse.getNext();
                            if (friendList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;

                        case Constants.NetworkConstant.NO_DATA:
                            friendList.clear();
                            friendsAdapter.notifyDataSetChanged();
                            if (friendList.size() == 0) {
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
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (friendList.size() == 0) {
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
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (friendList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
        }
    }
}
