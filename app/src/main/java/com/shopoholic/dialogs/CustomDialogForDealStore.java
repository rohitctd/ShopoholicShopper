package com.shopoholic.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.shopoholic.R;
import com.shopoholic.adapters.LocationAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.LocationDialogCallback;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.productdealsresponse.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Dialog for image selection
 */

public class CustomDialogForDealStore extends Dialog {

    @BindView(R.id.tv_message_title)
    CustomTextView tvMessageTitle;
    @BindView(R.id.rv_address_list)
    RecyclerView rvAddressList;
    @BindView(R.id.tv_cancel)
    CustomTextView tvCancel;
    private List<Result> productList;
    private Context mContext;
    private LocationDialogCallback locationDialogCallback;
    private LocationAdapter mLocationAdapter;

    public CustomDialogForDealStore(Context mContext, List<Result> productList, LocationDialogCallback locationDialogCallback) {
        super(mContext);
        this.mContext = mContext;
        this.productList = productList;
        this.locationDialogCallback = locationDialogCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_deal_store);
        ButterKnife.bind(this);
        if (getWindow() != null) {
            setCancelable(true);
            setCanceledOnTouchOutside(true);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
        setAdapter();
    }

    /**
     * function to set adapter
     */
    private void setAdapter() {
        mLocationAdapter = new LocationAdapter(mContext, productList, (RecyclerCallBack) (position, view) -> {
            switch (view.getId()) {
                case R.id.root_view:
                    try {
                        double lati, longi;
                        String dealId;
                        Result product = productList.get(position);
                        if (product.getUserType().equals("1")) {
                            lati = Double.parseDouble(product.getStoreLatitude());
                            longi = Double.parseDouble(product.getStoreLongitude());
                            dealId = productList.get(position).getStoreId();
                        } else {
                            lati = Double.parseDouble(product.getBuddyLatitude());
                            longi = Double.parseDouble(product.getBuddyLongitude());
                            dealId = productList.get(position).getId();
                        }
                        locationDialogCallback.onClick(lati, longi, dealId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dismiss();
                    break;
            }
        });
        rvAddressList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvAddressList.setAdapter(mLocationAdapter);
    }

    @OnClick({R.id.tv_cancel, R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
