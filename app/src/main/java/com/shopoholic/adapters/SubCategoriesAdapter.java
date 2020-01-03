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
import com.shopoholic.models.subcategoryresponse.Result;
import com.shopoholic.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Adapter to show category list
 */

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.CategoryHolder> {
    private final Context mContext;
    private final List<Result> subCategoriesList;
    private final RecyclerCallBack recyclerCallBack;

    public SubCategoriesAdapter(Context mContext, List<Result> subCategoriesList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.subCategoriesList = subCategoriesList;
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
        holder.tvCategory.setText(subCategoriesList.get(position).getSubCatName());
        if (position % 2 == 0) {
            holder.llRootView.setBackgroundResource(R.color.colorWhiteTransparent);
        } else {
            holder.llRootView.setBackgroundResource(android.R.color.transparent);
        }
        holder.cbCategory.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return subCategoriesList.size();
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
