package com.shopoholic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.fragments.BlockDealsFragment;
import com.shopoholic.fragments.MyBuddyFragment;
import com.shopoholic.fragments.ProductFragment;
import com.shopoholic.fragments.ProductHuntFragment;
import com.shopoholic.interfaces.PopupItemDialogCallback;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommonActivity extends BaseActivity {


    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    private boolean isProduct = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        ButterKnife.bind(this);
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        getDataFromIntent();
    }


    /**
     * method to get the data from intent
     */
    private void getDataFromIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            int fragmentType = getIntent().getExtras().getInt(Constants.IntentConstant.FRAGMENT_TYPE, 0);
            isProduct = getIntent().getExtras().getBoolean(Constants.IntentConstant.IS_PRODUCT, true);
            setFragmentOnContainer(fragmentType);
        }
    }


    /**
     * Method to set fragment on container
     *
     * @param fragmentType\
     */
    public void setFragmentOnContainer(int fragmentType) {
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        Fragment fragment;
        switch (fragmentType) {
            case 0:
                menuRight.setVisibility(View.VISIBLE);
                menuRight.setImageResource(R.drawable.ic_shppers_order_reverse);
                fragment = new MyBuddyFragment();
                AppUtils.getInstance().replaceFragments(this,
                        R.id.fl_container, fragment, MyBuddyFragment.class.getName(), false);
                tvTitle.setText(getString(R.string.my_buddy));
                break;

            case 1:
                fragment = new BlockDealsFragment();
                AppUtils.getInstance().replaceFragments(this,
                        R.id.fl_container, fragment, BlockDealsFragment.class.getName(), false);
                tvTitle.setText(getString(R.string.blocked_deals));
                menuRight.setVisibility(View.GONE);
                menuSecondRight.setVisibility(View.GONE);
                menuThirdRight.setVisibility(View.GONE);
                break;

            case 2:
                Bundle productBundle = new Bundle();
                productBundle.putBoolean(Constants.IntentConstant.IS_PRODUCT, isProduct);
                fragment = new ProductFragment();
                fragment.setArguments(productBundle);
                AppUtils.getInstance().replaceFragments(this,
                        R.id.fl_container, fragment, ProductFragment.class.getName(), false);
                tvTitle.setText(getString(R.string.product_hunt));
                menuRight.setVisibility(View.GONE);
                menuSecondRight.setVisibility(View.GONE);
                menuThirdRight.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    @OnClick({R.id.iv_menu, R.id.menu_right, R.id.menu_second_right, R.id.menu_third_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.menu_right:
                final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_container);
                if (fragment instanceof MyBuddyFragment) {
                    AppUtils.getInstance().showMorePopUp(this, menuRight, getString(R.string.rating), getString(R.string.sort),
                            "",1, new PopupItemDialogCallback() {
                                @Override
                                public void onItemOneClick() {
                                    ((MyBuddyFragment) fragment).sortBuddyList(1);
                                }

                                @Override
                                public void onItemTwoClick() {
                                    ((MyBuddyFragment) fragment).sortBuddyList(2);
                                }

                                @Override
                                public void onItemThreeClick() {
                                }
                            });
                }
                break;
            case R.id.menu_second_right:
                break;
            case R.id.menu_third_right:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
