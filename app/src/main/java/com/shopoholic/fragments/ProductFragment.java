package com.shopoholic.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.cameraandgallery.activities.CameraGalleryActivity;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.multiple_media_picker.ImagesGallery;
import com.shopoholic.R;
import com.shopoholic.activities.AddTimeSlotsActivity;
import com.shopoholic.activities.CommonActivity;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.activities.HuntTimeSlotsActivity;
import com.shopoholic.adapters.DealImagesAdapter;
import com.shopoholic.adapters.PreferredCategoriesAdapter;
import com.shopoholic.adapters.SubCategoryAdapter;
import com.shopoholic.adapters.TimeSlotsAdapter;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.AddressCallback;
import com.shopoholic.models.AddSlotsModel;
import com.shopoholic.models.countrymodel.CountryBean;
import com.shopoholic.models.preferredcategorymodel.PreferredCategoriesResponse;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.models.subcategoryresponse.Result;
import com.shopoholic.models.subcategoryresponse.SubCategoryResponse;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;
import com.shopoholic.utils.ReverseGeocoding;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

public class ProductFragment extends Fragment implements NetworkListener, AmazonCallback, TextWatcher, AddressCallback {


    @BindView(R.id.tv_select_category)
    CustomTextView tvSelectCategory;
    @BindView(R.id.tv_select_sub_category)
    CustomTextView tvSelectSubCategory;
    @BindView(R.id.tv_price_range)
    CustomTextView tvPriceRange;
    @BindView(R.id.seekbar)
    LinearLayout seekbar;
    @BindView(R.id.tv_min)
    CustomTextView tvMin;
    @BindView(R.id.min_currency)
    CustomTextView minCurrency;
    @BindView(R.id.max_currency)
    CustomTextView maxCurrency;
    @BindView(R.id.tv_max)
    CustomTextView tvMax;
    @BindView(R.id.tv_min_price)
    CustomEditText tvMinPrice;
    @BindView(R.id.tv_max_price)
    CustomEditText tvMaxPrice;
    @BindView(R.id.tv_upload_image)
    CustomTextView tvUploadImage;
    @BindView(R.id.image_loader)
    ProgressBar imageLoader;
    @BindView(R.id.tv_upload)
    CustomTextView tvUpload;
    @BindView(R.id.rv_upload_image)
    RecyclerView rvUploadImage;
    @BindView(R.id.tv_description)
    CustomTextView tvDescription;
    @BindView(R.id.add_description)
    CustomEditText addDescription;
    @BindView(R.id.tv_address)
    CustomTextView tvAddress;
    @BindView(R.id.label_target_area)
    CustomTextView labelTargetArea;
    @BindView(R.id.tv_target_area)
    CustomTextView tvTargetArea;
    @BindView(R.id.view_target_area)
    View viewTargetArea;
    @BindView(R.id.tv_expected_date)
    CustomTextView tvExpectedDate;
    @BindView(R.id.rb_followed)
    CheckBox rbFollowed;
    @BindView(R.id.rb_worked)
    CheckBox rbWorked;
    @BindView(R.id.rb_nearby)
    CheckBox rbNearby;
    @BindView(R.id.rb_target_area)
    CheckBox rbTargetArea;
    @BindView(R.id.tv_start_date)
    CustomTextView tvStartDate;
    @BindView(R.id.tv_end_date)
    CustomTextView tvEndDate;
    @BindView(R.id.rb_recursive)
    CheckBox rbRecursive;
    @BindView(R.id.tv_add_time_slots)
    CustomTextView tvAddTimeSlots;
    @BindView(R.id.tv_time_slots)
    CustomTextView tvTimeSlots;
    @BindView(R.id.rl_upload_images)
    RelativeLayout rlUploadImages;
    @BindView(R.id.rv_time_slots)
    RecyclerView rvTimeSlots;
    @BindView(R.id.ll_slots_view)
    LinearLayout llSlotsView;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.et_search)
    CustomEditText etSearch;
    @BindView(R.id.et_hunt_name)
    CustomEditText etHuntName;
    @BindView(R.id.rv_country_code)
    RecyclerView rvCategories;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    @BindView(R.id.ll_buddy_type)
    LinearLayout llBuddyType;
    @BindView(R.id.rl_price_range)
    RelativeLayout rlPriceRange;

    private boolean isProduct;
    private AppCompatActivity mActivity;
    private List<com.shopoholic.models.preferredcategorymodel.Result> categoryList;
    private List<Result> subCategoryList;
    private PreferredCategoriesAdapter prefferdCategoriesAdapter;
    private SubCategoryAdapter subCategoryAdapter;
    private DealImagesAdapter imagesAdapter;
    private ArrayList<ImageBean> imagesBeanList;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private com.shopoholic.models.producthuntlistresponse.Result productServiceModel;
    private int id = 0;
    private AmazonS3 mAmazonS3;
    private Unbinder unbinder;
    private boolean openPlacePicker;
    private boolean isLoading;
    private RangeSeekBar<Integer> rangeSeekBar;
    private String huntId = "";
    private boolean isDateClick = false;
    private String currencySymbol = "";
    private String currencyCode = "";
    private ArrayList<String> slotsDayList;
    private ArrayList<ServiceSlot> timeSlotList;
    private TimeSlotsAdapter addSlotsAdapter;
    private boolean isClicked = false;
    public String fromClass = "";
    public ArrayList<String> selectedImages;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null && getArguments().containsKey(Constants.IntentConstant.IS_PRODUCT))
            isProduct = getArguments().getBoolean(Constants.IntentConstant.IS_PRODUCT, false);
        initVariables();
        setAdapters();
        setSeekbarAndListeners();
        initializeAmazonS3();
        createAndSetAdapters();
        getDataAndSetValue();
        return view;
    }



    @OnClick({R.id.tv_select_category, R.id.tv_select_sub_category, R.id.tv_upload, R.id.tv_address, R.id.tv_target_area, R.id.tv_expected_date, R.id.rb_followed, R.id.rb_worked, R.id.rb_nearby, R.id.rb_target_area, R.id.tv_start_date, R.id.tv_end_date, R.id.tv_time_slots, R.id.btn_action, R.id.rb_recursive})
    public void onViewClicked(View view) {
        Calendar startDate, endDate;
        switch (view.getId()) {
            case R.id.tv_select_category:
                rvCategories.setAdapter(prefferdCategoriesAdapter);
                new Handler().postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 500);
                break;
            case R.id.tv_select_sub_category:
                rvCategories.setAdapter(subCategoryAdapter);
                new Handler().postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 500);
                break;
            case R.id.tv_upload:
                if (!isClicked) {
                    setClicked();
                    if (imagesBeanList.size() < 5) {
                        checkStoragePermission();
                    } else {
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.max_5_images_allowed));
                    }
                }
                break;
            case R.id.tv_address:
                if (!openPlacePicker) {
                    try {
                        openPlacePicker = true;
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        ProductFragment.this.startActivityForResult(builder.build(mActivity), Constants.IntentConstant.REQUEST_PLACE_PICKER);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        openPlacePicker = false;
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.tv_target_area:
                if (!openPlacePicker) {
                    try {
                        openPlacePicker = true;
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        ProductFragment.this.startActivityForResult(builder.build(mActivity), Constants.IntentConstant.REQUEST_TARGET_PLACE_PICKER);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        openPlacePicker = false;
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.tv_expected_date:
                AppUtils.getInstance().openDatePicker(mActivity, tvExpectedDate, Calendar.getInstance(), null, tvExpectedDate.getText().toString().trim());
                break;
            case R.id.rb_followed:
                new Handler().postDelayed(() -> {
                    changeBuddyType(rbFollowed.isChecked(), rbFollowed);
                }, 100);
                break;
            case R.id.rb_worked:
                new Handler().postDelayed(() -> {
                    changeBuddyType(rbWorked.isChecked(), rbWorked);
                }, 100);
                break;
            case R.id.rb_nearby:
                new Handler().postDelayed(() -> {
                    changeBuddyType(rbNearby.isChecked(), rbNearby);
                }, 100);
                break;
            case R.id.rb_target_area:
                new Handler().postDelayed(() -> {
                    changeBuddyType(rbTargetArea.isChecked(), rbTargetArea);
                }, 100);
                break;
            case R.id.tv_start_date:
                if (!isDateClick) {
                    isDateClick = true;
                    new Handler().postDelayed(() -> isDateClick = false, 1000);
                    endDate = tvEndDate.getText().toString().trim().length() == 0 ? null :
                            AppUtils.getInstance().getCallenderObject(tvEndDate.getText().toString().trim());
                    if (!isProduct) {
                        Calendar date = Calendar.getInstance();
                        if (null == endDate) {
                            startDate = Calendar.getInstance();
                        } else {
                            date.setTime(endDate.getTime());
                            date.add(Calendar.MONTH, -2);
                            startDate = date.before(Calendar.getInstance()) ? Calendar.getInstance() : date;
                        }
                    } else {
                        startDate = Calendar.getInstance();
                    }
                    AppUtils.getInstance().openHuntDatePicker(mActivity, tvStartDate, startDate, endDate, tvStartDate.getText().toString().trim(), 2);
                }
                break;
            case R.id.tv_end_date:
                if (!isDateClick) {
                    isDateClick = true;
                    new Handler().postDelayed(() -> isDateClick = false, 1000);
//                tvChooseDate.setText("");
                    startDate = tvStartDate.getText().toString().trim().length() == 0 ? Calendar.getInstance() :
                            AppUtils.getInstance().getCallenderObject(tvStartDate.getText().toString().trim());
                    if (!isProduct) {
                        Calendar date1 = Calendar.getInstance();
                        if (null == startDate || tvStartDate.getText().toString().length() == 0) {
                            endDate = null;
                        } else {
                            date1.setTime(startDate.getTime());
                            date1.add(Calendar.MONTH, 2);
                            endDate = date1;
                        }
                    } else {
                        endDate = null;
                    }
                    AppUtils.getInstance().openHuntDatePicker(mActivity, tvEndDate, startDate, endDate, tvEndDate.getText().toString().trim(), 2);
                }
                break;
            case R.id.rb_recursive:
                if (tvStartDate.getText().toString().trim().length() == 0) {
                    AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_start_date));
                    rbRecursive.setChecked(!rbRecursive.isChecked());
                } else if (tvEndDate.getText().toString().trim().length() == 0) {
                    AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_end_date));
                    rbRecursive.setChecked(!rbRecursive.isChecked());
                } else {
                    timeSlotList.clear();
                    addSlotsAdapter.notifyDataSetChanged();
                    slotsDayList.clear();
                    String stDate = AppUtils.getInstance().formatDate(tvStartDate.getText().toString().trim(), DATE_FORMAT, SERVICE_DATE_FORMAT);
                    String edDate = AppUtils.getInstance().formatDate(tvEndDate.getText().toString().trim(), DATE_FORMAT, SERVICE_DATE_FORMAT);
                    if (slotsDayList.size() == 0)
                        slotsDayList.addAll(AppUtils.getInstance().getDates(stDate, edDate));
                }
                break;
            case R.id.tv_time_slots:
                if (!isClicked) {
                    setClicked();
                    if (tvStartDate.getText().toString().trim().length() == 0) {
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_start_date));
                    } else if (tvEndDate.getText().toString().trim().length() == 0) {
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_end_date));
                    } else if (tvAddress.getText().toString().trim().length() == 0) {
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_address_first));
                    } else if (currencyCode.equals("") || currencySymbol.equals("")) {
                        AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_valid_address));
                    } else {
                        String stDate = AppUtils.getInstance().formatDate(tvStartDate.getText().toString().trim(), DATE_FORMAT, SERVICE_DATE_FORMAT);
                        String edDate = AppUtils.getInstance().formatDate(tvEndDate.getText().toString().trim(), DATE_FORMAT, SERVICE_DATE_FORMAT);
                        if (slotsDayList.size() == 0)
                            slotsDayList.addAll(AppUtils.getInstance().getDates(stDate, edDate));
                        if (rbRecursive.isChecked()) {
                            Intent intent = new Intent(mActivity, AddTimeSlotsActivity.class);
                            ArrayList<AddSlotsModel> list = new ArrayList<>();
                            for (int i = 0; i < timeSlotList.size(); i++) {
                                list.add(AddSlotsModel.getSlotModel(timeSlotList.get(i)));
                                list.get(i).setCurrency(currencySymbol);
                                list.get(i).setCurrencyCode(currencyCode);
                            }
                            intent.putExtra(Constants.IntentConstant.CURRENCY, currencySymbol);
                            intent.putExtra(Constants.IntentConstant.CURRENCY_CODE, currencyCode);
                            intent.putExtra(Constants.IntentConstant.SLOTS, list);
                            intent.putExtra(Constants.IntentConstant.START_DATE, stDate);
                            intent.putExtra(Constants.IntentConstant.END_DATE, edDate);
                            ProductFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_TIME_SLOTS);

                        } else {
                            Intent intent = new Intent(mActivity, HuntTimeSlotsActivity.class);
                            intent.putStringArrayListExtra(Constants.IntentConstant.DAYS_LIST, slotsDayList);
                            intent.putExtra(Constants.IntentConstant.SLOTS, timeSlotList);
                            intent.putExtra(Constants.IntentConstant.CURRENCY, currencySymbol);
                            intent.putExtra(Constants.IntentConstant.CURRENCY_CODE, currencyCode);
                            intent.putExtra(Constants.IntentConstant.START_DATE, tvStartDate.getText().toString().trim());
                            intent.putExtra(Constants.IntentConstant.END_DATE, tvEndDate.getText().toString().trim());
                            ProductFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_SLOTS);
                        }
                    }
                }
                break;
            case R.id.btn_action:
                if (isValidate()) {
                    saveServiceDealData();
                    if (!isLoading && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                        hitProductHuntApi();
                    }
                }
                break;
        }
    }

    /**
     * function to change buddy type
     * @param isChecked
     * @param checkBox
     */
    private void changeBuddyType(boolean isChecked, CheckBox checkBox) {
        checkBox.setChecked(isChecked);
        checkBox.setTextColor(ContextCompat.getColor(mActivity, isChecked ? R.color.colorAccent : R.color.colorHintText));
        if (checkBox.getId() == R.id.rb_target_area) {
            tvTargetArea.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            viewTargetArea.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            labelTargetArea.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        }
    }


    /**
     * save service deal data
     */
    private void saveServiceDealData() {
        if (isProduct) {
            productServiceModel.setPriceStart(String.valueOf(rangeSeekBar.getSelectedMinValue()));
            productServiceModel.setPriceEnd(String.valueOf(rangeSeekBar.getSelectedMaxValue()));
            productServiceModel.setExpectedDeliveryDate(tvExpectedDate.getText().toString().trim());
            String buddyType = "";
            if (rbFollowed.isChecked()) buddyType += "1";
            if (rbWorked.isChecked()) buddyType += buddyType.equals("") ? "2" : ",2";
            if (rbNearby.isChecked()) buddyType += buddyType.equals("") ? "3" : ",3";
            if (rbTargetArea.isChecked()) buddyType += buddyType.equals("") ? "4" : ",4";
            productServiceModel.setChooseBuddy(buddyType);
        } else {
            productServiceModel.setIsRecursive(rbRecursive.isChecked() ? "1" : "2");
            productServiceModel.setHuntStartDate(tvStartDate.getText().toString().trim());
            productServiceModel.setHuntEndDate(tvEndDate.getText().toString().trim());
            double price = 0.0;
            for (ServiceSlot slot : timeSlotList) {
                price += Double.parseDouble(slot.getPrice());
            }
            productServiceModel.setPriceStart(String.valueOf(price));
        }
        ArrayList<String> images = new ArrayList<>();
        for (ImageBean bean : imagesBeanList) {
            images.add(bean.getServerUrl());
        }
        productServiceModel.setImagesList(images);
        productServiceModel.setProductType(isProduct ? "1" : "2");
        productServiceModel.setHuntTitle(etHuntName.getText().toString().trim());
        productServiceModel.setDescription(addDescription.getText().toString().trim());
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, productServiceModel.toString());
    }

    /**
     * initialize amazon service
     */
    private void initializeAmazonS3() {
        mAmazonS3 = new AmazonS3();
        mAmazonS3.setVariables(Constants.UrlConstant.AMAZON_POOLID, Constants.UrlConstant.BUCKET, Constants.UrlConstant.AMAZON_SERVER_URL, Constants.UrlConstant.END_POINT, Constants.UrlConstant.REGION);
        mAmazonS3.setActivity(mActivity);
        mAmazonS3.setCallback(this);
    }

    /**
     * initialize the variables
     */

    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        imageLoader.setVisibility(View.GONE);
        btnAction.setText(getString(R.string.submit));

        categoryList = new ArrayList<>();
        subCategoryList = new ArrayList<>();
        imagesBeanList = new ArrayList<>();
        slotsDayList = new ArrayList<>();
        timeSlotList = new ArrayList<>();
        selectedImages = new ArrayList<>();

        if (productServiceModel == null) {
            productServiceModel = new com.shopoholic.models.producthuntlistresponse.Result();
        }
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(0);

        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            hitGetCategoryListApi();
        }

    }

    /**
     * method to set the time slots adapter
     */
    private void setAdapters() {

        addSlotsAdapter = new TimeSlotsAdapter(mActivity, this, timeSlotList, (position, view) -> {
            switch (view.getId()) {
                case R.id.tv_start_time:
//                        setTime(position, 0);
//                    showDateTimePicker(position, 0);
                    break;

                case R.id.tv_end_time:
//                        setTime(position, 1);
//                    showDateTimePicker(position, 1);
                    break;


                case R.id.iv_delete_row:
                    timeSlotList.remove(position);
                    addSlotsAdapter.notifyItemRemoved(position);
                    addSlotsAdapter.notifyItemRangeChanged(position, timeSlotList.size());
                    break;


            }
        });
        rvTimeSlots.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        rvTimeSlots.setAdapter(addSlotsAdapter);

    }

    /**
     * method to create and set adapter on views
     */
    private void createAndSetAdapters() {


        prefferdCategoriesAdapter = new PreferredCategoriesAdapter(mActivity, categoryList, (position, view) -> new Handler().postDelayed(() -> {
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setPeekHeight(0);
            tvSelectCategory.setText(categoryList.get(position).getCatName());
            productServiceModel.setCategoryName(categoryList.get(position).getCatName());
            productServiceModel.setCatId(categoryList.get(position).getCatId());
            subCategoryList.clear();
            tvSelectSubCategory.setText("");
            //  hit subcategory
            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                hitSubGetCategoryListApi(categoryList.get(position).getCatId());
            }
        }, 500));
        subCategoryAdapter = new SubCategoryAdapter(mActivity, subCategoryList, (position, view) -> new Handler().postDelayed(() -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setPeekHeight(0);
            tvSelectSubCategory.setText(subCategoryList.get(position).getSubCatName());
            productServiceModel.setSubCatName(subCategoryList.get(position).getSubCatName());
            productServiceModel.setSubcatId(subCategoryList.get(position).getSubCatId());
        }, 500));

        rvCategories.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

        imagesAdapter = new DealImagesAdapter(mActivity, this, imagesBeanList, (position, view) -> {
            switch (view.getId()) {
                case R.id.iv_remove_image:
                    imagesBeanList.remove(position);
                    imagesAdapter.notifyDataSetChanged();
            }
            /*  productServiceModel.setImage(imagesBeanList.get(position));*/
        });
        rvUploadImage.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rvUploadImage.setAdapter(imagesAdapter);

    }

    /**
     * method to set seekbar listener
     */
    private void setSeekbarAndListeners() {

        tvMinPrice.addTextChangedListener(this);
        tvMaxPrice.addTextChangedListener(this);

        rangeSeekBar = new RangeSeekBar<Integer>(mActivity);
        // Set the range
        rangeSeekBar.setRangeValues(0, 99999);
        rangeSeekBar.setSelectedMinValue(0);
        rangeSeekBar.setSelectedMaxValue(99999);

        // Add to layout
        seekbar.addView(rangeSeekBar);

        rangeSeekBar.setOnRangeSeekBarChangeListener((bar, minValue, maxValue) -> {
            if (minValue != null && maxValue != null) {

                tvMinPrice.removeTextChangedListener(ProductFragment.this);
                tvMaxPrice.removeTextChangedListener(ProductFragment.this);

                tvMinPrice.setText(String.valueOf(minValue.intValue()));
                tvMaxPrice.setText(String.valueOf(maxValue.intValue()));

                tvMinPrice.setSelection(tvMinPrice.getText().toString().trim().length());
                tvMaxPrice.setSelection(tvMaxPrice.getText().toString().trim().length());

                tvMinPrice.addTextChangedListener(this);
                tvMaxPrice.addTextChangedListener(this);
            }
        });

        tvMinPrice.setText(getString(R.string.zero));
        tvMaxPrice.setText(getString(R.string._10000));

        tvStartDate.addTextChangedListener(new TextWatcher() {
            private String startDate = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                startDate = s.toString();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (tvStartDate.getText().length() != 0 && tvEndDate.getText().length() != 0
                        && !s.toString().equals(startDate)) {
                    if (!isProduct) {
                        slotsDayList.clear();
                        timeSlotList.clear();
                        addSlotsAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        tvEndDate.addTextChangedListener(new TextWatcher() {
            private String endDate = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                endDate = s.toString();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (tvStartDate.getText().length() != 0 && tvEndDate.getText().length() != 0
                        && !s.toString().equals(endDate)) {
                    if (!isProduct) {
                        slotsDayList.clear();
                        timeSlotList.clear();
                        addSlotsAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }


    @Override
    public void afterTextChanged(Editable s) {
        if (isAdded()) {
            try {
                String minPrice = tvMinPrice.getText().toString().trim();
                String maxPrice = tvMaxPrice.getText().toString().trim();
                if (Integer.parseInt(minPrice.equals("") ? "0" : minPrice) > Integer.parseInt(maxPrice.equals("") ? "0" : maxPrice)) {
                    AppUtils.getInstance().showToast(mActivity, getString(R.string.min_must_lass_than_max));
//                tvMinPrice.removeTextChangedListener(this);
//                tvMaxPrice.removeTextChangedListener(this);
//                tvMinPrice.setText(String.valueOf(rangeSeekBar.getSelectedMinValue()));
//                tvMaxPrice.setText(String.valueOf(rangeSeekBar.getSelectedMaxValue()));
//                tvMinPrice.addTextChangedListener(this);
//                tvMaxPrice.addTextChangedListener(this);
                    return;
                }
                if (tvMinPrice.hasFocus()) {
                    rangeSeekBar.setSelectedMinValue(Integer.parseInt(minPrice.equals("") ? "0" : minPrice));
                    rangeSeekBar.setSelectedMaxValue(Integer.parseInt(maxPrice.equals("") ? "0" : maxPrice));
                } else if (tvMaxPrice.hasFocus()) {
                    rangeSeekBar.setSelectedMaxValue(Integer.parseInt(maxPrice.equals("") ? "0" : maxPrice));
                    rangeSeekBar.setSelectedMinValue(Integer.parseInt(minPrice.equals("") ? "0" : minPrice));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * method to hit the category list api
     */
    private void hitGetCategoryListApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        Call<ResponseBody> call = apiInterface.hitCategoriesListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }


    /**
     * method to hit the sub-category list api
     *
     * @param catId
     */
    private void hitSubGetCategoryListApi(String catId) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_DEAL_CATEGORY_ID, catId);
        Call<ResponseBody> call = apiInterface.hitGetSubCategoryListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_SUB_CATEGORY);
    }


    /**
     * method to hit the sub-category list api
     */
    private void hitProductHuntApi() {
        isLoading = true;
        AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, true);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_DEAL_CATEGORY_ID, productServiceModel.getCatId());
        params.put(Constants.NetworkConstant.PARAM_SUB_CATEGORY_ID, productServiceModel.getSubcatId());
        params.put(Constants.NetworkConstant.PARAM_HUNT_TITLE, productServiceModel.getHuntTitle());
        params.put(Constants.NetworkConstant.PARAM_PRODUCT_TYPE, productServiceModel.getProductType());
        params.put(Constants.NetworkConstant.PARAM_SHOPPER_ADDRESS, productServiceModel.getAddress());
        params.put(Constants.NetworkConstant.PARAM_SHOPPER_LATITUDE, productServiceModel.getLatitude());
        params.put(Constants.NetworkConstant.PARAM_SHOPPER_LONGITUDE, productServiceModel.getLongitude());
        params.put(Constants.NetworkConstant.PARAM_DESCRIPTION, productServiceModel.getDescription());
        params.put(Constants.NetworkConstant.PARAM_CURRENCY_CODE, currencyCode);
        params.put(Constants.NetworkConstant.PARAM_CURRENCY_SYMBOL, currencySymbol);
        params.put(Constants.NetworkConstant.PARAM_COUNTRY_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.COUNTRY_CODE));
        params.put(Constants.NetworkConstant.PARAM_IMAGE_ARRAY, new Gson().toJson(getImagesArray()));
        if (isProduct) {
            params.put(Constants.NetworkConstant.PARAM_PRICE_START, productServiceModel.getPriceStart());
            params.put(Constants.NetworkConstant.PARAM_PRICE_END, productServiceModel.getPriceEnd());
            params.put(Constants.NetworkConstant.PARAM_EXPECTED_DATE, AppUtils.getInstance().formatDate(productServiceModel.getExpectedDeliveryDate(), DATE_FORMAT, SERVICE_DATE_FORMAT));
            params.put(Constants.NetworkConstant.PARAM_TARGET_AREA, productServiceModel.getTargetArea());
            params.put(Constants.NetworkConstant.PARAM_TARGET_LAT, productServiceModel.getTargetLat());
            params.put(Constants.NetworkConstant.PARAM_TARGET_LONG, productServiceModel.getTargetLong());
            params.put(Constants.NetworkConstant.PARAM_BUDDY_TYPE, productServiceModel.getChooseBuddy());
        }else {
            params.put(Constants.NetworkConstant.PARAM_PRICE_START, productServiceModel.getPriceStart());
            params.put(Constants.NetworkConstant.PARAM_HUNT_START_DATE, AppUtils.getInstance().formatDate(productServiceModel.getHuntStartDate(), DATE_FORMAT, SERVICE_DATE_FORMAT));
            params.put(Constants.NetworkConstant.PARAM_HUNT_END_DATE, AppUtils.getInstance().formatDate(productServiceModel.getHuntEndDate(), DATE_FORMAT, SERVICE_DATE_FORMAT));
            params.put(Constants.NetworkConstant.PARAM_TIME_SLOT_ARRAY, new Gson().toJson(getTimeSlotsArray()));
            params.put(Constants.NetworkConstant.PARAM_IS_RECURSIVE, productServiceModel.getIsRecursive());
            params.put(Constants.NetworkConstant.PARAM_SLOT_SELECTED_DATE, new Gson().toJson(slotsDayList));
        }
        Call<ResponseBody> call;
        if (huntId != null && !huntId.equals("")) {
            params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntId);
            call = apiInterface.hitEditProductHuntApi(AppUtils.getInstance().encryptData(params));
        } else {
            call = apiInterface.hitProductHuntApi(AppUtils.getInstance().encryptData(params));
        }
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_PRODUCT_HUNT);
    }


    /**
     * method to get time slot array
     *
     * @return
     */
    private ArrayList<HashMap<String, String>> getTimeSlotsArray() {
        ArrayList<HashMap<String, String>> slotsList = new ArrayList<>();
        for (ServiceSlot slotsBean : timeSlotList) {
            HashMap<String, String> timeSlotMap = new HashMap<>();
            String slotStartTime = AppUtils.getInstance().formatDate(slotsBean.getSlotStartTime(), "hh:mm a", "HH:mm:ss");
            String slotEndTime = AppUtils.getInstance().formatDate(slotsBean.getSlotEndTime(), "hh:mm a", "HH:mm:ss");
            timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_START_DATE, slotsBean.getSlotStartDate());
            timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_END_DATE, slotsBean.getSlotEndDate());
            timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_START_TIME, slotsBean.isAllDay() ? "00:00:00" : slotStartTime);
            timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_END_TIME, slotsBean.isAllDay() ? "01:00:00" : slotEndTime);
            timeSlotMap.put(Constants.NetworkConstant.PARAM_HUNT_ID, productServiceModel.getId());
            timeSlotMap.put(Constants.NetworkConstant.PARAM_ALL_DAYS, slotsBean.isAllDay() ? "1" : "0");
            timeSlotMap.put(Constants.NetworkConstant.PARAM_PRICE, slotsBean.getPrice());
            timeSlotMap.put(Constants.NetworkConstant.PARAM_SLOT_ID, slotsBean.getId());
            slotsList.add(timeSlotMap);
        }
        return slotsList;
    }


    /**
     * method to get images array
     *
     * @return list of images map
     */
    private ArrayList<HashMap<String, String>> getImagesArray() {
        ArrayList<HashMap<String, String>> imagesList = new ArrayList<>();
        for (int i = 0; i < imagesBeanList.size(); i++) {
            HashMap<String, String> imageMap = new HashMap<>();
            imageMap.put(Constants.NetworkConstant.PARAM_IMAGE_PATH, imagesBeanList.get(i).getServerUrl());
            imageMap.put(Constants.NetworkConstant.PARAM_MEDIA_TYPE, "1");
            imageMap.put(Constants.NetworkConstant.PARAM_DEFAULT_IMAGE, i == 0 ? "1" : "0");
            imagesList.add(imageMap);
        }
        return imagesList;
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_CATEGORIES:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            PreferredCategoriesResponse categoriesResponse = new Gson().fromJson(response, PreferredCategoriesResponse.class);
                            categoryList.clear();
                            String type = isProduct ? "1" : "2";
                            for (com.shopoholic.models.preferredcategorymodel.Result category : categoriesResponse.getResult()) {
                                if (category.getCategoryType().equals(type)) {
                                    categoryList.add(category);
                                }
                            }
                            prefferdCategoriesAdapter.notifyDataSetChanged();
                            break;
                        default:
                            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, false);
                            isLoading = false;
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                    break;

                case Constants.NetworkConstant.REQUEST_SUB_CATEGORY:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            SubCategoryResponse subCategoryResponse = new Gson().fromJson(response, SubCategoryResponse.class);
                            subCategoryList.clear();
                            subCategoryList.addAll(subCategoryResponse.getResult());
                            subCategoryAdapter.notifyDataSetChanged();
                            break;
                        default:
                            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, false);
                            isLoading = false;
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                    break;

                case Constants.NetworkConstant.REQUEST_PRODUCT_HUNT:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            try {
                                JSONObject object = new JSONObject(response);
                                JSONObject data = object.optJSONObject(Constants.NetworkConstant.RESULT);
                                huntId = data.optString(Constants.IntentConstant.HUNT_ID);
                                if (huntId != null && !huntId.equals("")) {
/*
                                    startActivity(new Intent(mActivity, CommonActivity.class)
                                            .putExtra(Constants.IntentConstant.FRAGMENT_TYPE, 0)
                                            .putExtra(Constants.IntentConstant.IS_HUNT, true)
                                            .putExtra(Constants.IntentConstant.HUNT_ID, huntId));
*/
                                    if (mActivity instanceof HomeActivity) {
                                        Intent homeIntent = new Intent(mActivity, HomeActivity.class);
                                        homeIntent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.HUNT);
                                        AppUtils.getInstance().openNewActivity(mActivity, homeIntent);
//                                        if (!mActivity.isFinishing() || !mActivity.isDestroyed()) mActivity.finish();
                                    } else {
                                        Intent intent = new Intent();
                                        intent.putExtra(Constants.IntentConstant.PRODUCT_DETAILS, productServiceModel);

                                        mActivity.setResult(RESULT_OK, intent);
                                        if (!mActivity.isFinishing() || !mActivity.isDestroyed())
                                            mActivity.finish();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case Constants.NetworkConstant.EMAIL_NOT_VERIFIED:
                            String email = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.EMAIL);
                            new AlertDialog.Builder(mActivity, R.style.DatePickerTheme)
//                            .setTitle(getString(R.string.cancel_order))
                                    .setMessage(getString(email.equals("") ? R.string.please_enter_email_address : R.string.email_not_verified))
                                    .setPositiveButton(getString(email.equals("") ? R.string.ok : R.string.resend_link), (dialog, which) -> {
                                        if (!isLoading && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                                            hitResendLinkApi();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                        // do nothing
                                    })
                                    .show();
                            break;
                        default:
                            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, false);
                            isLoading = false;
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                    break;
            }
        }
    }


    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, false);
            imageLoader.setVisibility(View.GONE);
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            AppUtils.getInstance().setButtonLoaderAnimation(mActivity, btnAction, viewButtonLoader, viewButtonDot, false);
            imageLoader.setVisibility(View.GONE);

        }
    }

    @Override
    public void onDestroyView() {
        if (isAdded()) {
            super.onDestroyView();
            unbinder.unbind();
        }
    }

    /**
     * validate the views
     *
     * @return
     */
    private boolean isValidate() {
        String minPrice = tvMinPrice.getText().toString().trim();
        String maxPrice = tvMaxPrice.getText().toString().trim();
        if (tvSelectCategory.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_category));
            return false;
        } else if (tvSelectSubCategory.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_sub_category));
            return false;
        } else if (etHuntName.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_hunt_name));
            return false;
        } else if (isProduct && Integer.parseInt(minPrice.equals("") ? "0" : minPrice) > Integer.parseInt(maxPrice.equals("") ? "0" : maxPrice)) {
//            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_price_range));
            AppUtils.getInstance().showToast(mActivity, getString(R.string.min_must_lass_than_max));
            return false;
        } else if (isProduct && imagesBeanList.size() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_upload_image));
            return false;
        } else if (addDescription.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_description));
            return false;
        } else if (tvAddress.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_address));
            return false;
        } else if (isProduct && tvExpectedDate.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_expected_date));
            return false;
        } else if (currencyCode.equals("") || currencySymbol.equals("")) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_valid_address));
            return false;
        } else if (isProduct && !rbFollowed.isChecked() && !rbNearby.isChecked() && !rbWorked.isChecked() && !rbTargetArea.isChecked()) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_select_buddy_type));
            return false;
        } else if (isProduct && rbTargetArea.isChecked() && tvTargetArea.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_target_area));
            return false;
        } else if (!isProduct && (timeSlotList.size() == 0 || timeSlotList.get(timeSlotList.size() - 1).getSlotStartTime().equals("") || timeSlotList.get(timeSlotList.size() - 1).getSlotEndTime().equals(""))) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.please_enter_slots));
            return false;
        } else if (imageLoader.getVisibility() == View.VISIBLE) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.images_loading_please_wait));
            return false;
        }
        return true;
    }

    /**
     * Checks permission to Write external storage in Marshmallow and above devices
     */
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here

            if (ContextCompat.checkSelfPermission(mActivity, CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mActivity, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mActivity, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mActivity, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mActivity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]
                        {CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, Constants.IntentConstant.REQUEST_GALLERY);
            } else {
                // permission already granted
                ProductFragment.this.startActivityForResult(new Intent(mActivity, CameraGalleryActivity.class)
                                .putExtra("maxSelection", 5 - imagesBeanList.size())
                        , Constants.IntentConstant.REQUEST_GALLERY);
            }
        } else {
            //before marshmallow
            ProductFragment.this.startActivityForResult(new Intent(mActivity, CameraGalleryActivity.class)
                            .putExtra("maxSelection", 5 - imagesBeanList.size())
                    , Constants.IntentConstant.REQUEST_GALLERY);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (isAdded()) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToImageGallery();
            }
        }
    }

    /**
     * create image bean object
     *
     * @param path
     * @return
     */
    private ImageBean addDataInBean(String path, int id) {
        ImageBean bean = new ImageBean();
        bean.setId(String.valueOf(id));
        bean.setName("pic");
        bean.setImagePath(path);
        bean.setLoading(true);
        return bean;
    }

    /**
     * upload file in S3
     *
     * @param path
     */
    private void startUpload(String path) {
        ImageBean bean = addDataInBean(path, ++id);
        imagesBeanList.add(bean);
        imagesAdapter.notifyDataSetChanged();
        mAmazonS3.uploadImage(bean);
        showImageProgress();
    }

    private void goToImageGallery() {
        Intent intent = new Intent(mActivity, ImagesGallery.class);
        intent.putExtra("selectedList", new ArrayList<String>());
        // Set the title
        intent.putExtra("title", "Select Image");
        intent.putExtra("maxSelection", 5); // Optional
        ProductFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_MULTIPLE_IMAGE_INTENT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.IntentConstant.REQUEST_MULTIPLE_IMAGE_INTENT && data != null) {
            if (data.getStringArrayListExtra("result") != null) {
                ArrayList<String> imagesList = data.getStringArrayListExtra("result");
                for (String filePath : imagesList) {
                    startUpload(filePath);
                }
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER) {
            openPlacePicker = false;
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(mActivity, data);
                getCurrentLocation(place);
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_TARGET_PLACE_PICKER) {
            openPlacePicker = false;
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(mActivity, data);
                getTargetLocation(place);
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_GALLERY && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                ArrayList<String> imagesList = data.getExtras().getStringArrayList("result");
                if (imagesList != null) {
                    for (String filePath : imagesList) {
                        startUpload(filePath);
                    }
                }
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_TIME_SLOTS && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            ArrayList<AddSlotsModel> slotList = (ArrayList<AddSlotsModel>) data.getExtras().getSerializable(Constants.IntentConstant.TIME_SLOTS);
            if (slotList != null) {
                timeSlotList.clear();
                for (int i = 0; i < slotList.size(); i++) {
                    timeSlotList.add(AddSlotsModel.getSlotModel(slotList.get(i)));
                    timeSlotList.get(i).setCurrency(currencySymbol);
                }
                addSlotsAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_SLOTS && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            ArrayList<ServiceSlot> slotList = (ArrayList<ServiceSlot>) data.getExtras().getSerializable(Constants.IntentConstant.TIME_SLOTS);
            ArrayList<String> daysList = data.getExtras().getStringArrayList(Constants.IntentConstant.DAYS_LIST);
            if (daysList != null) {
                slotsDayList.clear();
                slotsDayList.addAll(daysList);
            }
            if (slotList != null) {
                timeSlotList.clear();
                timeSlotList.addAll(slotList);
                addSlotsAdapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * Method to get the current location of user
     *
     * @param location
     */
    private void getCurrentLocation(Place location) {
        if (location != null && location.getAddress() != null) {
            String address = "";
            if (location.getName()!= null && !location.getName().equals("") && !location.getName().toString().contains("\"")) {
                address += location.getName() + ", ";
            }
            address += location.getAddress().toString();
            currencySymbol = "";
            currencyCode = "";
            if (productServiceModel == null)
                productServiceModel = new com.shopoholic.models.producthuntlistresponse.Result();
            productServiceModel.setAddress(address);
            productServiceModel.setLatitude(String.valueOf(location.getLatLng().latitude));
            productServiceModel.setLongitude(String.valueOf(location.getLatLng().longitude));
            tvAddress.setText(address);

            new ReverseGeocoding(mActivity, location.getLatLng().latitude, location.getLatLng().longitude, this).execute();
//            ArrayList<String> currency = AppUtils.getInstance().getCurrency(mActivity, location.getLatLng().latitude, location.getLatLng().longitude);
//            currencySymbol = currency.get(0);
//            currencyCode = currency.get(1);
//            minCurrency.setText(TextUtils.concat("(" + currencySymbol + ")"));
//            maxCurrency.setText(TextUtils.concat("(" + currencySymbol + ")"));
        } else {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.unable_to_fetch_location));
        }
    }

    /**
     * Method to get the current location of user
     *
     * @param location
     */
    private void getTargetLocation(Place location) {
        if (location != null && location.getAddress() != null) {
            String address = "";
            if (location.getName()!= null && !location.getName().equals("") && !location.getName().toString().contains("\"")) {
                address += location.getName() + ", ";
            }
            address += location.getAddress().toString();
            if (productServiceModel == null)
                productServiceModel = new com.shopoholic.models.producthuntlistresponse.Result();
            productServiceModel.setTargetArea(address);
            productServiceModel.setTargetLat(String.valueOf(location.getLatLng().latitude));
            productServiceModel.setTargetLong(String.valueOf(location.getLatLng().longitude));
            tvTargetArea.setText(address);
        } else {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.unable_to_fetch_location));
        }
    }

    @Override
    public void uploadSuccess(ImageBean imageBean) {
        if (isAdded()) {
            imageBean.setLoading(false);
            updateImageBeanList(imageBean, 1);
        }
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        if (isAdded()) {
            AppUtils.getInstance().showToast(mActivity, getString(R.string.image_upload_fail));
            updateImageBeanList(bean, 2);
        }
    }

    @Override
    public void uploadProgress(ImageBean bean) {
        if (isAdded()) {
            AppUtils.getInstance().printLogMessage(mActivity.getCallingPackage(), "Uploaded " + bean.getProgress() + " %");

        }
    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {
    }

    /**
     * method to update image bean list
     *
     * @param imageBean
     * @param status
     */
    private void updateImageBeanList(ImageBean imageBean, int status) {
        for (int i = 0; i < imagesBeanList.size(); i++) {
            if (imageBean.getId().equals(imagesBeanList.get(i).getId())) {
                switch (status) {
                    case 1:
                        imagesBeanList.set(i, imageBean);
                        break;
                    case 2:
                        imagesBeanList.remove(imageBean);
                        break;
                }
                break;
            }
        }
        showImageProgress();
        imagesAdapter.notifyDataSetChanged();
    }

    /**
     * method to change loader state
     */
    private void showImageProgress() {
        boolean check = false;
        for (ImageBean bean : imagesBeanList) {
            if (bean.isLoading()) {
                check = true;
                break;
            }
        }
        imageLoader.setVisibility(check ? View.VISIBLE : View.GONE);
    }


    /**
     * method to get data and set values in views
     */
    private void getDataAndSetValue() {
        if (mActivity instanceof CommonActivity) {
            Intent intent = ((CommonActivity) mActivity).getIntent();
            if (intent != null && intent.getExtras() != null) {
                com.shopoholic.models.producthuntlistresponse.Result huntData = (com.shopoholic.models.producthuntlistresponse.Result) intent
                        .getExtras().getSerializable(Constants.IntentConstant.PRODUCT_DETAILS);
                fromClass = intent.getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
                if (huntData != null) {
                    if (huntData.getProductType().equals("1") == isProduct) {
                        productServiceModel = huntData;
                        huntId = huntData.getId();
                        tvSelectCategory.setText(productServiceModel.getCategoryName());
                        tvSelectSubCategory.setText(productServiceModel.getSubCatName());
                        etHuntName.setText(productServiceModel.getHuntTitle());
                        addDescription.setText(productServiceModel.getDescription());
                        if (!productServiceModel.getLatitude().equals("") && !productServiceModel.getLongitude().equals("")) {
                            tvAddress.setText(productServiceModel.getAddress());
                            new ReverseGeocoding(mActivity,
                                    Double.parseDouble(productServiceModel.getLatitude()),
                                    Double.parseDouble(productServiceModel.getLongitude()),
                                   this).execute();
//                            ArrayList<String> currency = AppUtils.getInstance().getCurrency(mActivity,
//                                    Double.parseDouble(productServiceModel.getLatitude()),
//                                    Double.parseDouble(productServiceModel.getLongitude()));
//                            currencySymbol = currency.get(0);
//                            currencyCode = currency.get(1);
                        }
                        if (!productServiceModel.getTargetLat().equals("") && !productServiceModel.getTargetLong().equals("")) {
                            tvTargetArea.setText(productServiceModel.getTargetArea());
                        }
                        if (!huntData.getHuntImage().equals("")) {
                            productServiceModel.setImagesList(new ArrayList<>(Arrays.asList(huntData.getHuntImage().split(","))));
                            selectedImages.addAll(productServiceModel.getImagesList());
                            for (String image : productServiceModel.getImagesList()) {
                                ImageBean imageBean = addDataInBean(image, ++id);
                                imageBean.setLoading(false);
                                imageBean.setServerUrl(image);
                                imagesBeanList.add(imageBean);
                            }
                        }
                        imagesAdapter.notifyDataSetChanged();


                        if (isProduct) {
                            rangeSeekBar.setSelectedMaxValue((int) Double.parseDouble(productServiceModel.getPriceEnd()));
                            rangeSeekBar.setSelectedMinValue((int) Double.parseDouble(productServiceModel.getPriceStart()));
                            tvMinPrice.setText(String.valueOf((int)Double.parseDouble(productServiceModel.getPriceStart())));
                            tvMaxPrice.setText(String.valueOf((int)Double.parseDouble(productServiceModel.getPriceEnd())));
                            tvExpectedDate.setText(AppUtils.getInstance().formatDate(productServiceModel.getExpectedDeliveryDate(), SERVICE_DATE_FORMAT, DATE_FORMAT));
                            changeBuddyType(productServiceModel.getChooseBuddy().contains("1"), rbFollowed);
                            changeBuddyType(productServiceModel.getChooseBuddy().contains("2"), rbWorked);
                            changeBuddyType(productServiceModel.getChooseBuddy().contains("3"), rbNearby);
                            changeBuddyType(productServiceModel.getChooseBuddy().contains("4"), rbTargetArea);
                            minCurrency.setText(TextUtils.concat("(" + currencySymbol + ")"));
                            maxCurrency.setText(TextUtils.concat("(" + currencySymbol + ")"));
                        }else {
                            productServiceModel.setHuntStartDate(AppUtils.getInstance().formatDate(productServiceModel.getHuntStartDate(), SERVICE_DATE_FORMAT, DATE_FORMAT));
                            productServiceModel.setHuntEndDate(AppUtils.getInstance().formatDate(productServiceModel.getHuntEndDate(), SERVICE_DATE_FORMAT, DATE_FORMAT));
                            tvStartDate.setText(productServiceModel.getHuntStartDate());
                            tvEndDate.setText(productServiceModel.getHuntEndDate());
                            if (productServiceModel.getSlotSelectedDate() != null) {
                                for (int i=0; i<productServiceModel.getSlotSelectedDate().size(); i++) {
                                    if (!slotsDayList.contains(productServiceModel.getSlotSelectedDate().get(i).getSelectedDate())) {
                                        slotsDayList.add(productServiceModel.getSlotSelectedDate().get(i).getSelectedDate());
                                    }
                                }
                            }
                            for (int i=0; i<productServiceModel.getServiceSlot().size(); i++) {
                                String startTime = AppUtils.getInstance().formatDate(productServiceModel.getServiceSlot().get(i).getSlotStartTime(), "HH:mm:ss", "hh:mm a");
                                String endTime = AppUtils.getInstance().formatDate(productServiceModel.getServiceSlot().get(i).getSlotEndTime(), "HH:mm:ss", "hh:mm a");
                                productServiceModel.getServiceSlot().get(i).setSlotStartTime(startTime);
                                productServiceModel.getServiceSlot().get(i).setSlotEndTime(endTime);
                            }
                            timeSlotList.addAll(productServiceModel.getServiceSlot());
                            rbRecursive.setChecked(productServiceModel.getIsRecursive().equals("1"));
                            addSlotsAdapter.notifyDataSetChanged();
                        }

                    }
                }
                if (fromClass.equals(Constants.AppConstant.EDIT_HUNT)) {
                    tvSelectCategory.setEnabled(false);
                    tvSelectSubCategory.setEnabled(false);
                    etHuntName.setEnabled(false);
                    rangeSeekBar.setEnabled(false);
                    tvMinPrice.setEnabled(false);
                    tvMaxPrice.setEnabled(false);
                    tvAddress.setEnabled(false);
                    tvExpectedDate.setEnabled(false);
                    tvStartDate.setEnabled(false);
                    tvEndDate.setEnabled(false);
                    rbRecursive.setEnabled(false);
                    tvTimeSlots.setEnabled(false);
                    rvTimeSlots.setEnabled(false);
                }
            }
        }
        if (TextUtils.isEmpty(tvAddress.getText().toString().trim())) {
            if (productServiceModel == null)
                productServiceModel = new com.shopoholic.models.producthuntlistresponse.Result();
            productServiceModel.setLatitude(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.LATITUDE));
            productServiceModel.setLongitude(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.LONGITUDE));
            if (!productServiceModel.getLatitude().equals("") && !productServiceModel.getLongitude().equals("")) {
                tvAddress.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.ADDRESS) + " " +
                        AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.ADDRESS2)).toString().trim());
                productServiceModel.setAddress(tvAddress.getText().toString());
                new ReverseGeocoding(mActivity,
                        Double.parseDouble(productServiceModel.getLatitude()),
                        Double.parseDouble(productServiceModel.getLongitude()),
                        this).execute();
//                ArrayList<String> currency = AppUtils.getInstance().getCurrency(mActivity,
//                        Double.parseDouble(productServiceModel.getLatitude()),
//                        Double.parseDouble(productServiceModel.getLongitude()));
//                currencySymbol = currency.get(0);
//                currencyCode = currency.get(1);
//                minCurrency.setText(TextUtils.concat("(" + currencySymbol + ")"));
//                maxCurrency.setText(TextUtils.concat("(" + currencySymbol + ")"));
            }
        }
        if (isProduct) {
            llSlotsView.setVisibility(View.GONE);
            rlPriceRange.setVisibility(View.VISIBLE);
            llBuddyType.setVisibility(View.VISIBLE);
        } else {
            llSlotsView.setVisibility(View.VISIBLE);
            rlPriceRange.setVisibility(View.GONE);
            llBuddyType.setVisibility(View.GONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * function to set clicked
     */
    private void setClicked() {
        isClicked = true;
        new Handler().postDelayed(() -> isClicked = false, 4000);
    }


    @Override
    public void setAddress(Address address) {
        if (isAdded() && address != null) {
            String countryCode = address.getCountryCode();
            ArrayList<CountryBean> countries = AppUtils.getInstance().getAllCountries(mActivity);
            for (CountryBean country : countries) {
                if (country.getISOCode().equals(countryCode)) {
                    currencySymbol = country.getCurrency();
                    currencyCode = country.getCurrencyCode();
                    break;
                }
            }
            minCurrency.setText(TextUtils.concat("(" + currencySymbol + ")"));
            maxCurrency.setText(TextUtils.concat("(" + currencySymbol + ")"));
        }
    }

    /**
     * method to hit resend link api
     */
    private void hitResendLinkApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitResendLinkApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, null, 1001);
    }

}
