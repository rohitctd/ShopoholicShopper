package com.shopoholic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.models.OrderBean;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Class created by Sachin on 12-May-18.
 */
public class FilterOrderActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_clear)
    CustomTextView tvClear;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.tv_filter_order_status)
    CustomTextView tvFilterOrderStatus;
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_pending)
    RadioButton rbPending;
    @BindView(R.id.rb_confirmed)
    RadioButton rbConfirmed;
    @BindView(R.id.rl_filter_order_status)
    RelativeLayout rlFilterOrderStatus;
    @BindView(R.id.tv_date_range)
    CustomTextView tvDateRange;
    @BindView(R.id.tv_start_range)
    CustomTextView tvStartRange;
    @BindView(R.id.ll_date_range)
    LinearLayout llDateRange;
    @BindView(R.id.tv_end_date)
    CustomTextView tvEndDate;
    @BindView(R.id.rl_date_range)
    RelativeLayout rlDateRange;
    @BindView(R.id.btn_apply)
    CustomButton btnApply;
    @BindView(R.id.rg_order_status)
    LinearLayout rgOrderStatus;
    @BindView(R.id.rb_delivered)
    RadioButton rbDelivered;
    @BindView(R.id.rb_out_for_delivery)
    RadioButton rbOutForDelivery;
    @BindView(R.id.rb_rejected)
    RadioButton rbRejected;
    private OrderBean orderBean;
    private int count = 0;
    private boolean isDateClick = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_order);
        ButterKnife.bind(this);
        setViews();
        setListener();
    }

    @OnClick({R.id.iv_back, R.id.tv_clear, R.id.tv_start_range, R.id.tv_end_date, R.id.btn_apply})
    public void onViewClicked(View view) {
        Calendar startDate, endDate;
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_clear:
                new AlertDialog.Builder(this, R.style.DatePickerTheme)
                        .setMessage(getString(R.string.sure_to_clear_filter))
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            if (AppUtils.getInstance().isInternetAvailable(this)) {
                                orderBean = new OrderBean();
                                rbAll.setChecked(true);
                                tvStartRange.setText("");
                                tvEndDate.setText("");
                                updateStatus(rbAll);
                                btnApply.performClick();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                            // do nothing
                        })
                        .show();
                break;
            case R.id.tv_start_range:
                if (!isDateClick) {
                    isDateClick = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isDateClick = false;
                        }
                    }, 1000);
                    endDate = tvEndDate.getText().toString().trim().length() == 0 ? null :
                            AppUtils.getInstance().getCallenderObject(tvEndDate.getText().toString().trim());
                    AppUtils.getInstance().openDatePicker(this, tvStartRange, null, endDate, tvStartRange.getText().toString().trim());
                }
                break;
            case R.id.tv_end_date:
                if (!isDateClick) {
                    isDateClick = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isDateClick = false;
                        }
                    }, 1000);
                    startDate = tvStartRange.getText().toString().trim().length() == 0 ? Calendar.getInstance() :
                            AppUtils.getInstance().getCallenderObject(tvStartRange.getText().toString().trim());
                    AppUtils.getInstance().openDatePicker(this, tvEndDate, startDate, null, tvEndDate.getText().toString().trim());
                }
                break;
            case R.id.btn_apply:
                setDataOnModel();
                break;
        }
    }

    /*
    method to set views in toolbar
     */
    private void setListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(view);
            }
        };
        rbAll.setOnClickListener(listener);
        rbPending.setOnClickListener(listener);
        rbConfirmed.setOnClickListener(listener);
        rbDelivered.setOnClickListener(listener);
        rbOutForDelivery.setOnClickListener(listener);
        rbRejected.setOnClickListener(listener);

    }

    /**
     * method to update status
     */
    private void updateStatus(View view) {
        int color = ContextCompat.getColor(FilterOrderActivity.this, R.color.colorAccent);
        int unselectedColor = ContextCompat.getColor(FilterOrderActivity.this, R.color.colorHintText);
        setColorAndChecked(unselectedColor);
        switch (view.getId()) {
            case R.id.rb_all:
                rbAll.setChecked(true);
                rbAll.setTextColor(color);
                break;
            case R.id.rb_confirmed:
                rbConfirmed.setChecked(true);
                rbConfirmed.setTextColor(color);
                break;
            case R.id.rb_pending:
                rbPending.setChecked(true);
                rbPending.setTextColor(color);
                break;
            case R.id.rb_delivered:
                rbDelivered.setChecked(true);
                rbDelivered.setTextColor(color);
                break;
            case R.id.rb_out_for_delivery:
                rbOutForDelivery.setChecked(true);
                rbOutForDelivery.setTextColor(color);
                break;
            case R.id.rb_rejected:
                rbRejected.setChecked(true);
                rbRejected.setTextColor(color);
                break;
        }
    }

    /**
     * method to set checked and color of radio button
     *
     * @param unselectedColor
     */
    private void setColorAndChecked(int unselectedColor) {
        rbAll.setTextColor(unselectedColor);
        rbPending.setTextColor(unselectedColor);
        rbConfirmed.setTextColor(unselectedColor);
        rbDelivered.setTextColor(unselectedColor);
        rbOutForDelivery.setTextColor(unselectedColor);
        rbRejected.setTextColor(unselectedColor);

        rbAll.setChecked(false);
        rbPending.setChecked(false);
        rbConfirmed.setChecked(false);
        rbDelivered.setChecked(false);
        rbOutForDelivery.setChecked(false);
        rbRejected.setChecked(false);
    }

    /*
    method to set views in toolbar
     */

    private void setViews() {

        ivBack.setVisibility(View.VISIBLE);
        tvClear.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.filter_orders);
        if (getIntent() != null && getIntent().getExtras() != null) {
            orderBean = (OrderBean) getIntent().getExtras().getSerializable(Constants.IntentConstant.FILTER_DATA);
        }
        if (orderBean != null) {
            switch (orderBean.getStatus()) {
                case "1":
                    updateStatus(rbPending);
                    break;
                case "2":
                    updateStatus(rbConfirmed);
                    break;
                case "4":
                    updateStatus(rbOutForDelivery);
                    break;
                case "5":
                    updateStatus(rbDelivered);
                    break;
                case "7":
                    updateStatus(rbRejected);
                    break;
                default:
                    updateStatus(rbAll);
                    break;
            }
            tvStartRange.setText(orderBean.getStartDate());
            tvEndDate.setText(orderBean.getEndDate());
        }
    }

    /**
     * method to set data on filter model
     */
    private void setDataOnModel() {
        if (orderBean == null) orderBean = new OrderBean();
        orderBean.setStatus(rbPending.isChecked() ? "1" : rbConfirmed.isChecked() ? "2" : rbDelivered.isChecked() ? "5" :
                rbOutForDelivery.isChecked() ? "4" : rbRejected.isChecked() ? "7" : "");
        orderBean.setStartDate(tvStartRange.getText().toString());
        orderBean.setEndDate(tvEndDate.getText().toString());
        AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, orderBean.toString());

        if (!orderBean.getStatus().equals("")) ++count;
        if (!orderBean.getStartDate().equals("") || !orderBean.getEndDate().equals("")) ++count;
//        if (!orderBean.getStartDate().equals("")) ++count;
//        if (!orderBean.getEndDate().equals("")) ++count;
        orderBean.setCount(count);

        Intent intent = new Intent();
        intent.putExtra(Constants.IntentConstant.ORDER_DETAILS, orderBean);
        setResult(RESULT_OK, intent);
        finish();
    }
}
