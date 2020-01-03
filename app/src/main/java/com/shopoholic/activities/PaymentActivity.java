package com.shopoholic.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dnitinverma.locationlibrary.RCLocation;
import com.dnitinverma.locationlibrary.interfaces.LocationsCallback;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.shopoholic.R;
import com.shopoholic.adapters.CardAdapter;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForMessage;
import com.shopoholic.firebasechat.models.HuntDeal;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.fragments.MyWalletFragment;
import com.shopoholic.models.checksumresponse.ChecksumResponse;
import com.shopoholic.models.productservicedetailsresponse.Result;
import com.shopoholic.models.walletresponse.WalletResponse;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;
import com.stripe.android.SourceCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.READ_SMS;

/**
 * Class created by Sachin on 19-Apr-18.
 */
public class PaymentActivity extends BaseActivity implements NetworkListener, LocationsCallback {
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
    @BindView(R.id.rv_card_payment)
    RecyclerView rvCardPayment;
    @BindView(R.id.tv_add_new_card)
    CustomTextView tvAddNewCard;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.tv_product_price)
    CustomTextView tvProductPrice;
    @BindView(R.id.ll_card_payment)
    LinearLayout llCardPayment;
    @BindView(R.id.ll_online_payment)
    LinearLayout llOnlinePayment;
    @BindView(R.id.ll_cod)
    LinearLayout llCod;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.iv_pay_with_paytm)
    ImageView ivPayWithPaytm;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;

    @BindView(R.id.cb_wallet)
    CheckBox cbWallet;
    @BindView(R.id.tv_points)
    TextView tvPoints;
    @BindView(R.id.wallet_message)
    TextView walletMessage;
    @BindView(R.id.remain_amount)
    TextView remainAmount;
    @BindView(R.id.tv_pay)
    TextView tvPay;
    @BindView(R.id.progress)
    ProgressBar progress;


    private CardAdapter cardAdapter;
    private LinearLayoutManager layoutManager;
    private boolean isLoading;
    private Result productDetails;
    private com.shopoholic.models.deliveryaddressresponse.Result deliveryAddress;
    private String paymentMode = "1";
    private double finalPrice;
    private String roomId = "";
    private String checksum = "";
    private String newChecksum = "";
    private String orderNo = "";
    private String paytmMerchantId = "";
    private String customerId = "";
    private String transactionId = "";
    private RCLocation location;
    private double latitude, longitude;
    private HuntDeal huntDeal;
    private String buddyId = "";
    private String isoCode;
    private String currencyCode;
    private String sourceToken = "";
    private String paymentType = "";
    private double walletAmount = 0;
    private String publishableKey = "pk_test_pNQwocVkhFKS4Nz1aG9qC8ic"; //alipay testing key
    private boolean alipay = false;
//    private String publishableKey = "pk_live_tymoCcAHJqvRuefVvdYmJ9Kw";  //alipay live key

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        initVariables();
//        initializeLocation();
        setAdapter();
    }

    /**
     * method to initialize the location
     */
    private void initializeLocation() {
        location = new RCLocation();
        location.setActivity(this);
        location.setCallback(this);
        location.startLocation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (alipay) {
            alipay = false;
            if (AppUtils.getInstance().isInternetAvailable(this)) {
                if (productDetails != null) {
                    hitBuyProductServiceAPi();
                } else if (huntDeal != null) {
                    hitBuyHuntAPi();
                } else {
                    AppUtils.getInstance().showToast(PaymentActivity.this, getString(R.string.unable_to_make_payment));
                }
            }
        }
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        cbWallet.setClickable(false);
        cbWallet.setEnabled(false);

        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);
        btnAction.setText(getString(R.string.request_for_cod));
        tvTitle.setText(getString(R.string.payment));
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        cardAdapter = new CardAdapter(this, (position, view) -> {
            if (!isLoading) {
                if (AppUtils.getInstance().isInternetAvailable(PaymentActivity.this)) {
                    payOnline();
                }
            }
        });
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(0, -1);
        if (getIntent() != null && getIntent().getExtras() != null) {
            roomId = getIntent().getExtras().getString(FirebaseConstants.ROOM_ID, "");
            productDetails = (Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.PRODUCT_DETAILS);
            huntDeal = (HuntDeal) getIntent().getExtras().getSerializable(Constants.IntentConstant.HUNT_DEAL);
            buddyId = getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_ID, "");
//            customerId = getIntent().getExtras().getString(Constants.IntentConstant.CUSTOMER_ID, "");
//            paytmMerchantId = getIntent().getExtras().getString(Constants.IntentConstant.MERCHANT_ID, "");
            if (productDetails != null) {
                deliveryAddress = (com.shopoholic.models.deliveryaddressresponse.Result) getIntent().getExtras()
                        .getSerializable(Constants.IntentConstant.DELIVERY_ADDRESS);
//                String currency = getString(productDetails.getCurrency().equals("2") ? R.string.rupees : productDetails.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
                double taxes = 0.0, deliveryCharges = 0.0;
                if (productDetails.getTaxes() != null && !productDetails.getTaxes().equals("")) {
                    taxes = Double.parseDouble(productDetails.getTaxes());
                }
                if (productDetails.getDileveryCharge() != null && !productDetails.getDileveryCharge().equals("")) {
                    deliveryCharges = Double.parseDouble(productDetails.getDileveryCharge());
                }
                String price = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(productDetails.getSellingPrice()));
                finalPrice = (((Double.parseDouble(productDetails.getSellingPrice()) * Integer.parseInt(productDetails.getQuantity())) + taxes + deliveryCharges));
                tvProductPrice.setText(TextUtils.concat(productDetails.getCurrencySymbol() + String.format(Locale.ENGLISH, "%.2f", finalPrice)));
                if (productDetails.getPaymentMethod().equals("1")) {
                    llCod.setVisibility(View.INVISIBLE);
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        hitWalletDetailsApi();
                    }
                } else if (productDetails.getPaymentMethod().equals("2")) {
                    llCardPayment.setVisibility(View.GONE);
                    llOnlinePayment.setVisibility(View.GONE);
                } else {
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        hitWalletDetailsApi();
                    }
                }

                // Allow only cod
//                llCardPayment.setVisibility(View.GONE);
//                llOnlinePayment.setVisibility(View.GONE);
//                llCod.setVisibility(View.VISIBLE);
            }
            if (huntDeal != null) {
                double taxes = 0.0;
                if (huntDeal.getTaxes() != null && !huntDeal.getTaxes().equals("")) {
                    taxes = Double.parseDouble(huntDeal.getTaxes());
                }
                finalPrice = Double.parseDouble(huntDeal.getPrice()) + taxes;
                tvProductPrice.setText(TextUtils.concat(huntDeal.getCurrencySymbol() + String.format(Locale.ENGLISH, "%.2f", finalPrice)));
//                llCod.setVisibility(View.INVISIBLE);
//                if (AppUtils.getInstance().isInternetAvailable(this)) {
//                    hitWalletDetailsApi();
//                }

                if (huntDeal.getCurrencyCode().equals("INR")) {
                    llCod.setVisibility(View.INVISIBLE);
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        hitWalletDetailsApi();
                    }
                } else {
                    llCardPayment.setVisibility(View.GONE);
                    llOnlinePayment.setVisibility(View.GONE);
                }


                // Allow only cod
                llCardPayment.setVisibility(View.GONE);
                llOnlinePayment.setVisibility(View.GONE);
                llCod.setVisibility(View.VISIBLE);
            }
            setPaymentOption();
        }
    }

    /**
     * function to make online payment
     */
    private void payOnline() {
        /*if (latitude == 0.0 && longitude == 0.0) {
            AppUtils.getInstance().showToast(this, getString(R.string.fetching_location));
            return;
        }*/
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            return;
        }
        paymentMode = "2";
        switch (currencyCode) {
            case "INR":
                //implementing paytm payment gateway
                if (transactionId.equals("") || checksum.equals("") || orderNo.equals("")) {
                    paymentType = "2";
                    hitGenerateChecksumApiForPaytm();
//                    hitAlipayPayment();
//                    hitStripePayment();
                } else {
                    if (productDetails != null) {
                        hitBuyProductServiceAPi();
                    } else if (huntDeal != null){
                        hitBuyHuntAPi();
                    } else {
                        AppUtils.getInstance().showToast(PaymentActivity.this, getString(R.string.unable_to_make_payment));
                    }
                }
                break;
            case "HKD":
            case "SGD":
                //implementing alipay (strip) payment gateway
//                    hitGenerateChecksumApiForPaytm();
                paymentType = "1";
                hitAlipayPayment();
                break;
            case "MOP":
            case "AED":
            default:
                //implementing (strip) payment gateway todo... till now its paytm
                paymentType = "2";
                if (transactionId.equals("") || checksum.equals("") || orderNo.equals("")) {
                    hitGenerateChecksumApiForPaytm();
                } else {
                    if (productDetails != null) {
                        hitBuyProductServiceAPi();
                    } else if (huntDeal != null){
                        hitBuyHuntAPi();
                    } else {
                        AppUtils.getInstance().showToast(PaymentActivity.this, getString(R.string.unable_to_make_payment));
                    }
                }
                break;
        }
    }


    /**
     * method to set adapter in views
     */
    private void setAdapter() {
        rvCardPayment.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvCardPayment);
        rvCardPayment.setAdapter(cardAdapter);
    }

    @OnClick({R.id.iv_menu, R.id.btn_action, R.id.iv_pay_with_paytm, R.id.cb_wallet, R.id.tv_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                finish();
                break;
            case R.id.iv_pay_with_paytm:
                if (!isLoading) {
                    if (AppUtils.getInstance().isInternetAvailable(PaymentActivity.this)) {
                        payOnline();
                    }
                }
            case R.id.tv_pay:
                if (cbWallet.isChecked() && !isLoading) {
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        paymentMode = "3";
                        paymentType = "4";
                        if (productDetails != null) {
                            hitBuyProductServiceAPi();
                        } else if (huntDeal != null){
                            hitBuyHuntAPi();
                        } else {
                            AppUtils.getInstance().showToast(PaymentActivity.this, getString(R.string.unable_to_make_payment));
                        }
                    }
                }
                break;
            case R.id.cb_wallet:
                new Handler().postDelayed(this::updateWallet, 100);
                break;
            case R.id.btn_action:
                if (!isLoading) {
                    isLoading = true;
                    if (cbWallet.isChecked()) {
                        new AlertDialog.Builder(this, R.style.DatePickerTheme)
                                .setMessage(getString(R.string.wallet_not_applied))
                                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                                    if (AppUtils.getInstance().isInternetAvailable(PaymentActivity.this)) {
                                        paymentMode = "1";
                                        paymentType = "3";
                                        if (productDetails != null) {
                                            hitBuyProductServiceAPi();
                                        } else if (huntDeal != null) {
                                            hitBuyHuntAPi();
                                        } else {
                                            isLoading = false;
                                            AppUtils.getInstance().showToast(PaymentActivity.this, getString(R.string.unable_to_make_payment));
                                        }
                                    }else {
                                        isLoading = false;
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                    // do nothing
                                    isLoading = false;
                                })
                                .show();
                    } else {
                        if (AppUtils.getInstance().isInternetAvailable(PaymentActivity.this)) {
                            paymentMode = "1";
                            paymentType = "3";
                            if (productDetails != null) {
                                hitBuyProductServiceAPi();
                            } else if (huntDeal != null) {
                                hitBuyHuntAPi();
                            } else {
                                isLoading = false;
                                AppUtils.getInstance().showToast(PaymentActivity.this, getString(R.string.unable_to_make_payment));
                            }
                        }else {
                            isLoading = false;
                        }
                    }
                }
                break;
        }
    }

    /**
     * function to update the wallet
     */
    private void updateWallet() {
        double points = walletAmount;
        tvPoints.setText(TextUtils.concat("(" + points + " " + getString(R.string.points) + (points > 0 ? " = " + (productDetails != null ? productDetails.getCurrencySymbol() : huntDeal.getCurrencySymbol()) + String.format(Locale.ENGLISH, "%.2f",points) : "") + ")"));
        if (cbWallet.isChecked() && points > 0) {
            cbWallet.setChecked(true);
            if (points < finalPrice) {
                walletMessage.setVisibility(View.VISIBLE);
                walletMessage.setText(getString(R.string.choose_another_option_to_complete_the_payment));
                remainAmount.setVisibility(View.VISIBLE);
                remainAmount.setText(TextUtils.concat((productDetails != null ? productDetails.getCurrencySymbol() : huntDeal.getCurrencySymbol()) + String.format(Locale.ENGLISH, "%.2f",(finalPrice - points))));
                ivPayWithPaytm.setVisibility(View.VISIBLE);
                tvPay.setVisibility(View.GONE);
                setPaymentOption();
            } else {
                walletMessage.setVisibility(View.GONE);
                remainAmount.setVisibility(View.GONE);
                ivPayWithPaytm.setVisibility(View.GONE);
                tvPay.setVisibility(View.VISIBLE);
            }
        } else {
            cbWallet.setChecked(false);
            remainAmount.setVisibility(View.GONE);
            walletMessage.setVisibility(View.GONE);
            ivPayWithPaytm.setVisibility(View.VISIBLE);
            tvPay.setVisibility(View.GONE);
            setPaymentOption();
        }
    }


    /**
     * Method to hit the buy order api
     */
    private void hitBuyProductServiceAPi() {
        isLoading = true;
        if (paymentMode.equals("1")) {
            AppUtils.getInstance().setButtonLoaderAnimation(PaymentActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
        }
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, productDetails.getId());
        params.put(Constants.NetworkConstant.PARAM_QUANTITY, productDetails.getQuantity());
        params.put(Constants.NetworkConstant.PARAM_ACTUAL_AMOUNT, String.format(Locale.ENGLISH, "%.2f", finalPrice));
        params.put(Constants.NetworkConstant.PARAM_DELIVERED_BY, productDetails.getUserType());
        params.put(Constants.NetworkConstant.PARAM_DELIVERY_ADDRESS, deliveryAddress.getAddress());
        params.put(Constants.NetworkConstant.PARAM_SHIPPING_ID, deliveryAddress.getId());
        params.put(Constants.NetworkConstant.PARAM_LATITUDE, deliveryAddress.getLatitude());
        params.put(Constants.NetworkConstant.PARAM_LONGITUDE, deliveryAddress.getLongitude());
        params.put(Constants.NetworkConstant.PARAM_DISCOUNT, productDetails.getDiscount());
        params.put(Constants.NetworkConstant.PARAM_PRODUCT_TYPE, productDetails.getProductType());
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, productDetails.getUserType().equals("2") || productDetails.getIsShared().equals("1") ? productDetails.getUserId() : "");
        params.put(Constants.NetworkConstant.PARAM_PAYMENT_MODE, paymentMode);
        params.put(Constants.NetworkConstant.PARAM_IS_SHARED, productDetails.getIsShared());
        params.put(Constants.NetworkConstant.PARAM_HOME_DELIVERY, productDetails.getHomeDelivery());
        params.put(Constants.NetworkConstant.PARAM_FINAL_NEGOTIATED_AMOUNT, String.format(Locale.ENGLISH, "%.2f",(Double.parseDouble(productDetails.getSellingPrice()) * Double.parseDouble(productDetails.getQuantity()))));
//        params.put(Constants.NetworkConstant.PARAM_SLOT_ID, productDetails.getSelectedSlots());
        params.put(Constants.NetworkConstant.PARAM_SLOT_ID_ARR, new Gson().toJson(getSlotIdArray()));
        params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());

        //pay with wallet
        double walletPoints = walletAmount;
        double finalPrice = this.finalPrice;
        if (finalPrice < walletPoints) {
            walletPoints = finalPrice;
        }
        params.put(Constants.NetworkConstant.PARAM_WALLET_AMOUNT, cbWallet.isChecked() ? String.format(Locale.ENGLISH, "%.2f", walletPoints) : "0.00");

        //params for paytm
        params.put(Constants.NetworkConstant.PARAM_ORDER_NO, orderNo);
        params.put(Constants.NetworkConstant.PARAM_CHECKSUM, checksum);
        params.put(Constants.NetworkConstant.PARAM_TRANSACTION_ID, transactionId);

        //params for alipay
        params.put(Constants.NetworkConstant.PARAM_PAYMENT_TYPE, cbWallet.isChecked() && !paymentType.contains("4") ? paymentType + ",4" : paymentType);
//        params.put(Constants.NetworkConstant.PARAM_PAYMENT_TYPE, paymentType);
        params.put(Constants.NetworkConstant.PARAM_PAYMENT_SOURCE_TOKEN, sourceToken);
        params.put(Constants.NetworkConstant.PARAM_CURRENCY, productDetails.getCurrencyCode());

//        params.put(Constants.NetworkConstant.PARAM_PAYMENT_DATE, productDetails.getUserType().equals("2") ? productDetails.getUserId() : "");
        Call<ResponseBody> call = apiInterface.hitCreateOrderApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_ORDER);
    }


    /**
     * Method to hit the buy hunt api
     */
    private void hitBuyHuntAPi() {
        isLoading = true;
        if (paymentMode.equals("1")) {
            AppUtils.getInstance().setButtonLoaderAnimation(PaymentActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
        }
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntDeal.getId());
        params.put(Constants.NetworkConstant.PARAM_QUANTITY, "1");
        params.put(Constants.NetworkConstant.PARAM_ACTUAL_AMOUNT, String.format(Locale.ENGLISH, "%.2f", finalPrice));
        params.put(Constants.NetworkConstant.PARAM_DELIVERED_BY, huntDeal.getUserType());
        params.put(Constants.NetworkConstant.PARAM_DELIVERY_ADDRESS, huntDeal.getAddress());
        params.put(Constants.NetworkConstant.PARAM_LATITUDE, huntDeal.getLatitude());
        params.put(Constants.NetworkConstant.PARAM_LONGITUDE, huntDeal.getLongitude());
        params.put(Constants.NetworkConstant.PARAM_DISCOUNT, "0");
        params.put(Constants.NetworkConstant.PARAM_PRODUCT_TYPE, huntDeal.getProductType());
        params.put(Constants.NetworkConstant.PARAM_BUDDY_ID, buddyId);
        params.put(Constants.NetworkConstant.PARAM_PAYMENT_MODE, paymentMode);
        params.put(Constants.NetworkConstant.PARAM_IS_SHARED, "0");
        params.put(Constants.NetworkConstant.PARAM_HOME_DELIVERY, "1");
        params.put(Constants.NetworkConstant.PARAM_SLOT_ID_ARR, new Gson().toJson(getHuntSlotIdArray()));
        params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());
        params.put(Constants.NetworkConstant.PARAM_FINAL_NEGOTIATED_AMOUNT, String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(huntDeal.getPrice())));

        //pay with wallet
        double walletPoints = walletAmount;
        double finalPrice = this.finalPrice;
        if (finalPrice < walletPoints) {
            walletPoints = finalPrice;
        }
        params.put(Constants.NetworkConstant.PARAM_WALLET_AMOUNT, cbWallet.isChecked() ? String.format(Locale.ENGLISH, "%.2f", walletPoints) : "0.00");


        //params for paytm
        params.put(Constants.NetworkConstant.PARAM_ORDER_NO, orderNo);
        params.put(Constants.NetworkConstant.PARAM_CHECKSUM, checksum);
        params.put(Constants.NetworkConstant.PARAM_TRANSACTION_ID, transactionId);

        //params for alipay
        params.put(Constants.NetworkConstant.PARAM_PAYMENT_TYPE, cbWallet.isChecked() && !paymentType.contains("4") ? paymentType + ",4" : paymentType);
//        params.put(Constants.NetworkConstant.PARAM_PAYMENT_TYPE, paymentType);
        params.put(Constants.NetworkConstant.PARAM_PAYMENT_SOURCE_TOKEN, sourceToken);
        params.put(Constants.NetworkConstant.PARAM_CURRENCY, huntDeal.getCurrencyCode());

        Call<ResponseBody> call = apiInterface.hitCreateOrderApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_ORDER);
    }


    /**
     * method to get the slot id array
     */
    private ArrayList<HashMap<String, String>> getSlotIdArray() {
        ArrayList<HashMap<String, String>> slotList = new ArrayList<>();
        if (productDetails.getSelectedSlots() != null && !productDetails.getSelectedSlots().equals("")) {
            String[] slotIdArray = productDetails.getSelectedSlots().split(",");
            if (productDetails.getSelectedDates() != null) {
                for (String id : slotIdArray) {
                    for (String date : productDetails.getSelectedDates()) {
                        HashMap<String, String> slotMap = new HashMap<>();
                        slotMap.put(Constants.NetworkConstant.PARAM_SLOT_ID, id);
                        slotMap.put(Constants.NetworkConstant.PARAM_DATE, date);
                        slotList.add(slotMap);
                    }
                }
            }
        }
        return slotList;
    }

    /**
     * method to get the slot id array
     */
    private ArrayList<HashMap<String, String>> getHuntSlotIdArray() {
        ArrayList<HashMap<String, String>> slotList = new ArrayList<>();
        if (huntDeal.getSelectedSlots() != null && !huntDeal.getSelectedSlots().equals("")) {
            String[] slotIdArray = huntDeal.getSelectedSlots().split(",");
            if (huntDeal.getSelectedDates() != null) {
                for (String id : slotIdArray) {
                    for (String date : huntDeal.getSelectedDates()) {
                        HashMap<String, String> slotMap = new HashMap<>();
                        slotMap.put(Constants.NetworkConstant.PARAM_SLOT_ID, id);
                        slotMap.put(Constants.NetworkConstant.PARAM_DATE, date);
                        slotList.add(slotMap);
                    }
                }
            }
        }
        return slotList;
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_ORDER:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
//                        PurchaseOrderResponse purchaseOrderResponse = new Gson().fromJson(response, PurchaseOrderResponse.class);
//                        AppUtils.getInstance().showToast(this, getString(R.string.order_placed_successfully));
                      /*  Intent intent = new Intent(this, QRCodeActivity.class);
                        intent.putExtra(Constants.IntentConstant.ORDER_DETAILS, purchaseOrderResponse.getWalletDetail());
                        startActivity(intent);
*/
                        if (roomId != null && !roomId.equals(""))
                            FirebaseDatabaseQueries.getInstance().changeOfferStatus(roomId, huntDeal == null ? "0" : "4");
                        if (roomId != null && huntDeal != null) {
                            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.CHAT_ROOM_HUNT).child(huntDeal.getId())
                                    .child(FirebaseConstants.USER_ID).setValue(buddyId);
                        }
                        isLoading = false;
                        showSuccessDialog();
                        break;
                    default:
                        isLoading = false;
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
            case Constants.NetworkConstant.REQUEST_CHECKSUM:
                isLoading = false;
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        ChecksumResponse checksumResponse = new Gson().fromJson(response, ChecksumResponse.class);
                        orderNo = checksumResponse.getResult().getOrderNumber();
                        paytmMerchantId = checksumResponse.getResult().getPaytmMerchantId();
                        customerId = checksumResponse.getResult().getCustomerId();
                        checksum = checksumResponse.getResult().getChecksum();
                        openPaytmGateway();
                        break;
                    default:
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
            case Constants.NetworkConstant.REQUEST_BUDDY:
                isLoading = false;
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        WalletResponse walletResponse = new Gson().fromJson(response, WalletResponse.class);
                        if (walletResponse.getWalletDetail() != null && walletResponse.getWalletDetail().size() > 0
                                && !walletResponse.getWalletDetail().get(0).getPoints().equals("")) {
                            walletAmount = Double.parseDouble(walletResponse.getWalletDetail().get(0).getPoints());
                            if (walletAmount > 0) {
                                cbWallet.setClickable(true);
                                cbWallet.setEnabled(true);
                            } else {
                                cbWallet.setClickable(false);
                                cbWallet.setEnabled(false);
                            }
                        }
                        updateWallet();
                        break;
                    case Constants.NetworkConstant.NO_DATA:
                        updateWallet();
                        break;
                    default:
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
            default:
                isLoading = false;
                try {
                    AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);

    }

    /**
     * method to show order placed success dialog
     */
    private void showSuccessDialog() {
        String userType = productDetails != null ? productDetails.getUserType() : (huntDeal != null ? huntDeal.getUserType() : "");
        String productSummery = getString(R.string.order_placed_summary) + " " +
                (userType.equals("1") && !productDetails.getIsShared().equals("1") ? getString(R.string.merchant) : getString(R.string.buddy))
                + getString(R.string.order_placed_summary2);
        CustomDialogForMessage messageDialog = new CustomDialogForMessage(this, getString(R.string.order_placed), productSummery,
                getString(R.string.ok), false, () -> {
            Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
            intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.QR_CODE);
            AppUtils.getInstance().openNewActivity(PaymentActivity.this, intent);
            //                finish();
        });
        messageDialog.show();
    }


    /**
     * function to start paytm payment gateway
     */
    private void openPaytmGateway() {
        HashMap<String, String> paramMap = new HashMap<String,String>();
        paramMap.put( "MID" , paytmMerchantId);
        paramMap.put( "ORDER_ID" , orderNo);
        paramMap.put( "CUST_ID" , customerId);
//        paramMap.put( "MOBILE_NO" , AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.PHONE_NO));
        paramMap.put( "EMAIL" , AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.EMAIL));
        paramMap.put( "CHANNEL_ID" , "WAP");
        paramMap.put( "CHECKSUMHASH" , checksum);
        if (productDetails != null) {
            double price = Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", finalPrice));
            double ammount = cbWallet.isChecked() ? price - walletAmount : price;
            paramMap.put( "TXN_AMOUNT", String.format(Locale.ENGLISH, "%.2f", ammount));
        }
        if (huntDeal != null) {
            double ammount = cbWallet.isChecked() ? finalPrice - walletAmount : finalPrice;
            paramMap.put( "TXN_AMOUNT", String.format(Locale.ENGLISH, "%.2f", ammount));
        }

        //staging server
//        paramMap.put( "WEBSITE" , "APPSTAGING");
//        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
//        paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + orderNo);
//        PaytmPGService Service = PaytmPGService.getStagingService();

        //production server
        paramMap.put( "WEBSITE" , "APPPROD");
        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail92");
        paramMap.put( "CALLBACK_URL", "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderNo);
        PaytmPGService Service = PaytmPGService.getProductionService();

        PaytmOrder Order = new PaytmOrder(paramMap);


        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
            /*Call Backs*/
            public void someUIErrorOccurred(String inErrorMessage) {
                AppUtils.getInstance().showToast(PaymentActivity.this, "UI Error ");
            }
            public void onTransactionResponse(Bundle inResponse) {
                AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.SHOPPER, "Payment Transaction response " + inResponse.toString());
                String status = inResponse.getString("STATUS", "");
                if (status.equals("TXN_SUCCESS")) {
                    transactionId = inResponse.getString("TXNID");
                    newChecksum = inResponse.getString("CHECKSUMHASH");
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> {
                        if (productDetails != null) {
                            hitBuyProductServiceAPi();
                        } else if (huntDeal != null) {
                            hitBuyHuntAPi();
                        } else {
                            AppUtils.getInstance().showToast(PaymentActivity.this, getString(R.string.unable_to_make_payment));
                        }
                    }, 1000);
                } else {
                    AppUtils.getInstance().showToast(PaymentActivity.this, "Transaction fail");
//                    AppUtils.getInstance().showToast(PaymentActivity.this, inResponse.getString("RESPMSG"));
                }

            }
            public void networkNotAvailable() {
                AppUtils.getInstance().showToast(PaymentActivity.this, "Network connection error: Check your internet connectivity");
            }
            public void clientAuthenticationFailed(String inErrorMessage) {
                AppUtils.getInstance().showToast(PaymentActivity.this, "Authentication failed: Server error");
            }
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                AppUtils.getInstance().showToast(PaymentActivity.this, "Unable to load webpage ");
            }
            public void onBackPressedCancelTransaction() {
                AppUtils.getInstance().showToast(PaymentActivity.this, "Transaction cancelled");
            }
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                AppUtils.getInstance().showToast(PaymentActivity.this, "Transaction Cancelled");
            }
        });
    }


    /**
     * Method to hit the Generate Checksum Api
     */
    private void hitGenerateChecksumApiForPaytm() {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        if (productDetails != null) params.put(Constants.NetworkConstant.PARAM_DEAL_ID, productDetails.getId());
        if (huntDeal != null) params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntDeal.getId());
        if (productDetails != null) {
            double price = Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", finalPrice));
            double ammount = cbWallet.isChecked() ? price - walletAmount : price;
            params.put(Constants.NetworkConstant.PARAM_ACTUAL_AMOUNT, String.format(Locale.ENGLISH, "%.2f", ammount));
        }
        if (huntDeal != null) {
            double ammount = cbWallet.isChecked() ? finalPrice - walletAmount : finalPrice;
            params.put(Constants.NetworkConstant.PARAM_ACTUAL_AMOUNT, String.format(Locale.ENGLISH, "%.2f", ammount));
        }
        Call<ResponseBody> call = apiInterface.hitGenerateChecksumApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_CHECKSUM);

    }

    /**
     * method to payment using alipay
     */
    private void hitAlipayPayment() {
        double ammount = 0.00;
        if (productDetails != null){
            double price = Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", finalPrice));
            ammount = cbWallet.isChecked() ? price - walletAmount : price;
        }
        if (huntDeal != null){
            ammount = cbWallet.isChecked() ? finalPrice - walletAmount : finalPrice;
        }
        SourceParams alipayParams = SourceParams.createAlipaySingleUseParams(
                (long) (Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", ammount)) * 100), // Amount is a long int in the lowest denomination.
//                "HKD",
                productDetails == null ? huntDeal == null ? "" : huntDeal.getCurrencyCode() : productDetails.getCurrencyCode(),
                AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.FIRST_NAME) + " "
                        + AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LAST_NAME), // customer name
                AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.EMAIL), // customer email
                "shopoholic://alipay"); // a redirect address to get the user back into your app
        Stripe stripe = new Stripe(this, publishableKey);
        progressBar.setVisibility(View.VISIBLE);
        stripe.createSource(alipayParams, new SourceCallback() {
            @Override
            public void onError(Exception error) {
                progressBar.setVisibility(View.GONE);
                AppUtils.getInstance().showToast(PaymentActivity.this, error.toString());
            }

            @Override
            public void onSuccess(Source source) {
                progressBar.setVisibility(View.GONE);
                sourceToken = source.getId();
                if (source.getStatus().equalsIgnoreCase("pending")) {
                    String redirectUrl = source.getRedirect().getUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(redirectUrl));
                    startActivity(intent);
                    alipay = true;
                }
            }
        });

    }

    /**
     * method to payment using stripe
     */
    private void hitStripePayment() {
        Card card = new Card("4242-4242-4242-4242", 12, 2020, "123");
        SourceParams cardSourceParams = SourceParams.createCardParams(card);

        // The asynchronous way to do it. Call this method on the main thread.
        Stripe stripe = new Stripe(this, publishableKey);

        progressBar.setVisibility(View.VISIBLE);
        stripe.createSource(cardSourceParams, new SourceCallback() {
            @Override
            public void onSuccess(Source source) {
                // Store the source somewhere, use it, etc
//                AppUtils.getInstance().showToast(PaymentActivity.this, source.getStatus());
                String cardSourceId = source.getId();
                SourceParams threeDParams = SourceParams.createThreeDSecureParams(
                        1000L, // some price: this represents 10.00 EUR
                        "MOP", // a currency
                        "shopoholic://alipay", // your redirect
                        cardSourceId);

                // Remember not to call this method on the UI thread
                stripe.createSource(threeDParams, new SourceCallback() {
                    @Override
                    public void onSuccess(Source source) {
//                        AppUtils.getInstance().showToast(PaymentActivity.this, source.getStatus());
                        progressBar.setVisibility(View.GONE);
                        if (AppUtils.getInstance().isInternetAvailable(PaymentActivity.this)) {
                            if (productDetails != null) {
                                hitBuyProductServiceAPi();
                            } else if (huntDeal != null){
                                hitBuyHuntAPi();
                            } else {
                                AppUtils.getInstance().showToast(PaymentActivity.this, getString(R.string.unable_to_make_payment));
                            }
                        }
                    }

                    @Override
                    public void onError(Exception error) {
                        // handle the error
                        progressBar.setVisibility(View.GONE);
                        AppUtils.getInstance().showToast(PaymentActivity.this, error.toString());
                    }
                });
            }

            @Override
            public void onError(Exception error) {
                // Tell the user that something went wrong
                progressBar.setVisibility(View.GONE);
                AppUtils.getInstance().showToast(PaymentActivity.this, error.toString());
            }
        });
    }


    @Override
    public void setLocation(Location mCurrentLocation) {
        location.disconnect();
        if (mCurrentLocation == null)
            return;
        latitude = mCurrentLocation.getLatitude();
        longitude = mCurrentLocation.getLongitude();
    }

    /*
    function to set the payment option
     */
    private void setPaymentOption() {
        currencyCode = productDetails == null ? (huntDeal == null ? "AED" : huntDeal.getCurrencyCode()) : productDetails.getCurrencyCode();
        switch (currencyCode) {
            case "INR":
                ivPayWithPaytm.setImageResource(R.drawable.ic_pay_with_paytm);
//                ivPayWithPaytm.setImageResource(R.drawable.ic_pay_with_alipay);
//                ivPayWithPaytm.setVisibility(View.GONE);
//                llCardPayment.setVisibility(View.VISIBLE);
                break;
            case "HKD":
            case "SGD":
//                ivPayWithPaytm.setImageResource(R.drawable.ic_pay_with_paytm);
                ivPayWithPaytm.setImageResource(R.drawable.ic_pay_with_alipay);
                break;
            case "MOP":
            case "AED":
            default:
                ivPayWithPaytm.setVisibility(View.GONE);
                llCardPayment.setVisibility(View.GONE);
                tvPay.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    public void setAddress(Address address) {}
    @Override
    public void setLatAndLong(Address location) {}
    @Override
    public void disconnect() {}


    /**
     * method to get wallet amount
     */
    private void hitWalletDetailsApi() {
        progress.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_COUNT, "0");
        params.put(Constants.NetworkConstant.TYPE, "4");
        if (productDetails != null)
            params.put(Constants.NetworkConstant.PARAM_CURRENCY_CODE, productDetails.getCurrencyCode());
        Call<ResponseBody> call = apiInterface.hitWalletDetailsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_BUDDY);
    }
}
