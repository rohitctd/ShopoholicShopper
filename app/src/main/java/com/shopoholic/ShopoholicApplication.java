package com.shopoholic;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shopoholic.database.DataBaseProvider;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.Constants;

import io.fabric.sdk.android.Fabric;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.OkHttpClient;


@ReportsCrashes(// will not be used
        mailTo = "sachin.upreti@appinventiv.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)

public class ShopoholicApplication extends Application implements LifecycleObserver {
    private static ShopoholicApplication mApplicationInstance;
    private DataBaseProvider dataBaseProvider;
    public Socket mSocket = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        ACRA.init(this);
        mApplicationInstance = this;
        dataBaseProvider = new DataBaseProvider(mApplicationInstance);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public static ShopoholicApplication getInstance() {
        return mApplicationInstance;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }




    /**
     * method to get database provider instance
     * @return
     */
    public DataBaseProvider getDataBaseProvider() {
        return dataBaseProvider;
    }

    /**
     * To get socket singleton instance.
     *
     * @return socket instance
     */
    @SuppressLint("TrustAllX509TrustManager")
    public Socket getSocket() {
        if (mSocket != null)
            return mSocket;
        else {
            try {
                SSLContext mySSLContext = SSLContext.getInstance("TLS");
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                }};
                mySSLContext.init(null, trustAllCerts, new java.security.SecureRandom());
                IO.Options options = new IO.Options();
                options.reconnection = true;
                mSocket = IO.socket(Constants.NetworkConstant.SOCKET_BASE_URL, options);
                mSocket.connect();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }

        }
        return mSocket;

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        Log.d("MyApp", "App in background");
        FirebaseDatabaseQueries.getInstance().changeUserStatus(this, false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        Log.d("MyApp", "App in foreground");
        FirebaseDatabaseQueries.getInstance().changeUserStatus(this, true);
    }
}
