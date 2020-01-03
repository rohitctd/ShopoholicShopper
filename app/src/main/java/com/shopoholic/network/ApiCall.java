package com.shopoholic.network;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.shopoholic.R;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ApiCall.java
 * This singleton class is used to hit service and handle response at single place.
 *  @author Appinvetiv
 *  @version 1.0
 *  @since 1.0
 */
public class ApiCall {
    private static ApiCall apiCall = null;
    private final String LOG_TAG = "Network Connection";


    /**
     * This method is used to provide the instance of Network Connection Class
     * @return instance of Network Connection Class
     */
    public static ApiCall getInstance() {
        if (apiCall == null) {
            return new ApiCall();
        } else {
            return apiCall;
        }
    }


    /**
     * This class is used to hit the service and handle their responses
     * @param context  - Context of the class
     * @param bodyCall - retrofit call
     * @param requestCode
     */
    public void hitService(final AppCompatActivity context, Call<ResponseBody> bodyCall, final NetworkListener networkListener, final int requestCode) {
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response != null) {
                        if (response.body() != null) {
                            String encryptResp = response.body().string();
                            String decresp = encryptResp;
                            if (!Constants.SecretKey.equals(""))
                                decresp = AppUtils.getInstance().decryptMsg(encryptResp.getBytes(), AppUtils.getInstance().generateKey(Constants.SecretKey));
                            int responseCode = new JSONObject(decresp.trim()).optInt(Constants.NetworkConstant.CODE);
                            if (AppSharedPreference.getInstance().getBoolean(context, AppSharedPreference.PREF_KEY.IS_SIGN_UP)) {
                                if (responseCode == 401) {
                                    AppUtils.getInstance().showSessionPopup(responseCode, context);
                                } else if (responseCode == 101) {
                                    AppUtils.getInstance().showSessionPopup(responseCode, context);
                                } else if (responseCode == 103) {
                                    AppUtils.getInstance().showSessionPopup(responseCode, context);
                                } else {
                                    if (networkListener != null)
                                        networkListener.onSuccess(responseCode, decresp.trim(), requestCode);
                                }
                            } else if (networkListener != null)
                                    networkListener.onSuccess(responseCode, decresp.trim(), requestCode);
                        } else if (response.errorBody() != null && networkListener != null)
                            networkListener.onError(response.errorBody().string(), requestCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    networkListener.onError(context.getString(R.string.something_went_wrong), requestCode);
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(networkListener != null) {
                    AppUtils.getInstance().showToast(context, context.getResources().getString(R.string.network_issue));
                    networkListener.onFailure();
                }
            }
        });
    }
}


