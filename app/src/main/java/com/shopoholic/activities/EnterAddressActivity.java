package com.shopoholic.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dnitinverma.locationlibrary.RCLocation;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.shopoholic.R;
import com.shopoholic.adapters.CountryCodeAdapter;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForSelectCountry;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.interfaces.SelectCountryDialogCallback;
import com.shopoholic.models.countrymodel.CountryBean;
import com.shopoholic.models.deliveryaddressresponse.Result;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class created by Sachin on 19-Apr-18.
 */
public class EnterAddressActivity extends BaseActivity implements NetworkListener {


    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.et_first_name)
    CustomEditText etFirstName;
    @BindView(R.id.view_first_name)
    View viewFirstName;
    @BindView(R.id.et_last_name)
    CustomEditText etLastName;
    @BindView(R.id.view_last_name)
    View viewLastName;
    @BindView(R.id.tv_address_line_1)
    CustomTextView tvAddressLine1;
    @BindView(R.id.view_address_line_1)
    View viewAddressLine1;
    @BindView(R.id.tv_address_line_2)
    CustomEditText tvAddressLine2;
    @BindView(R.id.view_address_line_2)
    View viewAddressLine2;
    @BindView(R.id.tv_city)
    CustomEditText tvCity;
    @BindView(R.id.view_city)
    View viewCity;
    @BindView(R.id.tv_state)
    CustomEditText tvState;
    @BindView(R.id.view_state)
    View viewState;
    @BindView(R.id.tv_country)
    CustomEditText tvCountry;
    @BindView(R.id.view_country)
    View viewCountry;
    @BindView(R.id.tv_pincode)
    CustomEditText tvPincode;
    @BindView(R.id.view_pincode)
    View viewPincode;
    @BindView(R.id.et_phone_no)
    CustomEditText etPhoneNo;
    @BindView(R.id.view_phone_no)
    View viewPhoneNo;
    @BindView(R.id.tv_country_code)
    CustomTextView tvCountryCode;
    @BindView(R.id.iv_down)
    ImageView ivDown;
    @BindView(R.id.view_country_code)
    View viewCountryCode;
    @BindView(R.id.rl_country_code)
    RelativeLayout rlCountryCode;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.et_search)
    CustomEditText etSearch;
    @BindView(R.id.rv_country_code)
    RecyclerView rvCountryCode;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    private boolean isLoading;
    private double latitude;
    private double longitude;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private CountryCodeAdapter countryAdapter;
    private ArrayList<CountryBean> selectedCountriesList;
    private boolean openPlacePicker;
    private ArrayList<CountryBean> allCountriesList;
    private RCLocation location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_address);
        ButterKnife.bind(this);
        initVariables();
        initializeLocation();
        setListener();
    }


    /**
     * method to initialize the location
     */
    private void initializeLocation() {
        location = new RCLocation();
        location.setActivity(this);
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        selectedCountriesList = new ArrayList<>();
        allCountriesList = AppUtils.getInstance().getAllCountries(this);
        selectedCountriesList.addAll(allCountriesList);
        btnAction.setText(getString(R.string.save));
        tvTitle.setText(getString(R.string.add_address));
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(0);

        tvCountryCode.setText(AppUtils.getInstance().getUserCountryCode(this));

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
                    }
                }, 500);
            }
        });
        rvCountryCode.setLayoutManager(new LinearLayoutManager(this));
        rvCountryCode.setAdapter(countryAdapter);

        etFirstName.setText(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.FIRST_NAME));
        etLastName.setText(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.LAST_NAME));
        tvCountryCode.setText(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.COUNTRY_CODE));
        etPhoneNo.setText(AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.PHONE_NO));
        if (TextUtils.isEmpty(tvCountryCode.getText().toString().trim())){
            tvCountryCode.setText(AppUtils.getInstance().getUserCountryCode(this));
        }
        tvAddressLine1.requestFocus();
    }


    @OnClick({R.id.iv_menu, R.id.tv_address_line_1, R.id.btn_action, R.id.rl_country_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.rl_country_code:
                rlCountryCode.requestFocus();
/*
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 500);
*/

                AppUtils.getInstance().hideKeyboard(this);
                CustomDialogForSelectCountry dialogForSelectCountry = new CustomDialogForSelectCountry(this, "", tvCountryCode.getText().toString(), new SelectCountryDialogCallback() {
                    @Override
                    public void onOkClick(String country) {
                        for (CountryBean countryBean : AppUtils.getInstance().getAllCountries(EnterAddressActivity.this)) {
                            if ((countryBean.getCountryEnglishName() + " (" + countryBean.getCountryCode() + ")").equalsIgnoreCase(country)) {
                                tvCountryCode.setText(countryBean.getCountryCode());
                            }
                        }
                    }
                });
                dialogForSelectCountry.show();
                break;
            case R.id.btn_action:
                if ( !isLoading && AppUtils.getInstance().isInternetAvailable(this)) {
                    if (isValidate()) {
                        hitSaveAddressApi();
                    }
                }
                break;
            case R.id.tv_address_line_1:
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
                break;
        }
    }

    /**
     * method to set the listener on views
     */
    private void setListener() {
        etFirstName.addTextChangedListener(this);
        etLastName.addTextChangedListener(this);
        tvAddressLine1.addTextChangedListener(this);
        tvAddressLine2.addTextChangedListener(this);
        tvCity.addTextChangedListener(this);
        tvState.addTextChangedListener(this);
        tvCountry.addTextChangedListener(this);
        tvPincode.addTextChangedListener(this);
        etPhoneNo.addTextChangedListener(this);

/*
        tvAddressLine1.setParser(new AutoCompleteView.AutoCompleteResponseParser() {
            @Override
            public ArrayList<? extends Object> parseAutoCompleteResponse(String response) {
                ArrayList<Place> places = null;
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    final JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
                    String status = jsonObj.getString("status");
                    if (predsJsonArray.length() != 0 && status.equalsIgnoreCase("OK")) {
                        places = new ArrayList<Place>();
                        for (int i = 0; i < predsJsonArray.length(); i++) {
                            String placeName = predsJsonArray.getJSONObject(i).getString("description");
                            String placeReference = predsJsonArray.getJSONObject(i).getString("reference");
                            String placeId = predsJsonArray.getJSONObject(i).getString("place_id");
                            Place place = new Place();
                            place.setName(placeName);
                            place.setPhotoReference(placeReference);
                            place.setPlaceId(placeId);
                            places.add(place);
                        }
                    } else {
//                        Toast.makeText(MainActivity.this, "No result found for search", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("AppUtil", "Cannot process JSON results", e);
                }
                return places;
            }
        });
*/

/*
        tvAddressLine1.setSelectionListener(new AutoCompleteView.AutoCompleteItemSelectionListener() {

            @Override
            public void onItemSelection(Object obj) {
                AppUtils.getInstance().hideKeyboard(EnterAddressActivity.this);
                Place place = (Place) obj;
//                ((AutoCompleteView) findViewById(R.id.tv_address_line_1)).setText(place.getName());
//                ((AutoCompleteView) findViewById(R.id.tv_address_line_1)).clearFocus();
                String mapUrl = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place.getPlaceId() + "&key=" + getString(R.string.gooogle_api_key);
                new GetLocation(mapUrl, EnterAddressActivity.this).execute();
            }
        });
*/
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        AppUtils.getInstance().changeSeparatorViewColor(etFirstName, viewFirstName);
        AppUtils.getInstance().changeSeparatorViewColor(etLastName, viewLastName);
        AppUtils.getInstance().changeSeparatorViewColor(tvAddressLine1, viewAddressLine1);
        AppUtils.getInstance().changeSeparatorViewColor(tvAddressLine2, viewAddressLine2);
        AppUtils.getInstance().changeSeparatorViewColor(tvCity, viewCity);
        AppUtils.getInstance().changeSeparatorViewColor(tvState, viewState);
        AppUtils.getInstance().changeSeparatorViewColor(tvCountry, viewCountry);
        AppUtils.getInstance().changeSeparatorViewColor(tvPincode, viewPincode);
        AppUtils.getInstance().changeSeparatorViewColor(etPhoneNo, viewPhoneNo);
        AppUtils.getInstance().changeSeparatorViewColor(tvCountryCode, viewCountryCode);
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
        } else if (etLastName.getText().toString().trim().length() != 0 && !etLastName.getText().toString().matches("^[a-zA-Z]+[a-zA-Z0-9 ]*")) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_last_name));
            return false;
        } else if (tvAddressLine1.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_address));
            return false;
        }else if (tvCity.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_city));
            return false;
        }else if (tvState.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_state));
            return false;
        }else if (tvCountry.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_country));
            return false;
        }else if (tvPincode.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_pincode));
            return false;
        } else if (tvCountryCode.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_country_code));
            return false;
        } else if (etPhoneNo.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_phone_no));
            return false;
        } else if (etPhoneNo.getText().toString().trim().length() < 7) {
            AppUtils.getInstance().showToast(this, getString(R.string.phone_no_must_be_7_15_digits));
            return false;
        }/* else if (!AppUtils.getInstance().isValidPhoneNumber(tvCountryCode.getText().toString(), etPhoneNo.getText().toString().trim())) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_phone_no));
            return false;
        } */else if (latitude == 0.0 && longitude == 0.0) {
            AppUtils.getInstance().showToast(this, getString(R.string.please_enter_valid_address));
            return false;
        }
        return true;
    }

    /**
     * Method to hit the signup api
     */
    private void hitSaveAddressApi() {
        AppUtils.getInstance().setButtonLoaderAnimation(EnterAddressActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_NAME, etFirstName.getText().toString().trim() + " " + etLastName.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_ADDRESS, tvAddressLine1.getText().toString().trim()  + " " + tvAddressLine2.getText().toString().trim()
                + " " + tvCity.getText().toString().trim() + " " + tvState.getText().toString().trim() + " " + tvCountry.getText().toString().trim()
                + " " + tvPincode.getText().toString().trim());
        params.put(Constants.NetworkConstant.PARAM_LATITUDE, String.valueOf(latitude));
        params.put(Constants.NetworkConstant.PARAM_LONGITUDE, String.valueOf(longitude));
        params.put(Constants.NetworkConstant.PARAM_COUNTRY_ID, tvCountryCode.getText().toString().trim());

        params.put(Constants.NetworkConstant.PARAM_MOBILE_NO, etPhoneNo.getText().toString().trim());
        Call<ResponseBody> call = apiInterface.hitSaveAddressApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_SAVE_ADDRESS);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(EnterAddressActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        isLoading = false;
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_SAVE_ADDRESS:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        Result result = new Result();
                        result.setName(etFirstName.getText().toString().trim() + " " + etLastName.getText().toString().trim());
                        result.setLatitude(String.valueOf(latitude));
                        result.setLongitude(String.valueOf(longitude));
                        result.setMobileNo(etPhoneNo.getText().toString().trim());
                        result.setCountryId(tvCountryCode.getText().toString().trim());
                        result.setAddress(tvAddressLine1.getText().toString().trim()  + " " + tvAddressLine2.getText().toString().trim()
                                + " " + tvCity.getText().toString().trim() + " " + tvState.getText().toString().trim() + " " + tvCountry.getText().toString().trim()
                                + " " + tvPincode.getText().toString().trim());
                        Intent intent = new Intent();
                        intent.putExtra(Constants.IntentConstant.DELIVERY_ADDRESS, result);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(EnterAddressActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(EnterAddressActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        openPlacePicker = false;
        if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
//                Place place = PlaceAutocomplete.getPlace(this, data);
                Place place = PlacePicker.getPlace(this, data);
                if (place != null && place.getAddress() != null) {
                    tvAddressLine1.setText(place.getName());
                    tvAddressLine2.setText(place.getAddress().toString());
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;

                    tvAddressLine2.setText("");
                    tvAddressLine2.requestFocus();
                    try {
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        if (addresses != null && addresses.size() > 0) {
                            String address2 = (addresses.get(0).getFeatureName() != null ? addresses.get(0).getFeatureName() + ", " : "") +
                                    (addresses.get(0).getSubAdminArea() != null ? addresses.get(0).getSubAdminArea() : "");
                            tvAddressLine2.setText(address2);
                            tvAddressLine1.clearFocus();
                            tvCity.setText(addresses.get(0).getLocality());
                            tvState.setText(addresses.get(0).getAdminArea());
                            tvCountry.setText(addresses.get(0).getCountryName());
                            tvPincode.setText(addresses.get(0).getPostalCode());
                            tvCountryCode.requestFocus();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

/*

    *//**
     * Method used to parse lat and long from google api
     * @return
     *//*
    private class GetLocation extends AsyncTask {
        String url,result;
        private Context context;

        public GetLocation(String mapUrl, Context context) {
            url = mapUrl;
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpConnector httpConnector = new HttpConnector();
            result = httpConnector.getResponse(url);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            setLocation(result);
        }
    }

    *//**
     * method to set location
     * @param result
     *//*
    private void setLocation(String result) {
        Place detailBean = new Place();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject resultOb = jsonObject.getJSONObject("result");
            JSONObject object = resultOb.getJSONObject("geometry");
            JSONObject locationObj = object.getJSONObject("location");
            detailBean.lat = latitude = locationObj.getDouble("lat");
            detailBean.longi = longitude = locationObj.getDouble("lng");
            detailBean.setName(resultOb.getString("name"));

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && addresses.size() > 0) {
                tvAddressLine1.setText(addresses.get(0).getAddressLine(0)); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                tvAddressLine1.clearFocus();
                tvAddressLine2.setText("");
                tvAddressLine2.requestFocus();
                tvCity.setText(addresses.get(0).getLocality());
                tvState.setText(addresses.get(0).getAdminArea());
                tvCountry.setText(addresses.get(0).getCountryName());
                tvPincode.setText(addresses.get(0).getPostalCode());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
