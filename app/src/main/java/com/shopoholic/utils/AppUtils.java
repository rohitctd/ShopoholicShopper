
package com.shopoholic.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.shopoholic.R;
import com.shopoholic.activities.LoginActivity;
import com.shopoholic.animations.MyAnimation;
import com.shopoholic.calendar.CalendarBean;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.database.AppDatabase;
import com.shopoholic.dialogs.CustomDialogForMessage;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.interfaces.MessageDialogCallback;
import com.shopoholic.interfaces.PopupItemDialogCallback;
import com.shopoholic.models.countrymodel.CountryBean;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.RestApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;


/**
 * Provides convenience methods and abstractions to some tasks in Android
 * <p/>
 * <br/>
 * <br/>
 *
 * @author Jay
 */
public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();
    private static AppUtils appUtils;
    private Toast toast;
    private DatePickerDialog datePickerDialog;

    public static AppUtils getInstance() {
        if (appUtils == null) {
            appUtils = new AppUtils();
        }
        return appUtils;
    }


    /**
     * print toast message
     *
     * @param context
     * @param text
     */
    public void showToast(Context context, String text) {
        if (context != null) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    /**
     * function to get device token
     */
    public void getDeviceToken(Activity mActivity) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(mActivity, instanceIdResult -> {
            String refreshedToken = instanceIdResult.getToken();
            Log.e("newToken",refreshedToken);
            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.DEVICE_TOKEN, refreshedToken);
        });
    }

    /**
     * set image by glide
     *
     * @param context
     * @param imageUrl
     * @param imageView
     * @param radius
     * @param placeholder
     */
    public void setImages(final Context context, Object imageUrl, final ImageView imageView, final float radius, final int placeholder) {
       if (context != null) {
           Glide.with(context)
                   .load(imageUrl)
                   .asBitmap()
                   .placeholder(placeholder)
                   .error(placeholder)
                   .diskCacheStrategy(DiskCacheStrategy.ALL)
                   .centerCrop()
                   .into(new BitmapImageViewTarget(imageView) {

                       @Override
                       protected void setResource(Bitmap resource) {
                           RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                           circularBitmapDrawable.setCornerRadius(radius);
                           imageView.setImageDrawable(circularBitmapDrawable);
                       }

                       @Override
                       public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                           super.onLoadFailed(e, errorDrawable);
                           imageView.setImageResource(placeholder);
                       }
                   });
       }
    }

    /**
     * save images in cache using glide
     *
     * @param context
     * @param imageUrls
     */
    public void saveImages(final Context context, List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
        }
    }

    /**
     * set image by glide
     *
     * @param context
     * @param imageUrl
     * @param imageView
     * @param placeholder
     */
    public void setCircularImages(final Context context, Object imageUrl, final ImageView imageView, final int placeholder) {
        if (context != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .placeholder(placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(placeholder)
                    .centerCrop()
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                        }

                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imageView.setImageDrawable(circularBitmapDrawable);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                            super.onLoadFailed(e, errorDrawable);
                            imageView.setImageResource(placeholder);
                        }
                    });
        }
    }

    /**
     * Checks if the Internet connection is available.
     *
     * @return Returns true if the Internet connection is available. False otherwise.
     **/
    public boolean isInternetAvailable(Context ctx) {
        // using received context (typically activity) to get SystemService causes memory link as this holds strong reference to that activity.
        // use application level context instead, which is available until the app dies.
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        // if network is NOT available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        showToast(ctx, ctx.getResources().getString(R.string.no_internet_connection));
        return false;
    }

    /**
     * Checks if the Internet connection is available.
     *
     * @return Returns true if the Internet connection is available. False otherwise.
     **/
    public boolean internetAvailable(Context ctx) {
        // using received context (typically activity) to get SystemService causes memory link as this holds strong reference to that activity.
        // use application level context instead, which is available until the app dies.
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        // if network is NOT available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    /**
     * method to set button loader
     *
     * @param context
     * @param button
     * @param loader
     * @param isVisible
     */
    public void setButtonLoader(Context context, CustomButton button, FrameLayout loader, boolean isVisible) {
        if (button != null && loader != null) {
            if (isVisible) {
                button.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.VISIBLE);

            } else {
                button.setVisibility(View.VISIBLE);
                loader.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * method to set button loader
     *
     * @param btnAction
     * @param context
     * @param loader
     * @param view
     * @param isVisible
     */
    public void setButtonLoaderAnimation(Context context, CustomButton btnAction, View loader, View view, boolean isVisible) {
        if (btnAction != null && loader != null && view != null) {
            if (isVisible) {
//                loader.setVisibility(View.VISIBLE);
//                view.setVisibility(View.GONE);
                changeViewWithAnimation(view, loader, 100);
                float centreX = btnAction.getX() + btnAction.getWidth() / 2;
                float centreY = btnAction.getY() + btnAction.getHeight() / 2;
                Animation animation = new MyAnimation(btnAction, context.getResources().getDimension(R.dimen._45sdp), centreX, centreY);
                animation.setDuration(1500);
                animation.setRepeatCount(Animation.INFINITE);
                animation.setRepeatMode(Animation.INFINITE);
                loader.startAnimation(animation);
            } else {
//                loader.setVisibility(View.GONE);
//                view.setVisibility(View.VISIBLE);
                changeViewWithAnimation(loader, view, 100);
                loader.clearAnimation();
            }
        }
    }

    /**
     * method to set button loader
     *
     * @param imageView
     * @param context
     * @param loader
     * @param view
     * @param isVisible
     */
    public void setImageLoaderAnimation(Context context, ImageView imageView, View loader, View view, boolean isVisible) {
        if (imageView != null && loader != null && view != null) {
            if (isVisible) {
//                loader.setVisibility(View.VISIBLE);
//                view.setVisibility(View.GONE);
                changeViewWithAnimation(view, loader, 100);
                float centreX = imageView.getX() + imageView.getWidth() / 2;
                float centreY = imageView.getY() + imageView.getHeight() / 2;
                Animation animation = new MyAnimation(imageView, context.getResources().getDimension(R.dimen._42sdp), centreX, centreY);
                animation.setDuration(1500);
                animation.setRepeatCount(Animation.INFINITE);
                animation.setRepeatMode(Animation.INFINITE);
                loader.startAnimation(animation);
            } else {
//                loader.setVisibility(View.GONE);
//                view.setVisibility(View.VISIBLE);
                changeViewWithAnimation(loader, view, 100);
                loader.clearAnimation();
            }
        }
    }


    /**
     * change tab bar font
     */
    public void setCustomFont(Context mContext, TabLayout mCustomFontTab) {

        ViewGroup vg = (ViewGroup) mCustomFontTab.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.orkney_medium)));
                }
            }
        }
    }

    /**
     * Checks if the SD Card is mounted on the device.
     **/
    public boolean isSdCardMounted() {
        String status = Environment.getExternalStorageState();

        if (status != null && status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }

        return false;
    }

    /**
     * Serializes the Bitmap to Base64
     *
     * @return Base64 string value of a {@linkplain Bitmap} passed in as a parameter
     * @throws NullPointerException If the parameter bitmap is null.
     **/
    public String toBase64(Bitmap bitmap) {

        if (bitmap == null) {
            throw new NullPointerException("Bitmap cannot be null");
        }

        String base64Bitmap = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBitmap = stream.toByteArray();
        base64Bitmap = Base64.encodeToString(imageBitmap, Base64.DEFAULT);

        return base64Bitmap;
    }

    /**
     * Converts the passed in drawable to Bitmap representation
     *
     * @throws NullPointerException If the parameter drawable is null.
     **/
    public Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable == null) {
            throw new NullPointerException("Drawable to convert should NOT be null");
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        if (drawable.getIntrinsicWidth() <= 0 && drawable.getIntrinsicHeight() <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Converts the given bitmap to {@linkplain InputStream}.
     *
     * @throws NullPointerException If the parameter bitmap is null.
     **/
    public InputStream bitmapToInputStream(Bitmap bitmap) throws NullPointerException {

        if (bitmap == null) {
            throw new NullPointerException("Bitmap cannot be null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream inputstream = new ByteArrayInputStream(baos.toByteArray());

        return inputstream;
    }


    /**
     * method to replace fragment
     *
     * @param mActivity
     * @param container
     * @param fragment
     * @param TAG
     * @param shouldAddToBackStack
     */
    public void replaceFragments(AppCompatActivity mActivity, int container, Fragment fragment, String TAG, boolean shouldAddToBackStack) {

        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        ft.replace(container, fragment, TAG);
        if (shouldAddToBackStack) {
            ft.addToBackStack(TAG);
        }
        ft.commit();

    }

    // this method is used to add a fragment

    public void addFragment(AppCompatActivity mActivity, int container, Fragment fragment, String TAG, boolean shouldAddToBackStack) {

        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        ft.add(container, fragment, TAG);
        if (shouldAddToBackStack) {
            ft.addToBackStack(TAG);
        }
        ft.commit();

    }

    // this method is used to pop fragment from stack

    public void popFragment(AppCompatActivity mActivity) {
        mActivity.getSupportFragmentManager().popBackStack();
    }

    /**
     * this method is used to get the date with month name
     *
     * @param completeDate
     * @return
     */
    public String formatDate(String completeDate, String inputFormat, String outputFormat) {
        DateFormat originalFormat = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat(outputFormat);
        Date date = null;
        String formattedDate = "";
        try {
            date = originalFormat.parse(completeDate);
            formattedDate = targetFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    /**
     * Gets the version name of the application. For e.g. 1.9.3
     **/
    public String getApplicationVersionNumber(Context context) {

        String versionName = null;

        if (context == null) {
            return versionName;
        }

        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    @SuppressLint("HardwareIds")
    public String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Gets the version code of the application. For e.g. Maverick Meerkat or 2013050301
     **/
    public int getApplicationVersionCode(Context ctx) {

        int versionCode = 0;

        try {
            versionCode = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    /**
     * Gets the version number of the Android OS For e.g. 2.3.4 or 4.1.2
     **/
    public String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Checks if the service with the given name is currently running on the device.
     *
     * @param serviceName Fully qualified name of the server. <br/>
     *                    For e.g. nl.changer.myservice.name
     **/
    public boolean isServiceRunning(Context ctx, String serviceName) {

        if (serviceName == null) {
            throw new NullPointerException("Service name cannot be null");
        }

        // use application level context to avoid unnecessary leaks.
        ActivityManager manager = (ActivityManager) ctx.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals(serviceName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Shares an application over the social network like Facebook, Twitter etc.
     *
     * @param sharingMsg   Message to be pre-populated when the 3rd party app dialog opens up.
     * @param emailSubject Message that shows up as a subject while sharing through email.
     * @param title        Title of the sharing options prompt. For e.g. "Share via" or "Share using"
     **/
    public void share(Context ctx, String sharingMsg, String emailSubject, String title) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);

        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharingMsg);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

        ctx.startActivity(Intent.createChooser(sharingIntent, title));
    }

    /**
     * Checks the type of data connection that is currently available on the device.
     *
     * @return <code>ConnectivityManager.TYPE_*</code> as a type of internet connection on the
     * device. Returns -1 in case of error or none of
     * <code>ConnectivityManager.TYPE_*</code> is found.
     **/
    public int getDataConnectionType(Context ctx) {

        // use application level context to avoid unnecessary leaks.
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                return ConnectivityManager.TYPE_MOBILE;
            } else if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                return ConnectivityManager.TYPE_WIFI;
            } else
                return -1;
        } else
            return -1;
    }


    /**
     * method to generate FB key hash
     *
     * @param context
     */
    public void generateFBKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName()
                    , PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash ::::::", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the DB with the given name is present on the device.
     *
     * @param packageName
     * @param dbName
     * @return
     */
    public boolean isDatabasePresent(String packageName, String dbName) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/" + packageName + "/databases/" + dbName, null, SQLiteDatabase.OPEN_READONLY);
            sqLiteDatabase.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            e.printStackTrace();
            Log.e(TAG, "The database does not exist." + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception " + e.getMessage());
        }

        return (sqLiteDatabase != null);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * method to get real path from uri
     *
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * method to return data column
     *
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * json array to list
     *
     * @param jsonArr
     * @return
     */
    public ArrayList<String> toStringArray(JSONArray jsonArr) {

        if (jsonArr == null || jsonArr.length() == 0) {
            return null;
        }

        ArrayList<String> stringArray = new ArrayList<String>();

        for (int i = 0, count = jsonArr.length(); i < count; i++) {
            try {
                String str = jsonArr.getString(i);
                stringArray.add(str);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return stringArray;
    }


    /**
     * Convert a given list of {@link String} into a {@link JSONArray}
     **/
    public JSONArray toJSONArray(ArrayList<String> stringArr) {
        JSONArray jsonArr = new JSONArray();

        for (int i = 0; i < stringArr.size(); i++) {
            String value = stringArr.get(i);
            jsonArr.put(value);
        }

        return jsonArr;
    }

    /**
     * Gets the data storage directory(pictures dir) for the device. If the external storage is not
     * available, this returns the reserved application data storage directory. SD Card storage will
     * be preferred over internal storage.
     *
     * @param dirName if the directory name is specified, it is created inside the DIRECTORY_PICTURES
     *                directory.
     * @return Data storage directory on the device. Maybe be a directory on SD Card or internal
     * storage of the device.
     **/
    public File getStorageDirectory(Context ctx, String dirName) {

        if (TextUtils.isEmpty(dirName)) {
            dirName = "atemp";
        }

        File f = null;

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + dirName);
        } else {
            // media is removed, unmounted etc
            // Store image in
            // /data/data/<package-name>/cache/atemp/photograph.jpeg
            f = new File(ctx.getCacheDir() + "/" + dirName);
        }

        if (!f.exists()) {
            f.mkdirs();
        }

        return f;
    }


    /**
     * method to get all country list
     *
     * @return
     */
/*
    public ArrayList<CountryCodeBean> getAllCountries() {
        ArrayList<CountryCodeBean> allCountriesList = new ArrayList<>();
        try {
            String allCountriesCode = new String(Base64.decode(ConstantsCountry.ENCODED_COUNTRY_CODE, Base64.DEFAULT), "UTF-8");
            JSONArray countryArray = new JSONArray(allCountriesCode);
            for (int i = 0; i < countryArray.length(); i++) {
                JSONObject jsonObject = countryArray.getJSONObject(i);
                String countryDialCode = jsonObject.getString("dial_code");
                String countryCode = jsonObject.getString("code");
                CountryCodeBean country = new CountryCodeBean();
                country.setCode(countryCode);
                country.setDialCode(countryDialCode);
                allCountriesList.add(country);
            }
            Collections.sort(allCountriesList, new Comparator<CountryCodeBean>() {
                @Override
                public int compare(CountryCodeBean lhs, CountryCodeBean rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allCountriesList;
    }
*/


    /**
     * method to get all country list
     *
     * @return
     */
    public ArrayList<CountryBean> getAllCountries(Context mContext) {
        String json = null;
        ArrayList<CountryBean> allCountriesList = new ArrayList<>();
        try {
            InputStream is = mContext.getAssets().open("countryData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONArray countryArray = new JSONArray(json);
            for (int i = 0; i < countryArray.length(); i++) {
                JSONObject jsonObject = countryArray.getJSONObject(i);
                CountryBean countryBean = new Gson().fromJson(String.valueOf(jsonObject), CountryBean.class);
                allCountriesList.add(countryBean);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
        return allCountriesList;
    }


    /**
     * Given a file name, this method creates a {@link File} on best chosen device storage and
     * returns the file object. You can get the file path using {@link File#getAbsolutePath()}
     **/
    public File getFile(Context ctx, String fileName) {
        File dir = getStorageDirectory(ctx, null);
        File f = new File(dir, fileName);
        return f;
    }

    /**
     * @return Path of the image file that has been written.
     * @deprecated Use {@link(Context, byte[])}
     * Writes the given image to the external storage of the device. If external storage is not
     * available, the image is written to the application private directory
     **/
    public String writeImage(Context ctx, byte[] imageData) {

        final String FILE_NAME = "photograph.jpeg";
        File dir = null;
        String filePath = null;
        OutputStream imageFileOS;

        dir = getStorageDirectory(ctx, null);
        File f = new File(dir, FILE_NAME);

        try {
            imageFileOS = new FileOutputStream(f);
            imageFileOS.write(imageData);
            imageFileOS.flush();
            imageFileOS.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        filePath = f.getAbsolutePath();

        return filePath;
    }

    /**
     * Inserts an image into {@link Media} content provider of the device.
     *
     * @return The media content Uri to the newly created image, or null if the image failed to be
     * stored for any reason.
     **/
    public String writeImageToMedia(Context ctx, Bitmap image, String title, String description) {
        // TODO: move to MediaUtils
        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        return Media.insertImage(ctx.getContentResolver(), image, title, description);
    }

    /**
     * Gets the name of the application that has been defined in AndroidManifest.xml
     *
     * @throws NameNotFoundException
     **/
    public String getApplicationName(Context ctx) throws NameNotFoundException {

        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        final PackageManager packageMgr = ctx.getPackageManager();
        ApplicationInfo appInfo = null;

        try {
            appInfo = packageMgr.getApplicationInfo(ctx.getPackageName(), PackageManager.SIGNATURE_MATCH);
        } catch (final NameNotFoundException e) {
            throw new NameNotFoundException(e.getMessage());
        }

        final String applicationName = (String) (appInfo != null ? packageMgr.getApplicationLabel(appInfo) : "UNKNOWN");

        return applicationName;
    }

    /**
     * Returns the URL without the query string
     **/
    public URL getPathFromUrl(URL url) {

        if (url != null) {
            String urlStr = url.toString();
            String urlWithoutQueryString = urlStr.split("\\?")[0];
            try {
                return new URL(urlWithoutQueryString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * check that  two ranges overlap
     * @param time
     * @return
     */
    public boolean checkTime(String time, String time2) {
        try {
            String[] times = time.split(" - ");

            Date start1 = getDateFromString(times[0], Constants.AppConstant.TIME_FORMAT);
            Date end1 = getDateFromString(times[1], Constants.AppConstant.TIME_FORMAT);

            String[] times2 = time2.split(" - ");
            Date start2 = getDateFromString(times2[0], Constants.AppConstant.TIME_FORMAT);
            Date end2 = getDateFromString(times2[1], Constants.AppConstant.TIME_FORMAT);

            if (start1.after(end1)) {
                if (start2.after(end2)) {
                    if ((!start1.after(end2)) || (!end1.before(start2))) {
                        return true;
                    }
                }else {
                    if ((!start1.before(end2)) || (!end1.before(start2))) {
                        return true;
                    }
                }
            }else {
                if (start2.after(end2)) {
                    if ((!start1.after(end2)) || (!end1.after(start2))) {
                        return true;
                    }
                }else {
                    if ((!start1.before(end2)) || (!end1.after(start2))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }



    /**
     * Transforms Calendar to ISO 8601 string.
     **/
    public String fromCalendar(final Calendar calendar) {
        // TODO: move this method to DateUtils
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * method to change date fornat to milliseconds string
     * @param timeString
     * @return
     */
    public long getMillisecondsTime(String timeString) {
        long time = 0;
        String[] timeArr = timeString.split(":");
        if (timeArr.length > 0) {
            time += Long.parseLong(timeArr[0]) * 3600 * 1000;
        }
        if (timeArr.length > 1) {
            time += Long.parseLong(timeArr[1]) * 60 * 1000;
        }
        if (timeArr.length > 2) {
            time += Long.parseLong(timeArr[2]) * 1000;
        }
        return time;
    }


    /**
     * Gets current date and time formatted as ISO 8601 string.
     **/
    public String now() {
        // TODO: move this method to DateUtils
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /**
     * Transforms ISO 8601 string to Calendar.
     **/
    public Calendar toCalendar(final String iso8601string) throws ParseException {
        // TODO: move this method to DateUtils
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);
        } catch (IndexOutOfBoundsException e) {
            // throw new org.apache.http.ParseException();
            e.printStackTrace();
        }

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar;
    }


    /**
     * Gets random color integer
     **/
    public int getRandomColor() {
        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);

        return Color.argb(255, red, green, blue);
    }

    /**
     * Converts a given bitmap to byte array
     */
    public byte[] toBytes(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Resizes an image to the given width and height parameters Prefer using
     *
     * @param sourceBitmap Bitmap to be resized
     * @param newWidth     Width of resized bitmap
     * @param newHeight    Height of the resized bitmap
     */
    public Bitmap resizeImage(Bitmap sourceBitmap, int newWidth, int newHeight, boolean filter) {
        // TODO: move this method to ImageUtils
        if (sourceBitmap == null) {
            throw new NullPointerException("Bitmap to be resized cannot be null");
        }

        Bitmap resized = null;

        if (sourceBitmap.getWidth() < sourceBitmap.getHeight()) {
            // image is portrait
            resized = Bitmap.createScaledBitmap(sourceBitmap, newHeight, newWidth, true);
        } else {
            // image is landscape
            resized = Bitmap.createScaledBitmap(sourceBitmap, newWidth, newHeight, true);
        }

        resized = Bitmap.createScaledBitmap(sourceBitmap, newWidth, newHeight, true);

        return resized;
    }

    /**
     * <br/>
     * <br/>
     *
     * @param compressionFactor Powers of 2 are often faster/easier for the decoder to honor
     */
    public Bitmap compressImage(Bitmap sourceBitmap, int compressionFactor) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Config.ARGB_8888;
        opts.inSampleSize = compressionFactor;

        if (Build.VERSION.SDK_INT >= 10) {
            opts.inPreferQualityOverSpeed = true;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opts);

        return image;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * Provide the height to which the sourceImage is to be resized. This method will calculate the
     * resultant height. Use scaleDownBitmap from {@link } wherever possible
     */
    public Bitmap resizeImageByHeight(int height, Bitmap sourceImage) {
        // TODO: move this method to ImageUtils
        int widthO = 0; // original width
        int heightO = 0; // original height
        int widthNew = 0;
        int heightNew = 0;

        widthO = sourceImage.getWidth();
        heightO = sourceImage.getHeight();
        heightNew = height;

        // Maintain the aspect ratio
        // of the original banner image.
        widthNew = (heightNew * widthO) / heightO;

        return Bitmap.createScaledBitmap(sourceImage, widthNew, heightNew, true);
    }

    /**
     * Checks if the url is valid
     */
    public boolean isValidURL(String url) {
        URL urlObj;

        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            urlObj.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }

    @Nullable
    /**
     * @return Lower case string for one of above listed media type
     * @deprecated Use {@link MediaUtils#getMediaType(Uri)}
     * Get the type of the media. Audio, Video or Image.
     */
    public String getMediaType(String contentType) {
        if (isMedia(contentType)) {
            if (isVideo(contentType)) {
                return "video";
            } else if (isAudio(contentType)) {
                return "audio";
            } else if (isImage(contentType)) {
                return "image";
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @param mimeType standard MIME type of the data.
     * @deprecated {@link #(String)}
     * Identifies if the content represented by the parameter mimeType is media. Image, Audio and
     * Video is treated as media by this method. You can refer to standard MIME type here. <a
     * href="http://www.iana.org/assignments/media-types/media-types.xhtml" >Standard MIME
     * types.</a>
     */
    public boolean isMedia(String mimeType) {
        boolean isMedia = false;

        if (mimeType != null) {
            if (mimeType.startsWith("image/") || mimeType.startsWith("video/") || mimeType.startsWith("audio/")) {
                isMedia = true;
            }
        } else {
            isMedia = false;
        }

        return isMedia;
    }

    /**
     * Gets the Uri without the fragment. For e.g if the uri is
     * content://com.android.storage/data/images/48829#is_png the part after '#' is called as
     * fragment. This method strips the fragment and returns the url.
     */
    public String removeUriFragment(String url) {

        if (url == null || url.length() == 0) {
            return null;
        }

        String[] arr = url.split("#");

        if (arr.length == 2) {
            return arr[0];
        } else {
            return url;
        }
    }

    /**
     * Removes the parameters from the query from the uri
     */
    public String removeQueryParameters(Uri uri) {
        assert (uri.getAuthority() != null);
        assert (uri.getPath() != null);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(uri.getScheme());
        builder.encodedAuthority(uri.getAuthority());
        builder.encodedPath(uri.getPath());
        return builder.build().toString();
    }

    /**
     * Returns true if the mime type is a standard image mime type
     */
    public boolean isImage(String mimeType) {
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns true if the mime type is a standard audio mime type
     */
    public boolean isAudio(String mimeType) {
        if (mimeType != null) {
            if (mimeType.startsWith("audio/")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @deprecated Use {@link (String)}
     * Returns true if the mime type is a standard video mime type
     */
    public boolean isVideo(String mimeType) {
        if (mimeType != null) {
            if (mimeType.startsWith("video/")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Nullable
    /**
     * @deprecated This is a monster that will lead to OutOfMemory exception some day and the world
     * will come to an end.
     * Gets the media data from the one of the following media {@link android.content.ContentProvider} This method
     * should not be called from the main thread of the application. Calling this method may have
     * performance issues as this may allocate a huge memory array.
     * <ul>
     * <li>{@link Media}</li>
     * <li>{@link MediaStore.Audio.Media}</li>
     * <li>{@link Video.Media}</li>
     * </ul>
     *
     * @param context Context object
     * @param uri Media content uri of the image, audio or video resource
     */
    public byte[] getMediaData(Context context, Uri uri) {
        if (uri == null) {
            throw new NullPointerException("Uri cannot be null");
        }

        Cursor cursor = context.getContentResolver().query(uri, new String[]{Media.DATA}, null, null, null);
        byte[] data = null;

        try {
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(Media.DATA));

                    try {
                        File file = new File(path);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        data = readStreamToBytes(fileInputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Log.v( TAG, "#getVideoData byte.size: " + data.length );
                } // end while
            } else {
                Log.e(TAG, "#getMediaData cur is null or blank");
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return data;
    }

    /**
     * Convert {@linkplain InputStream} to byte array.
     *
     * @throws NullPointerException If input parameter {@link InputStream} is null
     **/
    public byte[] readStreamToBytes(InputStream inputStream) {

        if (inputStream == null) {
            throw new NullPointerException("InputStream is null");
        }

        byte[] bytesData = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            bytesData = buffer.toByteArray();

            // Log.d( TAG, "#readStream data: " + data );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (reader != null) {
                try {
                    reader.close();

                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }    // finally

        return bytesData;
    }

    /**
     * @param mediaUri uri to the media resource. For e.g. content://media/external/images/media/45490 or
     *                 content://media/external/video/media/45490
     * @return Size in bytes
     * @deprecated Use {@link (Context, Uri)}
     * Gets the size of the media resource pointed to by the paramter mediaUri.
     * <p/>
     * Known bug: for unknown reason, the image size for some images was found to be 0
     **/
    public long getMediaSize(Context context, Uri mediaUri) {
        Cursor cur = context.getContentResolver().query(mediaUri, new String[]{Media.SIZE}, null, null, null);
        long size = -1;

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    size = cur.getLong(cur.getColumnIndex(Media.SIZE));

                    // for unknown reason, the image size for image was found to
                    // be 0
                    // Log.v( TAG, "#getSize byte.size: " + size );

                    if (size == 0)
                        Log.w(TAG, "#getSize The media size was found to be 0. Reason: UNKNOWN");

                } // end while
            } else if (cur.getCount() == 0) {
                Log.e(TAG, "#getMediaSize cur size is 0. File may not exist");
            } else {
                Log.e(TAG, "#getMediaSize cur is null");
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return size;
    }

    /**
     * @deprecated {@link Context, Uri)}
     * Gets media file name.
     **/
    public String getMediaFileName(Context ctx, Uri mediaUri) {
        String colName = MediaColumns.DISPLAY_NAME;
        Cursor cur = ctx.getContentResolver().query(mediaUri, new String[]{colName}, null, null, null);
        String dispName = null;

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    dispName = cur.getString(cur.getColumnIndex(colName));

                    // for unknown reason, the image size for image was found to
                    // be 0
                    // Log.v( TAG, "#getMediaFileName byte.size: " + size );

                    if (TextUtils.isEmpty(colName)) {
                        Log.w(TAG, "#getMediaFileName The file name is blank or null. Reason: UNKNOWN");
                    }

                } // end while
            } else if (cur != null && cur.getCount() == 0) {
                Log.e(TAG, "#getMediaFileName File may not exist");
            } else {
                Log.e(TAG, "#getMediaFileName cur is null");
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return dispName;
    }

    @Nullable
    /**
     * @deprecated Use {@link MediaUtils#getMediaType(Uri)}
     * Gets media type from the Uri.
     */
    public String getMediaType(Uri uri) {
        if (uri == null) {
            return null;
        }

        String uriStr = uri.toString();

        if (uriStr.contains("video")) {
            return "video";
        } else if (uriStr.contains("audio")) {
            return "audio";
        } else if (uriStr.contains("image")) {
            return "image";
        } else {
            return null;
        }
    }

    /**
     * @param sourceText String to be converted to bold.
     * @deprecated Use {@link #toBold(String, String)}
     * Returns {@link SpannableString} in Bold typeface
     */
    public SpannableStringBuilder toBold(String sourceText) {

        if (sourceText == null) {
            throw new NullPointerException("String to convert cannot be bold");
        }

        final SpannableStringBuilder sb = new SpannableStringBuilder(sourceText);

        // Span to set text color to some RGB value
        final StyleSpan bss = new StyleSpan(Typeface.BOLD);

        // set text bold
        sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    /**
     * Typefaces the string as bold.
     * If sub-string is null, entire string will be typefaced as bold and returned.
     *
     * @param string
     * @param subString The subString within the string to bold. Pass null to bold entire string.
     * @return {@link SpannableString}
     */
    public SpannableStringBuilder toBold(String string, String subString) {
        if (TextUtils.isEmpty(string)) {
            return new SpannableStringBuilder("");
        }

        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(string);

        StyleSpan bss = new StyleSpan(Typeface.BOLD);
        if (subString != null) {
            int substringNameStart = string.toLowerCase().indexOf(subString);
            if (substringNameStart > -1) {
                spannableBuilder.setSpan(bss, substringNameStart, substringNameStart + subString.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            // set entire text to bold
            spannableBuilder.setSpan(bss, 0, spannableBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return spannableBuilder;
    }

    /**
     * Formats given size in bytes to KB, MB, GB or whatever. This will work up to 1000 TB
     */
    public String formatSize(long size) {

        if (size <= 0) return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * Formats given size in bytes to KB, MB, GB or whatever. Preferably use this method for
     * performance efficiency.
     *
     * @param si Controls byte value precision. If true, formatting is done using approx. 1000 Uses
     *           1024 if false.
     **/
    public String formatSize(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;

        if (bytes < unit) {
            return bytes + " B";
        }

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Creates the uri to a file located on external storage or application internal storage.
     */
    public Uri createUri(Context ctx) {
        File root = getStorageDirectory(ctx, null);
        root.mkdirs();
        File file = new File(root, Long.toString(new Date().getTime()));
        Uri uri = Uri.fromFile(file);

        return uri;
    }

    /**
     * @param ctx
     * @param savingUri
     * @param durationInSeconds
     * @return Creates an intent to take a video from camera or gallery or any other application that can
     * handle the intent.
     */
    public Intent createTakeVideoIntent(Activity ctx, Uri savingUri, int durationInSeconds) {

        if (savingUri == null) {
            throw new NullPointerException("Uri cannot be null");
        }

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        final PackageManager packageManager = ctx.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, savingUri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, durationInSeconds);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("video/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        return chooserIntent;
    }

    /**
     * @param savingUri Uri to store a high resolution image at. If the user takes the picture using the
     *                  camera the image will be stored at this uri.
     *                  Creates a ACTION_IMAGE_CAPTURE photo & ACTION_GET_CONTENT intent. This intent will be
     *                  aggregation of intents required to take picture from Gallery and Camera at the minimum. The
     *                  intent will also be directed towards the apps that are capable of sourcing the image data.
     *                  For e.g. Dropbox, Astro file manager.
     **/
    public Intent createTakePictureIntent(Activity ctx, Uri savingUri) {

        if (savingUri == null) {
            throw new NullPointerException("Uri cannot be null");
        }

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = ctx.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, savingUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        return chooserIntent;
    }

    @Nullable
    /**
     * @deprecated Use {@link MediaUtils#createImageUri(Context)}
     * Creates external content:// scheme uri to save the images at. The image saved at this
     * {@link Uri} will be visible via the gallery application on the device.
     */
    public Uri createImageUri(Context ctx) throws IOException {

        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        Uri imageUri = null;

        ContentValues values = new ContentValues();
        values.put(MediaColumns.TITLE, "");
        values.put(ImageColumns.DESCRIPTION, "");
        imageUri = ctx.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);

        return imageUri;
    }

    @Nullable
    /**
     * @deprecated Use {@link MediaUtils#createVideoUri(Context)}
     * Creates external content:// scheme uri to save the videos at.
     */
    public Uri createVideoUri(Context ctx) throws IOException {

        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        Uri imageUri;

        ContentValues values = new ContentValues();
        values.put(MediaColumns.TITLE, "");
        values.put(ImageColumns.DESCRIPTION, "");
        imageUri = ctx.getContentResolver().insert(Video.Media.EXTERNAL_CONTENT_URI, values);

        return imageUri;
    }

    @Nullable
    /**
     *
     * @deprecated Use {#setTextValues} or {#getNullEmptyCheckedValue}
     * Get the correctly appended name from the given name parameters
     *
     * @param firstName
     *            First name
     * @param lastName
     *            Last name
     *
     * @return Returns correctly formatted full name. Returns null if both the values are null.
     **/
    public String getName(String firstName, String lastName) {
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {
            return firstName + " " + lastName;
        } else if (!TextUtils.isEmpty(firstName)) {
            return firstName;
        } else if (!TextUtils.isEmpty(lastName)) {
            return lastName;
        } else {
            return null;
        }
    }

    public Bitmap roundBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    /**
     * Checks if given url is a relative path.
     *
     * @param url
     * @return false if parameter url is null or false
     */
    public final boolean isRelativeUrl(String url) {

        if (TextUtils.isEmpty(url)) {
            return false;
        }

        Uri uri = Uri.parse(url);

        return uri.getScheme() == null;
    }

    /**
     * Checks if the parameter {@link Uri} is a content uri.
     **/
    public boolean isContentUri(Uri uri) {
        if (!uri.toString().contains("content://")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Hides the already popped up keyboard from the screen.
     *
     * @param context
     */
    public void showKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        } catch (Exception e) {
            Log.e(TAG, "Sigh, cant even open keyboard " + e.getMessage());
        }
    }


    /**
     * Hides the already popped up keyboard from the screen.
     *
     * @param context
     */
    public void hideKeyboard(Context context) {
        try {
            // use application level context to avoid unnecessary leaks.
            InputMethodManager inputManager = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.e(TAG, "Sigh, cant even hide keyboard " + e.getMessage());
        }
    }

    /**
     * Hides the already popped up keyboard from the screen.
     *
     * @param context
     */
    public void hideKeyboardOfEditText(Context context, EditText editText) {
        try {
            // use application level context to avoid unnecessary leaks.
            InputMethodManager inputManager = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.e(TAG, "Sigh, cant even hide keyboard " + e.getMessage());
        }
    }


    @Nullable
    /**
     * Partially capitalizes the string from paramter start and offset.
     *
     * @param string String to be formatted
     * @param start  Starting position of the substring to be capitalized
     * @param offset Offset of characters to be capitalized
     * @return
     */
    public String capitalizeString(String string, int start, int offset) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        String formattedString = string.substring(start, offset).toUpperCase() + string.substring(offset, string.length());
        return formattedString;
    }

    @Nullable
    /**
     * Generates SHA-512 hash for given binary data.
     * @param stringToHash
     * @return
     */
    public String getSha512Hash(String stringToHash) {
        if (stringToHash == null) {
            return null;
        } else {
            return getSha512Hash(stringToHash.getBytes());
        }
    }

    @Nullable
    /**
     * Generates SHA-512 hash for given binary data.
     */
    public String getSha512Hash(byte[] dataToHash) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (md != null) {
            md.update(dataToHash);
            byte byteData[] = md.digest();
            String base64 = Base64.encodeToString(byteData, Base64.DEFAULT);

            return base64;
        }
        return null;
    }

    @Nullable
    /**
     * Gets the extension of a file.
     */
    public String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }

        return ext;
    }


    /*
     *
     *  method to decrypt encrypted server response
     **/
    public String decryptMsg(byte[] cipherText, SecretKey secretKey) throws Exception {
        if (secretKey != null) {
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte iv[] = Constants.initVector.getBytes();
            //  new SecureRandom().nextBytes(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] ciphertextBytes = Base64.decode(cipherText, Base64.NO_WRAP);
            // String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
            byte[] original = cipher.doFinal(ciphertextBytes);

            return new String(original, "UTF-8");
        }
        return "";
    }


    /**
     * this method converts hashmap to json and encrypt it and
     * put back it to  hashmap
     *
     * @param params
     * @return
     */
    public HashMap<String, String> encryptData(HashMap<String, String> params) {
        if (!Constants.SecretKey.equals("")) {
            String json = new Gson().toJson(params);
            HashMap<String, String> param = new HashMap<>();
            try {
                String enc = encryptMsg(json, generateKey(Constants.SecretKey));
                param.put("packet", enc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return param;
        } else
            return params;


    }


    /*  *  * method to generate key to encrypt/decrypt server response  *  * */
    public SecretKey generateKey(String key) throws Exception {
        return new SecretKeySpec(key.getBytes(), "AES");
    }



    /**
     * method to get product background color
     * @param context
     * @param position
     * @return
     */
    public int getProductBackgroundColor(Context context, int position) {
        switch (position) {
            case 0:
                return context.getResources().getColor(R.color.colorBluePost);
            case 1:
                return context.getResources().getColor(R.color.colorPinkPost);
            case 2:
                return context.getResources().getColor(R.color.colorPurplePost);
            case 3:
                return context.getResources().getColor(R.color.colorLightBluePost);
            case 4:
                return context.getResources().getColor(R.color.colorLightPurplePost);
            case 5:
                return context.getResources().getColor(R.color.colorLightPinkPost);
            default:
                return context.getResources().getColor(R.color.colorLightPinkPost);
        }

    }

    /*
      *  *
      *  * method to encrypt server request
      *
      *  * */
    public String encryptMsg(String message, SecretKey secretKey) throws Exception {
        if (secretKey != null) {
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte iv[] = Constants.initVector.getBytes();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
            return Base64.encodeToString(cipherText, Base64.NO_WRAP);
        }
        return "";
    }

    /**
     * check phone validate
     * @param phoneNumber
     * @return
     */
    public boolean isValidPhoneNumber(String countryCode, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            if (Patterns.PHONE.matcher(phoneNumber).matches()){
                return validateUsingLibphonenumber(countryCode, phoneNumber);
            }
        }
        return false;
    }

    /**
     * check phone no validate with country
     * @param countryCode
     * @param phNumber
     * @return
     */
    private boolean validateUsingLibphonenumber(String countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return phoneNumber == null ? false : phoneNumberUtil.isValidNumber(phoneNumber);
    }


    /**
     * this method converts hashmap to json and encrypt it and
     * put back it to  hashmap
     *
     * @param params
     * @return
     */
    public HashMap<String, RequestBody> encryptMultiPartData(HashMap<String, String> params) {
        String json = new Gson().toJson(params);
        HashMap<String, RequestBody> param = new HashMap<>();
        try {
            String enc = encryptMsg(json, generateKey(Constants.SecretKey));
            param.put("packet", getRequestBody(enc));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }


    public RequestBody getRequestBody(String params) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), params);
    }

    /**
     * method to check if user login or not
     */
    public boolean isLoggedIn(final AppCompatActivity mActivity) {
        if (!AppSharedPreference.getInstance().getBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_SIGN_UP)){
//            showToast(mContext, mContext.getString(R.string.login_required));

            new CustomDialogForMessage(mActivity, mActivity.getString(R.string.authentication_required), mActivity.getString(R.string.login_required),
                    mActivity.getString(R.string.login), true, new MessageDialogCallback() {
                @Override
                public void onSubmitClick() {
                    Intent loginIntent = new Intent(mActivity, LoginActivity.class);
                    openNewActivity(mActivity, loginIntent);
//                    if (!mActivity.isFinishing() || !mActivity.isDestroyed()) mActivity.finish();
                }
            }).show();

            return false;
        }
        return true;
    }

    /**
     * method to change milliseconds to timer
     *
     * @param milliseconds
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public String getTimeFromMilliseconds(long milliseconds, String timeFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
            return dateFormat.format(calendar.getTime());
        } catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }



    /**
     * method to get diffrence in millis
     * @param slotStartTime
     * @param slotEndTime
     * @return
     */
    public long getMillisecondsDifference(String slotStartTime, String slotEndTime) {
        Date startDate = getDateFromString(slotStartTime, Constants.AppConstant.TIME_FORMAT);
        Date endDate = getDateFromString(slotEndTime, Constants.AppConstant.TIME_FORMAT);
        long difference = endDate.getTime() - startDate.getTime();
        return difference < 0 ? (24 * 60 * 60 * 1000) + difference : difference;
    }


    /**
     * method to get the date from Calender
     *
     */
    public String getTime() {
        String dateFormat;
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.AppConstant.TIME_FORMAT, Locale.ENGLISH);
        dateFormat = sdf.format(Calendar.getInstance().getTime());
        return dateFormat;
    }

    /**
     * method to change milliseconds to timer
     *
     * @param milliseconds
     * @return
     */
    public String getTimeFromMillis(long milliseconds) {
        String finalTimerString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + (hours == 1 ? " hour " : " hours ");
        }

        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        if (minutes > 0)
            finalTimerString = finalTimerString + minutesString + (minutes == 1 ? " minute " : " minutes ");

        // return timer string
        return finalTimerString;
    }




    /**
     * method to check if user phone no register or not
     */
    public boolean isPhoneValidate(Context mContext) {
        if (AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.PHONE_NO).equals("")) {
            showToast(mContext, mContext.getString(R.string.phone_no_required));
            return false;
        }
        if (AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.IS_PHONE_VALIDATE).equals("0")) {
            showToast(mContext, mContext.getString(R.string.validate_phone_no));
            return false;
        }
        return true;
    }

    /**
     * method to clear all activity and open this class
     *
     * @param intent
     * @param mActivity
     */
    public void openNewActivity(AppCompatActivity mActivity, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    /**
     * method to check soft keyboard is visible or not
     *
     * @param context
     */
    public boolean isKeyboardVisible(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm != null && imm.isAcceptingText();
    }


    /**
     * this method is used to set the transparent status bar
     *
     * @param isTransparent
     */
    public void setStatusBarTransparent(Activity activity, boolean isTransparent) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isTransparent) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View decor = window.getDecorView();
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                } else
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }

    /**
     * method to get hidden phone no with last 3 digits
     *
     * @param phone
     * @return
     */
    public String getHiddenPhoneNo(String phone) {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < phone.length(); i++) {
            if (i >= phone.length() - 3)
                number.append(phone.charAt(i));
            else
                number.append("X");
        }
        return String.valueOf(number);
    }

    /**
     * method for bounce animation on a view
     *
     * @param view
     */
    public void bounceAnimation(AppCompatActivity mActivity, View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(mActivity, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator();
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
    }


    /**
     * Method to open date picker and set date on view
     * @param dateString
     * @param context
     * @param textView
     * @param isDobDate
     * @param tvOther
     */
    @SuppressLint("SimpleDateFormat")
    public void openDatePickerAndSetDate(final Context context, final TextView textView, final boolean isDobDate, final TextView tvOther, String dateString) {
        final Calendar myCalendar = Calendar.getInstance();
        Calendar startDate = null;
        if (tvOther.getId() == R.id.tv_dob) {
            startDate = tvOther.getText().toString().trim().length() == 0 ? null :
                    AppUtils.getInstance().getCallenderObject(tvOther.getText().toString().trim());
        }
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (isDobDate){
                    tvOther.setText("");
                }
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT, Locale.US);
                textView.setText(sdf.format(myCalendar.getTime()));
            }
        };
        long timeInMilliseconds = 0;
        try{
            timeInMilliseconds =  new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT).parse(dateString).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (timeInMilliseconds != 0){
            myCalendar.setTimeInMillis(timeInMilliseconds);
        }
        DatePickerDialog pickerDialog = new DatePickerDialog(context, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        Calendar dateLimit = Calendar.getInstance();
        if (isDobDate) {
            dateLimit.add(Calendar.YEAR, -18);
            pickerDialog.getDatePicker().setMaxDate(dateLimit.getTimeInMillis());
        }
        else
        if (startDate != null){
            dateLimit.setTimeInMillis(startDate.getTimeInMillis());
            dateLimit.add(Calendar.YEAR, +10);
            pickerDialog.getDatePicker().setMinDate(dateLimit.getTimeInMillis());
        }
        pickerDialog.show();
    }


    /**
     * Method to open date picker between two date and set date on view
     *  @param context
     * @param textView
     * @param startDate
     * @param endDate
     * @param dateString
     * @param type
     */
    @SuppressLint("SimpleDateFormat")
    public void openHuntDatePicker(final Context context, final TextView textView, Calendar startDate, Calendar endDate, String dateString, int type) {
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT, Locale.ENGLISH);
            textView.setText(sdf.format(myCalendar.getTime()));
        };
        long timeInMilliseconds = 0;
        try{
            timeInMilliseconds =  new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT, Locale.ENGLISH).parse(dateString).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (timeInMilliseconds != 0){
            myCalendar.setTimeInMillis(timeInMilliseconds + 1000);
        }else {
            try {
                myCalendar.setTimeInMillis(new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT, Locale.ENGLISH).parse(getCurrentDate()).getTime() + 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        DatePickerDialog pickerDialog = new DatePickerDialog(context, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        if (type == 2) {
            if (endDate != null)
                pickerDialog.getDatePicker().setMaxDate(endDate.getTimeInMillis() + 5000);
            if (startDate != null && startDate.getTimeInMillis()>Calendar.getInstance().getTimeInMillis())
                pickerDialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
            else {
                try {
                    pickerDialog.getDatePicker().setMinDate(new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT).parse(getCurrentDate()).getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            if (endDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
                pickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
            }
            if (startDate != null)
                pickerDialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
            else {
                try {
                    pickerDialog.getDatePicker().setMinDate(new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT, Locale.ENGLISH).parse(getCurrentDate()).getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        pickerDialog.show();
    }

    /**
     * Method to open date picker between two date and set date on view
     *
     * @param context
     * @param textView
     * @param startDate
     * @param endDate
     */
    @SuppressLint("SimpleDateFormat")
    public void openDatePicker(final Context context, final TextView textView, Calendar startDate, Calendar endDate, String dateString) {
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT, Locale.US);
            textView.setText(sdf.format(myCalendar.getTime()));
        };
        long timeInMilliseconds = 0;
        try{
            timeInMilliseconds =  new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT).parse(dateString).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (timeInMilliseconds != 0){
            myCalendar.setTimeInMillis(timeInMilliseconds);
        }
        DatePickerDialog pickerDialog = new DatePickerDialog(context, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        if (startDate != null) pickerDialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
        if (endDate != null) pickerDialog.getDatePicker().setMaxDate(endDate.getTimeInMillis());
        pickerDialog.show();
    }

    /**
     * method to change view separator color on text change
     *
     * @param view
     * @param separator
     */
    public void changeSeparatorViewColor(View view, View separator) {
        int length = (view instanceof EditText ? (EditText) view : (TextView) view).getText().toString().length();
        if (length > 0) {
            separator.setBackgroundResource(R.color.colorSeparatorFilled);
        } else {
            separator.setBackgroundResource(R.color.colorSeparator);
        }
    }


    /**
     * method to change text with animation
     *
     * @param viewText
     * @param firstText
     * @param secondText
     * @param isChecked
     */
    public void changeTextWithAnimation(final View viewText, final String firstText, final String secondText, final boolean isChecked) {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(400);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (isChecked)
                    (viewText instanceof EditText ? (EditText) viewText : viewText instanceof Button ? (Button) viewText : (TextView) viewText).setText(secondText);
                else
                    (viewText instanceof EditText ? (EditText) viewText : viewText instanceof Button ? (Button) viewText : (TextView) viewText).setText(firstText);
            }
        });

        viewText.startAnimation(anim);
    }


    /**
     * method to change text with animation
     *
     * @param imageView
     * @param firstImg
     * @param secondImg
     * @param isChecked
     */
    public void changeImageWithAnimation(final ImageView imageView, final int firstImg, final int secondImg, final boolean isChecked) {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(400);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (isChecked)
                    imageView.setImageResource(secondImg);
                else
                    imageView.setImageResource(firstImg);
            }
        });

        imageView.startAnimation(anim);
    }


    /**
     * method to change view with animation
     *
     * @param firstView
     * @param secondView
     */
    public void changeViewWithAnimation(final View firstView, final View secondView, long durationMillis) {

        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(durationMillis);

        AlphaAnimation animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation2.setDuration(durationMillis);

        firstView.startAnimation(animation2);
        secondView.startAnimation(animation1);
        firstView.setVisibility(View.INVISIBLE);
        secondView.setVisibility(View.VISIBLE);
    }


    /**
     * method to show hode view with animation
     *
     * @param isShown
     * @param view
     */
    public void showHideViewWithAnimation(final View view, boolean isShown) {

        if (isShown) {
            // Prepare the View for the animation
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0.0f);

            // Start the animation
            view.animate()
                    .translationY(view.getHeight())
                    .alpha(1.0f)
                    .setListener(null);
        }else {
            view.animate()
                    .translationY(0)
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.GONE);
                        }
                    });
        }
    }

    /**
     * method to show the message log
     *
     * @param TAG
     * @param message
     */
    public void printLogMessage(String TAG, String message) {
        Log.i(TAG, message);
    }


    /**
     * method to change milliseconds to timer
     *
     * @param milliseconds
     * @return
     */
    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * method to animate marker on map on position change
     *
     * @param marker
     * @param location
     */
    public void animateMarker(final Marker marker, final Location location) {
        if (marker != null && location != null) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final LatLng startLatLng = marker.getPosition();
            final double startRotation = marker.getRotation();
            final long duration = 500;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);

                    double lng = t * location.getLongitude() + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * location.getLatitude() + (1 - t)
                            * startLatLng.latitude;

                    float rotation = (float) (t * location.getBearing() + (1 - t)
                            * startRotation);

                    marker.setPosition(new LatLng(lat, lng));
                    marker.setRotation(rotation);

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }
    }

    /**
     * updating edit text
     *
     * @param editText
     */
    public void setEditTextUpdates(EditText editText) {
        String result = editText.getText().toString().replaceAll(" ", "");
        if (!editText.getText().toString().equals(result)) {
            editText.setText(result);
            editText.setSelection(result.length());
            // alert the user
        }
    }

    /**
     * method to get product random color
     *
     * @param mContext
     * @param position
     * @return
     */
    public int getProductRandomColor(Context mContext, int position) {
        switch (position % 4) {
            case 0:
                return ContextCompat.getColor(mContext, R.color.colorMessageTitle);
            case 1:
                return ContextCompat.getColor(mContext, R.color.colorAccent);
            case 2:
                return ContextCompat.getColor(mContext, R.color.colorToolbarStart);
            default:
                return ContextCompat.getColor(mContext, R.color.colorToolbarEnd);
        }
    }

    /**
     * method to filter name
     */
    public void filterName(EditText editText) {
        editText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end, Spanned spanned, int dStart, int dEnd) {
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("^[a-z](?:[a-z0-9]+)*$")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });
    }

    /**
     * method to check that user is login or not
     */
    public boolean isUserLogin(Context mContext) {
        if (AppSharedPreference.getInstance().getBoolean(mContext, AppSharedPreference.PREF_KEY.IS_SIGN_UP)) {
            return true;
        } else {
            return false;
        }
    }

    public void logoutUser(AppCompatActivity mActivity) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).child(AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID))
                .child(FirebaseConstants.DEVICE_TOKEN).setValue("");
        FirebaseDatabaseQueries.getInstance().setUserStatus(mActivity, 4);
        String language = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE);
        String languageCode = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE_CODE);
        boolean isHomeSeen = AppSharedPreference.getInstance().getBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_HOME_SEEN);
        AppSharedPreference.getInstance().clearAllPreferences(mActivity);
        AppDatabase.clearDatabase();
        AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_TUTORIAL_SEEN, true);
        AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE, language);
        AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE_CODE, languageCode);
        AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_HOME_SEEN, isHomeSeen);
        removeNotificationsPopups(mActivity);
        openNewActivity(mActivity, new Intent(mActivity, LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
    }


    /**
     * method to remove notification popups
     */
    public void removeNotificationsPopups(Context mContext) {
        try {
            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.cancelAll();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mNotificationManager.deleteNotificationChannelGroup(Constants.AppConstant.NOTIFICATION_CHANNEL_GROUP);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            ShortcutBadger.removeCount(mContext);
        }
    }

    /**
     * Method to show options for more item
     */
    public void showMorePopUp(Context mContext, View view, String itemOne, String itemTwo, String itemThree, int type, final PopupItemDialogCallback popupItemDialogCallback) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = null;
        if (inflater != null) {
            popupView = inflater.inflate(R.layout.layout_pop_up, null);
        }

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        if (popupView != null) {
            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return true;
                }
            });

            TextView tvItemOne = popupView.findViewById(R.id.tv_item_one);
            TextView tvItemTwo = popupView.findViewById(R.id.tv_item_two);
            TextView tvItemThree = popupView.findViewById(R.id.tv_item_three);

            tvItemOne.setVisibility(itemOne.equals("") ? View.GONE : View.VISIBLE);
            tvItemTwo.setVisibility(itemTwo.equals("") ? View.GONE : View.VISIBLE);
            tvItemThree.setVisibility(itemThree.equals("") ? View.GONE : View.VISIBLE);

            tvItemOne.setText(itemOne);
            tvItemTwo.setText(itemTwo);
            tvItemThree.setText(itemThree);

            tvItemOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                    popupItemDialogCallback.onItemOneClick();
                }
            });
            tvItemTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                    popupItemDialogCallback.onItemTwoClick();
                }
            });
            tvItemThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                    popupItemDialogCallback.onItemThreeClick();
                }
            });


            int location[] = new int[2];
            // Get the View's(the one that was clicked in the Fragment) location
            view.getLocationOnScreen(location);
            float margingX = 0;
            float margingY = 0;

            switch (type) {
                case 1:
                    margingX = mContext.getResources().getDimension(R.dimen._50sdp);
                    margingY = mContext.getResources().getDimension(R.dimen._35sdp);
                    break;
                case 2:
                    margingX = mContext.getResources().getDimension(R.dimen._50sdp);
                    margingY = mContext.getResources().getDimension(R.dimen._35sdp);
                    tvItemOne.setBackgroundResource(android.R.color.white);
                    tvItemOne.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                    break;
                case 3:
                    margingX = mContext.getResources().getDimension(R.dimen._50sdp);
                    margingY = mContext.getResources().getDimension(R.dimen._1sdp);
                    break;
            }
            int locationX = (int) (location[0] - margingX);
            int locationY = (int) (location[1] + margingY);
//            int locationY = (int) (location[1]);
//            int locationX = (int) (location[0]);
            // Using location, the PopupWindow will be displayed right under anchorView
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, locationX, locationY);
            // show the popup window
            popupWindow.showAsDropDown(view);

        }
    }



    /**
     * method to get the calender instance from date
     *
     * @param date
     */
    public Calendar getCallenderObject(String date) {
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT, Locale.ENGLISH);
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;

    }

    /**
     * method to check that app is in background or not
     * @param context
     * @return
     */
    public boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = null;
            if (am != null) {
                runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = null;
            if (am != null) {
                taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }
        }

        return isInBackground;
    }



    /**
     * method to get the user country code
     * @param context
     * @return
     */
    public String getUserCountryCode(Context context) {
        String locale = "";
        String countryCode = "";
        String countryListJsonData = null;
        try {
            InputStream is = context.getAssets().open("countryData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            countryListJsonData = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //todo.... change when it updated
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            locale = context.getResources().getConfiguration().getLocales().get(0).getCountry();
//        } else {
//            locale = context.getResources().getConfiguration().locale.getCountry();
//        }
//        countryCode = checkCountryCodeFromIsoCode(countryListJsonData, locale);

        if (countryCode.equals("")) {
            locale = getUserCountry(context);
            countryCode = checkCountryCodeFromIsoCode(countryListJsonData, locale);
        }
        return countryCode;
    }



    /**
     * check country code from isocode
     *
     * @param countryListJsonData
     * @param locale
     * @return
     */
    public String checkCountryCodeFromIsoCode(String countryListJsonData, String locale) {
        String countryCode = "";
        if (countryListJsonData != null && locale != null) {
            try {
                JSONArray jsonArray = new JSONArray(countryListJsonData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject dataCountry = jsonArray.getJSONObject(i);
                    if (dataCountry.getString("ISOCode").equalsIgnoreCase(locale)) {
                        countryCode = "+" + String.valueOf(dataCountry.getInt("CountryCode"));
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return countryCode;
    }

    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    private String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry;
            if (tm != null) {
                simCountry = tm.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                    return simCountry.toLowerCase(Locale.US);
                } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                    String networkCountry = tm.getNetworkCountryIso();
                    if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                        return networkCountry.toLowerCase(Locale.US);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * method to get the time slots
     * @return
     */
    public String getTimeSlots(String slotStartTime, String slotEndTime) {
        return TextUtils.concat(formatDate(slotStartTime, "HH:mm:ss", "hh:mm a")
                + " " + "-" + " " + formatDate(slotEndTime, "HH:mm:ss", "hh:mm a")).toString();
    }

    /**
     * get country code from latlng
     * @param latitude
     * @param longitude
     * @return
     */
    public String getUserCountryCodeFromLatlng(Context context, double latitude, double longitude) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            String address = "";
            geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses != null && addresses.size() > 0) {
                address =  addresses.get(0).getCountryCode();
            }
            /*if (address.equals("")) {
                addresses = getAddress(context, latitude, longitude);
                if (addresses != null && addresses.size() > 0) {
                    address = addresses.get(0).getCountryCode();
                }
            }*/
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * get address from url
     *
     * @param mContext
     * @param latitude
     * @param longitude
     * @return
     */
    public List<Address> getAddress(Context mContext, double latitude, double longitude) {
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

    /**
     * session expired popup
     * @param responseCode
     * @param context
     */
    public void showSessionPopup(int responseCode, final AppCompatActivity context) {
        if (!context.isDestroyed() || !context.isFinishing()) {
            String message = responseCode == 401 ? context.getString(R.string.session_expire_message) :
                    (responseCode == 101 ? context.getString(R.string.blocked_by_admin) :
                            (responseCode == 103 ? context.getString(R.string.deleted_by_admin) : ""));

            new AlertDialog.Builder(context, R.style.DatePickerTheme).setTitle(context.getString(R.string.oops))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
                        AppUtils.getInstance().logoutUser(context);
                        context.finish();
                    })
                    .show();
        }
    }


    /**
     * method to change date fornat to milliseconds string
     * @param dateString
     * @param dateFormat
     * @return
     */
    public String getMilliseconds(String dateString, String dateFormat) {
        try {
            DateFormat formatter = new SimpleDateFormat(dateFormat);
            Date date = formatter.parse(dateString);
            return String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * method to get currency
     *
     * @return
     */
    public ArrayList<String> getCurrency(final Context mContext, double latitude, double longitude) {
        ArrayList<String> currencyArray = new ArrayList<>();
        currencyArray.add("");
        currencyArray.add("");
        List<Address> addresses;
        try {
            Geocoder geocoder;
            geocoder = new Geocoder(mContext, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String countryCode = addresses.get(0).getCountryCode();
                ArrayList<CountryBean> countries = getAllCountries(mContext);
                for (CountryBean country : countries) {
                    if (country.getISOCode().equals(countryCode)) {
                        currencyArray.set(0, country.getCurrency());
                        currencyArray.set(1, country.getCurrencyCode());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*if (currencyArray.size() > 0 && currencyArray.get(0).equals("")){
            new AlertDialog.Builder(mContext, R.style.DatePickerTheme)
                    .setMessage(mContext.getString(R.string.unable_to_get_currency))
                    .setCancelable(true)
                    .setPositiveButton(mContext.getString(R.string.ok), (dialog, which) -> {
                        //
                    })
                    .show();
        }*/
        return currencyArray;
    }



    /**
     * method to get User Country Code
     * @param latitude
     * @param longitude
     * @param mContext
     * @return
     */
    public String getCurrentCountryCode(Context mContext, double latitude, double longitude) {
        String countryCode = "";
        try {
            if (latitude != 0.0 && longitude != 0.0) {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(mContext, Locale.getDefault());
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    String isoCode = addresses.get(0).getCountryCode();
                    ArrayList<CountryBean> countries = getAllCountries(mContext);
                    for (CountryBean country : countries){
                        if (country.getISOCode().equals(isoCode)){
                            countryCode = country.getCountryCode();
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (countryCode.equals("")) {
            countryCode = getUserCountryCode(mContext);
        }

        if (countryCode.equals("")) {
            countryCode = AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.COUNTRY_CODE);
        }

        return countryCode;
    }


    /**
     * method to check that user is login or not
     */
    public HashMap<String, String> getUserMap(Context mContext) {
        HashMap<String, String> params = new HashMap<>();
        String language = AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE_CODE);
        int onlineStatus = AppSharedPreference.getInstance().getInt(mContext, AppSharedPreference.PREF_KEY.USER_ONLINE_STATUS);
        params.put(Constants.NetworkConstant.PARAM_FIRST_NAME, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.FIRST_NAME));
        params.put(Constants.NetworkConstant.PARAM_LAST_NAME, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.LAST_NAME));
        params.put(Constants.NetworkConstant.PARAM_IMAGE, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_IMAGE));
        params.put(Constants.NetworkConstant.PARAM_ADDRESS, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.ADDRESS));
        params.put(Constants.NetworkConstant.PARAM_ADDRESS2, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.ADDRESS2));
        params.put(Constants.NetworkConstant.PARAM_GENDER, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.GENDER));
        params.put(Constants.NetworkConstant.PARAM_DOB, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.DOB));
        params.put(Constants.NetworkConstant.PARAM_ANNIVERSARY, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.ANNVERSARY));
        params.put(Constants.NetworkConstant.PARAM_LATITUDE, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.LATITUDE));
        params.put(Constants.NetworkConstant.PARAM_LONGITUDE, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.LONGITUDE));
        params.put(Constants.NetworkConstant.PARAM_NOTIFICATION_STATUS, AppSharedPreference.getInstance().getBoolean(mContext, AppSharedPreference.PREF_KEY.IS_NOTIFICATION_ON) ? "1" : "2");
        params.put(Constants.NetworkConstant.PARAM_LOCATION_STATUS, AppSharedPreference.getInstance().getBoolean(mContext, AppSharedPreference.PREF_KEY.IS_LOCATION_ON) ? "1" : "2");
        params.put(Constants.NetworkConstant.PARAM_USER_LANGUAGE, language.equals("") ? "1" : language);
        params.put(Constants.NetworkConstant.PARAM_IS_AVAILABLE, String.valueOf(onlineStatus == 0 ? 1 : onlineStatus));

        return params;
    }

    /**
     * method to get the date from Calender
     *
     */
    public String getCurrentDate() {
        String dateFormat;
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.AppConstant.DATE_FORMAT, Locale.ENGLISH);
        dateFormat = sdf.format(Calendar.getInstance().getTime());
        return dateFormat;
    }

    /**
     * method to get date
     * @param timeInMillis .
     * @param dateFormat .
     * @return .
     */
    public String getDate(long timeInMillis, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return formatter.format(calendar.getTime());
    }

    /**
     * method to get date
     * @param completeDate .
     * @param inputFormat .
     * @param outputFormat .
     * @return .
     */
    @SuppressLint("SimpleDateFormat")
    public String utcToLocal(String completeDate, String inputFormat, String outputFormat) {
        try {
            SimpleDateFormat inFormat = new SimpleDateFormat(inputFormat, Locale.US);
            inFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            SimpleDateFormat outFormat = new SimpleDateFormat(outputFormat);
            Date date = inFormat.parse(completeDate);
            return outFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * get date list b/w two dates
     * @param dateString1
     * @param dateString2
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public List<String> getDates(String dateString1, String dateString2) {
        ArrayList<String> dates = new ArrayList<>();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(dateString1);
            date2 = df1 .parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2)) {
            dates.add(df1.format(cal1.getTime()));
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    /**
     * check start time must be before end time
     * @param currentDate
     * @param endDate
     * @param startDate
     * @return
     */
    public boolean checkDateInRange(String startDate, String endDate, String currentDate) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(SERVICE_DATE_FORMAT);
        try {
            Date stTime = sdf.parse(startDate);
            Date edTime = sdf.parse(endDate);
            Date crTime = sdf.parse(currentDate);
            if (crTime.before(stTime) || crTime.after(edTime)) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * method to get date
     * @param bean
     *@param dateFormat  @return
     */
    public String convertDate(CalendarBean bean, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String str_date = String.valueOf(bean);
        Date date = null;
        try {
            date = (Date) formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Today is " + date.getTime());
        return String.valueOf(date.getTime());
        //return formatter.format(calendar.getTime());
    }

    /**
     * method to get date
     * @param stringDate .
     * @param dateFormat .
     * @return .
     */
    public Date getDateFromString(String stringDate, String dateFormat) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            return format.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    /**
     * get cards type map
     * @return
     */
    public HashMap<String, String> getCardsType() {
        HashMap<String, String> cardsMap = new HashMap<>();
        cardsMap.put("electron", "/^(4026|417500|4405|4508|4844|4913|4917)\\d+$/");
        cardsMap.put("maestro", "/^(5018|5020|5038|5612|5893|6304|6759|6761|6762|6763|0604|6390)\\d+$/");
        cardsMap.put("dankort", "/^(5019)\\d+$/");
        cardsMap.put("interpayment", "/^(636)\\d+$/");
        cardsMap.put("unionpay", "/^(62|88)\\d+$/");
        cardsMap.put("visa", "/^4[0-9]{12}(?:[0-9]{3})?$/");
        cardsMap.put("mastercard", "/^5[1-5][0-9]{14}$/");
        cardsMap.put("amex", "/^3[47][0-9]{13}$/");
        cardsMap.put("diners", "/^3(?:0[0-5]|[68][0-9])[0-9]{11}$/");
        cardsMap.put("discover", "/^6(?:011|5[0-9]{2})[0-9]{12}$/");
        cardsMap.put("jcb", "/^(?:2131|1800|35\\d{3})\\d{11}$/");
        return cardsMap;

    }

    /**
     * get map key by value
     * @param map
     * @param value
     * @return
     */
    public <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }


    /**
     * function to update count
     * @param mActivity
     * @param huntId
     */
    public void hitUpdateCountApi(AppCompatActivity mActivity, String huntId) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_HUNT_ID, huntId);
        Call<ResponseBody> call = apiInterface.hitUpdateCountApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, null, 1);

    }

    /**
     * convert distance
     * @param distance value
     * @param type 0 for km, 1 for miles
     * @return convertedDistanceUnit
     */
    public double convertDistanceUnit(double distance, int type) {
        float value = Float.parseFloat(String.valueOf(distance));
        return type == 0 ? (value / 0.621371f) : (0.621371f * value);
    }

    /**
     * encrypt text message
     * @param text message
     * @return base64 encrypt data
     */
    public String encryptString(String text) {
        // Sending side
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * decrypt base64 encrypt data
     * @param base64 encrypt data
     * @return text message
     */
    public String decryptString(String base64) {
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, StandardCharsets.UTF_8);
    }
}