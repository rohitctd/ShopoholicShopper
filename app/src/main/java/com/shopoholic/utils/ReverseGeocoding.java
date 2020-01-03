package com.shopoholic.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.shopoholic.R;
import com.shopoholic.interfaces.AddressCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReverseGeocoding extends AsyncTask<String, String, Address> {
    private static final String TAG = "TAG";
    Context mContext;
    private double latitude;
    private double longitude;
    private AddressCallback addressCallback;
    private Address address;

    public ReverseGeocoding(Context context, double latitude, double longitude , AddressCallback addressCallback) {
        super();
        mContext = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressCallback = addressCallback;
    }

    @Override
    protected Address doInBackground(String... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        String addressText = null;
        List<Address> addresses = null;
        try {
            // Call the synchronous getFromLocation() method by passing in the lat/long values.
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            address = addresses.get(0);
            // Format the first line of address (if available), city, and country name.
            addressText = String.format("%s, %s, %s, %s",
                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    address.getLocality(),
                    address.getCountryCode(),
                    address.getCountryName());
        }
        if (address == null) {
            addresses = getAddress(latitude, longitude);
            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0);
            }
        }
        return address;
    }

    @Override
    protected void onPostExecute(Address address) {
        super.onPostExecute(address);
        addressCallback.setAddress(address);
        if (address!= null && address.getCountryCode().equals("")){
            new AlertDialog.Builder(mContext, R.style.DatePickerTheme)
                    .setMessage(mContext.getString(R.string.unable_to_get_currency))
                    .setCancelable(true)
                    .setPositiveButton(mContext.getString(R.string.ok), (dialog, which) -> {
                        //
                    })
                    .show();
        }
    }


    /**
     * get address from url
     * @param latitude
     * @param longitude
     * @return
     */
    public List<Address> getAddress(double latitude, double longitude) {
        List<Address> retList = null;
        OkHttpClient client = new OkHttpClient();

        int maxResult = 1;
        String key = "&key=" + mContext.getString(R.string.gooogle_api_key);
        String url = String.format(Locale.US,
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=false&language="
                        + Locale.getDefault().getCountry() + key, latitude, longitude);
        Log.d(TAG, "address = " + url);
        Log.d(TAG, "Locale.getDefault().getCountry() = " + Locale.getDefault().getCountry());

        Request request = new Request.Builder().url(url)
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseStr = response.body().string();
            JSONObject jsonObject = new JSONObject(responseStr);

            retList = new ArrayList<Address>();

            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 0) {
                    for (int i = 0; i < results.length() && i < maxResult; i++) {
                        JSONObject result = results.getJSONObject(i);
                        Address addr = new Address(Locale.getDefault());

                        JSONArray components = result.getJSONArray("address_components");
                        String streetNumber = "";
                        String route = "";
                        for (int a = 0; a < components.length(); a++) {
                            JSONObject component = components.getJSONObject(a);
                            JSONArray types = component.getJSONArray("types");
                            for (int j = 0; j < types.length(); j++) {
                                String type = types.getString(j);
                                if (type.equals("locality")) {
                                    addr.setLocality(component.getString("long_name"));
                                } else if (type.equals("street_number")) {
                                    streetNumber = component.getString("long_name");
                                } else if (type.equals("route")) {
                                    route = component.getString("long_name");
                                } else if (type.equals("country")) {
                                    addr.setCountryName(component.getString("long_name"));
                                    addr.setCountryCode(component.getString("short_name"));
                                }
                            }
                        }
                        addr.setAddressLine(0, route + " " + streetNumber);

                        addr.setLatitude(
                                result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                        addr.setLongitude(
                                result.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                        retList.add(addr);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error calling Google geocode webservice.", e);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing Google geocode webservice response.", e);
        }

        return retList;
    }
}