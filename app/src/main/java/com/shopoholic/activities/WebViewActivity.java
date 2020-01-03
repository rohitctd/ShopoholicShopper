package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;


public class WebViewActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private AppCompatActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        initViewsAndSetData();
    }

    /**
     * method to initialize views and set data
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initViewsAndSetData() {
        ivBack.setVisibility(View.VISIBLE);
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        mActivity = WebViewActivity.this;
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);

//        webView.getSettings().setAllowFileAccess(true);

        String url = "";
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().hasExtra(Constants.ABOUT_US)) {
                tvTitle.setText(getString(R.string.about_us));
                url = Constants.ABOUT_US_URL;
            }
            if (getIntent().hasExtra(Constants.TERM_AND_CONDITION)) {
                tvTitle.setText(getString(R.string.terms_conditions));
                url = Constants.TERM_CONDITION_URL;
            }
            if (getIntent().hasExtra(Constants.PRIVACY_POLICY)) {
                tvTitle.setText(getString(R.string.privacy_policy));
                url = Constants.PRIVACY_POLICY_URL;
            }
            if (getIntent().hasExtra(Constants.COPY_RIGHT)) {
                tvTitle.setText(getString(R.string.cancellation_policy));
                url = Constants.COPY_RIGHT_URL;
            }
            if (getIntent().hasExtra(Constants.FAQ)) {
                tvTitle.setText(getString(R.string.about_us_and_faqs));
                url = Constants.FAQ_URL;
            }
            if (getIntent().hasExtra(FirebaseConstants.FILE)) {
                tvTitle.setText(getString(R.string.file));
                String pdfUrl = getIntent().getExtras().getString(FirebaseConstants.FILE);
                if (pdfUrl != null && !pdfUrl.contains("http") && !pdfUrl.contains("https")) {
                    pdfUrl = "https://" + url;
                }
                url = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + pdfUrl;
            }
            if (getIntent().hasExtra(FirebaseConstants.PDF)) {
                tvTitle.setText(getString(R.string.file));
                String pdfUrl = getIntent().getExtras().getString(FirebaseConstants.PDF);
                if (pdfUrl != null && !pdfUrl.contains("http") && !pdfUrl.contains("https")) {
                    pdfUrl = "https://" + pdfUrl;
                }
                url = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + pdfUrl;
            }
            if (getIntent().hasExtra(FirebaseConstants.SHEET)) {
                tvTitle.setText(getString(R.string.file));
                String pdfUrl = getIntent().getExtras().getString(FirebaseConstants.SHEET);
                if (pdfUrl != null && !pdfUrl.contains("http") && !pdfUrl.contains("https")) {
                    pdfUrl = "https://" + pdfUrl;
                }
                url = "http://docs.google.com/gview?embedded=true&url=" + pdfUrl;
            }
            startWebView(url);
            webView.loadUrl(url);

        }
    }

    /**
     * method to set web view client
     *
     * @param url url to load in web view
     */
    private void startWebView(final String url) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                progressBar.setVisibility(View.VISIBLE);
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressBar.isShown()) {
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        onBackPressed();
    }
}