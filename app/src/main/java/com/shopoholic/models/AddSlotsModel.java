package com.shopoholic.models;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.shopoholic.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholic.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;


public class AddSlotsModel implements Serializable{
    private String dateRange;
    private String startTime = AppUtils.getInstance().getTime();
    private String endTime;
    private long duration = 3600000;
    private String price;
    private boolean allDay;
    private String currency;
    private String currencyCode;

    public String getDateRange() {
        return dateRange == null ? "" : dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public String getStartTime() {
        return startTime == null ? AppUtils.getInstance().getTime() : startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        updateEndTime(getStartTime(), duration);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        updateEndTime(getStartTime(), duration);
    }

    public String getPrice() {
        return price == null ? "" : price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCurrency() {
        return currency == null ? "" : currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    /**
     * method to update end date
     * @param startTime
     * @param duration
     */
    @SuppressLint("SimpleDateFormat")
    private void updateEndTime(String startTime, long duration) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.AppConstant.TIME_FORMAT);
            Date parsedDate = dateFormat.parse(startTime);
            long timestamp = parsedDate.getTime();
            timestamp += duration;
            endTime = AppUtils.getInstance().getTimeFromMilliseconds(timestamp, Constants.AppConstant.TIME_FORMAT);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * get add slot model
     * @param addSlotsModel
     * @return
     */
    public static ServiceSlot getSlotModel(AddSlotsModel addSlotsModel){
        ServiceSlot slotsModel = new ServiceSlot();
        slotsModel.setPrice(addSlotsModel.getPrice());
        slotsModel.setSlotStartDate(AppUtils.getInstance().formatDate(addSlotsModel.getDateRange().split(" - ")[0], DATE_FORMAT, SERVICE_DATE_FORMAT));
        if (addSlotsModel.getDateRange().contains("-")) {
            slotsModel.setSlotEndDate(AppUtils.getInstance().formatDate(addSlotsModel.getDateRange().split(" - ")[1], DATE_FORMAT, SERVICE_DATE_FORMAT));
        }else {
            slotsModel.setSlotEndDate(slotsModel.getSlotStartDate());
        }
        slotsModel.setSlotStartTime(addSlotsModel.getStartTime());
        slotsModel.setSlotEndTime(addSlotsModel.getEndTime());
        slotsModel.setAllDay(addSlotsModel.isAllDay());
        return slotsModel;
    }


    /**
     * get add slot model
     * @param result
     * @return
     */
    public static AddSlotsModel getSlotModel(ServiceSlot result){
        AddSlotsModel slotsModel = new AddSlotsModel();
        slotsModel.setPrice(result.getPrice());
        slotsModel.setAllDay(result.isAllDay());
        String time = result.getSlotEndTime();
        slotsModel.setStartTime(result.getSlotStartTime());
        slotsModel.setDuration(AppUtils.getInstance().getMillisecondsDifference(result.getSlotStartTime(), time));
        if (result.getSlotStartDate().equals(result.getSlotEndDate())) {
            slotsModel.setDateRange(AppUtils.getInstance().formatDate(result.getSlotStartDate(), SERVICE_DATE_FORMAT, DATE_FORMAT));
        } else {
            slotsModel.setDateRange(TextUtils.concat(AppUtils.getInstance().formatDate(result.getSlotStartDate(), SERVICE_DATE_FORMAT, DATE_FORMAT)
                    + " - " + AppUtils.getInstance().formatDate(result.getSlotEndDate(), SERVICE_DATE_FORMAT, DATE_FORMAT)).toString());
        }
        slotsModel.setAllDay(result.isAllDay());
        return slotsModel;
    }

    public String getCurrencyCode() {
        return currencyCode == null ? "" : currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
