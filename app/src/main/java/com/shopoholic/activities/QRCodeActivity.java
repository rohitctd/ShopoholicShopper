package com.shopoholic.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.models.NotificationBean;
import com.shopoholic.models.purchaseorderresponse.Result;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Class created by Sachin on 25-Apr-18.
 */
public class QRCodeActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.tv_order_details)
    CustomTextView tvOrderDetails;
    @BindView(R.id.tv_order_number)
    CustomTextView tvOrderNumber;
    @BindView(R.id.tv_order_id)
    CustomTextView tvOrderId;
    @BindView(R.id.iv_order_product_image)
    ImageView ivOrderProductImage;
    @BindView(R.id.tv_order_product)
    CustomTextView tvOrderProduct;
    @BindView(R.id.tv_order_product_name)
    CustomTextView tvOrderProductName;
    @BindView(R.id.tv_order_date)
    CustomTextView tvOrderDate;
    @BindView(R.id.tv_order_product_date)
    CustomTextView tvOrderProductDate;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    private String qrContent = "";
    private Result orderResponse;

    private BroadcastReceiver orderUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                NotificationBean notificationBean = (NotificationBean) intent.getExtras().getSerializable(Constants.IntentConstant.NOTIFICATION);
                if (notificationBean != null && orderResponse.getId().equals(notificationBean.getOrderId()) && notificationBean.getOrderStatus().equals("5"))  {
                    Intent homeIntent = new Intent(QRCodeActivity.this, HomeActivity.class);
                    homeIntent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.QR_CODE);
                    AppUtils.getInstance().openNewActivity(QRCodeActivity.this, homeIntent);
//                    finish();
                }
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(orderUpdateReceiver, new IntentFilter(Constants.IntentConstant.NOTIFICATION));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(orderUpdateReceiver);
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
        initVariables();
        getProductDetails();
        setQRCode();
    }

    /**
     * method to initialize variables
     */
    private void initVariables() {
        ivMenu.setImageResource(R.drawable.ic_back);
        tvTitle.setText(getString(R.string.order_placed));
    }

    /**
     * method to get the product details from intent
     */
    private void getProductDetails() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            orderResponse = (Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.ORDER_DETAILS);
            if (orderResponse != null) {
                qrContent = orderResponse.getId();
                tvOrderId.setText(orderResponse.getOrderNumber());
                tvOrderProductName.setText(orderResponse.getDealName());
                tvOrderProductDate.setText(AppUtils.getInstance().formatDate(orderResponse.getOrderDate(), Constants.AppConstant.SERVICE_DATE_FORMAT,
                        Constants.AppConstant.DATE_FORMAT));
                AppUtils.getInstance().setImages(this, orderResponse.getDealImage(), ivOrderProductImage, 0, R.drawable.ic_placeholder);
            }
        }
    }

    /**
     * method to set the qr code
     */
    private void setQRCode() {
// this is a small sample use of the QRCodeEncoder class from zxing
        QRCodeWriter writer = new QRCodeWriter();
        try {
//            BitMatrix bitMatrix = writer.encode("12ww33", BarcodeFormat.QR_CODE, (int) getResources().getDimension(R.dimen._150sdp), (int) getResources().getDimension(R.dimen._150sdp));
            BitMatrix bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, (int) getResources().getDimension(R.dimen._150sdp), (int) getResources().getDimension(R.dimen._150sdp));
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ivQrCode.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.iv_menu)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
