package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shopoholic.fragments.ProductFragment;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

public class TimeSlotsAdapter extends RecyclerView.Adapter<TimeSlotsAdapter.AddTimeSlotsHolder> {


    private Fragment mFragment;
    private Context mContext;
    private List<ServiceSlot> list;
    private RecyclerCallBack recyclerCallBack;

    public TimeSlotsAdapter(Context mContext, Fragment mFragment, List<ServiceSlot> list, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.list = list;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public AddTimeSlotsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_time_slots, parent, false);
        return new AddTimeSlotsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddTimeSlotsHolder holder, int position) {

        String startDate = AppUtils.getInstance().formatDate(list.get(position).getSlotStartDate(), SERVICE_DATE_FORMAT, DATE_FORMAT);
        String endDate = AppUtils.getInstance().formatDate(list.get(position).getSlotEndDate(), SERVICE_DATE_FORMAT, DATE_FORMAT);

//        holder.tvStartTime.setText(TextUtils.concat(startDate + (list.get(position).isAllDay() ? "" : " " + list.get(position).getSlotStartTime())));
//        holder.tvEndTime.setText(TextUtils.concat(endDate + (list.get(position).isAllDay() ? "" : " " + list.get(position).getSlotEndTime())));

        holder.tvStartTime.setText(list.get(position).isAllDay() || (list.get(position).getAllDays() != null && list.get(position).getAllDays().equals("1"))  ? startDate : list.get(position).getSlotStartTime());
        holder.tvEndTime.setText(list.get(position).isAllDay() || (list.get(position).getAllDays() != null && list.get(position).getAllDays().equals("1")) ? endDate : list.get(position).getSlotEndTime());

        if (mFragment instanceof ProductFragment) {
            if (((ProductFragment)mFragment).fromClass.equals(Constants.AppConstant.EDIT_HUNT)) {
                holder.ivDeleteRow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AddTimeSlotsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_start_time)
        CustomTextView tvStartTime;
        @BindView(R.id.tv_end_time)
        CustomTextView tvEndTime;
        @BindView(R.id.ll_time_slots)
        LinearLayout llTimeSlots;
        @BindView(R.id.iv_delete_row)
        ImageView ivDeleteRow;

        AddTimeSlotsHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }


        @OnClick({R.id.tv_start_time, R.id.tv_end_time, R.id.iv_delete_row})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.tv_start_time:
//                    recyclerCallBack.onClick(getAdapterPosition(), tvStartTime);
                    break;
                case R.id.tv_end_time:
//                    recyclerCallBack.onClick(getAdapterPosition(),tvEndTime);
                    break;
                case R.id.iv_delete_row:
                    recyclerCallBack.onClick(getAdapterPosition(),ivDeleteRow);

                    break;
            }
        }

    }
}
