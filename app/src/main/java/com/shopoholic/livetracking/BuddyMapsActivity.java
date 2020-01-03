package com.shopoholic.livetracking;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.shopoholic.R;
import com.shopoholic.ShopoholicApplication;
import com.shopoholic.customviews.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class BuddyMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationUpdatesTask.LocationChanged, DirectionsTask.DirectionListener {

    private GoogleMap mMap;
    private LocationUpdatesTask mLocationUpdates;
    private Polyline mPolyline;
    private Location mCurrentLocation;
    private Location mDestinationLocation;
    private Location mOriginLocation;
    private boolean mReqGoogleApiConnection;
    private Marker mBuddyMarker;
    private Socket mSocket;
    private Emitter.Listener trackBuddy = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BuddyMapsActivity.this, args[0].toString(), Toast.LENGTH_LONG).show();
                    Log.d("Track Buddy", args[0].toString());
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (mLocationUpdates != null) {
            mLocationUpdates.connectGoogleApiClient();
        }
    }


    private void initVariables() {
        mLocationUpdates = new LocationUpdatesTask(this,
                this,
                1000,
                1000);

        if (mSocket == null)
            mSocket = ((ShopoholicApplication) getApplication()).getSocket();


    }
    private void getLatLngFromIntent() {

        mOriginLocation = new Location("origin");
        mOriginLocation.setLatitude(28.570096);
        mOriginLocation.setLongitude(77.322645);

        mDestinationLocation = new Location("destination");
        mDestinationLocation.setLatitude(28.634293);
        mDestinationLocation.setLongitude(77.2998647);


    }

    @Override
    public void onLocationUpdated(Location location) {
        Log.e("onLocationResult: ", String.valueOf(location.getLatitude()));
        Log.e("onLocationResult: ", String.valueOf(location.getLongitude()));
        mCurrentLocation = location;
        requestDirections(mCurrentLocation, mDestinationLocation);
        adjustCamera(mCurrentLocation, mDestinationLocation, mOriginLocation);
        setBuddyCurrentLocationMarker(location);
        emitLocation(location);

    }

    private void emitLocation(Location location) {
        if (mSocket != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lat", location.getLatitude());
                jsonObject.put("user_id", "345");
                jsonObject.put("order_id", "1");
                jsonObject.put("long", location.getLongitude());
                mSocket.emit("locationupdate", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void requestDirections(Location originLocation, Location destinationLocation) {
        new DirectionsTask(this, 1).execute(
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

        //here map instance is with us so we can add markers of origin and destination.
        setOriginAndDestinationMarker();

    }


    private void setBuddyCurrentLocationMarker(Location buddyCurrentLocation) {

        if (mBuddyMarker == null && buddyCurrentLocation != null && mMap != null) {
            mBuddyMarker = mMap.addMarker
                    (new MarkerOptions()
                            .position(
                                    new LatLng(buddyCurrentLocation.getLatitude(),
                                            buddyCurrentLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title("Buddy"));

        } else if (buddyCurrentLocation != null && mMap != null)
            mBuddyMarker
                    .setPosition(
                            new LatLng(buddyCurrentLocation.getLatitude(),
                                    buddyCurrentLocation.getLongitude()));

    }

    private void setOriginAndDestinationMarker() {

        mMap.addMarker
                (new MarkerOptions()
                        .position(
                                new LatLng(mOriginLocation.getLatitude(), mOriginLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title("Merchant"));


        mMap.addMarker
                (new MarkerOptions()
                        .position(
                                new LatLng(mDestinationLocation.getLatitude(), mDestinationLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title("Shopper"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationUpdates != null)
            mLocationUpdates.disconnectGoogleApiClient();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mLocationUpdates.REQUEST_CHECK_SETTINGS)
            mLocationUpdates.onActivityResult(requestCode, resultCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (LocationUpdatesTask.FINE_LOCATION_PERMISSIONS == requestCode) {
            mLocationUpdates.setPermissionResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onDirectionRequestCompleted(PolylineOptions polylineOptions, int type) {
        if (mMap != null && polylineOptions != null) {
            if (mPolyline != null)
                mPolyline.remove();
            mPolyline = mMap.addPolyline(polylineOptions);
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
    public void adjustCamera(Location originLocation, Location currentLocation, Location destinationLocation) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (currentLocation != null)
            builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        if (destinationLocation != null)
            builder.include(new LatLng(destinationLocation.getLatitude(), destinationLocation.getLongitude()));

        if (originLocation != null)
            builder.include(new LatLng(originLocation.getLatitude(), originLocation.getLongitude()));


        int padding = 150;
        LatLngBounds bounds = builder.build();
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
