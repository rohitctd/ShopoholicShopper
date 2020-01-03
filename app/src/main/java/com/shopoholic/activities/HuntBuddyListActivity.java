package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.HuntBuddyAdapter;
import com.shopoholic.animations.ExpandViewAnimation;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.activities.FullScreenImageSliderActivity;
import com.shopoholic.firebasechat.activities.SingleChatActivity;
import com.shopoholic.firebasechat.interfaces.FirebaseUserListener;
import com.shopoholic.firebasechat.models.HuntDeal;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.interfaces.ItemCallBack;
import com.shopoholic.models.huntbuddyresponse.HuntBuddyResponse;
import com.shopoholic.models.huntbuddyresponse.Result;
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

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class HuntBuddyListActivity extends BaseActivity implements NetworkListener {

    @BindView(R.id.iv_menu)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.rb_newest_first)
    RadioButton rbNewestFirst;
    @BindView(R.id.rb_oldest_first)
    RadioButton rbOldestFirst;
    @BindView(R.id.rb_price_high_low)
    RadioButton rbPriceHighLow;
    @BindView(R.id.rb_price_low_high)
    RadioButton rbPriceLowHigh;
    @BindView(R.id.rb_rating_high_low)
    RadioButton rbRatingHighLow;
    @BindView(R.id.rb_rating_low_high)
    RadioButton rbRatingLowHigh;
    @BindView(R.id.ll_sorting)
    LinearLayout llSorting;
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
    private HuntBuddyAdapter huntBuddyAdapter;
    private ArrayList<Result> buddiesList;
    private int count = 0;
    private boolean isMoreData;
    private boolean isLoading;
    private boolean isPagination;
    private LinearLayoutManager linearLayoutManager;
    private com.shopoholic.models.producthuntlistresponse.Result huntDeal;
    private int sort = 3;
    private int order = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_buddies);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.buddy_list);
        initVariable();
        setAdapter();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            if (huntDeal != null) {
                progressBar.setVisibility(View.VISIBLE);
                hitGetHuntBuddiesApi();
            }
        }

    }


    @OnClick({R.id.iv_menu, R.id.menu_right, R.id.rb_newest_first, R.id.rb_oldest_first, R.id.rb_price_high_low, R.id.rb_price_low_high, R.id.rb_rating_high_low, R.id.rb_rating_low_high})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.menu_right:
                if (llSorting.isShown()){
                    ExpandViewAnimation.getInstance().collapse(llSorting);
                }else {
                    ExpandViewAnimation.getInstance().expand(llSorting);
                }
                break;
            case R.id.rb_newest_first:
                if (sort != 3 || order != 2) {
                    resetAll();
                    rbNewestFirst.setChecked(true);
                    sort = 3;
                    order = 2;
                    progressBar.setVisibility(View.VISIBLE);
                    hitGetHuntBuddiesApi();
                }
                break;
            case R.id.rb_oldest_first:
                if (sort != 3 || order != 1) {
                    resetAll();
                    rbOldestFirst.setChecked(true);
                    sort = 3;
                    order = 1;
                    progressBar.setVisibility(View.VISIBLE);
                    hitGetHuntBuddiesApi();
                }
                break;
            case R.id.rb_price_high_low:
                if (sort != 1 || order != 2) {
                    resetAll();
                    rbPriceHighLow.setChecked(true);
                    sort = 1;
                    order = 2;
                    progressBar.setVisibility(View.VISIBLE);
                    hitGetHuntBuddiesApi();
                }
                break;
            case R.id.rb_price_low_high:
                if (sort != 1 || order != 1) {
                    resetAll();
                    rbPriceLowHigh.setChecked(true);
                    sort = 1;
                    order = 1;
                    progressBar.setVisibility(View.VISIBLE);
                    hitGetHuntBuddiesApi();
                }
                break;
            case R.id.rb_rating_high_low:
                if (sort != 2 || order != 2) {
                    resetAll();
                    rbRatingHighLow.setChecked(true);
                    sort = 2;
                    order = 2;
                    progressBar.setVisibility(View.VISIBLE);
                    hitGetHuntBuddiesApi();
                }
                break;
            case R.id.rb_rating_low_high:
                if (sort != 2 || order != 1) {
                    resetAll();
                    rbRatingLowHigh.setChecked(true);
                    sort = 2;
                    order = 1;
                    progressBar.setVisibility(View.VISIBLE);
                    hitGetHuntBuddiesApi();
                }
                break;
        }
    }

    /**
     * method to reset the sorting views
     */
    private void resetAll() {
        rbNewestFirst.setChecked(false);
        rbOldestFirst.setChecked(false);
        rbPriceHighLow.setChecked(false);
        rbPriceLowHigh.setChecked(false);
        rbRatingHighLow.setChecked(false);
        rbRatingLowHigh.setChecked(false);
    }

    /**
     * method to initialize the variables
     */
    private void initVariable() {
        swipeRefreshLayout.setEnabled(true);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.VISIBLE);
        menuRight.setImageResource(R.drawable.ic_shppers_order_reverse);
        gifProgress.setImageResource(R.drawable.shopholic_loader);

        if (getIntent() != null && getIntent().getExtras() != null) {
            huntDeal = (com.shopoholic.models.producthuntlistresponse.Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.HUNT_DEAL);
        }

        buddiesList = new ArrayList<>();
        huntBuddyAdapter = new HuntBuddyAdapter(this, buddiesList, (position, view) -> {
            ArrayList<String> slotsDayList = new ArrayList<>();
            switch (view.getId()) {
                case R.id.ll_item:
                    String[] imagesArray = buddiesList.get(position).getHuntImage().split(",");
                    ArrayList<String> productImagesList = new ArrayList<>();
                    for (String image : imagesArray) {
                        if (!image.trim().equals("")) productImagesList.add(image.trim());
                    }
                    if (productImagesList.size() > 0) {
                        Intent intent = new Intent(this, FullScreenImageSliderActivity.class);
                        intent.putExtra("imagelist", productImagesList);
                        intent.putExtra("from", getString(R.string.app_name));
                        intent.putExtra("pos", 0);
                        startActivity(intent);
                    }
                    break;
                case R.id.rl_post_row:
                    if (huntDeal != null) {
                        huntDeal.setPrice(buddiesList.get(position).getBidPrice());
                        huntDeal.setTaxArr(buddiesList.get(position).getTaxArr());
                        if (buddiesList.get(position).getServiceSlot() != null && buddiesList.get(position).getServiceSlot().size() > 0) {
                            StringBuilder selectedSlots = new StringBuilder();
                            for (int i = 0; i < buddiesList.get(position).getServiceSlot().size(); i++) {
                                if (i != 0) selectedSlots.append(",");
                                selectedSlots.append(buddiesList.get(position).getServiceSlot().get(i).getId());
                            }
                            for (SlotSelectedDate ss : buddiesList.get(position).getSlotSelectedDate()) {
                                slotsDayList.add(ss.getSelectedDate());
                            }
                            huntDeal.setSelectedSlots(selectedSlots.toString());
                            huntDeal.setSelectedDates(slotsDayList);
                        }

                        openChatActivity(buddiesList.get(position).getId());
                    }
                    break;
                case R.id.iv_show_slots:
                    if (buddiesList.get(position).getServiceSlot() != null && buddiesList.get(position).getServiceSlot().size() > 0) {
                        for (SlotSelectedDate ss : buddiesList.get(position).getSlotSelectedDate()) {
                            slotsDayList.add(ss.getSelectedDate());
                        }
                        Intent intent = new Intent(this, TimeSlotsActivity.class);
                        intent.putExtra(Constants.IntentConstant.DAYS_LIST, buddiesList.get(position).getSlotSelectedDate());
                        intent.putStringArrayListExtra(Constants.IntentConstant.SELECTED_DAYS_LIST, slotsDayList);
                        intent.putExtra(Constants.IntentConstant.SLOTS, buddiesList.get(position).getServiceSlot());
                        intent.putExtra(Constants.IntentConstant.CURRENCY, buddiesList.get(position).getCurrencySymbol());
                        intent.putExtra(Constants.IntentConstant.START_DATE, buddiesList.get(position).getServiceSlot().get(0).getSlotStartDate());
                        intent.putExtra(Constants.IntentConstant.END_DATE, buddiesList.get(position).getServiceSlot().get(0).getSlotEndDate());
                        intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.IntentConstant.BUDDY);
                        startActivity(intent);
                    }
                    break;
            }

        });
    }


    /**
     * function to open chat activity
     */
    public void openChatActivity(String buddyId) {
        FirebaseDatabaseQueries.getInstance().getUser(buddyId, new FirebaseUserListener() {
            @Override
            public void getUser(UserBean user) {
                Intent intent = new Intent(HuntBuddyListActivity.this, SingleChatActivity.class);
                intent.putExtra(FirebaseConstants.OTHER_USER, user);
                intent.putExtra(FirebaseConstants.CHAT_ROOM_HUNT, new HuntDeal().getHuntDeal(huntDeal));
                startActivityForResult(intent, 1001);
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
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(HuntBuddyListActivity.this) && huntDeal != null) {
                count = 0;
                hitGetHuntBuddiesApi();
            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= buddiesList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(HuntBuddyListActivity.this) && huntDeal != null)
                            hitGetHuntBuddiesApi();
                    }
                }
            }

        });

    }


    /**
     * method to hit buddies list api
     */
    private void hitGetHuntBuddiesApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntDeal.getId());
        params.put(Constants.NetworkConstant.PARAM_SORT_BY, String.valueOf(sort)); //1 for bidding price,2 for rating,3 for request
        params.put(Constants.NetworkConstant.PARAM_ORDER, order == 1 ? "asc" : "desc");   //'asc','desc'
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        params.put(Constants.NetworkConstant.PARAM_CURRENCY_CODE, huntDeal.getCurrencyCode());
        Call<ResponseBody> call = apiInterface.hitGetHuntBuddiesApi(AppUtils.getInstance().encryptData(params));
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
                        HuntBuddyResponse huntBuddyResponse = new Gson().fromJson(response, HuntBuddyResponse.class);
                        if (isPagination) {
                            isPagination = false;
                        } else {
                            buddiesList.clear();
                        }
                        buddiesList.addAll(huntBuddyResponse.getResult());
                        huntDeal.setTaxArr(huntBuddyResponse.getTaxArr());
                        huntBuddyAdapter.notifyDataSetChanged();
                        isMoreData = huntBuddyResponse.getNext() != -1;
                        if (isMoreData) count = huntBuddyResponse.getNext();
                        if (buddiesList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;
                    case Constants.NetworkConstant.NO_DATA:
                        buddiesList.clear();
                        huntBuddyAdapter.notifyDataSetChanged();
                        if (buddiesList.size() > 0) {
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
        huntBuddyAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (buddiesList.size() == 0) {
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
        huntBuddyAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (buddiesList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }

    /**
     * Method to show options for more item
     */
    @SuppressLint("ClickableViewAccessibility")
    public void showMorePopUp(Context mContext, View view, final ItemCallBack itemCallBack) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = null;
        if (inflater != null) {
            popupView = inflater.inflate(R.layout.layout_buddy_filter_pop_up, null);
        }
        // create the popup window
        int width = (int) getResources().getDimension(R.dimen._150sdp);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        if (popupView != null) {
            popupView.setOnTouchListener((v, event) -> {
                popupWindow.dismiss();
                return true;
            });
            TextView tvPriceHigh = popupView.findViewById(R.id.tv_price_high);
            TextView tvPriceLow = popupView.findViewById(R.id.tv_price_low);
            TextView tvRatingHigh = popupView.findViewById(R.id.tv_rating_high);
            TextView tvRatingLow = popupView.findViewById(R.id.tv_rating_low);
            TextView tvNew = popupView.findViewById(R.id.tv_new);
            TextView tvOld = popupView.findViewById(R.id.tv_old);
            View.OnClickListener listener = v -> {
                itemCallBack.onClick(v);
                popupWindow.dismiss();
            };
            tvPriceHigh.setOnClickListener(listener);
            tvPriceLow.setOnClickListener(listener);
            tvRatingHigh.setOnClickListener(listener);
            tvRatingLow.setOnClickListener(listener);
            tvNew.setOnClickListener(listener);
            tvOld.setOnClickListener(listener);
            int location[] = new int[2];
            // Get the View's(the one that was clicked in the Fragment) location
            view.getLocationOnScreen(location);
            float marginX = mContext.getResources().getDimension(R.dimen._50sdp);
            float marginY = mContext.getResources().getDimension(R.dimen._35sdp);
            int locationX = (int) (location[0] - marginX);
            int locationY = (int) (location[1] + marginY);
//            int locationY = (int) (location[1]);
//            int locationX = (int) (location[0]);
            // Using location, the PopupWindow will be displayed right under anchorView
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, locationX, locationY);
            // show the popup window
            popupWindow.showAsDropDown(view);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && resultCode == RESULT_OK  && data != null && data.getExtras() != null) {
            String userId = data.getExtras().getString(Constants.IntentConstant.USER_ID, "");
            if (huntDeal != null) {
                huntDeal.setUserId(userId);
                Intent intent = new Intent();
                intent.putExtra(Constants.IntentConstant.USER_ID, huntDeal.getUserId());
                intent.putExtra(Constants.IntentConstant.HUNT_ID, huntDeal.getId());
                setResult(RESULT_OK, intent);
            }
        }
    }
}