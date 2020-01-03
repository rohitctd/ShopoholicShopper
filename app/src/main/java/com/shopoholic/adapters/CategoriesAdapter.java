package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.fragments.HomeProductDealsFragment;
import com.shopoholic.fragments.HomePercentageDealsFragment;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.preferredcategorymodel.Result;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by appinventiv-pc on 2/4/18.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryHolder> {
    private final Context mContext;
    private final List<Result> categoryList;
    private final RecyclerCallBack recyclerCallBack;
    private final Fragment mFragment;

    public CategoriesAdapter(Context mContext, Fragment mFragment, List<Result> categoryList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.categoryList = categoryList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_item_category, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        if (mFragment instanceof HomeProductDealsFragment) {
            holder.tvCategoryName.setVisibility(View.VISIBLE);
            holder.ivCategoryImage.setVisibility(View.VISIBLE);
            holder.tvPercentage.setVisibility(View.GONE);
            holder.tvCategoryName.setText(AppUtils.getInstance().capitalizeString(categoryList.get(position).getCatName(), 0, 1));
            if (categoryList.get(position).isSelected()) {
                holder.ivCategoryImage.setBackgroundResource(R.drawable.round_corner_pink_bg);
//                holder.ivCategoryImage.setImageResource(R.drawable.ic_home_buddy_alll_selected);
                AppUtils.getInstance().setCircularImages(mContext, categoryList.get(position).getDarkImage(), holder.ivCategoryImage, R.drawable.ic_home_buddy_alll_selected);
            } else {
                holder.ivCategoryImage.setBackgroundResource(R.drawable.round_corner_grey_bg);
//                holder.ivCategoryImage.setImageResource(R.drawable.ic_home_buddy_alll_unselected);
                AppUtils.getInstance().setCircularImages(mContext, categoryList.get(position).getLightImage(), holder.ivCategoryImage, R.drawable.ic_home_buddy_alll_unselected);
            }
        } else if (mFragment instanceof HomePercentageDealsFragment) {
            if (position >= 0) {
                holder.tvCategoryName.setVisibility(View.GONE);
                holder.ivCategoryImage.setVisibility(View.GONE);
                holder.tvPercentage.setVisibility(View.VISIBLE);
                if (position == 0) {
                    holder.tvPercentage.setText(mContext.getString(R.string.all));
                }else {
                    holder.tvPercentage.setText(TextUtils.concat((100 - position * 10) + "%"));
                }
                if (categoryList.get(position).isSelected()) {
                    holder.tvPercentage.setBackgroundResource(R.drawable.round_corner_pink_bg);
                    holder.tvPercentage.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
                } else {
                    holder.tvPercentage.setBackgroundResource(R.drawable.round_corner_grey_bg);
                    holder.tvPercentage.setTextColor(ContextCompat.getColor(mContext, R.color.colorPercentageText));
                }
            } else  {
                holder.tvCategoryName.setVisibility(View.GONE);
                holder.ivCategoryImage.setVisibility(View.GONE);
                holder.tvPercentage.setVisibility(View.GONE);
                holder.ivCategoryImage.setImageResource(R.drawable.ic_shoppers_home_edit);
                holder.tvCategoryName.setText(mContext.getString(R.string.custom));
//                holder.rlRootView.setVisibility(View.GONE);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.rlRootView.getLayoutParams();
                layoutParams.width = RecyclerView.LayoutParams.WRAP_CONTENT;
                holder.rlRootView.setLayoutParams(layoutParams);
            }

        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_category_image)
        CircleImageView ivCategoryImage;
        @BindView(R.id.tv_category_name)
        CustomTextView tvCategoryName;
        @BindView(R.id.tv_percentage)
        CustomTextView tvPercentage;
        @BindView(R.id.rl_root_view)
        RelativeLayout rlRootView;

        CategoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.rl_root_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.rl_root_view:
                    recyclerCallBack.onClick(getAdapterPosition(), rlRootView);
                    break;
            }
        }
    }
}
