package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.fragments.WalletPaidReceiveFragment;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.walletresponse.Result_;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Class created by Sachin on 30-May-18.
 */
public class WalletStatusAdapter extends RecyclerView.Adapter<WalletStatusAdapter.WalletHolder> {

    private WalletPaidReceiveFragment walletPaidReceiveFragment;
    private Context mContext;
    private List<Result_> walletStatusList;
    private RecyclerCallBack recyclerCallBack;

    public WalletStatusAdapter(Context mContext, WalletPaidReceiveFragment walletPaidReceiveFragment, List<Result_> walletStatusList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.walletStatusList = walletStatusList;
        this.walletPaidReceiveFragment = walletPaidReceiveFragment;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public WalletHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wallet, parent, false);
        return new WalletHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletHolder holder, int position) {
        holder.tvProductName.setText(walletStatusList.get(position).getDealName());
        holder.tvProductNumber.setText(walletStatusList.get(position).getOrderNumber());
        holder.tvOrderProductDate.setText(AppUtils.getInstance().formatDate(walletStatusList.get(position).getOrderDate(),
                Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT));

        holder.tvProductPrice.setText(walletPaidReceiveFragment.isPaidFragment ? walletStatusList.get(position).getActualAmount() :
                walletStatusList.get(position).getRefundedAmount());
        holder.tvProductPrice.setTextColor(ContextCompat.getColor(mContext, walletPaidReceiveFragment.isPaidFragment ? R.color.colorAccent : R.color.colorGreen));
        String image = walletStatusList.get(position).getDealId().equals("0") ? walletStatusList.get(position).getHuntImage() : walletStatusList.get(position).getDealImage();
        AppUtils.getInstance().setImages(mContext, image, holder.ivProductImage, 0, R.drawable.ic_placeholder);
        String country;
        switch (walletStatusList.get(position).getCurrencyCode()) {
            case "INR":
                country = mContext.getString(R.string.txt_india);
                break;
            case "HKD":
                country = mContext.getString(R.string.txt_hong_kong);
                break;
            case "SGD":
                country = mContext.getString(R.string.txt_singapore);
                break;
            case "MOP":
                country = mContext.getString(R.string.txt_macau);
                break;
            default:
                country = mContext.getString(R.string.txt_uae);
                break;
        }
        holder.tvOrderPlace.setText(TextUtils.concat(mContext.getString(R.string.order_in) + " " + country.toUpperCase()));
    }

    @Override
    public int getItemCount() {
        return walletStatusList.size();
    }


    class WalletHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_product_image)
        ImageView ivProductImage;
        @BindView(R.id.tv_product_name)
        CustomTextView tvProductName;
        @BindView(R.id.tv_product_number)
        CustomTextView tvProductNumber;
        @BindView(R.id.tv_order_product_date)
        CustomTextView tvOrderProductDate;
        @BindView(R.id.tv_order_place)
        CustomTextView tvOrderPlace;
        @BindView(R.id.tv_product_price)
        CustomTextView tvProductPrice;
        WalletHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }
}
