package com.shopoholic.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.PopupItemDialogCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomDialogForCounterOffer extends Dialog {


    @BindView(R.id.tv_message_title)
    CustomTextView tvMessageTitle;
    @BindView(R.id.btn_accept)
    CustomButton btnAccept;
    @BindView(R.id.accept_button_dot)
    View acceptButtonDot;
    @BindView(R.id.layout_accept_button)
    FrameLayout layoutAcceptButton;
    @BindView(R.id.btn_reject)
    CustomButton btnReject;
    @BindView(R.id.reject_button_dot)
    View rejectButtonDot;
    @BindView(R.id.layout_reject_button)
    FrameLayout layoutRejectButton;
    @BindView(R.id.btn_counter)
    CustomButton btnCounter;
    @BindView(R.id.counter_button_dot)
    View counterButtonDot;
    @BindView(R.id.layout_counter_button)
    FrameLayout layoutCounterButton;
    @BindView(R.id.ll_accept_reject)
    LinearLayout llAcceptReject;
    private Context context;
    private String currency;
    private String price;
    private PopupItemDialogCallback popupItemDialogCallback;

    public CustomDialogForCounterOffer(@NonNull Context context, String currency, String price, PopupItemDialogCallback popupItemDialogCallback) {
        super(context);
        this.context = context;
        this.price = price;
        this.currency = currency;
        this.popupItemDialogCallback = popupItemDialogCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_counter_offer);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
        tvMessageTitle.setText(TextUtils.concat(context.getString(R.string.i_would_like_to_buy_it_for) + " " + currency + price));

    }

    @OnClick({R.id.btn_accept, R.id.btn_reject, R.id.btn_counter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_accept:
                dismiss();
                popupItemDialogCallback.onItemOneClick();
                break;
            case R.id.btn_reject:
                dismiss();
                popupItemDialogCallback.onItemTwoClick();
                break;
            case R.id.btn_counter:
                dismiss();
                popupItemDialogCallback.onItemThreeClick();
                break;
        }
    }
}
