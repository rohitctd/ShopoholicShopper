package com.shopoholic.firebasechat.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.shopoholic.R;
import com.shopoholic.firebasechat.models.ChatMessageBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class contains firebase utility functions
 */

public class FirebaseChatUtils {


    private static FirebaseChatUtils mFirebaseChatUtils;
    private static Toast toast;

    /**
     * Method to get instance of class
     * @return
     */
    public static FirebaseChatUtils getInstance() {
        if (mFirebaseChatUtils == null) {
            mFirebaseChatUtils = new FirebaseChatUtils();
            return mFirebaseChatUtils;
        } else {
            return mFirebaseChatUtils;
        }
    }

    /**
     * Checks if the Internet connection is available.
     *
     * @return Returns true if the Internet connection is available. False otherwise.
     *
     **/
    public boolean isInternetAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();

    }


    /**
     * Shows the message passed in the parameter in the Toast.
     *
     * @param msg      Message to be show in the toast.
     *
     **/
    public void showToast(Context ctx, CharSequence msg) {

        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * method used to set image using glide
     * @param mContext
     * @param imageUrl
     * @param imageView
     * @param radius
     * @param placeholder
     * @param isCircular
     */
    public void setImageOnView(final Context mContext, Object imageUrl, final ImageView imageView, final float radius, final int placeholder, final ProgressBar progressBar, final boolean isCircular) {
        if (progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
        Glide.with(mContext).load(imageUrl).asBitmap().placeholder(placeholder).error(placeholder).centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                if (progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                if (isCircular)
                    circularBitmapDrawable.setCircular(true);
                else
                    circularBitmapDrawable.setCornerRadius(radius);
                imageView.setImageDrawable(circularBitmapDrawable);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                if (progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * method used to set image using glide
     * @param mContext
     * @param imageUrl
     * @param imageView
     * @param radius
     * @param placeholder
     * @param isCircular
     */
    public void setMessageImageOnView(final Context mContext, Object imageUrl, final ImageView imageView, final float radius, final Drawable placeholder, final ProgressBar progressBar, final boolean isCircular) {
        Glide.with(mContext).load(imageUrl).asBitmap().placeholder(placeholder).error(placeholder).centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                if (progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                if (isCircular)
                    circularBitmapDrawable.setCircular(true);
                else
                    circularBitmapDrawable.setCornerRadius(radius);
                imageView.setImageDrawable(circularBitmapDrawable);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                if (progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }



    /**
     * method to get date
     *
     *
     * @param context
     * @param chatMessage
     * @return
     */
    public ChatMessageBean getDateStamp(Context context, ChatMessageBean chatMessage, ChatMessageBean nextMessage) {
        ChatMessageBean time = new ChatMessageBean();
        int day = -1;
        if (nextMessage != null) {
            if (!getDate(nextMessage.getTimestamp().toString()).equals(getDate(chatMessage.getTimestamp().toString()))) {
                day = getDateDifference(chatMessage.getTimestamp().toString());
            }
        } else {
            day = getDateDifference(chatMessage.getTimestamp().toString());
        }

        if (day == -1) {
            return null;
        } else if (day == 0) {
            time.setType(FirebaseConstants.CHAT_TIME);
            time.setMessageText("Today");
            time.setTimestamp(Long.parseLong(chatMessage.getTimestamp().toString()) - 1);
            return time;
        } else if (day == 1) {
            time.setType(FirebaseConstants.CHAT_TIME);
            time.setMessageText(context.getString(R.string.yesterday));
            time.setTimestamp(Long.parseLong(chatMessage.getTimestamp().toString()) - 1);
            return time;
        } else {
            time.setType(FirebaseConstants.CHAT_TIME);
            time.setMessageText(getDate(chatMessage.getTimestamp().toString()));
            time.setTimestamp(Long.parseLong(chatMessage.getTimestamp().toString()) - 1);
            return time;
        }
    }

    /**
     * get time difference
     *
     * @param timeStamp
     * @return
     */
    public int getDateDifference(String timeStamp) {
        long timeInMillis = Long.parseLong(timeStamp);
        int time = 0;
        try {
            String date = (String) DateFormat.format("MM/dd/yyyy", timeInMillis);
            String currentDate = (String) DateFormat.format("MM/dd/yyyy", System.currentTimeMillis());

            Date date1;
            Date date2;

            SimpleDateFormat dates = new SimpleDateFormat("MM/dd/yyyy");

            //Setting dates
            date1 = dates.parse(date);
            date2 = dates.parse(currentDate);

            //Comparing dates
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);

            time = (int) differenceDates;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }


    /**
     * convert Time
     *
     * @param timeStamp
     * @return
     */
    public String getDate(String timeStamp) {
        long timeInMillis = Long.parseLong(timeStamp);
        String date = (String) DateFormat.format("dd/MM/yyyy", timeInMillis);
        return date;
    }

    /**
     * Method to get Image path from uri
     *
     * @param mContext
     * @param mImageUri
     * @return
     */
    public String getImagePathFromUri(Context mContext, Uri mImageUri) {
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = mContext.getContentResolver().query(mImageUri, filePath, null, null, null);
        String picturePath;
        if (c != null) {
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            picturePath = c.getString(columnIndex);
            c.close();
        } else {
            picturePath = mImageUri.getPath();
        }
        return picturePath;
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
     * Method to get image uri from bitmap
     * @param mContext
     * @param inImage
     * @return
     */
    public Uri getImageUri(Context mContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Method to get image uri from bitmap
     * @param bitmapImage
     * @return
     */
    public Bitmap compressBitmap(Bitmap bitmapImage) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG,80, bytearrayoutputstream);
        byte[] BYTE = bytearrayoutputstream.toByteArray();
        return BitmapFactory.decodeByteArray(BYTE, 0, BYTE.length);
    }

    /**
     * Method to get time from timestamp
     * @param timeStamp
     * @return
     */
    public String getTimeFromTimeStamp(Context context, String timeStamp) {
        int day = getDateDifference(timeStamp);
        if (day == 0) {
            return DateFormat.format("hh:mm a", Long.parseLong(timeStamp)).toString();
        } else if (day == 1) {
            return context.getString(R.string.yesterday);
        } else {
            return getDate(timeStamp);
        }
    }

    /**
     * Method to get time from timestamp
     * @param dateString of date
     * @param context
     * @param dateFormat
     * @return day difference
     */
    @SuppressLint("SimpleDateFormat")
    public String getDayDifference(Context context, String dateString, String dateFormat) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            Date date = (Date) formatter.parse(dateString);
            long day = getDateDifference(String.valueOf(date.getTime()));
            if (day == 0) {
                return DateFormat.format("hh:mm a", date.getTime()).toString();
            } else if (day == 1) {
                return context.getString(R.string.yesterday);
            } else {
                return getDate(String.valueOf(date.getTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Method to get the current location
     * @param mContext
     */
    public void getLocation(Context mContext, LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            }
        }
    }

    public String getStaticMapImage(Context mContext, double latitude, double longitude) {
        String key = "&key=" + mContext.getString(R.string.gooogle_api_key);
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=12&size=200x180&markers=color:red%7Clabel:S%7C" + latitude + "," + longitude + "&markers=size:tiny%7Ccolor:green%7CDelta+Junction,AK&markers=size:mid%7Ccolor:0xFFFF00%7Clabel:C%7CTok,AK&key=AIzaSyAZxKx6JwRrhBJFJy0Wp_V9ASrG-wGV_xo"+key;
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
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */
    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }


    /**
     * method to get audio file path
     *
     * @return
     */
    public String getAudioFilePath() {
        File file = new File(android.os.Environment.getExternalStorageDirectory() + "/Chat/Audios");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/AUDIO_" + System.currentTimeMillis() + ".aac");
        return uriSting;

    }

    /**
     * method to get the file type
     * @param messageType
     * @return
     */
    public int getFileType(String messageType) {
        if (messageType.equals(FirebaseConstants.IMAGE))
            return FirebaseConstants.FILE_IMAGE;
        else if (messageType.equals(FirebaseConstants.VIDEO))
            return FirebaseConstants.FILE_VIDEO;
        else if (messageType.equals(FirebaseConstants.AUDIO))
            return FirebaseConstants.FILE_AUDIO;
        else
            return -1;

    }
}
