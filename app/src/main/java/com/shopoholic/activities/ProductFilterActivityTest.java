package com.shopoholic.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dnitinverma.locationlibrary.RCLocation;
import com.dnitinverma.locationlibrary.interfaces.LocationsCallback;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shopoholic.R;
import com.shopoholic.adapters.FilterBean;
import com.shopoholic.adapters.StoresCategoryAdapter;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.preferredcategorymodel.Result;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;
import com.shopoholic.utils.ScrollGoogleMap;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class created by Sachin on 25-Apr-18.
 */
public class ProductFilterActivityTest extends BaseActivity implements OnMapReadyCallback, LocationsCallback, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.tv_user_location)
    CustomTextView tvUserLocation;
    @BindView(R.id.tv_change_location)
    CustomTextView tvUserChangeLocation;
    @BindView(R.id.tv_distance_in_miles)
    CustomTextView tvDistanceInMiles;
    @BindView(R.id.seekbar_range)
    SeekBar seekbarRange;
    @BindView(R.id.tv_search_shop_or_store)
    CustomTextView tvSearchShopOrStore;
    @BindView(R.id.tv_choose_category)
    CustomTextView tvChooseCategory;
    @BindView(R.id.tv_sub_category)
    CustomTextView tvSubCategory;
    @BindView(R.id.tv_percentage)
    CustomTextView tvPercentage;
    @BindView(R.id.iv_increase_percentage)
    ImageView ivIncreasePercentage;
    @BindView(R.id.iv_decrease_percentage)
    ImageView ivDecreasePercentage;
    @BindView(R.id.seekbar_discount)
    SeekBar seekbarDiscount;
    @BindView(R.id.tv_expiry_date)
    CustomTextView tvExpiryDate;
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_product)
    RadioButton rbProduct;
    @BindView(R.id.rb_service)
    RadioButton rbService;
    @BindView(R.id.rg_type_of_deal)
    RadioGroup rgTypeOfDeal;
    @BindView(R.id.rb_posted_by_both)
    RadioButton rbPostedByBoth;
    @BindView(R.id.rb_posted_by_merchant)
    RadioButton rbPostedByMerchant;
    @BindView(R.id.rb_posted_by_buddy)
    RadioButton rbPostedByBuddy;
    @BindView(R.id.rg_posted_by)
    RadioGroup rgPostedBy;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.iv_cross)
    ImageView iv_cross;
    @BindView(R.id.et_search)
    CustomEditText etSearch;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.iv_bottom_sheet_back)
    ImageView ivBottomSheetBack;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.tv_distance)
    CustomTextView tvDistance;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.tv_location)
    CustomTextView tvLocation;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.ll_store)
    LinearLayout llStore;
    @BindView(R.id.ll_category)
    LinearLayout llCategory;
    @BindView(R.id.ll_sub_category)
    LinearLayout llSubCategory;
    @BindView(R.id.tv_max_range)
    CustomTextView tvMaxRange;
    @BindView(R.id.ll_percentage)
    LinearLayout llPercentage;
    @BindView(R.id.view_percentage)
    View viewPercentage;
    @BindView(R.id.iv_circle_bg)
    ImageView ivCircleBg;
    @BindView(R.id.tv_km)
    TextView tvKm;
    @BindView(R.id.tv_miles)
    TextView tvMiles;


    private RCLocation location;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private FilterBean filterBean;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private List<com.shopoholic.models.storelistresponse.Result> storesList;
    private List<Result> categoryList;
    private List<Object> storeCategoryList;
    private StoresCategoryAdapter storesCategoryAdapters;
    private boolean openPlacePicker;
    private double currentLat;
    private double currentLong;
    private String currentAddress;
    private int count = 0;
    private int addressCount = 0;
    private int postedCount = 0;
    private int typeCount = 0;
    private int rangeCount = 0;
    private int discountCount = 0;
    private int storeCount = 0;
    private int categoryCount = 0;
    private int subCategoryCount = 0;
    private int clickCheck = 0;
    private boolean isClear = false;
    private int toggleAnimationStatus = 0;
    private int type = 0;
    private double distance = 20;
    private double maxRange = 20;
    private double distanceKm = 20;
    private double maxRangeKm = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_filter);
        ButterKnife.bind(this);
        initVariables();
        initializeLocation();
        setListener();
        setAdapter();
        initMap();
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {

        ivBottomSheetBack.setVisibility(View.VISIBLE);
        btnAction.setText(getString(R.string.apply));
        storeCategoryList = new ArrayList<>();
        storesList = new ArrayList<>();
        categoryList = new ArrayList<>();
        categoryList = new ArrayList<>();
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(0);

        setSupportActionBar(layoutToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            tvTitle.setText(getString(R.string.filters));
        }
        if (getIntent() != null && getIntent().getExtras() != null) {
            filterBean = (FilterBean) getIntent().getExtras().getSerializable(Constants.IntentConstant.FILTER_DATA);
            if (filterBean != null) clickCheck = filterBean.getTypeOfDeals();
            setDataFromModel();
        }
        if (filterBean == null) filterBean = new FilterBean();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.right_menu_text).setVisible(true);
        menu.findItem(R.id.right_menu_text).setTitle(getString(R.string.clear));
        menu.findItem(R.id.right_menu_text).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.right_menu_text) {
            new AlertDialog.Builder(this, R.style.DatePickerTheme)
//                    .setTitle(getString(R.string.cancel_order))
                    .setMessage(getString(R.string.sure_to_clear_filter))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        if (AppUtils.getInstance().isInternetAvailable(ProductFilterActivityTest.this)) {
                            isClear = true;
                            resetAllFields();
                            btnAction.performClick();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                        // do nothing
                    })
                    .show();
        }
        return true;
    }

    /**
     * method to reset all fields
     */
    private void resetAllFields() {
        filterBean = new FilterBean();

        distance = 20;
        getKmValue();
        updateRange();
//        seekbarRange.setProgress(20);

        tvSearchShopOrStore.setText("");
        tvChooseCategory.setText("");
        tvSubCategory.setText("");
        seekbarDiscount.setProgress(0);
        tvExpiryDate.setText("");
        rbAll.setChecked(true);
        rbProduct.setChecked(false);
        rbService.setChecked(false);
        rbPostedByBoth.setChecked(true);
        rbPostedByMerchant.setChecked(false);
        rbPostedByBuddy.setChecked(false);

        updateProductStatus(rbAll);
        updatePostedStatus(rbPostedByBoth);

        count = 0;
        addressCount = 0;
        rangeCount = 0;
        discountCount = 0;
        postedCount = 0;
        typeCount = 0;
        storeCount = 0;
        categoryCount = 0;
        subCategoryCount = 0;

//        filterBean.setDiscountPercentage(-1);

        tvUserLocation.setText(currentAddress);
        filterBean.setAddress(currentAddress);
        filterBean.setLatitude(currentLat);
        filterBean.setLongitude(currentLong);
        latitude = currentLat;
        longitude = currentLong;
        setLocationAndCircle();
    }

    /**
     * Method to set adapter on views
     */
    private void setAdapter() {
        storesCategoryAdapters = new StoresCategoryAdapter(this, storeCategoryList, new RecyclerCallBack() {
            @Override
            public void onClick(final int position, View view) {
                new Handler().postDelayed(() -> {

                }, 500);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(storesCategoryAdapters);
    }


    /**
     * method to initialize the variables
     */
    private void initMap() {
        ScrollGoogleMap mapFragment = (ScrollGoogleMap) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(() -> scrollView.requestDisallowInterceptTouchEvent(true));
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

    /**
     * method to set listener on views
     */
    private void setListener() {
        seekbarDiscount.setOnSeekBarChangeListener(this);
        seekbarRange.setOnSeekBarChangeListener(this);
        etSearch.addTextChangedListener(this);

        View.OnClickListener listener = view -> {
            if (!rbService.isChecked()) {
                updatePostedStatus(view);
            }else {
                rbPostedByBuddy.setChecked(true);
                updatePostedStatus(rbPostedByBuddy);
            }
        };
        View.OnClickListener typeListener = this::updateProductStatus;
        rbPostedByBoth.setOnClickListener(listener);
        rbPostedByBuddy.setOnClickListener(listener);
        rbPostedByMerchant.setOnClickListener(listener);
        rbAll.setOnClickListener(typeListener);
        rbProduct.setOnClickListener(typeListener);
        rbService.setOnClickListener(typeListener);
    }

    /**
     * method to update product status
     *
     * @param view
     */
    private void updateProductStatus(View view) {
        int color = ContextCompat.getColor(ProductFilterActivityTest.this, R.color.colorAccent);
        int unselectedColor = ContextCompat.getColor(ProductFilterActivityTest.this, R.color.colorHintText);
        rbAll.setTextColor(unselectedColor);
        rbProduct.setTextColor(unselectedColor);
        rbService.setTextColor(unselectedColor);
        switch (view.getId()) {
            case R.id.rb_all:
                llPercentage.setVisibility(View.VISIBLE);
                viewPercentage.setVisibility(View.VISIBLE);
                rbAll.setTextColor(color);
                typeCount = 0;
                clickCheck = 0;
                break;
            case R.id.rb_product:
                llPercentage.setVisibility(View.VISIBLE);
                viewPercentage.setVisibility(View.VISIBLE);
                rbProduct.setTextColor(color);
                typeCount = 1;
                if (clickCheck != 1 && filterBean != null) {
                    Result category = filterBean.getCategoryDetails();
                    if (category != null && !category.getCategoryType().equals("1")) {
                        filterBean.setCategoryDetails(null);
                        tvChooseCategory.setText("");
                        filterBean.setSubCategoryDetails(null);
                        tvSubCategory.setText("");
                    }
                    clickCheck = 1;
                }
                break;
            case R.id.rb_service:
                llPercentage.setVisibility(View.GONE);
                viewPercentage.setVisibility(View.GONE);
                rbService.setTextColor(color);
                typeCount = 1;
                if (clickCheck != 2 && filterBean != null) {
                    Result category = filterBean.getCategoryDetails();
                    if (category != null && !category.getCategoryType().equals("2")) {
                        filterBean.setCategoryDetails(null);
                        tvChooseCategory.setText("");
                        filterBean.setSubCategoryDetails(null);
                        tvSubCategory.setText("");
                    }
                    clickCheck = 2;
                }
                rbPostedByBuddy.setChecked(true);
                updatePostedStatus(rbPostedByBuddy);
                postedCount = 1;
                break;
        }
    }

    /**
     * method to update posted status
     *
     * @param view
     */
    private void updatePostedStatus(View view) {
        int color = ContextCompat.getColor(ProductFilterActivityTest.this, R.color.colorAccent);
        int unselectedColor = ContextCompat.getColor(ProductFilterActivityTest.this, R.color.colorHintText);
        rbPostedByBoth.setTextColor(unselectedColor);
        rbPostedByMerchant.setTextColor(unselectedColor);
        rbPostedByBuddy.setTextColor(unselectedColor);
        switch (view.getId()) {
            case R.id.rb_posted_by_both:
                rbPostedByBoth.setTextColor(color);
                postedCount = 0;
                break;
            case R.id.rb_posted_by_buddy:
                rbPostedByBuddy.setTextColor(color);
                postedCount = 1;
                break;
            case R.id.rb_posted_by_merchant:
                rbPostedByMerchant.setTextColor(color);
                postedCount = 1;
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbar_discount:
                tvPercentage.setText(TextUtils.concat(String.valueOf(progress) + "%"));
                if (isClear) {
                    filterBean.setDiscountPercentage(-1);
                    isClear = false;
                }else {
                    filterBean.setDiscountPercentage(seekbarDiscount.getProgress());
                }
                break;
            case R.id.seekbar_range:
                if (type == 0) {
                    distance = progress;
                    getKmValue();
                } else {
                    distanceKm = progress;
                    getMilesValue();
                }
                setLocationAndCircle();
//                tvDistance.setText(String.valueOf(progress));
                updateRange();
                break;
        }
    }

    /**
     * method to set location and range
     */
    private void setLocationAndCircle() {
        if (mMap != null) {
            mMap.clear();
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
//                    .radius(1609.34 * seekbarRange.getProgress())
                    .radius(1609.34 * distance)
                    .strokeColor(ContextCompat.getColor(this, android.R.color.transparent))
                    .fillColor(ContextCompat.getColor(this, R.color.colorMapMarker)));
            circle.setCenter(new LatLng(latitude, longitude));

            if (distance < 4)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11f));
            else if (distance < 8)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10f));
            else if (distance < 12)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9.5f));
            else if (distance < 16)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9f));
            else if (distance <= 20)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 8.5f));
            else if (distance <= 24)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 8f));
            else if (distance <= 30)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 7.5f));
            else if (distance <= 55)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 7f));
            else if (distance <= 75)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 6.5f));
            else if (distance <= 100)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 6f));
            else
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 5.5f));

            mMap.addMarker(new MarkerOptions().flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_main_map_start_point))
                    .anchor(0.5f, 0.5f).position(new LatLng(latitude, longitude)));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void setLocation(Location mCurrentLocation) {
        if (mCurrentLocation != null && (filterBean == null || (filterBean.getLatitude() == 0.0 && filterBean.getLongitude() == 0.0))) {
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
        } else {
            latitude = filterBean.getLatitude();
            longitude = filterBean.getLongitude();
        }
        if (mCurrentLocation == null)
            return;

        currentLat = mCurrentLocation.getLatitude();
        currentLong = mCurrentLocation.getLongitude();
        setLocationAndCircle();
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9f));
        location.getAddress();
        hitGetRangeApi(AppUtils.getInstance().getUserCountryCodeFromLatlng(this, latitude, longitude));
    }

    @Override
    public void setAddress(Address address) {
        if (address != null) {
            if (filterBean == null) filterBean = new FilterBean();
            if (filterBean.getAddress() == null || filterBean.getAddress().equals("")) {
                String locality = "";
                if (address.getLocality() != null && !address.getLocality().equals("")) {
                    locality = address.getLocality() + ", ";
                }
                tvUserLocation.setText(TextUtils.concat(locality + address.getCountryName()));
                filterBean.setAddress(locality + address.getCountryName());
                filterBean.setLatitude(address.getLatitude());
                filterBean.setLongitude(address.getLongitude());
            }
            String locality = "";
            if (address.getLocality() != null && !address.getLocality().equals("")) {
                locality = address.getLocality() + ", ";
            }
            currentAddress = locality + address.getCountryName();
        }
        location.disconnect();
    }

    @Override
    public void setLatAndLong(Address location) {
    }

    @Override
    public void disconnect() {
    }

    @OnClick({R.id.tv_change_location, R.id.ll_store, R.id.ll_category, R.id.ll_sub_category, R.id.iv_increase_percentage, R.id.iv_decrease_percentage,
            R.id.tv_expiry_date, R.id.btn_action, R.id.iv_cross, R.id.iv_bottom_sheet_back, R.id.fl_switch, R.id.iv_circle_bg, R.id.tv_km, R.id.tv_miles})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_change_location:
                if (!openPlacePicker) {
                    try {
                        openPlacePicker = true;
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        startActivityForResult(builder.build(this), Constants.IntentConstant.REQUEST_PLACE_PICKER);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        openPlacePicker = false;
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.ll_store:
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                startActivityForResult(new Intent(this, StoreListActivity.class)
                                .putExtra(Constants.IntentConstant.LOCATION, new LatLng(currentLat, currentLong))
                                .putExtra(Constants.IntentConstant.STORE, filterBean.getStoreDetail())
                                .putExtra(Constants.NetworkConstant.PARAM_RANGE, String.valueOf(filterBean.getRange()))
                                .putExtra(Constants.NetworkConstant.PARAM_LATITUDE, String.valueOf(latitude))
                                .putExtra(Constants.NetworkConstant.PARAM_LONGITUDE, String.valueOf(longitude))
                        , Constants.IntentConstant.REQUEST_STORES);
                break;
            case R.id.ll_category:
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                startActivityForResult(new Intent(this, PreferredCategoriesActivity.class)
                                .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.FILTER)
                                .putExtra(Constants.IntentConstant.IS_CATEGORY, true)
                                .putExtra(Constants.IntentConstant.CHECK, clickCheck)
                        , Constants.IntentConstant.REQUEST_CATEGORIES);
                break;
            case R.id.ll_sub_category:
                if (filterBean != null && filterBean.getCategoryDetails() != null) {
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    startActivityForResult(new Intent(this, PreferredCategoriesActivity.class)
                                    .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.FILTER)
                                    .putExtra(Constants.IntentConstant.IS_CATEGORY, false)
                                    .putExtra(Constants.IntentConstant.CATEGORY, filterBean.getCategoryDetails().getCatId())
                            , Constants.IntentConstant.REQUEST_SUB_CATEGORIES);
                } else {
                    AppUtils.getInstance().showToast(this, getString(R.string.please_select_category_first));
                }
                break;
            case R.id.iv_increase_percentage:
                int prog = seekbarDiscount.getProgress();
                seekbarDiscount.setProgress(++prog);
                break;
            case R.id.iv_decrease_percentage:
                int prog1 = seekbarDiscount.getProgress();
                seekbarDiscount.setProgress(--prog1);
                break;
            case R.id.tv_expiry_date:
                AppUtils.getInstance().openDatePicker(this, tvExpiryDate, Calendar.getInstance(), null, tvExpiryDate.getText().toString().trim());
                break;
            case R.id.btn_action:
                if (rbPostedByMerchant.isChecked() && rbService.isChecked()) {
                    AppUtils.getInstance().showToast(this, getString(R.string.combination_not_valid));
                    return;
                }
                setDataOnModel();
                break;
            case R.id.iv_cross:
                if (etSearch.getText().toString().trim().length() > 0) {
                    etSearch.setText("");
                }
                break;
            case R.id.iv_bottom_sheet_back:
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(0);
                }
                break;
            case R.id.fl_switch:
            case R.id.iv_circle_bg:
            case R.id.tv_km:
            case R.id.tv_miles:
                switchSelection();
                break;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setPeekHeight(0);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * method to set data on filter model
     */
    private void setDataOnModel() {
        if (filterBean == null) filterBean = new FilterBean();
        filterBean.setAddress(tvUserLocation.getText().toString().trim());
        filterBean.setLatitude(latitude);
        filterBean.setLongitude(longitude);
//        filterBean.setDiscountPercentage(seekbarDiscount.getProgress());

//        filterBean.setRange(seekbarRange.getProgress());
        filterBean.setRange((int) (distance + 0.5));
        filterBean.setMaxRange((int) (maxRange + 0.5));
        filterBean.setType(type);

        filterBean.setExpireDate(tvExpiryDate.getText().toString().trim());
        filterBean.setPostedBy(rbPostedByMerchant.isChecked() ? 1 : rbPostedByBuddy.isChecked() ? 2 : 0);
        filterBean.setTypeOfDeals(rbService.isChecked() ? 2 : rbProduct.isChecked() ? 1 : 0);
        if (tvExpiryDate.getText().toString().trim().length() > 0) ++count;
        discountCount = filterBean.getDiscountPercentage() != -1 ? 1 : 0;

//        rangeCount = seekbarRange.getProgress() != 20 ? 1 : 0;
        rangeCount = distance != 20 ? 1 : 0;

        addressCount = filterBean.getAddress() != null && !filterBean.getAddress().equals("") && !filterBean.getAddress().equals(currentAddress) ? 1 : 0;
        filterBean.setCount(count + postedCount + typeCount + rangeCount + addressCount + discountCount + storeCount + categoryCount + subCategoryCount);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, filterBean.toString());
        Intent intent = new Intent();
        intent.putExtra(Constants.IntentConstant.FILTER_DATA, filterBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * method to set data on views from model
     */
    private void setDataFromModel() {
        if (filterBean != null) {
            count = filterBean.getCount();
            tvUserLocation.setText(filterBean.getAddress());
            latitude = filterBean.getLatitude();
            longitude = filterBean.getLongitude();
            tvExpiryDate.setText(filterBean.getExpireDate());

//            seekbarRange.setMax(filterBean.getMaxRange());
//            tvMaxRange.setText(String.valueOf(filterBean.getMaxRange()));
//            seekbarRange.setProgress(filterBean.getRange());
            maxRange = filterBean.getMaxRange();
            distance = filterBean.getRange();
            type = filterBean.getType();
            getKmValue();
            if (type == 1)
                switchSelection();
            else
                updateRange();

            if (filterBean.getDiscountPercentage() != -1) {
                tvPercentage.setText(TextUtils.concat(filterBean.getDiscountPercentage() + "%"));
                seekbarDiscount.setProgress(filterBean.getDiscountPercentage());
            }else {
                tvPercentage.setText(TextUtils.concat(getString(R.string.zero) + "%"));
            }
            switch (filterBean.getPostedBy()) {
                default:
                    rbPostedByBoth.setChecked(true);
                    updatePostedStatus(rbPostedByBoth);
                    postedCount = 0;
//                    --count;
                    break;
                case 1:
                    rbPostedByMerchant.setChecked(true);
                    updatePostedStatus(rbPostedByMerchant);
                    postedCount = 1;
//                    --count;
                    break;
                case 2:
                    rbPostedByBuddy.setChecked(true);
                    updatePostedStatus(rbPostedByBuddy);
                    postedCount = 1;
//                    --count;
                    break;
            }
            switch (filterBean.getTypeOfDeals()) {
                default:
                    rbAll.setChecked(true);
                    updateProductStatus(rbAll);
                    typeCount = 0;
//                    --count;
                    break;
                case 1:
                    rbProduct.setChecked(true);
                    updateProductStatus(rbProduct);
                    typeCount = 1;
//                    --count;
                    break;
                case 2:
                    rbService.setChecked(true);
                    updateProductStatus(rbService);
                    typeCount = 1;
//                    --count;
                    break;
            }
            if (filterBean.getDiscountPercentage() != 0 || filterBean.getDiscountPercentage() != -1) {
//                --count;
                discountCount = 1;
            }
            if (filterBean.getRange() != 0) {
//                --count;
                rangeCount = 1;
            }
            if (filterBean.getAddress() != null && !filterBean.getAddress().equals("")) {
//                --count;
                addressCount = 1;
            }
            ArrayList<com.shopoholic.models.storelistresponse.Result> store = filterBean.getStoreDetail();
            if (store != null && store.size() > 0) {
                StringBuilder storeNames = new StringBuilder();
                for (int i = 0; i < store.size(); i++) {
                    if (i != 0) {
                        storeNames.append(", ");
                    }
                    storeNames.append(store.get(i).getStoreName());
                }
                tvSearchShopOrStore.setText(storeNames.toString());
//                --count;
                storeCount = 1;
            }
            if (filterBean.getCategoryDetails() != null) {
                tvChooseCategory.setText(filterBean.getCategoryDetails().getCatName());
//                --count;
                categoryCount = 1;
            }
            if (filterBean.getSubCategoryDetails() != null) {
                tvSubCategory.setText(filterBean.getSubCategoryDetails().getSubCatName());
//                --count;
                subCategoryCount = 1;
            }
            count = 0;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        openPlacePicker = false;
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null && place.getAddress() != null) {
                    String address = "";
                    if (place.getName()!= null && !place.getName().equals("") && !place.getName().toString().contains("\"")) {
                        address += place.getName() + ", ";
                    }
                    address += place.getAddress().toString();
                    tvUserLocation.setText(address);
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
//                    currentLat = place.getLatLng().latitude;
//                    currentLong = place.getLatLng().longitude;
                    addressCount = 1;
                    setLocationAndCircle();
                    hitGetRangeApi(AppUtils.getInstance().getUserCountryCodeFromLatlng(this, latitude, longitude));
                }
            } else if (requestCode == Constants.IntentConstant.REQUEST_STORES) {
                if (data != null && data.getExtras() != null) {
                    ArrayList<com.shopoholic.models.storelistresponse.Result> store = (ArrayList<com.shopoholic.models.storelistresponse.Result>) data.getExtras().getSerializable(Constants.IntentConstant.STORE);
                    if (store != null) {
                        StringBuilder storeNames = new StringBuilder();
                        for (int i = 0; i < store.size(); i++) {
                            if (i != 0) {
                                storeNames.append(", ");
                            }
                            storeNames.append(store.get(i).getStoreName());
                        }
                        tvSearchShopOrStore.setText(storeNames.toString());
                        if (filterBean == null) filterBean = new FilterBean();
                        filterBean.setStoreDetail(store);
                        storeCount = 1;
                    }
                }
            } else if (requestCode == Constants.IntentConstant.REQUEST_CATEGORIES) {
                if (data != null && data.getExtras() != null) {
                    Result category = (Result) data.getExtras().getSerializable(Constants.IntentConstant.CATEGORY);
                    if (category != null) {
                        tvChooseCategory.setText(category.getCatName());
                        if (filterBean == null) filterBean = new FilterBean();
                        filterBean.setCategoryDetails(category);
                        categoryCount = 1;
                        subCategoryCount = 0;
                        tvSubCategory.setText("");
                        filterBean.setSubCategoryDetails(null);
                    }
                }
            } else if (requestCode == Constants.IntentConstant.REQUEST_SUB_CATEGORIES) {
                if (data != null && data.getExtras() != null) {
                    com.shopoholic.models.subcategoryresponse.Result subCategory = (com.shopoholic.models.subcategoryresponse.Result) data.getExtras().getSerializable(Constants.IntentConstant.CATEGORY);
                    if (subCategory != null) {
                        tvSubCategory.setText(subCategory.getSubCatName());
                        if (filterBean == null) filterBean = new FilterBean();
                        filterBean.setSubCategoryDetails(subCategory);
                        subCategoryCount = 1;
                    }
                }
            }
        }
    }


    /**
     * method to get range
     *
     * @param countryCode
     */
    public void hitGetRangeApi(String countryCode) {
        if (!countryCode.equals("")) progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_ISO_CODE, countryCode);
        Call<ResponseBody> call = apiInterface.hitGetRangeApi(AppUtils.getInstance().encryptData(params));
        if (!countryCode.equals("")) ApiCall.getInstance().hitService(this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
                if (responseCode == Constants.NetworkConstant.SUCCESS_CODE) {
                    try {
                        String max = new JSONObject(response).optString(Constants.NetworkConstant.RESULT);
//                        seekbarRange.setMax(Integer.parseInt(max));
//                        tvMaxRange.setText(max);
//                        filterBean.setMaxRange(Integer.parseInt(max));

                        maxRange = Double.parseDouble(max);
                        getKmValue();
                        updateRange();
                    } catch (Exception e) {
                        e.printStackTrace();
//                        seekbarRange.setMax(filterBean.getMaxRange());
//                        tvMaxRange.setText(String.valueOf(filterBean.getMaxRange()));
                        updateRange();
                    }
                } else {
//                    seekbarRange.setMax(filterBean.getMaxRange());
//                    tvMaxRange.setText(String.valueOf(filterBean.getMaxRange()));
                    updateRange();
                }
//                seekbarRange.setProgress(filterBean.getRange());
                updateRange();
            }

            @Override
            public void onError(String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
//                seekbarRange.setMax(filterBean.getMaxRange());
//                tvMaxRange.setText(String.valueOf(filterBean.getMaxRange()));
//                seekbarRange.setProgress(filterBean.getRange());
                updateRange();
            }

            @Override
            public void onFailure() {
                progressBar.setVisibility(View.GONE);
//                seekbarRange.setMax(filterBean.getMaxRange());
//                tvMaxRange.setText(String.valueOf(filterBean.getMaxRange()));
//                seekbarRange.setProgress(filterBean.getRange());
                updateRange();
            }
        }, Constants.NetworkConstant.REQUEST_PRODUCTS);
    }

    /**
     * method to switch selection
     */
    private void switchSelection() {
        if (toggleAnimationStatus == 0) {
            Animation leftToRight = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
            leftToRight.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    tvKm.setTextColor(ContextCompat.getColor(ProductFilterActivityTest.this, R.color.colorTabUnselected));
                    tvMiles.setTextColor(Color.WHITE);
                    type = 1;
                    updateRange();
                }
                @Override
                public void onAnimationEnd(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}

            });
            ivCircleBg.startAnimation(leftToRight);
            leftToRight.setFillAfter(true);
            toggleAnimationStatus = 1;
        } else {
            Animation rightToLeft = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
            rightToLeft.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    tvMiles.setTextColor(ContextCompat.getColor(ProductFilterActivityTest.this, R.color.colorTabUnselected));
                    tvKm.setTextColor(Color.WHITE);
                    type = 0;
                    updateRange();
                }
                @Override
                public void onAnimationEnd(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            ivCircleBg.startAnimation(rightToLeft);
            rightToLeft.setFillAfter(true);
            toggleAnimationStatus = 0;
        }
    }

    /**
     * function to change value
     */
    private void getKmValue() {
        if (filterBean == null) filterBean = new FilterBean();
        distanceKm = AppUtils.getInstance().convertDistanceUnit(distance, 0);
        maxRangeKm = AppUtils.getInstance().convertDistanceUnit(maxRange, 0);
    }

    /**
     * function to change value
     */
    private void getMilesValue() {
        if (filterBean == null) filterBean = new FilterBean();
        distance = AppUtils.getInstance().convertDistanceUnit(distanceKm, 1);
        maxRange = AppUtils.getInstance().convertDistanceUnit(maxRangeKm, 1);
    }

    /**
     * function to set value
     */
    private void updateRange() {
        if (filterBean == null) filterBean = new FilterBean();
        if (type == 0) {
            seekbarRange.setOnSeekBarChangeListener(null);
            seekbarRange.setMax((int) (maxRange + 0.5));
            seekbarRange.setProgress((int) (distance + 0.5));
            tvDistance.setText(String.valueOf((int) (distance + 0.5)));
            tvMaxRange.setText(String.valueOf((int) (maxRange + 0.5)));
            tvDistanceInMiles.setText(getString(R.string.discover_deals_near_you_in_miles));
            seekbarRange.setOnSeekBarChangeListener(this);
        } else {
            seekbarRange.setOnSeekBarChangeListener(null);
            seekbarRange.setMax((int) maxRangeKm);
            seekbarRange.setProgress((int) distanceKm);
            tvDistance.setText(String.valueOf((int) distanceKm));
            tvMaxRange.setText(String.valueOf((int) maxRangeKm));
            tvDistanceInMiles.setText(getString(R.string.discover_deals_near_you_in_km));
            seekbarRange.setOnSeekBarChangeListener(this);
        }
    }
}
