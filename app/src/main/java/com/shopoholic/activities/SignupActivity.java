package com.shopoholic.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cameraandgallery.activities.CameraGalleryActivity;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shopoholic.R;
import com.shopoholic.adapters.CountryCodeAdapter;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.customviews.CustomTypefaceSpan;
import com.shopoholic.dialogs.CustomDialogForImagePicker;
import com.shopoholic.dialogs.CustomDialogForMessage;
import com.shopoholic.dialogs.CustomDialogForSelectCountry;
import com.shopoholic.interfaces.DialogCallback;
import com.shopoholic.interfaces.MessageDialogCallback;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.interfaces.SelectCountryDialogCallback;
import com.shopoholic.models.UserSocialModel;
import com.shopoholic.models.countrymodel.CountryBean;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

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
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Activity used for login
 */

public class SignupActivity extends BaseActivity implements NetworkListener, View.OnTouchListener, AmazonCallback {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_signup_image)
    CircleImageView ivSignupImage;
    @BindView(R.id.view_loader)
    View viewLoader;
    @BindView(R.id.view_dot)
    View viewDot;
    @BindView(R.id.et_first_name)
    CustomEditText etFirstName;
    @BindView(R.id.view_first_name)
    View viewFirstName;
    @BindView(R.id.et_last_name)
    CustomEditText etLastName;
    @BindView(R.id.view_last_name)
    View viewLastName;
    @BindView(R.id.et_email)
    CustomEditText etEmail;
    @BindView(R.id.view_email)
    View viewEmail;
    @BindView(R.id.tv_select_country)
    CustomTextView tvSelectCountry;
    @BindView(R.id.view_country)
    View viewCountry;
    @BindView(R.id.tv_country_code)
    CustomTextView tvCountryCode;
    @BindView(R.id.view_country_code)
    View viewCountryCode;
    @BindView(R.id.rl_country_code)
    RelativeLayout rlCountryCode;
    @BindView(R.id.et_phone_no)
    CustomEditText etPhoneNo;
    @BindView(R.id.view_phone_no)
    View viewPhoneNo;
    @BindView(R.id.et_password)
    CustomEditText etPassword;
    @BindView(R.id.view_password)
    View viewPassword;
    @BindView(R.id.et_confirm_password)
    CustomEditText etConfirmPassword;
    @BindView(R.id.view_confirm_password)
    View viewConfirmPassword;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.tv_login)
    CustomTextView tvLogin;
    @BindView(R.id.tv_privacy_policy)
    CustomTextView tvPrivacyPolicy;
    @BindView(R.id.et_search)
    CustomEditText etSearch;
    @BindView(R.id.rv_country_code)
    RecyclerView rvCountryCode;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    private List<CountryBean> allCountriesList;
    private List<CountryBean> selectedCountriesList;
    private CountryCodeAdapter countryAdapter;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private boolean isPasswordShowing;
    private boolean isConfirmPasswordShowing;
    private Uri outputUri;
    private String userId = "";
    private AmazonS3 mAmazonS3;
    private ImageBean imageBean;
    private boolean isUploading;
    private UserSocialModel userSocialModel;
    private String imageUrl = "";
    private Animation animation;
    private boolean isLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        initVariables();
        setListenersAndSetAdapter();
        setSpannableString();
        initializeAmazonS3();
//        imageLoader.setVisibility(View.VISIBLE);
    }

    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        btnAction.setText(getString(R.string.signup));
        allCountriesList = new ArrayList<>();
        selectedCountriesList = new ArrayList<>();
        allCountriesList = AppUtils.getInstance().getAllCountries(this);
        selectedCountriesList.addAll(allCountriesList);
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(0);

        if (getIntent() != null && getIntent().getExtras() != null){
            if (getIntent().getExtras().getBoolean(Constants.IntentConstant.FROM_TUTORIAL, false)){
                ivBack.setVisibility(View.INVISIBLE);
            }
        }

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra(Constants.IntentConstant.USER_SOCIAL_MODEL)) {
            userSocialModel = (UserSocialModel) getIntent().getExtras().getSerializable(Constants.IntentConstant.USER_SOCIAL_MODEL);
            if (userSocialModel != null) {
                etFirstName.setText(userSocialModel.getFirstName());
                etLastName.setText(userSocialModel.getLastName());
                if (userSocialModel.getEmail() != null && !userSocialModel.getEmail().equals("")) {
                    etEmail.setText(userSocialModel.getEmail());
                    etEmail.setEnabled(false);
                }
                imageUrl = userSocialModel.getUserPic() == null ? "" : userSocialModel.getUserPic();
                AppUtils.getInstance().setCircularImages(this, userSocialModel.getUserPic(), ivSignupImage, R.drawable.ic_side_menu_user_placeholder);
                etPassword.setVisibility(View.GONE);
                viewPassword.setVisibility(View.GONE);
                etConfirmPassword.setVisibility(View.GONE);
                viewConfirmPassword.setVisibility(View.GONE);
            }
        }
        etConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (AppUtils.getInstance().isInternetAvailable(SignupActivity.this)) {
                        if (isValidate() && !isLoading) {
                            hitSignupApi();
                        }
                    }
                }
                return false;
            }
        });

    }

    /**
     * method used to set listeners on views and set adapter on views
     */
    private void setListenersAndSetAdapter() {
        etFirstName.addTextChangedListener(this);
        etLastName.addTextChangedListener(this);
        etEmail.addTextChangedListener(this);
        tvSelectCountry.addTextChangedListener(this);
        etPhoneNo.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        etConfirmPassword.addTextChangedListener(this);

        etPassword.performClick();
        etConfirmPassword.performClick();
        etPassword.setOnTouchListener(this);
        etConfirmPassword.setOnTouchListener(this);
        etSearch.addTextChangedListener(this);

        countryAdapter = new CountryCodeAdapter(this, selectedCountriesList, new RecyclerCallBack() {
            @Override
            public void onClick(final int position, View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        bottomSheetBehavior.setPeekHeight(0);
                        tvCountryCode.setText(selectedCountriesList.get(position).getCountryCode());
                        etSearch.setText("");
                    }
                }, 500);
            }
        });
        rvCountryCode.setLayoutManager(new LinearLayoutManager(this));
        rvCountryCode.setAdapter(countryAdapter);

        tvCountryCode.setText(AppUtils.getInstance().getUserCountryCode(this));
        switch (tvCountryCode.getText().toString().trim()) {
            case "+853": tvSelectCountry.setText(getString(R.string.macau).split(" ")[0]); break;
            case "+91": tvSelectCountry.setText(getString(R.string.india).split(" ")[0]); break;
            case "+65": tvSelectCountry.setText(getString(R.string.singapore).split(" ")[0]); break;
            case "+852": tvSelectCountry.setText(getString(R.string.hong_kong).split(" ")[0]); break;
            case "+971": tvSelectCountry.setText(getString(R.string.uae).split(" ")[0]); break;
        }
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
     * method to create a spannable string
     */
    private void setSpannableString() {
        SpannableString spannableString = new SpannableString(getString(R.string.already_have_an_account_login));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (getIntent() != null && getIntent().getExtras() != null) {
                    if (getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "")
                            .equals(Constants.AppConstant.LOGIN)) {
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                        finish();
                    } else {
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.SIGNUP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    }
                }

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        int start, end;
        switch (AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE)) {
            case Constants.AppConstant.CHINES_TRAD:
            case Constants.AppConstant.CHINES_SIMPLE:
                start = 8;
                end = spannableString.length();
                break;
            default:
                start = 24;
                end = spannableString.length();
        }
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorSignupLogin)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomTypefaceSpan("", Typeface.createFromAsset(getAssets(), getString(R.string.orkney_bold))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLogin.setText(spannableString);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
        tvLogin.setHighlightColor(Color.TRANSPARENT);


        String terms = getString(R.string.terms_and_conditions);
        String policy = getString(R.string.privacy_policy);
        String policyMsg = getString(R.string.terms_msg) + " " + terms + " & " + policy;
        SpannableString policyString = new SpannableString(policyMsg);
        ClickableSpan policyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                startActivity(new Intent(SignupActivity.this, WebViewActivity.class).putExtra(Constants.PRIVACY_POLICY, ""));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan termClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                startActivity(new Intent(SignupActivity.this, WebViewActivity.class).putExtra(Constants.TERM_AND_CONDITION, ""));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        policyString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorSignupLogin)), policyMsg.indexOf(policy), policyMsg.indexOf(policy) + policy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        policyString.setSpan(new CustomTypefaceSpan("", Typeface.createFromAsset(getAssets(), getString(R.string.orkney_bold))), policyMsg.indexOf(policy), policyMsg.indexOf(policy) + policy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        policyString.setSpan(policyClickableSpan, policyMsg.indexOf(policy), policyMsg.indexOf(policy) + policy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        policyString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorSignupLogin)), policyMsg.indexOf(terms), policyMsg.indexOf(terms) + terms.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        policyString.setSpan(new CustomTypefaceSpan("", Typeface.createFromAsset(getAssets(), getString(R.string.orkney_bold))), policyMsg.indexOf(terms), policyMsg.indexOf(terms) + terms.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        policyString.setSpan(termClickableSpan, policyMsg.indexOf(terms), policyMsg.indexOf(terms) + terms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPrivacyPolicy.setText(policyString);
        tvPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        tvPrivacyPolicy.setHighlightColor(Color.TRANSPARENT);
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        AppUtils.getInstance().changeSeparatorViewColor(etFirstName, viewFirstName);
        AppUtils.getInstance().changeSeparatorViewColor(etLastName, viewLastName);
        AppUtils.getInstance().changeSeparatorViewColor(etEmail, viewEmail);
        AppUtils.getInstance().changeSeparatorViewColor(etPhoneNo, viewPhoneNo);
        AppUtils.getInstance().changeSeparatorViewColor(etPassword, viewPassword);
        AppUtils.getInstance().changeSeparatorViewColor(etConfirmPassword, viewConfirmPassword);
        if (tvSelectCountry.getText().toString().length() > 0) {
            viewCountry.setBackgroundResource(R.color.colorSeparatorFilled);
            viewCountryCode.setBackgroundResource(R.color.colorSeparatorFilled);
            tvCountryCode.setTextColor(Color.WHITE);
        } else {
            viewCountry.setBackgroundResource(R.color.colorSeparator);
            viewCountryCode.setBackgroundResource(R.color.colorSeparator);
            tvCountryCode.setTextColor(ContextCompat.getColor(this, R.color.colorHintText));
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        CustomEditText viewEditText;
        if (v.getId() == R.id.et_password) {
            viewEditText = etPassword;
        } else {
            viewEditText = etConfirmPassword;
        }
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        Drawable drawable;
        if (viewEditText.getCompoundDrawables()[DRAWABLE_RIGHT] != null) drawable = viewEditText.getCompoundDrawables()[DRAWABLE_RIGHT];
        else drawable = viewEditText.getCompoundDrawables()[DRAWABLE_LEFT];
        if (drawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (v.getRight() - drawable.getBounds().width())) {
                // your action here
                if (v.getId() == R.id.et_password ? !isPasswordShowing : !isConfirmPasswordShowing) {
                    if (v.getId() == R.id.et_password) isPasswordShowing = true;
                    else isConfirmPasswordShowing = true;
                    viewEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_login_password_visible, 0);
                    viewEditText.setTransformationMethod(null);
                    viewEditText.setSelection(viewEditText.getText().length());
                } else {
                    if (v.getId() == R.id.et_password) isPasswordShowing = false;
                    else isConfirmPasswordShowing = false;
                    viewEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_login_password_not_visible, 0);
                    viewEditText.setTransformationMethod(new PasswordTransformationMethod());
                    viewEditText.setSelection(viewEditText.getText().length());
                }
                viewEditText.setSelection(viewEditText.getText().length());
                viewEditText.requestFocus();
                return true;
            }
        }
        return false;
    }


    @OnClick({R.id.iv_back, R.id.rl_country_code, R.id.btn_action, R.id.tv_select_country, R.id.iv_signup_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_signup_image:
                if (!isUploading)
                    checkWritePermission();
                break;
            case R.id.rl_country_code:
/*
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    }
                }, 500);
*/
                break;
            case R.id.tv_select_country:
                AppUtils.getInstance().hideKeyboard(this);
                CustomDialogForSelectCountry dialogForSelectCountry = new CustomDialogForSelectCountry(this, tvSelectCountry.getText().toString(), tvCountryCode.getText().toString(), new SelectCountryDialogCallback() {
                    @Override
                    public void onOkClick(String country) {
//                        tvSelectCountry.setText(country);
                        for (CountryBean countryBean : allCountriesList) {
                            if ((countryBean.getCountryEnglishName() + " (" + countryBean.getCountryCode() + ")").equalsIgnoreCase(country)) {
                                tvCountryCode.setText(countryBean.getCountryCode());
                                tvSelectCountry.setText(countryBean.getCountryEnglishName());
                            }
                        }
                    }
                });
                dialogForSelectCountry.show();
                break;
            case R.id.btn_action:
                if (AppUtils.getInstance().isInternetAvailable(this)) {
                    if (isValidate() && !isLoading) {
                        hitSignupApi();
                    }
                } else {
                    AppUtils.getInstance().showToast(this, getString(R.string.no_internet_connection));
                }
                break;
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
//                ImageCropper.startCaptureImageActivity(SignupActivity.this, Constants.IntentConstant.REQUEST_CAMERA, Constants.IntentConstant.REQUEST_CROPPER_CODE);
                File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/Shopoholic");
                if (!myDir.exists())
                    myDir.mkdir();
                String fname = "IMG_" + System.currentTimeMillis() + ".jpg";
                File file = new File(myDir, fname);
                outputUri = Uri.fromFile(file);
                ImageCropper.CROPPER_REQUEST_CODE = Constants.IntentConstant.REQUEST_CROPPER_CODE;
                startActivityForResult(new Intent(SignupActivity.this, CameraGalleryActivity.class)
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
                ImageCropper.CROPPER_REQUEST_CODE = Constants.IntentConstant.REQUEST_CROPPER_CODE;
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), Constants.IntentConstant.REQUEST_GALLERY);
//                ImageCropper.startPickImageFromGalleryActivity(SignupActivity.this, Constants.IntentConstant.REQUEST_GALLERY, Constants.IntentConstant.REQUEST_CROPPER_CODE);
            }

            @Override
            public void onRemove() {
                outputUri = null;
                imageUrl = "";
                ivSignupImage.setImageResource(R.drawable.ic_side_menu_user_placeholder);
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
                AppUtils.getInstance().setImages(this, outputUri.getPath(), ivSignupImage, 5, R.drawable.ic_side_menu_user_placeholder);
                imageBean = null;
                startUpload(AppUtils.getInstance().getPathFromUri(this, outputUri));
            }

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        return super.dispatchTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
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
        }  else if (etLastName.getText().toString().trim().length() != 0 && !etLastName.getText().toString().matches("^[a-zA-Z]+[a-zA-Z0-9 ]*")) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_last_name));
            return false;
        } else if (etEmail.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_email));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_email));
            return false;
        } else if (tvSelectCountry.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_country));
            return false;
        } else if (etPhoneNo.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_phone_no));
            return false;
        } else if (etPhoneNo.getText().toString().trim().length() < 7) {
            AppUtils.getInstance().showToast(this, getString(R.string.phone_no_must_be_7_15_digits));
            return false;
        } /*else if (!AppUtils.getInstance().isValidPhoneNumber(tvCountryCode.getText().toString(), etPhoneNo.getText().toString().trim())) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_phone_no));
            return false;
        }*/ else if (etPassword.getVisibility() == View.VISIBLE && etPassword.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_password));
            return false;
        } else if (etPassword.getVisibility() == View.VISIBLE && etPassword.getText().toString().length() < 6) {
            AppUtils.getInstance().showToast(this, getString(R.string.password_must_be_6_15_chars));
            return false;
        } else if (etConfirmPassword.getVisibility() == View.VISIBLE && etConfirmPassword.getText().toString().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_confir_password));
            return false;
        } else if (etPassword.getVisibility() == View.VISIBLE && !etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            AppUtils.getInstance().showToast(this, getString(R.string.password_not_match));
            return false;
        } else if (isUploading) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_wait_image_loading));
            return false;
        }

        return true;
    }


    /**
     * Method to hit the signup api
     */
    private void hitSignupApi() {
        String deviceToken = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.DEVICE_TOKEN);
        if (deviceToken == null || deviceToken.equals("")) {
            AppUtils.getInstance().getDeviceToken(this);
            deviceToken = FirebaseInstanceId.getInstance().getToken();
        }

        AppUtils.getInstance().setButtonLoaderAnimation(SignupActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        isLoading = true;
        String language = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE_CODE);

        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
//        params.put(Constants.NetworkConstant.PARAM_SOCIAL_ID, "1234567890");
        params.put(Constants.NetworkConstant.PARAM_SOCIAL_ID, userSocialModel == null ? "" : userSocialModel.getSocialId());
        params.put(Constants.NetworkConstant.PARAM_ACCOUNT_TYPE, userSocialModel == null ? "" : userSocialModel.getSocialType());
        params.put(Constants.NetworkConstant.PARAM_FIRST_NAME, etFirstName.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_LAST_NAME, etLastName.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_EMAIL, etEmail.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_COUNTRY_ID, tvCountryCode.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_MOBILE_NO, etPhoneNo.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_PASSWORD, etPassword.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_DEVICE_ID, AppUtils.getInstance().getDeviceId(this));
        params.put(Constants.NetworkConstant.PARAM_DEVICE_TOKEN, deviceToken);
        params.put(Constants.NetworkConstant.PARAM_PLATFORM, Constants.NetworkConstant.ANDROID_PLATFORM);
        params.put(Constants.NetworkConstant.PARAM_IMAGE, imageUrl.trim());
        params.put(Constants.NetworkConstant.PARAM_USER_TYPE, Constants.NetworkConstant.SHOPPER);
        params.put(Constants.NetworkConstant.PARAM_USER_LANGUAGE, language.equals("") ? "1" : language);
        Call<ResponseBody> call = apiInterface.hitSignUpApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_SIGN_UP);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(SignupActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        isLoading = false;
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_SIGN_UP:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject data = object.optJSONObject(Constants.NetworkConstant.RESULT);
                            userId = data.optString(Constants.NetworkConstant.USER_ID);
                            showSuccessDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Constants.NetworkConstant.NUMBER_NOT_REGISTERED:
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
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
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(SignupActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(SignupActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);

    }

    /**
     * Method to show success dialog after signup complete
     */
    private void showSuccessDialog() {
        CustomDialogForMessage messageDialog = new CustomDialogForMessage(this, getString(R.string.success), getString(R.string.signup_success_message),
                getString(R.string.ok), false, new MessageDialogCallback() {
            @Override
            public void onSubmitClick() {
                Intent intent = new Intent(SignupActivity.this, EnterOtpActivity.class);
                intent.putExtra(Constants.IntentConstant.USER_ID, userId);
                intent.putExtra(Constants.IntentConstant.COUNTRY_CODE, tvCountryCode.getText().toString().trim());
                intent.putExtra(Constants.IntentConstant.PHONE_NUMBER, etPhoneNo.getText().toString().trim());
                intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.SIGNUP);
                startActivity(intent);
//                AppUtils.getInstance().openNewActivity(SignupActivity.this, intent);
//                finish();

            }
        });
        messageDialog.show();
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
        AppUtils.getInstance().setImageLoaderAnimation(this, ivSignupImage, viewLoader, viewDot, true);
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
        AppUtils.getInstance().setImageLoaderAnimation(this, ivSignupImage, viewLoader, viewDot, false);
        imageUrl = imageBean.getServerUrl();
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        AppUtils.getInstance().showToast(this, getString(R.string.image_upload_fail));
        isUploading = false;
//        imageLoader.setVisibility(View.INVISIBLE);
        AppUtils.getInstance().setImageLoaderAnimation(this, ivSignupImage, viewLoader, viewDot, false);
        outputUri = null;
        imageUrl = "";
        ivSignupImage.setImageResource(R.drawable.ic_side_menu_user_placeholder);
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
        AppUtils.getInstance().setImageLoaderAnimation(this, ivSignupImage, viewLoader, viewDot, false);
    }
}
