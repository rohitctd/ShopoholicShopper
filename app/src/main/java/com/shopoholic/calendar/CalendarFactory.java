package com.shopoholic.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Akash Jain on 19/07/2017.
 */

public class CalendarFactory {

    private static HashMap<String, List<CalendarBean>> cache = new HashMap<>();

    //Get the collection in the middle of January
    public static List<CalendarBean> getMonthOfDayList(int y, int m) {

        String key = y+""+m;
        if(cache.containsKey(key)){
            List<CalendarBean> list=cache.get(key);
            if(list==null){
                cache.remove(key);
            }else{
                return list;
            }
        }

        List<CalendarBean> list = new ArrayList<CalendarBean>();
        cache.put(key,list);

        //Calculate the first day of the month is the day of the week
        int fweek = CalendarUtil.getDayOfWeek(y, m, 1);
        int total = CalendarUtil.getDayOfMaonth(y, m);

        //According to the launch of the week there are a few shows
        for (int i = fweek - 1; i > 0; i--) {
            CalendarBean bean = geCalendarBean(y, m, 1 - i);
            bean.mothFlag = -1;
            list.add(bean);
        }

        //Get the number of days in the month
        for (int i = 0; i < total; i++) {
            CalendarBean bean = geCalendarBean(y, m, i + 1);
            list.add(bean);
        }

        //In order to fill the 42 lattice, showing the number of days out of the month
        for (int i = 0; i < 42 - (fweek - 1) - total; i++) {
            CalendarBean bean = geCalendarBean(y, m, total + i + 1);
            bean.mothFlag = 1;
            list.add(bean);
        }
        return list;
    }


    public static CalendarBean geCalendarBean(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DATE);

        CalendarBean bean = new CalendarBean(year, month, day);
        bean.week = CalendarUtil.getDayOfWeek(year, month, day);
//        String[] chinaDate = ChinaDate.getChinaDate(year, month, day);
//        bean.chinaMonth = chinaDate[0];
//        bean.chinaDay = chinaDate[1];

        return bean;
    }

    public static void main(String[] args) {
    }


}
