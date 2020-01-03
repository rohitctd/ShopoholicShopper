package com.shopoholic.firebasechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholic.firebasechat.models.ChatMessageBean;
import com.shopoholic.firebasechat.models.ChatRoomBean;
import com.shopoholic.firebasechat.models.InboxMessageBean;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseChatUtils;
import com.shopoholic.firebasechat.utils.FirebaseConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter to inflate inbox list
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageHolder> {
    private final Context mContext;
    private final List<InboxMessageBean> messagesList;
    private final RecycleViewCallBack recycleViewCallBack;

    public MessageListAdapter(Context mContext, List<InboxMessageBean> messagesList, RecycleViewCallBack recycleViewCallBack) {
        this.mContext = mContext;
        this.messagesList = messagesList;
        this.recycleViewCallBack = recycleViewCallBack;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_message, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        if (messagesList.get(position).getChatLastMessageBean() != null) {
            ChatMessageBean message = messagesList.get(position).getChatLastMessageBean();
            holder.tvTime.setText(FirebaseChatUtils.getInstance().getTimeFromTimeStamp(mContext, messagesList.get(position).getChatLastMessageBean().getTimestamp().toString()));
            setMessageTime(holder, position);
            switch (message.getType()) {
                case FirebaseConstants.TEXT:
                case FirebaseConstants.ACTION:
                    holder.tvMessageText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    holder.tvMessageText.setText(message.getMessageText());
                    break;
                case FirebaseConstants.IMAGE:
//                    holder.tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_inbox_image, 0, 0, 0);
                    holder.tvMessageText.setText(FirebaseConstants.IMAGE);
                    break;
                case FirebaseConstants.LOCATION:
//                    holder.tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_inbox_location, 0, 0, 0);
                    holder.tvMessageText.setText(FirebaseConstants.LOCATION);
                    break;
                case FirebaseConstants.VIDEO:
//                    holder.tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_inbox_video, 0, 0, 0);
                    holder.tvMessageText.setText(FirebaseConstants.VIDEO);
                    break;
                case FirebaseConstants.FILE:
//                    holder.tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_inbox_file, 0, 0, 0);
                    holder.tvMessageText.setText(FirebaseConstants.FILE);
                    break;
                case FirebaseConstants.PDF:
//                    holder.tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pdf, 0, 0, 0);
                    holder.tvMessageText.setText(FirebaseConstants.PDF);
                    break;
            }
        }
        holder.tvMessageText.setTextColor(ContextCompat.getColor(mContext, messagesList.get(position).isSeen() ? R.color.colorHintText : R.color.colorAccent));
        if (messagesList.get(position).getUserBean() != null) {
            UserBean user = messagesList.get(position).getUserBean();
            holder.tvTitle.setText(TextUtils.concat(user.getFirstName() + " " + user.getLastName()));
            FirebaseChatUtils.getInstance().setImageOnView(mContext, user.getUserImage(), holder.civUserImage, 0, R.drawable.ic_side_menu_user_placeholder, null, true);
        }
        if (messagesList.get(position).getChatRoomBean() != null) {
            ChatRoomBean roomDetails = messagesList.get(position).getChatRoomBean();
            if (roomDetails.getChatRoomType().equals(FirebaseConstants.GROUP_CHAT)) {
                holder.tvTitle.setText(roomDetails.getChatRoomTitle());
                FirebaseChatUtils.getInstance().setImageOnView(mContext, roomDetails.getChatRoomPic(), holder.civUserImage, 0, R.drawable.ic_side_menu_user_placeholder, null, true);
            }
            if (roomDetails.getProduct() != null && roomDetails.getProduct().getName() != null){
                holder.tvProduct.setVisibility(View.VISIBLE);
                holder.tvProduct.setText(roomDetails.getProduct().getName());
            }else if (roomDetails.getHuntDeal() != null && roomDetails.getHuntDeal().getCategoryName() != null
                    && roomDetails.getHuntDeal().getSubCategoryName() != null){
                holder.tvProduct.setVisibility(View.VISIBLE);
                holder.tvProduct.setText(roomDetails.getHuntDeal().getHuntTitle());
//                holder.tvProduct.setText(TextUtils.concat(roomDetails.getHuntDeal().getSubCategoryName() + " " + mContext.getString(R.string.in)
//                        + " " + roomDetails.getHuntDeal().getCategoryName()));
            }else {
                holder.tvProduct.setVisibility(View.GONE);
            }
        }


    }


    /**
     * method to set time
     *
     * @param holder
     * @param position
     */
    private void setMessageTime(MessageHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.civ_user_image)
        CircleImageView civUserImage;
        @BindView(R.id.tv_title)
        CustomTextView tvTitle;
        @BindView(R.id.tv_product)
        CustomTextView tvProduct;
        @BindView(R.id.tv_message_text)
        CustomTextView tvMessageText;
        @BindView(R.id.ll_view)
        LinearLayout llView;
        @BindView(R.id.tv_time)
        CustomTextView tvTime;
        @BindView(R.id.rl_message_row)
        RelativeLayout rlMessageRow;

        MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rlMessageRow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recycleViewCallBack.onLongClick(getAdapterPosition(), rlMessageRow);
                    return false;
                }
            });

        }

        @OnClick({R.id.rl_message_row})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.rl_message_row:
                    recycleViewCallBack.onClick(getAdapterPosition(), rlMessageRow);
                    break;
            }
        }
    }
}
