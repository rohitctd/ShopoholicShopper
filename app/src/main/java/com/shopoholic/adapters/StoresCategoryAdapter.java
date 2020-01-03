package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.storelistresponse.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by appinventiv-pc on 2/4/18.
 */

public class StoresCategoryAdapter extends RecyclerView.Adapter<StoresCategoryAdapter.CategoryHolder> {
    private final Context mContext;
    private final List<Object> storeCategoryList;
    private final RecyclerCallBack recyclerCallBack;


    public StoresCategoryAdapter(Context mContext, List<Object> storeCategoryList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.storeCategoryList = storeCategoryList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_spinner_text_view, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        if (storeCategoryList.get(position) instanceof Result){
            holder.textView.setText(((Result) storeCategoryList.get(position)).getStoreName());
        }else if (storeCategoryList.get(position) instanceof com.shopoholic.models.preferredcategorymodel.Result){
            holder.textView.setText(((com.shopoholic.models.preferredcategorymodel.Result) storeCategoryList.get(position)).getCatName());
        }
    }

    @Override
    public int getItemCount() {
        return storeCategoryList.size();
    }


    class CategoryHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view)
        CustomTextView textView;

        CategoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.text_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.text_view:
                    recyclerCallBack.onClick(getAdapterPosition(), textView);
                    break;
            }
        }

    }
}
