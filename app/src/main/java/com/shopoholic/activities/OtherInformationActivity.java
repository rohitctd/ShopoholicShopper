package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Activity to enter user other information
 */

public class OtherInformationActivity extends BaseActivity implements NetworkListener, View.OnTouchListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.iv_male)
    ImageView ivMale;
    @BindView(R.id.iv_female)
    ImageView ivFemale;
    @BindView(R.id.iv_other)
    ImageView ivOther;
    @BindView(R.id.tv_dob)
    CustomTextView tvDob;
    @BindView(R.id.view_dob)
    View viewDob;
    @BindView(R.id.tv_anniversary)
    CustomTextView tvAnniversary;
    @BindView(R.id.view_anniversary)
    View viewAnniversary;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.tv_skip)
    CustomTextView tvSkip;
    @BindView(R.id.iv_dob)
    ImageView ivDob;
    @BindView(R.id.iv_anniversary)
    ImageView ivAnniversary;
    private Menu menu;
    private int gender = 1;
    private Animation animation;
    private boolean isLoading;
    private boolean openPlacePicker;
    private String dob, annversary, address;
    private double latitude = 0.0, longitude = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_information);
        ButterKnife.bind(this);
        setListener();
    }

    /**
     * Method to set listener on views
     */
    private void setListener() {
        btnAction.setText(getString(R.string.next));
        tvDob.addTextChangedListener(this);
        tvAnniversary.addTextChangedListener(this);
//        tvDob.setOnTouchListener(this);
//        tvAnniversary.setOnTouchListener(this);
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (tvDob.getText().toString().length() > 0) {
            viewDob.setBackgroundResource(R.color.colorSeparatorFilled);
            ivDob.setImageResource(R.drawable.ic_add_deal_cross);
        } else {
            viewDob.setBackgroundResource(R.color.colorSeparator);
            ivDob.setImageResource(R.drawable.ic_shoppers_other_info_calender);
        }
        if (tvAnniversary.getText().toString().length() > 0) {
            viewAnniversary.setBackgroundResource(R.color.colorSeparatorFilled);
            ivAnniversary.setImageResource(R.drawable.ic_add_deal_cross);
        } else {
            viewAnniversary.setBackgroundResource(R.color.colorSeparator);
            ivAnniversary.setImageResource(R.drawable.ic_shoppers_other_info_calender);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        CustomTextView viewEditText;
        if (v.getId() == R.id.tv_dob) {
            viewEditText = tvDob;
        } else {
            viewEditText = tvAnniversary;
        }
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        Drawable drawable;
        if (viewEditText.getCompoundDrawables()[DRAWABLE_RIGHT] != null) drawable = viewEditText.getCompoundDrawables()[DRAWABLE_RIGHT];
        else drawable = viewEditText.getCompoundDrawables()[DRAWABLE_LEFT];
        if (drawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (viewEditText.getRight() - drawable.getBounds().width())) {
                // your action here
                if (viewEditText.getText().toString().trim().length() == 0) {
                    if (v.getId() == R.id.tv_dob) {
                        AppUtils.getInstance().openDatePickerAndSetDate(this, tvDob, true, tvAnniversary, tvDob.getText().toString().trim());
                    } else {
                        AppUtils.getInstance().openDatePickerAndSetDate(this, tvAnniversary, false, tvDob, tvAnniversary.getText().toString().trim());
                    }
                } else {
                    viewEditText.setText("");
                }
                return true;
            }
        }
        return false;
    }

    @OnClick({R.id.iv_back, R.id.iv_male, R.id.iv_female, R.id.iv_other, R.id.tv_dob, R.id.tv_anniversary, R.id.iv_dob, R.id.iv_anniversary,
            R.id.tv_location, R.id.btn_action, R.id.tv_skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_male:
                gender = 1;
                ivMale.setImageResource(R.drawable.ic_shoppers_other_info_male_selected);
                ivFemale.setImageResource(R.drawable.ic_shoppers_other_info_female_unselected);
                ivOther.setImageResource(R.drawable.ic_other_gender_inactive);
                AppUtils.getInstance().bounceAnimation(this, ivMale);
                break;
            case R.id.iv_female:
                gender = 2;
                ivMale.setImageResource(R.drawable.ic_shoppers_other_info_male_unselected);
                ivFemale.setImageResource(R.drawable.ic_shoppers_other_info_female_selected);
                ivOther.setImageResource(R.drawable.ic_other_gender_inactive);
                AppUtils.getInstance().bounceAnimation(this, ivFemale);
                break;
            case R.id.iv_other:
                gender = 3;
                ivMale.setImageResource(R.drawable.ic_shoppers_other_info_male_unselected);
                ivFemale.setImageResource(R.drawable.ic_shoppers_other_info_female_unselected);
                ivOther.setImageResource(R.drawable.ic_other_gender_active);
                AppUtils.getInstance().bounceAnimation(this, ivOther);
                break;
            case R.id.tv_dob:
                AppUtils.getInstance().openDatePickerAndSetDate(this, tvDob, true, tvAnniversary, tvDob.getText().toString().trim());
                break;
            case R.id.tv_anniversary:
                AppUtils.getInstance().openDatePickerAndSetDate(this, tvAnniversary, false, tvDob, tvAnniversary.getText().toString().trim());
                break;
            case R.id.iv_dob:
                if (tvDob.getText().toString().trim().length() == 0) {
                    AppUtils.getInstance().openDatePickerAndSetDate(this, tvDob, true, tvAnniversary, tvDob.getText().toString().trim());
                }else {
                    tvDob.setText("");
                }
                break;
            case R.id.iv_anniversary:
                if (tvAnniversary.getText().toString().trim().length() == 0) {
                    AppUtils.getInstance().openDatePickerAndSetDate(this, tvAnniversary, false, tvDob, tvAnniversary.getText().toString().trim());
                }else {
                    tvAnniversary.setText("");
                }
                break;
            case R.id.btn_action:
                if (!isLoading) {
                    if (AppUtils.getInstance().isInternetAvailable(this)) {
                        /*if (TextUtils.isEmpty(tvLocation.getText().toString().trim())) {
                            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_address));
                            return;
                        }
                        else if (latitude == 0.0 && longitude == 0.0) {
                            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_address));
                            return;
                        }*/
                        hitSaveInformationApi();
                    }
                }
                break;
            case R.id.tv_skip:
                startActivity(new Intent(OtherInformationActivity.this, HomeActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();

//                startActivity(new Intent(OtherInformationActivity.this, CardDetailsActivity.class));
                break;
            case R.id.tv_location:
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
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER) {
            openPlacePicker = false;
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                if (place.getAddress() != null) {
                    String address = "";
                    if (place.getName()!= null && !place.getName().equals("") && !place.getName().toString().contains("\"")) {
                        address += place.getName() + ", ";
                    }
                    address += place.getAddress().toString();
                    tvLocation.setText(address);
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                }
            }
        }
    }

    /**
     * method to hit save information api
     */
    private void hitSaveInformationApi() {
        dob = tvDob.getText().toString().trim().equals("") ? "" :
                AppUtils.getInstance().formatDate(tvDob.getText().toString().trim(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT);
        annversary = tvAnniversary.getText().toString().trim().equals("") ? "" :
                AppUtils.getInstance().formatDate(tvAnniversary.getText().toString().trim(), Constants.AppConstant.DATE_FORMAT, Constants.AppConstant.SERVICE_DATE_FORMAT);
        address = tvLocation.getText().toString().trim();

        AppUtils.getInstance().setButtonLoaderAnimation(OtherInformationActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);

        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
//        params.put(Constants.NetworkConstant.USER_ID, "5");
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_GENDER, String.valueOf(gender));
        params.put(Constants.NetworkConstant.PARAM_DOB, dob);
        params.put(Constants.NetworkConstant.PARAM_ANNIVERSARY, annversary);
        params.put(Constants.NetworkConstant.PARAM_ADDRESS, address);
        params.put(Constants.NetworkConstant.PARAM_LATITUDE, String.valueOf(latitude));
        params.put(Constants.NetworkConstant.PARAM_LONGITUDE, String.valueOf(longitude));
        Call<ResponseBody> call = apiInterface.hitSaveInformationApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_OTHER_INFORMATION);

    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(OtherInformationActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        isLoading = false;
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_OTHER_INFORMATION:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.GENDER, String.valueOf(gender));
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.DOB, dob);
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.ANNVERSARY, annversary);
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.ADDRESS, address);
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.LATITUDE, String.valueOf(latitude));
                        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.LONGITUDE, String.valueOf(longitude));

                        startActivity(new Intent(this, HomeActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();

//                        startActivity(new Intent(this, CardDetailsActivity.class));
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
        AppUtils.getInstance().setButtonLoaderAnimation(OtherInformationActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(OtherInformationActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);

    }

}
