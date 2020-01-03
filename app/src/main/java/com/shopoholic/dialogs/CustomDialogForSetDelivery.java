package com.shopoholic.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.MakeOfferDialogCallback;
import com.shopoholic.utils.AppUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;


/**
 * Dialog for image selection
 */

public class CustomDialogForSetDelivery extends Dialog {

    @BindView(R.id.tv_message_title)
    CustomTextView tvMessageTitle;
    @BindView(R.id.label_bidding_price)
    CustomTextView labelBiddingPrice;
    @BindView(R.id.tv_price)
    CustomEditText tvPrice;
    @BindView(R.id.tv_delivery_date)
    CustomTextView tvDeliveryDate;
    @BindView(R.id.ll_delivery_date)
    LinearLayout llDeliveryDate;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.tv_cancel)
    CustomTextView tvCancel;

    private String charges;
    private String currency;
    private Context mContext;
    private MakeOfferDialogCallback dialogCallback;
    private String date;

    public CustomDialogForSetDelivery(@NonNull Context mContext, String currency, String charges, String date, MakeOfferDialogCallback dialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.currency = currency;
        this.charges = charges;
        this.date = date;
        this.dialogCallback = dialogCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_accept_hunt);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
        labelBiddingPrice.setText(TextUtils.concat(mContext.getString(R.string.expected_delivery_charge) + "(" + currency + ")*"));
        btnAction.setText(mContext.getString(R.string.submit));
        if (!charges.equals("") && Double.parseDouble(charges) != 0)
            tvPrice.setText(String.valueOf((int)Double.parseDouble(charges)));
        if (!date.equals("") && !date.equals("0000-00-00"))
            tvDeliveryDate.setText(AppUtils.getInstance().formatDate(date, SERVICE_DATE_FORMAT, DATE_FORMAT));

        if (tvDeliveryDate.getText().toString().trim().length() != 0) {
            tvDeliveryDate.setEnabled(false);
        }else {
            tvDeliveryDate.setEnabled(true);
        }
    }


    @OnClick({R.id.tv_delivery_date, R.id.btn_action, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_action:
                    if (isValidate()) {
                        dialogCallback.onSelect(tvPrice.getText().toString().trim(), currency, tvDeliveryDate.getText().toString().trim(), 0);
                        dismiss();
                    }
                break;
            case R.id.tv_delivery_date:
                AppUtils.getInstance().openDatePicker(mContext, tvDeliveryDate, Calendar.getInstance(), null, tvDeliveryDate.getText().toString().trim());
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    /**
     * function for validation
     *
     * @return
     */
    private boolean isValidate() {
        if (tvPrice.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mContext, mContext.getString(R.string.please_enter_expected_charges));
            return false;
        } else if (!charges.equals("") && Double.parseDouble(tvPrice.getText().toString().trim()) < Double.parseDouble(charges)) {
            AppUtils.getInstance().showToast(mContext, mContext.getString(R.string.cannot_decrease_price));
            return false;
        } else if (tvDeliveryDate.getText().toString().trim().length() == 0) {
            AppUtils.getInstance().showToast(mContext, mContext.getString(R.string.please_enter_expected_date));
            return false;
        }
        return true;
    }
}
