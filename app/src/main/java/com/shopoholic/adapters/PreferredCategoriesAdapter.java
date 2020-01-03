package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.activities.PreferredCategoriesActivity;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.preferredcategorymodel.Result;
import com.shopoholic.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Adapter to show category list
 */

public class PreferredCategoriesAdapter extends RecyclerView.Adapter<PreferredCategoriesAdapter.CategoryHolder> {
    private final Context mContext;
    private final List<Result> preferredCategoriesList;
    private final RecyclerCallBack recyclerCallBack;

    public PreferredCategoriesAdapter(Context mContext, List<Result> preferredCategoriesList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.preferredCategoriesList = preferredCategoriesList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_preferred_category, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        holder.tvCategory.setText(preferredCategoriesList.get(position).getCatName());
        if (position % 2 == 0) {
            holder.llRootView.setBackgroundResource(R.color.colorWhiteTransparent);
        } else {
            holder.llRootView.setBackgroundResource(android.R.color.transparent);
        }
        holder.cbCategory.setChecked(preferredCategoriesList.get(position).isSelected());
        if (mContext instanceof PreferredCategoriesActivity) {
            holder.cbCategory.setVisibility(View.VISIBLE);
            if (((PreferredCategoriesActivity)mContext).fromClass.equals(Constants.AppConstant.FILTER)){
                holder.cbCategory.setVisibility(View.GONE);
            }
        } else {
            holder.cbCategory.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return preferredCategoriesList.size();
    }

    /**
     * holder class
     */
    public class CategoryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_root_view)
        LinearLayout llRootView;
        @BindView(R.id.cb_category)
        CheckBox cbCategory;
        @BindView(R.id.tv_category)
        TextView tvCategory;

        CategoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ll_root_view)
        public void onViewClicked() {
            recyclerCallBack.onClick(getAdapterPosition(), llRootView);
        }
    }
}
