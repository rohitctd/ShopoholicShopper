package com.dnitinverma.locationlibrary;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.dnitinverma.locationlibrary.interfaces.LocationsCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by appinventiv on 12/9/17.
 */

public class DirectGeocodingTask extends AsyncTask<String, String, String> {
    Context mContext;
    private String address;
    private LocationsCallback locationsCallback;
    private Address location;

    public DirectGeocodingTask(Context context, String address, LocationsCallback locationsCallback) {
        super();
        mContext = context;
        this.address = address;
        this.locationsCallback = locationsCallback;
    }

    @Override
    protected String doInBackground(String... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        String addressText = null;
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 5);
            if (address == null) {
                return null;
            }
            location = addressList.get(0);
        }
        catch (Exception e)
        {e.printStackTrace();}
        return addressText;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        locationsCallback.setLatAndLong(location);
    }
}