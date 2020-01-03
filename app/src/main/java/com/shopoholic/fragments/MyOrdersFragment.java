package com.shopoholic.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.activities.OrderDetailActivity;
import com.shopoholic.adapters.OrdersAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForRatingAndReview;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.interfaces.RatingCallback;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.NotificationBean;
import com.shopoholic.models.OrderBean;
import com.shopoholic.models.orderlistdetailsresponse.OrderListDetailsRespose;
import com.shopoholic.models.orderlistdetailsresponse.Result;
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

public class MyOrdersFragment extends Fragment implements NetworkListener {
    @BindView(R.id.recycle_view)
    RecyclerView rvOrders;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private AppCompatActivity mActivity;
    Unbinder unbinder;
    private View rootView;
    private OrdersAdapter ordersAdapter;
    private List<Result> ordersList;
    private boolean isMoreData;
    private boolean isLoading;
    private LinearLayoutManager linearLayoutManager;
    private int count = 0;
    public String status= "";
    private String startDate = "";
    private String endDate = "";
    private boolean isPagination;
    private CustomDialogForRatingAndReview customDialogForRatingAndReview;
    private String tollFreeNumber = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_recycle_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mActivity = (AppCompatActivity) getActivity();
        initVariable();
        setAdapters();
        setListeners();
        getNotificationData();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            count = 0;
            progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            hitOrdersListApi();
        }

        return rootView;
    }



    /**
     * filter broadcast
     */
    private OrderBean orderFilterData;
    private BroadcastReceiver orderFilterReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mActivity instanceof HomeActivity && isAdded()){
                orderFilterData = ((HomeActivity)mActivity).getMyOrderFilterData();
                if (orderFilterData != null) {
                    status = orderFilterData.getStatus();
                    startDate = orderFilterData.getStartDate();
                    endDate = orderFilterData.getEndDate();
                    if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                       count = 0;
                       progressBar.setVisibility(View.VISIBLE);
                       hitOrdersListApi();
                    }
                }
            }
        }
    };

    private BroadcastReceiver orderUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null){
            NotificationBean notificationBean = (NotificationBean) intent.getExtras().getSerializable(Constants.IntentConstant.NOTIFICATION);
                if (notificationBean != null) {
                    for (int i = 0; i < ordersList.size(); i++) {
                        if (ordersList.get(i).getId().equals(notificationBean.getOrderId())) {
                            ordersList.get(i).setOrderStatus(notificationBean.getOrderStatus());
                            ordersAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }
        }
    };



    /**
     * Method to get data on notification click
     */
    private void getNotificationData() {
        if (mActivity instanceof HomeActivity) {
            Intent receivedIntent = ((HomeActivity) mActivity).getIntent();
            if (receivedIntent != null && receivedIntent.getExtras() != null && receivedIntent.hasExtra(FirebaseConstants.NOTIFICATION)) {
                final NotificationBean notificationBean = (NotificationBean) receivedIntent.getExtras().getSerializable(FirebaseConstants.NOTIFICATION);
                if (notificationBean != null) {
                    String action = notificationBean.getOrderStatus();
                    if (!action.equals("6") && !action.equals("7")) {
                        MyOrdersFragment.this.startActivityForResult(new Intent(mActivity, OrderDetailActivity.class)
                                        .putExtra(Constants.IntentConstant.ORDER_ID, notificationBean.getOrderId())
                                , Constants.IntentConstant.REQUEST_ORDER);
                    }
                }
                ((HomeActivity) mActivity).setIntent(null);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(orderFilterReceiver, new IntentFilter(Constants.IntentConstant.ORDER_DETAILS));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(orderUpdateReceiver, new IntentFilter(Constants.IntentConstant.NOTIFICATION));
    }

    @Override
    public void onStop() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
//        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(orderFilterReceiver);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(orderUpdateReceiver);
        super.onStop();
    }

    /**
     * method to initialize the variables
     */
    private void initVariable() {
        ordersList = new ArrayList<>();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        ordersAdapter = new OrdersAdapter(mActivity, ordersList, new RecyclerCallBack() {
            @Override
            public void onClick(final int position, View view) {
                switch (view.getId()) {
                    case R.id.ll_root_view:
//                        LatLng latLng = new LatLng(Double.parseDouble(ordersList.get(position).getLatitude()), Double.parseDouble(ordersList.get(position).getLongitude()));
//                        LatLng latLngMerchant = new LatLng(0.0, 0.0);
                        ordersList.get(position).setTollFreeNumber(tollFreeNumber);
                        if (!ordersList.get(position).getOrderStatus().equals("6") &&
                                 !ordersList.get(position).getOrderStatus().equals("7")) {
                            MyOrdersFragment.this.startActivityForResult(new Intent(mActivity, OrderDetailActivity.class)
                                    .putExtra(Constants.IntentConstant.ORDER_DETAILS, ordersList.get(position))
                                    , Constants.IntentConstant.REQUEST_ORDER);
                        }
                        break;
                    case R.id.tv_provided_rating:
                        final int type = ordersList.get(position).getMerchantId().equals(ordersList.get(position).getBuddyId()) ? 2 : 1;
                        customDialogForRatingAndReview = new CustomDialogForRatingAndReview(mActivity, type, new RatingCallback() {
                            @Override
                            public void ratingAndReview(int buddyRating, int merchantRating, String review) {
                                hitRatingAndReviewApi(type, position, buddyRating, merchantRating, review);
                            }
                        });
                        customDialogForRatingAndReview.show();
                        break;
                }
            }
        });
    }


    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)){
                count = 0;
                hitOrdersListApi();
            }
        });
        rvOrders.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= ordersList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            isPagination = true;
                            hitOrdersListApi();
                        }
                    }
                }
            }

        });

    }


    /**
     * method to set adapter on view
     */
    private void setAdapters() {
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rvOrders.setLayoutManager(linearLayoutManager);
        rvOrders.setAdapter(ordersAdapter);
    }

    /**
     * method to hit order list api
     */
    private void hitOrdersListApi() {
        isLoading = true;
        layoutNoDataFound.setVisibility(View.GONE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        params.put(Constants.NetworkConstant.PARAM_STATUS, status);
        if (startDate!= null && !startDate.equals(""))
        params.put(Constants.NetworkConstant.PARAM_START_DATE, AppUtils.getInstance().formatDate(startDate, Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
        if (endDate!= null && !endDate.equals(""))
            params.put(Constants.NetworkConstant.PARAM_END_DATE, AppUtils.getInstance().formatDate(endDate, Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
        Call<ResponseBody> call = apiInterface.hitOrdersListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_ORDER);
    }

    /**
     * method to rate merchant and buddy
     * @param type
     * @param position
     * @param buddyRating
     * @param merchantRating
     * @param review
     */
    private void hitRatingAndReviewApi(int type, final int position, int buddyRating, int merchantRating, String review) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, ordersList.get(position).getBuddyId());
        params.put(Constants.NetworkConstant.PARAM_BUDDY_RATE, String.valueOf(buddyRating));
        if (type == 1) {
            params.put(Constants.NetworkConstant.PARAM_MERCHANT_ID, ordersList.get(position).getMerchantId());
            params.put(Constants.NetworkConstant.PARAM_MERCHANT_RATE, String.valueOf(merchantRating));
        }
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, ordersList.get(position).getDealId());
        params.put(Constants.NetworkConstant.PARAM_ORDER_ID, ordersList.get(position).getId());
        params.put(Constants.NetworkConstant.PARAM_REVIEW, review);
        Call<ResponseBody> call = apiInterface.hitRatingAndReviewApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                if (isAdded()) {
                    if (customDialogForRatingAndReview != null && customDialogForRatingAndReview.isShowing()) {
                        customDialogForRatingAndReview.stopLoader(true);
                    }
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            ordersList.get(position).setIsRated("1");
                            ordersAdapter.notifyDataSetChanged();
                            break;
                        default:
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                }
            }

            @Override
            public void onError(String response, int requestCode) {
                if (customDialogForRatingAndReview != null && customDialogForRatingAndReview.isShowing()) {
                    customDialogForRatingAndReview.stopLoader(false);
                }
            }

            @Override
            public void onFailure() {
                if (customDialogForRatingAndReview != null && customDialogForRatingAndReview.isShowing()) {
                    customDialogForRatingAndReview.stopLoader(false);
                }
            }
        }, 1);
    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_ORDER:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            OrderListDetailsRespose ordersResponse = new Gson().fromJson(response, OrderListDetailsRespose.class);
                            if (!isPagination){
                                ordersList.clear();
                            }else {
                                isPagination = false;
                            }
                            tollFreeNumber = ordersResponse.getTollFreeNumber();
                            ordersList.addAll(ordersResponse.getResult());
                            ordersAdapter.notifyDataSetChanged();
                            isMoreData = ordersResponse.getNext() != -1;
                            if (isMoreData) count = ordersResponse.getNext();
                            if (ordersList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.NetworkConstant.NO_DATA:
                            ordersList.clear();
                            ordersAdapter.notifyDataSetChanged();
                            if (ordersList.size() == 0) {
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
            ordersAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (ordersList.size() == 0) {
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
            ordersAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (ordersList.size() == 0) {
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
        if (requestCode == Constants.IntentConstant.REQUEST_ORDER && resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            String orderId = data.getExtras().getString(Constants.IntentConstant.ORDER_ID, "");
            String orderStatus = data.getExtras().getString(Constants.IntentConstant.ORDER_STATUS, "");
            if (!orderStatus.equals("")) {
                for (int i = 0; i < ordersList.size(); i++) {
                    if (ordersList.get(i).getId().equals(orderId)) {
//                        if (orderStatus.equals("6")) {
//                            ordersList.remove(i);
//                            ordersAdapter.notifyItemRemoved(i);
//                            ordersAdapter.notifyItemRangeChanged(i, ordersList.size());
//                        }else {
                            ordersList.get(i).setOrderStatus(orderStatus);
                            ordersAdapter.notifyItemChanged(i);
//                        }
                        break;
                    }

                }
            }
        }
    }
}
