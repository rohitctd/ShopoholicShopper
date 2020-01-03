package com.shopoholic.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.MessageDialogCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Dialog for image selection
 */

public class CustomDialogForMessage extends Dialog {
    private final boolean isCancelShown;
    @BindView(R.id.tv_message_title)
    CustomTextView tvMessageTitle;
    @BindView(R.id.tv_message)
    CustomTextView tvMessage;
    @BindView(R.id.btn_submit)
    CustomButton btnSubmit;
    @BindView(R.id.view_cancel)
    View viewCancel;
    @BindView(R.id.tv_cancel)
    CustomTextView tvCancel;
    private Context mContext;
    private MessageDialogCallback messageDialogCallback;
    private String dialogTitle;
    private String dialogText;
    private String dialogButtonText;

    public CustomDialogForMessage(Context mContext, String dialogTitle, String dialogText, String dialogButtonText, boolean isCancelShown, MessageDialogCallback messageDialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.dialogTitle = dialogTitle;
        this.dialogText = dialogText;
        this.isCancelShown = isCancelShown;
        this.dialogButtonText = dialogButtonText;
        this.messageDialogCallback = messageDialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_message);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
        setData();
    }

    /**
     * Method to set data on views
     */
    private void setData() {
        tvMessageTitle.setText(dialogTitle);
        tvMessage.setText(dialogText);
        btnSubmit.setText(dialogButtonText);
        if (dialogTitle.equals("")) {
            tvMessageTitle.setVisibility(View.GONE);
            tvMessage.setPadding(0, 30, 0, 0);
        }
        if (isCancelShown) {
            viewCancel.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.VISIBLE);
        }else {
            viewCancel.setVisibility(View.GONE);
            tvCancel.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btn_submit, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                dismiss();
                messageDialogCallback.onSubmitClick();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
