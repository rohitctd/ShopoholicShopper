package com.cameraandgallery.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cameraandgallery.adapters.MediaAdapter;
import com.cameraandgallery.interfaces.RecyclerCallBack;
import com.cameraandgallery.utils.DoubleClickListener;
import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


public class CameraGalleryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CameraActivity";

    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };


    private int mCurrentFlash;

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private static final String FRAGMENT_DIALOG = "dialog";

    private static final int PREVIEW_IMAGE = 101;
    private RecyclerView rvHorizontalGallery;
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvNoData;
    private FloatingActionButton fabDone;
    private RecyclerView rvGallery;
    private RelativeLayout rlFullGallery;
    private FrameLayout bottomSheet;
    private ImageView takePicture;
    private FloatingActionButton fabCameraDone;
    private RelativeLayout llHorizontalView;
    private CameraView mCameraView;
    private FrameLayout control;
    private FrameLayout toolbar;
    private ImageView switchFlash;
    private ImageView switchCamera;

    public int maxSelection;
    private final String[] projection = new String[]{MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
    private final String[] projection2 = new String[]{MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA};
    public ArrayList<String> imagesSelected;
    public List<Boolean> selected;
    private List<String> mediaList;
    private MediaAdapter mAdapter;
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private Handler mBackgroundHandler;

    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);
            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    OutputStream os = null;
                    try {
                        File file = createImageFile();
                        os = new FileOutputStream(file);
                        os.write(data);
                        Intent intent = new Intent(CameraGalleryActivity.this, ImagePreviewActivity.class);
                        intent.putExtra("image", file.getPath());
                        startActivityForResult(intent, PREVIEW_IMAGE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
        }

    };


    /**
     * method to create image file
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name));
        if (!myDir.exists()) myDir.mkdir();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "image_" + timeStamp + "_";
        return File.createTempFile(imageFileName, ".jpg", myDir);
    }


    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        initViewsAndSetListener();
        initVariables();
        setListeners();
    }

    /**
     * method to initialize the views
     */
    private void initViewsAndSetListener() {
        rvHorizontalGallery = findViewById(R.id.rv_horizontal_gallery);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvNoData = findViewById(R.id.tv_no_data);
        fabDone = findViewById(R.id.fab_done);
        rvGallery = findViewById(R.id.rv_gallery);
        rlFullGallery = findViewById(R.id.rl_full_gallery);
        bottomSheet = findViewById(R.id.bottom_sheet);
        takePicture = findViewById(R.id.take_picture);
        fabCameraDone = findViewById(R.id.fab_camera_done);
        llHorizontalView = findViewById(R.id.ll_horizontal_view);
        mCameraView = findViewById(R.id.camera);
        control = findViewById(R.id.control);
        toolbar = findViewById(R.id.toolbar);
        switchFlash = findViewById(R.id.switch_flash);
        switchCamera = findViewById(R.id.switch_camera);

        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }
        ivBack.setOnClickListener(this);
        takePicture.setOnClickListener(this);
        fabDone.setOnClickListener(this);
        fabCameraDone.setOnClickListener(this);
        switchFlash.setOnClickListener(this);
        switchCamera.setOnClickListener(this);
/*
        mCameraView.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick() {
                if (rvHorizontalGallery.getVisibility() == View.VISIBLE) {
                    rvHorizontalGallery.setVisibility(View.INVISIBLE);
                }else {
                    rvHorizontalGallery.setVisibility(View.VISIBLE);
                }
                checkImageSize();
            }
        });
*/
        mCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rvHorizontalGallery.getVisibility() == View.VISIBLE) {
                    rvHorizontalGallery.setVisibility(View.INVISIBLE);
                }else {
                    rvHorizontalGallery.setVisibility(View.VISIBLE);
                }
                checkImageSize();
            }
        });

    }

    /**
     * method to initialize the variables
     */
    @SuppressLint("StaticFieldLeak")
    private void initVariables() {

        imagesSelected = new ArrayList<>();
        selected = new ArrayList<>();
        mediaList = new ArrayList<>();

//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) llHorizontalView.getLayoutParams();
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight((int) getResources().getDimension(R.dimen._200sdp));
        rlFullGallery.setVisibility(View.GONE);
        bottomSheet.setFocusable(true);
        rvHorizontalGallery.setFocusable(true);
        rvGallery.setFocusable(true);

        if (getIntent() != null && getIntent().getExtras() != null) {
            maxSelection = getIntent().getExtras().getInt("maxSelection", 1);
            List<String> selectedList = (List<String>) getIntent().getExtras().getSerializable("selectedList");
            if (selectedList != null && imagesSelected != null)
                imagesSelected.addAll(selectedList);
        }

        if (maxSelection == 0) maxSelection = Integer.MAX_VALUE;
        populateRecyclerView();

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                getPicBuckets();
                return null;
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.take_picture) {
            if (mCameraView != null && mCameraView.isCameraOpened()) {
                mCameraView.takePicture();

            }
        } else if (i == R.id.iv_back) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) llHorizontalView.getLayoutParams();
                    bottomSheetBehavior.setHideable(false);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight((int) getResources().getDimension(R.dimen._200sdp));
                }
            }, 500);

        } else if (i == R.id.fab_camera_done || i == R.id.fab_done) {
            returnResult();

        } else if (i == R.id.switch_flash) {
            if (mCameraView != null) {
                mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                switchFlash.setImageResource(FLASH_ICONS[mCurrentFlash]);
                mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
            }

        } else if (i == R.id.switch_camera) {
            if (mCameraView != null) {
                int facing = mCameraView.getFacing();
                mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
                        CameraView.FACING_BACK : CameraView.FACING_FRONT);
            }
        }
    }

    /**
     * method to set listener on views
     */
    private void setListeners() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                llHorizontalView.setAlpha(1 - slideOffset);
                rlFullGallery.setAlpha(slideOffset);
                control.setAlpha(1 - slideOffset * 5);
                llHorizontalView.setVisibility(llHorizontalView.getAlpha() == 0.0f ? View.GONE : View.VISIBLE);
                rlFullGallery.setVisibility(rlFullGallery.getAlpha() == 0.0f ? View.GONE : View.VISIBLE);
//                control.setVisibility(llHorizontalView.getAlpha() == 1.0f ? View.VISIBLE : View.GONE);
            }
        });
    }
    /**
     * method to populate recycle views
     */
    private void populateRecyclerView() {
        mAdapter = new MediaAdapter(mediaList, selected, getApplicationContext(), new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                if (selected.size() > 0 && position >= 0) {
                    if (selected.get(position).equals(false) && imagesSelected.size() < maxSelection) {
                        imagesSelected.add(mediaList.get(position));
                        selected.set(position, !selected.get(position));
                        mAdapter.notifyItemChanged(position);
                    } else if (selected.get(position).equals(true)) {
                        if (imagesSelected.indexOf(mediaList.get(position)) != -1) {
                            imagesSelected.remove(imagesSelected.indexOf(mediaList.get(position)));
                            selected.set(position, !selected.get(position));
                            mAdapter.notifyItemChanged(position);
                        }
                    } else {
                        Toast.makeText(CameraGalleryActivity.this, getString(R.string.you_can_select_only) + " " + maxSelection, Toast.LENGTH_SHORT).show();
                    }
                    checkImageSize();
                }
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 5, GridLayoutManager.HORIZONTAL, false);

        rvGallery.setLayoutManager(layoutManager);
        rvHorizontalGallery.setLayoutManager(mLayoutManager);

        rvGallery.getItemAnimator().setChangeDuration(0);
        rvHorizontalGallery.getItemAnimator().setChangeDuration(0);

        rvGallery.setAdapter(mAdapter);
        rvHorizontalGallery.setAdapter(mAdapter);

    }

    /**
     * function to check image size
     */
    private void checkImageSize() {
        if (imagesSelected.size() > 0 && rvHorizontalGallery.getVisibility() == View.VISIBLE){
            fabCameraDone.setVisibility(View.VISIBLE);
        }else {
            fabCameraDone.setVisibility(View.GONE);
        }
    }

    /*
     * Get all folders of images
     * */
    public void getPicBuckets() {
        Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Images.Media.DATE_ADDED);
        ArrayList<String> bucketNamesTEMP = null;
        if (cursor != null) {
            bucketNamesTEMP = new ArrayList<>(cursor.getCount());
        }
        HashSet<String> albumSet = new HashSet<>();
        if (cursor != null && cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                String album = cursor.getString(cursor.getColumnIndex(projection[0]));
                if (!albumSet.contains(album)) {
                    bucketNamesTEMP.add(album);
                    albumSet.add(album);
                    getPictures(album);
                }
            } while (cursor.moveToPrevious());
        }
        if (cursor != null) {
            cursor.close();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                if (mediaList.size() == 0) {
                    tvNoData.setVisibility(View.VISIBLE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                }
            }
        });
    }

    /*
     * Get all images of particular folder
     * */
    public void getPictures(String bucket) {
        Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection2,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{bucket}, MediaStore.Images.Media.DATE_ADDED);
        HashSet<String> albumSet = new HashSet<>();
        File file;
        if (cursor != null && cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                String path = cursor.getString(cursor.getColumnIndex(projection2[1]));
                file = new File(path);
                if (file.exists() && !albumSet.contains(path)) {
                    albumSet.add(path);
                    if (imagesSelected.size() > 0) {
                        if (imagesSelected.contains(path))
                            selected.add(true);
                        else
                            selected.add(false);
                    } else
                        selected.add(false);
                    mediaList.add(path);
                }
            } while (cursor.moveToPrevious());
        }
        if (cursor != null) {
            cursor.close();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaList.size() == 0) {
                    tvNoData.setVisibility(View.VISIBLE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                }
            }
        });

    }


    /*
     * Return selected images
     * */
    private void returnResult() {
        if (imagesSelected.size() > 0) {
            Intent returnIntent = new Intent();
            returnIntent.putStringArrayListExtra("result", imagesSelected);
            setResult(RESULT_OK, returnIntent);
            finish();
        } else {
            Toast.makeText(CameraGalleryActivity.this, getString(R.string.select_one_image), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PREVIEW_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null && data.getExtras().getString("image") != null) {
                File file = new File(data.getExtras().getString("image"));
                imagesSelected.clear();
                imagesSelected.add(file.getPath());

                //create file in storage
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                returnResult();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(this, R.string.camera_permission_not_granted,
                    Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }
}
