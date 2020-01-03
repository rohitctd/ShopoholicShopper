package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.activities.BuddySharePostDealActivity;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.fragments.HomeProductDealsFragment;
import com.shopoholic.fragments.HomePercentageDealsFragment;
import com.shopoholic.fragments.LauncherHomeDealsFragment;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.productdealsresponse.Result;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by appinventiv-pc on 2/4/18.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductHolder> {
    private Context mContext;
    private List<Result> productList;
    private RecyclerCallBack recyclerCallBack;
    private Fragment mFragment;

    public ProductsAdapter(Context mContext, Fragment mFragment, List<Result> productList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.productList = productList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_product, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        boolean isMapShow = false;
        if (mFragment instanceof HomeProductDealsFragment) {
            isMapShow = ((HomeProductDealsFragment) mFragment).isMapShow;
            holder.productStatus.setText(productList.get(position).getCategoryName());
        } else if (mFragment instanceof HomePercentageDealsFragment) {
            isMapShow = ((HomePercentageDealsFragment) mFragment).isMapShow;
            if (productList.get(position).getProductType().equals("2")) {
                holder.productStatus.setText("");
            }else {
                holder.productStatus.setText(TextUtils.concat(productList.get(position).getDiscount() + mContext.getString(R.string.per_off)));
            }
        }
        if (productList.get(position).getIsExpire().equals("1")) {
            holder.tvExpire.setVisibility(View.VISIBLE);
            holder.tvExpire.setText(mContext.getString(R.string.expire));
        }else {
            holder.tvExpire.setVisibility(holder.tvExpire.isShown() ? View.VISIBLE : View.GONE);
        }
        if (!productList.get(position).getIsExpire().equals("1") && (mFragment instanceof HomeProductDealsFragment || mFragment instanceof HomePercentageDealsFragment)) {
            if (!productList.get(position).getResharedCount().equals("") && !productList.get(position).getResharedCount().equals("0")) {
                holder.tvExpire.setVisibility(View.VISIBLE);
                holder.tvExpire.setText(TextUtils.concat(mContext.getString(R.string.reshared_buddy_count) + " " + productList.get(position).getResharedCount()));
            }else {
                holder.tvExpire.setVisibility(View.GONE);
            }
        }

        /**
         * Code For KYC Status check
         */

            if (productList.get(position).getKycStatus().equals("1") && (mFragment instanceof HomeProductDealsFragment || mFragment instanceof HomePercentageDealsFragment)){
                holder.kyc_status_overlay.setVisibility(View.GONE);
            }else{
                holder.kyc_status_overlay.setVisibility(View.VISIBLE);
            }


        if (mFragment instanceof LauncherHomeDealsFragment) {
            String type = ((LauncherHomeDealsFragment) mFragment).type;
            holder.productStatus.setText(type.equals(Constants.IntentConstant.CATEGORY) ?
                    AppUtils.getInstance().capitalizeString(productList.get(position).getCategoryName(), 0, 1)
                    : TextUtils.concat(productList.get(position).getDiscount() + mContext.getString(R.string.per_off)));
        }
        if (mContext instanceof BuddySharePostDealActivity) {
            holder.productStatus.setText(AppUtils.getInstance().capitalizeString(productList.get(position).getCategoryName(), 0, 1));
        }
        if (isMapShow) {
            CardView.LayoutParams layoutParams = (CardView.LayoutParams) holder.rlRootView.getLayoutParams();
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.width = (int) mContext.getResources().getDimension(R.dimen._120sdp);
            holder.rlRootView.setLayoutParams(layoutParams);
        } else {
            CardView.LayoutParams layoutParams = (CardView.LayoutParams) holder.rlRootView.getLayoutParams();
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            holder.rlRootView.setLayoutParams(layoutParams);
        }
//        holder.tvExpire.setVisibility(productList.get(position).getIsExpire().equals("1") ? View.VISIBLE : View.GONE);
        String dealImage = productList.get(position).getDealImage();
        dealImage = dealImage.contains(",") ? dealImage.split(",")[0] : dealImage;
        AppUtils.getInstance().setImages(mContext, dealImage, holder.ivProductPic, 0, R.drawable.ic_placeholder);
        AppUtils.getInstance().setCircularImages(mContext, productList.get(position).getImage(), holder.civUserImage, R.drawable.ic_side_menu_user_placeholder);
        holder.tvUserName.setText(TextUtils.concat(productList.get(position).getFirstName() + " " + productList.get(position).getLastName()));
        holder.tvDetails.setText(productList.get(position).getName());
        holder.llBottomView.setBackgroundColor(AppUtils.getInstance().getProductRandomColor(mContext, position));
        holder.ivServiceType.setImageResource(productList.get(position).getUserType()
                .equals("1") ? R.drawable.ic_home_cards_bag : R.drawable.ic_home_buddy_service_selected);
        holder.ivLike.setImageResource(productList.get(position).getIsFavourite().equals("1") ? R.drawable.ic_home_shoppers_like_fill : R.drawable.ic_shopper_home_like_unfill);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * function to set product list
     * @param list
     */
    public void setProductList(List<Result> list) {
        productList = list;
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_product_pic)
        ImageView ivProductPic;
        @BindView(R.id.iv_like)
        ImageView ivLike;
        @BindView(R.id.tv_details)
        CustomTextView tvDetails;
        @BindView(R.id.tv_expire)
        CustomTextView tvExpire;
        @BindView(R.id.civ_user_image)
        CircleImageView civUserImage;
        @BindView(R.id.fl_user_pic)
        FrameLayout flUserPic;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.iv_service_type)
        ImageView ivServiceType;
        @BindView(R.id.ll_bottom_view)
        LinearLayout llBottomView;
        @BindView(R.id.rl_root_view)
        LinearLayout rlRootView;
        @BindView(R.id.product_status)
        CustomTextView productStatus;
        @BindView(R.id.iv_kyc_coming_soon)
        ImageView kyc_status_overlay;

        ProductHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @OnClick({R.id.iv_like, R.id.civ_user_image, R.id.rl_root_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.iv_like:
                    recyclerCallBack.onClick(getAdapterPosition(), ivLike);
                    break;
                case R.id.civ_user_image:
                    recyclerCallBack.onClick(getAdapterPosition(), civUserImage);
                    break;
                case R.id.rl_root_view:
                    recyclerCallBack.onClick(getAdapterPosition(), ivProductPic);
                    break;
            }
        }
    }
}
