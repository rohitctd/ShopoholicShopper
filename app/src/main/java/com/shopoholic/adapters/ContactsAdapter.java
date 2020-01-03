package com.shopoholic.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.contactsresponse.Result;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsHolder> {

    private final RecyclerCallBack recyclerCallBack;
    private Context mContext;
    private final List<Result> contactList;

    public ContactsAdapter(AppCompatActivity mContext, List<Result> contactsList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.contactList = contactsList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public ContactsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contacts, parent, false);
        return new ContactsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsHolder holder, int position) {

        holder.tvContactName.setText(contactList.get(position).getContactUserName());
        holder.tvContactNo.setText(contactList.get(position).getMobile());
        if (contactList.get(position).getContactUserImage() != null) {
            AppUtils.getInstance().setImages(mContext, contactList.get(position).getContactUserImage(), holder.ivContactsImage, 0, R.drawable.ic_side_menu_user_placeholder);
        } else {
            holder.ivContactsImage.setImageResource(R.drawable.ic_side_menu_user_placeholder);
        }

        if (contactList.get(position).getRequestStatus().equalsIgnoreCase("0")) {
            holder.ivFriendContactAdd.setText(mContext.getString(R.string.invite));
            holder.ivFriendContactAdd.setClickable(true);
        } else {
            holder.ivFriendContactAdd.setText(mContext.getString(R.string.invited));
            holder.ivFriendContactAdd.setClickable(false);
        }

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_contacts_image)
        CircleImageView ivContactsImage;
        @BindView(R.id.tv_contact_name)
        CustomTextView tvContactName;
        @BindView(R.id.tv_contact_no)
        CustomTextView tvContactNo;
        @BindView(R.id.iv_friend_contact_add)
        CustomTextView ivFriendContactAdd;
        @BindView(R.id.rl_row_main)
        RelativeLayout rlRowMain;
        @BindView(R.id.tv_add)
        CustomTextView tvAdd;


        public ContactsHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);

        }


        @OnClick(R.id.iv_friend_contact_add)
        public void onViewClicked() {
            recyclerCallBack.onClick(getAdapterPosition(), ivFriendContactAdd);
            /*try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(mContext); // Need to change the build to API 19

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setData(Uri.parse("sms:" + contactList.get(getAdapterPosition()).getMobile()));
                    sendIntent.putExtra("address", contactList.get(getAdapterPosition()).getMobile());
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_app_message));

                    // Can be null in case that there is no default, then the user would be able to choose
                    // any app that support this intent.
                    if (defaultSmsPackageName != null) {
                        sendIntent.setPackage(defaultSmsPackageName);
                        mContext.startActivity(sendIntent);
                    } else {
                        AppUtils.getInstance().showToast(mContext, mContext.getString(R.string.unable_to_send_invite));
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("vnd.android-dir/mms-sms");
                    intent.putExtra("address", contactList.get(getAdapterPosition()).getMobile());
                    intent.putExtra("sms_body", mContext.getString(R.string.share_app_message));
                    mContext.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.getInstance().showToast(mContext, mContext.getString(R.string.unable_to_send_invite));
            }*/
        }
    }
}
