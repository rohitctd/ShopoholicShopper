package com.shopoholic.livetracking;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class LocationUpdatesTask implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static final int FINE_LOCATION_PERMISSIONS = 0x001;
    public final int REQUEST_CHECK_SETTINGS = 0x002;
    public long updateIntervalInMilliseconds ;//10 seconds Initialized in constructor
    public long fastestUpdateIntervalInMilliseconds;//10 seconds Initialized in constructor
    private GoogleApiClient mGoogleApiClient;
    private Activity mActivity;
    private LocationChanged locationChanged;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallBacks;


    public LocationUpdatesTask(Activity mActivity, LocationChanged locationChanged,
                               long updateIntervalInMilliseconds, long fastestUpdateIntervalInMilliseconds) {
        this.mActivity = mActivity;
        this.locationChanged = locationChanged;
        this.updateIntervalInMilliseconds = updateIntervalInMilliseconds;//10 seconds
        this.fastestUpdateIntervalInMilliseconds = fastestUpdateIntervalInMilliseconds;//10 seconds
        initLocationCallBacks();

    }






    private void initLocationCallBacks() {
        mLocationCallBacks = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    locationChanged.onLocationUpdated(location);

                        /*
                        Log.e("onLocationResult: ", String.valueOf(location.getLatitude()));
                        Log.e("onLocationResult: ", String.valueOf(location.getLongitude()));
                        // Update UI with location data
                        // ...*/
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
    }


    public void connectGoogleApiClient() {
        if (checkHasPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSIONS)) {
            createLocationRequest();
            buildLocationSettingsRequest();
        }
    }


    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                // Sets the desired interval for active location updates. This interval is
                // inexact. You may not receive updates at all if no location sources are available, or
                // you may receive them slower than requested. You may also receive updates faster than
                // requested if other applications are requesting location at a faster interval.
                .setInterval(updateIntervalInMilliseconds)
                // Sets the fastest rate for active location updates. This interval is exact, and your
                // application will never receive updates faster than this value.
                .setFastestInterval(fastestUpdateIntervalInMilliseconds)

                .setSmallestDisplacement(0);//set smallest displacement to change lat long

    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    private void buildGoogleApiClient() {

        // Check if instance is already initialized.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Connect if the Google Api Client is not connected & it will throw callback in
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

    }


    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);//this make sure dialog is always visible


        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(mActivity).checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    response.getLocationSettingsStates();
                    buildGoogleApiClient();
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        mActivity,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException | ClassCastException e) {
                                e.printStackTrace();
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }

            }
        });

    }

    public void disconnectGoogleApiClient(){
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            LocationServices.getFusedLocationProviderClient(mActivity).removeLocationUpdates(mLocationCallBacks);
            mGoogleApiClient.disconnect();
        }
    }


    private boolean checkHasPermission(Activity context, String permission, int reqId) {

        int result = ContextCompat.checkSelfPermission(context, permission);
        if (result == PackageManager.PERMISSION_GRANTED) return true;
        else {
            ActivityCompat.requestPermissions(context, new String[]{permission}, reqId);
            return false;
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    @SuppressWarnings({"MissingPermission"})//because permission case has been handled perfectly
    private void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.getFusedLocationProviderClient(mActivity).requestLocationUpdates(mLocationRequest,mLocationCallBacks,null);
        }
    }

    public void onActivityResult(int requestCode, int resultCode) {
    // final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                         startLocationUpdates();
                        // All required changes were successfully made
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void setPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSIONS:
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connectGoogleApiClient();
                } else if (permissions.length > 0 && !ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissions[0])) {//has selected do not allow allow
                    showAllowPermissionFromSettingDialog(mActivity);
                } else {
                    Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                    //hasPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSIONS)
                }
                break;
        }
    }


    private void showAllowPermissionFromSettingDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Enable Location Permission");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", activity.getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                activity.finish();
            }
        });
        builder.create().show();
    }


    public interface LocationChanged {
        public void onLocationUpdated(Location location);
    }

}
