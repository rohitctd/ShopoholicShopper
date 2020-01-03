package com.shopoholic.firebasechat.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.firebasechat.activities.GroupChatActivity;
import com.shopoholic.firebasechat.activities.SingleChatActivity;
import com.shopoholic.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholic.firebasechat.models.ChatMessageBean;
import com.shopoholic.firebasechat.models.MediaPlayerModel;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseChatUtils;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.utils.AppSharedPreference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter used to inflate single chat messages
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatHolder> {
    private static final int CHAT_MESSAGE_SEND = 101, CHAT_MESSAGE_RECEIVE = 102, CHAT_TIME = 103;
    private final Context mContext;
    private final List<ChatMessageBean> chatMessagesList;
    private final RecycleViewCallBack recycleViewCallBack;
    private final List<UserBean> usersList;
    public MediaPlayerModel playerModel;
    private boolean isFirst;
    private AsyncTask<Void, Void, Void> task;
    private boolean isLoading;

    public ChatListAdapter(Context mContext, List<ChatMessageBean> chatMessagesList, List<UserBean> usersList, RecycleViewCallBack recycleViewCallBack) {
        this.mContext = mContext;
        this.usersList = usersList;
        this.chatMessagesList = chatMessagesList;
        this.recycleViewCallBack = recycleViewCallBack;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == CHAT_TIME) {
            view = LayoutInflater.from(mContext).inflate(R.layout.row_chat_time, parent, false);
        } else if (viewType == CHAT_MESSAGE_SEND) {
            view = LayoutInflater.from(mContext).inflate(R.layout.row_chat_message_send, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.row_chat_message_receive, parent, false);
        }
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatHolder holder, final int position) {
        if (holder.ivUserImage != null) holder.ivUserImage.setVisibility(mContext instanceof SingleChatActivity ? View.GONE : View.VISIBLE);
        if (chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.CHAT_TIME) || chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.ACTION)) {
            holder.tvMessageText.setText(chatMessagesList.get(position).getMessageText());
        } else {
            try {
                holder.tvTime.setText(DateFormat.format("hh:mm a", Long.parseLong(chatMessagesList.get(position).getTimestamp().toString())));
            }catch (Exception e) {
                holder.tvTime.setText("");
            }
            if (holder.ivUserImage != null && usersList != null) {
                for (UserBean user : usersList) {
                    if (user.getUserId().equals(chatMessagesList.get(position).getSenderId())) {
                        if (mContext instanceof SingleChatActivity && !((SingleChatActivity) mContext).isDestroyed() ||
                                mContext instanceof GroupChatActivity && !((GroupChatActivity) mContext).isDestroyed())
                            FirebaseChatUtils.getInstance().setImageOnView(mContext, user.getUserImage(), holder.ivUserImage, 0, R.drawable.ic_side_menu_user_placeholder, null, true);
                        break;
                    }
                }
            }


            if (holder.tvResend != null) {
                if (chatMessagesList.get(position).getStatus().equalsIgnoreCase(FirebaseConstants.FAILED)) {
                    holder.tvResend.setVisibility(View.VISIBLE);
                }else {
                    holder.tvResend.setVisibility(View.GONE);
                }
            }

            if (chatMessagesList.get(position).getIsDeleted()) {
                holder.tvMessageText.setVisibility(View.VISIBLE);
                holder.cvMedia.setVisibility(View.GONE);
                holder.ivMessageImage.setVisibility(View.GONE);
                holder.viewOverlay.setVisibility(View.GONE);
                holder.ivPlayVideo.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
                if (holder.uploadLoader != null) holder.uploadLoader.setVisibility(View.GONE);
                if (holder.tvMessageStatus != null) holder.tvMessageStatus.setVisibility(View.GONE);
//                holder.tvMessageText.setText(mContext.getResources().getString(R.string.delete_message));
//                holder.tvMessageText.setTextColor(ContextCompat.getColor(mContext, R.color.colorDeleteMessage));
//                holder.tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_no_message, 0, 0, 0);

            } else if (chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.IMAGE)) {
                holder.tvMessageText.setVisibility(View.GONE);
                holder.cvMedia.setVisibility(View.VISIBLE);
                holder.ivMessageImage.setVisibility(View.VISIBLE);
                holder.viewOverlay.setVisibility(View.VISIBLE);
                holder.ivPlayVideo.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
                if (mContext instanceof SingleChatActivity && !((SingleChatActivity) mContext).isDestroyed() ||
                        mContext instanceof GroupChatActivity && !((GroupChatActivity) mContext).isDestroyed())
                    FirebaseChatUtils.getInstance().setImageOnView(mContext, chatMessagesList.get(position).getMediaUrl(), holder.ivMessageImage, 10, R.drawable.ic_placeholder, holder.progressBar, false);
                holder.ivMessageImage.setBackgroundResource(R.drawable.round_corner_grey_bg);

                if (holder.uploadLoader != null) {
                    if (chatMessagesList.get(position).getStatus().equalsIgnoreCase(FirebaseConstants.PENDING)) {
                        holder.uploadLoader.setVisibility(View.VISIBLE);
//                        if (holder.tvMessageStatus != null)
//                            holder.tvMessageStatus.setVisibility(View.INVISIBLE);
                    } else {
                        holder.uploadLoader.setVisibility(View.GONE);
//                        holder.tvMessageStatus.setVisibility(View.VISIBLE);
                    }
                }

            } else if (chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.LOCATION)) {
                holder.tvMessageText.setVisibility(View.GONE);
                holder.cvMedia.setVisibility(View.VISIBLE);
                holder.ivMessageImage.setVisibility(View.VISIBLE);
                holder.viewOverlay.setVisibility(View.VISIBLE);
                holder.ivPlayVideo.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
                if (mContext instanceof SingleChatActivity && !((SingleChatActivity) mContext).isDestroyed() ||
                        mContext instanceof GroupChatActivity && !((GroupChatActivity) mContext).isDestroyed())
                    FirebaseChatUtils.getInstance().setImageOnView(mContext, chatMessagesList.get(position).getMediaUrl(), holder.ivMessageImage, 10, R.drawable.ic_placeholder, holder.progressBar, false);
                holder.ivMessageImage.setBackgroundResource(R.drawable.round_corner_grey_bg);

                if (holder.uploadLoader != null) {
                    if (chatMessagesList.get(position).getStatus().equalsIgnoreCase(FirebaseConstants.PENDING)) {
                        holder.uploadLoader.setVisibility(View.VISIBLE);
                        holder.tvMessageStatus.setVisibility(View.INVISIBLE);
                    } else {
                        holder.uploadLoader.setVisibility(View.GONE);
                        holder.tvMessageStatus.setVisibility(View.VISIBLE);
                    }
                }

            } else if (chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.FILE) ||
                    chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.PDF) ||
                    chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.SHEET)) {
                holder.tvMessageText.setVisibility(View.GONE);
                holder.cvMedia.setVisibility(View.VISIBLE);
                holder.ivMessageImage.setVisibility(View.VISIBLE);
                holder.viewOverlay.setVisibility(View.VISIBLE);
                holder.ivPlayVideo.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
                if (mContext instanceof SingleChatActivity && !((SingleChatActivity) mContext).isDestroyed() ||
                        mContext instanceof GroupChatActivity && !((GroupChatActivity) mContext).isDestroyed())
//                    FirebaseChatUtils.getInstance().setImageOnView(mContext, chatMessagesList.get(position).getMediaUrl(), holder.ivMessageImage, 15, R.drawable.ic_chat_file, null, false);
                    holder.ivMessageImage.setImageResource(R.drawable.ic_chat_file);
                holder.ivMessageImage.setBackgroundResource(android.R.color.transparent);

                if (holder.uploadLoader != null) {
                    if (chatMessagesList.get(position).getStatus().equalsIgnoreCase(FirebaseConstants.PENDING)) {
                        holder.uploadLoader.setVisibility(View.VISIBLE);
                        holder.tvMessageStatus.setVisibility(View.INVISIBLE);
                    } else {
                        holder.uploadLoader.setVisibility(View.GONE);
                        holder.tvMessageStatus.setVisibility(View.VISIBLE);
                    }
                }

            } else if (chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.VIDEO)) {
                holder.tvMessageText.setVisibility(View.GONE);
                holder.cvMedia.setVisibility(View.VISIBLE);
                holder.ivMessageImage.setVisibility(View.VISIBLE);
                holder.viewOverlay.setVisibility(View.VISIBLE);
                holder.ivPlayVideo.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.VISIBLE);
                if (mContext instanceof SingleChatActivity && !((SingleChatActivity) mContext).isDestroyed() ||
                        mContext instanceof GroupChatActivity && !((GroupChatActivity) mContext).isDestroyed())
                    FirebaseChatUtils.getInstance().setImageOnView(mContext, chatMessagesList.get(position).getThumbnail(), holder.ivMessageImage, 10, R.drawable.ic_placeholder, holder.progressBar, false);
                holder.ivMessageImage.setBackgroundResource(R.drawable.round_corner_grey_bg);

                if (holder.uploadLoader != null) {
                    if (chatMessagesList.get(position).getStatus().equalsIgnoreCase(FirebaseConstants.PENDING)) {
                        holder.uploadLoader.setVisibility(View.VISIBLE);
                        holder.ivPlayVideo.setVisibility(View.GONE);
                        holder.tvMessageStatus.setVisibility(View.INVISIBLE);
                    } else {
                        holder.uploadLoader.setVisibility(View.GONE);
                        holder.ivPlayVideo.setVisibility(View.VISIBLE);
                        holder.tvMessageStatus.setVisibility(View.VISIBLE);
                    }
                }

//            } else if (chatMessagesList.get(position).getType().equals(FirebaseConstants.TEXT)) {
            } else {
                holder.tvMessageText.setVisibility(View.VISIBLE);
                holder.cvMedia.setVisibility(View.GONE);
                holder.ivMessageImage.setVisibility(View.GONE);
                holder.viewOverlay.setVisibility(View.GONE);
                holder.ivPlayVideo.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
                if (holder.uploadLoader != null) holder.uploadLoader.setVisibility(View.GONE);
                if (holder.tvMessageStatus != null)
                    holder.tvMessageStatus.setVisibility(View.VISIBLE);
                String message = chatMessagesList.get(position).getMessageText();
                holder.tvMessageText.setText(message.equals("") ? mContext.getString(R.string.unsupport_file) : message);
                holder.tvMessageText.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
                holder.tvMessageText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                Linkify.addLinks(holder.tvMessageText, Linkify.ALL);

            }

            if (holder.tvMessageStatus != null) {
                String status = chatMessagesList.get(position).getStatus();
                holder.tvMessageStatus.setText(mContext.getString(status.equals(FirebaseConstants.READ) ? R.string.seen : R.string.sent));
            }

            if (mContext instanceof SingleChatActivity && ((SingleChatActivity)mContext).isResume
                    && chatMessagesList.get(position).getReceiverId().equals(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID))) {
                FirebaseDatabaseQueries.getInstance().changeMessageStatus(chatMessagesList.get(position).getRoomId(), chatMessagesList.get(position).getMessageId());
            }
            setListeners(holder, holder.getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.CHAT_TIME) || chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.ACTION)) {
            return CHAT_TIME;
        } else {
            if (chatMessagesList.get(position).getSenderId().equals(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID))) {
                return CHAT_MESSAGE_SEND;
            } else {
                return CHAT_MESSAGE_RECEIVE;
            }
        }
    }

    /**
     * Method to set listeners on views
     *
     * @param holder
     * @param position
     */
    private void setListeners(final ChatHolder holder, final int position) {
        holder.llMessage.setOnClickListener(v -> {
            if (!chatMessagesList.get(position).getIsDeleted()) {
                recycleViewCallBack.onClick(position, holder.llMessage);
            }
        });
        holder.llMessageRow.setOnClickListener(v -> {
            if (!chatMessagesList.get(position).getIsDeleted()) {
                recycleViewCallBack.onClick(position, holder.llMessage);
            }
        });
        if (holder.ivPlayVideo != null && chatMessagesList.get(position).getType().equals(FirebaseConstants.VIDEO)) {
            holder.ivPlayVideo.setOnClickListener(v -> recycleViewCallBack.onClick(position, holder.ivPlayVideo));
        }
        if (holder.tvResend != null) {
            holder.tvResend.setOnClickListener(v -> recycleViewCallBack.onClick(position, holder.tvResend));
        }
        holder.llMessage.setOnLongClickListener(v -> {
            if (!chatMessagesList.get(position).getIsDeleted()) {
                recycleViewCallBack.onLongClick(position, holder.llMessageRow);
                return true;
            } else
                return false;
        });
        holder.llMessageRow.setOnLongClickListener(v -> {
            if (!chatMessagesList.get(position).getIsDeleted()) {
                recycleViewCallBack.onLongClick(position, holder.llMessageRow);
                return true;
            } else
                return false;
        });

    }

    class ChatHolder extends RecyclerView.ViewHolder {
        private final CircleImageView ivUserImage;
        private final TextView tvMessageText;
        private final ImageView ivMessageImage;
        private final TextView tvTime;
        private final LinearLayout llMessage;
        private final TextView tvMessageStatus;
        private final View viewOverlay;
        private final ImageView ivPlayVideo;
        private final ProgressBar progressBar;
        private final ProgressBar uploadLoader;
        private final TextView tvResend;
        private final LinearLayout llMessageRow;
        private final CardView cvMedia;

        ChatHolder(View itemView) {
            super(itemView);
            ivUserImage = itemView.findViewById(R.id.iv_user_image);
            tvMessageText = itemView.findViewById(R.id.tv_message_text);
            ivMessageImage = itemView.findViewById(R.id.iv_message_image);
            tvTime = itemView.findViewById(R.id.tv_time);
            llMessage = itemView.findViewById(R.id.ll_message);
            llMessageRow = itemView.findViewById(R.id.ll_message_row);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            viewOverlay = itemView.findViewById(R.id.view_overlay);
            ivPlayVideo = itemView.findViewById(R.id.iv_play_video);
            progressBar = itemView.findViewById(R.id.progress_bar);
            uploadLoader = itemView.findViewById(R.id.upload_loader);
            tvResend = itemView.findViewById(R.id.tv_resend);
            cvMedia = itemView.findViewById(R.id.cv_media);

        }
    }
}
