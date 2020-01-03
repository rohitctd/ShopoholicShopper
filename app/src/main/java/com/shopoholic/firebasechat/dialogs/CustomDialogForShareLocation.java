package com.shopoholic.firebasechat.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.firebasechat.interfaces.LocationDialogCallback;


/**
 * Dialog for image selection
 */

public class CustomDialogForShareLocation extends Dialog implements View.OnClickListener {
    private Context mContext;
    private String locationUri;
    private LocationDialogCallback locationDialogCallback;
    private TextView tvCamera;
    private TextView tvGallery;
    private ImageView ivLocation;
    private Button btnShareLocation;
    private Button btnCancel;
    private ProgressBar progressBar;

    public CustomDialogForShareLocation(Context mContext, String locationUri, LocationDialogCallback locationDialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.locationUri = locationUri;
        this.locationDialogCallback = locationDialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.dialog_share_location);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(null);
        getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        setListeners();
    }



    /**
     * Method to set listener in views
     */
    private void setListeners() {
        btnShareLocation.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
      if (viewId == R.id.btn_cancel) {
            dismiss();
            locationDialogCallback.onCancel();
        } else {
            dismiss();

        }
    }
}
