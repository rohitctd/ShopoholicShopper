package com.shopoholic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.activities.SingleChatActivity;
import com.shopoholic.firebasechat.interfaces.FirebaseUserListener;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.models.buddydetailsresponse.BuddyDetailsResponse;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class BuddyProfileActivity extends AppCompatActivity implements NetworkListener {
    private static final String NOT_REQUESTED = "0", REQUESTED = "1", ACCEPTED = "2", CANCELED = "3";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_buddy_chat)
    ImageView ivBuddyChat;
    @BindView(R.id.iv_buddy_image)
    CircleImageView ivBuddyImage;
    @BindView(R.id.iv_buddy_follow)
    ImageView ivBuddyFollow;
    @BindView(R.id.tv_buddy_name)
    CustomTextView tvBuddyName;
    @BindView(R.id.tv_buddy_location)
    CustomTextView tvBuddyLocation;
    @BindView(R.id.tv_buddy_category)
    CustomTextView tvBuddyCategory;
    @BindView(R.id.rb_buddy_rating)
    com.whinc.widget.ratingbar.RatingBar rbBuddyRating;
    @BindView(R.id.tv_buddy_review)
    CustomTextView tvBuddyReview;
    @BindView(R.id.tv_buddy_deal_posted)
    CustomTextView tvBuddyDealPosted;
    @BindView(R.id.tv_buddy_deal_shared)
    CustomTextView tvBuddyDealShared;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.ll_root_view)
    LinearLayout llRootView;
    private String buddyId;
    private String isFollow;
    private com.shopoholic.models.buddylistresponse.Result buddyDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_profile);
        ButterKnife.bind(this);
        getDataFromIntent();
    }

    /**
     * method to get data from intent
     */
    private void getDataFromIntent() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        if (getIntent().getExtras() != null) {
            buddyDetails = (com.shopoholic.models.buddylistresponse.Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.BUDDY);
        }
        progressBar.setVisibility(View.GONE);
        setBuddyDetails(buddyDetails);
        buddyId = buddyDetails.getUserId();
        //hit category
        /*if (AppUtils.getInstance().isInternetAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            hitBuddyDetailsListApi();
        }*/
    }

    /**
     * method to hit buddy details api
     */
    private void hitBuddyDetailsListApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, buddyId);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitBuddyDetailsListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(BuddyProfileActivity.this, call, this, Constants.NetworkConstant.REQUEST_BUDDY_DETAILS);
    }

    /**
     * method to hit follow buddy api
     * @param status
     */
    private void hitBuddyFollowApi(String status) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_FOLLOW_ID, buddyId);
        params.put(Constants.NetworkConstant.PARAM_STATUS, status);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitBuddyFollowApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(BuddyProfileActivity.this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                switch (requestCode) {
                    case Constants.NetworkConstant.BUDDY_REQUEST:
                        switch (responseCode) {
                            case Constants.NetworkConstant.SUCCESS_CODE:
                                try {
                                    if (isFollow.equals(NOT_REQUESTED) || isFollow.equals(CANCELED)) {
                                        isFollow = REQUESTED;
                                    } else {
                                        isFollow = CANCELED;
                                    }
                                    AppUtils.getInstance().showToast(BuddyProfileActivity.this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                        break;
                }

            }

            @Override
            public void onError(String response, int requestCode) {
                if (isFollow.equals(NOT_REQUESTED) || isFollow.equals(CANCELED)) {
                    ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_follow);
                    buddyDetails.setIsFollow("0");
                } else {
                    ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_following);
                    buddyDetails.setIsFollow("1");
                }
            }

            @Override
            public void onFailure() {
                if (isFollow.equals(NOT_REQUESTED) || isFollow.equals(CANCELED)) {
                    ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_follow);
                    buddyDetails.setIsFollow("0");
                } else {
                    ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_following);
                    buddyDetails.setIsFollow("1");
                }
            }
        }, Constants.NetworkConstant.BUDDY_REQUEST);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {

        progressBar.setVisibility(View.GONE);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_BUDDY_DETAILS:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        llRootView.setVisibility(View.VISIBLE);
                        BuddyDetailsResponse buddyDetailsResponse = new Gson().fromJson(response, BuddyDetailsResponse.class);
//                        setBuddyDetails(buddyDetailsResponse.getWalletDetail());
                        break;
                }
                break;
        }
    }

    /**
     * method to set the buddy details
     * @param result
     */
    private void setBuddyDetails(com.shopoholic.models.buddylistresponse.Result result) {

        if (result.getImage() != null) {
            AppUtils.getInstance().setCircularImages(this, result.getImage(), ivBuddyImage, R.drawable.ic_side_menu_user_placeholder);
        }

        tvBuddyName.setText(TextUtils.concat(result.getFirstName() + " " + result.getLastName()));
        String address = result.getAddress() + " " + result.getAddress2();
        if (!address.trim().equals("")) {
            tvBuddyLocation.setText(address);
        } else {
            tvBuddyLocation.setVisibility(View.GONE);
        }
        tvBuddyCategory.setText(result.getUserCategories().replace(",", " | "));
        rbBuddyRating.setCount((int)(Float.parseFloat(result.getRating()) + 0.5));
        tvBuddyReview.setText(TextUtils.concat(result.getReviews() + " " + getString(R.string.reviews)));
        isFollow = result.getIsFollow() == null ? "0" : result.getIsFollow();
        if (isFollow.equals(NOT_REQUESTED) || isFollow.equals(CANCELED)) {
            ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_follow);
            buddyDetails.setIsFollow("0");
        } else {
            ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_following);
            buddyDetails.setIsFollow("1");
        }

    }

    @Override
    public void onError(String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        progressBar.setVisibility(View.GONE);
    }

    @OnClick({R.id.iv_back, R.id.iv_buddy_chat, R.id.iv_buddy_follow, R.id.tv_buddy_deal_posted, R.id.tv_buddy_deal_shared, R.id.ll_rate_review})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_buddy_chat:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    FirebaseDatabaseQueries.getInstance().getUser(buddyId, new FirebaseUserListener() {
                        @Override
                        public void getUser(UserBean user) {
                            Intent intent = new Intent(BuddyProfileActivity.this, SingleChatActivity.class);
                            intent.putExtra(FirebaseConstants.OTHER_USER, user);
                            startActivity(intent);
                        }

                        @Override
                        public void updateUser(UserBean user) {
                        }

                    });
//                    AppUtils.getInstance().showToast(this, getString(R.string.under_development));
                }
                break;
            case R.id.iv_buddy_follow:
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    if (isFollow.equals(NOT_REQUESTED) || isFollow.equals(CANCELED)) {
                        ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_following);
                        buddyDetails.setIsFollow("1");
                        hitBuddyFollowApi(REQUESTED);
                    } else {
                        ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_follow);
                        buddyDetails.setIsFollow("0");
                        hitBuddyFollowApi(CANCELED);
                    }
                }
                break;
            case R.id.tv_buddy_deal_posted:
                Intent dealPostedIntent = new Intent(BuddyProfileActivity.this, BuddySharePostDealActivity.class);
                dealPostedIntent.putExtra(Constants.IntentConstant.BUDDY_DEAL, true);
                dealPostedIntent.putExtra(Constants.IntentConstant.BUDDY_ID, buddyId);
                startActivity(dealPostedIntent);
                break;
            case R.id.tv_buddy_deal_shared:
                Intent dealSharedIntent = new Intent(BuddyProfileActivity.this, BuddySharePostDealActivity.class);
                dealSharedIntent.putExtra(Constants.IntentConstant.BUDDY_DEAL, false);
                dealSharedIntent.putExtra(Constants.IntentConstant.BUDDY_ID, buddyId);
                startActivity(dealSharedIntent);
                break;
            case R.id.ll_rate_review:
                startActivity(new Intent(BuddyProfileActivity.this, RatingAndReviewActivity.class)
                        .putExtra(Constants.IntentConstant.BUDDY_ID, buddyId));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.IntentConstant.BUDDY, buddyDetails);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
