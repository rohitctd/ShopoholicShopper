package com.shopoholic.network;

import android.content.Context;
import android.util.Base64;


import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * network class to communicate with the server
 */
public class RestApi {

    private static Retrofit retrofit = null;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(Constants.NetworkConstant.BASE_URL).addConverterFactory(GsonConverterFactory.create());

    /**
     * method to get the retrofit class instance
     * @return
     */
    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.NetworkConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    /**
     * method to create service
     * @param context
     * @param aClass
     * @param <S>
     * @return
     */
    public static <S> S createService(final Context context, Class<S> aClass) {
        String credentials = Constants.NetworkConstant.USER_NAME + ":" + Constants.NetworkConstant.PASSWORD;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        httpClient.connectTimeout(30,TimeUnit.SECONDS);
        httpClient.readTimeout(30,TimeUnit.SECONDS);
        httpClient.writeTimeout(30,TimeUnit.SECONDS);
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header(Constants.NetworkConstant.PARAM_AUTHORIZATION, basic)
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        Retrofit retrofit = retrofitBuilder.client(httpClient.build()).build();
        return retrofit.create(aClass);
    }

    /**
     * method to create service with access token
     * @param context
     * @param aClass
     * @param <S>
     * @return
     */
    public static <S> S createServiceAccessToken(final Context context, Class<S> aClass) {
        String credentials = Constants.NetworkConstant.USER_NAME + ":" + Constants.NetworkConstant.PASSWORD;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        httpClient.connectTimeout(30,TimeUnit.SECONDS);
        httpClient.readTimeout(30,TimeUnit.SECONDS);
        httpClient.writeTimeout(30,TimeUnit.SECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header(Constants.NetworkConstant.PARAM_AUTHORIZATION, basic)
                        .header(Constants.NetworkConstant.PARAM_ACCESS_TOKEN, AppSharedPreference.getInstance().getString(context, AppSharedPreference.PREF_KEY.ACCESS_TOKEN))
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        Retrofit retrofit = retrofitBuilder.client(httpClient.build()).build();
        return retrofit.create(aClass);
    }

    /**
     * method to get the response body
     * @param params
     * @return
     */
    public static RequestBody getRequestBody(String params){
        return RequestBody.create(MediaType.parse("multipart/form-data"),params);
    }
}
