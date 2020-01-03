package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholic.models.deliveryaddressresponse.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Class created by Sachin on 23-Apr-18.
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {
    private final Context mContext;
    private final List<Result> addressList;
    private final RecycleViewCallBack recycleViewCallBack;

    public AddressAdapter(Context mContext, List<Result> addressList, RecycleViewCallBack recycleViewCallBack) {
        this.mContext = mContext;
        this.addressList = addressList;
        this.recycleViewCallBack = recycleViewCallBack;
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_delivery_address, parent, false);
        return new AddressHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressHolder holder, int position) {
        holder.tvPurchaserName.setText(addressList.get(position).getName());
        holder.tvPurchaserAddress.setText(addressList.get(position).getAddress());
        holder.tvPurchaserPhoneNo.setText(TextUtils.concat(addressList.get(position).getCountryId() + " "
                + addressList.get(position).getMobileNo()));
        if (addressList.get(position).isSelected()){
            holder.ivSelectAddress.setImageResource(R.drawable.ic_checkmark_selected);
        }else {
            holder.ivSelectAddress.setImageResource(R.drawable.ic_checkmark_unselected);
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class AddressHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_purchaser_name)
        TextView tvPurchaserName;
        @BindView(R.id.tv_purchaser_address)
        TextView tvPurchaserAddress;
        @BindView(R.id.tv_purchaser_phone_no)
        TextView tvPurchaserPhoneNo;
        @BindView(R.id.iv_select_address)
        ImageView ivSelectAddress;
        @BindView(R.id.ll_root_view)
        LinearLayout llRootView;

        AddressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.ll_root_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.ll_root_view:
                    recycleViewCallBack.onClick(getAdapterPosition(), llRootView);
                    break;
            }
        }
    }
}
