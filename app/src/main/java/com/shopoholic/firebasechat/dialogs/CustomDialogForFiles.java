package com.shopoholic.firebasechat.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.interfaces.FilePickerDialogCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustomDialogForFiles extends Dialog {

    @BindView(R.id.tv_photos)
    CustomTextView tvPhotos;
    @BindView(R.id.tv_videos)
    CustomTextView tvVideos;
    @BindView(R.id.tv_location)
    CustomTextView tvLocation;
    @BindView(R.id.tv_file)
    CustomTextView tvFile;
    @BindView(R.id.tv_cancel)
    CustomTextView tvCancel;
    @BindView(R.id.dialogImagePickerRootRl)
    LinearLayout dialogImagePickerRootRl;
    private Context context;
    private FilePickerDialogCallback filePickerDialogCallback;

    public CustomDialogForFiles(Context context, FilePickerDialogCallback filePickerDialogCallback) {
        super(context);
        this.context = context;
        this.filePickerDialogCallback = filePickerDialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_files);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(null);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().getAttributes().windowAnimations = R.style.DialogBottomAnimation;
        }

    }

    @OnClick({R.id.tv_photos, R.id.tv_videos, R.id.tv_location, R.id.tv_file, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_photos:
                dismiss();
                filePickerDialogCallback.onPhotosSelection();
                break;
            case R.id.tv_videos:
                dismiss();
                filePickerDialogCallback.onVideosSelection();
                break;
            case R.id.tv_location:
                dismiss();
                filePickerDialogCallback.onLocationSelection();
                break;
            case R.id.tv_file:
                dismiss();
                filePickerDialogCallback.onFilesSelection();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
