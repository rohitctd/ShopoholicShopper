package com.shopoholic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.shopoholic.R;
import com.shopoholic.adapters.WalkThroughPagerAdapter;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class TutorialActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.pageIndicator)
    CircleIndicator pageIndicator;
    @BindView(R.id.btn_signup)
    CustomButton btnSignup;
    @BindView(R.id.btn_login)
    CustomButton btnLogin;
    @BindView(R.id.btn_guest)
    CustomTextView btnGuest;
    @BindView(R.id.ll_signup_login)
    LinearLayout llSignupLogin;
    private WalkThroughPagerAdapter mPagerAdapter;
    private Handler handler;
    private Runnable runnable;
    private boolean shouldRemoveCallback;

    public TutorialActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initVariables();
        setAdapters();
    }


    /**
     * function to initialize the variables
     */
    public void initVariables() {
        handler = new Handler();
        mPagerAdapter = new WalkThroughPagerAdapter(this, Constants.AppConstant.VIEW_PAGER_SIZE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runnable = this;
                if (pager.getAdapter() != null) {
                    int item = pager.getCurrentItem() + 1;
                    if (item >= mPagerAdapter.getCount()) item = 0;
                    pager.setCurrentItem(item);
                }
                handler.postDelayed(runnable, Constants.AppConstant.TIME_OUT);
            }
        }, Constants.AppConstant.TIME_OUT);
        pager.addOnPageChangeListener(this);
        pager.setOffscreenPageLimit(3);

        if (getIntent() != null && getIntent().getExtras() != null){
            String fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
            if (fromClass.equals(Constants.IntentConstant.NOTIFICATION)){
                llSignupLogin.setVisibility(View.GONE);
                btnGuest.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * method to set the pager adapter
     */
    private void setAdapters() {
        pager.setAdapter(mPagerAdapter);
        pageIndicator.setViewPager(pager);
    }


    @OnClick({R.id.btn_signup, R.id.btn_login, R.id.btn_guest})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_signup:
                intent = new Intent(this, SignupActivity.class);
                intent.putExtra(Constants.IntentConstant.FROM_TUTORIAL, true);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_login:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.WALKTHROUGH);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_guest:
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.WALKTHROUGH);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                startActivity(intent);
                finish();
                break;
        }
        AppSharedPreference.getInstance().putBoolean(this, AppSharedPreference.PREF_KEY.IS_TUTORIAL_SEEN, true);
    }
}
