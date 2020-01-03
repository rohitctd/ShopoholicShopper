package com.shopoholic.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SlotsAdapter extends RecyclerView.Adapter<SlotsAdapter.BuddySlotsHolder> {

    private Context mContext;
    private List<ServiceSlot> slotsList;
    private RecyclerCallBack recyclerCallBack;

    public SlotsAdapter(Context mContext, List<ServiceSlot> slotsList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.slotsList = slotsList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public BuddySlotsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selection_view_white, parent, false);
        return new BuddySlotsHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull BuddySlotsHolder holder, int position) {
        boolean allday = slotsList.get(position).getAllDays().equals("1");
        String startTime = AppUtils.getInstance().formatDate(slotsList.get(position).getSlotStartTime(), "HH:mm:ss", "hh:mm a");
        String endTime = AppUtils.getInstance().formatDate(slotsList.get(position).getSlotEndTime(), "HH:mm:ss", "hh:mm a");
        String startDate = AppUtils.getInstance().formatDate(slotsList.get(position).getSlotStartDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
        String endDate = AppUtils.getInstance().formatDate(slotsList.get(position).getSlotEndDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
        if (allday) {
//            holder.textView.setText(TextUtils.concat(startDate + " - " + endDate + " (" + mContext.getString(R.string.all_day_available) + ")"));
            holder.textView.setText(mContext.getString(R.string.all_day_available));
        }else {
//        holder.textView.setText(TextUtils.concat(startDate + " " + startTime + " - " + endDate + " " + endTime));
        holder.textView.setText(TextUtils.concat(startTime + " - " + endTime));
        }
        if (!slotsList.get(position).isSelected()){
            holder.textView.setBackgroundResource(R.drawable.round_corner_white_bg);
        }else {
//            holder.textView.setBackgroundResource(R.drawable.round_corner_selected_bg);
            holder.textView.setBackgroundResource(R.drawable.round_corner_white_bg);
        }
        if (!slotsList.get(position).getIsAvailable().equals("1")){
            holder.textView.setBackgroundResource(R.drawable.round_corner_unavable_bg);
        }
    }

    @Override
    public int getItemCount() {
        return slotsList.size();
    }


    class BuddySlotsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view)
        CustomTextView textView;
        @BindView(R.id.fl_root_view)
        FrameLayout flRootView;

        BuddySlotsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.fl_root_view})
        public void onViewClicked(View view) {
            switch (view.getId()){
                case R.id.fl_root_view:
                    if (slotsList.get(getAdapterPosition()).getIsAvailable().equals("1")) {
                        recyclerCallBack.onClick(getAdapterPosition(), flRootView);
                    }
                    break;
            }
        }

    }
}
