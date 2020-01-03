package com.shopoholic.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.shopoholic.R;
import com.shopoholic.activities.TimeSlotsActivity;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BuddySlotsAdapter extends RecyclerView.Adapter<BuddySlotsAdapter.BuddySlotsHolder> {

    private Context mContext;
    private List<ServiceSlot> buddyScheduleList;
    private RecyclerCallBack recyclerCallBack;

    public BuddySlotsAdapter(Context mContext, List<ServiceSlot> buddyScheduleList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.buddyScheduleList = buddyScheduleList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public BuddySlotsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_slots, parent, false);
        return new BuddySlotsHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull BuddySlotsHolder holder, int position) {
        String startTime, endTime;
        String startDate = AppUtils.getInstance().formatDate(buddyScheduleList.get(position).getSlotStartDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
        String endDate = AppUtils.getInstance().formatDate(buddyScheduleList.get(position).getSlotEndDate(), Constants.AppConstant.SERVICE_DATE_FORMAT, Constants.AppConstant.DATE_FORMAT);
        if (!buddyScheduleList.get(position).getAllDays().equals("1")) {
            startTime = AppUtils.getInstance().formatDate(buddyScheduleList.get(position).getSlotStartTime(), "HH:mm:ss", "hh:mm a");
            endTime = AppUtils.getInstance().formatDate(buddyScheduleList.get(position).getSlotEndTime(), "HH:mm:ss", "hh:mm a");
//            holder.tvDate.setText(TextUtils.concat(startDate + " " + startTime + " - " + endDate + " " + endTime));
            holder.tvDate.setText(TextUtils.concat(startTime + " - " + endTime));
        }else {
//            holder.tvDate.setText(TextUtils.concat(startDate + " - " + endDate + " (" + mContext.getString(R.string.all_day_available) + ")"));
            holder.tvDate.setText(mContext.getString(R.string.all_day_available));

        }
        holder.etPrice.setText(TextUtils.concat(((TimeSlotsActivity)mContext).currency + buddyScheduleList.get(position).getPrice()
                + " " + mContext.getString(R.string.per_day)));
        holder.stAllDay.setChecked(buddyScheduleList.get(position).getAllDays().equals("1"));
        if (buddyScheduleList.get(position).getIsAvailable().equals("1")) {
            if (buddyScheduleList.get(position).isSelected()) {
                holder.tvDate.setBackgroundResource(R.drawable.round_corner_pink_bg);
            } else {
                holder.tvDate.setBackgroundResource(R.drawable.round_corner_purple_stroke_transparent_bg);
            }
        } else {
            buddyScheduleList.get(position).setSelected(false);
            holder.tvDate.setBackgroundResource(R.drawable.round_corner_dark_grey_bg);
        }
    }

    @Override
    public int getItemCount() {
        return buddyScheduleList.size();
    }




    class BuddySlotsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date)
        CustomTextView tvDate;
        @BindView(R.id.et_price)
        CustomEditText etPrice;
        @BindView(R.id.st_all_day)
        Switch stAllDay;

        BuddySlotsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.tv_date, R.id.et_price})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.tv_date:
                    if (buddyScheduleList.get(getAdapterPosition()).getIsAvailable().equals("1")) {
                        recyclerCallBack.onClick(getAdapterPosition(), tvDate);
                    }
                    break;
            }
        }
    }
}
