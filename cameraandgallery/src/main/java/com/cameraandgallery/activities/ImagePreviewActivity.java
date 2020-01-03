package com.cameraandgallery.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.cameraview.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by appinventiv-pc on 25/5/18.
 */

public class ImagePreviewActivity extends AppCompatActivity {
    private static final String TAG = "Image";
    private ImageView ivImage;
    private FloatingActionButton fabCameraCancel, fabCameraDone;
    private String image = "";
    private Handler mBackgroundHandler;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initViews();
        setListener();
    }

    /**
     *
     */
    private void initViews() {
        ivImage = findViewById(R.id.iv_image);
        progressBar = findViewById(R.id.progress_bar);
        fabCameraCancel = findViewById(R.id.fab_camera_cancel);
        fabCameraDone = findViewById(R.id.fab_camera_done);
        if (getIntent() != null && getIntent().getExtras() != null){
            image = getIntent().getExtras().getString("image", "");
            if (!image.equals("")) {
                setImage();
            }else {
                Toast.makeText(ImagePreviewActivity.this, getString(R.string.unable_to_take_image), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void setImage() {
        progressBar.setVisibility(View.VISIBLE);
        getBackgroundHandler().post(new Runnable() {
            @Override
            public void run() {
                File file = new File(image);
                OutputStream os = null;
                int size = (int) file.length();
                byte[] data = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                    buf.read(data, 0, data.length);
                    buf.close();
                    ExifInterface exifInterface = new ExifInterface(new ByteArrayInputStream(data));
                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    int rotationDegrees = 0;
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotationDegrees = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotationDegrees = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotationDegrees = 270;
                            break;
                    }
                    Bitmap bitmap;
                    os = new FileOutputStream(file);
                    if (rotationDegrees != 0) {
                        bitmap = rotateImage(BitmapFactory.decodeByteArray(data, 0, data.length), rotationDegrees);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    } else {
                        os.write(data);
                    }
                    image = file.getPath();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isDestroyed() && !isFinishing()) {
                                Glide.with(ImagePreviewActivity.this).load(image).into(ivImage);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                } catch (IOException e) {
                    Log.w(TAG, "Cannot write to file", e);
                    finish();
                } catch (OutOfMemoryError e) {
                    Toast.makeText(ImagePreviewActivity.this, getString(R.string.cannot_take_image), Toast.LENGTH_SHORT).show();
                    finish();
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException ignore) {
                        }
                    }
                }
            }
        });
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }


    /**
     * method to rotate bitmap
     * @param source
     * @param angle
     * @return
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private void setListener() {
        fabCameraCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        fabCameraDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (!image.equals(""))
                    intent.putExtra("image", image);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }
}
