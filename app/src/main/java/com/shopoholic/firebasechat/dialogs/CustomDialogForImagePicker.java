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
import com.shopoholic.firebasechat.interfaces.DialogCallback;


/**
 * Dialog for image selection
 */

public class CustomDialogForImagePicker extends Dialog implements View.OnClickListener {
    private Context mContext;
    private DialogCallback dialogCallback;
    private TextView tvCamera;
    private TextView tvGallery;

    public CustomDialogForImagePicker(Context mContext, DialogCallback dialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.dialogCallback = dialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image_selection);
        getWindow().setBackgroundDrawable(null);
        getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        initViews();
        setListeners();
    }

    /**
     * Method to initialize the views
     */
    private void initViews() {
        tvCamera = findViewById(R.id.tv_camera);
        tvGallery = findViewById(R.id.tv_gallery);
    }

    /**
     * Method to set listener in views
     */
    private void setListeners() {
        tvCamera.setOnClickListener(this);
        tvGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.tv_camera) {
            dismiss();
            dialogCallback.onCameraSelection();
        } else if (viewId == R.id.tv_gallery) {
            dismiss();
            dialogCallback.onGallerySelection();
        } else {
            dismiss();

        }
    }
}
