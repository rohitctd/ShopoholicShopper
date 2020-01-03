package com.shopoholic.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopoholic.R;
import com.shopoholic.adapters.ViewPagerAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class MyWalletFragment extends Fragment {
    Unbinder unbinder;
    @BindView(R.id.tv_total_balance)
    CustomTextView tvTotalBalance;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private AppCompatActivity mActivity;
    private ViewPagerAdapter adapter;
    private WalletPaidReceiveFragment walletPaidFragment;
    private WalletPaidReceiveFragment walletReceiveFragment;
    private PointsSummaryFragment pointsSummaryFragment;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_wallet, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        setAdapters();
        setUpViewPager();
        return rootView;
    }

    /**
     * method to set adapter on views
     */
    private void setAdapters() {
    }

    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        walletPaidFragment = new WalletPaidReceiveFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IntentConstant.IS_PAID, true);
        walletPaidFragment.setArguments(bundle);
        walletReceiveFragment = new WalletPaidReceiveFragment();
        Bundle receiveBundle = new Bundle();
        receiveBundle.putBoolean(Constants.IntentConstant.IS_PAID, false);
        walletReceiveFragment.setArguments(receiveBundle);
        pointsSummaryFragment = new PointsSummaryFragment();
    }


    /**
     * Method to set viewPager
     */
    private void setUpViewPager() {
        adapter.addFragment(walletPaidFragment, getString(R.string.paid));
        adapter.addFragment(walletReceiveFragment, getString(R.string.receive));
        adapter.addFragment(pointsSummaryFragment, getString(R.string.points_summary));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        AppUtils.getInstance().setCustomFont(mActivity, tabLayout);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * method to set total balance
     * @param totalBalance
     */
    public void setTotalBalance(String totalBalance) {
        tvTotalBalance.setText(TextUtils.concat(totalBalance + " " + getString(R.string.points)));
        try {
            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.WALLET_POINTS, String.valueOf((int) Double.parseDouble(totalBalance)));
        }catch (Exception e) {
            e.printStackTrace();
            AppSharedPreference.getInstance().putString(mActivity, AppSharedPreference.PREF_KEY.WALLET_POINTS, "0");
        }
    }
}
