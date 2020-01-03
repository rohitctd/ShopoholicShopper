package com.shopoholic.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.RatingAndReviewAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.ratingreviewresponse.RatingReviewResponse;
import com.shopoholic.models.ratingreviewresponse.Result;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class RatingAndReviewActivity extends AppCompatActivity implements NetworkListener {


    @BindView(R.id.iv_menu)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
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
    private RatingAndReviewAdapter ratingAndReviewAdapter;
    private ArrayList<Result> ratingList;
    private int count = 0;
    private boolean isMoreData;
    private boolean isLoading;
    private boolean isPagination;
    private LinearLayoutManager linearLayoutManager;
    private String buddyId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_and_review);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.rating_and_review);
        initVariable();
        setAdapter();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(this))
            hitRatingAndReviewApi();

    }

    @OnClick(R.id.iv_menu)
    public void onViewClicked() {
        onBackPressed();

    }

    /**
     * method to initialize the variables
     */
    private void initVariable() {
        swipeRefreshLayout.setEnabled(false);
        ivBack.setVisibility(View.VISIBLE);
        gifProgress.setImageResource(R.drawable.shopholic_loader);

        if (getIntent() != null && getIntent().getExtras() != null) {
            buddyId = getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_ID, "");
        }

        ratingList = new ArrayList<>();
        ratingAndReviewAdapter = new RatingAndReviewAdapter(this, ratingList, (position, view) -> {

        });
    }

    /**
     * method to set adapter
     */
    private void setAdapter() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(ratingAndReviewAdapter);
    }

    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(RatingAndReviewActivity.this)) {
                count = 0;
                hitRatingAndReviewApi();
            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= ratingList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(RatingAndReviewActivity.this))
                            isPagination = true;
                        hitRatingAndReviewApi();
                    }
                }
            }

        });

    }

    /**
     * method to hit rating review api
     */
    private void hitRatingAndReviewApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, buddyId);
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitGetRatingAndReviewApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_RATING_REVIEW);
    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_RATING_REVIEW:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        RatingReviewResponse ratingReviewResponse = new Gson().fromJson(response, RatingReviewResponse.class);
                        if (isPagination) {
                            isPagination = false;
                        } else {
                            ratingList.clear();
                        }
                        ratingList.addAll(ratingReviewResponse.getResult());
                        ratingAndReviewAdapter.notifyDataSetChanged();
                        isMoreData = ratingReviewResponse.getNext() != -1;
                        if (isMoreData) count = ratingReviewResponse.getNext();
                        if (ratingList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;
                    case Constants.NetworkConstant.NO_DATA:
                        ratingList.clear();
                        ratingAndReviewAdapter.notifyDataSetChanged();
                        if (ratingList.size() > 0) {
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
        progressBar.setVisibility(View.GONE);
        ratingAndReviewAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (ratingList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        ratingAndReviewAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (ratingList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }
}