package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.productdealsresponse.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.Holder> {
    private final Context mContext;
    private final List<Result> productList;
    private final RecyclerCallBack recyclerCallBack;

    public LocationAdapter(Context mContext, List<Result> productList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.productList = productList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_location, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String name = "", address = "";
        int serviceType = R.drawable.ic_home_cards_bag_pink;
        if (productList.get(position).getUserType().equals("1")) {
            name = productList.get(position).getStoreName();
            address = productList.get(position).getStoreLocation();
            serviceType = R.drawable.ic_home_cards_bag_pink;
        } else {
            name = productList.get(position).getFirstName() + " " + productList.get(position).getLastName();
            address = productList.get(position).getBuddyAddress();
            serviceType = R.drawable.ic_home_buddy_service_pink;
        }
        holder.tvName.setText(name);
        holder.tvAddress.setText(address);
        holder.ivServiceType.setImageResource(serviceType);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        CustomTextView tvName;
        @BindView(R.id.tv_address)
        CustomTextView tvAddress;
        @BindView(R.id.iv_service_type)
        ImageView ivServiceType;
        @BindView(R.id.root_view)
        RelativeLayout rootView;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.root_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.root_view:
                    recyclerCallBack.onClick(getAdapterPosition(), rootView);
                    break;
            }
        }
    }
}
