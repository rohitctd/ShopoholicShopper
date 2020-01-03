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
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.orderlistdetailsresponse.Result;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.BuddyHolder> {

    private Context mContext;
    private List<Result> ordersList;
    private RecyclerCallBack recyclerCallBack;

    public OrdersAdapter(Context mContext, List<Result> ordersList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.ordersList = ordersList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public BuddyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_orders, parent, false);
        return new BuddyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuddyHolder holder, int position) {
        AppUtils.getInstance().setImages(mContext, ordersList.get(position).getDealImage(), holder.ivOrderImage, 0, R.drawable.ic_placeholder);
        holder.tvOrderId.setText(ordersList.get(position).getOrderNumber());
        if (!ordersList.get(position).getDealName().equals("")) {
            holder.dealLabel.setVisibility(View.VISIBLE);
            holder.tvProduct.setVisibility(View.VISIBLE);
            holder.tvProduct.setText(ordersList.get(position).getDealName());
        } else {
            holder.dealLabel.setVisibility(View.GONE);
            holder.tvProduct.setVisibility(View.GONE);
        }
        holder.tvProvidedRating.setVisibility(View.GONE);
        holder.viewSeparator.setVisibility(View.GONE);
        holder.dealLabel.setText(mContext.getString(ordersList.get(position).getProductType().equals("1") ? R.string.product : R.string.service));
        switch (ordersList.get(position).getOrderStatus()) {
            case "1":
                holder.tvDealStatus.setText(R.string.status_pending);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                holder.tvOrderDeliveryDate.setText(TextUtils.concat(mContext.getString(R.string.order_placed_on) + " "
                        + AppUtils.getInstance().formatDate(ordersList.get(position).getOrderDate(),
                        Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.ORDER_DATE_FORMAT)));
                break;
            case "2":
                holder.tvDealStatus.setText(R.string.status_confirm);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                holder.tvOrderDeliveryDate.setText(TextUtils.concat(mContext.getString(R.string.order_placed_on) + " "
                        + AppUtils.getInstance().formatDate(ordersList.get(position).getOrderDate(),
                        Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.ORDER_DATE_FORMAT)));
                break;
            case "3":
                holder.tvDealStatus.setText(R.string.status_shipped);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                holder.tvOrderDeliveryDate.setText(TextUtils.concat(mContext.getString(R.string.order_placed_on) + " "
                        + AppUtils.getInstance().formatDate(ordersList.get(position).getOrderDate(),
                        Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.ORDER_DATE_FORMAT)));
                break;
            case "4":
                holder.tvDealStatus.setText(R.string.status_out_for_delivery);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                holder.tvOrderDeliveryDate.setText(TextUtils.concat(mContext.getString(R.string.order_placed_on) + " "
                        + AppUtils.getInstance().formatDate(ordersList.get(position).getOrderDate(),
                        Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.ORDER_DATE_FORMAT)));
                break;
            case "5":
                holder.tvDealStatus.setText(R.string.status_delivered);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                if (!ordersList.get(position).getIsRated().equals("1")) {
                    holder.tvProvidedRating.setVisibility(View.VISIBLE);
                    holder.viewSeparator.setVisibility(View.VISIBLE);
                }
                holder.tvOrderDeliveryDate.setText(TextUtils.concat(mContext.getString(R.string.delivered_on) + " "
                        + AppUtils.getInstance().formatDate(ordersList.get(position).getDileveredDate(),
                        Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.ORDER_DATE_FORMAT)));
                break;
            case "6":
                holder.tvDealStatus.setText(R.string.status_cancel);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                holder.tvOrderDeliveryDate.setText(TextUtils.concat(mContext.getString(R.string.order_placed_on) + " "
                        + AppUtils.getInstance().formatDate(ordersList.get(position).getOrderDate(),
                        Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.ORDER_DATE_FORMAT)));
                break;
            case "7":
                holder.tvDealStatus.setText(R.string.status_rejected);
                holder.tvDealStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                holder.tvOrderDeliveryDate.setText(TextUtils.concat(mContext.getString(R.string.order_placed_on) + " "
                        + AppUtils.getInstance().formatDate(ordersList.get(position).getOrderDate(),
                        Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.ORDER_DATE_FORMAT)));
                break;
        }
    }
    @Override
    public int getItemCount() {
        return ordersList.size();
    }


    class BuddyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_order_image)
        ImageView ivOrderImage;
        @BindView(R.id.tv_order_id)
        CustomTextView tvOrderId;
        @BindView(R.id.deal_label)
        CustomTextView dealLabel;
        @BindView(R.id.tv_product)
        CustomTextView tvProduct;
        @BindView(R.id.tv_order_delivery_date)
        CustomTextView tvOrderDeliveryDate;
        @BindView(R.id.tv_deal_status)
        CustomTextView tvDealStatus;
        @BindView(R.id.ll_root_view)
        RelativeLayout llRootView;
        @BindView(R.id.view_separator)
        View viewSeparator;
        @BindView(R.id.tv_provided_rating)
        CustomTextView tvProvidedRating;

        BuddyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.ll_root_view, R.id.tv_provided_rating})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.ll_root_view:
                    recyclerCallBack.onClick(getAdapterPosition(), llRootView);
                    break;
                case R.id.tv_provided_rating:
                    recyclerCallBack.onClick(getAdapterPosition(), tvProvidedRating);
                    break;
            }
        }
    }
}
