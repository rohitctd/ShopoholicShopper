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
import com.shopoholic.fragments.ProductHuntListingFragment;
import com.shopoholic.models.producthuntlistresponse.Result;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;


public class ProductHuntAdapter extends RecyclerView.Adapter<ProductHuntAdapter.ProductHuntViewHolder> {

    private final ProductHuntListingFragment productHuntListingFragment;
    private Context mContext;
    private List<Result> productHuntList;

    public ProductHuntAdapter(Context mContext, List<Result> productHuntList, ProductHuntListingFragment productHuntListingFragment) {
        this.mContext = mContext;
        this.productHuntList = productHuntList;
        this.productHuntListingFragment = productHuntListingFragment;
    }

    @NonNull
    @Override
    public ProductHuntViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_posted, parent, false);
        return new ProductHuntViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductHuntViewHolder holder, final int position) {
/*
        holder.tvDiscount.setText(TextUtils.concat(productHuntList.get(position).getSubCatName() + " "
                + mContext.getString(R.string.in) + " " + productHuntList.get(position).getCategoryName()));
*/
        holder.tvDiscount.setText(productHuntList.get(position).getHuntTitle());
        holder.tvPostedOn.setText(TextUtils.concat(mContext.getString(R.string.expected_delivery_date) + " : "));
        holder.tvPostedDate.setText(AppUtils.getInstance().formatDate(productHuntList.get(position).getExpectedDeliveryDate(), SERVICE_DATE_FORMAT, DATE_FORMAT));
        holder.tvInstructions.setText(productHuntList.get(position).getDescription());
        if (!productHuntList.get(position).getBuddyAcceptCount().equals("") && Integer.parseInt(productHuntList.get(position).getBuddyAcceptCount()) > 0) {
            int buddyCount = Integer.parseInt(productHuntList.get(position).getBuddyAcceptCount());
            holder.tvBuddies.setVisibility(View.VISIBLE);
            holder.tvBuddies.setText(TextUtils.concat(productHuntList.get(position).getBuddyAcceptCount() + " "
                    + mContext.getString(buddyCount == 1 ? R.string.buddy_has : R.string.buddies_have) + " " + mContext.getString(R.string.accepted_request)));
        }else {
            holder.tvBuddies.setVisibility(View.GONE);
        }
        if (productHuntList.get(position).getHuntImage() == null || productHuntList.get(position).getHuntImage().equals("")){
            holder.tvNoOfItems.setText(TextUtils.concat("0 " + mContext.getString(R.string.txt_images)));
            holder.ivProductImage.setImageResource(R.drawable.ic_placeholder);
        }else {
            holder.tvNoOfItems.setText(TextUtils.concat(productHuntList.get(position).getHuntImage().split(",").length + " " + mContext.getString(R.string.txt_images)));
            if (productHuntList.get(position).getHuntImage() != null && productHuntList.get(position).getHuntImage().split(",").length > 0) {
                AppUtils.getInstance().setImages(mContext, productHuntList.get(position).getHuntImage().split(",")[0], holder.ivProductImage, 0, R.drawable.ic_placeholder);
            }
        }
        holder.rlPostRow.setBackgroundColor(AppUtils.getInstance().getProductBackgroundColor(mContext, position % 5));

        if (productHuntList.get(position).getProductType().equals("1")) {
            holder.tvPostedDate.setVisibility(View.VISIBLE);
            holder.tvPostedOn.setVisibility(View.VISIBLE);
        }else {
            holder.tvPostedDate.setVisibility(View.GONE);
            holder.tvPostedOn.setVisibility(View.GONE);
        }

        if (productHuntList.get(position).getBuddyId().equals("") ||
                productHuntList.get(position).getBuddyId().equals("0")) {
            holder.ivMenu.setVisibility(View.VISIBLE);
        }else {
            holder.ivMenu.setVisibility(View.GONE);
        }
        holder.ivMenu.setOnClickListener(v -> {
            holder.ivMenu.setVisibility(View.GONE);
            showPopupWindow(holder.ivMenu, position, holder.rlPostRow);
        });
        holder.rlPostRow.setOnClickListener(v -> {
            productHuntListingFragment.openBuddyListActivity(position);
        });


        if (productHuntList.get(position).getBuddyAcceptCount().equals("0")) {
            if (productHuntList.get(position).getIsRead().equals("1")) {
                holder.ivIcon.setVisibility(View.INVISIBLE);
            } else {
                if (productHuntList.get(position).getNotificationAction().equals("15")) {
                    holder.ivIcon.setVisibility(View.VISIBLE);
                } else
                    holder.ivIcon.setVisibility(View.INVISIBLE);
            }
        }else {
            if (productHuntList.get(position).getIsRead().equals("1")) {
                holder.ivIcon.setVisibility(View.INVISIBLE);
            } else {
                holder.ivIcon.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * method to show te popup window
     * @param mIvImage
     * @param position
     * @param mRlPost
     */
    private void showPopupWindow(final ImageView mIvImage, final int position, final RelativeLayout mRlPost) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_row_appointment_option, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(() -> mIvImage.setVisibility(View.VISIBLE));

        TextView tvEdit = popupView.findViewById(R.id.tv_edit);
        TextView tvDelete = popupView.findViewById(R.id.tv_sold_out);
        tvDelete.setText(mContext.getString(R.string.delete));
//        tvDelete.setVisibility(View.GONE);
        tvEdit.setOnClickListener(v -> {
            productHuntListingFragment.onPopupItemClick(1, position);
            popupWindow.dismiss();
        });
        tvDelete.setOnClickListener(v -> {
            productHuntListingFragment.onPopupItemClick(2, position);
            popupWindow.dismiss();
        });
        int location[] = new int[2];
        // Get the View's(the one that was clicked in the Fragment) location
        mIvImage.getLocationOnScreen(location);

        float margingX = mContext.getResources().getDimension(R.dimen._30sdp);
        float margingY = mContext.getResources().getDimension(R.dimen._1sdp);


        int locationX = (int) (location[0] - margingX);
        int locationY = (int) (location[1] + margingY);
        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(mIvImage, Gravity.NO_GRAVITY, locationX, locationY);
        popupWindow.showAsDropDown(mIvImage);

    }

    @Override
    public int getItemCount() {
        return productHuntList.size();
    }

    class ProductHuntViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_menu)
        ImageView ivMenu;
        @BindView(R.id.iv_product_image)
        ImageView ivProductImage;
        @BindView(R.id.tv_no_of_items)
        TextView tvNoOfItems;
        @BindView(R.id.ll_item)
        RelativeLayout llItem;
        @BindView(R.id.tv_discount)
        TextView tvDiscount;
        @BindView(R.id.tv_instructions)
        TextView tvInstructions;
        @BindView(R.id.tv_buddies)
        TextView tvBuddies;
        @BindView(R.id.tv_posted_on)
        TextView tvPostedOn;
        @BindView(R.id.tv_posted_date)
        TextView tvPostedDate;
        @BindView(R.id.rl_post_row)
        RelativeLayout rlPostRow;
        @BindView(R.id.iv_icon)
        ImageView ivIcon;

        ProductHuntViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }
}
