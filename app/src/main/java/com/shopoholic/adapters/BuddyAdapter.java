package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.activities.CommonActivity;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.buddylistresponse.Result;
import com.shopoholic.utils.AppUtils;
import com.whinc.widget.ratingbar.RatingBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BuddyAdapter extends RecyclerView.Adapter<BuddyAdapter.BuddyHolder> {

    private Context mContext;
    private List<Result> buddyList;
    private RecyclerCallBack recyclerCallBack;

    public BuddyAdapter(Context mContext, List<Result> buddyList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.buddyList = buddyList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public BuddyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_buddy, parent, false);
        return new BuddyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuddyHolder holder, int position) {
        holder.tvBuddyName.setText(TextUtils.concat(buddyList.get(position).getFirstName() + " " + buddyList.get(position).getLastName()));
        if( buddyList.get(position).getAddress() != null && buddyList.get(position).getAddress2() != null) {
            String address = buddyList.get(position).getAddress() + " " + buddyList.get(position).getAddress2();
            if (!address.trim().equals("")) {
                holder.tvBuddyAddress.setVisibility(View.VISIBLE);
                holder.tvBuddyAddress.setText(address);
            } else {
                holder.tvBuddyAddress.setVisibility(View.GONE);
            }
        }

        holder.rbBuddyRating.setCount((int)(Float.parseFloat(buddyList.get(position).getRating()) + 0.5));
        if (buddyList.get(position).getImage() != null) {
            AppUtils.getInstance().setImages(mContext, buddyList.get(position).getImage(), holder.ivBuddyImage, 0, R.drawable.ic_side_menu_user_placeholder);
        } else {
            holder.ivBuddyImage.setImageResource(R.drawable.ic_side_menu_user_placeholder);
        }

        holder.ivFollowBuddy.setImageResource(buddyList.get(position).getIsFollow().equals("1") ? R.drawable.ic_buddy_details_following : R.drawable.ic_buddy_details_follow);
        if (mContext instanceof HomeActivity){
            holder.tvBuddyRequest.setVisibility(View.GONE);
        } else if (mContext instanceof CommonActivity){
            holder.rlBuddyAssigned.setClickable(true);
            holder.tvBuddyRequest.setVisibility(View.VISIBLE);
            if (buddyList.get(position).getRequestStatus().equals("1")){
                holder.tvBuddyRequest.setText(mContext.getString(R.string.requested));
//                holder.tvBuddyRequest.setClickable(false);
            }else {
                holder.tvBuddyRequest.setText(mContext.getString(R.string.request));
//                holder.tvBuddyRequest.setClickable(true);
            }
        }


    }

    @Override
    public int getItemCount() {
        return buddyList.size();
    }


    public class BuddyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_buddy_image)
        ImageView ivBuddyImage;
        @BindView(R.id.tv_buddy_request)
        CustomTextView tvBuddyRequest;
        @BindView(R.id.tv_buddy_name)
        CustomTextView tvBuddyName;
        @BindView(R.id.rb_buddy_rating)
        RatingBar rbBuddyRating;
        @BindView(R.id.rl_buddy_assigned)
        RelativeLayout rlBuddyAssigned;
        @BindView(R.id.tv_buddy_address)
        CustomTextView tvBuddyAddress;
        @BindView(R.id.iv_follow_buddy)
        ImageView ivFollowBuddy;


        BuddyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @OnClick({R.id.tv_buddy_request, R.id.rl_buddy_assigned})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.tv_buddy_request:
                    recyclerCallBack.onClick(getAdapterPosition(),tvBuddyRequest);
                    break;
                case R.id.rl_buddy_assigned:
                    recyclerCallBack.onClick(getAdapterPosition(),rlBuddyAssigned);
                    break;
            }
        }


    }
}
