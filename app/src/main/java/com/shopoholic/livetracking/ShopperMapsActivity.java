package com.shopoholic.livetracking;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.ShopoholicApplication;
import com.shopoholic.activities.BaseActivity;
import com.shopoholic.activities.QRCodeActivity;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.livetracking.trackresopnse.TrackResponse;
import com.shopoholic.models.purchaseorderresponse.Result;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.Manifest.permission.CALL_PHONE;

public class ShopperMapsActivity extends BaseActivity implements OnMapReadyCallback, DirectionsTask.DirectionListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.iv_buddy_image)
    CircleImageView ivBuddyImage;
    @BindView(R.id.tv_buddy_name)
    CustomTextView tvBuddyName;
    @BindView(R.id.btn_call)
    CustomTextView btnCall;
    @BindView(R.id.btn_show_code)
    CustomTextView btnShowCode;
    @BindView(R.id.iv_my_location)
    ImageView ivMyLocation;

    private final int MERCHANT = 1, SHOPPER = 2;
    private GoogleMap mMap;
    private LocationUpdatesTask mLocationUpdates;
    private Polyline merchantPolyline, shopperPolyline;
    private Location mCurrentLocation;
    private Location mDestinationLocation;
    private Location mOriginLocation;
    private boolean mReqGoogleApiConnection;
    private Marker mBuddyMarker;
    private String orderId = "";
    private String buddyId = "";
    private String merchantId = "";
    private String buddyNumber = "";
    private Socket mSocket;
    private com.shopoholic.models.orderlistdetailsresponse.Result orderDetails;
    private boolean onCenterClick = true;
    private Emitter.Listener trackBuddy = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(() -> {
                AppUtils.getInstance().printLogMessage("Location", args[0].toString());

                TrackResponse trackResponse = new Gson().fromJson(args[0].toString(), TrackResponse.class);
                //Todo do your stuff here
                if (trackResponse.getResult().getOrderId().toString().equals(orderId)) {
                    Location location = new Location("BuddyLocation");
                    location.setLatitude(trackResponse.getResult().getBlat());//Blat
                    location.setLongitude(trackResponse.getResult().getBlong());//Blong
                    mCurrentLocation = location;
                    requestDirections(SHOPPER, mCurrentLocation, mDestinationLocation);
                    if (mOriginLocation != null && !buddyId.equals(merchantId)) {
                        requestDirections(MERCHANT, mCurrentLocation, mOriginLocation);
                    }
                    setBuddyCurrentLocationMarker(location);
                    if (onCenterClick) {
                        adjustCamera(mCurrentLocation, mDestinationLocation, mOriginLocation);
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        //Get origin and destination latlng from the intent and start processing it.
        getLatLngFromIntent();

        initVariables();
        emitLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * method to initialize the variables
     */
    private void initVariables() {

        if (mSocket == null)
            mSocket = ((ShopoholicApplication) getApplication()).getSocket();


    }

    /**
     * method to get the lat long from  intent
     */
    private void getLatLngFromIntent() {

        LatLng latLngMerchant = null;
        LatLng latLng = null;
        if (getIntent() != null && getIntent().getExtras() != null) {
            latLng = getIntent().getExtras().getParcelable(Constants.IntentConstant.LOCATION);
            latLngMerchant = getIntent().getExtras().getParcelable(Constants.IntentConstant.LOCATION_MERCHANT);
            orderId = getIntent().getExtras().getString(Constants.IntentConstant.ORDER_ID);
            orderDetails = (com.shopoholic.models.orderlistdetailsresponse.Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.ORDER_DETAILS);
            merchantId = getIntent().getExtras().getString(Constants.IntentConstant.MERCHANT_ID);
            buddyId = getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_ID);
            AppUtils.getInstance().setImages(this, getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_IMAGE, ""), ivBuddyImage, 0, R.drawable.ic_side_menu_user_placeholder);
            tvBuddyName.setText(getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_NAME, ""));
            buddyNumber = getIntent().getExtras().getString(Constants.IntentConstant.BUDDY_NUNBER, "");
        }

        if (latLngMerchant != null) {
            mOriginLocation = new Location("origin");
            mOriginLocation.setLatitude(latLngMerchant.latitude);
            mOriginLocation.setLongitude(latLngMerchant.longitude);
        }

        mDestinationLocation = new Location("destination");
        mDestinationLocation.setLatitude(latLng == null ? 0 : latLng.latitude);
        mDestinationLocation.setLongitude(latLng == null ? 0 : latLng.longitude);

        tvTitle.setText(R.string.track_order);
        ivBack.setVisibility(View.VISIBLE);

    }


    @OnClick({R.id.iv_back, R.id.btn_call, R.id.btn_show_code, R.id.iv_my_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_call:
                checkCallPermission();
                break;
            case R.id.iv_my_location:
                onCenterClick = true;
                adjustCamera(mCurrentLocation, mDestinationLocation, mOriginLocation);
                break;
            case R.id.btn_show_code:
                if (orderDetails.getSlotArr().size() == 0) {
                    openQRCodeActivity();
                }else {
                    new AlertDialog.Builder(this, R.style.DatePickerTheme)
                            .setMessage(getString(R.string.sure_to_show_code))
                            .setPositiveButton(getString(R.string.yes), (dialog, which) -> openQRCodeActivity())
                            .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                                // do nothing
                            })
                            .show();
                }
                break;
        }
    }

    /**
     * function to open qr code activity
     */
    private void openQRCodeActivity() {
        Intent intent = new Intent(ShopperMapsActivity.this, QRCodeActivity.class);
        if (orderDetails != null) {
            Result result = new Result();
            result.setId(orderDetails.getId());
            result.setOrderNumber(orderDetails.getOrderNumber());
            result.setDealName(orderDetails.getDealName());
            result.setOrderDate(orderDetails.getOrderDate());
            result.setDealImage(orderDetails.getDealImage());
            intent.putExtra(Constants.IntentConstant.ORDER_DETAILS, result);
            startActivity(intent);
        }
    }


    /**
     * method to check the call permission
     */
    public void checkCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, Constants.IntentConstant.REQUEST_CALL);
            } else {
                callBuddy();
            }
        } else {
            callBuddy();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.IntentConstant.REQUEST_CALL:
                boolean isRationalGalleryStorage = !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) &&
                        ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callBuddy();
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_location_permission));
                }

                break;
            case LocationUpdatesTask.FINE_LOCATION_PERMISSIONS:
                mLocationUpdates.setPermissionResult(requestCode, permissions, grantResults);
                break;
        }
    }


    /**
     * method to make phone call to buddy
     */
    private void callBuddy() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + buddyNumber));
        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        }
    }

    /**
     * method to emit the location
     */
    private void emitLocation() {
        if (mSocket != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
//                jsonObject.put("order_id", "35");
                mSocket.emit("trackbuddy", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void requestDirections(int type, Location originLocation, Location destinationLocation) {
        new DirectionsTask(this, type).execute(
                DirectionsTask.
                        getUrl(this, new LatLng(originLocation.getLatitude(), originLocation.getLongitude()),
                                new LatLng(destinationLocation.getLatitude(), destinationLocation.getLongitude())));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(latLng -> {
            Log.d("TAG", "onMapClick");
            onCenterClick = false;
        });

        mMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                Log.d("TAG", "onCameraMoveStarted");
                onCenterClick = false;
            }
        });
        //here map instance is with us so we can add markers of origin and destination.
        setOriginAndDestinationMarker();

    }

    /**
     * function to set buddy marker
     * @param buddyCurrentLocation
     */
    private void setBuddyCurrentLocationMarker(Location buddyCurrentLocation) {

        if (mBuddyMarker == null && buddyCurrentLocation != null && mMap != null) {
            mBuddyMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(buddyCurrentLocation.getLatitude(), buddyCurrentLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_request_dot_icon))
                    .title(getString(R.string.buddy)));

        } else if (buddyCurrentLocation != null && mMap != null)
            mBuddyMarker.setPosition(
                            new LatLng(buddyCurrentLocation.getLatitude(), buddyCurrentLocation.getLongitude()));

    }

    /**
     * function to origin and destination marker
     */
    private void setOriginAndDestinationMarker() {

        if (mOriginLocation != null && !buddyId.equals(merchantId)) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mOriginLocation.getLatitude(), mOriginLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shop_2))
                    .title(getString(R.string.merchant)));
        }


        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mDestinationLocation.getLatitude(), mDestinationLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_2))
                .title(getString(R.string.shopper)));

        if (onCenterClick) {
            adjustCamera(mCurrentLocation, mDestinationLocation, mOriginLocation);
        }
        if (mOriginLocation != null && !buddyId.equals(merchantId)) {
            requestDirections(MERCHANT, mDestinationLocation, mOriginLocation);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mLocationUpdates.REQUEST_CHECK_SETTINGS)
            mLocationUpdates.onActivityResult(requestCode, resultCode);
    }


    @Override
    public void onDirectionRequestCompleted(PolylineOptions polylineOptions,int type) {
        if (mMap != null && polylineOptions != null) {
            if (type == MERCHANT) {
                if (merchantPolyline != null)
                    merchantPolyline.remove();
                merchantPolyline = mMap.addPolyline(polylineOptions);
                merchantPolyline.setColor(ContextCompat.getColor(this, R.color.colorToolbarStart));
            } else {
                if (shopperPolyline != null)
                    shopperPolyline.remove();
                shopperPolyline = mMap.addPolyline(polylineOptions);
                shopperPolyline.setColor(ContextCompat.getColor(this, R.color.colorToolbarStart));
            }
        }


    }


    /**
     * Adjust camera according to Lat Lng values for adjusting zoom area.
     *
     * @param originLocation
     * @param currentLocation     is the parameter for current location of
     *                            user which he is fetching from location listener
     * @param destinationLocation is the parameter for static location
     *                            to which user is drawing path from current
     */
    public void adjustCamera(Location currentLocation, Location destinationLocation, Location originLocation) {
        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            if (currentLocation != null && currentLocation.getLatitude() != 0 && currentLocation.getLongitude() != 0)
                builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            if (destinationLocation != null && destinationLocation.getLatitude() != 0 && destinationLocation.getLongitude() != 0)
                builder.include(new LatLng(destinationLocation.getLatitude(), destinationLocation.getLongitude()));

            if (!merchantId.equals(buddyId) && originLocation != null && originLocation.getLatitude() != 0 && originLocation.getLongitude() != 0)
                builder.include(new LatLng(originLocation.getLatitude(), originLocation.getLongitude()));


            int padding = 200;
            LatLngBounds bounds = builder.build();
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSocket != null) {
            mSocket.on("trackbuddy", trackBuddy);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSocket != null) {
            mSocket.off("trackbuddy");
        }
    }
}
