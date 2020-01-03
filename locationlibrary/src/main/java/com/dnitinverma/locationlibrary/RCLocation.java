package com.dnitinverma.locationlibrary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.dnitinverma.locationlibrary.interfaces.LocationsCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by appinventiv on 12/9/17.
 */

public class RCLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final int FINE_LOCATION_PERMISSIONS = 0x001;
    public static final int REQUEST_CHECK_SETTINGS = 0x002;
    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 10 * 1000;//10 seconds
    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5 * 1000;//10 seconds
    private Activity mActivity;
    private LocationsCallback locationsCallback;
    private LocationRequest mLocationRequest;
    public GoogleApiClient mGoogleApiClient;
    private android.location.Location mCurrentLocation;



    /*
   *  Initialize activity instance
   */
    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    /*
    *  Initialize location callback
    */
    public void setCallback(LocationsCallback locationsCallback) {
        this.locationsCallback = locationsCallback;
    }


    /*
   * Method to check permission
   * */
    public void startLocation() {
        if (hasPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSIONS)) {
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Sets up the rcLocation request. Android has two rcLocation request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current rcLocation. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused RCLocation Provider API returns rcLocation updates that are
     * accurate to within a few feet.
     * <p>
     * These settings are appropriate for mapping applications that show real-time rcLocation
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                // Sets the desired interval for active rcLocation updates. This interval is
                // inexact. You may not receive updates at all if no rcLocation sources are available, or
                // you may receive them slower than requested. You may also receive updates faster than
                // requested if other applications are requesting rcLocation at a faster interval.
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                // Sets the fastest rate for active rcLocation updates. This interval is exact, and your
                // application will never receive updates faster than this value.
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setSmallestDisplacement(10);//set smallest displacement to change lat long
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed rcLocation settings.
     */
    private void buildLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);//this make sure dialog is always visible

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // RCLocation settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            status.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);//this prompts a user with a dialog, we get its call in onActivityResult.
                        } catch (IntentSender.SendIntentException ignored) {
                            //do nothing
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // RCLocation settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    /*
    * Method to check permissions
    * */
    private boolean hasPermission(Activity context, String permission, int reqId) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        if (result == PackageManager.PERMISSION_GRANTED) return true;
        else {
            ActivityCompat.requestPermissions(context, new String[]{permission}, reqId);
            return false;
        }
    }

    public void setPermissionresult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case FINE_LOCATION_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    createLocationRequest();
                    buildLocationSettingsRequest();
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissions[0])) {//has selected do not allow allow
                    showAllowPermissionFromSettingDialog(mActivity);
                } else {
                    Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                    //hasPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSIONS)
                }
                break;
        }
    }

    public void setActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    if(mGoogleApiClient!=null || !mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();//If person has successfully open the rcLocation try to connect to connect the mGoogleApi client
                } else {
                    buildLocationSettingsRequest();//otherwise show him the rcLocation dialog
                }
                break;
        }
    }

    /**
     * Requests rcLocation updates from the FusedLocationApi.
     */
    @SuppressWarnings({"MissingPermission"})//because permission case has been handled perfectly
    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    //Connection Callbacks Method
    @Override
    @SuppressWarnings({"MissingPermission"})//because permission case has been handled perfectly
    public void onConnected(Bundle bundle) {
        android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);//
            }
        } else {
            mCurrentLocation = location;
            locationsCallback.setLocation(mCurrentLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    //onConnection Failed method
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(mActivity, 1);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    // RCLocation listener method - this method gets call back when requestLocationUpdates is called
    @Override
    public void onLocationChanged(android.location.Location location) {
        if (location != null) {
            mCurrentLocation = location;
            locationsCallback.setLocation(mCurrentLocation);
        }
    }

    /*
    * Method to get address from lat and long
    * */
    public void getAddress() {
        if (mCurrentLocation != null)
            new ReverseGeocodingTask(mActivity, mCurrentLocation, locationsCallback).execute();
        else
            Toast.makeText(mActivity, "Please get rcLocation first", Toast.LENGTH_SHORT).show();
    }

    /*
    * Method to get rcLocation
    * */
    public void getLatAndLang(String address) {
        new DirectGeocodingTask(mActivity, address, locationsCallback).execute();
    }

    /*
    * Method to disconnect mGoogleApiClient
    * */
    public void disconnect() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
                locationsCallback.disconnect();
            } else {
//                Toast.makeText(mActivity, "Not connected", Toast.LENGTH_SHORT).show();
            }
        } else {
//            Toast.makeText(mActivity, "Not connected", Toast.LENGTH_SHORT).show();
        }
    }

        /**
     * This method is used to show permission allow from Setting
         * Please add this in utils or as per your requirement
     */
    public void showAllowPermissionFromSettingDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Enable RCLocation Permission");
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


}
