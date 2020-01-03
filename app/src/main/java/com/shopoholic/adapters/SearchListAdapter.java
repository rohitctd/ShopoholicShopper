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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Class created by Sachin on 26-Apr-18.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchHolder> {
    private final Context mContext;
    private final List<String> searchList;
    private final RecyclerCallBack recyclerCallBack;


    public SearchListAdapter(Context mContext, List<String> searchList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.searchList = searchList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_spinner_text_view, parent, false);
        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        holder.textView.setText(searchList.get(position));
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }


    class SearchHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view)
        CustomTextView textView;

        SearchHolder(View itemView) {
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