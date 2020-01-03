package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.requestresponse.Result;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsAdapter extends Adapter<RequestsAdapter.RequestsHolder> {


    private Context mContext;
    private final List<Result> requestList;
    private final RecyclerCallBack recyclerCallBack;

    public RequestsAdapter(Context mContext, List<Result> requestList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.requestList = requestList;
        this.recyclerCallBack = recyclerCallBack;

    }

    @NonNull
    @Override
    public RequestsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_requests, parent, false);
        return new RequestsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsHolder holder, int position) {

        if (requestList.get(position).getContactUserImage() != null) {
            AppUtils.getInstance().setImages(mContext, requestList.get(position).getContactUserImage(), holder.ivRequestImage, 0, R.drawable.ic_side_menu_user_placeholder);
        } else {
            holder.ivRequestImage.setImageResource(R.drawable.ic_side_menu_user_placeholder);
        }
        if (requestList.get(position).getRequestedId().equals(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID))){
            holder.llAcceptReject.setVisibility(View.GONE);
            holder.tvRequestCancel.setVisibility(View.VISIBLE);
            holder.tvRequsetingUserName.setText(TextUtils.concat(mContext.getString(R.string.request_to)  + " "
                    + requestList.get(position).getContactUserName() + " "
                    + mContext.getString(R.string.is_pending)));
        }else {
            holder.llAcceptReject.setVisibility(View.VISIBLE);
            holder.tvRequestCancel.setVisibility(View.GONE);
            holder.tvRequsetingUserName.setText(TextUtils.concat(requestList.get(position).getContactUserName() + " "
                    + mContext.getString(R.string.wants_to_be_your_friend)));
        }

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }



    public class RequestsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_request_image)
        CircleImageView ivRequestImage;
        @BindView(R.id.tv_requseting_user_name)
        CustomTextView tvRequsetingUserName;
        @BindView(R.id.tv_request_cancel)
        CustomTextView tvRequestCancel;
        @BindView(R.id.rl_friend_name)
        RelativeLayout rlFriendName;
        @BindView(R.id.tv_request_accept)
        CustomTextView tvRequestAccept;
        @BindView(R.id.tv_request_reject)
        CustomTextView tvRequestReject;
        @BindView(R.id.ll_accept_reject)
        LinearLayout llAcceptReject;
        @BindView(R.id.rl_row_request)
        LinearLayout rlRowRequest;
        public RequestsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @OnClick({R.id.tv_request_cancel, R.id.tv_request_accept, R.id.tv_request_reject})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.tv_request_cancel:
                    recyclerCallBack.onClick(getAdapterPosition(), tvRequestCancel);
                    break;
                case R.id.tv_request_accept:
                    recyclerCallBack.onClick(getAdapterPosition(), tvRequestAccept);
                    break;
                case R.id.tv_request_reject:
                    recyclerCallBack.onClick(getAdapterPosition(), tvRequestReject);
                    break;
            }
        }
    }
}
