package com.shopoholic.firebasechat.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cameraandgallery.activities.CameraGalleryActivity;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForImagePicker;
import com.shopoholic.firebasechat.adapters.MyFriendsListAdapter;
import com.shopoholic.firebasechat.adapters.SelectedFriendsListAdapter;
import com.shopoholic.firebasechat.interfaces.FirebaseUserListener;
import com.shopoholic.firebasechat.models.GroupDetailBean;
import com.shopoholic.firebasechat.models.ProductBean;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.models.myfriendslistresponse.MyFriendsListResponse;
import com.shopoholic.firebasechat.models.myfriendslistresponse.Result;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.interfaces.DialogCallback;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import appinventiv.com.imagecropper.cicularcropper.CropImageView;
import appinventiv.com.imagecropper.cicularcropper.ImageCropper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CreateGroupActivity extends AppCompatActivity implements NetworkListener, AmazonCallback {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.iv_group_image)
    CircleImageView ivGroupImage;
    @BindView(R.id.iv_add_image)
    ImageView ivAddImage;
    @BindView(R.id.fl_create_image)
    FrameLayout flCreateImage;
    @BindView(R.id.et_group_name)
    CustomEditText etGroupName;
    @BindView(R.id.tv_add_participants)
    CustomTextView tvAddParticipants;
    @BindView(R.id.et_search_participants)
    CustomEditText etSearchParticipants;
    @BindView(R.id.rv_selected_friends)
    RecyclerView rvSelectedFriends;
    @BindView(R.id.recycle_view)
    RecyclerView rvFriends;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private MyFriendsListAdapter friendsAdapter;
    private List<UserBean> selectedFriendsList;
    private List<UserBean> users;
    private List<Result> friendsList;
    private String groupImage = "";
    private boolean isLoading;
    private boolean isMoreData;
    private int count = 0;
    private List<String> list;
    private SelectedFriendsListAdapter selectedFriendsListAdapter;
    private String groupName = "";
    private boolean isUploading;
    private Uri outputUri;
    private ImageBean imageBean;
    private AmazonS3 mAmazonS3;
    private boolean createGroup= true;
    private String groupRoomId;
    private com.shopoholic.models.productservicedetailsresponse.Result productDetails;
//    private ViewPagerAdapter pagerAdapter;
//    private FriendsAndBuddyFragment friendsFragment;
//    private FriendsAndBuddyFragment buddyFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        setSupportActionBar(layoutToolbar);
        initVariables();
        setListener();
        setAdapters();
        initializeAmazonS3();
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            hitMyFriendsListApi();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.right_menu_text).setVisible(true);
        menu.findItem(R.id.right_menu_text).setTitle(getString(R.string.done));
        menu.findItem(R.id.right_menu_text).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.right_menu_text) {
            if (isValidate()) {
                createGroup();
            }
        }
        return true;
    }


    /**
     * initialize amazon service
     */
    private void initializeAmazonS3() {
        mAmazonS3 = new AmazonS3();
        mAmazonS3.setVariables(Constants.UrlConstant.AMAZON_POOLID, Constants.UrlConstant.BUCKET, Constants.UrlConstant.AMAZON_SERVER_URL, Constants.UrlConstant.END_POINT, Constants.UrlConstant.REGION);
        mAmazonS3.setActivity(this);
        mAmazonS3.setCallback(this);
    }

    /**
     * Method to check the validation on fields
     *
     * @return true if every entry is right, else false
     */
    private boolean isValidate() {
        if (etGroupName.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_group_name));
            return false;
        } else if (selectedFriendsList.size() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_select_at_least_one_friend));
            return false;
        } else if (isUploading) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_wait_image_loading));
            return false;
        }
        return true;
    }

    @OnClick({R.id.iv_back, R.id.iv_add_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_add_image:
                if (!isUploading)
                    checkWritePermission();
                break;
        }
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {

        setSupportActionBar(layoutToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            tvTitle.setText(getString(R.string.create_group));
        }


        list = new ArrayList<>();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        selectedFriendsList = new ArrayList<>();
        friendsList = new ArrayList<>();
        if (getIntent() != null && getIntent().getExtras() != null) {
            users = (List<UserBean>) getIntent().getExtras().getSerializable(FirebaseConstants.OTHER_USER);
            groupName = getIntent().getExtras().getString(FirebaseConstants.CHAT_ROOM_TITLE, "");
            groupImage = getIntent().getExtras().getString(FirebaseConstants.CHAT_ROOM_PIC, "");
            groupRoomId = getIntent().getExtras().getString(FirebaseConstants.CHAT_ROOM_ID, "");
            createGroup = getIntent().getExtras().getBoolean(FirebaseConstants.CREATE_GROUP, true);
            productDetails = (com.shopoholic.models.productservicedetailsresponse.Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.PRODUCT_DETAILS);
        }
        if (!groupName.equals("")) {
            etGroupName.setEnabled(false);
            etGroupName.setText(groupName);
        }
        if (!groupImage.equals("")) {
            ivAddImage.setVisibility(View.GONE);
            AppUtils.getInstance().setImages(this, groupImage, ivGroupImage, 0, R.drawable.ic_friend_placeholder);
        }

        friendsAdapter = new MyFriendsListAdapter(this, friendsList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                friendsList.get(position).setSelected(!friendsList.get(position).isSelected());
                friendsAdapter.notifyItemChanged(position);
                if (friendsList.get(position).isSelected()) {
                    getUsers(friendsList.get(position).getUserId());
                }
                for (int i = 0; i < selectedFriendsList.size(); i++) {
                    if (selectedFriendsList.get(i).getUserImage().equals(friendsList.get(position).getImage())) {
                        selectedFriendsList.remove(selectedFriendsList.get(i));
                        selectedFriendsListAdapter.notifyDataSetChanged();
                        break;
                    }
                }

            }
        });
        selectedFriendsListAdapter = new SelectedFriendsListAdapter(this, selectedFriendsList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                for (int i = 0; i < friendsList.size(); i++) {
                    if (friendsList.get(i).getImage().equals(selectedFriendsList.get(position).getUserImage())) {
                        selectedFriendsList.remove(position);
                        selectedFriendsListAdapter.notifyDataSetChanged();
                        friendsList.get(position).setSelected(false);
                        friendsAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
//        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        Bundle friendsBundle = new Bundle();
//        Bundle buddyBundle = new Bundle();
//
//        friendsBundle.putString(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.FRIENDS);
//        buddyBundle.putString(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.BUDDY);

//        friendsFragment = new FriendsAndBuddyFragment();
//        buddyFragment = new FriendsAndBuddyFragment();

//        friendsFragment.setArguments(friendsBundle);
//        buddyFragment.setArguments(buddyBundle);

    }

    /**
     * method to set listener in views
     */
    private void setListener() {

    }

    /**
     * method to set adapter in views
     */
    private void setAdapters() {
        rvFriends.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvFriends.setAdapter(friendsAdapter);
        rvSelectedFriends.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSelectedFriends.setAdapter(selectedFriendsListAdapter);
//        pagerAdapter.addFragment(friendsFragment, getString(R.string.txt_friends));
//        pagerAdapter.addFragment(buddyFragment, getString(R.string.txt_buddies));
//        viewPager.setAdapter(pagerAdapter);
//        viewPager.setOffscreenPageLimit(2);//ths is done to make sure that fragment does not load again
//        tabLayout.setupWithViewPager(viewPager);

    }

    /**
     * method to get users
     */
    private void getUsers(String id) {
//        String[] list = {"5", "7", "17", "22", "23", "34", "37"};
//        for (String id : list) {
        FirebaseDatabaseQueries.getInstance().getUser(id, new FirebaseUserListener() {
            @Override
            public void getUser(UserBean user) {
                selectedFriendsList.add(user);
                selectedFriendsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void updateUser(UserBean user) {
            }
        });
//        }
    }

    /**
     * method to create group chat
     */
    private void createGroup() {
        if (createGroup) {
            for (UserBean user : users) {
                if (!selectedFriendsList.contains(user) && !user.getUserId().equals(
                        AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID))) {
                    selectedFriendsList.add(0, user);
                }
            }
            GroupDetailBean groupDetailBean = new GroupDetailBean();
            String roomId = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).push().getKey();
            groupDetailBean.setGroupRoomId(roomId);
            groupDetailBean.setGroupName(etGroupName.getText().toString().trim());
            groupDetailBean.setGroupImage(groupImage);
            groupDetailBean.setGroupMembers(selectedFriendsList);
            FirebaseDatabaseQueries.getInstance().createGroup(this, groupDetailBean, productDetails);

            Intent intent = new Intent();
            intent.putExtra(FirebaseConstants.ROOM_ID, roomId);
            setResult(RESULT_OK, intent);
        }
        else {
            FirebaseDatabaseQueries.getInstance().addNewMembers(this, groupRoomId, selectedFriendsList);
        }
        finish();
    }


    /**
     * method to get friends list
     */
    private void hitMyFriendsListApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.DEVICE_ID, AppUtils.getInstance().getDeviceId(this));
        params.put(Constants.NetworkConstant.TYPE, "");
        Call<ResponseBody> call = apiInterface.hitMyFriendsListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_FRIENDS);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_FRIENDS:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        MyFriendsListResponse friendsListResponse = new Gson().fromJson(response, MyFriendsListResponse.class);
                        friendsList.addAll(friendsListResponse.getResult());
                        if (users != null && users.size() > 0) {
                            for (UserBean user : users) {
                                for (int i = 0; i < friendsList.size(); i++) {
                                    if (friendsList.get(i).getUserId().equals(user.getUserId())) {
                                        friendsList.remove(i);
                                        break;
                                    }
                                }
                            }
                        }
                        friendsAdapter.notifyDataSetChanged();
                        isMoreData = friendsListResponse.getNext() != -1;
                        if (isMoreData) count = friendsListResponse.getNext();
                        if (friendsList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;

                    case Constants.NetworkConstant.NO_DATA:
                        friendsList.clear();
                        friendsAdapter.notifyDataSetChanged();
                        if (friendsList.size() == 0) {
                            layoutNoDataFound.setVisibility(View.VISIBLE);
                        } else {
                            layoutNoDataFound.setVisibility(View.GONE);
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        friendsAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (friendsList.size() == 0) {
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
        friendsAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (friendsList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }


    /**
     * Checks permission to Write external storage in Marshmallow and above devices
     */
    private void checkWritePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here
            if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, Constants.IntentConstant.REQUEST_GALLERY);
            } else {
                // permission already granted
                startCamera();
            }
        } else {
            //befor marshmallow
            startCamera();
        }
    }

    /**
     * Show Image picker dialog at bottom
     */
    private void showDialog() {
        final CustomDialogForImagePicker dialog = new CustomDialogForImagePicker(this, new DialogCallback() {
            // method called when click image from camera
            @Override
            public void onSubmit() {
//                File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/Shopoholic");
//                if (!myDir.exists())
//                    myDir.mkdir();
//                String fname = "IMG_" + System.currentTimeMillis() + ".jpg";
//                File file = new File(myDir, fname);
//                outputUri = Uri.fromFile(file);
//                ImageCropper.startCaptureImageActivity(CreateGroupActivity.this, Constants.IntentConstant.REQUEST_CAMERA, Constants.IntentConstant.REQUEST_CROPPER_CODE);

                File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/Shopoholic");
                if (!myDir.exists())
                    myDir.mkdir();
                String fname = "IMG_" + System.currentTimeMillis() + ".jpg";
                File file = new File(myDir, fname);
                outputUri = Uri.fromFile(file);
                ImageCropper.CROPPER_REQUEST_CODE = Constants.IntentConstant.REQUEST_CROPPER_CODE;
                startActivityForResult(new Intent(CreateGroupActivity.this, CameraGalleryActivity.class)
                                .putExtra("maxSelection", 1)
                        , Constants.IntentConstant.REQUEST_GALLERY);
            }

            // method called when pick image from gallery
            @Override
            public void onCancel() {
                File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/Shopoholic");
                if (!myDir.exists())
                    myDir.mkdir();
                String fname = "IMG_" + System.currentTimeMillis() + ".jpg";
                File file = new File(myDir, fname);
                outputUri = Uri.fromFile(file);
                ImageCropper.startPickImageFromGalleryActivity(CreateGroupActivity.this, Constants.IntentConstant.REQUEST_GALLERY, Constants.IntentConstant.REQUEST_CROPPER_CODE);
            }

            @Override
            public void onRemove() {
                outputUri = null;
                groupImage = "";
                ivGroupImage.setImageResource(R.drawable.ic_side_menu_user_placeholder);
            }
        });
        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case Constants.IntentConstant.REQUEST_GALLERY:

                boolean isRationalGalleryStorage = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                        ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;

                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_storage_permission));
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.IntentConstant.REQUEST_GALLERY) {
                if (data != null && data.getExtras() != null) {
                    ArrayList<String> images = data.getExtras().getStringArrayList("result");
                    if (images != null && images.size() > 0) {
                        ImageCropper.activity(Uri.fromFile(new File(images.get(0)))).setGuidelines(CropImageView.Guidelines.OFF).
                                setCropShape(CropImageView.CropShape.OVAL).
                                setBorderLineColor(Color.WHITE).
                                setBorderCornerColor(Color.TRANSPARENT).
                                setAspectRatio(80, 80).setBorderLineThickness(5).
                                setOutputUri(outputUri).setActionbarColor(ContextCompat.getColor(this, R.color.colorPrimary)).
                                setAutoZoomEnabled(true).start(this);
                    }
                }
            } else if (requestCode == Constants.IntentConstant.REQUEST_CAMERA) {
                ImageCropper.activity(ImageCropper.getCapturedImageURI()).setGuidelines(CropImageView.Guidelines.OFF).
                        setCropShape(CropImageView.CropShape.OVAL).
                        setBorderLineColor(Color.WHITE).
                        setBorderCornerColor(Color.TRANSPARENT).
                        setAspectRatio(80, 80).setBorderLineThickness(5).
                        setOutputUri(outputUri).setActionbarColor(ContextCompat.getColor(this, R.color.colorPrimary)).
                        setAutoZoomEnabled(true).start(this);
            } else if (requestCode == Constants.IntentConstant.REQUEST_CROPPER_CODE) {
                AppUtils.getInstance().setImages(this, outputUri.getPath(), ivGroupImage, 5, R.drawable.ic_friend_placeholder);
                imageBean = null;
                startUpload(AppUtils.getInstance().getPathFromUri(this, outputUri));
            }

        }
    }


    /**
     * method to start the camera
     */
    private void startCamera() {
        if (!groupImage.equals("")) {
            showDialog();
        } else {
            File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/Shopoholic");
            if (!myDir.exists())
                myDir.mkdir();
            String fname = "IMG_" + System.currentTimeMillis() + ".jpg";
            File file = new File(myDir, fname);
            outputUri = Uri.fromFile(file);
            ImageCropper.CROPPER_REQUEST_CODE = Constants.IntentConstant.REQUEST_CROPPER_CODE;
            startActivityForResult(new Intent(this, CameraGalleryActivity.class)
                            .putExtra("maxSelection", 1)
                    , Constants.IntentConstant.REQUEST_GALLERY);
        }
    }

    /**
     * upload file in S3
     *
     * @param path
     */
    private void startUpload(String path) {
        ImageBean bean = addDataInBean(path);
        isUploading = true;
//        imageLoader.setVisibility(View.VISIBLE);
//        AppUtils.getInstance().setImageLoaderAnimation(this, ivGroupImage, viewLoader, viewDot, true);
        mAmazonS3.uploadImage(bean);
    }

    /**
     * create image bean object
     *
     * @param path
     * @return
     */
    private ImageBean addDataInBean(String path) {
        ImageBean bean = new ImageBean();
        bean.setId("1");
        bean.setName("sample");
        bean.setImagePath(path);
        return bean;
    }


    @Override
    public void uploadSuccess(ImageBean imageBean) {
        this.imageBean = imageBean;
        isUploading = false;
//        imageLoader.setVisibility(View.INVISIBLE);
//        AppUtils.getInstance().setImageLoaderAnimation(this, ivGroupImage, viewLoader, viewDot, false);
        groupImage = imageBean.getServerUrl();
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        AppUtils.getInstance().showToast(this, getString(R.string.image_upload_fail));
        isUploading = false;
//        imageLoader.setVisibility(View.INVISIBLE);
//        AppUtils.getInstance().setImageLoaderAnimation(this, ivGroupImage, viewLoader, viewDot, false);
        outputUri = null;
        groupImage = "";
        ivGroupImage.setImageResource(R.drawable.ic_side_menu_user_placeholder);
    }

    @Override
    public void uploadProgress(ImageBean bean) {
        AppUtils.getInstance().printLogMessage(this.getCallingPackage(), "Uploaded " + bean.getProgress() + " %");

    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {
        AppUtils.getInstance().printLogMessage(this.getCallingPackage(), "" + e.getMessage().toString());
        isUploading = false;
//        imageLoader.setVisibility(View.INVISIBLE);
//        AppUtils.getInstance().setImageLoaderAnimation(this, ivGroupImage, viewLoader, viewDot, false);
    }


}