package com.shopoholic.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.ProductImagesAdapter;
import com.shopoholic.adapters.SlotsAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForMakeOffer;
import com.shopoholic.firebasechat.activities.SingleChatActivity;
import com.shopoholic.firebasechat.interfaces.FirebaseUserListener;
import com.shopoholic.firebasechat.models.ProductBean;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.interfaces.MakeOfferDialogCallback;
import com.shopoholic.interfaces.PopupItemDialogCallback;
import com.shopoholic.interfaces.RecyclerCallBack;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class created by Sachin on 09-Apr-18.
 */

public class ProductServiceDetailsActivityDemo extends BaseActivity implements NetworkListener {

    @BindView(R.id.iv_product_pic)
    ImageView ivProductPic;
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
    @BindView(R.id.rv_product_images)
    RecyclerView rvProductImages;
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
    @BindView(R.id.tv_attributes)
    CustomTextView tvAttributes;
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
    @BindView(R.id.tv_label_deal_expired)
    CustomTextView tvLabelDealExpiry;
    @BindView(R.id.tv_deal_expiry)
    CustomTextView tvDealExpiry;
    @BindView(R.id.tv_slot_availability)
    CustomTextView tvSlotAvailability;
    //    @BindView(R.id.fbl_time_slots)
//    FlexboxLayout rvTimeSlots;
    @BindView(R.id.rv_time_slots)
    RecyclerView rvTimeSlots;
    @BindView(R.id.view_time_slots)
    View viewTimeSlots;
    @BindView(R.id.images_view)
    View imagesView;
    @BindView(R.id.tv_contact)
    CustomTextView tvContact;
    @BindView(R.id.tv_buy_now)
    CustomTextView tvBuyNow;
    @BindView(R.id.root_view)
    ScrollView rootView;
    @BindView(R.id.layout_retry)
    LinearLayout layoutRetry;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.view_attributes)
    View viewAttributes;
    @BindView(R.id.tv_product_images)
    CustomTextView tvProductImages;
    @BindView(R.id.label_attributes)
    CustomTextView labelAttributes;
//    @BindView(R.id.vv_product_video)
//    VideoView vvProductVideo;
    @BindView(R.id.iv_play_video)
    ImageView ivPlayVideo;
    private String dealId = "";
    private List<String> productImagesList;
    private ProductImagesAdapter productImagesAdapter;
    private Result productDetails;
    private ArrayList<ServiceSlot> slotsArray;
    private SlotsAdapter slotsAdapter;
    private boolean isSlotAvailable;
    private boolean isShared;
    private String buddyId;
    private String videoUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_services_details_demo);
        ButterKnife.bind(this);
        initVariables();
        setAdapters();
        if (getIntent() != null && getIntent().getExtras() != null) {
            dealId = getIntent().getExtras().getString(Constants.IntentConstant.DEAL_ID, "");
            isShared = getIntent().getExtras().getBoolean(Constants.IntentConstant.IS_SHARED, false);
            buddyId = getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_ID, "");
        }
        if (AppUtils.getInstance().isInternetAvailable(this)) hitProductServiceDetailsApi();
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        layoutToolbar.setBackgroundResource(R.drawable.gradient_transluscent_overlay);
        productImagesList = new ArrayList<>();
        slotsArray = new ArrayList<>();
        productImagesAdapter = new ProductImagesAdapter(this, productImagesList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                if (productImagesList.get(position).equals(videoUrl)) {
//                    ivProductPic.setVisibility(View.GONE);
//                    vvProductVideo.setVisibility(View.VISIBLE);
                    ivPlayVideo.setVisibility(View.VISIBLE);
//                    vvProductVideo.setVideoPath(productImagesList.get(position));

//                    MediaController mediaController = new MediaController(ProductServiceDetailsActivity.this);
//                    mediaController.setAnchorView(vvProductVideo);

//                    vvProductVideo.setMediaController(mediaController);
//                    vvProductVideo.start();
                } else {
//                    ivProductPic.setVisibility(View.VISIBLE);
//                    vvProductVideo.setVisibility(View.GONE);
                    ivPlayVideo.setVisibility(View.GONE);
                }
                AppUtils.getInstance().setImages(ProductServiceDetailsActivityDemo.this, productImagesList.get(position), ivProductPic,
                        0, R.drawable.ic_placeholder);
            }
        });
        slotsAdapter = new SlotsAdapter(this, slotsArray, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.fl_root_view:
                        slotsArray.get(position).setSelected(!slotsArray.get(position).isSelected());
                        slotsAdapter.notifyItemChanged(position);
                        break;
                }
            }
        });
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setImageResource(R.drawable.ic_home_shopper_more_ic);
        menuThirdRight.setImageResource(R.drawable.ic_home_shopper_share_ic);
        menuRight.setVisibility(View.VISIBLE);
        menuSecondRight.setVisibility(View.VISIBLE);
        menuThirdRight.setVisibility(View.VISIBLE);

    }

    /**
     * method to set the adapters in views
     */
    private void setAdapters() {
        rvTimeSlots.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTimeSlots.setAdapter(slotsAdapter);

        rvProductImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvProductImages.setAdapter(productImagesAdapter);
    }

    @OnClick({R.id.menu_right, R.id.menu_second_right, R.id.menu_third_right, R.id.iv_menu, R.id.tv_search_buddy, R.id.tv_contact, R.id.tv_buy_now, R.id.iv_play_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search_buddy:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    startActivity(new Intent(this, CommonActivity.class)
                            .putExtra(Constants.IntentConstant.FRAGMENT_TYPE, 0)
                            .putExtra(Constants.IntentConstant.DEAL_ID, productDetails.getId()));
                }
                break;
            case R.id.menu_second_right:
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
                break;
            case R.id.menu_right:
                AppUtils.getInstance().showMorePopUp(this, menuRight, getString(R.string.block), getString(R.string.report),
                        "", 1, new PopupItemDialogCallback() {
                            @Override
                            public void onItemOneClick() {
                                if (AppUtils.getInstance().isLoggedIn(ProductServiceDetailsActivityDemo.this)) {
                                    showBlockDialog();
                                }
                            }

                            @Override
                            public void onItemTwoClick() {
                                if (AppUtils.getInstance().isLoggedIn(ProductServiceDetailsActivityDemo.this)) {
                                    CustomDialogForMakeOffer customDialogForSelectDate = new CustomDialogForMakeOffer(ProductServiceDetailsActivityDemo.this, 3, "", new MakeOfferDialogCallback() {
                                        @Override
                                        public void onSelect(String message, String currency, String text, int type) {
                                            if (AppUtils.getInstance().isInternetAvailable(ProductServiceDetailsActivityDemo.this)) {
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
                break;
            case R.id.menu_third_right:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, productDetails.getName());
                /*if (productImagesList.size() > 0) {
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(productImagesList.get(0)));
                }*/
                sendIntent.putExtra(Intent.EXTRA_TEXT, productDetails.getDealUrl());
                sendIntent.setType("text/uri");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_deal)));
                break;
            case R.id.tv_contact:
                if (AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")) {
                    checkForProduct(2);
                } else {
                    hitEmailValidateApi(2);
                }
                break;
            case R.id.tv_buy_now:
                if (AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")) {
                    productDetails.setIsShared(isShared ? "1" : "");
                    productDetails.setUserId(isShared ? buddyId : productDetails.getUserId());
                    checkForProduct(1);
                } else {
                    hitEmailValidateApi(1);
                }
                break;
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.iv_play_video:
                startActivity(new Intent(this, VideoviewActivity.class)
                        .putExtra(Constants.VIDEO_URL, videoUrl)
                        .putExtra(Constants.VIDEO_URL_THUMB, "")
                );
                break;
        }
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
                        AppSharedPreference.getInstance().putString(ProductServiceDetailsActivityDemo.this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE, "1");
                        switch (status) {
                            case 1:
                                productDetails.setIsShared(isShared ? "1" : "");
                                productDetails.setUserId(isShared ? buddyId : productDetails.getUserId());
                                checkForProduct(1);
                                break;
                            case 2:
                                checkForProduct(2);
                                break;
                        }
                        break;
                    default:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        try {
                            AppUtils.getInstance().showToast(ProductServiceDetailsActivityDemo.this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }

            @Override
            public void onError(String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
                AppUtils.getInstance().showToast(ProductServiceDetailsActivityDemo.this, response);
            }

            @Override
            public void onFailure() {
                progressBar.setVisibility(View.GONE);
            }
        }, 1);
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
                for (int i = 0; i < slotsArray.size(); i++) {
                    if (!isSlotAvailable && slotsArray.get(i).getIsAvailable().equals("1")) {
                        isSlotAvailable = true;
                    }
                    if (slotsArray.get(i).isSelected()) {
                        if (i != 0) selectedSlots.append(",");
                        selectedSlots.append(slotsArray.get(i).getId());
                    }
                }
                if (!isSlotAvailable) {
                    AppUtils.getInstance().showToast(this, getString(R.string.no_slots_available));
                    return;
                }
                if (!selectedSlots.toString().equals("")) {
                    productDetails.setSelectedSlots(selectedSlots.toString());
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
                ProductBean productBean = new ProductBean();
                productBean.setId(productDetails.getId());
                productBean.setName(productDetails.getName());
                if (productDetails.getDealImages() != null && productDetails.getDealImages().split(",").length > 0)
                    productBean.setImage(productDetails.getDealImages().split(",")[0]);
                productBean.setPrice(productDetails.getSellingPrice());
                productBean.setCurrency(productDetails.getCurrency());
                productBean.setQuantity(productDetails.getQuantity());

                productBean.setOriginalPrice(productDetails.getOrignalPrice());
                productBean.setDiscount(productDetails.getDiscount());
                productBean.setDealStartTime(AppUtils.getInstance().formatDate(productDetails.getDealStartTime(), "yyyy-MM-dd", "dd MMM"));
                productBean.setDealEndTime(AppUtils.getInstance().formatDate(productDetails.getDealEndTime(), "yyyy-MM-dd", "dd MMM"));

                Intent intent = new Intent(ProductServiceDetailsActivityDemo.this, SingleChatActivity.class);
                intent.putExtra(FirebaseConstants.OTHER_USER, user);
//                intent.putExtra(FirebaseConstants.CHAT_ROOM_PRODUCT, productBean);
                intent.putExtra(FirebaseConstants.CHAT_ROOM_PRODUCT, productDetails);
                startActivity(intent);
            }

            @Override
            public void updateUser(UserBean user) {
            }

        });
    }


    /**
     * dialog to block user
     */
    private void showBlockDialog() {
        new AlertDialog.Builder(this, R.style.DatePickerTheme).setTitle(getString(R.string.block_deal))
                .setMessage(getString(R.string.sure_to_block_deal))
                .setPositiveButton(getString(R.string.block), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (AppUtils.getInstance().isInternetAvailable(ProductServiceDetailsActivityDemo.this)) {
                            hitBlockProductApi();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
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
                        if (detailsResponse.getResult().getDealImages() != null && !detailsResponse.getResult().getDealImages().equals("")) {
                            Collections.addAll(productImagesList, detailsResponse.getResult().getDealImages().split(","));
                        } else {
                            tvProductImages.setVisibility(View.GONE);
                            imagesView.setVisibility(View.GONE);
                        }
                        AppUtils.getInstance().saveImages(this, productImagesList);
                        videoUrl = detailsResponse.getResult().getVideoUrl();
                        if (videoUrl != null && !videoUrl.equals(""))productImagesList.add(videoUrl);
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
        String currency = getString(result.getCurrency().equals("2") ? R.string.rupees : result.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
        if (productImagesList.size() > 0) {
            AppUtils.getInstance().setImages(this, productImagesList.get(0), ivProductPic, 0, R.drawable.ic_placeholder);
        }
        tvProductUserName.setText(TextUtils.concat(result.getFirstName() + " " + result.getLastName()));
        AppUtils.getInstance().setCircularImages(this, result.getImage(), civProductUserImage, R.drawable.ic_side_menu_user_placeholder);

//        tvOriginalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(result.getOrignalPrice()))));
//        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        tvFinalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(result.getSellingPrice()))));
        tvFlatDiscount.setText(TextUtils.concat(result.getDiscount() + getString(R.string.discount_post_string)));
        tvProductDescription.setText(result.getDescription());
        tvContact.setText(getString(result.getUserType().equals("1") ? R.string.contact_merchant : R.string.contact_buddy));
        tvAttributes.setText(result.getCustomAttribute());
        tvDealPosted.setText(AppUtils.getInstance().formatDate(productDetails.getDealStartTime(), Constants.AppConstant.SERVICE_DATE_FORMAT,
                Constants.AppConstant.DATE_FORMAT));
        tvProductDetails.setText(productDetails.getName());


        if (result.getProductType().equals("1")) {

            tvOriginalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(result.getOrignalPrice()))));
            tvFinalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(result.getSellingPrice()))));

            tvProductType.setVisibility(View.VISIBLE);
            tvProductType.setText(TextUtils.concat(result.getSubcategoryName() + " " + getString(R.string.in) + " " + result.getCategoryName()));

            llProductAvailability.setVisibility(View.VISIBLE);
            tvHomeDeliveryAvailable.setText(getString(result.getHomeDelivery().equals("1") ? R.string.yes : R.string.no));
//            tvSearchBuddy.setVisibility(result.getHomeDelivery().equals("1") ? View.GONE : View.VISIBLE);
            tvProductAvailability.setText(productDetails.getPaymentMethod().equals("1") ? getString(R.string.online) : (productDetails.getPaymentMethod().equals("2") ? getString(R.string.cash_on_delivery) : getString(R.string.online_cash_on_delivery)));

            tvSlotAvailability.setVisibility(View.GONE);
            rvTimeSlots.setVisibility(View.GONE);
            viewTimeSlots.setVisibility(View.GONE);

            if (result.getDiscount().equals("0")) {
                tvProductName.setText(result.getName());
            } else {
                tvProductName.setText(TextUtils.concat(getString(R.string.flat) + " " + result.getDiscount() + getString(R.string.off_on) + " " + result.getName()));
            }
            tvDealExpiry.setText(AppUtils.getInstance().formatDate(productDetails.getDealEndTime(), Constants.AppConstant.SERVICE_DATE_FORMAT,
                    Constants.AppConstant.DATE_FORMAT));
        } else {

            tvOriginalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(result.getOrignalPrice())) + getString(R.string.per_hour)));
            tvFinalPrice.setText(TextUtils.concat(currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(result.getSellingPrice())) + getString(R.string.per_hour)));

            tvProductDetails.setVisibility(View.GONE);
            tvProductUserType.setVisibility(View.GONE);
            tvProductType.setVisibility(View.GONE);
            llProductAvailability.setVisibility(View.GONE);
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
            slotsAdapter.notifyDataSetChanged();
            if (productDetails.getDealStartTime() != null && !productDetails.getDealStartTime().equals("") &&
                    productDetails.getDealEndTime() != null && !productDetails.getDealEndTime().equals("")) {
                tvLabelDealExpiry.setText(getString(R.string.deal_validity));
                tvDealExpiry.setText(TextUtils.concat(
                        AppUtils.getInstance().formatDate(productDetails.getDealStartTime(), Constants.AppConstant.SERVICE_DATE_FORMAT,
                                Constants.AppConstant.DATE_FORMAT) + " "
                                + getString(R.string.to)
                                + " " + AppUtils.getInstance().formatDate(productDetails.getDealEndTime(),
                                Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT)));
            } else {
                tvLabelDealExpiry.setVisibility(View.GONE);
                tvDealExpiry.setVisibility(View.GONE);
            }
        }
        if (result.getUserType().equals("1")) {
            tvProductUserType.setVisibility(View.VISIBLE);
            tvProductUserType.setText(TextUtils.concat(" (" + getString(R.string.merchant) + ")"));
        } else {
            tvProductUserType.setVisibility(View.VISIBLE);
            tvProductUserType.setText(TextUtils.concat(" (" + getString(R.string.buddy) + ")"));
            tvHomeDeliveryAvailable.setText(getString(R.string.yes));
            tvSearchBuddy.setVisibility(View.GONE);
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
}
