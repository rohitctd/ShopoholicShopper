package com.shopoholic.activities;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cameraandgallery.activities.CameraGalleryActivity;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForImagePicker;
import com.shopoholic.dialogs.CustomDialogForSelectUserStatus;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.interfaces.DialogCallback;
import com.shopoholic.interfaces.UserStatusDialogCallback;
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

import appinventiv.com.imagecropper.cicularcropper.CropImageView;
import appinventiv.com.imagecropper.cicularcropper.ImageCropper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EditProfileActivity extends BaseActivity implements AmazonCallback, NetworkListener {
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.civ_profile_pic)
    CircleImageView civProfilePic;
    @BindView(R.id.view_image_loader)
    View viewImageLoader;
    @BindView(R.id.view_image_dot)
    View viewImageDot;
    @BindView(R.id.iv_edit_image)
    ImageView ivEditImage;
    @BindView(R.id.et_first_name)
    CustomEditText etFirstName;
    @BindView(R.id.et_last_name)
    CustomEditText etLastName;
    @BindView(R.id.et_address_line_1)
    CustomEditText etAddressLine1;
    @BindView(R.id.et_address_line_2)
    CustomEditText etAddressLine2;
    @BindView(R.id.et_phone_number)
    CustomEditText etPhoneNumber;
    @BindView(R.id.tv_change_phone_no)
    TextView tvChangePhoneNo;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.et_email)
    CustomEditText etEmail;
    @BindView(R.id.view_first_name)
    View viewFirstName;
    @BindView(R.id.view_last_name)
    View viewLastName;
    @BindView(R.id.view_email)
    View viewEmail;
    @BindView(R.id.view_address_line_1)
    View viewAddressLine1;
    @BindView(R.id.view_address_line_2)
    View viewAddressLine2;
    @BindView(R.id.view_phone_no)
    View viewPhoneNo;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.et_gender)
    CustomEditText etGender;
    @BindView(R.id.view_gender)
    View viewGender;
    @BindView(R.id.et_dob)
    CustomEditText etDob;
    @BindView(R.id.view_dob)
    View viewDob;
    @BindView(R.id.et_anniversary)
    CustomEditText etAnniversary;
    @BindView(R.id.view_anniversary)
    View viewAnniversary;
    private boolean isUploading;
    private Uri outputUri;
    private String imageUrl = "";
    private ImageBean imageBean;
    private AmazonS3 mAmazonS3;
    private boolean isLoading;
    private boolean openPlacePicker;
    private double latitude, longitude;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            count = getIntent().getExtras().getInt(Constants.IntentConstant.COUNT);
        }
        setListener();
        initializeAmazonS3();
        setDataOnViews();
    }


    /**
     * method to set the listener on views
     */
    private void setListener() {
        tvTitle.setText(getString(R.string.edit_profile));
        ivBack.setVisibility(View.VISIBLE);
        etAddressLine1.setFocusable(false);
        etAddressLine1.setClickable(true);
        etAddressLine1.setCursorVisible(false);
        etGender.setFocusable(false);
        etGender.setClickable(true);
        etGender.setCursorVisible(false);
        etDob.setFocusable(false);
        etDob.setClickable(true);
        etDob.setCursorVisible(false);
        etAnniversary.setFocusable(false);
        etAnniversary.setClickable(true);
        etAnniversary.setCursorVisible(false);
        etFirstName.addTextChangedListener(this);
        etLastName.addTextChangedListener(this);
        etEmail.addTextChangedListener(this);
        etPhoneNumber.addTextChangedListener(this);
        etAddressLine1.addTextChangedListener(this);
        etAddressLine2.addTextChangedListener(this);
        etGender.addTextChangedListener(this);
        etDob.addTextChangedListener(this);
        etAnniversary.addTextChangedListener(this);
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
     * method to set data on views from share preference
     */
    private void setDataOnViews() {
        btnAction.setText(getString(R.string.save));
        etFirstName.setText(AppSharedPreference.getInstance().getString(EditProfileActivity.this, AppSharedPreference.PREF_KEY.FIRST_NAME));
        etLastName.setText(AppSharedPreference.getInstance().getString(EditProfileActivity.this, AppSharedPreference.PREF_KEY.LAST_NAME));
        etPhoneNumber.setText(TextUtils.concat(AppSharedPreference.getInstance().getString(EditProfileActivity.this, AppSharedPreference.PREF_KEY.COUNTRY_CODE) + " "
                + AppSharedPreference.getInstance().getString(EditProfileActivity.this, AppSharedPreference.PREF_KEY.PHONE_NO)));

        imageUrl = AppSharedPreference.getInstance().getString(EditProfileActivity.this, AppSharedPreference.PREF_KEY.USER_IMAGE);
        String email = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.EMAIL);
        etEmail.setText(email);
        etEmail.setEnabled(email.trim().equals(""));
        etEmail.setFocusable(email.trim().equals(""));
        etEmail.setFocusableInTouchMode(email.trim().equals(""));
        etEmail.setTextColor(ContextCompat.getColor(this, email.trim().equals("") ? android.R.color.white : R.color.colorTextUnselected));

//        AppUtils.getInstance().setCircularImages(EditProfileActivity.this, imageUrl, civProfilePic, R.drawable.ic_side_menu_user_placeholder);
        AppUtils.getInstance().setImages(this, imageUrl, civProfilePic, 5, R.drawable.ic_side_menu_user_placeholder);
        String gender = AppSharedPreference.getInstance().getString(EditProfileActivity.this, AppSharedPreference.PREF_KEY.GENDER);
        etGender.setText(getString(gender.equals("1") ? R.string.male : gender.equals("2") ? R.string.female : gender.equals("3") ? R.string.other : R.string.empty));
        String dob = AppSharedPreference.getInstance().getString(EditProfileActivity.this, AppSharedPreference.PREF_KEY.DOB);
        etDob.setText(dob.equals("") || dob.equals("0000-00-00") ? "" : AppUtils.getInstance().formatDate(dob, Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT));
        String anniversary = AppSharedPreference.getInstance().getString(EditProfileActivity.this, AppSharedPreference.PREF_KEY.ANNVERSARY);
        etAnniversary.setText(anniversary.equals("") || anniversary.equals("0000-00-00") ? "" : AppUtils.getInstance().formatDate(anniversary, Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT));

//        etAddressLine1.setEnabled(count == 0);
//        etAddressLine1.setFocusableInTouchMode(count == 0);
//        etAddressLine1.setTextColor(ContextCompat.getColor(this, count == 0 ? android.R.color.white : R.color.colorTextUnselected));

        etAddressLine2.setEnabled(count == 0);
        etAddressLine2.setFocusableInTouchMode(count == 0);
//        etAddressLine2.setTextColor(ContextCompat.getColor(this, count == 0 ? android.R.color.white : R.color.colorTextUnselected));

        try {
            latitude = Double.parseDouble(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LATITUDE));
            longitude = Double.parseDouble(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LONGITUDE));
            etAddressLine1.setText(AppSharedPreference.getInstance().getString(EditProfileActivity.this, AppSharedPreference.PREF_KEY.ADDRESS));
            etAddressLine2.setText(AppSharedPreference.getInstance().getString(EditProfileActivity.this, AppSharedPreference.PREF_KEY.ADDRESS2));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        AppUtils.getInstance().changeSeparatorViewColor(etFirstName, viewFirstName);
        AppUtils.getInstance().changeSeparatorViewColor(etLastName, viewLastName);
        AppUtils.getInstance().changeSeparatorViewColor(etEmail, viewEmail);
        AppUtils.getInstance().changeSeparatorViewColor(etPhoneNumber, viewPhoneNo);
        AppUtils.getInstance().changeSeparatorViewColor(etAddressLine1, viewAddressLine1);
        AppUtils.getInstance().changeSeparatorViewColor(etAddressLine2, viewAddressLine2);
        AppUtils.getInstance().changeSeparatorViewColor(etGender, viewGender);
        AppUtils.getInstance().changeSeparatorViewColor(etDob, viewDob);
        AppUtils.getInstance().changeSeparatorViewColor(etAnniversary, viewAnniversary);

//        AppUtils.getInstance().filterName(etFirstName);
//        AppUtils.getInstance().filterName(etLastName);
    }

    /**
     * Method to check the validation on fields
     *
     * @return true if every entry is right, else false
     */
    private boolean isValidate() {
        if (etFirstName.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_first_name));
            return false;
        } else if (etFirstName.getText().toString().trim().length() < 3) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_3_char_first_name));
            return false;
        } else if (!etFirstName.getText().toString().matches("^[a-zA-Z]+[a-zA-Z0-9 ]*")) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_first_name));
            return false;
        } else if (etLastName.getText().toString().trim().length() != 0 && !etLastName.getText().toString().matches("^[a-zA-Z]+[a-zA-Z0-9 ]*")) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_last_name));
            return false;
        } else if (etAddressLine1.getText().toString().trim().length() != 0 || etAddressLine2.getText().toString().trim().length() != 0) {
            if (latitude == 0.0 && longitude == 0.0) {
                AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_address));
                return false;
            }
        } else if (isUploading) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_wait_image_loading));
            return false;
        }
        return true;
    }


    /**
     * Show Image picker dialog at bottom
     */
    private void showDialog() {
        final CustomDialogForImagePicker dialog = new CustomDialogForImagePicker(this, new DialogCallback() {
            // method called when click image from camera
            @Override
            public void onSubmit() {
                File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/Shopoholic");
                if (!myDir.exists())
                    myDir.mkdir();
                String fname = "IMG_" + System.currentTimeMillis() + ".jpg";
                File file = new File(myDir, fname);
                outputUri = Uri.fromFile(file);
                ImageCropper.CROPPER_REQUEST_CODE = Constants.IntentConstant.REQUEST_CROPPER_CODE;
                startActivityForResult(new Intent(EditProfileActivity.this, CameraGalleryActivity.class)
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
                ImageCropper.startPickImageFromGalleryActivity(EditProfileActivity.this, Constants.IntentConstant.REQUEST_GALLERY, Constants.IntentConstant.REQUEST_CROPPER_CODE);
            }

            @Override
            public void onRemove() {
                outputUri = null;
                imageUrl = "";
                civProfilePic.setImageResource(R.drawable.ic_side_menu_user_placeholder);
            }
        });
        dialog.show();
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
            //before marshmallow
            startCamera();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case Constants.IntentConstant.REQUEST_GALLERY:
                boolean isRationalGalleryStorage = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                        ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        openPlacePicker = false;
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
                AppUtils.getInstance().setImages(this, outputUri.getPath(), civProfilePic, 5, R.drawable.ic_side_menu_user_placeholder);
                imageBean = null;
                startUpload(AppUtils.getInstance().getPathFromUri(this, outputUri));
            } else if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER) {
//                Place place = PlaceAutocomplete.getPlace(this, data);
                Place place = PlacePicker.getPlace(this, data);
                if (place != null && place.getAddress() != null) {
                    String address = "";
                    if (place.getName()!= null && !place.getName().equals("") && !place.getName().toString().contains("\"")) {
                        address += place.getName() + ", ";
                    }
                    address += place.getAddress().toString();
                    etAddressLine1.setText(address);
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                }
            }
        }
    }

    /**
     * method to start the camera
     */
    private void startCamera() {
        if (!imageUrl.equals("")) {
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


    @OnClick({R.id.iv_back, R.id.iv_edit_image, R.id.tv_change_phone_no, R.id.btn_action, R.id.et_address_line_1, R.id.et_address_line_2, R.id.et_gender, R.id.et_dob, R.id.et_anniversary})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_edit_image:
                if (!isUploading)
                    checkWritePermission();
                break;
            case R.id.tv_change_phone_no:
                startActivity(new Intent(this, ChangePhoneNumberActivity.class));
                break;
            case R.id.et_gender:
                CustomDialogForSelectUserStatus dialogForSelectUserStatus = new CustomDialogForSelectUserStatus(this, 2, new UserStatusDialogCallback() {
                    @Override
                    public void onSelect(String status, int type) {
                        etGender.setText(type == 1 ? "" : status);
                    }
                });
                dialogForSelectUserStatus.show();
                break;
            case R.id.et_dob:
                AppUtils.getInstance().openDatePickerAndSetDate(this, etDob, true, etAnniversary, etDob.getText().toString().trim());
                break;
            case R.id.et_anniversary:
                AppUtils.getInstance().openDatePickerAndSetDate(this, etAnniversary, false, etDob, etAnniversary.getText().toString().trim());
                break;
            case R.id.et_address_line_1:
                AppUtils.getInstance().hideKeyboard(this);
                if (count == 0) {
                    if (!openPlacePicker) {
                        try {
//                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
//                        startActivityForResult(intent, Constants.IntentConstant.REQUEST_PLACE_PICKER);
                            openPlacePicker = true;
                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                            startActivityForResult(builder.build(this), Constants.IntentConstant.REQUEST_PLACE_PICKER);
                        } catch (Exception e) {
                            openPlacePicker = false;
                            Log.e("MSG: ", e.getStackTrace().toString());
                        }
                    }
                } else {
                    new AlertDialog.Builder(this, R.style.DatePickerTheme)
                            .setMessage(getString(R.string.address_not_editable))
                            .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                            }).show();

                }
                break;
            case R.id.et_address_line_2:
                if (count != 0) {
                    AppUtils.getInstance().hideKeyboard(this);
                    new AlertDialog.Builder(this, R.style.DatePickerTheme)
                            .setMessage(getString(R.string.address_not_editable))
                            .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                            }).show();

                }
                break;
            case R.id.btn_action:
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    if (isValidate() && !isLoading) {
                        hitEditProfileDataApi();
                    }
                }
                break;
        }
    }

    /**
     * Method to hit the signup api
     */
    private void hitEditProfileDataApi() {
        AppUtils.getInstance().setButtonLoaderAnimation(EditProfileActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        isLoading = true;
        String language = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE_CODE);
        int onlineStatus = AppSharedPreference.getInstance().getInt(this, AppSharedPreference.PREF_KEY.USER_ONLINE_STATUS);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_FIRST_NAME, etFirstName.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_LAST_NAME, etLastName.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_IMAGE, imageUrl);
        params.put(Constants.NetworkConstant.PARAM_ADDRESS, etAddressLine1.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_ADDRESS2, etAddressLine2.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_GENDER, etGender.getText().toString().trim().equalsIgnoreCase(getString(R.string.male)) ? "1" :
                etGender.getText().toString().trim().equalsIgnoreCase(getString(R.string.female)) ? "2" :
                        etGender.getText().toString().trim().equalsIgnoreCase(getString(R.string.other)) ? "3" : "");
        params.put(Constants.NetworkConstant.PARAM_DOB, etDob.getText().toString().trim().equals("") ? "" :
                AppUtils.getInstance().formatDate(etDob.getText().toString().trim(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_ANNIVERSARY, etAnniversary.getText().toString().trim().equals("") ? "" :
                AppUtils.getInstance().formatDate(etAnniversary.getText().toString().trim(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
        params.put(Constants.NetworkConstant.PARAM_LATITUDE, String.valueOf(latitude));
        params.put(Constants.NetworkConstant.PARAM_LONGITUDE, String.valueOf(longitude));
        params.put(Constants.NetworkConstant.PARAM_NOTIFICATION_STATUS, AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_NOTIFICATION_ON) ? "1" : "2");
        params.put(Constants.NetworkConstant.PARAM_LOCATION_STATUS, AppSharedPreference.getInstance().getBoolean(this, AppSharedPreference.PREF_KEY.IS_LOCATION_ON) ? "1" : "2");
        params.put(Constants.NetworkConstant.PARAM_USER_LANGUAGE, language.equals("") ? "1" : language);
        params.put(Constants.NetworkConstant.PARAM_IS_AVAILABLE, String.valueOf(onlineStatus == 0 ? 1 : onlineStatus));
        Call<ResponseBody> call = apiInterface.hitEditProfileDataApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_SIGN_UP);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(EditProfileActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        isLoading = false;
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_SIGN_UP:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.USER_IMAGE, imageUrl);
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.FIRST_NAME, etFirstName.getText().toString().trim());
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.LAST_NAME, etLastName.getText().toString().trim());
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.ADDRESS2, etAddressLine2.getText().toString().trim());
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.ADDRESS, etAddressLine1.getText().toString().trim());
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.LATITUDE, String.valueOf(latitude));
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.LONGITUDE, String.valueOf(longitude));
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.GENDER, etGender.getText().toString().trim()
                                .equalsIgnoreCase(getString(R.string.male)) ? "1" : etGender.getText().toString().trim().equalsIgnoreCase(getString(R.string.female)) ? "2" :
                                etGender.getText().toString().trim().equalsIgnoreCase(getString(R.string.other)) ? "3" : "");
                        if (!etDob.getText().toString().trim().equals("")) AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.DOB,
                                AppUtils.getInstance().formatDate(etDob.getText().toString().trim(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
                        if (!etAnniversary.getText().toString().trim().equals("")) AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.ANNVERSARY,
                                AppUtils.getInstance().formatDate(etAnniversary.getText().toString().trim(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT));
                        FirebaseDatabaseQueries.getInstance().createUser(this);
                        setResult(RESULT_OK);
                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(EditProfileActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(EditProfileActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);

    }

    /**
     * upload file in S3
     *
     * @param path
     */
    private void startUpload(String path) {
        ImageBean bean = addDataInBean(path);
        isUploading = true;
        ivEditImage.setVisibility(View.GONE);
        AppUtils.getInstance().setImageLoaderAnimation(this, civProfilePic, viewImageLoader, viewImageDot, true);
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
        ivEditImage.setVisibility(View.VISIBLE);
        AppUtils.getInstance().setImageLoaderAnimation(this, civProfilePic, viewImageLoader, viewImageDot, false);
        imageUrl = imageBean.getServerUrl();
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        AppUtils.getInstance().showToast(this, getString(R.string.image_upload_fail));
        isUploading = false;
        ivEditImage.setVisibility(View.VISIBLE);
        AppUtils.getInstance().setImageLoaderAnimation(this, civProfilePic, viewImageLoader, viewImageDot, false);
        outputUri = null;
        imageUrl = "";
        civProfilePic.setImageResource(R.drawable.ic_side_menu_user_placeholder);
    }

    @Override
    public void uploadProgress(ImageBean bean) {
        AppUtils.getInstance().printLogMessage(this.getCallingPackage(), "Uploaded " + bean.getProgress() + " %");

    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {
        AppUtils.getInstance().printLogMessage(this.getCallingPackage(), "" + e.getMessage());
        isUploading = false;
        ivEditImage.setVisibility(View.VISIBLE);
        AppUtils.getInstance().setImageLoaderAnimation(this, civProfilePic, viewImageLoader, viewImageDot, false);
    }
}
