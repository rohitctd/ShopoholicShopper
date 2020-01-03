package com.dnitinverma.locationlibrary;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.dnitinverma.locationlibrary.interfaces.LocationsCallback;

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

import static android.content.ContentValues.TAG;

/**
 * Created by appinventiv on 12/9/17.
 */

public class ReverseGeocodingTask extends AsyncTask<String, String, Address> {
    Context mContext;
    private android.location.Location location;
    private LocationsCallback locationsCallback;
    private Address address;

    public ReverseGeocodingTask(Context context, android.location.Location location,LocationsCallback locationsCallback) {
        super();
        mContext = context;
        this.location = location;
        this.locationsCallback = locationsCallback;
    }

    @Override
    protected Address doInBackground(String... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        String addressText = null;
        List<Address> addresses = null;
        try {
            // Call the synchronous getFromLocation() method by passing in the lat/long values.
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            address = addresses.get(0);
            // Format the first line of address (if available), city, and country name.
            addressText = String.format("%s, %s, %s",
                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    address.getLocality(),
                    address.getCountryCode(),
                    address.getCountryName());
        }
        if (address == null) {
            addresses = getAddress(location.getLatitude(), location.getLongitude());
            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0);
            }
        }
        return address;
    }

    @Override
    protected void onPostExecute(Address address) {
        super.onPostExecute(address);
        locationsCallback.setAddress(address);
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