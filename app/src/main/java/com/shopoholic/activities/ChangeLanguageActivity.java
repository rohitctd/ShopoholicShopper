package com.shopoholic.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ChangeLanguageActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.tv_english)
    TextView tvEnglish;
    @BindView(R.id.tv_chinese_trad)
    TextView tvChineseTrad;
    @BindView(R.id.tv_chinese_simple)
    TextView tvChineseSimple;
    @BindView(R.id.tv_malyalm)
    TextView tvMalyalm;
    @BindView(R.id.tv_hindi)
    TextView tvHindi;
    @BindView(R.id.tv_arabic)
    TextView tvUrdu;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private Locale locale = Locale.ENGLISH;
    private int languageCode = 1;
    private String language = Constants.AppConstant.CODE_ENGLISH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        ButterKnife.bind(this);
        initVariables();


    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.change_language));

        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);
        String currentLang = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE);
        setLanguage(currentLang);
    }


    @OnClick({R.id.iv_back, R.id.tv_english, R.id.tv_chinese_trad, R.id.tv_chinese_simple, R.id.tv_malyalm, R.id.tv_hindi, R.id.tv_arabic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_english:
                languageCode = 1;
                changeLanguage(Constants.AppConstant.ENGLISH);
                language = Constants.AppConstant.CODE_ENGLISH;
                locale = Locale.ENGLISH;
                break;
            case R.id.tv_chinese_trad:
                languageCode = 3;
                changeLanguage(Constants.AppConstant.CHINES_TRAD);
                language = Constants.AppConstant.CODE_CHINES_TRADITIONAL;
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            case R.id.tv_chinese_simple:
                languageCode = 4;
                changeLanguage(Constants.AppConstant.CHINES_SIMPLE);
                language = Constants.AppConstant.CODE_CHINES_SIMPLIFIED;
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case R.id.tv_malyalm:
                languageCode = 0;
                changeLanguage(Constants.AppConstant.MALAYALAM);
                language = Constants.AppConstant.CODE_MALAYALAM;
                locale = new Locale(language, "");
                break;
            case R.id.tv_hindi:
                languageCode = 0;
                changeLanguage(Constants.AppConstant.HINDI);
                language = Constants.AppConstant.CODE_HINDI;
                locale = new Locale(language, "");
                break;
            case R.id.tv_arabic:
                languageCode = 2;
                changeLanguage(Constants.AppConstant.ARABIC);
                language = Constants.AppConstant.CODE_ARABIC;
                locale = new Locale(language, "");
                break;
        }
    }

    /**
     * method to change languages
     */
    private void changeLanguage(String selectedLang) {
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            hitChangeLanguageApi(selectedLang);

        }
    }

    /**
     * method to set current Language
     *
     * @param currentLang
     */
    private void setLanguage(String currentLang) {
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE, currentLang);
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE_CODE, String.valueOf(languageCode));
        tvEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvChineseTrad.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvChineseSimple.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvMalyalm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHindi.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvUrdu.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        switch (currentLang) {
            case Constants.AppConstant.CHINES_TRAD:
                tvChineseTrad.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_green, 0);
                break;
            case Constants.AppConstant.CHINES_SIMPLE:
                tvChineseSimple.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_green, 0);
                break;
            case Constants.AppConstant.MALAYALAM:
                tvMalyalm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_green, 0);
                break;
            case Constants.AppConstant.HINDI:
                tvHindi.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_green, 0);
                break;
            case Constants.AppConstant.ARABIC:
                tvUrdu.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_green, 0);
                break;
            default:
                tvEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_green, 0);
        }
    }


    /**
     * Method to hit the change language api
     * @param selectedLang
     */
    private void hitChangeLanguageApi(final String selectedLang) {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = AppUtils.getInstance().getUserMap(this);
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_USER_LANGUAGE, String.valueOf(languageCode));
        Call<ResponseBody> call = apiInterface.hitEditProfileDataApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {

            @Override
            public void onSuccess(int responseCode, String response, int requestCode) {
                progressBar.setVisibility(View.GONE);
                AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        setLanguage(selectedLang);
//                        Locale locale = new Locale(language);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                        AppUtils.getInstance().openNewActivity(ChangeLanguageActivity.this, new Intent(ChangeLanguageActivity.this, HomeActivity.class));
                        break;
                }
            }


            @Override
            public void onError(String response, int requestCode) {
                AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onFailure() {
                progressBar.setVisibility(View.GONE);
            }
        }, 1);
    }

}
