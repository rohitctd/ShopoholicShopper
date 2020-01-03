package com.shopoholic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.shopoholic.R;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.adapters.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.shopoholic.utils.Constants.IntentConstant.TYPE;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class MyFriendsFragment extends Fragment {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    Unbinder unbinder;
    private View rootView;
    private ViewPagerAdapter adapter;
    private AppCompatActivity mActivity;
    private FriendsFragment friendsFragment;
    private RequestsFragment requestsFragment;
    private ContactsFragment contactsFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_friend, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariable();
        setUpViewPager();
        getDataFromIntent();
        return rootView;
    }

    /**
     * function to initialize the variables
     */
    private void initVariable()
    {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        friendsFragment = new FriendsFragment();
        requestsFragment = new RequestsFragment();
        contactsFragment = new ContactsFragment();

    }

    /**
     * function to setup view pager
     */
    private void setUpViewPager() {
        adapter.addFragment(friendsFragment, getString(R.string.friends));
        adapter.addFragment(requestsFragment, getString(R.string.requests));
        adapter.addFragment(contactsFragment, getString(R.string.contacts));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);//ths is done to make sure that fragment does not load again
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * function to get data from intent
     */
    private void getDataFromIntent() {
        Intent intent = null;
        if (mActivity instanceof HomeActivity) {
            intent = ((HomeActivity) mActivity).getIntent();
        }
        if (intent != null && intent.getExtras() != null) {
            String type = intent.getExtras().getString(TYPE, "");
            if (type.equals("11")) {
                viewPager.setCurrentItem(0);
            }else if (type.equals("12")) {
                viewPager.setCurrentItem(1);
            }
            ((HomeActivity) mActivity).setIntent(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
