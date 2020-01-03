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
import com.shopoholic.fragments.BlockDealsFragment;
import com.shopoholic.models.blockdealresponse.Result;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BlockDealsAdapter extends RecyclerView.Adapter<BlockDealsAdapter.BlockDealsViewHolder> {

    private final BlockDealsFragment blockDealsFragment;
    private Context mContext;
    private int i = 0;
    private List<Result> blockDealsList;

    public BlockDealsAdapter(Context mContext, List<Result> blockDealsList, BlockDealsFragment blockDealsFragment) {
        this.mContext = mContext;
        this.blockDealsList = blockDealsList;
        this.blockDealsFragment = blockDealsFragment;
    }

    @NonNull
    @Override
    public BlockDealsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new BlockDealsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_posted, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BlockDealsViewHolder holder, final int position) {
        holder.tvDiscount.setText(TextUtils.concat(blockDealsList.get(position).getDiscount() + mContext.getString(R.string.off_on) + " " + blockDealsList.get(position).getName()));
        holder.tvPostedDate.setText(AppUtils.getInstance().formatDate(blockDealsList.get(position).getPublishDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT));
        holder.tvInstructions.setText(blockDealsList.get(position).getDescription());
        if (blockDealsList.get(position).getDealImage() == null || blockDealsList.get(position).getDealImage().equals("")){
            holder.tvNoOfItems.setText(TextUtils.concat("0 " + mContext.getString(R.string.txt_images)));
            holder.ivProductImage.setImageResource(R.drawable.ic_placeholder);
        }else {
            holder.tvNoOfItems.setText(TextUtils.concat(blockDealsList.get(position).getDealImage().split(",").length + " " + mContext.getString(R.string.txt_images)));
            if (blockDealsList.get(position).getDealImage() != null && blockDealsList.get(position).getDealImage().split(",").length > 0) {
                AppUtils.getInstance().setImages(mContext, blockDealsList.get(position).getDealImage().split(",")[0], holder.ivProductImage, 0, R.drawable.ic_placeholder);
            }
        }
        holder.rlPostRow.setBackgroundColor(AppUtils.getInstance().getProductBackgroundColor(mContext, position % 5));

        holder.ivMenu.setOnClickListener(v -> {
            holder.ivMenu.setVisibility(View.GONE);
            showPopupWindow(holder.ivMenu, position, holder.rlPostRow);
        });
    }


    /**
     * method toshow te popup window
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
        TextView tvSold = popupView.findViewById(R.id.tv_sold_out);
        tvEdit.setText(mContext.getString(R.string.unblock));
        tvSold.setVisibility(View.GONE);
        tvEdit.setOnClickListener(v -> {
            blockDealsFragment.onPopupItemClick(position);
            popupWindow.dismiss();
        });
        int location[] = new int[2];
        // Get the View's(the one that was clicked in the Fragment) location
        mIvImage.getLocationOnScreen(location);

        float margingX = mContext.getResources().getDimension(R.dimen._70sdp);
        float margingY = mContext.getResources().getDimension(R.dimen._1sdp);


        int locationX = (int) (location[0] - margingX);
        int locationY = (int) (location[1] + margingY);
        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(mIvImage, Gravity.NO_GRAVITY, locationX, locationY);
        popupWindow.showAsDropDown(mIvImage);

    }

    @Override
    public int getItemCount() {
        return blockDealsList.size();
    }

    class BlockDealsViewHolder extends RecyclerView.ViewHolder {

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
        @BindView(R.id.tv_posted_on)
        TextView tvPostedOn;
        @BindView(R.id.tv_posted_date)
        TextView tvPostedDate;
        @BindView(R.id.rl_post_row)
        RelativeLayout rlPostRow;

        BlockDealsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }
}
