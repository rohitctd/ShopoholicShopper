package com.shopoholic.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.MakeOfferDialogCallback;
import com.shopoholic.interfaces.UserStatusDialogCallback;
import com.shopoholic.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Dialog for image selection
 */

public class CustomDialogForMakeOffer extends Dialog {

    private final int status;
    @BindView(R.id.tv_message_title)
    CustomTextView tvMessageTitle;
    @BindView(R.id.et_message)
    CustomEditText etMessage;
    @BindView(R.id.btn_submit)
    CustomButton btnSubmit;
    @BindView(R.id.tv_cancel)
    CustomTextView tvCancel;
    private Context mContext;
    private MakeOfferDialogCallback makeOfferDialogCallback;
    private String currency;

    public CustomDialogForMakeOffer(Context mContext, int status, String currency, MakeOfferDialogCallback makeOfferDialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.status = status;
        this.currency = currency;
        this.makeOfferDialogCallback = makeOfferDialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_make_offer);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
        InputFilter[] filterArray = new InputFilter[1];
        if (status == 1) {
            etMessage.setHint(R.string.my_offer_for_this_would_be);
//            etMessage.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
//            etMessage.setInputType(InputType.TYPE_CLASS_NUMBER);
            etMessage.setMaxLines(1);
            filterArray[0] = new InputFilter.LengthFilter(15);
            tvMessageTitle.setText(TextUtils.concat(mContext.getString(R.string.make_an_offer) + "(" + currency + ")"));
        }else {
            etMessage.setHint(R.string.enter_your_text_here);
            etMessage.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            tvMessageTitle.setText(mContext.getString(R.string.why_report_deal));
            etMessage.setMaxLines(3);
            filterArray[0] = new InputFilter.LengthFilter(150);
            btnSubmit.setText(mContext.getString(R.string.report));
        }
        etMessage.setFilters(filterArray);
    }

    @OnClick({R.id.btn_submit, R.id.tv_cancel})
    public void onViewClicked(View view) {
        AppUtils.getInstance().hideKeyboardOfEditText(mContext, etMessage);
        switch (view.getId()) {
            case R.id.btn_submit:
                if (etMessage.getText().toString().trim().length() > 0) {
                    dismiss();
//                    String currency = mContext.getString(this.currency.equals("2") ? R.string.rupees : this.currency.equals("1") ? R.string.dollar : R.string.singapuri_dollar);
                    String message = mContext.getString(R.string.i_would_like_to_buy_it_for) + " " + currency + etMessage.getText().toString().trim();
                    makeOfferDialogCallback.onSelect(message, this.currency, etMessage.getText().toString().trim(), 0);
                }else {
                    AppUtils.getInstance().showToast(mContext,
                            mContext.getString(status == 1 ? R.string.please_enter_offer : R.string.please_enter_message));
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
