package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.AddSlotsModel;
import com.shopoholic.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddSlotsAdapter extends RecyclerView.Adapter<AddSlotsAdapter.AddTimeSlotsHolder> {

    private Context mContext;
    private List<AddSlotsModel> slotList;
    private RecyclerCallBack recyclerCallBack;

    public AddSlotsAdapter(Context mContext, List<AddSlotsModel> slotList, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.slotList = slotList;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public AddTimeSlotsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_slots, parent, false);
        return new AddTimeSlotsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddTimeSlotsHolder holder, int position) {

        holder.tvDate.setText(slotList.get(position).getDateRange());
        holder.tvTime.setText(TextUtils.concat(slotList.get(position).getStartTime() + " - " + slotList.get(position).getEndTime()));
        holder.tvDuration.setText(AppUtils.getInstance().getTimeFromMillis(slotList.get(position).getDuration()));
        holder.stAllDay.setChecked(slotList.get(position).isAllDay());

        if (holder.stAllDay.isChecked()) {
            holder.tvTime.setVisibility(View.GONE);
            holder.tvDuration.setVisibility(View.GONE);
        } else  {
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.tvDuration.setVisibility(View.VISIBLE);
        }
        holder.etPrice.setText(slotList.get(position).getPrice());
        if (0 == position) {
            holder.tvDelete.setVisibility(View.GONE);
        }else{
            holder.tvDelete.setVisibility(View.VISIBLE);
        }
        holder.tvCurrency.setText(slotList.get(position).getCurrency());
        holder.tvCurrencyRight.setText(slotList.get(position).getCurrency());
        holder.tvCurrencyRight.setVisibility(View.GONE);

        if (slotList.get(position).getCurrencyCode().equalsIgnoreCase("AED")) {
            holder.tvCurrency.setVisibility(View.GONE);
            holder.tvCurrencyRight.setVisibility(View.VISIBLE);
        }else {
            holder.tvCurrency.setVisibility(View.VISIBLE);
            holder.tvCurrencyRight.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }


    public class AddTimeSlotsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_time)
        CustomTextView tvTime;
        @BindView(R.id.tv_date)
        CustomTextView tvDate;
        @BindView(R.id.tv_delete)
        CustomTextView tvDelete;
        @BindView(R.id.tv_duration)
        CustomTextView tvDuration;
        @BindView(R.id.et_price)
        CustomEditText etPrice;
        @BindView(R.id.ll_price)
        LinearLayout llPrice;
        @BindView(R.id.tv_currency)
        CustomTextView tvCurrency;
        @BindView(R.id.tv_currency_right)
        CustomTextView tvCurrencyRight;
        @BindView(R.id.st_all_day)
        Switch stAllDay;

        AddTimeSlotsHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            etPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    slotList.get(getAdapterPosition()).setPrice(etPrice.getText().toString().trim());
                }
            });

        }

        @OnClick({R.id.tv_time, R.id.tv_delete, R.id.tv_duration, R.id.st_all_day, R.id.ll_price})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.tv_time:
                    recyclerCallBack.onClick(getAdapterPosition(), tvTime);
                    break;
                case R.id.tv_delete:
                    recyclerCallBack.onClick(getAdapterPosition(), tvDelete);
                    break;
                case R.id.tv_duration:
                    recyclerCallBack.onClick(getAdapterPosition(), tvDuration);
                    break;
                case R.id.ll_price:
                    etPrice.requestFocus();
                    etPrice.setSelection(etPrice.getText().toString().length());
                    AppUtils.getInstance().showKeyboard(mContext);
                    break;
                case R.id.st_all_day:
                    if (slotList.size() > 1) {
                        AppUtils.getInstance().showToast(mContext, mContext.getString(R.string.cannot_select_all_day));
                        stAllDay.setChecked(false);
                    }else {
                        recyclerCallBack.onClick(getAdapterPosition(), stAllDay);
                    }
                    break;
            }
        }
    }
}
