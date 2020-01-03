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
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RatingCallback;
import com.shopoholic.utils.AppUtils;
import com.whinc.widget.ratingbar.RatingBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Dialog for image selection
 */

public class CustomDialogForRatingAndReview extends Dialog {


    @BindView(R.id.tv_skip)
    CustomTextView tvSkip;
    @BindView(R.id.rb_rate_buddy)
    RatingBar rbRateBuddy;
    @BindView(R.id.rb_rate_merchant)
    RatingBar rbRateMerchant;
    @BindView(R.id.et_reviews)
    CustomEditText etReviews;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.tv_rate_merchant)
    CustomTextView tvRateMerchant;
    private Context mContext;
    private int type;
    private RatingCallback ratingCallback;
    private boolean isLoading = false;

    public CustomDialogForRatingAndReview(Context mContext, int type, RatingCallback ratingCallback) {
        super(mContext);
        this.mContext = mContext;
        this.type = type;
        this.ratingCallback = ratingCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rating_and_review);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
        btnAction.setText(mContext.getString(R.string.submit));

        if (type == 2) {
            tvRateMerchant.setVisibility(View.GONE);
            rbRateMerchant.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_skip, R.id.btn_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_skip:
                dismiss();
                break;
            case R.id.btn_action:
                if (rbRateBuddy.getCount() == 0 && rbRateMerchant.getCount() == 0) {
                    AppUtils.getInstance().showToast(mContext, mContext.getString(R.string.please_rate_merchant_buddy));
                    return;
                }
                if (!isLoading) {
                    isLoading = true;
                    AppUtils.getInstance().hideKeyboardOfEditText(mContext, etReviews);
                    AppUtils.getInstance().setButtonLoaderAnimation(mContext, btnAction, viewButtonLoader, viewButtonDot, true);
                    ratingCallback.ratingAndReview(rbRateBuddy.getCount(), rbRateMerchant.getCount(), etReviews.getText().toString());
                }
                break;
        }
    }

    /**
     * method to stop loader
     * @param isSuccess
     */
    public void stopLoader(boolean isSuccess) {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(mContext, btnAction, viewButtonLoader, viewButtonDot, false);
        if (isSuccess){
            dismiss();
        }
    }
}
