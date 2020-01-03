package com.shopoholic.calendar;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Akash Jain on 19/07/2017.
 */

public interface CalendarAdapter {
     View getView(View convertView, ViewGroup parentView, CalendarBean bean, int position);
}
