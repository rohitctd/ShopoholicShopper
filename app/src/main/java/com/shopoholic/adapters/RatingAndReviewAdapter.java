package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.ratingreviewresponse.Result;
import com.shopoholic.utils.AppUtils;
import com.whinc.widget.ratingbar.RatingBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RatingAndReviewAdapter extends RecyclerView.Adapter<RatingAndReviewAdapter.RatingAndReviewHolder> {

    private final RecyclerCallBack recyclerCallBack;
    private Context mContext;
    private ArrayList<Result> ratingList;

    public RatingAndReviewAdapter(Context mContext, ArrayList<Result> ratingList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.ratingList = ratingList;
        this.recyclerCallBack = recyclerCallBack;

    }

    @NonNull
    @Override
    public RatingAndReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rating_and_review, parent, false);
        return new RatingAndReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingAndReviewHolder holder, int position) {
        holder.tvBuddyName.setText(TextUtils.concat(ratingList.get(position).getFirstName() + " " + ratingList.get(position).getLastName()));
        holder.tvBuddyDescription.setText(ratingList.get(position).getReviews());
        holder.rbBuddyRating.setCount((int)(Float.parseFloat(ratingList.get(position).getRating())+ 0.5));
        holder.tvDate.setText(AppUtils.getInstance().formatDate(ratingList.get(position).getCreatedAt(), "yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy"));
        AppUtils.getInstance().setImages(mContext, ratingList.get(position).getImage(), holder.ivBuddyImage, 0 , R.drawable.ic_side_menu_user_placeholder);

        if (position % 2 != 0){
            holder.rlRootView.setBackgroundResource(android.R.color.transparent);
        }else {
            holder.rlRootView.setBackgroundResource(R.color.colorWhiteTransparent);
        }
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    class RatingAndReviewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rl_root_view)
        RelativeLayout rlRootView;
        @BindView(R.id.iv_buddy_image)
        CircleImageView ivBuddyImage;
        @BindView(R.id.tv_date)
        CustomTextView tvDate;
        @BindView(R.id.tv_buddy_name)
        CustomTextView tvBuddyName;
        @BindView(R.id.rb_buddy_rating)
        RatingBar rbBuddyRating;
        @BindView(R.id.tv_buddy_description)
        CustomTextView tvBuddyDescription;
        RatingAndReviewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
