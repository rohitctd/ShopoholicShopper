package com.shopoholic.adapters;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.shopoholic.R;
import com.shopoholic.activities.StoreListActivity;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.storelistresponse.Result;
import com.shopoholic.utils.AppUtils;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.StoresHolder> {

    private final Context mContext;
    private final List<Result> storesList;
    private final RecyclerCallBack recyclerCallBack;

    public StoresAdapter(Context mContext, List<Result> storesList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.storesList = storesList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public StoresHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_store, parent, false);
        return new StoresHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoresHolder holder, int position) {
        holder.tvMapDistance.setVisibility(View.GONE);
        holder.tvMerchantName.setText(storesList.get(position).getMerchantName());
        holder.tvStoreName.setText(storesList.get(position).getStoreName());
        holder.tvStoreAddress.setText(storesList.get(position).getStoreLocation());
        AppUtils.getInstance().setImages(mContext, storesList.get(position).getStoreImage(), holder.ivStoreImage, 5, R.drawable.ic_placeholder);
        String latitude = storesList.get(position).getStoreLatitude();
        String longitude = storesList.get(position).getStoreLongitude();
        if (latitude!= null && longitude != null && !latitude.equals("") && !longitude.equals("")) {
            LatLng storeLatLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            Location location = new Location("origin");
            location.setLatitude(storeLatLng.latitude);
            location.setLongitude(storeLatLng.longitude);
            if (mContext instanceof StoreListActivity && ((StoreListActivity) mContext).currentLocation != null) {
                float distanceInMeters = (((StoreListActivity)mContext).currentLocation).distanceTo(location);
                holder.tvMapDistance.setVisibility(View.VISIBLE);
                holder.tvMapDistance.setText(distanceInMeters > 1000 ? String.format(Locale.ENGLISH, "%.1f",
                        ((double) distanceInMeters / 1000)) + " " + mContext.getString(R.string.km) : (int) distanceInMeters + " " + mContext.getString(R.string.m));
            }
        }
        if (storesList.get(position).isSelected()){
            holder.cbStore.setChecked(true);
        }else {
            holder.cbStore.setChecked(false);
        }
        if (position % 2 == 0) {
            holder.rlRootView.setBackgroundResource(R.color.colorWhiteTransparent);
        } else {
            holder.rlRootView.setBackgroundResource(android.R.color.transparent);
        }

    }

    @Override
    public int getItemCount() {
        return storesList.size();
    }

    class StoresHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_store_image)
        ImageView ivStoreImage;
        @BindView(R.id.tv_map_distance)
        CustomTextView tvMapDistance;
        @BindView(R.id.tv_merchant_name)
        CustomTextView tvMerchantName;
        @BindView(R.id.tv_store_name)
        CustomTextView tvStoreName;
        @BindView(R.id.tv_store_address)
        CustomTextView tvStoreAddress;
        @BindView(R.id.rl_root_view)
        RelativeLayout rlRootView;
        @BindView(R.id.cb_store)
        CheckBox cbStore;

        StoresHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.rl_root_view, R.id.cb_store})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.cb_store:
                    recyclerCallBack.onClick(getAdapterPosition(), cbStore);
                    break;
                case R.id.rl_root_view:
                    recyclerCallBack.onClick(getAdapterPosition(), rlRootView);
                    break;
            }
        }
    }
}
