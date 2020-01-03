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

import com.daimajia.swipe.SwipeLayout;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.models.myfriendslistresponse.Result;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyFriendsListAdapter extends RecyclerView.Adapter<MyFriendsListAdapter.AddParticipantHolder> {

    private Context context;
    private RecyclerCallBack recyclerCallBack;
    private List<Result> friendsList;


    public MyFriendsListAdapter(Context context, List<Result> friendsList, RecyclerCallBack recyclerCallBack) {
        this.context = context;
        this.friendsList = friendsList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public AddParticipantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friends, parent, false);
        return new AddParticipantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddParticipantHolder holder, int position) {
        holder.tvStatus.setVisibility(View.GONE);
        holder.rlRootView.setBackgroundResource(!friendsList.get(position).isSelected() ? android.R.color.transparent : R.color.colorWhiteTransparent);
        holder.tvFriendsName.setText(TextUtils.concat(friendsList.get(position).getFirstName() + " " + friendsList.get(position).getLastName()));
        AppUtils.getInstance().setImages(context, friendsList.get(position).getImage(), holder.ivFriendsImage, 0, R.drawable.ic_friend_placeholder);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class AddParticipantHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_friends_image)
        CircleImageView ivFriendsImage;
        @BindView(R.id.tv_friends_name)
        CustomTextView tvFriendsName;
        @BindView(R.id.rl_root_view)
        RelativeLayout rlRootView;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        AddParticipantHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rl_root_view)
        public void onViewClicked() {
            recyclerCallBack.onClick(getAdapterPosition(), rlRootView);
        }
    }
}
