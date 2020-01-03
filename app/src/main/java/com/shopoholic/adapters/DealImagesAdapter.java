package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dnitinverma.amazons3library.model.ImageBean;
import com.shopoholic.R;
import com.shopoholic.fragments.ProductFragment;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Class created by Sachin on 29-Apr-18.
 */
public class DealImagesAdapter extends RecyclerView.Adapter<DealImagesAdapter.DealImagesHolder> {
    private final Context mContext;
    private final ArrayList<ImageBean> imagesBeanList;
    private final RecyclerCallBack recyclerCallBack;
    private final Fragment mFragment;


    public DealImagesAdapter(Context mContext, Fragment mFragment, ArrayList<ImageBean> imagesBeanList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.imagesBeanList = imagesBeanList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public DealImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_deal_images, parent, false);
        return new DealImagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealImagesHolder holder, int position) {
        AppUtils.getInstance().setImages(mContext, imagesBeanList.get(position).getImagePath(), holder.ivDealImage, 8, R.drawable.ic_placeholder);
        holder.ivDealImage.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
        holder.ivRemoveImage.setVisibility(imagesBeanList.get(position).isLoading() ? View.GONE : View.VISIBLE );
        if (mFragment instanceof ProductFragment) {
            if (((ProductFragment)mFragment).fromClass.equals(Constants.AppConstant.EDIT_HUNT)
                    && ((ProductFragment)mFragment).selectedImages.contains(imagesBeanList.get(position).getImagePath())) {
                holder.ivRemoveImage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return imagesBeanList.size();
    }

    class DealImagesHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_deal_image)
        ImageView ivDealImage;
        @BindView(R.id.iv_remove_image)
        ImageView ivRemoveImage;

        DealImagesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.iv_remove_image)
        public void onViewClicked() {
            recyclerCallBack.onClick(getAdapterPosition(), ivRemoveImage);
        }
    }
}
