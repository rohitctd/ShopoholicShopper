package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.huntbuddyresponse.Result;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;


public class HuntBuddyAdapter extends RecyclerView.Adapter<HuntBuddyAdapter.ProductHuntViewHolder> {

    private final RecyclerCallBack recyclerCallBack;
    private Context mContext;
    private List<Result> huntBuddyList;

    public HuntBuddyAdapter(Context mContext, List<Result> huntBuddyList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.huntBuddyList = huntBuddyList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public ProductHuntViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_posted, parent, false);
        return new ProductHuntViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductHuntViewHolder holder, final int position) {
        holder.tvDiscount.setText(TextUtils.concat(huntBuddyList.get(position).getFirstName() + " "
                + huntBuddyList.get(position).getLastName()));
        holder.tvPostedOn.setText(mContext.getString(R.string.delivery_date));
        holder.tvPostedDate.setText(AppUtils.getInstance().formatDate(huntBuddyList.get(position).getBuddyDeliveryDate(), SERVICE_DATE_FORMAT, DATE_FORMAT));
        holder.tvInstructions.setText(TextUtils.concat(mContext.getString(R.string.bidding_price) + " : " + huntBuddyList.get(position).getCurrencySymbol() +  huntBuddyList.get(position).getBidPrice()));
        if (huntBuddyList.get(position).getHuntImage() == null || huntBuddyList.get(position).getHuntImage().equals("")){
            holder.tvNoOfItems.setText(TextUtils.concat("0 " + mContext.getString(R.string.txt_images)));
            holder.ivProductImage.setImageResource(R.drawable.ic_placeholder);
        }else {
            holder.tvNoOfItems.setText(TextUtils.concat(huntBuddyList.get(position).getHuntImage().split(",").length + " " + mContext.getString(R.string.txt_images)));
            if (huntBuddyList.get(position).getHuntImage() != null && huntBuddyList.get(position).getHuntImage().split(",").length > 0) {
                AppUtils.getInstance().setImages(mContext, huntBuddyList.get(position).getHuntImage().split(",")[0], holder.ivProductImage, 0, R.drawable.ic_placeholder);
            }
        }
        holder.rlPostRow.setBackgroundColor(AppUtils.getInstance().getProductBackgroundColor(mContext, position % 5));
        holder.ivMenu.setVisibility(View.GONE);
        if (huntBuddyList.get(position).getServiceSlot() != null && huntBuddyList.get(position).getServiceSlot().size() > 0) {
            holder.ivShowSlots.setVisibility(View.VISIBLE);
            holder.tvPostedDate.setVisibility(View.GONE);
            holder.tvPostedOn.setVisibility(View.GONE);
        }else {
            holder.ivShowSlots.setVisibility(View.GONE);
            holder.tvPostedDate.setVisibility(View.VISIBLE);
            holder.tvPostedOn.setVisibility(View.VISIBLE);
        }

        holder.rlPostRow.setOnClickListener(v -> {
            recyclerCallBack.onClick(position, holder.rlPostRow);
        });
        holder.llItem.setOnClickListener(v -> {
            recyclerCallBack.onClick(position, holder.llItem);
        });
        holder.ivShowSlots.setOnClickListener(v -> {
            recyclerCallBack.onClick(position, holder.ivShowSlots);
        });
    }


    @Override
    public int getItemCount() {
        return huntBuddyList.size();
    }

    class ProductHuntViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_menu)
        ImageView ivMenu;
        @BindView(R.id.iv_product_image)
        ImageView ivProductImage;
        @BindView(R.id.iv_show_slots)
        ImageView ivShowSlots;
        @BindView(R.id.tv_no_of_items)
        TextView tvNoOfItems;
        @BindView(R.id.ll_item)
        RelativeLayout llItem;
        @BindView(R.id.tv_discount)
        TextView tvDiscount;
        @BindView(R.id.tv_instructions)
        TextView tvInstructions;
        @BindView(R.id.tv_posted_on)
        TextView tvPostedOn;
        @BindView(R.id.tv_posted_date)
        TextView tvPostedDate;
        @BindView(R.id.rl_post_row)
        RelativeLayout rlPostRow;

        ProductHuntViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }
}
