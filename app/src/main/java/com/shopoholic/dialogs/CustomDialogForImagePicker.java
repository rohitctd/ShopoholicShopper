package com.shopoholic.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.activities.GroupChatActivity;
import com.shopoholic.firebasechat.activities.SingleChatActivity;
import com.shopoholic.interfaces.DialogCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustomDialogForImagePicker extends Dialog {
    @BindView(R.id.view_remove_photo_separator)
    View viewRemovePhotoSeparator;
    @BindView(R.id.tv_remove_photo)
    CustomTextView tvRemovePhoto;
    @BindView(R.id.tv_camera)
    CustomTextView tvCamera;
    @BindView(R.id.tv_gallery)
    CustomTextView tvGallery;
    @BindView(R.id.dialogImagePickerRootRl)
    LinearLayout dialogImagePickerRootRl;
    private Context context;
    private DialogCallback dialogCallback;

    public CustomDialogForImagePicker(Context context, DialogCallback dialogCallback) {
        super(context);
        this.context = context;
        this.dialogCallback = dialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image_picker);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(null);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().getAttributes().windowAnimations = R.style.DialogBottomAnimation;
        }
//        if (context instanceof SingleChatActivity || context instanceof GroupChatActivity) {
//            viewRemovePhotoSeparator.setVisibility(View.GONE);
//            tvRemovePhoto.setVisibility(View.GONE);
//        }
        tvCamera.setText(context.getString(R.string.update_photo));
        tvGallery.setVisibility(View.GONE);
        viewRemovePhotoSeparator.setVisibility(View.GONE);

    }

    @OnClick({R.id.tv_camera, R.id.tv_gallery, R.id.tv_remove_photo, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_camera:
                dialogCallback.onSubmit();
                dismiss();
                break;
            case R.id.tv_gallery:
                dialogCallback.onCancel();
                dismiss();
                break;
            case R.id.tv_remove_photo:
                dialogCallback.onRemove();
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }
}
