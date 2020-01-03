package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.walletresponse.WalletDetail;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.Holder> {
    private Context mContext;
    private ArrayList<WalletDetail> pointsList;
    private RecyclerCallBack recyclerCallBack;

    public PointsAdapter(Context mContext, ArrayList<WalletDetail> pointsList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.pointsList = pointsList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_points, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tvCountry.setText(TextUtils.concat(mContext.getString(R.string.available_points_for) + " " + pointsList.get(position).getCountry().toUpperCase()));
        holder.tvPoints.setText(pointsList.get(position).getPoints());
    }

    @Override
    public int getItemCount() {
        return pointsList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_country)
        CustomTextView tvCountry;
        @BindView(R.id.tv_points)
        CustomTextView tvPoints;
        @BindView(R.id.root_view)
        LinearLayout rootView;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @OnClick({R.id.root_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.root_view:
                    recyclerCallBack.onClick(getAdapterPosition(), rootView);
                    break;
            }
        }
    }
}
