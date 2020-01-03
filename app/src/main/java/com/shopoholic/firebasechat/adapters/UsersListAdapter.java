package com.shopoholic.firebasechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholic.firebasechat.models.UserBean;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter class to inflate user data row
 */

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserHolder> {

    private final Context mContext;
    private final List<UserBean> usersList;
    private final RecycleViewCallBack recycleViewCallBack;

    public UsersListAdapter(Context mContext, List<UserBean> usersList, RecycleViewCallBack recycleViewCallBack) {
        this.mContext = mContext;
        this.usersList = usersList;
        this.recycleViewCallBack = recycleViewCallBack;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_chat_time, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.tvName.setText(TextUtils.concat(usersList.get(position).getFirstName() + usersList.get(position).getLastName()));
        holder.tvEmail.setText(usersList.get(position).getEmail());

        if (usersList.get(position).isSelected()){
        }else {
            holder.rlParent.setBackgroundResource(android.R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlParent;
        private CircleImageView ivProfileImage;
        private final TextView tvName;
        private final TextView tvEmail;

        UserHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail= itemView.findViewById(R.id.tv_email);

            rlParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recycleViewCallBack.onClick(getAdapterPosition(), rlParent);
                }
            });
        }
    }
}
