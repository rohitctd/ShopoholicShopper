package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.friendsresponse.Result;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsHolder> {
    private final RecyclerCallBack recyclerCallBack;
    private Context context;
    private List<Result> friendList;

    public FriendsAdapter(AppCompatActivity mActivity, List<Result> friendList, RecyclerCallBack recyclerCallBack) {
        context = mActivity;
        this.friendList = friendList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friends, parent, false);
        return new FriendsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsHolder holder, int position) {
        holder.tvFriendsName.setText(friendList.get(position).getContactUserName());
        holder.tvFriendsNo.setText(friendList.get(position).getMobile());
        AppUtils.getInstance().setCircularImages(context, friendList.get(position).getContactUserImage(), holder.ivFriendsImage, R.drawable.ic_side_menu_user_placeholder);
        if (friendList.get(position).getRequestStatus().equalsIgnoreCase("2")) {
            holder.tvRemove.setText(context.getString(R.string.remove));
            holder.tvRemove.setBackgroundResource(R.drawable.round_corner_reject_request);
            holder.tvRemove.setClickable(true);
        } else if (friendList.get(position).getRequestStatus().equalsIgnoreCase("1")) {
            holder.tvRemove.setText(context.getString(R.string.txt_pending));
            holder.tvRemove.setBackgroundResource(R.drawable.round_corner_white_stroke_transparent_bg);
            holder.tvRemove.setClickable(false);
        } else {
            holder.tvRemove.setText(context.getString(R.string.request));
            holder.tvRemove.setBackgroundResource(R.drawable.round_corner_blue_gradient_button_bg);
            holder.tvRemove.setClickable(true);
        }

    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


    public class FriendsHolder extends ViewHolder {
        @BindView(R.id.tv_status)
        CustomTextView tvRemove;
        @BindView(R.id.iv_friends_image)
        CircleImageView ivFriendsImage;
        @BindView(R.id.tv_friends_name)
        CustomTextView tvFriendsName;
        @BindView(R.id.tv_friends_no)
        CustomTextView tvFriendsNo;
        @BindView(R.id.rl_root_view)
        RelativeLayout rlRootView;

        FriendsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.tv_status)
        public void onViewClicked() {
            recyclerCallBack.onClick(getAdapterPosition(), tvRemove);
        }
    }
}
