package com.shopoholic.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.dialogs.CustomDialogForMessage;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ContactUsActivity extends BaseActivity implements NetworkListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.et_feedback)
    CustomEditText etFeedback;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.ll_parent)
    LinearLayout llParent;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ButterKnife.bind(this);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.contact_us);
        ivBack.setVisibility(View.VISIBLE);
        btnAction.setText(getString(R.string.submit));
        llParent.requestLayout();
    }

    @OnClick({R.id.iv_back, R.id.btn_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_action:
                if (etFeedback.getText().toString().trim().length() != 0) {
                    if (!isLoading  && AppUtils.getInstance().isInternetAvailable(this)) {
                        hitFeedbackApi();
                    }
                }else {
                    AppUtils.getInstance().showToast(this, getString(R.string.please_give_feedback));
                }
                break;
        }
    }


    /**
     * method to hit feedback api
     */
    private void hitFeedbackApi() {
        isLoading = true;
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, true);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_FEEDBACK, etFeedback.getText().toString().trim());
        Call<ResponseBody> call = apiInterface.hitFeedbackApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_FEEDBACK);
    }

    @Override
    public void onSuccess(int responseCode, final String response, int requestCode) {
        isLoading = false;
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_FEEDBACK:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        new CustomDialogForMessage(this, "", getString(R.string.feedback_shared_with_admin),
                                getString(R.string.ok), false, this::finish).show();
                        break;
                    default:
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);
        isLoading = false;
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        AppUtils.getInstance().setButtonLoaderAnimation(this, btnAction, viewButtonLoader, viewButtonDot, false);
        isLoading = false;

    }
}

