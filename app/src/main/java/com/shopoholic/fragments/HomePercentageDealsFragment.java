package com.shopoholic.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dnitinverma.locationlibrary.RCLocation;
import com.dnitinverma.locationlibrary.interfaces.LocationsCallback;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.shopoholic.R;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.activities.ProductFilterActivity;
import com.shopoholic.activities.ProductServiceDetailsActivity;
import com.shopoholic.adapters.BannerAdapter;
import com.shopoholic.adapters.CategoriesAdapter;
import com.shopoholic.adapters.ClusterLocation;
import com.shopoholic.adapters.FilterBean;
import com.shopoholic.adapters.ProductsAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForDealStore;
import com.shopoholic.dialogs.CustomDialogForSelectUserStatus;
import com.shopoholic.interfaces.LocationDialogCallback;
import com.shopoholic.interfaces.UserStatusDialogCallback;
import com.shopoholic.models.ProductLocation;
import com.shopoholic.models.bannerlistresponse.BannerArr;
import com.shopoholic.models.productdealsresponse.ProductDealsResponse;
import com.shopoholic.models.productdealsresponse.Result;
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
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.app.Activity.RESULT_OK;
import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

/**
 * Created by appinventiv-pc on 2/4/18.
 */

public class HomePercentageDealsFragment extends Fragment implements OnMapReadyCallback, LocationsCallback, NetworkListener {

    @BindView(R.id.tv_location)
    CustomTextView tvLocation;
    @BindView(R.id.tv_status)
    CustomTextView tvStatus;
    @BindView(R.id.iv_status)
    CircleImageView ivStatus;
    @BindView(R.id.iv_show_category)
    ImageView ivShowCategory;
    @BindView(R.id.menu_right_count)
    ImageView menuRightCount;
    @BindView(R.id.tv_filter_count)
    CustomTextView tvFilterCount;
    @BindView(R.id.fl_menu_right_home)
    FrameLayout flMenuRightHome;
    @BindView(R.id.rv_categories)
    RecyclerView rvCategories;
    @BindView(R.id.rv_map_deals)
    RecyclerView rvMapDeals;
    @BindView(R.id.fl_map_view)
    FrameLayout flMapView;
    @BindView(R.id.rv_banners)
    RecyclerView rvBanners;
    @BindView(R.id.rv_deals)
    RecyclerView rvDeals;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.fl_grid_view)
    FrameLayout flGridView;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private Unbinder unbinder;
    private AppCompatActivity mActivity;
    private List<Result> productList;
    private List<Result> mapList;
    private ProductsAdapter productsAdapter;
    private GoogleMap mMap;
    private RCLocation location;
    private Marker mPositionMarker;
    private LinearLayoutManager dealsLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    public boolean isMapShow;
    private ArrayList<com.shopoholic.models.preferredcategorymodel.Result> percentageList;
    private CategoriesAdapter categoryAdapter;
    private double latitude, longitude;
    private String selectedPercentage = "";
    private String selectedCategoryId = "";
    private String selectedSubCategoryId = "";
    private boolean isMoreData;
    private Location currentLocation;
    private boolean isLoading;
    private ClusterManager<ProductLocation> mClusterManager;
    private String range = Constants.NetworkConstant.RANGE;
    private String storeId = "";
    private String postedBy = "";
    private String expireDate = "";
    private int count = 0;
    private String searchText = "";
    private boolean isPagination;
    private SupportMapFragment mapFragment;
    private BannerAdapter bannerAdapter;
    private List<BannerArr> bannerList;
    private Handler handler;
    private Runnable runnable;
    private boolean openPlacePicker = false;
    private ArrayList<String> storeIdList;
    private double mapLat, mapLong;
    private List<Location> mapLocations;
    private boolean isFirst = true;
    private String dealId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_deals, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        setListeners();
        initMap();
        setAdapters();
        initializeLocation();
        return rootView;
    }

    //get broadcast of location permission
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (location != null && ContextCompat.checkSelfPermission(mActivity, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (intent != null && intent.getExtras() != null) {
                    boolean isGPSEnable = intent.getExtras().getBoolean(Constants.IntentConstant.LOCATION, true);
                    if (isGPSEnable) {
                        location.startLocation();
                    } else {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                            count = 0;
                            searchText = "";
                            hitProductsDealsApi();
                        }
                    }
                } else {
                    location.startLocation();
                }
            }
        }
    };


    /**
     * filter broadcast
     */
    private FilterBean filterData;
    private BroadcastReceiver filterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mActivity instanceof HomeActivity && isAdded()) {
                filterData = ((HomeActivity) mActivity).getFilterData();
                applyFilter();

            }
        }
    };


    /**
     * location broadcast
     */
    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mActivity instanceof HomeActivity && isAdded()) {
                filterData = ((HomeActivity) mActivity).getFilterData();
                Place place = PlacePicker.getPlace(mActivity, intent);
                if (place != null && place.getAddress() != null) {
                    String address = "";
                    if (place.getName()!= null && !place.getName().equals("") && !place.getName().toString().contains("\"")) {
                        address += place.getName() + ", ";
                    }
                    address += place.getAddress().toString();
                    tvLocation.setText(address);
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                    setMarkersOnMap(productList);
                    if (mActivity instanceof HomeActivity) {
                        FilterBean filterBean = ((HomeActivity) mActivity).getFilterData();
                        filterBean.setAddress(address);
                        filterBean.setLatitude(latitude);
                        filterBean.setLongitude(longitude);
                    }
                    if (mMap != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
                    if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                        count = 0;
                        hitProductsDealsApi();
                    }
                }
            }
        }
    };


    //get broadcast of location permission
    private BroadcastReceiver searchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mActivity instanceof HomeActivity && isAdded()) {
                if (intent != null && intent.getExtras() != null) {
                    searchText = intent.getExtras().getString(Constants.IntentConstant.SEARCH, "");
                    if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                        progressBar.setVisibility(View.VISIBLE);
                        count = 0;
                        hitProductsDealsApi();
                    }

                }
            }
        }
    };


    /**
     * method to initialize the variables
     */
    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        productList = new ArrayList<>();
        mapList = new ArrayList<>();
        percentageList = new ArrayList<>();
        bannerList = new ArrayList<>();
        storeIdList = new ArrayList<>();
        if (!AppSharedPreference.getInstance().getBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_SIGN_UP)) {
            tvStatus.setVisibility(View.INVISIBLE);
        }
//        AppUtils.getInstance().showHideViewWithAnimation(rvCategories, false);
        for (int i = 0; i < 10; i++) {
            com.shopoholic.models.preferredcategorymodel.Result result = new com.shopoholic.models.preferredcategorymodel.Result();
            percentageList.add(result);
        }
        percentageList.get(0).setSelected(true);
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        dealsLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        gridLayoutManager = new GridLayoutManager(mActivity, 2);
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        productsAdapter = new ProductsAdapter(mActivity, this, productList, (position, view) -> {
            Result product = null;
            if (isMapShow){
                if (mapList.size() > position) {
                    product = mapList.get(position);
                }
            }else {
                if (productList.size() > position) {
                    product = productList.get(position);
                }
            }
            if (product != null) {
                switch (view.getId()) {
                    case R.id.iv_like:
                        if (AppUtils.getInstance().isLoggedIn(mActivity) && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            AppUtils.getInstance().bounceAnimation(mActivity, view);
                            product.setIsFavourite(product.getIsFavourite().equals("1") ? "2" : "1");
                            productsAdapter.notifyItemChanged(position);
                            hitLikeProductsApi(position);
                            Fragment fragment = getParentFragment();
                            if (fragment instanceof HomeFragment) {
                                ((HomeFragment) fragment).changeFavouriteIcon(Constants.IntentConstant.PERCENTAGE, product.getId(), product.getIsFavourite());
                            }
                        }
                        break;
                    case R.id.civ_user_image:
//                        AppUtils.getInstance().showToast(mActivity, getString(R.string.under_development));
                        break;
                    case R.id.iv_product_pic:
//                        AppUtils.getInstance().showToast(mActivity, getString(R.string.under_development));
                        String dealId = product.getId();
                        String dealImage = product.getDealImage();
                        dealImage = dealImage.contains(",") ? dealImage.split(",")[0] : dealImage;
                        Intent intent = new Intent(mActivity, ProductServiceDetailsActivity.class);
                        intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
                        intent.putExtra(Constants.IntentConstant.DEAL_IMAGE, dealImage);
                        ActivityOptionsCompat options = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(mActivity, view, ViewCompat.getTransitionName(view));
                        HomePercentageDealsFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_PRODUCT_DEAL, options.toBundle());
                        break;
                }
            }
        });
        categoryAdapter = new CategoriesAdapter(mActivity, this, percentageList, (position, view) -> {
            switch (view.getId()) {
                case R.id.rl_root_view:
                    if (position > 0) {
                        selectedPercentage = String.valueOf(100 - 10 * position);
                        if (filterData != null)
                            filterData.setDiscountPercentage(100 - 10 * position);
                    } else {
                        selectedPercentage = "";
                        if (filterData != null) filterData.setDiscountPercentage(-1);
                    }
                    for (int i = 0; i < percentageList.size(); i++)
                        percentageList.get(i).setSelected(false);
                    percentageList.get(position).setSelected(true);
                    categoryAdapter.notifyDataSetChanged();
                    if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                        progressBar.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        count = 0;
                        searchText = "";
                        hitProductsDealsApi();
                    }
                    if (position != 0 && mActivity instanceof HomeActivity && ((HomeActivity) mActivity).getFilterData() != null) {
                        ((HomeActivity) mActivity).getFilterData().setDiscountPercentage(100 - 10 * position);
//                            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Constants.IntentConstant.FILTER_DATA));
                    } else if (position == 0 && mActivity instanceof HomeActivity && ((HomeActivity) mActivity).getFilterData() != null) {
                        ((HomeActivity) mActivity).getFilterData().setDiscountPercentage(-1);
                    }
                    break;
            }
        });
        bannerAdapter = new BannerAdapter(mActivity, bannerList, (position, view) -> {
            switch (view.getId()) {
                case R.id.iv_banner:
                case R.id.rl_root_view:
                    if (!bannerList.get(position).getDealId().equals("0")) {
                        String dealId = bannerList.get(position).getDealId();
                        Intent intent = new Intent(mActivity, ProductServiceDetailsActivity.class);
                        intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
                        HomePercentageDealsFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_PRODUCT_DEAL);
                    }
                    break;
            }
        });


        if (mActivity instanceof HomeActivity) {
            selectedPercentage = String.valueOf(((HomeActivity) mActivity).getFilterData().getDiscountPercentage());
            if (selectedPercentage.equals("-1")) {
                selectedPercentage = "";
                if (percentageList.size() > 0) percentageList.get(0).setSelected(true);
            } else {
                if (percentageList.size() > 0) percentageList.get(0).setSelected(false);
                for (int i = 1; i < 10; i++) {
                    if (selectedPercentage.equals(String.valueOf(100 - 10 * i))) {
                        percentageList.get(i).setSelected(true);
                    } else {
                        percentageList.get(i).setSelected(false);
                    }
                }
            }
            categoryAdapter.notifyDataSetChanged();

        }
//        if (mActivity instanceof HomeActivity && ((HomeActivity) mActivity).getFilterData() != null) {
//            ((HomeActivity) mActivity).getFilterData().setPriceRange(90);
//            ((HomeActivity) mActivity).getFilterData().setCount(((HomeActivity) mActivity).getFilterData().getCount() + 1);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(broadcastReceiver, new IntentFilter(Constants.IntentConstant.LOCATION));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(filterReceiver, new IntentFilter(Constants.IntentConstant.FILTER_DATA));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(searchReceiver, new IntentFilter(Constants.IntentConstant.SEARCH));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(locationReceiver, new IntentFilter(Constants.IntentConstant.MAP_LOCATION));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        location.disconnect();
        unbinder.unbind();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(filterReceiver);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(searchReceiver);

    }


    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                count = 0;
                searchText = "";
                hitProductsDealsApi();
            }
        });
        rvDeals.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isMapShow && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = gridLayoutManager.getItemCount();
                    if (!isFirst && firstVisibleItemPosition + totalVisibleItems >= productList.size()) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            isPagination = true;
                            hitProductsDealsApi();
                        }
                    }else isFirst = false;
                }
            }

        });

    }

    /**
     * method to set adapters in views
     */
    private void setAdapters() {
        rvCategories.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);
        rvDeals.setLayoutManager(gridLayoutManager);
        rvDeals.setAdapter(productsAdapter);
        rvMapDeals.setLayoutManager(dealsLayoutManager);
        rvMapDeals.setAdapter(productsAdapter);
        rvBanners.setLayoutManager(linearLayoutManager);
        rvBanners.setAdapter(bannerAdapter);
    }

    /**
     * method to initialize the location
     */
    private void initializeLocation() {
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            hitBannerApi();
        }
        location = new RCLocation();
        location.setActivity(mActivity);
        location.setCallback(this);
        if (ContextCompat.checkSelfPermission(mActivity, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location.startLocation();
        } else {
            if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                count = 0;
                searchText = "";
                hitProductsDealsApi();
            }
        }
    }

    /**
     * method to initialize the variables
     */
    private void initMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @OnClick({R.id.tv_location,/* R.id.iv_map_grid,*/ R.id.fl_menu_right_home, R.id.tv_status, R.id.iv_show_category})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_location:
                if (!openPlacePicker) {
                    try {
                        openPlacePicker = true;
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        HomePercentageDealsFragment.this.startActivityForResult(builder.build(mActivity), Constants.IntentConstant.REQUEST_PLACE_PICKER);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        openPlacePicker = false;
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.iv_show_category:
                if (rvCategories.isShown()) {
//                    AppUtils.getInstance().showHideViewWithAnimation(rvCategories, false);
                    rvCategories.setVisibility(View.GONE);
                    ivShowCategory.setImageResource(R.drawable.ic_home_buddy_all_purple);
                } else {
//                    AppUtils.getInstance().showHideViewWithAnimation(rvCategories, true);
                    rvCategories.setVisibility(View.VISIBLE);
                    ivShowCategory.setImageResource(R.drawable.ic_home_buddy_grid);
                }
                break;
/*
            case R.id.iv_map_grid:
                if (ContextCompat.checkSelfPermission(mActivity, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    onChangeView();
                } else {
                    location.startLocation();
                }
                break;
*/
            case R.id.fl_menu_right_home:
                if (mActivity instanceof HomeActivity) {
                    FilterBean filterBean = ((HomeActivity) mActivity).getFilterData();
                    Intent intent = new Intent(mActivity, ProductFilterActivity.class);
                    intent.putExtra(Constants.IntentConstant.FILTER_DATA, filterBean);
                    HomePercentageDealsFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_FILTER);
                }
                break;
            case R.id.tv_status:
                CustomDialogForSelectUserStatus dialogForSelectUserStatus = new CustomDialogForSelectUserStatus(mActivity, 1, new UserStatusDialogCallback() {

                    @Override
                    public void onSelect(String status, int type) {
                        Fragment fragment = getParentFragment();
                        if (fragment instanceof HomeFragment) {
                            ((HomeFragment) fragment).setUserStatus(type, status);
                        }
                    }
                });
                dialogForSelectUserStatus.show();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) mMap.setOnMapClickListener(latLng -> rvMapDeals.setVisibility(View.GONE));
        setUpCluster();
    }


    /**
     * Method to hit the signup api
     */
    public void hitProductsDealsApi() {
//        setLatLngChange();
        isLoading = true;
        if (layoutNoDataFound != null) layoutNoDataFound.setVisibility(View.GONE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
//        params.put(Constants.NetworkConstant.COUNTRY_CODE, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.COUNTRY_CODE));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        params.put(Constants.NetworkConstant.PARAM_METHOD, Constants.NetworkConstant.GET_ALL_DEALS);
        params.put(Constants.NetworkConstant.PARAM_LATITUDE, latitude == 0.0 ? "" : String.valueOf(latitude));
        params.put(Constants.NetworkConstant.PARAM_LONGITUDE, longitude == 0.0 ? "" : String.valueOf(longitude));
        params.put(Constants.NetworkConstant.PARAM_RANGE, range);
        params.put(Constants.NetworkConstant.PARAM_DISCOUNT, selectedPercentage);
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        params.put(Constants.NetworkConstant.PARAM_TYPE, Constants.NetworkConstant.PERCENTAGE);
        params.put(Constants.NetworkConstant.PARAM_DEAL_CATEGORY_ID, selectedCategoryId);
        params.put(Constants.NetworkConstant.PARAM_SUB_CATEGORY_ID, selectedSubCategoryId);
        params.put(Constants.NetworkConstant.PARAM_STORE, storeId);
        params.put(Constants.NetworkConstant.PARAM_POSTED_BY, postedBy);
        params.put(Constants.NetworkConstant.PARAM_EXPIRE_DATE, expireDate);
        params.put(Constants.NetworkConstant.PARAM_PRODUCT_TYPE, "1");
        params.put(Constants.NetworkConstant.PARAM_SEARCH, searchText);
        params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());

        Call<ResponseBody> call = apiInterface.hitProductsDealsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_PRODUCTS);
    }


    /**
     * Method to hit the signup api
     */
    private void hitBannerApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        params.put(Constants.NetworkConstant.COUNTRY_CODE, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.COUNTRY_CODE));
        params.put(Constants.NetworkConstant.PARAM_TYPE, "1");
        Call<ResponseBody> call = apiInterface.hitBannerApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_LAUNCHER_HOME);
    }

    /**
     * Method to hit the signup api
     *
     * @param position
     */
    public void hitLikeProductsApi(final int position) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_METHOD, Constants.NetworkConstant.SAVE_FAVOURITES_DEALS);
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, isMapShow ? mapList.get(position).getId() : productList.get(position).getId());
        params.put(Constants.NetworkConstant.PARAM_IS_FAVOURITE, isMapShow ? mapList.get(position).getIsFavourite() : productList.get(position).getIsFavourite());
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        Call<ResponseBody> call = apiInterface.hitLikeDealsApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
            }

            @Override
            public void onError(String response, int requestCode) {
                (isMapShow ? mapList.get(position) : productList.get(position)).setIsFavourite((isMapShow ? mapList.get(position) : productList.get(position)).getIsFavourite().equals("1") ? "2" : "1");
                productsAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure() {
                (isMapShow ? mapList.get(position) : productList.get(position)).setIsFavourite((isMapShow ? mapList.get(position) : productList.get(position)).getIsFavourite().equals("1") ? "2" : "1");
                productsAdapter.notifyItemChanged(position);
            }
        }, Constants.NetworkConstant.REQUEST_CATEGORIES);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_PRODUCTS:
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            ProductDealsResponse dealsResponse = new Gson().fromJson(response, ProductDealsResponse.class);
//                            int previousPosition = productList.size();
                            if (!isPagination) {
                                productList.clear();
                            } else {
                                isPagination = false;
                            }
                            productList.addAll(dealsResponse.getResult());
                            productsAdapter.notifyDataSetChanged();
                            checkList();
                            isMoreData = dealsResponse.getNext() != -1;
                            if (isMoreData) count = dealsResponse.getNext();
                            if (productList.size() > 0 || isMapShow) {
                                layoutNoDataFound.setVisibility(View.GONE);
                            } else {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            }
                            setMarkersOnMap(productList);
                            if (mapLat != 0 && mapLong != 0) setList(mapLat, mapLong, 1);
                            if (!dealId.equals("")) setProductList();
//                            if (mapLocations != null && mapLocations.size() > 0) setList(mapLocations);
                            break;
                        case Constants.NetworkConstant.NO_DATA:
                            isMoreData = false;
                            productList.clear();
                            mapList.clear();
                            productsAdapter.notifyDataSetChanged();
                            checkList();
                            if (productList.size() > 0 || isMapShow) {
                                layoutNoDataFound.setVisibility(View.GONE);
                            } else {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            }
                            setMarkersOnMap(productList);
                            break;
                        default:
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                    break;
               /* case Constants.NetworkConstant.REQUEST_LAUNCHER_HOME:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            BannerListResponse bannerListResponse = new Gson().fromJson(response, BannerListResponse.class);
                            bannerList.addAll(bannerListResponse.getWalletDetail().getBannerArr());
                            bannerAdapter.notifyDataSetChanged();
                            setTimer();
                            break;
                        default:
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }
                    break;*/
            }
        }
    }

    /**
     * method to set markers on map
     *
     * @param productList
     */
    private void setMarkersOnMap(List<Result> productList) {
        if (isAdded() && mMap != null) {
            mMap.clear();
            storeIdList.clear();
            mClusterManager.clearItems();
            mClusterManager.cluster();
            if (latitude == 0 && longitude == 0) {
                if (currentLocation != null) {
/*
                    mMap.addMarker(new MarkerOptions().flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_main_map_start_point))
                            .anchor(0.5f, 0.5f)
                            .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
*/
                }
            } else {
/*
                mMap.addMarker(new MarkerOptions().flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_main_map_start_point))
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(latitude, longitude)));
*/
            }
            for (Result product : productList) {
                try {
//            latLngArrayList.add(new LatLng(28.605965, 77.362035));
                    LatLng latLng = null;
                    if (product.getUserType().equals("1")) {
                        if (!storeIdList.contains(product.getStoreId())) {
                            storeIdList.add(product.getStoreId());
                            latLng = new LatLng(Double.parseDouble(product.getStoreLatitude()), Double.parseDouble(product.getStoreLongitude()));
                        }
                    } else {
                        latLng = new LatLng(Double.parseDouble(product.getBuddyLatitude()), Double.parseDouble(product.getBuddyLongitude()));
                    }
                    // Add cluster items (markers) to the cluster manager.
                    if (latLng != null) {
                        ProductLocation productLocation = new ProductLocation(latLng, product.getUserType().equals("1") ? product.getStoreName() : "");
                        productLocation.setType(product.getUserType());
                        mClusterManager.addItem(productLocation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mClusterManager.cluster();
    }


    /**
     * setUp map clustering
     */
    private void setUpCluster() {
        if (isAdded() && mMap != null) {
            // Initialize the manager with the context and the map.
            // (Activity extends context, so we can pass 'this' in the constructor.)
            mClusterManager = new ClusterManager<>(mActivity, mMap);
            // Point the map's listeners at the listeners implemented by the cluster manager.
            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);

            ClusterLocation mRenderer = new ClusterLocation(mActivity, mMap, mClusterManager);
            mClusterManager.setRenderer(mRenderer);
            mClusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm());

            mMap.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setOnClusterClickListener(cluster -> {
                double latitude = cluster.getPosition().latitude;
                double longitude = cluster.getPosition().longitude;
//                setList(latitude, longitude, 1);
                List<Location> locations = new ArrayList<>();
                for (ProductLocation location : cluster.getItems()) {
                    Location loc = new Location("");
                    loc.setLatitude(location.getPosition().latitude);
                    loc.setLongitude(location.getPosition().longitude);
                    locations.add(loc);
                }
                openDealStorePopup(locations);
//                setList(locations);
                return true;
            });
            mClusterManager.setOnClusterItemClickListener(item -> {
                double latitude = item.getPosition().latitude;
                double longitude = item.getPosition().longitude;
                setList(latitude, longitude, 2);
//                List<Location> locations = new ArrayList<>();
//                Location loc = new Location("");
//                loc.setLatitude(item.getPosition().latitude);
//                loc.setLongitude(item.getPosition().longitude);
//                locations.add(loc);
//                setList(locations);
                return true;
            });
        }

    }


    /**
     * function to open popup
     * @param locations
     */
    private void openDealStorePopup(List<Location> locations) {
        if (locations != null && locations.size() > 0) {
            if (mapLocations == null) mapLocations = new ArrayList<>();
            else mapLocations.clear();
            mapLocations.addAll(locations);
            ArrayList<Result> list = new ArrayList<>();
            for (Result product : productList) {
                try {
                    double lati, longi;
                    boolean isExists = false;
                    if (product.getUserType().equals("1")) {
                        lati = Double.parseDouble(product.getStoreLatitude());
                        longi = Double.parseDouble(product.getStoreLongitude());
                        for (Result pro : list) {
                            if (product.getUserType().equals("1") && pro.getStoreId().equals(product.getStoreId())) {
                                isExists = true;
                                break;
                            }
                        }
                    } else {
                        lati = Double.parseDouble(product.getBuddyLatitude());
                        longi = Double.parseDouble(product.getBuddyLongitude());
                    }
                    if (!isExists) {
                        for (Location location : locations) {
                            if (location.getLatitude() == lati && location.getLongitude() == longi) {
                                list.add(product);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            new CustomDialogForDealStore(mActivity, list, (LocationDialogCallback) (lati, longi, dealId) -> {
//                setList(lati, longi, 2);
                mapLat = 0.0;
                mapLong = 0.0;
                this.dealId = dealId;
                setProductList();
            }).show();
        }
    }

    /**
     * set List of map
     *
     * @param latitude
     * @param longitude
     * @param type
     */
    private void setList(double latitude, double longitude, int type) {
        mapLat = latitude;
        mapLong = longitude;
        Location location1 = new Location("");
        Location location2 = new Location("");
        location1.setLatitude(latitude);
        location1.setLongitude(longitude);

        if (mapList == null) mapList = new ArrayList<>();
        else mapList.clear();
        for (Result product : productList) {
            try {
                double lati, longi;
                if (product.getUserType().equals("1")) {
                    lati = Double.parseDouble(product.getStoreLatitude());
                    longi = Double.parseDouble(product.getStoreLongitude());
                } else {
                    lati = Double.parseDouble(product.getBuddyLatitude());
                    longi = Double.parseDouble(product.getBuddyLongitude());
                }
                location2.setLatitude(lati);
                location2.setLongitude(longi);
                if (type == 1) {
                    if (location1.distanceTo(location2) < 5) {
                        mapList.add(product);
                    }
                } else {
                    if (lati == latitude && longi == longitude) {
                        mapList.add(product);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isMapShow) productsAdapter.setProductList(mapList);
        productsAdapter.notifyDataSetChanged();
        checkList();

    }



    /**
     * set List of map
     */
    private void setProductList() {
        if (mapList == null) mapList = new ArrayList<>();
        else mapList.clear();
        for (Result product : productList) {
            try {
                String dealId = "";
                if (product.getUserType().equals("1")) {
                    dealId = product.getStoreId();
                } else {
                    dealId = product.getId();
                }
                if (!this.dealId.equals("") && !dealId.equals("") && dealId.equals(this.dealId)) {
                    mapList.add(product);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isMapShow) productsAdapter.setProductList(mapList);
        productsAdapter.notifyDataSetChanged();
        checkList();
    }

    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * method to set status circle
     */
    public void setStatus(int iconDrawable, String status) {
        switch (iconDrawable) {
            case 1:
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_buddy_available_circle, 0, R.drawable.ic_home_buddy_available_dropdown, 0);
                break;
            case 2:
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_buddy_away_circle, 0, R.drawable.ic_home_buddy_available_dropdown, 0);
                break;
            case 3:
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_buddy_dnd_circle, 0, R.drawable.ic_home_buddy_available_dropdown, 0);
                break;
            default:
                tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_buddy_offline_circle, 0, R.drawable.ic_home_buddy_available_dropdown, 0);
        }
        tvStatus.setText(status);
    }


    /**
     * method to set status circle
     */
    public void resetStatus() {
        if (isAdded()) {
            int status = AppSharedPreference.getInstance().getInt(mActivity, AppSharedPreference.PREF_KEY.USER_ONLINE_STATUS);
//            FirebaseDatabaseQueries.getInstance().setUserStatus(mActivity, status);
            switch (status) {
                case 0:
                case 1:
                    tvStatus.setText(getString(R.string.active));
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_buddy_available_circle, 0, R.drawable.ic_home_buddy_available_dropdown, 0);
                    break;
                case 2:
                    tvStatus.setText(getString(R.string.away));
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_buddy_away_circle, 0, R.drawable.ic_home_buddy_available_dropdown, 0);
                    break;
                case 3:
                    tvStatus.setText(getString(R.string.do_not_disturb));
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_buddy_dnd_circle, 0, R.drawable.ic_home_buddy_available_dropdown, 0);
                    break;
                default:
                    tvStatus.setText(getString(R.string.offline));
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_buddy_offline_circle, 0, R.drawable.ic_home_buddy_available_dropdown, 0);
            }
        }
    }

    /**
     * method to change the view
     */
    public void onChangeView() {
        swipeRefreshLayout.setRefreshing(false);
        SwipeRefreshLayout.LayoutParams rvDealsLayoutParams = rvDeals.getLayoutParams();
        FrameLayout.LayoutParams swipeRefreshLayoutLayoutParams = (FrameLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();
        if (!isMapShow) {
            isMapShow = true;
            if (mActivity instanceof HomeActivity) {
                ((HomeActivity) mActivity).changeMapIcon(R.drawable.ic_home_buddy_grid);
            }
//            ivMapGrid.setImageResource(R.drawable.ic_home_buddy_grid);
//            rvDeals.setBackgroundResource(android.R.color.transparent);
//            rvDealsLayoutParams.height = SwipeRefreshLayout.LayoutParams.WRAP_CONTENT;
//            rvDealsLayoutParams.width = SwipeRefreshLayout.LayoutParams.MATCH_PARENT;
//            swipeRefreshLayoutLayoutParams.height = (int) getResources().getDimension(R.dimen._180sdp);
//            swipeRefreshLayoutLayoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
//            rvDeals.setLayoutParams(rvDealsLayoutParams);
//            swipeRefreshLayout.setLayoutParams(swipeRefreshLayoutLayoutParams);
//            rvDeals.setLayoutManager(dealsLayoutManager);
            flGridView.setVisibility(View.GONE);
//            flMapView.setVisibility(View.VISIBLE);
//            initMap();

            if (latitude == 0 && longitude == 0) {
                if (currentLocation != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 16));
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latitude, longitude), 16));
            }
            productsAdapter.setProductList(mapList);

        } else {
            isMapShow = false;
            if (mActivity instanceof HomeActivity) {
                ((HomeActivity) mActivity).changeMapIcon(R.drawable.ic_home_buddy_grid);
            }
//            ivMapGrid.setImageResource(R.drawable.ic_home_buddy_location_map);
//            rvDeals.setBackgroundResource(R.drawable.bg_login);
//            rvDealsLayoutParams.height = SwipeRefreshLayout.LayoutParams.MATCH_PARENT;
//            rvDealsLayoutParams.width = SwipeRefreshLayout.LayoutParams.MATCH_PARENT;
//            swipeRefreshLayoutLayoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
//            swipeRefreshLayoutLayoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
//            rvDeals.setLayoutParams(rvDealsLayoutParams);
//            swipeRefreshLayout.setLayoutParams(swipeRefreshLayoutLayoutParams);
//            rvDeals.setLayoutManager(gridLayoutManager);
            flGridView.setVisibility(View.VISIBLE);
//            flMapView.setVisibility(View.GONE);
//            if (mapFragment != null){
//                getChildFragmentManager().beginTransaction().remove(mapFragment).commit();
//            }
            productsAdapter.setProductList(productList);
        }
        swipeRefreshLayout.setEnabled(!isMapShow);
        productsAdapter.notifyDataSetChanged();
        checkList();

        if (productList.size() > 0 || isMapShow) {
            layoutNoDataFound.setVisibility(View.GONE);
        } else {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void setLocation(Location mCurrentLocation) {
        if (isAdded()) {
            currentLocation = mCurrentLocation;
            if (mCurrentLocation == null)
                return;
            /*if (mPositionMarker == null && mMap != null) {
                mPositionMarker = mMap.addMarker(new MarkerOptions().flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_main_map_start_point))
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
            }*/
            if (latitude == 0.0 && longitude == 0.0) {
                latitude = mCurrentLocation.getLatitude();
                longitude = mCurrentLocation.getLongitude();
                if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                    count = 0;
                    searchText = "";
                    hitProductsDealsApi();
                }
            } else {
                latitude = mCurrentLocation.getLatitude();
                longitude = mCurrentLocation.getLongitude();
            }
            location.getAddress();
//            AppUtils.getInstance().animateMarker(mPositionMarker, mCurrentLocation); // Helper method for smooth
        }
    }

    @Override
    public void setAddress(Address address) {
        if (isAdded() && address != null) {
            String locality = "";
            if (address.getLocality() != null && !address.getLocality().equals("")) {
                locality = address.getLocality() + ", ";
            }
            tvLocation.setText(TextUtils.concat(locality + address.getCountryName()));
        }
    }

    @Override
    public void setLatAndLong(Address location) {
    }

    @Override
    public void disconnect() {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(filterReceiver);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(searchReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        openPlacePicker = false;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_PRODUCT_DEAL && resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            String productId = data.getExtras().getString(Constants.IntentConstant.PRODUCT_ID, "");
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId().equals(productId)) {
                    productList.remove(i);
                    productsAdapter.notifyItemRemoved(i);
                    productsAdapter.notifyItemRangeChanged(i, productList.size());
                    break;
                }
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_PRODUCT_DEAL && resultCode == Activity.RESULT_CANCELED && data != null && data.getExtras() != null) {
            String productId = data.getExtras().getString(Constants.IntentConstant.PRODUCT_ID, "");
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId().equals(productId)) {
                    productList.get(i).setIsFavourite(data.getExtras().getString(Constants.IntentConstant.IS_FAVOURITE));
                    productsAdapter.notifyItemChanged(i);
                    break;
                }
            }
        } else if (requestCode == Constants.IntentConstant.REQUEST_FILTER && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            filterData = (FilterBean) data.getExtras().getSerializable(Constants.IntentConstant.FILTER_DATA);
            applyFilter();
        } else if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER && resultCode == RESULT_OK && data != null) {
            data.setAction(Constants.IntentConstant.MAP_LOCATION);
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(data);
        }
    }


    /**
     * method to set the lat lng change
     */
    private void setLatLngChange() {
        if (currentLocation != null) {
            if (latitude == currentLocation.getLatitude() && longitude == currentLocation.getLongitude()) {
                latitude = 0.0;
                longitude = 0.0;
            }
        }
    }

    /**
     * method to change the favourite icon of other fragment
     *
     * @param id
     * @param isFavourite
     */
    public void changeFavouriteIcon(String id, String isFavourite) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equals(id)) {
                productList.get(i).setIsFavourite(isFavourite);
                productsAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * method to set the timer
     */
    private void setTimer() {
        if (bannerList.size() > 1) {
            handler = new Handler();
            runnable = () -> {
                if (isAdded()) {
                    int position = linearLayoutManager.findFirstVisibleItemPosition();
                    rvBanners.smoothScrollToPosition(position < bannerList.size() - 1 ? position + 1 : 0);
                    handler.postDelayed(runnable, Constants.AppConstant.BANNER_TIME_OUT);
                }
            };
            if (isAdded()) {
                handler.postDelayed(runnable, Constants.AppConstant.BANNER_TIME_OUT);
            }
        }
    }

    /**
     * set banner on views
     *
     * @param banners
     */
    public void setBanners(List<BannerArr> banners) {
        bannerList.addAll(banners);
        bannerAdapter.notifyDataSetChanged();
        setTimer();
    }

    /**
     * method to change map grid
     */
    public void changeMapGrid() {
        if (ContextCompat.checkSelfPermission(mActivity, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onChangeView();
        } else {
            location.startLocation();
        }
    }

    /**
     * method to apply filter
     */
    private void applyFilter() {
        if (filterData != null) {
            if (filterData.getCount() > 0) tvFilterCount.setVisibility(View.VISIBLE);
            else tvFilterCount.setVisibility(View.GONE);
            selectedCategoryId = "";
            selectedSubCategoryId = "";
            storeId = "";
            expireDate = "";
            latitude = filterData.getLatitude();
            longitude = filterData.getLongitude();
            range = filterData.getRange() == 0 ? "20" : String.valueOf(filterData.getRange());
            postedBy = filterData.getPostedBy() == 0 ? "" : String.valueOf(filterData.getPostedBy());
//                    dealType = filterData.getTypeOfDeals() == 0 ? "" : String.valueOf(filterData.getTypeOfDeals());
            if (filterData.getExpireDate() != null && !filterData.getExpireDate().equals(""))
                expireDate = AppUtils.getInstance().formatDate(filterData.getExpireDate(), DATE_FORMAT, SERVICE_DATE_FORMAT);
            if (filterData.getStoreDetail() != null && filterData.getStoreDetail().size() > 0) {
                storeId = "";
                for (int i = 0; i < filterData.getStoreDetail().size(); i++) {
                    if (i != 0) {
                        storeId += ",";
                    }
                    storeId += filterData.getStoreDetail().get(i).getStoreId();
                }
            }
            if (filterData.getCategoryDetails() != null) {
                selectedCategoryId = filterData.getCategoryDetails().getCatId();
            }
            if (filterData.getSubCategoryDetails() != null) {
                selectedSubCategoryId = filterData.getSubCategoryDetails().getSubCatId();
            }
            selectedPercentage = String.valueOf(filterData.getDiscountPercentage());
            if (selectedPercentage.equals("-1")) {
                selectedPercentage = "";
                if (percentageList.size() > 0) percentageList.get(0).setSelected(true);
                for (int i = 1; i < 10; i++) {
                    percentageList.get(i).setSelected(false);
                }
            } else {
                if (percentageList.size() > 0) percentageList.get(0).setSelected(false);
                for (int i = 1; i < 10; i++) {
                    if (selectedPercentage.equals(String.valueOf(100 - 10 * i))) {
                        percentageList.get(i).setSelected(true);
                    } else {
                        percentageList.get(i).setSelected(false);
                    }
                }
            }
            categoryAdapter.notifyDataSetChanged();
            if (AppUtils.getInstance().isInternetAvailable(mActivity) && !isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                count = 0;
                searchText = "";
                hitProductsDealsApi();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latitude, longitude), 16));
            }
            if (filterData.getAddress() != null && !filterData.getAddress().equals("")) {
                tvLocation.setText(filterData.getAddress());
            }
        }
    }



    /**
     * function to check list size
     */
    private void checkList() {
        if (isMapShow) {
            rvMapDeals.setVisibility(mapList.size() > 0 ? View.VISIBLE : View.GONE);
        }
    }

}