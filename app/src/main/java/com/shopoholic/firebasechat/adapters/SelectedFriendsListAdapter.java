package com.shopoholic.firebasechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shopoholic.R;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedFriendsListAdapter extends RecyclerView.Adapter<SelectedFriendsListAdapter.AddParticipantHolder> {

    private Context context;
    private RecyclerCallBack recyclerCallBack;
    private List<UserBean> selectedFriendsList;


    public SelectedFriendsListAdapter(Context context, List<UserBean> selectedFriendsList, RecyclerCallBack recyclerCallBack) {
        this.context = context;
        this.selectedFriendsList = selectedFriendsList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public AddParticipantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_participants, parent, false);
        return new AddParticipantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddParticipantHolder holder, int position) {
        AppUtils.getInstance().setImages(context, selectedFriendsList.get(position).getUserImage(), holder.ivFriendImage, 0, R.drawable.ic_friend_placeholder);
    }

    @Override
    public int getItemCount() {
        return selectedFriendsList.size();
    }


    class AddParticipantHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_friend_image)
        CircleImageView ivFriendImage;
        @BindView(R.id.iv_remove)
        ImageView ivRemove;
        AddParticipantHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @OnClick(R.id.iv_remove)
        public void onViewClicked() {
            recyclerCallBack.onClick(getAdapterPosition(), ivRemove);
        }
    }
}
