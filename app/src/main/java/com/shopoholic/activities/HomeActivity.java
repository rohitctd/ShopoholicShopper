package com.shopoholic.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dnitinverma.locationlibrary.RCLocation;
import com.dnitinverma.mylibrary.contacts.ContactUpdate;
import com.dnitinverma.mylibrary.databaseLibrary.ContactsDatabase;
import com.dnitinverma.mylibrary.interfaces.ContactUpdationListener;
import com.dnitinverma.mylibrary.interfaces.ReceiveContactUpdateCallBack;
import com.dnitinverma.mylibrary.models.ContactResult;
import com.dnitinverma.mylibrary.services.ContactMonitorService;
import com.dnitinverma.mylibrary.services.CopyContactService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.FilterBean;
import com.shopoholic.adapters.SideMenuAdapter;
import com.shopoholic.coachmark.CoachMarksImpl;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.fragments.ChatFragment;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.firebasechat.utils.FirebaseEventListeners;
import com.shopoholic.fragments.FavouritesFragment;
import com.shopoholic.fragments.HomeFragment;
import com.shopoholic.fragments.KYCFragment;
import com.shopoholic.fragments.LauncherHomeFragment;
import com.shopoholic.fragments.MyBuddyFragment;
import com.shopoholic.fragments.MyFriendsFragment;
import com.shopoholic.fragments.MyOrdersFragment;
import com.shopoholic.fragments.MyWalletFragment;
import com.shopoholic.fragments.NotificationFragment;
import com.shopoholic.fragments.ProductHuntFragment;
import com.shopoholic.fragments.ProductHuntListingFragment;
import com.shopoholic.fragments.ProfileFragment;
import com.shopoholic.fragments.SettingsFragment;
import com.shopoholic.fragments.UnderDevelopmentFragment;
import com.shopoholic.interfaces.PopupItemDialogCallback;
import com.shopoholic.models.MenuItemModel;
import com.shopoholic.models.NotificationBean;
import com.shopoholic.models.OrderBean;
import com.shopoholic.models.profileresponse.ProfileResponse;
import com.shopoholic.models.unreadcountresponse.UnreadCountResponse;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import events.com.socketlibrary.SocketIO.Syncing;
import events.com.socketlibrary.interfaces.ResponseListener;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_IS_APP_USER;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_PHONE_WITH_CODE;

public class HomeActivity extends BaseActivity implements ReceiveContactUpdateCallBack, ContactUpdationListener, ResponseListener {

    @BindView(R.id.fl_home_container)
    FrameLayout flHomeContainer;
    @BindView(R.id.iv_user_image)
    CircleImageView ivUserImage;
    @BindView(R.id.tv_user_name)
    CustomTextView tvUserName;
    @BindView(R.id.iv_verify_email)
    ImageView ivVerifyEmail;
    @BindView(R.id.rv_menu_item_layout)
    RecyclerView rvMenuItemLayout;
    @BindView(R.id.side_panel)
    DrawerLayout sidePanel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.layout_menu_profile)
    RelativeLayout layoutMenuProfile;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.view_profile)
    CustomTextView viewProfile;
    @BindView(R.id.profile_toolbar_view)
    View profileToolbarView;
    @BindView(R.id.wallet_toolbar_view)
    View walletToolbarView;
    @BindView(R.id.menu_right_count)
    ImageView menuRightCount;
    @BindView(R.id.tv_filter_count)
    CustomTextView tvFilterCount;
    @BindView(R.id.fl_menu_right_home)
    FrameLayout flMenuRightHome;
    @BindView(R.id.fl_menu_right)
    FrameLayout flMenuRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.fl_profile)
    FrameLayout flProfile;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.iv_unread)
    ImageView ivUnread;
    @BindView(R.id.ll_user)
    LinearLayout llUser;
    //    @BindView(R.id.rb_buddy_rating)
//    com.whinc.widget.ratingbar.RatingBar ratingBar;
    private SideMenuAdapter sideMenuAdapter;
    private Menu menu;
    private boolean doubleBackToExitPressedOnce;
    private ArrayList<MenuItemModel> menuItemsList;
    private int previousPosition = -101;
    private FilterBean filterBean;
    private String fromClass = "";
    private String queryParams;
    private OrderBean orderBean;

    private Syncing syncing;
    private ArrayList<ContactResult> contactsList = new ArrayList<>();
    public String type = "";
    private FirebaseEventListeners unreadListener;
    public int unreadHunt = 0;
    public int unreadChat = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AppUtils.getInstance().removeNotificationsPopups(this);
        checkLocationPermission();
        ButterKnife.bind(this);
        initializeVariables();
        setAdapters();
        setListeners();
        getDataFromIntent();
        checkContactPermission();
    }

    /**
     * method to check the contact permission given or not
     */
    private void checkContactPermission() {

        if (!AppSharedPreference.getInstance().getString(HomeActivity.this, AppSharedPreference.PREF_KEY.USER_ID).equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    startSyncing();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
                }
            } else {
                startSyncing();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        setToolbar();
        if (AppUtils.getInstance().isUserLogin(this)) {
            hitGetUnreadCountApi();
            unreadListener = new FirebaseEventListeners() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            unreadChat = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                unreadChat += (long)snapshot.getValue();
                                if (unreadChat > 0) {
                                    break;
                                }
                            }
                            updateUnreadStatus();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        unreadChat = 0;
                        updateUnreadStatus();
                    }
                }
            };
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.UNREAD_COUNT)
                    .child(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID))
                    .addValueEventListener(unreadListener);
        }
    }

    @Override
    protected void onStop() {
        if (unreadListener != null) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.UNREAD_COUNT)
                    .child(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID))
                    .removeEventListener((ValueEventListener) unreadListener);
        }
        super.onStop();
    }

    /**
     * method to get data from intent
     */
    private void getDataFromIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
        }
        if (getIntent() != null && getIntent().getData() != null) {
            queryParams = getIntent().getData().getQueryParameter(Constants.IntentConstant.DEAL_ID);
        }

        if (fromClass.equals(Constants.AppConstant.QR_CODE)) {
            previousPosition = 0;
            resetSideMenu(1);
            setFragmentOnContainer(1);
        } else if (fromClass.equals(Constants.AppConstant.HUNT)) {
            previousPosition = 0;
            resetSideMenu(8);
            setFragmentOnContainer(8);
        } else if (fromClass.equals(Constants.AppConstant.NOTIFICATION)) {
            handelNotificationFlow();
        } else if (queryParams != null && !queryParams.equals("")) {
            try {
                String dealId = new String(Base64.decode(queryParams, Base64.DEFAULT), "UTF-8");
                Intent intent = new Intent(this, ProductServiceDetailsActivity.class);
                intent.putExtra(Constants.IntentConstant.DEAL_ID, dealId);
                startActivity(intent);
                resetSideMenu(0);
                setFragmentOnContainer(0);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            resetSideMenu(0);
            setFragmentOnContainer(0);
        }
    }

    /**
     * method to handel notification flow
     */
    private void handelNotificationFlow() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            int type = getIntent().getExtras().getInt(Constants.NetworkConstant.TYPE);
            switch (type) {
                case 1:
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                    if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra(FirebaseConstants.NOTIFICATION)) {
                        final NotificationBean notificationBean = (NotificationBean) getIntent().getExtras().getSerializable(FirebaseConstants.NOTIFICATION);
                        if (notificationBean != null) {
                            Intent intent = new Intent(this, ProductServiceDetailsActivity.class);
                            intent.putExtra(Constants.IntentConstant.DEAL_ID, notificationBean.getDealId());
                            startActivity(intent);
                        }
                    }
                    break;
                case 2:
                    if (previousPosition != 1) {
                        previousPosition = 0;
                        resetSideMenu(1);
                        setFragmentOnContainer(1);
                    }
                    break;
                case 3:
                case 8:
                    if (previousPosition != 8) {
                        previousPosition = 0;
                        previousPosition = 0;
                        resetSideMenu(8);
                        setFragmentOnContainer(8);
                    }
                    break;
                case 10:
                    if (previousPosition != 5) {
                        previousPosition = 0;
                        resetSideMenu(5);
                        setFragmentOnContainer(5);
                    }
                    break;
                case 11:
                case 12:
                    if (previousPosition != 3) {
                        previousPosition = 0;
                        resetSideMenu(3);
                        setFragmentOnContainer(3);
                    }
                    break;
                default:
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                    setIntent(null);
            }
        }
    }

    /**
     * method to check the location permission
     */
    public void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, Constants.IntentConstant.REQUEST_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.IntentConstant.REQUEST_LOCATION:
                boolean isRationalGalleryStorage = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                        ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.LOCATION));
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_location_permission));
                }

                break;
            case RCLocation.FINE_LOCATION_PERMISSIONS:
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.LOCATION));
                break;
            case 100:
                boolean isRationalContact = false;
                if (grantResults.length > 0)
                    isRationalContact = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                            ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSyncing();
                } else if (isRationalContact) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_contact_permission));
                }
                break;
        }
    }


    /**
     * Method to set the toolbar
     */
    public void setToolbar() {
        tvUserName.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.FIRST_NAME) + " " +
                AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LAST_NAME)));
        AppUtils.getInstance().setCircularImages(this, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_IMAGE),
                ivUserImage, R.drawable.ic_side_menu_user_placeholder);

        if (AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")
//                           && !AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.PHONE_NO).equals("")
        ) {
                ivVerifyEmail.setVisibility(View.GONE);
        } else {
            ivVerifyEmail.setVisibility(View.VISIBLE);
        }

        if (!AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_SIGN_UP)) {
            tvUserName.setText(getString(R.string.guest_user));
            viewProfile.setText(getString(R.string.login));
            ivVerifyEmail.setVisibility(View.GONE);
        }
    }


    /**
     * method to initialize the views
     */
    private void initializeVariables() {
        ContactUpdate.getInstance().setReceiveContactUpdateCallBack(this);
        ContactUpdate.getInstance().setContactUpdationListener(this);
        String[] sideMenuItems = getResources().getStringArray(R.array.side_menu_items);
        final TypedArray menuItemsImage = getResources().obtainTypedArray(R.array.menu_items_image);
        TypedArray menuItemsImageDeselected = getResources().obtainTypedArray(R.array.menu_items_image_deselected);
        menuItemsList = new ArrayList<>();
        for (int i = 0; i < sideMenuItems.length; i++) {
            MenuItemModel menuItemModel = new MenuItemModel();
            menuItemModel.setItemName(sideMenuItems[i]);
            menuItemModel.setSelectedResourceId(menuItemsImage.getResourceId(i, -1));
            menuItemModel.setDeselectedResourceId(menuItemsImageDeselected.getResourceId(i, -1));
            menuItemsList.add(menuItemModel);
        }
        menuItemsImage.recycle();
        menuItemsImageDeselected.recycle();

//        if (AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_SIGN_UP)) {
//            MenuItemModel menuItemModel = new MenuItemModel();
//            menuItemModel.setItemName("LOGOUT");
//            menuItemModel.setSelectedResourceId(R.drawable.ic_home_buddy_available_circle);
//            menuItemModel.setDeselectedResourceId(R.drawable.ic_home_buddy_available_circle);
//            menuItemsList.add(menuItemModel);
//        }

        menuItemsList.get(0).setSelected(true);
        sideMenuAdapter = new SideMenuAdapter(this, menuItemsList, (position, view) -> {
            new Handler().postDelayed(() -> {
                if (sidePanel.isDrawerOpen(GravityCompat.START)) {
                    sidePanel.closeDrawers();
                }
            }, 100);
            if (previousPosition != position) {
                resetSideMenu(position);
                setFragmentOnContainer(position);
            }
        });
        FirebaseDatabaseQueries.getInstance().updateUserData(this);

        syncing = new Syncing();
        syncing.start(this, Constants.NetworkConstant.SOCKET_BASE_URL, Constants.AppConstants.CONTACTS_SEND_EVENT, Constants.AppConstants.CONTACT_SYNCED_EVENT, Constants.AppConstants.ERROR_EVENT, Constants.AppConstants.SOCKET_CONNECT_EVENT, Constants.AppConstants.SOCKET_DISCONNECT_EVENT, Constants.AppConstants.DELETE_CONTACT_EVENT, this, AppSharedPreference.getInstance().getString(HomeActivity.this, AppSharedPreference.PREF_KEY.USER_ID));

    }


    /**
     * Method to set fragment on container
     *
     * @param position
     */
    public void setFragmentOnContainer(int position) {
        profileToolbarView.setVisibility(View.GONE);
        walletToolbarView.setVisibility(View.GONE);
        layoutToolbar.setBackgroundResource(R.drawable.toolbar_gradient);
        menuRight.setVisibility(View.GONE);
        flMenuRightHome.setVisibility(View.GONE);
        tvFilterCount.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        Fragment fragment;
        switch (position) {
            case 0:
                filterBean = new FilterBean();

                Bundle bundle = new Bundle();
                bundle.putString(Constants.IntentConstant.FROM_CLASS, type);
                fragment = new HomeFragment();
                fragment.setArguments(bundle);
                AppUtils.getInstance().replaceFragments(HomeActivity.this,
                        R.id.fl_home_container, fragment, HomeFragment.class.getName(), false);
                if (AppUtils.getInstance().isUserLogin(this)) {
                    tvTitle.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.FIRST_NAME)
                            + " " + getString(R.string.home)));
                } else {
                    tvTitle.setText(getString(R.string.shopper_home));
                }
                flMenuRightHome.setVisibility(View.VISIBLE);
                menuSecondRight.setVisibility(View.VISIBLE);
//                menuRight.setVisibility(View.VISIBLE);
                menuThirdRight.setVisibility(View.VISIBLE);
                menuSecondRight.setImageResource(R.drawable.ic_home_buddy_location_map_white);
                menuThirdRight.setImageResource(R.drawable.ic_shopper_home_cards_search);

                break;
            case 1:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    orderBean = new OrderBean();
                    fragment = new MyOrdersFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, MyOrdersFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.my_orders));
                    flMenuRightHome.setVisibility(View.VISIBLE);
//                    menuRight.setImageResource(R.drawable.ic_home_cards_filter);
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 2:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new MyBuddyFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, MyBuddyFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.my_buddy));
                    menuRight.setVisibility(View.VISIBLE);
                    menuRight.setImageResource(R.drawable.ic_shppers_order_reverse);
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 3:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    checkContactPermission();
                    fragment = new MyFriendsFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, MyFriendsFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.my_friends));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 4:
                walletToolbarView.setVisibility(View.VISIBLE);
                layoutToolbar.setBackgroundResource(android.R.color.transparent);
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new MyWalletFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, MyWalletFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.my_wallet));
                }else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;
            case 5:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new ChatFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, ChatFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.chat));
                    menuRight.setVisibility(View.GONE);
//                    menuRight.setImageResource(R.drawable.ic_chat_list_add);
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 6:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new FavouritesFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, FavouritesFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.favourites));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 7:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new ProductHuntFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, ProductHuntFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.product_hunt));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 8:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new ProductHuntListingFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, ProductHuntListingFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.product_hunt_listing));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 9:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new NotificationFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, NotificationFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.notifications));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            case 10:
                if (AppUtils.getInstance().isLoggedIn(this)) {
                    fragment = new KYCFragment();
                    AppUtils.getInstance().replaceFragments(HomeActivity.this,
                            R.id.fl_home_container, fragment, KYCFragment.class.getName(), false);
                    tvTitle.setText(getString(R.string.kyc));
                } else {
                    resetSideMenu(0);
                    setFragmentOnContainer(0);
                }
                break;

            /*default:
                fragment = new LauncherHomeFragment();
                AppUtils.getInstance().replaceFragments(HomeActivity.this,
                        R.id.fl_home_container, fragment, LauncherHomeFragment.class.getName(), false);
                if (AppUtils.getInstance().isUserLogin(this)) {
                    tvTitle.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.FIRST_NAME)
                            + " " + getString(R.string.home)));
                }else {
                    tvTitle.setText(getString(R.string.shopper_home));
                }*/
/*

                Bundle bundle = new Bundle();
                bundle.putString(Constants.IntentConstant.FROM_CLASS, type);
                fragment = new HomeFragment();
                fragment.setArguments(bundle);
                AppUtils.getInstance().replaceFragments(HomeActivity.this,
                        R.id.fl_home_container, fragment, HomeFragment.class.getName(), false);
                tvTitle.setText(getString(R.string.shopper_home));
                flMenuRightHome.setVisibility(View.VISIBLE);
                menuSecondRight.setVisibility(View.VISIBLE);
                menuSecondRight.setImageResource(R.drawable.ic_shopper_home_cards_search);
*/

//                break;
        }
    }

    /**
     * method to set adapter on views
     */
    private void setAdapters() {
        rvMenuItemLayout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMenuItemLayout.setAdapter(sideMenuAdapter);
    }

    /**
     * method to set listeners on views
     */
    private void setListeners() {
        sidePanel.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (AppUtils.getInstance().isUserLogin(HomeActivity.this)) {
                    hitGetUnreadCountApi();
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }


    /**
     * method to get that load fragment is main
     *
     * @return
     */
    private boolean isMainFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_home_container);
        return fragment instanceof HomeFragment || fragment instanceof MyOrdersFragment ||
                fragment instanceof UnderDevelopmentFragment || fragment instanceof MyBuddyFragment || fragment instanceof ChatFragment
                || fragment instanceof MyFriendsFragment || fragment instanceof MyWalletFragment || fragment instanceof FavouritesFragment
                || fragment instanceof ProductHuntFragment || fragment instanceof ProductHuntListingFragment || fragment instanceof SettingsFragment
                || fragment instanceof ProfileFragment || fragment instanceof NotificationFragment || fragment instanceof LauncherHomeFragment;
    }


    @OnClick({R.id.tv_user_name, R.id.layout_menu_profile, R.id.iv_setting, R.id.fl_menu, R.id.iv_menu, R.id.fl_menu_right_home, R.id.menu_right, R.id.menu_second_right, R.id.menu_third_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (sidePanel.isDrawerOpen(GravityCompat.START)) {
                            sidePanel.closeDrawers();
                        }
                    }
                }, 100);

                menuRight.setVisibility(View.GONE);
                flMenuRightHome.setVisibility(View.GONE);
                menuSecondRight.setVisibility(View.GONE);
                menuThirdRight.setVisibility(View.GONE);
                profileToolbarView.setVisibility(View.GONE);
                walletToolbarView.setVisibility(View.GONE);
                layoutToolbar.setBackgroundResource(R.drawable.toolbar_gradient);

                Fragment settingsFragment = new SettingsFragment();
                tvTitle.setText(getString(R.string.settings));
                AppUtils.getInstance().replaceFragments(HomeActivity.this,
                        R.id.fl_home_container, settingsFragment, SettingsFragment.class.getName(), false);
                resetSideMenu(-1);
                break;
            case R.id.layout_menu_profile:
                openProfileFragment();
                break;
            case R.id.fl_menu:
            case R.id.iv_menu:
                if (isMainFragment()) {
                    sidePanel.openDrawer(GravityCompat.START);
                } else {
                    onBackPressed();
                }
                break;
            case R.id.fl_menu_right_home:
            case R.id.menu_right:
                final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_home_container);
                Intent intent;
                if (fragment instanceof HomeFragment) {
//                    intent = new Intent(this, ProductFilterActivity.class);
                    intent = new Intent(this, ProductFilterActivityTest.class);
                    intent.putExtra(Constants.IntentConstant.FILTER_DATA, filterBean);
                    startActivityForResult(intent, Constants.IntentConstant.REQUEST_FILTER);
                } else if (fragment instanceof MyOrdersFragment) {
                    intent = new Intent(this, FilterOrderActivity.class);
                    intent.putExtra(Constants.IntentConstant.FILTER_DATA, orderBean);
                    startActivityForResult(intent, Constants.IntentConstant.REQUEST_ORDER_FILTER);
                } else if (fragment instanceof MyBuddyFragment) {
                    AppUtils.getInstance().showMorePopUp(this, menuRight, getString(R.string.rating), getString(R.string.sort),
                            "", 1, new PopupItemDialogCallback() {
                                @Override
                                public void onItemOneClick() {
                                    ((MyBuddyFragment) fragment).sortBuddyList(1);
                                }

                                @Override
                                public void onItemTwoClick() {
                                    ((MyBuddyFragment) fragment).sortBuddyList(2);
                                }

                                @Override
                                public void onItemThreeClick() {
                                }
                            });
                }
                break;
            case R.id.menu_second_right:
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.MAP_GRID));
                break;
            case R.id.menu_third_right:
                Fragment homeFragment = getSupportFragmentManager().findFragmentById(R.id.fl_home_container);
                Intent menuIntent;
                if (homeFragment instanceof HomeFragment) {
                    menuIntent = new Intent(this, SearchProductsActivity.class);
                    startActivityForResult(menuIntent, Constants.IntentConstant.REQUEST_SEARCH);
                }
                break;
        }
    }


    /**
     * function to open profile fragment
     */
    private void openProfileFragment() {
        new Handler().postDelayed(() -> {
            if (sidePanel.isDrawerOpen(GravityCompat.START)) {
                sidePanel.closeDrawers();
            }
        }, 100);

        menuRight.setVisibility(View.GONE);
        flMenuRightHome.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        if (AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_SIGN_UP)) {
            profileToolbarView.setVisibility(View.VISIBLE);
            walletToolbarView.setVisibility(View.GONE);
            layoutToolbar.setBackgroundResource(android.R.color.transparent);
            Fragment fragment = new ProfileFragment();
            tvTitle.setText(getString(R.string.my_profile));
            AppUtils.getInstance().replaceFragments(HomeActivity.this,
                    R.id.fl_home_container, fragment, ProfileFragment.class.getName(), false);
            resetSideMenu(-10);
        } else {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            AppUtils.getInstance().openNewActivity(this, loginIntent);
//                    finish();
        }
    }

    /**
     * method to reset side menu
     *
     * @param position
     */
    public void resetSideMenu(int position) {
        if (previousPosition >= 0) menuItemsList.get(previousPosition).setSelected(false);
        sideMenuAdapter.notifyItemChanged(previousPosition);
        if (position >= 0) menuItemsList.get(position).setSelected(true);
        previousPosition = position;
        sideMenuAdapter.notifyItemChanged(position);
    }

    @Override
    public void onBackPressed() {
        if (isMainFragment()) {
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            AppUtils.getInstance().showToast(this, getString(R.string.s_click_back_again_msg));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, Constants.AppConstant.TIME_OUT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RCLocation.REQUEST_CHECK_SETTINGS && resultCode == RESULT_CANCELED) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.LOCATION).putExtra(Constants.IntentConstant.LOCATION, false));
        } else if (requestCode == Constants.IntentConstant.REQUEST_FILTER && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            filterBean = (FilterBean) data.getExtras().getSerializable(Constants.IntentConstant.FILTER_DATA);
            if (filterBean != null && filterBean.getCount() > 0) {
                tvFilterCount.setVisibility(View.VISIBLE);
                tvFilterCount.setText(String.valueOf(filterBean.getCount()));
            } else {
                tvFilterCount.setVisibility(View.GONE);
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.FILTER_DATA));
        } else if (requestCode == Constants.IntentConstant.REQUEST_ORDER_FILTER && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            orderBean = (OrderBean) data.getExtras().getSerializable(Constants.IntentConstant.ORDER_DETAILS);
            if (orderBean != null && orderBean.getCount() > 0) {
                tvFilterCount.setVisibility(View.VISIBLE);
                tvFilterCount.setText(String.valueOf(orderBean.getCount()));
            } else {
                tvFilterCount.setVisibility(View.GONE);
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.ORDER_DETAILS));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * method to start contact sync
     */
    private void startSyncing() {
        try {
            startService(new Intent(this, ContactMonitorService.class));
            if (ContactsDatabase.getInstance(HomeActivity.this).checkContactsCountInDB() == 0) {
                ContactUpdate.getInstance().startContactSyncing(HomeActivity.this, new Intent(HomeActivity.this, CopyContactService.class).putExtra(ContactUpdate.CONTACT_SYNC_TYPE, ContactUpdate.SYNC_ALL_CONTACTS));
            } else if (!AppSharedPreference.getInstance().getBoolean(HomeActivity.this, AppSharedPreference.PREF_KEY.IS_CONTACTS_SYNCHED)) {
                new LoadContactAsyncTask(HomeActivity.this).execute();
                ContactUpdate.getInstance().startContactSyncing(HomeActivity.this, new Intent(HomeActivity.this, CopyContactService.class).putExtra(ContactUpdate.CONTACT_SYNC_TYPE, ContactUpdate.SYNC_NON_SYNC_CONTACTS));
            } else {
                new LoadContactAsyncTask(HomeActivity.this).execute();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * method to get the product filter data
     *
     * @return
     */
    public FilterBean getFilterData() {
        return filterBean == null ? new FilterBean() : filterBean;
    }

    /**
     * method to set the product filter data
     *
     * @return
     */
    public void setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;
        if (filterBean != null && filterBean.getCount() > 0) {
            tvFilterCount.setVisibility(View.VISIBLE);
            tvFilterCount.setText(String.valueOf(filterBean.getCount()));
        } else {
            tvFilterCount.setVisibility(View.GONE);
        }
    }

    /**
     * method to get the my order filter data
     *
     * @return
     */
    public OrderBean getMyOrderFilterData() {
        return orderBean;
    }

    @Override
    public void databaseUpdated(boolean isDbUpdated) {
        Log.e("result", isDbUpdated + "");

    }

    @Override
    public void showLocalContacts(ArrayList<ContactResult> allContacts) {
        updateAdapter(allContacts);
    }

    @Override
    public void allContacts(final ArrayList<ContactResult> allContacts) {
        Log.e("result", allContacts.size() + "");
        if (allContacts.size() > 0)
            syncing.syncContactsOnServer(allContacts);
    }

    @Override
    public void editedContacts(ArrayList<ContactResult> updatedContacts) {
        updateMessage(updatedContacts.size() + " contact updated");
        new LoadContactAsyncTask(HomeActivity.this).execute();
        ArrayList<ContactResult> nonSynchedContacts = ContactsDatabase.getInstance(HomeActivity.this).fetchNonSynchedContacts();
        if (nonSynchedContacts.size() > 0)
            syncing.syncContactsOnServer(nonSynchedContacts);
    }

    @Override
    public void deletedContacts(ArrayList<ContactResult> deletedContacts) {
        updateMessage(deletedContacts.size() + " contact deleted");
        new LoadContactAsyncTask(HomeActivity.this).execute();
        syncing.deleteContactsFromServer(deletedContacts);
    }

    @Override
    public void newlyAddedContacts(ArrayList<ContactResult> newlyAddedContacts) {
        updateMessage(newlyAddedContacts.size() + " contact added");
        new LoadContactAsyncTask(HomeActivity.this).execute();
        ArrayList<ContactResult> nonSynchedContacts = ContactsDatabase.getInstance(HomeActivity.this).fetchNonSynchedContacts();
        if (nonSynchedContacts.size() > 0)
            syncing.syncContactsOnServer(nonSynchedContacts);
    }

    @Override
    public void nonSynchedContacts(ArrayList<ContactResult> nonSynchedContacts) {
        Log.e("result", nonSynchedContacts.size() + "");
        if (nonSynchedContacts.size() > 0)
            syncing.syncContactsOnServer(nonSynchedContacts);
    }


    @Override
    public void contactsSyncResponse(JSONObject jsonObject, ArrayList<ContactResult> contactResults) {
        try {
            if (jsonObject.has("code")) {
                if (jsonObject.getInt("code") == 200) {
                    ContactsDatabase.getInstance(this).updateContactsSyncStatus(jsonObject.getInt("lastIndex"));
                    if (jsonObject.has("data")) {
                        List<ContentValues> contentValue = new ArrayList<>();
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            JSONObject jsonObject1 = jsonObject.getJSONArray("data").getJSONObject(i);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(CONTACT_IS_APP_USER, "1");
                            if (!jsonObject1.getString("mobile").equalsIgnoreCase(""))
                                contentValues.put(CONTACT_PHONE_WITH_CODE, jsonObject1.getString("mobile"));
                            contentValue.add(contentValues);
                        }
                        ContactsDatabase.getInstance(this).updateAllAppUsersInDB(contentValue);
                        new LoadContactAsyncTask(HomeActivity.this).execute();
                    }
                    if (jsonObject.has("lastIndex") && jsonObject.getInt("lastIndex") >= ContactsDatabase.getInstance(this).getMaxIndexValueFromDB()) {
                        AppSharedPreference.getInstance().putBoolean(HomeActivity.this, AppSharedPreference.PREF_KEY.IS_CONTACTS_SYNCHED, true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(String message) {
        Toast.makeText(HomeActivity.this, message + "", Toast.LENGTH_SHORT).show();

    }

    /**
     * method to change map icon
     *
     * @param drawable
     */
    public void changeMapIcon(int drawable) {

    }

    /**
     * function to set the coachMarks
     * @param viewGroup
     * @param tvLocation
     * @param ivShowCategory
     */
    public void setCoachMarks(ViewGroup viewGroup, View tvLocation, View ivShowCategory) {
        if (!AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_HOME_SEEN)) {
            CoachMarksImpl.getInstance().setHomeScreenCoachMarks(this, ivMenu, menuThirdRight, menuSecondRight, flMenuRightHome,
                    viewGroup.getChildAt(0), viewGroup.getChildAt(1), viewGroup.getChildAt(2), tvLocation, ivShowCategory);
        }
    }


    @SuppressLint("StaticFieldLeak")
    class LoadContactAsyncTask extends AsyncTask<Void, Void, Void> {
        private List<ContactResult> list = new ArrayList<>();
        private Context context;

        public LoadContactAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            list.addAll(ContactsDatabase.getInstance(HomeActivity.this).fetchAllContacts());
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ((HomeActivity) context).updateAdapter(list);
        }
    }

    /**
     * function to update the adapter
     *
     * @param list
     */
    private void updateAdapter(final List<ContactResult> list) {
        runOnUiThread(() -> {
            //bar.setVisibility(View.GONE);
            if (list.size() > 0) {
                contactsList.clear();
                contactsList.addAll(list);
//                    FriendListAdapter friendListAdapter = new FriendListAdapter(MainActivity.this,contactsList);
//                    listView.setAdapter(friendListAdapter);
            }
        });
    }


    /**
     * function to update the message
     *
     * @param message
     */
    private void updateMessage(final String message) {
        runOnUiThread(() -> Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show());
    }


    /**
     * function to hit unread count api
     */
    private void hitGetUnreadCountApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, "3");
        Call<ResponseBody> call = apiInterface.hitGetUnreadCountApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        UnreadCountResponse unreadCountResponse = new Gson().fromJson(response, UnreadCountResponse.class);
                        unreadHunt = unreadCountResponse.getResult().getUnreadHuntCount();
                        updateUnreadStatus();
                        break;
                }
            }

            @Override
            public void onError(String response, int requestCode) {
//                AppUtils.getInstance().showToast(HomeActivity.this, response);
            }

            @Override
            public void onFailure() {
            }
        }, 1);

        if (!AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE).equals("1")
//                || AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.PHONE_NO).equals("")
        ) {
            ApiInterface apiIntf = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
            final HashMap<String, String> par = new HashMap<>();
            par.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
            Call<ResponseBody> calls = apiIntf.hitProfileDataApi(AppUtils.getInstance().encryptData(par));
            ApiCall.getInstance().hitService(this, calls, new NetworkListener() {
                @Override
                public void onSuccess(int responseCode, String response, int requestCode) {
                    AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                    if (responseCode == Constants.NetworkConstant.SUCCESS_CODE) {
                        ProfileResponse profileResponse = new Gson().fromJson(response, ProfileResponse.class);
                        AppSharedPreference.getInstance().putString(HomeActivity.this, AppSharedPreference.PREF_KEY.IS_EMAIL_VALIDATE, profileResponse.getResult().getIsEmailVerified());
                        AppSharedPreference.getInstance().putString(HomeActivity.this, AppSharedPreference.PREF_KEY.PHONE_NO, profileResponse.getResult().getMobileNo());
                        setToolbar();
                    }
                }

                @Override
                public void onError(String response, int requestCode) {
                }

                @Override
                public void onFailure() {
                }
            }, Constants.NetworkConstant.REQUEST_PROFILE);
        }
    }

    /**
     * function to update unread status
     */
    private void updateUnreadStatus() {
        if (unreadHunt + unreadChat > 0) {
            //show menu dot
            ivUnread.setVisibility(View.VISIBLE);
        } else {
            //hide menu dot
            ivUnread.setVisibility(View.GONE);
        }
        sideMenuAdapter.notifyDataSetChanged();
    }

    /**
     * function to update fragment
     * @param position
     */
    public void updateFragment(int position) {

        if (position == 0) {
            resetSideMenu(0);
            setFragmentOnContainer(0);
        }else if (position == -1) {
            openProfileFragment();
        }
    }
}
