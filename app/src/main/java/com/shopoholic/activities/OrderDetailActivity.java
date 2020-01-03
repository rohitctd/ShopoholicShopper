package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dnitinverma.locationlibrary.RCLocation;
import com.dnitinverma.locationlibrary.interfaces.LocationsCallback;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForMessage;
import com.shopoholic.dialogs.CustomDialogForSetDelivery;
import com.shopoholic.interfaces.MakeOfferDialogCallback;
import com.shopoholic.livetracking.ShopperMapsActivity;
import com.shopoholic.models.NotificationBean;
import com.shopoholic.models.orderlistdetailsresponse.OrdersDetailsResponse;
import com.shopoholic.models.orderlistdetailsresponse.Result;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;


public class OrderDetailActivity extends AppCompatActivity implements View.OnTouchListener, NetworkListener, LocationsCallback {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.tv_order_no)
    CustomTextView tvOrderNo;
    @BindView(R.id.tv_product_name)
    CustomTextView tvProductName;
    @BindView(R.id.tv_order_date)
    CustomTextView tvOrderDate;
    @BindView(R.id.tv_user_type)
    CustomTextView tvUserType;
    @BindView(R.id.tv_merchant_name)
    CustomTextView tvMerchantName;
    @BindView(R.id.tv_address)
    CustomTextView tvAddress;
    @BindView(R.id.deal_label)
    CustomTextView dealLabel;
    @BindView(R.id.iv_deal_image)
    ImageView ivDealImage;
    @BindView(R.id.ll_order)
    LinearLayout llOrder;
    @BindView(R.id.tv_track)
    CustomTextView tvTrack;
    @BindView(R.id.rl_track)
    RelativeLayout rlTrack;
    @BindView(R.id.iv_order_confirmed)
    ImageView ivOrderConfirmed;
    @BindView(R.id.tv_order_confirmed)
    CustomTextView tvOrderConfirmed;
    @BindView(R.id.tv_order_confirmed_date)
    CustomTextView tvOrderConfirmedDate;
    @BindView(R.id.iv_order_shipped)
    ImageView ivOrderShipped;
    @BindView(R.id.tv_order_shipped)
    CustomTextView tvOrderShipped;
    @BindView(R.id.tv_message_shipped)
    CustomTextView tvMessageShipped;
    @BindView(R.id.iv_order_out_of_delivery)
    ImageView ivOrderOutOfDelivery;
    @BindView(R.id.tv_order_out_of_delivery)
    CustomTextView tvOrderOutOfDelivery;
    @BindView(R.id.tv_out_for_delivery_status)
    CustomTextView tvOutForDeliveryStatus;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    @BindView(R.id.tv_time_slots)
    CustomTextView tvTimeSlots;
    @BindView(R.id.fbl_time_slots)
    FlexboxLayout fblTimeSlots;
    @BindView(R.id.rl_root_view)
    RelativeLayout rlRootView;
    @BindView(R.id.tv_quantity_lable)
    CustomTextView tvQuantityLable;
    @BindView(R.id.tv_quantity)
    CustomTextView tvQuantity;
    @BindView(R.id.tv_price)
    CustomTextView tvPrice;
    @BindView(R.id.ll_details)
    LinearLayout llDetails;
    @BindView(R.id.other_details)
    LinearLayout otherDetails;
    @BindView(R.id.rl_shipped)
    RelativeLayout rlShipped;
    @BindView(R.id.rl_ofd)
    RelativeLayout rlOfd;
    @BindView(R.id.iv_dates)
    ImageView ivDates;
    @BindView(R.id.tv_payment_mode)
    TextView tvPaymentMode;
    @BindView(R.id.tv_helpline_no)
    TextView tvHelplineNo;

    private Result order;
    private String orderId = "";
    private RCLocation location;
    private double latitude = 0.0, longitude = 0.0;
    private String currencyCode;

    private BroadcastReceiver orderUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                NotificationBean notificationBean = (NotificationBean) intent.getExtras().getSerializable(Constants.IntentConstant.NOTIFICATION);
                if (notificationBean != null && notificationBean.getOrderId() != null) {
                    if (order != null && order.getId() != null) {
                        if (notificationBean.getOrderId().equals(order.getId())) {
                            order.setOrderStatus(notificationBean.getOrderStatus());
                            if (order.getBuddyId().equals("") || order.getBuddyId().equals("0")) {
                                if (AppUtils.getInstance().isInternetAvailable(OrderDetailActivity.this)) {
                                    hitOrderDetailsApi();
                                } else {
                                    showRetryDialog();
                                }
                            }else {
                                setOrderDetail();
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(orderUpdateReceiver, new IntentFilter(Constants.IntentConstant.NOTIFICATION));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(orderUpdateReceiver);
        super.onStop();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        setViews();
//        initializeLocation();
//        getUserCurrencyCode();
        if (getIntent() != null && getIntent().getExtras() != null) {
            order = (Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.ORDER_DETAILS);
            orderId = getIntent().getExtras().getString(Constants.IntentConstant.ORDER_ID, "");
            if (order != null) {
                orderId = order.getId();
                if (!order.getOrderStatus().equals("1") && (order.getBuddyId().equals("") || order.getBuddyId().equals("0"))) {
                    if (AppUtils.getInstance().isInternetAvailable(OrderDetailActivity.this)) {
                        hitOrderDetailsApi();
                    } else {
                        showRetryDialog();
                    }
                }else {
                    setOrderDetail();
                }
            } else if (!orderId.equals("")) {
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    hitOrderDetailsApi();
                } else {
                    showRetryDialog();
                }
            }
        }

    }

    /**
     * function to get user currency code
     */
    private String getUserCurrencyCode() {
        String latitude = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LATITUDE);
        String longitude = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LONGITUDE);
        if ((latitude.equals("") || latitude.equals("0")) && (longitude.equals("") || longitude.equals("0"))) {
/*
            new AlertDialog.Builder(this, R.style.DatePickerTheme)
                    .setMessage(getString(R.string.provide_address_in_profile))
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                        // do nothing
                    })
                    .show();
*/
            return order.getCurrencyCode();
        }
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        currencyCode = AppUtils.getInstance().getCurrency(this, this.latitude, this.longitude).get(1);
        if (currencyCode.equals("")) currencyCode = order.getCurrencyCode();
        return currencyCode;
    }

    /*
    method to set order details
     */

    @SuppressLint("ClickableViewAccessibility")
    private void setOrderDetail() {
        rlRootView.setVisibility(View.VISIBLE);
        if (order.getUserType().equals("2")/* || order.getHomeDelivery().equals("2")*/) {
            tvUserType.setText(getString(R.string.buddy));
            tvMerchantName.setText(TextUtils.concat(order.getFirstName() + " " + order.getLastName()));
            /*String address = "";
            if (order.getBuddyAddress() != null) address += order.getBuddyAddress();
            tvAddress.setText(address.trim().equals("") ? getString(R.string.na) : address);*/
        } else {
            tvUserType.setText(getString(R.string.merchant));
            tvMerchantName.setText(TextUtils.concat(order.getFirstName()));
        }

        String address = "";
        if (order.getMerchantAddress() != null) address += order.getMerchantAddress();
        tvAddress.setText(address.trim().equals("") ? getString(R.string.na) : address);


        tvOrderNo.setText(order.getOrderNumber());

        if (!order.getDealName().equals("")) {
            dealLabel.setVisibility(View.VISIBLE);
            tvProductName.setVisibility(View.VISIBLE);
            tvProductName.setText(order.getDealName());
        }else {
            dealLabel.setVisibility(View.GONE);
            tvProductName.setVisibility(View.GONE);
        }
        tvQuantity.setText(order.getQuantity());

        String currency = getString(order.getCurrency().equals("2") ? R.string.rupees : order.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
        tvPrice.setText(TextUtils.concat(order.getCurrencySymbol() + order.getActualAmount()));

        String orderDate = AppUtils.getInstance().formatDate(order.getOrderDate(), SERVICE_DATE_FORMAT, DATE_FORMAT);
        tvOrderDate.setText(orderDate);

        String paymentType = order.getPaymentType();
        if (paymentType.contains(",")) {
            String[] payment = paymentType.split(",");
            String mode = getString(R.string.wallet_and) + " ";
            if (payment.length > 0 && !payment[0].equals("4")) {
//                mode += getString(payment[0].equals("1") ? R.string.alipay : R.string.paytm);
                mode += getString(R.string.online);
            } else if (payment.length > 1 && !payment[1].equals("4")){
//                mode += getString(payment[1].equals("1") ? R.string.alipay : R.string.paytm);
                mode += getString(R.string.online);
            }
            tvPaymentMode.setText(mode);
        }else {
//            String mode = getString(paymentType.equals("1") ? R.string.alipay :
//                    paymentType.equals("2") ? R.string.paytm :
//                            paymentType.equals("3") ? R.string.wallet : R.string.cod);
            String mode = getString(paymentType.equals("1") ? R.string.online :
                    paymentType.equals("2") ? R.string.online :
                            paymentType.equals("3") ? R.string.cod : R.string.wallet);
            tvPaymentMode.setText(mode);
        }

        tvOrderOutOfDelivery.setText(R.string.out_for_delivery);
        if (order.getOrderStatus().equalsIgnoreCase("1")) {
            ivOrderConfirmed.setImageResource(R.drawable.ic_order_confirmed);
            tvOrderConfirmedDate.setVisibility(View.GONE);
            tvOrderConfirmed.setText(R.string.pending);
        }

        if (order.getOrderStatus().equalsIgnoreCase("2")) {
            ivOrderConfirmed.setImageResource(R.drawable.ic_order_confirmed);
            tvOrderConfirmedDate.setVisibility(View.VISIBLE);
            tvOrderConfirmed.setText(R.string.order_confirmed);
            tvOrderConfirmedDate.setText(orderDate);
        }


        if (order.getOrderStatus().equalsIgnoreCase("3")) {
            ivOrderConfirmed.setImageResource(R.drawable.ic_order_confirmed);
            tvOrderConfirmedDate.setVisibility(View.VISIBLE);
            tvOrderConfirmed.setText(R.string.order_confirmed);
            ivOrderShipped.setImageResource(R.drawable.ic_order_confirmed);
            tvOrderConfirmedDate.setText(orderDate);
            tvMessageShipped.setVisibility(View.GONE);
        } else {
            ivOrderShipped.setImageResource(R.drawable.ic_order_shipped);
        }


        if (order.getOrderStatus().equalsIgnoreCase("4")) {
            tvTrack.setBackgroundResource(R.drawable.ic_button);
            ivOrderConfirmed.setImageResource(R.drawable.ic_order_confirmed);
            tvOrderConfirmedDate.setVisibility(View.VISIBLE);
            tvOrderConfirmed.setText(R.string.order_confirmed);
            ivOrderShipped.setImageResource(R.drawable.ic_order_confirmed);
            ivOrderOutOfDelivery.setImageResource(R.drawable.ic_order_confirmed);
            tvOrderConfirmedDate.setText(orderDate);
            tvMessageShipped.setVisibility(View.GONE);
        } else {
            ivOrderOutOfDelivery.setImageResource(R.drawable.ic_order_shipped);
            tvTrack.setBackgroundResource(R.drawable.ic_btn_inactive_state);
            tvTrack.setTextColor(ContextCompat.getColor(this, R.color.colorButtonDeselectText));
        }


        if (order.getOrderStatus().equalsIgnoreCase("5")) {
            tvTrack.setBackgroundResource(R.drawable.ic_btn_inactive_state);
            tvTrack.setTextColor(ContextCompat.getColor(this, R.color.colorButtonDeselectText));
            ivOrderConfirmed.setImageResource(R.drawable.ic_order_confirmed);
            tvOrderConfirmedDate.setVisibility(View.VISIBLE);
            tvOrderConfirmedDate.setText(orderDate);
            tvOrderConfirmed.setText(R.string.order_confirmed);
            ivOrderShipped.setImageResource(R.drawable.ic_order_confirmed);
            ivOrderOutOfDelivery.setImageResource(R.drawable.ic_order_confirmed);
            tvOrderOutOfDelivery.setText(R.string.delivered);
            String deliveryDate = AppUtils.getInstance().formatDate(order.getDileveredDate(), SERVICE_DATE_FORMAT, DATE_FORMAT);
            tvOutForDeliveryStatus.setText(deliveryDate);
            tvMessageShipped.setVisibility(View.GONE);
        }

        if (!order.getDileveryDate().equals("0000-00-00") && !order.getOrderStatus().equalsIgnoreCase("5")) {
            String deliveryDate = AppUtils.getInstance().formatDate(order.getDileveryDate(), SERVICE_DATE_FORMAT, DATE_FORMAT);
            tvOutForDeliveryStatus.setText(TextUtils.concat(getString(R.string.your_order_will_be_out_for_delivery_on_date) + " " + deliveryDate));
        }

        if (order.getBuddyId().equalsIgnoreCase("0") && order.getUserType().equals("1")) {
            tvOrderOutOfDelivery.setText(R.string.biddy_assigned);
            if (!order.getHomeDelivery().equals("1") && !order.getIsShared().equals("1")) {
                tvOrderOutOfDelivery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.places_ic_search, 0);
                tvOrderOutOfDelivery.setOnTouchListener(this);
            } else {
                tvOrderOutOfDelivery.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            tvOutForDeliveryStatus.setText(R.string.biddy_not_assigned);
            tvTrack.setBackgroundResource(R.drawable.ic_btn_inactive_state);
            tvTrack.setTextColor(ContextCompat.getColor(this, R.color.colorButtonDeselectText));

            if (order.getOrderStatus().equalsIgnoreCase("1")) {
                ivOrderConfirmed.setImageResource(R.drawable.ic_order_confirmed);
                tvOrderConfirmedDate.setVisibility(View.GONE);
                tvOrderConfirmed.setText(R.string.pending);
            }
            if (order.getOrderStatus().equalsIgnoreCase("2")) {
                ivOrderConfirmed.setImageResource(R.drawable.ic_order_confirmed);
                tvOrderConfirmedDate.setVisibility(View.VISIBLE);
                tvOrderConfirmed.setText(R.string.order_confirmed);
                tvOrderConfirmedDate.setText(orderDate);
            }
            if (order.getOrderStatus().equalsIgnoreCase("3")) {
                ivOrderConfirmed.setImageResource(R.drawable.ic_order_confirmed);
                tvOrderConfirmedDate.setVisibility(View.VISIBLE);
                tvOrderConfirmed.setText(R.string.order_confirmed);
                ivOrderShipped.setImageResource(R.drawable.ic_order_confirmed);
                tvOrderConfirmedDate.setText(orderDate);
                tvMessageShipped.setVisibility(View.GONE);
            }
            if (order.getOrderStatus().equalsIgnoreCase("4")) {
                ivOrderConfirmed.setImageResource(R.drawable.ic_order_confirmed);
                tvOrderConfirmedDate.setVisibility(View.VISIBLE);
                tvOrderConfirmed.setText(R.string.order_confirmed);
                ivOrderShipped.setImageResource(R.drawable.ic_order_confirmed);
                tvOrderConfirmedDate.setText(orderDate);
                tvMessageShipped.setVisibility(View.GONE);
            }
            if (order.getOrderStatus().equalsIgnoreCase("5")) {
                tvTrack.setBackgroundResource(R.drawable.ic_btn_inactive_state);
                tvTrack.setTextColor(ContextCompat.getColor(this, R.color.colorButtonDeselectText));
                ivOrderConfirmed.setImageResource(R.drawable.ic_order_confirmed);
                tvOrderConfirmedDate.setVisibility(View.VISIBLE);
                tvOrderConfirmedDate.setText(orderDate);
                tvOrderConfirmed.setText(R.string.order_confirmed);
                ivOrderShipped.setImageResource(R.drawable.ic_order_confirmed);
                ivOrderOutOfDelivery.setImageResource(R.drawable.ic_order_confirmed);
                tvOrderOutOfDelivery.setText(R.string.delivered);
                String deliveryDate = AppUtils.getInstance().formatDate(order.getDileveredDate(), SERVICE_DATE_FORMAT, DATE_FORMAT);
                tvOutForDeliveryStatus.setText(deliveryDate);
                tvClear.setVisibility(View.GONE);
                tvMessageShipped.setVisibility(View.GONE);
            }
        }

        AppUtils.getInstance().setImages(this, order.getDealImage().split(",")[0], ivDealImage, 0, R.drawable.ic_placeholder);

        if (order.getProductType().equals("1")) {
            dealLabel.setText(getString(R.string.product));
            tvTimeSlots.setVisibility(View.GONE);
            ivDates.setVisibility(View.GONE);
            fblTimeSlots.setVisibility(View.GONE);
            tvQuantityLable.setVisibility(View.VISIBLE);
            tvQuantity.setVisibility(View.VISIBLE);
            if (order.getOrderStatus().equalsIgnoreCase("4")) {
                tvTrack.setBackgroundResource(R.drawable.ic_button);
                tvTrack.setTextColor(ContextCompat.getColor(this, R.color.colorLightWhite));
            } else {
                tvTrack.setBackgroundResource(R.drawable.ic_btn_inactive_state);
                tvTrack.setTextColor(ContextCompat.getColor(this, R.color.colorButtonDeselectText));
            }
        } else {
            dealLabel.setText(getString(R.string.service));
            tvTimeSlots.setVisibility(View.VISIBLE);
            ivDates.setVisibility(View.VISIBLE);
            fblTimeSlots.setVisibility(View.VISIBLE);
            rlShipped.setVisibility(View.GONE);
            if (order.getOrderStatus().equalsIgnoreCase("5")) {
                rlOfd.setVisibility(View.VISIBLE);
            } else {
                rlOfd.setVisibility(View.GONE);
            }
            tvQuantityLable.setVisibility(View.GONE);
            tvQuantity.setVisibility(View.GONE);

            fblTimeSlots.removeAllViews();
            for (int i = 0; i < order.getSlotArr().size(); i++) {
                String startDate = AppUtils.getInstance().formatDate(order.getSlotArr().get(i).getSlotStartDate(), SERVICE_DATE_FORMAT, DATE_FORMAT);
                String startTime = AppUtils.getInstance().formatDate(order.getSlotArr().get(i).getSlotStartTime(), "HH:mm:ss", "hh:mm a");
                String endDate = AppUtils.getInstance().formatDate(order.getSlotArr().get(i).getSlotEndDate(), SERVICE_DATE_FORMAT, DATE_FORMAT);
                String endTime = AppUtils.getInstance().formatDate(order.getSlotArr().get(i).getSlotEndTime(), "HH:mm:ss", "hh:mm a");
                if (order.getSlotArr().get(i).getAllDays().equals("1")) {
//                    fblTimeSlots.addView(timeSlotView(startDate + " - " + endDate + " (" + (getString(R.string.all_day_available)) + ")"));
                    fblTimeSlots.addView(timeSlotView(getString(R.string.all_day_available)));
                } else {
//                    fblTimeSlots.addView(timeSlotView(startDate + " " + startTime + " - " + endDate + " " + endTime));
                    fblTimeSlots.addView(timeSlotView(startTime + " - " + endTime));
                }
            }
            if (order.getOrderStatus().equalsIgnoreCase("2")) {
                tvTrack.setBackgroundResource(R.drawable.ic_button);
                tvTrack.setTextColor(ContextCompat.getColor(this, R.color.colorLightWhite));
            } else {
                tvTrack.setBackgroundResource(R.drawable.ic_btn_inactive_state);
                tvTrack.setTextColor(ContextCompat.getColor(this, R.color.colorButtonDeselectText));
            }
        }
        if (order.getOrderStatus().equalsIgnoreCase("3") ||
                order.getOrderStatus().equalsIgnoreCase("4") ||
                order.getOrderStatus().equalsIgnoreCase("5") ||
                order.getOrderStatus().equalsIgnoreCase("6") ||
                order.getOrderStatus().equalsIgnoreCase("7")) {
            tvClear.setVisibility(View.GONE);
        } else {
            tvClear.setVisibility(View.VISIBLE);
        }

        tvHelplineNo.setText(order.getTollFreeNumber());
    }


    /**
     * method to inflate time slots
     *
     * @param text
     * @return
     */
    private View timeSlotView(String text) {
        final View view = LayoutInflater.from(this).inflate(R.layout.row_request_slots, null);
        final CustomTextView tvSelection = view.findViewById(R.id.text_view);
        tvSelection.setText(text);
        view.setTag(text);
        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        Drawable drawable;
        if (tvOrderOutOfDelivery.getCompoundDrawables()[DRAWABLE_RIGHT] != null) drawable = tvOrderOutOfDelivery.getCompoundDrawables()[DRAWABLE_RIGHT];
        else drawable = tvOrderOutOfDelivery.getCompoundDrawables()[DRAWABLE_LEFT];
        if (drawable != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getRawX() >= (tvOrderOutOfDelivery.getRight() - drawable.getBounds().width())) {
//                 your action here
                if (order.getHomeDelivery() != null && !order.getHomeDelivery().equals("1") &&
                        ((order.getIsShared() != null && !order.getIsShared().equals("1")) ||
                                (order.getIsShareDeal() != null && !order.getIsShareDeal().equals("1")))) {
                    new CustomDialogForSetDelivery(this, order.getCurrencySymbol(), order.getShopperDeliveryCharge(),
                            order.getShopperDeliveryDate(), (price, currency, date, type) -> {
                        order.setShopperDeliveryCharge(price);
                        order.setShopperDeliveryDate(AppUtils.getInstance().formatDate(date, DATE_FORMAT, SERVICE_DATE_FORMAT));
                        startActivity(new Intent(this, CommonActivity.class)
                                .putExtra(Constants.IntentConstant.FRAGMENT_TYPE, 0)
                                .putExtra(Constants.NetworkConstant.PARAM_EXPECTED_DATE, date)
                                .putExtra(Constants.NetworkConstant.PARAM_PRICE, price)
                                .putExtra(Constants.IntentConstant.ORDER_ID, order.getId()));
                    }).show();
                }
                return true;
            }
        }
        return false;
    }


    /**
     * Method to set date in slash format
     *
     * @param createDate
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    private String getOrderDate(String createDate) {
        String today = "";
        try {
            DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = null;

            date1 = inputFormatter.parse(createDate);

            DateFormat requiredFormatter = new SimpleDateFormat("dd/MM/yyyy");
            today = requiredFormatter.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return today;

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

    /*
    method to set views in toolbar
     */

    private void setViews() {
        ivBack.setVisibility(View.VISIBLE);
        tvClear.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.your_order));
        tvClear.setText(getString(R.string.cancel));
        progressBar.setVisibility(View.GONE);
        gifProgress.setImageResource(R.drawable.shopholic_loader);

    }

    @OnClick({R.id.iv_back, R.id.tv_track, R.id.tv_clear, R.id.iv_dates, R.id.btn_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                Intent intent = new Intent();
                if (order != null) {
                    intent.putExtra(Constants.IntentConstant.ORDER_ID, order.getId());
                    intent.putExtra(Constants.IntentConstant.ORDER_STATUS, order.getOrderStatus());
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.tv_clear:
                if (currencyCode == null || currencyCode.equals("")) {
//                    AppUtils.getInstance().showToast(this, getString(R.string.unable_to_get_location));
                    currencyCode = getUserCurrencyCode();
//                    return;
                }
                if (currencyCode != null && !currencyCode.equals("")) {
                    new AlertDialog.Builder(this, R.style.DatePickerTheme).setTitle(getString(R.string.cancel_order))
                            .setMessage(getString(R.string.sure_to_cancel_order))
                            .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                                if (AppUtils.getInstance().isInternetAvailable(OrderDetailActivity.this)) {
                                    hitUpdateOrderStatusApi();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                                // do nothing
                            })
                            .show();
                }else {
                    if (latitude != 0.0 && longitude != 0.0) {
                        AppUtils.getInstance().showToast(this, getString(R.string.unable_to_get_location));
                    }
                }

                break;
            case R.id.tv_track:
//                if (order.getBuddyId().equalsIgnoreCase("0")) {
//                    startActivity(new Intent(this, CommonActivity.class)
//                            .putExtra(Constants.IntentConstant.FRAGMENT_TYPE, 0)
//                            .putExtra(Constants.IntentConstant.ORDER_ID, order.getId()));
//
//                } else
                if (order.getProductType().equals("2")) {
                    if (order.getOrderStatus().equalsIgnoreCase("2")) {
                        checkLocationPermission();
                    }
                } else {
                    if (order.getOrderStatus().equalsIgnoreCase("4")) {
                        checkLocationPermission();
                    }
                }
                break;
            case R.id.iv_dates:
//                createAlertDialog(new ArrayList<String>());
                createAlertDialog(order.getSelectedDateArr());
                break;
            case R.id.btn_call:
                checkCallPermission();
                break;
        }
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
        callIntent.setData(Uri.parse("tel:" + order.getTollFreeNumber()));
        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        }
    }


    /**
     * method to create alert dialog
     */
    public void createAlertDialog(List<String> list) {
        if (list == null) {
            AppUtils.getInstance().showToast(this, getString(R.string.no_slots_available));
            return;
        }
        List<String> aList = new ArrayList<>();
        for (String date : list) {
            if (!date.equals("0000-00-00")) {
                String formatDate = AppUtils.getInstance().formatDate(date, SERVICE_DATE_FORMAT, DATE_FORMAT);
                if (!aList.contains(formatDate)) aList.add(formatDate);
            }
        }
        //First Step: convert ArrayList to an Object array.
        Object[] objNames = aList.toArray();
        //Second Step: convert Object array to String array
        String[] dates = Arrays.copyOf(objNames, objNames.length, String[].class);

        AlertDialog.Builder alertBox = new AlertDialog.Builder(this)
                .setItems(dates, null);
        alertBox.show();
    }


    /**
     * method to check the location permission
     */
    public void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, Constants.IntentConstant.REQUEST_LOCATION);
            } else {
                openMapActivity();
            }
        } else {
            openMapActivity();
        }
    }

    /**
     * method to open map activity
     */
    private void openMapActivity() {
        LatLng latLng;
        if (order.getShopperLatitude().equals("") && order.getShopperLongitude().equals("")) {
            latLng = new LatLng(0.0, 0.0);
        } else {
            latLng = new LatLng(Double.parseDouble(order.getShopperLatitude()), Double.parseDouble(order.getShopperLongitude()));
        }
        LatLng latLngMerchant = null;
        if (!order.getMerchantId().equals(order.getBuddyId()) && !order.getMerchantLatitude().equals("") && !order.getMerchantLongitude().equals("")) {
            latLngMerchant = new LatLng(Double.parseDouble(order.getMerchantLatitude()), Double.parseDouble(order.getMerchantLongitude()));
        }
        startActivity(new Intent(this, ShopperMapsActivity.class)
                .putExtra(Constants.IntentConstant.MERCHANT_ID, order.getMerchantId())
                .putExtra(Constants.IntentConstant.BUDDY_ID, order.getBuddyId())
                .putExtra(Constants.IntentConstant.ORDER_ID, order.getId())
                .putExtra(Constants.IntentConstant.ORDER_DETAILS, order)
                .putExtra(Constants.IntentConstant.LOCATION, latLng)
                .putExtra(Constants.IntentConstant.LOCATION_MERCHANT, latLngMerchant)
                .putExtra(Constants.IntentConstant.BUDDY_IMAGE, order.getBuddyImage())
                .putExtra(Constants.IntentConstant.BUDDY_NAME, order.getBuddyFirstName() + " " + order.getBuddyLastName())
                .putExtra(Constants.IntentConstant.BUDDY_NUNBER, order.getBuddyCountryId() + order.getBuddyNumber())
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.IntentConstant.REQUEST_LOCATION:
                boolean isRationalGalleryStorage = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                        ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openMapActivity();
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_location_permission));
                }

                break;
            case Constants.IntentConstant.REQUEST_CALL:
                boolean isRationalCall = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                        ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callTollFree();
                } else if (isRationalCall) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_call_permission));
                }

                break;
        }
    }


    /**
     * This method hits the api to update order status
     */
    private void hitUpdateOrderStatusApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_ORDER_ID, order.getId());
        params.put(Constants.NetworkConstant.PARAM_STATUS, "6");
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        params.put(Constants.NetworkConstant.PARAM_USER_CURRENCY_CODE, currencyCode);
        params.put(Constants.NetworkConstant.PARAM_CURRENCY_CODE, order.getCurrencyCode());
        params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());
        if (order.getProductType().equals("2")) {
            String slotId = "";
            for (int i=0; i<order.getSlotArr().size(); i++) {
                if (i != 0) slotId += ",";
                slotId += order.getSlotArr().get(i).getId();
            }
            params.put(Constants.NetworkConstant.PARAM_SLOT_ID, slotId);
        }

        Call<ResponseBody> call = apiInterface.hitUpdateOrderStatusApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        Intent intent = new Intent();
                        order.setOrderStatus("6");
                        intent.putExtra(Constants.IntentConstant.ORDER_ID, order.getId());
                        intent.putExtra(Constants.IntentConstant.ORDER_STATUS, order.getOrderStatus());
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    default:
                        try {
                            AppUtils.getInstance().showToast(OrderDetailActivity.this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }


            @Override
            public void onError(String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
                AppUtils.getInstance().showToast(OrderDetailActivity.this, response);
            }

            @Override
            public void onFailure() {
                progressBar.setVisibility(View.GONE);
            }
        }, 1);
    }

    /**
     * method to hit order data api
     */
    private void hitOrderDetailsApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_ORDER_ID, orderId);
        Call<ResponseBody> call = apiInterface.hitOrderDetailsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_ORDER);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_ORDER:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        OrdersDetailsResponse ordersDetailsResponse = new Gson().fromJson(response, OrdersDetailsResponse.class);
                        order = ordersDetailsResponse.getResult();
                        order.setTollFreeNumber(ordersDetailsResponse.getTollFreeNumber());
                        if (order != null) {
                            setOrderDetail();
                        }
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
        showRetryDialog();
    }

    /**
     * Method to show retry dialog
     */
    private void showRetryDialog() {
        CustomDialogForMessage messageDialog = new CustomDialogForMessage(this, getString(R.string.error), getString(R.string.network_issue),
                getString(R.string.retry), false, () -> {
            if (AppUtils.getInstance().internetAvailable(OrderDetailActivity.this)) {
                hitOrderDetailsApi();
            } else {
                showRetryDialog();
            }
        });
        messageDialog.show();
    }

    @Override
    public void setLocation(Location mCurrentLocation) {
        location.disconnect();
        if (mCurrentLocation == null)
            return;
        latitude = mCurrentLocation.getLatitude();
        longitude = mCurrentLocation.getLongitude();
//        currencyCode = AppUtils.getInstance().getCurrency(this, latitude, longitude).get(1);
    }

    @Override
    public void setAddress(Address address) {}
    @Override
    public void setLatAndLong(Address location) {}
    @Override
    public void disconnect() {}
}
