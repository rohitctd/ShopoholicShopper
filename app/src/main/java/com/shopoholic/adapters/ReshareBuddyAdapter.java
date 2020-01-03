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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.productservicedetailsresponse.BuddyArr;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class ReshareBuddyAdapter extends RecyclerView.Adapter<ReshareBuddyAdapter.ProductHuntViewHolder> {

    private RecyclerCallBack recyclerCallBack;
    private Context mContext;
    private List<BuddyArr> huntBuddyList;

    public ReshareBuddyAdapter(Context mContext, List<BuddyArr> huntBuddyList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.huntBuddyList = huntBuddyList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public ProductHuntViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_reshare, parent, false);
        return new ProductHuntViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductHuntViewHolder holder, final int position) {
        holder.tvName.setText(huntBuddyList.get(position).getBuddyName());
        AppUtils.getInstance().setImages(mContext, huntBuddyList.get(position).getImage(), holder.ivImage, 0, R.drawable.ic_side_menu_user_placeholder);
        holder.tvDeliveryCharges.setText(TextUtils.concat(mContext.getString(R.string.delivery_charges) + " : "
                + huntBuddyList.get(position).getCurrencySymbol() + huntBuddyList.get(position).getDeliveryCharge()));
    }


    @Override
    public int getItemCount() {
        return huntBuddyList.size();
    }


    class ProductHuntViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_image)
        CircleImageView ivImage;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_delivery_charges)
        TextView tvDeliveryCharges;
        @BindView(R.id.btn_contact)
        CustomTextView btnContact;
        @BindView(R.id.btn_buy)
        CustomTextView btnBuy;
        @BindView(R.id.ll_accept_reject)
        LinearLayout llAcceptReject;
        @BindView(R.id.rl_post_row)
        RelativeLayout rlPostRow;

        ProductHuntViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @OnClick({R.id.btn_contact, R.id.btn_buy, R.id.rl_post_row})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.btn_contact:
                    recyclerCallBack.onClick(getAdapterPosition(), btnContact);
                    break;
                case R.id.btn_buy:
                    recyclerCallBack.onClick(getAdapterPosition(), btnBuy);
                    break;
                case R.id.rl_post_row:
                    recyclerCallBack.onClick(getAdapterPosition(), rlPostRow);
                    break;
            }
        }

    }
}
