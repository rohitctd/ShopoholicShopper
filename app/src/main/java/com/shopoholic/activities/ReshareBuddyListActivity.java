package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.shopoholic.adapters.HuntBuddyAdapter;
import com.shopoholic.adapters.ReshareBuddyAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.activities.FullScreenImageSliderActivity;
import com.shopoholic.firebasechat.activities.SingleChatActivity;
import com.shopoholic.firebasechat.interfaces.FirebaseUserListener;
import com.shopoholic.firebasechat.models.HuntDeal;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.models.huntbuddyresponse.HuntBuddyResponse;
import com.shopoholic.models.productservicedetailsresponse.BuddyArr;
import com.shopoholic.models.productservicedetailsresponse.Result;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.models.productservicedetailsresponse.SlotSelectedDate;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

public class ReshareBuddyListActivity extends BaseActivity {


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
    private ReshareBuddyAdapter huntBuddyAdapter;
    private ArrayList<BuddyArr> buddiesList;
    private int count = 0;
    private LinearLayoutManager linearLayoutManager;
    private Result productDetails;
    private ArrayList<String> slotsDayList;
    private ArrayList<ServiceSlot> selectedSlotsArray;

    public ReshareBuddyListActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_and_review);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.buddy_listing);
        initVariable();
        setAdapter();
    }

    @OnClick({R.id.iv_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
        }
    }

    /**
     * method to initialize the variables
     */
    private void initVariable() {
        swipeRefreshLayout.setEnabled(false);
        ivBack.setVisibility(View.VISIBLE);
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);

        if (getIntent() != null && getIntent().getExtras() != null) {
            productDetails = (Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.PRODUCT_DETAILS);
            buddiesList = (ArrayList<BuddyArr>) getIntent().getExtras().getSerializable(Constants.IntentConstant.BUDDY);
            selectedSlotsArray = (ArrayList<ServiceSlot>) getIntent().getExtras().getSerializable(Constants.IntentConstant.SLOTS);
            slotsDayList = getIntent().getExtras().getStringArrayList(Constants.IntentConstant.SELECTED_DAYS_LIST);
        }

        huntBuddyAdapter = new ReshareBuddyAdapter(this, buddiesList, (position, view) -> {
            switch (view.getId()) {
                case R.id.btn_contact:
                    if (productDetails.getProductType().equals("1") && Calendar.getInstance().getTime()
                            .before(AppUtils.getInstance().getDateFromString(productDetails.getDealStartTime(), SERVICE_DATE_FORMAT))) {
                        AppUtils.getInstance().showToast(this, getString(R.string.deal_not_start_yet));
                    } else {
                        //todo ... if shared by buddy
                        productDetails.setIsShared("1");
                        productDetails.setUserId(buddiesList.get(position).getBuddyId());
                        checkForProduct(2);
                    }
                    break;
                case R.id.btn_buy:
                    if (productDetails.getProductType().equals("1") && Calendar.getInstance().getTime()
                            .before(AppUtils.getInstance().getDateFromString(productDetails.getDealStartTime(), SERVICE_DATE_FORMAT))) {
                        AppUtils.getInstance().showToast(this, getString(R.string.deal_not_start_yet));
                    } else {
                        // todo......  if shared by buddy
                        productDetails.setIsShared("1");
                        productDetails.setUserId(buddiesList.get(position).getBuddyId());
                        productDetails.setDileveryCharge(buddiesList.get(position).getDeliveryCharge());
                        checkForProduct(1);
                    }
                    break;
                case R.id.rl_post_row:
                    break;
            }

        });
    }


    /**
     * function to open chat activity
     */
    public void openChatActivity() {
        FirebaseDatabaseQueries.getInstance().getUser(productDetails.getUserId(), new FirebaseUserListener() {
            @Override
            public void getUser(UserBean user) {
                Intent intent = new Intent(ReshareBuddyListActivity.this, SingleChatActivity.class);
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
     * method to set adapter
     */
    private void setAdapter() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(huntBuddyAdapter);
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
}