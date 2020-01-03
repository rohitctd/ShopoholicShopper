package com.shopoholic.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.UserStatusDialogCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Dialog for image selection
 */

public class CustomDialogForSelectUserStatus extends Dialog {

    @BindView(R.id.tv_active)
    CustomTextView tvActive;
    @BindView(R.id.tv_away)
    CustomTextView tvAway;
    @BindView(R.id.tv_do_not_disturb)
    CustomTextView tvDoNotDisturb;
    @BindView(R.id.tv_offline)
    CustomTextView tvOffline;
    @BindView(R.id.view_offline)
    View viewOffline;
    private Context mContext;
    private int type;
    private UserStatusDialogCallback dialogCallback;

    public CustomDialogForSelectUserStatus(Context mContext, int type, UserStatusDialogCallback dialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.dialogCallback = dialogCallback;
        this.type = type;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_status);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(null);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().getAttributes().windowAnimations = R.style.DialogBottomAnimation;
        }

        if (type == 2){
            tvActive.setText(mContext.getString(R.string.select_gender));
            tvAway.setText(mContext.getString(R.string.male));
            tvDoNotDisturb.setText(mContext.getString(R.string.female));
            tvOffline.setText(mContext.getString(R.string.other));
        }

        if (type == 3){
            tvActive.setText(mContext.getString(R.string.followed_buddies));
            tvAway.setText(mContext.getString(R.string.followed_worked_buddies));
            tvDoNotDisturb.setText(mContext.getString(R.string.nearby_buddies));
            tvOffline.setVisibility(View.GONE);
            viewOffline.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_active, R.id.tv_away, R.id.tv_do_not_disturb, R.id.tv_offline})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_active:
                dialogCallback.onSelect(tvActive.getText().toString(), 1);
                break;
            case R.id.tv_away:
                dialogCallback.onSelect(tvAway.getText().toString(), 2);
                break;
            case R.id.tv_do_not_disturb:
                dialogCallback.onSelect(tvDoNotDisturb.getText().toString(), 3);
                break;
            case R.id.tv_offline:
                dialogCallback.onSelect(tvOffline.getText().toString(), 4 );
                break;
        }
        dismiss();
    }
}
