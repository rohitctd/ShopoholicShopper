package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.shopoholic.adapters.HuntSlotsAdapter;
import com.shopoholic.calendar.CalendarAdapter;
import com.shopoholic.calendar.CalendarBean;
import com.shopoholic.calendar.CalendarDateView;
import com.shopoholic.calendar.CalendarView;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.AddSlotsModel;;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;

import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class HuntTimeSlotsActivity extends BaseActivity {

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
    private HuntSlotsAdapter buddySlotsAdapter;
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
    private int chooseMonth;
    private String startDate = "", endDate = "";
    private String currency = "د.إ";
    private String currencyCode = "AED";

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
    private void getDataAndUpdateViews() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            ArrayList<ServiceSlot> list = (ArrayList<ServiceSlot>) getIntent().getSerializableExtra(Constants.IntentConstant.SLOTS);
            slotsList.addAll(list);
            ArrayList<String> daysList = getIntent().getExtras().getStringArrayList(Constants.IntentConstant.DAYS_LIST);
            if (daysList != null) slotsDayList.addAll(daysList);
            currency = getIntent().getExtras().getString(Constants.IntentConstant.CURRENCY, "د.إ");
            currencyCode = getIntent().getExtras().getString(Constants.IntentConstant.CURRENCY_CODE, "AED");
            String startTime = getIntent().getExtras().getString(Constants.IntentConstant.START_DATE, "");
            String endTime = getIntent().getExtras().getString(Constants.IntentConstant.END_DATE, "");
            startDate = AppUtils.getInstance().formatDate(startTime, DATE_FORMAT, SERVICE_DATE_FORMAT);
            endDate = AppUtils.getInstance().formatDate(endTime, DATE_FORMAT, SERVICE_DATE_FORMAT);
            ////
            progressBar.setVisibility(View.GONE);
            noDataAvailable();
            buddySlotsAdapter.notifyDataSetChanged();
            calendarDateView.notifyData();
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
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.dayView.setText(TextUtils.concat("" + bean.day));
            viewHolder.ivEvent1.setVisibility(View.GONE);
            viewHolder.rlDayView.setBackgroundResource(0);
            viewHolder.dayView.setTextColor(ContextCompat.getColor(HuntTimeSlotsActivity.this, R.color.colorDate));
            String eventDate = bean.toString();
//                String eventDate = AppUtils.getInstance().formatDate(bean.toString(), SERVICE_DATE_FORMAT, DATE_FORMAT);

            setEventUI(viewHolder, bean, eventDate);

            if (bean.mothFlag != 0) {
                viewHolder.rlDayView.setBackgroundResource(0);
                viewHolder.ivEvent1.setVisibility(View.GONE);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(HuntTimeSlotsActivity.this, android.R.color.darker_gray));
            }
            return convertView;
        });

        calendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, final CalendarBean bean) {
                filterCalenderList(bean);

            }

            @Override
            public boolean onItemLongClick(View view, int position, CalendarBean bean) {
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
            if (AppUtils.getInstance().checkDateInRange(startDate, endDate, bean.toString())) {
                if (slotsDayList.contains(bean.toString())) {
                    slotsDayList.remove(bean.toString());
                } else {
                    slotsDayList.add(bean.toString());
                }
                progressBar.setVisibility(View.GONE);
                noDataAvailable();
                buddySlotsAdapter.notifyDataSetChanged();
                calendarDateView.notifyData();
            }
        }

//        setList();

    }

    /**
     * method to set the list
     */
    private void setList() {
        selectedSlotsList.clear();
        for (int i = 0; i < slotsList.size(); i++) {
            if (slotsList.size() != 0) {
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
            // int frequency = Collections.frequency(slotsDayList, eventDate);

            for (int i = 0; i < slotsDayList.size(); i++) {
                if (slotsDayList.get(i).equalsIgnoreCase(eventDate)) {
//                    viewHolder.ivEvent1.setVisibility(View.VISIBLE);
                    viewHolder.rlDayView.setBackgroundResource(R.drawable.round_date_drawable);
                    viewHolder.dayView.setTextColor(ContextCompat.getColor(HuntTimeSlotsActivity.this, R.color.colorLightWhite));
                } else {
//                    viewHolder.ivEvent1.setVisibility(View.GONE);
                }
            }
        }


    }

    @OnClick({R.id.iv_menu, R.id.tv_add_slots, R.id.menu_right, R.id.tv_previous_year, R.id.tv_next_year})
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
                if (slotsDayList.size() == 0) {
                    AppUtils.getInstance().showToast(this, getString(R.string.please_select_at_least_one_date));
                    return;
                }if (slotsList.size() == 0) {
                    AppUtils.getInstance().showToast(this, getString(R.string.please_select_at_least_one_slot));
                    return;
                }
                Intent in = new Intent();
                in.putExtra(Constants.IntentConstant.TIME_SLOTS, slotsList);
                in.putStringArrayListExtra(Constants.IntentConstant.DAYS_LIST, slotsDayList);
                setResult(RESULT_OK, in);
                finish();
                break;
            case R.id.tv_add_slots:
                Intent intent = new Intent(HuntTimeSlotsActivity.this, AddTimeSlotsActivity.class);
                ArrayList<AddSlotsModel> list = new ArrayList<>();
                for (int i = 0; i < slotsList.size(); i++) {
                    list.add(AddSlotsModel.getSlotModel(slotsList.get(i)));
                    list.get(i).setCurrency(currency);
                    list.get(i).setCurrencyCode(currencyCode);
                }
                intent.putExtra(Constants.IntentConstant.CURRENCY, currency);
                intent.putExtra(Constants.IntentConstant.CURRENCY_CODE, currencyCode);
                intent.putExtra(Constants.IntentConstant.SLOTS, list);
                intent.putExtra(Constants.IntentConstant.START_DATE, startDate);
                intent.putExtra(Constants.IntentConstant.END_DATE, endDate);
                startActivityForResult(intent, Constants.IntentConstant.REQUEST_TIME_SLOTS);
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
        slotsList = new ArrayList<>();
        selectedSlotsList = new ArrayList<>();
        lastClickTime = 0;
        tvTitle.setText(getString(R.string.time_slots));
        tvAddSlots.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.VISIBLE);
        menuRight.setVisibility(View.VISIBLE);
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setImageResource(R.drawable.ic_check_green);
        mCalendar = Calendar.getInstance();
        currentDate = mCalendar.getTime();
        slotDate = AppUtils.getInstance().getDate(Calendar.getInstance().getTimeInMillis(), SERVICE_DATE_FORMAT);
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);
        buddySlotsAdapter = new HuntSlotsAdapter(this, slotsList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.tv_edit:
                        Intent intent = new Intent(HuntTimeSlotsActivity.this, AddTimeSlotsActivity.class);
                        ArrayList<AddSlotsModel> list = new ArrayList<>();
                        for (int i = 0; i < slotsList.size(); i++) {
                            list.add(AddSlotsModel.getSlotModel(slotsList.get(i)));
                            list.get(i).setCurrency(currency);
                            list.get(i).setCurrencyCode(currencyCode);
                        }
                        intent.putExtra(Constants.IntentConstant.CURRENCY, currency);
                        intent.putExtra(Constants.IntentConstant.CURRENCY_CODE, currencyCode);
                        intent.putExtra(Constants.IntentConstant.SLOTS, list);
                        intent.putExtra(Constants.IntentConstant.START_DATE, startDate);
                        intent.putExtra(Constants.IntentConstant.END_DATE, endDate);
                        startActivityForResult(intent, Constants.IntentConstant.REQUEST_TIME_SLOTS);
                        break;
                }
            }
        });
    }


    /**
     * This method checks whether data is present in list or not and update UI accordingly
     */
    public void noDataAvailable() {
        if (slotsList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.IntentConstant.REQUEST_TIME_SLOTS && resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            slotsList.clear();
            ArrayList<AddSlotsModel> addSlotsList = (ArrayList<AddSlotsModel>) data.getExtras().getSerializable(Constants.IntentConstant.TIME_SLOTS);
            if (addSlotsList != null) {
                for (int i = 0; i < addSlotsList.size(); i++) {
                    slotsList.add(AddSlotsModel.getSlotModel(addSlotsList.get(i)));
                    slotsList.get(i).setCurrency(currency);
                }
//                setList();

                progressBar.setVisibility(View.GONE);
                noDataAvailable();
                buddySlotsAdapter.notifyDataSetChanged();
                calendarDateView.notifyData();
            }
        }
    }
}
