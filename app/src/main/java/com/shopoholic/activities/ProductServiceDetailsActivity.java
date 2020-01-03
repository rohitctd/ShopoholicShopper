package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.ProductImagesAdapter;
import com.shopoholic.adapters.SlotsAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForMakeOffer;
import com.shopoholic.firebasechat.activities.FullScreenImageSliderActivity;
import com.shopoholic.firebasechat.activities.SingleChatActivity;
import com.shopoholic.firebasechat.interfaces.FirebaseUserListener;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.interfaces.MakeOfferDialogCallback;
import com.shopoholic.interfaces.PopupItemDialogCallback;
import com.shopoholic.livetracking.LocationUpdatesTask;
import com.shopoholic.models.productservicedetailsresponse.BuddyArr;
import com.shopoholic.models.productservicedetailsresponse.ProductServiceDetailsResponse;
import com.shopoholic.models.productservicedetailsresponse.Result;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.CALL_PHONE;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

/**
 * Class created by Sachin on 09-Apr-18.
 */

public class ProductServiceDetailsActivity extends AppCompatActivity implements NetworkListener {

    private static final String NOT_REQUESTED = "0", REQUESTED = "1", ACCEPTED = "2", CANCELED = "3";
    @BindView(R.id.iv_product_pic)
    ImageView ivProductPic;
    @BindView(R.id.iv_play_video)
    ImageView ivPlayVideo;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.tv_product_images)
    CustomTextView tvProductImages;
    @BindView(R.id.rv_product_images)
    RecyclerView rvProductImages;
    @BindView(R.id.images_view)
    View imagesView;
    @BindView(R.id.civ_product_user_image)
    CircleImageView civProductUserImage;
    @BindView(R.id.fl_profile)
    FrameLayout flProfile;
    @BindView(R.id.tv_product_user_name)
    CustomTextView tvProductUserName;
    @BindView(R.id.tv_product_user_type)
    CustomTextView tvProductUserType;
    @BindView(R.id.tv_product_type)
    CustomTextView tvProductType;
    @BindView(R.id.tv_product_name)
    CustomTextView tvProductName;
    @BindView(R.id.tv_product_description)
    CustomTextView tvProductDescription;
    @BindView(R.id.tv_final_price)
    CustomTextView tvFinalPrice;
    @BindView(R.id.tv_original_price)
    CustomTextView tvOriginalPrice;
    @BindView(R.id.tv_flat_discount)
    CustomTextView tvFlatDiscount;
    @BindView(R.id.tv_product_details)
    CustomTextView tvProductDetails;
    @BindView(R.id.label_attributes)
    CustomTextView labelAttributes;
    @BindView(R.id.tv_attributes)
    CustomTextView tvAttributes;
    @BindView(R.id.view_attributes)
    View viewAttributes;
    @BindView(R.id.tv_home_delivery_available)
    CustomTextView tvHomeDeliveryAvailable;
    @BindView(R.id.tv_search_buddy)
    CustomTextView tvSearchBuddy;
    @BindView(R.id.tv_product_availability)
    CustomTextView tvProductAvailability;
    @BindView(R.id.ll_product_availability)
    LinearLayout llProductAvailability;
    @BindView(R.id.tv_deal_posted)
    CustomTextView tvDealPosted;
    @BindView(R.id.tv_address)
    CustomTextView tvAddress;
    @BindView(R.id.tv_read_more)
    CustomTextView tvReadMore;
    @BindView(R.id.tv_label_deal_expired)
    CustomTextView tvLabelDealExpired;
    @BindView(R.id.tv_deal_expiry)
    CustomTextView tvDealExpiry;
    @BindView(R.id.tv_slot_availability)
    CustomTextView tvSlotAvailability;
    @BindView(R.id.rv_time_slots)
    RecyclerView rvTimeSlots;
    @BindView(R.id.view_time_slots)
    View viewTimeSlots;
    @BindView(R.id.tv_contact)
    CustomTextView tvContact;
    @BindView(R.id.tv_buy_now)
    CustomTextView tvBuyNow;
    @BindView(R.id.root_view)
    LinearLayout rootView;
    @BindView(R.id.layout_retry)
    LinearLayout layoutRetry;
    @BindView(R.id.rl_price)
    RelativeLayout rlPrice;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.label_posted)
    CustomTextView labelPosted;
    @BindView(R.id.view_posted)
    View viewPosted;
    @BindView(R.id.iv_buddy_follow)
    ImageView ivBuddyFollow;
    @BindView(R.id.tv_reshare_buddy_count)
    TextView tvReshareBuddyCount;
    @BindView(R.id.tv_helpline_no)
    TextView tvHelplineNo;
    @BindView(R.id.ll_helpline)
    LinearLayout llHelpline;
    @BindView(R.id.btn_call)
    TextView btnCall;
    @BindView(R.id.iv_coming_soon_kyc)
    ImageView imgKyc;
    private String dealId = "";
    private ArrayList<String> productImagesList;
    private ProductImagesAdapter productImagesAdapter;
    private Result productDetails;
    private ArrayList<ServiceSlot> slotsArray;
    private ArrayList<ServiceSlot> selectedSlotsArray;
    private SlotsAdapter slotsAdapter;
    private boolean isSlotAvailable;
    private boolean isShared;
    private String buddyId;
    private String dealImage = "";
    private ArrayList<String> slotsDayList;
    private String isFollow;
    private boolean readMore = false;
    public String videoUrl;
    public BitmapDrawable videoDrawable;
    private boolean isClicked = false;
    private String kycStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_services);
        ButterKnife.bind(this);
        initVariables();
        setAdapters();
        if (getIntent() != null && getIntent().getExtras() != null) {
            dealId = getIntent().getExtras().getString(Constants.IntentConstant.DEAL_ID, "");
            isShared = getIntent().getExtras().getBoolean(Constants.IntentConstant.IS_SHARED, false);
            buddyId = getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_ID, "");
            dealImage = getIntent().getExtras().getString(Constants.IntentConstant.DEAL_IMAGE, "");
            kycStatus = getIntent().getExtras().getString(Constants.IntentConstant.KYC_STATUS, "");
            AppUtils.getInstance().setImages(this, dealImage, ivProductPic, 0, R.drawable.ic_placeholder);


        }
        if (AppUtils.getInstance().isInternetAvailable(this)) hitProductServiceDetailsApi();

        if (kycStatus.equals("0")){
            tvBuyNow.setEnabled(false);
            imgKyc.setVisibility(View.VISIBLE);
        }else{
            tvBuyNow.setEnabled(true);
            imgKyc.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (productDetails != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        layoutToolbar.setBackgroundResource(R.drawable.gradient_transluscent_overlay);
        productImagesList = new ArrayList<>();
        slotsArray = new ArrayList<>();
        slotsDayList = new ArrayList<>();
        selectedSlotsArray = new ArrayList<>();
        productImagesAdapter = new ProductImagesAdapter(this, productImagesList, (position, view) -> {
            if (productImagesList.get(position).equals(videoUrl)) {
                ivProductPic.setImageDrawable(videoDrawable);
                ivPlayVideo.setVisibility(View.VISIBLE);
            } else {
                AppUtils.getInstance().setImages(ProductServiceDetailsActivity.this, productImagesList.get(position), ivProductPic, 0, R.drawable.ic_placeholder);
                ivPlayVideo.setVisibility(View.GONE);
            }
        });
        slotsAdapter = new SlotsAdapter(this, selectedSlotsArray, (position, view) -> {
            switch (view.getId()) {
                case R.id.fl_root_view:
//                        slotsArray.get(position).setSelected(!slotsArray.get(position).isSelected());
//                        slotsAdapter.notifyItemChanged(position);
                    break;
            }
        });
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setImageResource(R.drawable.ic_home_shopper_more_ic);
        menuThirdRight.setImageResource(R.drawable.ic_home_shopper_share_ic);
        menuSecondRight.setImageResource(R.drawable.ic_shopper_home_like_unfill);
        menuRight.setVisibility(View.VISIBLE);
        menuSecondRight.setVisibility(View.VISIBLE);
        menuThirdRight.setVisibility(View.VISIBLE);

    }

    /**
     * method to set the adapters in views
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setAdapters() {
        rvTimeSlots.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTimeSlots.setAdapter(slotsAdapter);

        rvProductImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvProductImages.setAdapter(productImagesAdapter);

        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float offset = Math.abs(verticalOffset);
            float totalScrollRange = appBarLayout.getTotalScrollRange();
            layoutToolbar.setBackgroundResource(offset == totalScrollRange ? R.drawable.toolbar_gradient : R.drawable.gradient_transluscent_overlay);
        });
    }


    /**
     * method to show the images
     */
    public void showImages() {
        if (productImagesList != null) {
            ArrayList<String> images = new ArrayList<String>(productImagesList);
            images.remove(videoUrl);
            if (images.size() > 0) {
                Intent intent = new Intent(this, FullScreenImageSliderActivity.class);
                intent.putExtra("imagelist", images);
                intent.putExtra("from", productDetails.getName());
                if (productImagesAdapter != null)
                    intent.putExtra("pos", productImagesAdapter.selectedPosition);
                startActivityForResult(intent, 10001);
            }
        }
    }

    @OnClick({R.id.menu_right, R.id.menu_second_right, R.id.menu_third_right, R.id.iv_menu, R.id.tv_search_buddy, R.id.tv_contact,
            R.id.tv_buy_now, R.id.iv_play_video, R.id.iv_product_pic, R.id.tv_slot_availability, R.id.iv_buddy_follow, R.id.tv_read_more,
            R.id.tv_reshare_buddy_count, R.id.btn_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search_buddy:
                if (productDetails != null) {
                    if (AppUtils.getInstance().isLoggedIn(this)) {
                        startActivity(new Intent(this, CommonActivity.class)
                                .putExtra(Constants.IntentConstant.FRAGMENT_TYPE, 0)
                                .putExtra(Constants.IntentConstant.DEAL_ID, productDetails.getId()));
                    }
                }
                break;
            case R.id.tv_read_more:
                if (!readMore) {
                    readMore = true;
                    tvProductDescription.setMaxLines(100);
                    tvReadMore.setText(getString(R.string.read_less));
                }else {
                    readMore = false;
                    tvProductDescription.setMaxLines(2);
                    tvReadMore.setText(getString(R.string.read_more));
                }
                break;
            case R.id.menu_second_right:
                if (productDetails != null) {
                    if (AppUtils.getInstance().isLoggedIn(this) && AppUtils.getInstance().isInternetAvailable(this)) {
                        if (productDetails.getIsFavourite().equals("1")) {
                            productDetails.setIsFavourite("2");
                            menuSecondRight.setImageResource(R.drawable.ic_shopper_home_like_unfill);
                        } else {
                            productDetails.setIsFavourite("1");
                            menuSecondRight.setImageResource(R.drawable.ic_home_shoppers_like_fill);
                        }
                        hitLikeProductsApi();
                    }
                }
                break;
            case R.id.menu_right:
                if (productDetails != null) {
                    AppUtils.getInstance().showMorePopUp(this, menuRight, getString(R.string.block), getString(R.string.report),
                            "", 1, new PopupItemDialogCallback() {
                                @Override
                                public void onItemOneClick() {
                                    if (AppUtils.getInstance().isLoggedIn(ProductServiceDetailsActivity.this)) {
                                        showBlockDialog();
                                    }
                                }

                                @Override
                                public void onItemTwoClick() {
                                    if (AppUtils.getInstance().isLoggedIn(ProductServiceDetailsActivity.this)) {
                                        CustomDialogForMakeOffer customDialogForSelectDate = new CustomDialogForMakeOffer(ProductServiceDetailsActivity.this, 3, "", new MakeOfferDialogCallback() {
                                            @Override
                                            public void onSelect(String message, String currency, String text, int type) {
                                                if (AppUtils.getInstance().isInternetAvailable(ProductServiceDetailsActivity.this)) {
                                                    hitReportProductApi(text);
                                                }
                                            }
                                        });
                                        customDialogForSelectDate.show();
                                    }
                                }

                                @Override
                                public void onItemThreeClick() {
                                }
                            });
                }
                break;
            case R.id.menu_third_right:
                if (productDetails != null) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, productDetails.getName());
                    /*if (productImagesList.size() > 0) {
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(productImagesList.get(0)));
                    }*/
                    String dealUrl = productDetails.getDealUrl();
                    String user = "&user_type=" + AppUtils.getInstance().encryptString("3");
                    dealUrl += user;
                    /*if (!productDetails.getDealUrl().contains("http://tinyurl.com")) {
                        String url = productDetails.getDealUrl();
                        String[] params = productDetails.getDealUrl().split("Dealdetailinfo?");
                        if (params.length > 1) {
                            String queryParams = AppUtils.getInstance().decryptString(params[1]);
                            queryParams += "&user_type=3";
                            String encryptParam = AppUtils.getInstance().encryptString(queryParams);
                            dealUrl = params[0] + "Dealdetailinfo?" + encryptParam;
                        }
                    }*/
                    sendIntent.putExtra(Intent.EXTRA_TEXT, dealUrl);
                    sendIntent.setType("text/uri");
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_deal)));
                }
                progressBar.setVisibility(View.GONE);
                break;
            case R.id.iv_buddy_follow:
                if (AppUtils.getInstance().isLoggedIn(ProductServiceDetailsActivity.this)) {
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        if (isFollow.equals(NOT_REQUESTED) || isFollow.equals(CANCELED)) {
                            ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_following);
                            productDetails.setIsFollow("1");
                            hitBuddyFollowApi(REQUESTED, productDetails.getUserId());
                        } else {
                            ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_follow);
                            productDetails.setIsFollow("0");
                            hitBuddyFollowApi(CANCELED, productDetails.getUserId());
                        }
                    }
                }
                break;
            case R.id.tv_contact:
                if (AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")) {
                    if (productDetails.getProductType().equals("1") && Calendar.getInstance().getTime()
                            .before(AppUtils.getInstance().getDateFromString(productDetails.getDealStartTime(), SERVICE_DATE_FORMAT))) {
                        AppUtils.getInstance().showToast(this, getString(R.string.deal_not_start_yet));
                    } else {
                        //todo ... if shared by buddy
                        productDetails.setIsShared(isShared ? "1" : "");
                        productDetails.setUserId(isShared ? buddyId : productDetails.getUserId());
                        checkForProduct(2);
                    }
                } else {
                    if (AppUtils.getInstance().isLoggedIn(this)) {
                        if (!AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.EMAIL).equals("")) {
                            hitEmailValidateApi(2);
                        } else {
                            AppUtils.getInstance().showToast(this, getString(R.string.please_provide_email_address));
                        }
                    }
                }
                break;
            case R.id.tv_buy_now:
                if (AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")) {
                    if (productDetails.getProductType().equals("1") && Calendar.getInstance().getTime()
                            .before(AppUtils.getInstance().getDateFromString(productDetails.getDealStartTime(), SERVICE_DATE_FORMAT))) {
                        AppUtils.getInstance().showToast(this, getString(R.string.deal_not_start_yet));
                    } else {
                        // todo......  if shared by buddy
                        productDetails.setIsShared(isShared ? "1" : "");
                        productDetails.setUserId(isShared ? buddyId : productDetails.getUserId());
                        checkForProduct(1);
                    }
                } else {
                    if (AppUtils.getInstance().isLoggedIn(this)) {
                        if (!AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.EMAIL).equals("")) {
                            hitEmailValidateApi(1);
                        } else {
                            AppUtils.getInstance().showToast(this, getString(R.string.please_provide_email_address));
                        }
                    }
                }
                break;
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.iv_product_pic:
                if (productImagesList != null && productImagesList.size() > 0 && productImagesList.size() > productImagesAdapter.selectedPosition) {
                    if (!productImagesList.get(productImagesAdapter.selectedPosition).equals(videoUrl)) {
                        showImages();
                    }
                }
                break;
            case R.id.tv_slot_availability:
                if (!isClicked) {
                    setClicked(3000);
                    Intent intent = new Intent(this, TimeSlotsActivity.class);
                    intent.putExtra(Constants.IntentConstant.DAYS_LIST, productDetails.getSlotSelectedDate());
                    intent.putStringArrayListExtra(Constants.IntentConstant.SELECTED_DAYS_LIST, slotsDayList);
                    intent.putExtra(Constants.IntentConstant.SLOTS, slotsArray);
                    intent.putExtra(Constants.IntentConstant.CURRENCY, productDetails.getCurrencySymbol());
                    intent.putExtra(Constants.IntentConstant.CURRENCY_CODE, productDetails.getCurrencyCode());
                    intent.putExtra(Constants.IntentConstant.START_DATE, productDetails.getDealStartTime());
                    intent.putExtra(Constants.IntentConstant.END_DATE, productDetails.getDealEndTime());
                    startActivityForResult(intent, Constants.IntentConstant.REQUEST_SLOTS);
                }
                break;
            case R.id.iv_play_video:
                startActivity(new Intent(this, VideoviewActivity.class)
                        .putExtra(Constants.VIDEO_URL, videoUrl)
                        .putExtra(Constants.VIDEO_URL_THUMB, "")
                );
                progressBar.setVisibility(View.GONE);
                break;
            case R.id.tv_reshare_buddy_count:
                if (AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")) {
                    if (productDetails.getBuddyArr() != null && productDetails.getBuddyArr().size() > 0) {
                        startActivity(new Intent(this, ReshareBuddyListActivity.class)
                                .putExtra(Constants.IntentConstant.BUDDY, productDetails.getBuddyArr())
                                .putExtra(Constants.IntentConstant.SLOTS, selectedSlotsArray)
                                .putStringArrayListExtra(Constants.IntentConstant.SELECTED_DAYS_LIST, slotsDayList)
                                .putExtra(Constants.IntentConstant.PRODUCT_DETAILS, productDetails));
                    }
                } else {
                    if (AppUtils.getInstance().isLoggedIn(this)) {
                        if (!AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.EMAIL).equals("")) {
                            hitEmailValidateApi(2);
                        } else {
                            AppUtils.getInstance().showToast(this, getString(R.string.please_provide_email_address));
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
                break;
            case R.id.btn_call:
                checkCallPermission();
                break;
        }
    }


    /**
     * function to set clicked
     * @param time
     */
    private void setClicked(int time) {
        isClicked = true;
        new Handler().postDelayed(() -> isClicked = false, time);
    }

    /**
     * Method to hit the signup api
     *
     * @param status
     */
    public void hitEmailValidateApi(final int status) {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitEmailValidateApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppSharedPreference.getInstance().putString(ProductServiceDetailsActivity.this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE, "1");
                        switch (status) {
                            case 1:
                                if (Calendar.getInstance().getTime().before(AppUtils.getInstance().getDateFromString(productDetails.getDealStartTime(), SERVICE_DATE_FORMAT))) {
                                    AppUtils.getInstance().showToast(ProductServiceDetailsActivity.this, getString(R.string.deal_not_start_yet));
                                } else {
                                    productDetails.setIsShared(isShared ? "1" : "");
                                    productDetails.setUserId(isShared ? buddyId : productDetails.getUserId());
                                    checkForProduct(1);
                                }
                                break;
                            case 2:
                                if (Calendar.getInstance().getTime().before(AppUtils.getInstance().getDateFromString(productDetails.getDealStartTime(), SERVICE_DATE_FORMAT))) {
                                    AppUtils.getInstance().showToast(ProductServiceDetailsActivity.this, getString(R.string.deal_not_start_yet));
                                } else {
                                    checkForProduct(2);
                                }
                                break;
                        }
                        break;
                    case Constants.NetworkConstant.EMAIL_NOT_VERIFIED:
                        String email = AppSharedPreference.getInstance().getString(ProductServiceDetailsActivity.this, AppSharedPreference.PREF_KEY.EMAIL);
                        new AlertDialog.Builder(ProductServiceDetailsActivity.this, R.style.DatePickerTheme)
                                .setMessage(getString(email.equals("") ? R.string.please_enter_email_address : R.string.email_not_verified))
                                .setPositiveButton(getString(email.equals("") ? R.string.ok : R.string.resend_link), (dialog, which) -> {
                                    if (AppUtils.getInstance().isInternetAvailable(ProductServiceDetailsActivity.this)) {
                                        hitResendLinkApi();
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                    // do nothing
                                })
                                .show();
                        break;
                    default:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(ProductServiceDetailsActivity.this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }

            @Override
            public void onError(String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
                AppUtils.getInstance().showToast(ProductServiceDetailsActivity.this, response);
            }

            @Override
            public void onFailure() {
                progressBar.setVisibility(View.GONE);
            }
        }, 1);
    }


    /**
     * method to hit resend link api
     */
    private void hitResendLinkApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitResendLinkApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, null, 1001);
    }


    /**
     * method to check for product
     */
    private void checkForProduct(int status) {
        if (productDetails.getProductType().equals("1")) {
            if (AppUtils.getInstance().isLoggedIn(this)) {
                if (productDetails.getQuantity() != null && !productDetails.getQuantity().equals("0")) {
                    if (status == 1) {
                        startActivity(new Intent(this, PurchaseSummaryActivity.class)
                                .putExtra(Constants.IntentConstant.PRODUCT_DETAILS, productDetails));
                    } else {
                        openChatActivity();
                    }
                } else {
                    AppUtils.getInstance().showToast(this, getString(R.string.out_of_stock));
                }
            }
        } else {
            if (AppUtils.getInstance().isLoggedIn(this)) {
                StringBuilder selectedSlots = new StringBuilder();
                double price = 0.0;
                for (int i = 0; i < selectedSlotsArray.size(); i++) {
                    if (i != 0) selectedSlots.append(",");
                    selectedSlots.append(selectedSlotsArray.get(i).getId());
                    price += Double.parseDouble(selectedSlotsArray.get(i).getPrice());
                }
                /*if (!isSlotAvailable) {
                    AppUtils.getInstance().showToast(this, getString(R.string.no_slots_available));
                    return;
                }*/
                Collections.sort(slotsDayList, new StringDateComparator());
                if (!selectedSlots.toString().equals("")) {
                    if (slotsDayList != null) price = price * slotsDayList.size();
                    if (price == 0.0) {
                        AppUtils.getInstance().showToast(this, getString(R.string.amount_cant_be_zero));
                        return;
                    }
                    productDetails.setSelectedSlots(selectedSlots.toString());
                    productDetails.setSelectedDates(slotsDayList);
                    productDetails.setSellingPrice(String.valueOf(price));
                    if (status == 1) {
                        startActivity(new Intent(this, PurchaseSummaryActivity.class)
                                .putExtra(Constants.IntentConstant.PRODUCT_DETAILS, productDetails));
                    } else {
                        openChatActivity();
                    }
                } else {
                    AppUtils.getInstance().showToast(this, getString(R.string.select_slots));
                }
            }
        }
    }

    /**
     * method to open chat activity
     */
    private void openChatActivity() {
        FirebaseDatabaseQueries.getInstance().getUser(productDetails.getUserId(), new FirebaseUserListener() {
            @Override
            public void getUser(UserBean user) {
                Intent intent = new Intent(ProductServiceDetailsActivity.this, SingleChatActivity.class);
                intent.putExtra(FirebaseConstants.OTHER_USER, user);
                intent.putExtra(FirebaseConstants.CHAT_ROOM_PRODUCT, productDetails);
                startActivity(intent);
            }

            @Override
            public void updateUser(UserBean user) {
            }

        });
    }


    /**
     * dialog to block deal
     */
    private void showBlockDialog() {
        new AlertDialog.Builder(this, R.style.DatePickerTheme).setTitle(getString(R.string.block_deal))
                .setMessage(getString(R.string.sure_to_block_deal))
                .setPositiveButton(getString(R.string.block), (dialog, which) -> {
                    if (AppUtils.getInstance().isInternetAvailable(ProductServiceDetailsActivity.this)) {
                        hitBlockProductApi();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    // do nothing
                })
                .show();

    }

    /**
     * method to hit block deal api
     */
    private void hitBlockProductApi() {
//        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, dealId);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitBlockDealApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_BLOCK_DEAL);

        Intent intent = new Intent();
        intent.putExtra(Constants.IntentConstant.PRODUCT_ID, productDetails.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * method to hit report deal api
     */
    private void hitReportProductApi(String message) {
//        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, dealId);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_REPORT, message);
        Call<ResponseBody> call = apiInterface.hitReportDealApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_REPORT_DEAL);

        Intent intent = new Intent();
        intent.putExtra(Constants.IntentConstant.PRODUCT_ID, productDetails.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Method to hit the reset password api
     */
    private void hitProductServiceDetailsApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_METHOD, Constants.NetworkConstant.GET_DEAL_DETAILS);
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, dealId);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());
        Call<ResponseBody> call = apiInterface.hitProductServiceDetailsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_DEAL_DETAILS);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_DEAL_DETAILS:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        rootView.setVisibility(View.VISIBLE);
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        ProductServiceDetailsResponse detailsResponse = new Gson().fromJson(response, ProductServiceDetailsResponse.class);
//                        ivProductPic.setImageResource(R.videoDrawable.ic_placeholder);
                        if (detailsResponse.getResult().getDealImages() != null && !detailsResponse.getResult().getDealImages().equals("")) {
                            Collections.addAll(productImagesList, detailsResponse.getResult().getDealImages().split(","));
                        } else {
                            tvProductImages.setVisibility(View.GONE);
                            imagesView.setVisibility(View.GONE);
                        }
                        AppUtils.getInstance().saveImages(this, productImagesList);
                        videoUrl = detailsResponse.getResult().getVideoUrl();
                        if (videoUrl != null && !videoUrl.trim().equals("")) {
                            AsyncTask.execute(() -> {
                                try {
                                    productImagesList.add(videoUrl);
                                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                    retriever.setDataSource(videoUrl, new HashMap<>());
                                    Bitmap bitmap = retriever.getFrameAtTime(10000, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
                                    videoDrawable = new BitmapDrawable(getResources(), bitmap);
                                    runOnUiThread(() -> {
                                        if (productImagesList.size() > 0)
                                            productImagesAdapter.notifyDataSetChanged();
                                    });
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        if (productImagesList.size() > 0)
                            productImagesAdapter.notifyDataSetChanged();
                        setProductDetails(detailsResponse.getResult());
                        break;
                    default:
//                        layoutRetry.setVisibility(View.VISIBLE);
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
                break;
        }
    }


    /**
     * Method to hit the signup api
     */
    public void hitLikeProductsApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_METHOD, Constants.NetworkConstant.SAVE_FAVOURITES_DEALS);
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, productDetails.getId());
        params.put(Constants.NetworkConstant.PARAM_IS_FAVOURITE, productDetails.getIsFavourite());
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        Call<ResponseBody> call = apiInterface.hitLikeDealsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
            }

            @Override
            public void onError(String response, int requestCode) {
                if (productDetails.getIsFavourite().equals("1")) {
                    productDetails.setIsFavourite("2");
                    menuSecondRight.setImageResource(R.drawable.ic_shopper_home_like_unfill);
                } else {
                    productDetails.setIsFavourite("1");
                    menuSecondRight.setImageResource(R.drawable.ic_home_shoppers_like_fill);
                }
            }

            @Override
            public void onFailure() {
                if (productDetails.getIsFavourite().equals("1")) {
                    productDetails.setIsFavourite("2");
                    menuSecondRight.setImageResource(R.drawable.ic_shopper_home_like_unfill);
                } else {
                    productDetails.setIsFavourite("1");
                    menuSecondRight.setImageResource(R.drawable.ic_home_shoppers_like_fill);
                }
            }
        }, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }

    /**
     * method to set product details data
     *
     * @param result
     */
    private void setProductDetails(Result result) {
        productDetails = result;
//        String currency = getString(result.getCurrency().equals("2") ? R.string.rupees : result.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
        String currency = result.getCurrencySymbol();
        if (dealImage.equals("") && productImagesList.size() > 0) {
            AppUtils.getInstance().setImages(this, productImagesList.get(0), ivProductPic, 0, R.drawable.ic_placeholder);
        }
        tvProductUserName.setText(TextUtils.concat(result.getFirstName() + " " + result.getLastName()));
        AppUtils.getInstance().setCircularImages(this, result.getImage(), civProductUserImage, R.drawable.ic_side_menu_user_placeholder);

//        tvOriginalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(result.getOrignalPrice()))));
//        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        tvFinalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(result.getSellingPrice()))));
        tvFlatDiscount.setText(TextUtils.concat(result.getDiscount() + getString(R.string.discount_post_string)));
        tvProductDescription.setText(result.getDescription());
        tvProductDescription.post(() -> {
            int lineCount = tvProductDescription.getLineCount();
            runOnUiThread(() -> {
                if (lineCount > 2) {
                    tvReadMore.setVisibility(View.VISIBLE);
                    tvProductDescription.setMaxLines(2);
                }else {
                    tvReadMore.setVisibility(View.GONE);
                    tvProductDescription.setMaxLines(2);
                }
            });
        });
        tvContact.setText(getString(result.getUserType().equals("1") ? R.string.contact_merchant : R.string.contact_buddy));
        tvAttributes.setText(result.getCustomAttribute());
        tvDealPosted.setText(AppUtils.getInstance().formatDate(productDetails.getDealStartTime(), SERVICE_DATE_FORMAT,
                Constants.AppConstant.DATE_FORMAT));
//        tvProductDetails.setText(productDetails.getName());
        if (buddyId == null || buddyId.equals("")) {
            tvProductDetails.setText(TextUtils.concat(getString(R.string.delivery_charges) + " : " + currency + productDetails.getDileveryCharge()));
        }else {
            String deliveryCharges = productDetails.getDileveryCharge();
            for (BuddyArr buddyArr : productDetails.getBuddyArr()) {
                if (buddyArr.getBuddyId().equals(buddyId)) {
                    deliveryCharges = buddyArr.getDeliveryCharge();
                    break;
                }
            }
            tvProductDetails.setText(TextUtils.concat(getString(R.string.delivery_charges) + " : " + currency + deliveryCharges));
        }
        tvProductType.setVisibility(View.VISIBLE);
        tvProductType.setText(TextUtils.concat(result.getSubcategoryName() + " " + getString(R.string.in) + " " + result.getCategoryName()));

        tvHelplineNo.setText(result.getTollFreeNumber());


        if (result.getProductType().equals("1")) {

            tvOriginalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(result.getOrignalPrice()))));
            tvFinalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(result.getSellingPrice()))));

//            tvProductType.setVisibility(View.VISIBLE);
//            tvProductType.setText(TextUtils.concat(result.getSubcategoryName() + " " + getString(R.string.in) + " " + result.getCategoryName()));

            llProductAvailability.setVisibility(View.VISIBLE);
            tvHomeDeliveryAvailable.setText(getString(result.getHomeDelivery().equals("1") ? R.string.yes : R.string.no_home_delivery));
//            tvSearchBuddy.setVisibility(result.getHomeDelivery().equals("1") ? View.GONE : View.VISIBLE);
            tvProductAvailability.setText(productDetails.getPaymentMethod().equals("1") ? getString(R.string.online) : (productDetails.getPaymentMethod().equals("2") ? getString(R.string.cash_on_delivery) : getString(R.string.online_cash_on_delivery)));

            labelPosted.setVisibility(View.VISIBLE);
            tvDealPosted.setVisibility(View.VISIBLE);
            viewPosted.setVisibility(View.VISIBLE);

            tvSlotAvailability.setVisibility(View.GONE);
            rvTimeSlots.setVisibility(View.GONE);
            viewTimeSlots.setVisibility(View.GONE);

            if (result.getDiscount().equals("0")) {
                tvProductName.setText(result.getName());
            } else {
                tvProductName.setText(TextUtils.concat(getString(R.string.flat) + " " + result.getDiscount() + getString(R.string.off_on) + " " + result.getName()));
            }
            tvDealExpiry.setText(AppUtils.getInstance().formatDate(productDetails.getDealEndTime(), SERVICE_DATE_FORMAT,
                    Constants.AppConstant.DATE_FORMAT));
        } else {

            currency = productDetails.getCurrencySymbol();

//            currency = AppUtils.getInstance().getCurrency(this);
//            String currencyCode = currency.equals(getString(R.string.rupees)) ? "2" : currency.equals(getString(R.string.dollar)) ? "1" : "3";
//            productDetails.setCurrency(currencyCode);
            rlPrice.setVisibility(View.GONE);
            tvOriginalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(result.getOrignalPrice())) + (productDetails.getServiceType().equals("1") ? "" : getString(R.string.per_hour))));
            tvFinalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(result.getSellingPrice())) + (productDetails.getServiceType().equals("1") ? "" : getString(R.string.per_hour))));

            labelPosted.setVisibility(View.GONE);
            tvDealPosted.setVisibility(View.GONE);
            viewPosted.setVisibility(View.GONE);

//            tvProductDetails.setVisibility(View.GONE);
            tvProductUserType.setVisibility(View.GONE);
//            tvProductType.setVisibility(View.GONE);
            llProductAvailability.setVisibility(View.VISIBLE);
            tvProductAvailability.setText(productDetails.getPaymentMethod().equals("1") ? getString(R.string.online) : (productDetails.getPaymentMethod().equals("2") ? getString(R.string.cash_on_delivery) : getString(R.string.online_cash_on_delivery)));
            tvSearchBuddy.setVisibility(View.GONE);
            tvSlotAvailability.setVisibility(View.VISIBLE);
            rvTimeSlots.setVisibility(View.VISIBLE);
            viewTimeSlots.setVisibility(View.VISIBLE);
            tvProductName.setText(result.getName());
            /*for (int i = 0; i < result.getServiceSlot().size(); i++) {
                String startTime = AppUtils.getInstance().formatDate(result.getServiceSlot().get(i).getSlotStartTime(), "HH:mm:ss", "hh:mm a");
                String endTime = AppUtils.getInstance().formatDate(result.getServiceSlot().get(i).getSlotEndTime(), "HH:mm:ss", "hh:mm a");
                String startDate = AppUtils.getInstance().formatDate(result.getServiceSlot().get(i).getSlotStartDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
                String endDate = AppUtils.getInstance().formatDate(result.getServiceSlot().get(i).getSlotEndDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
                String time = startDate + " " + startTime + " - " + endDate + " " + endTime;
                rvTimeSlots.addView(timeSlotView(time));
            }*/
            slotsArray.clear();
            slotsArray.addAll(result.getServiceSlot());
//            slotsAdapter.notifyDataSetChanged();
            if (productDetails.getDealStartTime() != null && !productDetails.getDealStartTime().equals("") &&
                    productDetails.getDealEndTime() != null && !productDetails.getDealEndTime().equals("")) {
                tvLabelDealExpired.setText(getString(R.string.deal_validity));
                tvDealExpiry.setText(TextUtils.concat(
                        AppUtils.getInstance().formatDate(productDetails.getDealStartTime(), SERVICE_DATE_FORMAT,
                                Constants.AppConstant.DATE_FORMAT) + " "
                                + getString(R.string.to)
                                + " " + AppUtils.getInstance().formatDate(productDetails.getDealEndTime(),
                                SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT)));
            } else {
                tvLabelDealExpired.setVisibility(View.GONE);
                tvDealExpiry.setVisibility(View.GONE);
            }
        }
        if (result.getUserType().equals("1")) {
            tvProductUserType.setVisibility(View.VISIBLE);
            tvProductUserType.setText(TextUtils.concat(" (" + getString(R.string.merchant) + ")"));
            ivBuddyFollow.setVisibility(View.GONE);
            tvAddress.setText(productDetails.getStoreLocation());
        } else {
            tvProductUserType.setVisibility(View.VISIBLE);
            tvProductUserType.setText(TextUtils.concat(" (" + getString(R.string.buddy) + ")"));
            tvHomeDeliveryAvailable.setText(getString(R.string.yes));
            tvSearchBuddy.setVisibility(View.GONE);
            tvAddress.setText(productDetails.getBuddyAddress());

            ivBuddyFollow.setVisibility(View.VISIBLE);
            isFollow = productDetails.getIsFollow() == null ? "0" : result.getIsFollow();
            if (isFollow.equals(NOT_REQUESTED) || isFollow.equals(CANCELED)) {
                ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_follow);
            } else {
                ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_following);
            }
        }
        if (productDetails.getIsFavourite().equals("1")) {
            menuSecondRight.setImageResource(R.drawable.ic_home_shoppers_like_fill);
        } else {
            menuSecondRight.setImageResource(R.drawable.ic_shopper_home_like_unfill);
        }
        if (productDetails.getCustomAttribute() == null || productDetails.getCustomAttribute().equals("")) {
            tvAttributes.setVisibility(View.GONE);
            viewAttributes.setVisibility(View.GONE);
            labelAttributes.setVisibility(View.GONE);
        }
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvSearchBuddy.setVisibility(View.GONE);

        if (buddyId != null && !buddyId.equals("")) {
            tvContact.setText(getString(R.string.contact_buddy));
            tvReshareBuddyCount.setVisibility(View.GONE);
        }else {
            if (productDetails.getResharedCount() != null && !productDetails.getResharedCount().equals("") && !productDetails.getResharedCount().equals("0")) {
                tvReshareBuddyCount.setVisibility(View.VISIBLE);
                tvReshareBuddyCount.setText(TextUtils.concat(getString(R.string.reshared_buddy_count) + " " + productDetails.getResharedCount()));
            } else {
                tvReshareBuddyCount.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
//        layoutRetry.setVisibility(View.VISIBLE);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        progressBar.setVisibility(View.GONE);
//        layoutRetry.setVisibility(View.VISIBLE);
    }

    /**
     * method to inflate view for language list
     *
     * @param text
     * @return
     */
    private View timeSlotView(String text) {
        final View view = LayoutInflater.from(this).inflate(R.layout.item_selection_view_white, null);
        final CustomTextView tvSelection = view.findViewById(R.id.text_view);
        tvSelection.setText(text);
        view.setTag(text);
        return view;
    }

    @Override
    public void onBackPressed() {
        if (productDetails != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.IntentConstant.PRODUCT_ID, productDetails.getId());
            intent.putExtra(Constants.IntentConstant.IS_FAVOURITE, productDetails.getIsFavourite());
            setResult(RESULT_CANCELED, intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        progressBar.setVisibility(View.GONE);
        if (requestCode == Constants.IntentConstant.REQUEST_SLOTS && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            slotsDayList.clear();
            slotsArray.clear();
            ArrayList<ServiceSlot> slotList = (ArrayList<ServiceSlot>) data.getExtras().getSerializable(Constants.IntentConstant.TIME_SLOTS);
            ArrayList<String> daysList = data.getExtras().getStringArrayList(Constants.IntentConstant.SELECTED_DAYS_LIST);
            if (daysList != null) slotsDayList.addAll(daysList);
            if (slotList != null) {
                slotsArray.addAll(slotList);
                selectedSlotsArray.clear();
                for (ServiceSlot slot : slotsArray) {
                    if (slot.isSelected()) {
                        selectedSlotsArray.add(slot);
                    }
                }
                slotsAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * inner class for sort date array
     */
    class StringDateComparator implements Comparator<String> {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(SERVICE_DATE_FORMAT);

        public int compare(String lhs, String rhs) {
            try {
                return dateFormat.parse(lhs).compareTo(dateFormat.parse(rhs));
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    /**
     * function to hit follow buddies api
     * @param status
     * @param userId
     */
    private void hitBuddyFollowApi(String status, String userId) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_FOLLOW_ID, userId);
        params.put(Constants.NetworkConstant.PARAM_STATUS, status);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitBuddyFollowApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {
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
                                    AppUtils.getInstance().showToast(ProductServiceDetailsActivity.this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
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
                    productDetails.setIsFollow("0");
                } else {
                    ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_following);
                    productDetails.setIsFollow("1");
                }
            }

            @Override
            public void onFailure() {
                if (isFollow.equals(NOT_REQUESTED) || isFollow.equals(CANCELED)) {
                    ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_follow);
                    productDetails.setIsFollow("0");
                } else {
                    ivBuddyFollow.setImageResource(R.drawable.ic_buddy_details_following);
                    productDetails.setIsFollow("1");
                }
            }
        }, Constants.NetworkConstant.BUDDY_REQUEST);
    }

    /**
     * method to check the call permission
     */
    public void checkCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, Constants.IntentConstant.REQUEST_CALL);
            } else {
                callTollFree();
            }
        } else {
            callTollFree();
        }
    }


    /**
     * method to make phone call to buddy
     */
    private void callTollFree() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + productDetails.getTollFreeNumber()));
        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.IntentConstant.REQUEST_CALL:
                boolean isRationalGalleryStorage = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                        ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callTollFree();
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_call_permission));
                }

                break;
        }
    }

}
