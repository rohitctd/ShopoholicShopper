package com.shopoholic.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopoholic.R;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KYCFragment extends Fragment implements NetworkListener {

    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.kyc_fragment, container, false);

        return rootView;
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {

    }

    @Override
    public void onError(String response, int requestCode) {

    }

    @Override
    public void onFailure() {

    }
}
