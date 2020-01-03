package com.shopoholic.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.activities.ProductServiceDetailsActivity;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.utils.FirebaseChatUtils;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.NotificationBean;
import com.shopoholic.models.notificationresponse.Result;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.NOTIFICATION_DATE_FORMAT;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private Context mContext;
    private List<Result> notificationList;
    private RecyclerCallBack recyclerCallBack;

    public NotificationAdapter(Context mContext, List<Result> notificationList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.notificationList = notificationList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notifcation, parent, false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        String date = AppUtils.getInstance().utcToLocal(notificationList.get(position).getCreateDate(), NOTIFICATION_DATE_FORMAT, "dd/MM/yyyy hh:mm a");
//        holder.tvTime.setText(FirebaseChatUtils.getInstance().getDayDifference(mContext, date, DATE_FORMAT));
        holder.tvTime.setText(date);
        if (position % 2 == 0) {
            holder.rlNotification.setBackgroundResource(R.color.colorWhiteTransparent);
        } else {
            holder.rlNotification.setBackgroundResource(android.R.color.transparent);
        }
        switch (notificationList.get(position).getNotificationType()){
            case "1":
                holder.tvNotification.setText(TextUtils.concat(notificationList.get(position).getSenderName()
                        + " " + mContext.getString(R.string.addad_new_deal)));
                break;
            case "2":
                String message = "";
                switch (notificationList.get(position).getNotificationAction()) {
                    case "1":
                        break;
                    case "2":
                        message += notificationList.get(position).getSenderName() + " " + mContext.getString(R.string.confirm_order)
                                + " " + notificationList.get(position).getDealName();
                        break;
                    case "3":
                        message += mContext.getString(R.string.your_order_for) + " " + notificationList.get(position).getDealName()
                                + " " + mContext.getString(R.string.shipped_order);
                        break;
                    case "4":
                        message += mContext.getString(R.string.your_order_for) + " " + notificationList.get(position).getDealName()
                                + " " + mContext.getString(R.string.out_for_delivery_order);
                        break;
                    case "5":
                        message += mContext.getString(R.string.your_order_for) + " " + notificationList.get(position).getDealName()
                                + " " + mContext.getString(R.string.delivered_order);
                        break;
                    case "6":
                        message += notificationList.get(position).getSenderName() + " " + mContext.getString(R.string.canceled_order)
                                + " " + notificationList.get(position).getDealName();
                        break;
                    case "7":
                        message += notificationList.get(position).getSenderName() + " " + mContext.getString(R.string.reject_order)
                                + " " + notificationList.get(position).getDealName();
                        break;
                }
                holder.tvNotification.setText(message);
                break;
            case "3":
                String msg = "";
                switch (notificationList.get(position).getNotificationAction()) {
                    case "9":
                        msg += notificationList.get(position).getSenderName()
                                + " " + mContext.getString(R.string.accepted_hunt_request);
                        break;
                    case "10":
                        msg += notificationList.get(position).getSenderName()
                                + " " + mContext.getString(R.string.rejected_hunt_request);
                        break;
                    default:
                        msg += mContext.getString(R.string.no_nearby_buddy_available);
                        break;
                }
                holder.tvNotification.setText(msg);
                break;
            case "8":
                holder.tvNotification.setText(TextUtils.concat(mContext.getString(R.string.no_buddy_accept_request)
                        + " " + notificationList.get(position).getDealName()));
                break;
            case "11":
                holder.tvNotification.setText(TextUtils.concat(notificationList.get(position).getSenderName()
                                + " " + mContext.getString(R.string.send_friend_request)));
                break;
            case "12":
                holder.tvNotification.setText(TextUtils.concat(notificationList.get(position).getSenderName()
                        + " " + mContext.getString(R.string.accept_friend_request)));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_time)
        CustomTextView tvTime;
        @BindView(R.id.tv_notification)
        CustomTextView tvNotification;
        @BindView(R.id.rl_notification)
        RelativeLayout rlNotification;

        NotificationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rl_notification)
        public void onViewClicked() {
            recyclerCallBack.onClick(getAdapterPosition(), rlNotification);
        }


    }
}