package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.MenuItemModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class SideMenuAdapter extends RecyclerView.Adapter<SideMenuAdapter.MenuHolder> {
    private final Context mContext;
    private final List<MenuItemModel> sideMenuList;
    private final RecyclerCallBack recyclerCallBack;
    public SideMenuAdapter(Context mContext, List<MenuItemModel> sideMenuList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.sideMenuList = sideMenuList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_side_menu_item, parent, false);
        return new MenuHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder holder, int position) {
        holder.menuName.setText(sideMenuList.get(position).getItemName());
        int paddingHorizontal = (int) mContext.getResources().getDimension(R.dimen._8sdp);
        int paddingVertical = (int) mContext.getResources().getDimension(R.dimen._15sdp);
        if (sideMenuList.get(position).isSelected()){
            holder.menuName.setCompoundDrawablesWithIntrinsicBounds(sideMenuList.get(position).getSelectedResourceId(), 0, 0, 0);
            holder.menuName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            holder.menuName.setPadding(paddingHorizontal, paddingHorizontal, paddingVertical, paddingHorizontal);
            holder.menuItemRoot.setBackgroundResource(R.drawable.selected_menu_item_view_shadow);
        }else {
            holder.menuName.setCompoundDrawablesWithIntrinsicBounds(sideMenuList.get(position).getDeselectedResourceId(), 0, 0, 0);
            holder.menuName.setTextColor(ContextCompat.getColor(mContext, R.color.colorMessageText));
            holder.menuName.setPadding((int) mContext.getResources().getDimension(R.dimen._12sdp), paddingHorizontal, paddingVertical, paddingHorizontal);
            holder.menuItemRoot.setBackgroundResource(android.R.color.transparent);
        }

        if (position == 8 && mContext instanceof HomeActivity && ((HomeActivity)mContext).unreadHunt > 0) {
            holder.ivUnread.setVisibility(View.VISIBLE);
        } else if (position == 5 && mContext instanceof HomeActivity && ((HomeActivity)mContext).unreadChat > 0) {
            holder.ivUnread.setVisibility(View.VISIBLE);
        }else {
            holder.ivUnread.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sideMenuList.size();
    }

    class MenuHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fl_menu_item_root)
        FrameLayout menuItemRoot;
        @BindView(R.id.tv_title)
        TextView menuName;
        @BindView(R.id.iv_unread)
        ImageView ivUnread;
        MenuHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.tv_title)
        public void onViewClicked() {
            recyclerCallBack.onClick(getAdapterPosition(), menuName);
        }
    }
}
