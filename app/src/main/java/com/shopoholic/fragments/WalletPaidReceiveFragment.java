package com.shopoholic.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.shopoholic.adapters.WalletStatusAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.walletresponse.Result_;
import com.shopoholic.models.walletresponse.WalletResponse;
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
 * Class created by Sachin on 17-May-18.
 */
public class WalletPaidReceiveFragment extends Fragment implements NetworkListener {

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
    private Unbinder unbinder;
    private boolean isLoading;
    private boolean isPagination;
    private List<Result_> walletStatusList;
    private AppCompatActivity mActivity;
    private int count;
    private boolean isMoreData;
    private LinearLayoutManager linearLayoutManager;
    private WalletStatusAdapter walletStatusAdapter;
    public boolean isPaidFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_recycle_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariable();
        setAdapters();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            count = 0;
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            hitWalletDetailsApi();
        }
        return rootView;
    }

    /**
     * method to initialize the variables
     */


    private void initVariable() {
        if (getArguments() != null && getArguments().containsKey(Constants.IntentConstant.IS_PAID)){
            isPaidFragment = getArguments().getBoolean(Constants.IntentConstant.IS_PAID, false);
        }
        mActivity = (AppCompatActivity) getActivity();
        walletStatusList = new ArrayList<>();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        walletStatusAdapter = new WalletStatusAdapter(mActivity, this,  walletStatusList, (position, view) -> {

        });
    }


    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                count = 0;
                hitWalletDetailsApi();
            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= walletStatusList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            isPagination = true;
                            hitWalletDetailsApi();
                        }
                    }
                }
            }

        });

    }

    private void setAdapters() {
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(walletStatusAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * method to get buddy list
     */
    private void hitWalletDetailsApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        params.put(Constants.NetworkConstant.TYPE, isPaidFragment ? "1" : "2");
        Call<ResponseBody> call = apiInterface.hitWalletDetailsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_BUDDY);
    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_BUDDY:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            WalletResponse walletResponse = new Gson().fromJson(response, WalletResponse.class);
                            if (!isPagination){
                                walletStatusList.clear();
                            }else {
                                isPagination = false;
                            }
//                            walletStatusList.addAll(walletResponse.getWalletDetail().getWalletDetail());
                            walletStatusList.addAll(walletResponse.getResult());
                            walletStatusAdapter.notifyDataSetChanged();
                            isMoreData = walletResponse.getNext() != -1;
                            if (isMoreData) count = walletResponse.getNext();
                            if (walletStatusList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }

                            if (isPaidFragment) {
                                Fragment fragment = getParentFragment();
                                if (fragment instanceof MyWalletFragment) {
                                    ((MyWalletFragment) fragment).setTotalBalance(walletResponse.getTotalBalance());
                                }
                            }
                            break;
                        case Constants.NetworkConstant.NO_DATA:
                            walletStatusList.clear();
                            walletStatusAdapter.notifyDataSetChanged();
                            if (walletStatusList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;
                        default:
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                                if (walletStatusList.size() == 0) {
                                    layoutNoDataFound.setVisibility(View.VISIBLE);
                                } else {
                                    layoutNoDataFound.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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
            walletStatusAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (walletStatusList.size() == 0) {
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
            walletStatusAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (walletStatusList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }
        }
    }


}
