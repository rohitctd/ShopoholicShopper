package com.shopoholic.firebasechat.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.firebasechat.interfaces.FilePickerDialogCallback;


/**
 * Dialog for image selection
 */

public class CustomDialogForFilePicker extends Dialog implements View.OnClickListener {
    private Context mContext;
    private FilePickerDialogCallback filePickerDialogCallback;
    private TextView tvCamera;
    private TextView tvGallery;
    private TextView tvVideo;
    private TextView tvLocation;

    public CustomDialogForFilePicker(Context mContext, FilePickerDialogCallback filePickerDialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.filePickerDialogCallback = filePickerDialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_file_picker);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(null);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
        initViews();
        setListeners();
    }

    /**
     * Method to initialize the views
     */
    private void initViews() {
        tvCamera = findViewById(R.id.tv_camera);
        tvGallery = findViewById(R.id.tv_gallery);
        tvVideo = findViewById(R.id.tv_video);
        tvLocation = findViewById(R.id.tv_location);
    }

    /**
     * Method to set listener in views
     */
    private void setListeners() {
        tvCamera.setOnClickListener(this);
        tvGallery.setOnClickListener(this);
        tvVideo.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.tv_camera) {
            dismiss();
            filePickerDialogCallback.onFilesSelection();
        } else if (viewId == R.id.tv_gallery) {
            dismiss();
            filePickerDialogCallback.onPhotosSelection();
        } else if (viewId == R.id.tv_video) {
            dismiss();
            filePickerDialogCallback.onVideosSelection();
        } else if (viewId == R.id.tv_location) {
            dismiss();
            filePickerDialogCallback.onLocationSelection();
        } else {
            dismiss();

        }
    }
}
