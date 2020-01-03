package com.shopoholic.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.bannerlistresponse.BannerArr;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by appinventiv-pc on 2/4/18.
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {
    private final Context mContext;
    private final List<BannerArr> bannerList;
    private final RecyclerCallBack recyclerCallBack;

    public BannerAdapter(Context mContext, List<BannerArr> bannerList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.bannerList = bannerList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_banner, parent, false);
        return new BannerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
        setImages(mContext, bannerList.get(position).getBannerImage(), holder.ivBanner, 5, R.drawable.ic_placeholder);
        if (bannerList.get(position).getDealId().equals("0")) {
            holder.tvStatus.setText(bannerList.get(position).getType().equals("1") ?
                    AppUtils.getInstance().capitalizeString(bannerList.get(position).getCategoryName(), 0, 1)
                    : bannerList.get(position).getPercentage() + mContext.getString(R.string.per_off));
        }else {
            holder.tvStatus.setText(AppUtils.getInstance().capitalizeString(bannerList.get(position).getDealName(), 0, 1));
        }
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }


    class BannerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_banner)
        ImageView ivBanner;
        @BindView(R.id.cv_root_view)
        CardView cvRootView;
        @BindView(R.id.tv_status)
        CustomTextView tvStatus;

        BannerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.iv_banner, R.id.cv_root_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.iv_banner:
                    recyclerCallBack.onClick(getAdapterPosition(), ivBanner);
                    break;
                case R.id.cv_root_view:
                    recyclerCallBack.onClick(getAdapterPosition(), cvRootView);
                    break;
            }
        }
    }


    /**
     * set image by glide
     *
     * @param context
     * @param imageUrl
     * @param imageView
     * @param radius
     * @param placeholder
     */
    public void setImages(final Context context, Object imageUrl, final ImageView imageView, final float radius, final int placeholder) {
        if (context != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .placeholder(placeholder)
                    .error(placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(new BitmapImageViewTarget(imageView) {

                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCornerRadius(radius);
                            imageView.setImageDrawable(circularBitmapDrawable);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                           super.onLoadFailed(e, errorDrawable);
                            imageView.setImageResource(placeholder);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    });
        }
    }
}
