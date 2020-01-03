package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.adapters.BuddySlotsAdapter;
import com.shopoholic.calendar.CalendarAdapter;
import com.shopoholic.calendar.CalendarBean;
import com.shopoholic.calendar.CalendarDateView;
import com.shopoholic.calendar.CalendarView;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.models.productservicedetailsresponse.SlotSelectedDate;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;

import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;
import static com.shopoholic.utils.Constants.IntentConstant.BUDDY;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class TimeSlotsActivity extends BaseActivity {

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
    @BindView(R.id.tv_previous_year)
    TextView tvPreviousYear;
    @BindView(R.id.tv_current_year)
    TextView tvCurrentYear;
    @BindView(R.id.tv_next_year)
    TextView tvNextYear;
    @BindView(R.id.ll_year_month)
    LinearLayout llYearMonth;
    @BindView(R.id.ll_week_days)
    LinearLayout llWeekDays;
    @BindView(R.id.calendarDateView)
    CalendarDateView calendarDateView;
    @BindView(R.id.tv_shopper_selected_date)
    CustomTextView tvShopperSelectedDate;
    @BindView(R.id.tv_add_slots)
    CustomTextView tvAddSlots;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private BuddySlotsAdapter buddySlotsAdapter;
    private ArrayList<ServiceSlot> slotsList;
    private ArrayList<ServiceSlot> selectedSlotsList;
    private String slotDate;

    private long lastClickTime;
    private String selectedMonth;
    private Date currentDate;
    private Calendar mCalendar;
    private CalendarBean selectedDay;
    private int month, year, day;
    private ArrayList<String> slotsDayList;
    private ArrayList<String> selectedDayList;
    private int chooseMonth;
    private String startDate = "", endDate = "";
    public String currency = "د.إ";
    private ArrayList<SlotSelectedDate> daysList;
    private String fromClass = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);
        initVariable();
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        CalendarBean calendarBean = new CalendarBean(year, month + 1, day);
        setCalenderData(calendarBean);
        setAdapter();
        getDataAndUpdateViews();
    }

    /**
     * method to get data and update the views
     */
    @SuppressLint("SimpleDateFormat")
    private void getDataAndUpdateViews() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            ArrayList<ServiceSlot> list = (ArrayList<ServiceSlot>) getIntent().getSerializableExtra(Constants.IntentConstant.SLOTS);
            slotsList.addAll(list);
            daysList = (ArrayList<SlotSelectedDate>) getIntent().getExtras().getSerializable(Constants.IntentConstant.DAYS_LIST);
            ArrayList<String> selectedDaysList = getIntent().getExtras().getStringArrayList(Constants.IntentConstant.SELECTED_DAYS_LIST);
            if (daysList != null){
                for (int i=0; i<daysList.size(); i++) {
                    if (!slotsDayList.contains(daysList.get(i).getSelectedDate())){
                        slotsDayList.add(daysList.get(i).getSelectedDate());
                    }
                }
            }
            if (selectedDaysList != null) selectedDayList.addAll(selectedDaysList);
            currency = getIntent().getExtras().getString(Constants.IntentConstant.CURRENCY, "د.إ");
            startDate = getIntent().getExtras().getString(Constants.IntentConstant.START_DATE, "");
            endDate = getIntent().getExtras().getString(Constants.IntentConstant.END_DATE, "");
            progressBar.setVisibility(View.GONE);
            noDataAvailable();
            buddySlotsAdapter.notifyDataSetChanged();
            calendarDateView.notifyData();
            fromClass = getIntent().getExtras().getString(Constants.IntentConstant.FROM_CLASS, "");
            if (fromClass.equals(BUDDY)) menuRight.setVisibility(View.GONE);
        }
    }

    /**
     * This method is used to set the recycler view adapter
     **/
    private synchronized void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(buddySlotsAdapter);

        calendarDateView.setAdapter((convertView, parentView, bean, position) -> {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.adapter_calendar, null);
                viewHolder = new ViewHolder();
                viewHolder.dayView = (TextView) convertView.findViewById(R.id.tv_day);
                viewHolder.rlDayView = (RelativeLayout) convertView.findViewById(R.id.rl_day_view);
                viewHolder.ivEvent1 = (ImageView) convertView.findViewById(R.id.iv_event_1);
                viewHolder.rlEventMore = (RelativeLayout) convertView.findViewById(R.id.rl_event_4);
                viewHolder.position = position;
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.dayView.setText(TextUtils.concat("" + bean.day));
            viewHolder.ivEvent1.setVisibility(View.GONE);
            viewHolder.rlDayView.setBackgroundResource(0);
            viewHolder.dayView.setTextColor(ContextCompat.getColor(TimeSlotsActivity.this, R.color.colorDate));
            String eventDate = bean.toString();
//                String eventDate = AppUtils.getInstance().formatDate(bean.toString(), SERVICE_DATE_FORMAT, DATE_FORMAT);

            setEventUI(viewHolder, bean, eventDate);

            if (bean.mothFlag != 0) {
                viewHolder.rlDayView.setBackgroundResource(0);
                viewHolder.ivEvent1.setVisibility(View.GONE);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(TimeSlotsActivity.this, android.R.color.darker_gray));
            } /*else if (new SimpleDateFormat(DATE_FORMAT).format(currentDate).equalsIgnoreCase(eventDate)) {
//                    viewHolder.rlDayView.setBackground(ContextCompat.getDrawable(TimeSlotsActivity.this, R.drawable.white_drawable_stroke));
//                    viewHolder.dayView.setTextColor(ContextCompat.getColor(TimeSlotsActivity.this, R.color.colorLightPurple));
                viewHolder.rlDayView.setBackgroundResource(R.drawable.round_date_drawable);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(TimeSlotsActivity.this, R.color.colorLightWhite));
            }*/
            /*else if (selectedDay != null && selectedDay == bean) {
                viewHolder.rlDayView.setBackgroundResource(R.drawable.round_date_drawable);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(TimeSlotsActivity.this, R.color.colorLightWhite));
            } */
            /*else if (slotsDayList.size() > 0) {
                for (String date : slotsDayList) {
                    if (date.equals(eventDate)) {
                        viewHolder.rlDayView.setBackgroundResource(R.drawable.round_date_drawable);
                        viewHolder.dayView.setTextColor(ContextCompat.getColor(TimeSlotsActivity.this, R.color.colorLightWhite));
                        break;
                    }
                }
            }*/
            /*else {
                viewHolder.rlDayView.setBackgroundResource(0);
                //viewHolder.ivEvent1.setVisibility(View.GONE);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(TimeSlotsActivity.this, R.color.colorLightPurple));
            }*/


            return convertView;
        });

        calendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, final CalendarBean bean) {
                if (!fromClass.equals(BUDDY)) filterCalenderList(bean);

            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onItemLongClick(final View view, final int position, final CalendarBean bean) {
//                if (AppUtils.getInstance().checkDateInRange(startDate, endDate, bean.toString())
//                        && slotsDayList.contains(calendarDateView.getData(position).toString())) {
//                    setEventUI((ViewHolder) (view.getTag()), bean, bean.toString());
/*
                    calendarDateView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                case MotionEvent.ACTION_MOVE:
                                    int position = 0;
                                    if (position != -1) {
                                        selectedDayList.add(bean.toString());
                                        if (AppUtils.getInstance().checkDateInRange(startDate, endDate, calendarDateView.getData(position).toString())
                                                && slotsDayList.contains(calendarDateView.getData(position).toString())) {
                                            setEventUI((ViewHolder) (v.getTag()), bean, calendarDateView.getData(position).toString());
                                        }
                                    }
                                    break;
                                case MotionEvent.ACTION_UP:
                                    calendarDateView.setOnTouchListener(null);
                                    break;
                            }
                            return false;
                        }
                    });
*/
//                }
                return false;
            }
        });

        calendarDateView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }


            @Override
            public void onPageSelected(int position) {
                if (calendarDateView != null) {
                    CalendarBean bean = calendarDateView.getData(position);
                    setCalenderData(bean);
                    /*if (AppUtils.getInstance().isInternetAvailable(TimeSlotsActivity.this)) {
                        slotsList.clear();
                        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.VISIBLE);
                        hitOrdersListApi(bean.year, bean.month);
                        slotsDayList.clear();
                        calendarDateView.notifyData();

                    }*/
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * method to filter calender list
     * @param bean
     */
    private void filterCalenderList(CalendarBean bean) {
        lastClickTime = SystemClock.elapsedRealtime();

        if (bean.mothFlag == 0) {
            year = bean.year;
            month = bean.month - 1;
            day = bean.day;
            slotDate = AppUtils.getInstance().convertDate(bean, SERVICE_DATE_FORMAT);
            selectedMonth = String.valueOf(bean.month - 1);
            selectedDay = bean;
            Log.e("Date", "" + bean.year + "-" + bean.month + "-" + bean.day);
            setCalenderData(bean);
            String startDate = currentDate.before(AppUtils.getInstance().getDateFromString(this.startDate, SERVICE_DATE_FORMAT)) ? this.startDate :
                    AppUtils.getInstance().getDate(currentDate.getTime(), SERVICE_DATE_FORMAT);
            if (AppUtils.getInstance().checkDateInRange(startDate, endDate, bean.toString()) && slotsDayList.contains(bean.toString())) {
                if (selectedDayList.contains(bean.toString())) {
                    selectedDayList.remove(bean.toString());
                } else {
                    selectedDayList.add(bean.toString());
                }
                progressBar.setVisibility(View.GONE);
                noDataAvailable();
                ArrayList<String> slotsIds = new ArrayList<>();
                if (daysList != null) {
                    for (int i=0; i<daysList.size(); i++) {
                        if (selectedDayList.contains(daysList.get(i).getSelectedDate())) {
                            if (!daysList.get(i).getIsAvailable().equals("1")) {
                                slotsIds.add(daysList.get(i).getSlotId());
                            }
                        }
                    }
                }
                for (int i=0; i<slotsList.size(); i++) {
                    if (slotsIds.contains(slotsList.get(i).getId())) {
                        slotsList.get(i).setIsAvailable("2");
                    } else {
                        slotsList.get(i).setIsAvailable("1");
                    }

                    if (selectedDayList.size() <= 0) {
                        slotsList.get(i).setSelected(false);
                    }
                }
                buddySlotsAdapter.notifyDataSetChanged();
                calendarDateView.notifyData();
            }
        }
    }

    /**
     * method to set the list
     */
    private void setList() {
        selectedSlotsList.clear();
        for (int i = 0; i < slotsList.size(); i++) {
            progressBar.setVisibility(View.VISIBLE);
            ArrayList<String> slotsDate = new ArrayList<>();
            for (Object slot : slotsList) {
                if (slot instanceof ServiceSlot) {
                    ServiceSlot serviceSlot = (ServiceSlot) slot;
                    String startDate = serviceSlot.getSlotStartDate();
                    String endDate = serviceSlot.getSlotEndDate();
                    slotsDate.addAll(AppUtils.getInstance().getDates(startDate, endDate));
                }
            }
            for (String date : slotsDate) {
                if (date.equalsIgnoreCase(selectedDay.toString())) {
                    selectedSlotsList.add(slotsList.get(i));
                }
            }
        }
        progressBar.setVisibility(View.GONE);
        noDataAvailable();
        buddySlotsAdapter.notifyDataSetChanged();
    }

    /**
     * method to set the event UI
     *
     * @param viewHolder
     * @param bean
     * @param eventDate
     */
    private void setEventUI(ViewHolder viewHolder, CalendarBean bean, String eventDate) {
        if (slotsDayList != null) {
            for (int i = 0; i < slotsDayList.size(); i++) {
                if (slotsDayList.get(i).equalsIgnoreCase(eventDate)) {
                    viewHolder.ivEvent1.setVisibility(View.VISIBLE);
                }
            }
        }
        if (selectedDayList != null) {
            for (int i = 0; i < selectedDayList.size(); i++) {
                if (selectedDayList.get(i).equalsIgnoreCase(eventDate)) {
                    viewHolder.rlDayView.setBackgroundResource(R.drawable.round_date_drawable);
                    viewHolder.dayView.setTextColor(ContextCompat.getColor(TimeSlotsActivity.this, R.color.colorLightWhite));
                }
            }
        }


    }

    @OnClick({R.id.iv_menu, R.id.menu_right, R.id.tv_previous_year, R.id.tv_next_year})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.tv_previous_year:
                try {
                    calendarDateView.setCurrentItem(calendarDateView.getCurrentItem() - 1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_next_year:
                try {
                    calendarDateView.setCurrentItem(calendarDateView.getCurrentItem() + 1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.menu_right:
                if (selectedDayList.size() == 0) {
                    AppUtils.getInstance().showToast(this, getString(R.string.please_select_at_least_one_date));
                    return;
                }
                if (slotsList.size() > 0) {
                    boolean isSelected = false;
                    for (int i = 0; i < slotsList.size(); i++) {
                        if (slotsList.get(i).isSelected()) {
                            isSelected = true;
                            break;
                        }
                    }
                    if (!isSelected) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_select_at_least_one_slot));
                        return;
                    }
                }
                if (selectedDayList.contains(AppUtils.getInstance().getDate(Calendar.getInstance().getTimeInMillis(), SERVICE_DATE_FORMAT))) {
                    boolean isPrevious = false;
                    for (ServiceSlot slot: slotsList) {
                        if (slot.isSelected()) {
                            if (slot.getAllDays().equals("1")) {
                                AppUtils.getInstance().showToast(this, getString(R.string.cant_book_past_slot));
                                isPrevious = true;
                                break;
                            }
                            String currentTime = AppUtils.getInstance().getDate(Calendar.getInstance().getTimeInMillis(), "HH:mm:ss");
                            Date startDate = AppUtils.getInstance().getDateFromString(slot.getSlotStartTime(), "HH:mm:ss");
                            Date endDate = AppUtils.getInstance().getDateFromString(currentTime, "HH:mm:ss");
                            long difference = startDate.getTime() - endDate.getTime();
                            if (difference < 0) {
                                AppUtils.getInstance().showToast(this, getString(R.string.cant_book_past_slot));
                                isPrevious = true;
                                break;
                            }
                        }
                    }
                    if (isPrevious) {
                        return;
                    }
                }
                Intent in = new Intent();
                in.putExtra(Constants.IntentConstant.TIME_SLOTS, slotsList);
                in.putStringArrayListExtra(Constants.IntentConstant.SELECTED_DAYS_LIST, selectedDayList);
                setResult(RESULT_OK, in);
                finish();
                break;
        }
    }


    /**
     * View holder for calendar date view
     */
    public class ViewHolder {
        TextView dayView;
        ImageView ivEvent1;
        RelativeLayout rlDayView, rlEventMore;
        int position;
    }

    /**
     * Method to set the year and month name in calendar
     *
     * @param bean
     */
    private void setCalenderData(CalendarBean bean) {

        switch (bean.month) {
            case 1:
                selectedMonth = getString(R.string.month_january);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_january) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_december) + " " + (bean.year - 1)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_feburary) + " " + bean.year));
                break;

            case 2:
                selectedMonth = getString(R.string.month_feburary);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_feburary) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_january) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_march) + " " + bean.year));
                break;

            case 3:
                selectedMonth = getString(R.string.month_march);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_march) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_feburary) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_april) + " " + bean.year));
                break;

            case 4:
                selectedMonth = getString(R.string.month_april);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_april) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_march) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_may) + " " + bean.year));
                break;

            case 5:
                selectedMonth = getString(R.string.month_may);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_may) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_april) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_june) + " " + bean.year));
                break;

            case 6:
                selectedMonth = getString(R.string.month_june);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_june) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_may) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_july) + " " + bean.year));
                break;

            case 7:
                selectedMonth = getString(R.string.month_july);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_july) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_june) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_august) + " " + bean.year));
                break;

            case 8:
                selectedMonth = getString(R.string.month_august);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_august) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_july) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_september) + " " + bean.year));
                break;

            case 9:
                selectedMonth = getString(R.string.month_september);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_september) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_august) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_october) + " " + bean.year));
                break;

            case 10:
                selectedMonth = getString(R.string.month_october);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_october) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_september) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_november) + " " + bean.year));
                break;

            case 11:
                selectedMonth = getString(R.string.month_november);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_november) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_october) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_december) + " " + bean.year));
                break;

            case 12:
                selectedMonth = getString(R.string.month_december);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_december) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_november) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_january) + (bean.year + 1)));
                break;

        }
        tvShopperSelectedDate.setText(TextUtils.concat(selectedMonth + " " + bean.day + "," + " " + bean.year));
    }


    /**
     * method to initialize the variables
     */
    private void initVariable() {
        slotsDayList = new ArrayList<>();
        selectedDayList = new ArrayList<>();
        slotsList = new ArrayList<>();
        selectedSlotsList = new ArrayList<>();
        lastClickTime = 0;
        tvTitle.setText(getString(R.string.time_slots));
        tvAddSlots.setVisibility(View.GONE);
        ivMenu.setVisibility(View.VISIBLE);
        menuRight.setVisibility(View.VISIBLE);
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setImageResource(R.drawable.ic_check_green);
        mCalendar = Calendar.getInstance();
        currentDate = mCalendar.getTime();
        slotDate = AppUtils.getInstance().getDate(Calendar.getInstance().getTimeInMillis(), SERVICE_DATE_FORMAT);
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);
        buddySlotsAdapter = new BuddySlotsAdapter(this, slotsList, (position, view) -> {
            switch (view.getId()) {
                case R.id.tv_date:
                    if (!fromClass.equals(BUDDY)) {
                        slotsList.get(position).setSelected(!slotsList.get(position).isSelected());
                        buddySlotsAdapter.notifyItemChanged(position);
                    }
                    break;
            }
        });
    }


    /**
     * method checks whether data is present in list or not and update UI accordingly
     */
    public void noDataAvailable() {
        if (slotsList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }

}
