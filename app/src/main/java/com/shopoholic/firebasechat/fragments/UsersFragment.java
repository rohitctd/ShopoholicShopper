package com.shopoholic.firebasechat.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.shopoholic.R;
import com.shopoholic.firebasechat.adapters.UsersListAdapter;
import com.shopoholic.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseEventListeners;
import com.shopoholic.utils.AppSharedPreference;

import java.util.ArrayList;
import java.util.List;


/**
 * fragment to show user messages details
 */

public class UsersFragment extends Fragment {

    private View rootView;
    private Activity mActivity;
    private RecyclerView rvUsersList;
    private TextView tvNoData;
    private UsersListAdapter usersListAdapter;
    private List<UserBean> usersList;
    private LinearLayoutManager layoutManager;
    private String loginUserId;
    private FirebaseEventListeners usersListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_under_development, container, false);
        initViews();
        initVariables();
        setAdapter();
        getUsersList();
        return rootView;
    }

    /**
     * Method to initialize the views
     */
    private void initViews() {
        tvNoData = rootView.findViewById(R.id.tv_no_data);

    }

    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        mActivity = getActivity();
        usersList = new ArrayList<>();
        loginUserId = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID);
        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        usersListAdapter = new UsersListAdapter(mActivity, usersList, new RecycleViewCallBack() {
            @Override
            public void onClick(int position, View clickedView) {
                int id = clickedView.getId();

            }
            @Override
            public void onLongClick(int position, View clickedView) {}
        });
    }
    /**
     * Method to set listener in views
     */
    private void setAdapter() {
        rvUsersList.setLayoutManager(layoutManager);
        rvUsersList.setAdapter(usersListAdapter);
    }

    /**
     * Method to get the users list
     */
    private void getUsersList() {
        usersListener = new FirebaseEventListeners() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    UserBean userBean = dataSnapshot.getValue(UserBean.class);
                    if (userBean != null && !userBean.getUserId().equals(loginUserId)) {
                        usersList.add(userBean);
                        usersListAdapter.notifyItemInserted(usersList.size() - 1);
                    }
                    updateViews();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    UserBean userBean = dataSnapshot.getValue(UserBean.class);
                    if (userBean != null && !userBean.getUserId().equals(loginUserId)) {
                        for (int i = 0; i < usersList.size(); i++) {
                            if (usersList.get(i).getUserId().equals(userBean.getUserId())) {
                                usersList.set(i, userBean);
                                usersListAdapter.notifyItemChanged(i);
                                break;
                            }
                        }
                        updateViews();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserBean userBean = dataSnapshot.getValue(UserBean.class);
                    if (userBean != null && !userBean.getUserId().equals(loginUserId)) {
                        for (int i = 0; i < usersList.size(); i++) {
                            if (usersList.get(i).getUserId().equals(userBean.getUserId())) {
                                usersList.remove(i);
                                usersListAdapter.notifyItemRemoved(i);
                                usersListAdapter.notifyItemRangeChanged(i, usersList.size() - 1);
                                break;
                            }
                        }
                        updateViews();
                    }
                }
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).addChildEventListener(usersListener);
    }

    /**
     * method to updates the views
     */
    private void updateViews() {
        if (usersList.size() == 0){
            tvNoData.setVisibility(View.VISIBLE);
        }else {
            tvNoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        if (usersListener != null){
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).removeEventListener((ChildEventListener) usersListener);
        }
        super.onDestroy();
    }
}
