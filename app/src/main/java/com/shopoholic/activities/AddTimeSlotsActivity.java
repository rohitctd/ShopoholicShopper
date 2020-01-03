package com.shopoholic.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TimePicker;

import com.shopoholic.R;
import com.shopoholic.adapters.AddSlotsAdapter;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.AddSlotsModel;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;

import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

public class AddTimeSlotsActivity extends BaseActivity {
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;

    private ArrayList<AddSlotsModel> slotsList;
    private AddSlotsAdapter addSlotsAdapter;
    private String startDate, endDate;
    private boolean allDay = false;
    private String currency = "د.إ";
    private String currencyCode = "AED";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_slots);
        ButterKnife.bind(this);
        initVariables();
        setAdapters();
        getDataAndSetValues();
    }

    /**
     * method to set adapters in views
     */
    private void setAdapters() {
        recycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycleView.setAdapter(addSlotsAdapter);
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        slotsList = new ArrayList<>();
        progressBar.setVisibility(View.GONE);
        tvTitle.setText(getString(R.string.add_time_slots));
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setImageResource(R.drawable.ic_check_green);
        menuSecondRight.setImageResource(R.drawable.ic_add);
        menuRight.setVisibility(View.VISIBLE);
        menuSecondRight.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);
        addSlotsAdapter = new AddSlotsAdapter(this, slotsList, (position, view) -> {
            switch (view.getId()) {
                case R.id.tv_time:
                    setTime(position);
                    break;
                case R.id.tv_delete:
                    if (slotsList.size() > position) {
                        slotsList.remove(position);
                        addSlotsAdapter.notifyItemRemoved(position);
                        addSlotsAdapter.notifyItemRangeChanged(position, slotsList.size());
                    }
                    break;
                case R.id.tv_duration:
                    setDuration(position);
                    break;
                case R.id.st_all_day:
                    slotsList.get(position).setAllDay(((Switch) view).isChecked());
                    allDay = slotsList.get(position).isAllDay();
                    addSlotsAdapter.notifyItemChanged(position);
                    if (allDay) {
                        menuSecondRight.setVisibility(View.GONE);
                    }else {
                        menuSecondRight.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        });
    }

    /**
     * method to set duration
     * @param position
     */
    private void setDuration(final int position) {
        final Dialog dialog = new Dialog(this, R.style.DatePickerTheme);
        if(dialog.getWindow()!=null)
        {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        }
        dialog.setContentView(R.layout.dialog_duration);
        final CustomTextView ok = dialog.findViewById(R.id.ok);
        final CustomTextView cancel = dialog.findViewById(R.id.cancel);
        final NumberPicker hourPicker = dialog.findViewById(R.id.hour_picker);
        final NumberPicker minutesPicker = dialog.findViewById(R.id.minutes_picker);
        String[] numbers = new String[4];
        for(int i = 0; 4 > i; i++) {
            numbers[i] = (0 == i ? "0" : "") + (i * 15);
        }
        hourPicker.setMaxValue(7); // max value 7
        hourPicker.setMinValue(1);   // min value 0
        minutesPicker.setMaxValue(3);
        minutesPicker.setMinValue(0);
        minutesPicker.setDisplayedValues(numbers);
        hourPicker.setWrapSelectorWheel(false);
        minutesPicker.setWrapSelectorWheel(false);
        ok.setOnClickListener(v -> {
            slotsList.get(position).setDuration(AppUtils.getInstance().getMillisecondsTime
                    ((String.valueOf(hourPicker.getValue()) + ":" + String.valueOf(minutesPicker.getValue() * 15))));
            addSlotsAdapter.notifyItemChanged(position);
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss(); // dismiss the dialog
        });
        dialog.show();
    }


    // set time slots
    private void setTime(final int position) {
        final Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddTimeSlotsActivity.this, R.style.DatePickerTheme, (timePicker, selectedHour, selectedMinute) -> {

            int hour1 = selectedHour;
            String timeSet;
            if (12 < hour1) {
                hour1 -= 12;
                timeSet = "PM";
            } else if (0 == hour1) {
                hour1 += 12;
                timeSet = "AM";
            } else if (12 == hour1) {
                timeSet = "PM";
            } else {
                timeSet = "AM";
            }

            String min = "";
            if (10 > selectedMinute)
                min = "0" + selectedMinute;
            else
                min = String.valueOf(selectedMinute);

            // Append in a StringBuilder
            String aTime = String.valueOf(hour1) + ':' + min + " " + timeSet;
            slotsList.get(position).setStartTime(aTime);
            addSlotsAdapter.notifyItemChanged(position);

        }, hour, minute, false);
        mTimePicker.show();
    }

    @OnClick({R.id.iv_menu, R.id.menu_right, R.id.menu_second_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.menu_right:
                for (int i=0; i<slotsList.size(); i++) {
                    if (null == slotsList.get(i) || slotsList.get(i).getPrice().equals("")) {
                        AppUtils.getInstance().showToast(AddTimeSlotsActivity.this, getString(R.string.please_enter_price));
                        return;
                    }
                }
                boolean isOverlap = false;
                outerloop:
                for (int i = 0; i < slotsList.size(); i++) {
                    for (int j = 0; j < slotsList.size(); j++) {
                        if (i != j) {
                            if (!AppUtils.getInstance().checkTime(
                                    slotsList.get(i).getStartTime() + " - "
                                            + slotsList.get(i).getEndTime(),
                                    slotsList.get(j).getStartTime() + " - "
                                            + slotsList.get(j).getEndTime())) {
                                isOverlap = true;
                                break outerloop;
                            }
                        }
                    }
                }
                if (isOverlap) {
                    AppUtils.getInstance().showToast(this, getString(R.string.entered_slots_overlaping));
                    return;
                }
                Intent data = new Intent();
                data.putExtra(Constants.IntentConstant.TIME_SLOTS, slotsList);
                setResult(RESULT_OK, data);
                finish();
                break;
            case R.id.menu_second_right:

                if (0 < slotsList.size()) {
                    AddSlotsModel slot = slotsList.get(slotsList.size() - 1);
                    if (null != slot && !slot.getPrice().equals("")) {
                        if (allDay) {
                            AppUtils.getInstance().showToast(AddTimeSlotsActivity.this, getString(R.string.no_time_available));
                            return;
                        }
                        AddSlotsModel slotsModel = new AddSlotsModel();
                        slotsModel.setDateRange(TextUtils.concat(startDate + (startDate.equals(endDate) ? "" : (" - " + endDate))).toString());
                        slotsModel.setDuration(3600000);
                        slotsModel.setCurrency(currency);
                        slotsModel.setCurrencyCode(currencyCode);
                        slotsList.add(slotsModel);
                        addSlotsAdapter.notifyDataSetChanged();
                        recycleView.smoothScrollToPosition(slotsList.size() - 1);
                    } else {
                        AppUtils.getInstance().showToast(AddTimeSlotsActivity.this, getString(R.string.please_enter_price));
                        return;
                    }
                }
                break;
        }
    }


    /**
     * method to get data and set it in views
     */
    private void getDataAndSetValues() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            ArrayList<AddSlotsModel> addSlotsList = (ArrayList<AddSlotsModel>) getIntent().getSerializableExtra(Constants.IntentConstant.SLOTS);
            currency = getIntent().getExtras().getString(Constants.IntentConstant.CURRENCY, "د.إ");
            currencyCode = getIntent().getExtras().getString(Constants.IntentConstant.CURRENCY_CODE, "AED");
            String startTime = getIntent().getExtras().getString(Constants.IntentConstant.START_DATE, "");
            String endTime = getIntent().getExtras().getString(Constants.IntentConstant.END_DATE, "");
            startDate = AppUtils.getInstance().formatDate(startTime, SERVICE_DATE_FORMAT, DATE_FORMAT);
            endDate = AppUtils.getInstance().formatDate(endTime, SERVICE_DATE_FORMAT, DATE_FORMAT);
            if (null != addSlotsList) slotsList.addAll(addSlotsList);
            if (slotsList.size() == 0) {
                AddSlotsModel slotsModel = new AddSlotsModel();
                slotsModel.setDateRange(TextUtils.concat(startDate + (startDate.equals(endDate) ? "" : (" - " + endDate))).toString());
                slotsModel.setDuration(3600000);
                slotsModel.setCurrency(currency);
                slotsModel.setCurrencyCode(currencyCode);
                slotsList.add(slotsModel);
            }else {
                menuSecondRight.setVisibility(View.VISIBLE);
                for (int i = 0; i < slotsList.size(); i++) {
                    if (slotsList.get(i).isAllDay()) {
                        allDay = true;
                        menuSecondRight.setVisibility(View.GONE);
                        break;
                    }
                }
            }
            addSlotsAdapter.notifyDataSetChanged();
        }
    }
}
