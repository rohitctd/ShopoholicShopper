package com.shopoholic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.AddressAdapter;
import com.shopoholic.customviews.CustomButton;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholic.firebasechat.models.HuntDeal;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.models.deliveryaddressresponse.DeliveryAddressResponse;
import com.shopoholic.models.productservicedetailsresponse.BuddyArr;
import com.shopoholic.models.productservicedetailsresponse.Result;
import com.shopoholic.models.productservicedetailsresponse.TaxArr;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class created by Sachin on 19-Apr-18.
 */
public class PurchaseSummaryActivity extends BaseActivity implements NetworkListener {

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
    @BindView(R.id.iv_purchase_product)
    ImageView ivPurchaseProduct;
    @BindView(R.id.tv_purchase_product_name)
    CustomTextView tvPurchaseProductName;
    @BindView(R.id.tv_purchase_product_price)
    CustomTextView tvPurchaseProductPrice;
    @BindView(R.id.tv_purchase_product_quantity)
    CustomTextView tvPurchaseProductQuantity;
    @BindView(R.id.et_product_quantity)
    CustomEditText etProductQuantity;
    @BindView(R.id.tv_murchant_Name)
    CustomTextView tvMerchantName;
    @BindView(R.id.ll_quantity)
    LinearLayout ll_quantity;
    @BindView(R.id.tv_add)
    CustomTextView tvAdd;
    @BindView(R.id.btn_action)
    CustomButton btnAction;
    @BindView(R.id.view_button_loader)
    View viewButtonLoader;
    @BindView(R.id.view_button_dot)
    View viewButtonDot;
    @BindView(R.id.rv_address_list)
    RecyclerView rvAddressList;
    @BindView(R.id.tv_delivery_charges)
    CustomTextView tvDeliveryCharges;
    @BindView(R.id.fbl_taxes)
    FlexboxLayout fblTaxes;
    @BindView(R.id.tv_final_price)
    CustomTextView tvFinalPrice;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.ll_address)
    LinearLayout llAddress;
    private Result productDetails;
    private List<com.shopoholic.models.deliveryaddressresponse.Result> addressList;
    private AddressAdapter addressAdapter;
    private int previousPosition = 0;
    private int totalQuantity;
    private String roomId;
    private double totalTax = 0;
    private double price;
    private HuntDeal huntDeal;
    private UserBean buddy;
    private boolean isLoading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_summary);
        ButterKnife.bind(this);
        initVariables();
        setAdapters();
        setListener();
        if (AppUtils.getInstance().isInternetAvailable(this)) hitGetAddressesApi();
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        addressList = new ArrayList<>();
        btnAction.setText(getString(R.string.proceed_to_pay));
        tvTitle.setText(getString(R.string.purchase_summary));
        ivMenu.setImageResource(R.drawable.ic_back);
        menuRight.setVisibility(View.GONE);
        menuSecondRight.setVisibility(View.GONE);
        menuThirdRight.setVisibility(View.GONE);
        if (getIntent() != null && getIntent().getExtras() != null) {
            productDetails = (Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.PRODUCT_DETAILS);
            huntDeal = (HuntDeal) getIntent().getExtras().getSerializable(Constants.IntentConstant.HUNT_DEAL);
            buddy = (UserBean) getIntent().getExtras().getSerializable(Constants.IntentConstant.BUDDY);
            roomId = getIntent().getExtras().getString(FirebaseConstants.ROOM_ID, "");
            if (productDetails != null) {
                setProductDetailsData();
            }
            if (huntDeal != null) {
                setHuntDealData();
            }
        }
        addressAdapter = new AddressAdapter(this, addressList, new RecycleViewCallBack() {
            @Override
            public void onClick(int position, View clickedView) {
                addressList.get(previousPosition).setSelected(false);
                addressAdapter.notifyItemChanged(previousPosition);
                addressList.get(position).setSelected(true);
                addressAdapter.notifyItemChanged(position);
                previousPosition = position;
            }

            @Override
            public void onLongClick(int position, View clickedView) {

            }
        });
    }


    /**
     * method to set listener on views
     */
    private void setAdapters() {
        rvAddressList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvAddressList.setAdapter(addressAdapter);
    }

    /**
     * function to set listener on views
     */
    private void setListener() {
        etProductQuantity.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!isDestroyed() && productDetails != null) {
            String quantity = etProductQuantity.getText().toString().trim();
            price = Double.parseDouble(productDetails.getSellingPrice()) * Double.parseDouble(quantity.equals("") ? "0" : quantity);
            setTaxes();
        }
    }

    /**
     * method to set product details
     */
    private void setProductDetailsData() {
        try {
            totalQuantity = Integer.parseInt(productDetails.getQuantity());
            tvPurchaseProductName.setText(productDetails.getName());
//            String currency = getString(productDetails.getCurrency().equals("2") ? R.string.rupees : productDetails.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
            String currency = productDetails.getCurrencySymbol();
            price = Double.parseDouble(productDetails.getSellingPrice());
            String price = String.format(Locale.ENGLISH, "%.2f", this.price);
//            tvPurchaseProductPrice.setText(TextUtils.concat(currency + (productDetails.getProductType().equals("1") ? price : price + getString(R.string.per_hour))));
            tvPurchaseProductPrice.setText(TextUtils.concat(currency + price));
            tvPurchaseProductQuantity.setText(TextUtils.concat(getString(R.string.quantity)));
            if (productDetails.getHomeDelivery().equals("1")) {
                tvMerchantName.setText(TextUtils.concat(productDetails.getFirstName() + " " + productDetails.getLastName() + " ("
                        + getString(productDetails.getUserType().equals("1") ? R.string.merchant : R.string.buddy) + ")"));
            }else {
                if (productDetails.getUserId() != null && !productDetails.getUserId().equals("") && productDetails.getBuddyArr() != null)  {
                    boolean hasBuddy = false;
                    for (BuddyArr buddy : productDetails.getBuddyArr()) {
                        if (productDetails.getUserId().equals(buddy.getBuddyId())) {
                            tvMerchantName.setText(buddy.getBuddyName());
                            productDetails.setDileveryCharge(buddy.getDeliveryCharge());
                            hasBuddy = true;
                            break;
                        }
                    }
                    if (!hasBuddy) {
                        tvMerchantName.setText(getString(R.string.wiil_be_delivered_by_buddy));
                    }
                }else {
                    tvMerchantName.setText(getString(R.string.wiil_be_delivered_by_buddy));
                }
            }

            if (productDetails.getDealImages() != null && productDetails.getDealImages().split(",").length > 0)
                AppUtils.getInstance().setImages(this, productDetails.getDealImages().split(",")[0], ivPurchaseProduct, 0, R.drawable.ic_placeholder);
            if (productDetails.getProductType().equals("1")) {
                ll_quantity.setVisibility(View.VISIBLE);
            } else {
                ll_quantity.setVisibility(View.GONE);
            }

            if (productDetails.getProductType().equals("1") && productDetails.getDileveryCharge() != null && !productDetails.getDileveryCharge().equals("")) {
                tvDeliveryCharges.setText(TextUtils.concat(getString(R.string.delivery_charges) + "" + " : " + "" + currency + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(productDetails.getDileveryCharge()))));
            } else {
                tvDeliveryCharges.setVisibility(View.GONE);
            }
            setTaxes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method to set hunt details
     */
    private void setHuntDealData() {
        try {
            totalQuantity = Integer.parseInt("1");
            tvPurchaseProductName.setText(TextUtils.concat(huntDeal.getSubCategoryName() + "  " + getString(R.string.in) + " " + huntDeal.getCategoryName()));
//            String currency = getString(huntDeal.getCurrency().equals("2") ? R.string.rupees : huntDeal.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
            String currency = huntDeal.getCurrencySymbol();
            price = Double.parseDouble(huntDeal.getPrice());
            String price = String.format(Locale.ENGLISH, "%.2f", this.price);
//            tvPurchaseProductPrice.setText(TextUtils.concat(currency + (huntDeal.getProductType().equals("1") ? price : price + getString(R.string.per_hour))));
            tvPurchaseProductPrice.setText(TextUtils.concat(currency + price));
            tvPurchaseProductQuantity.setText(TextUtils.concat(getString(R.string.quantity)));
            if (buddy != null) {
                tvMerchantName.setText(TextUtils.concat(buddy.getFirstName() + " " + buddy.getLastName() + " (" + getString(R.string.buddy) + ")"));
            } else {
                tvMerchantName.setText(getString(R.string.na));
            }

            if (huntDeal.getHuntImage() != null && huntDeal.getHuntImage().split(",").length > 0)
                AppUtils.getInstance().setImages(this, huntDeal.getHuntImage().split(",")[0], ivPurchaseProduct, 0, R.drawable.ic_placeholder);
            ll_quantity.setVisibility(View.GONE);
            tvDeliveryCharges.setVisibility(View.GONE);
            llAddress.setVisibility(View.GONE);
            rvAddressList.setVisibility(View.GONE);
            setHuntTaxes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * function to set the taxes
     */
    private void setTaxes() {
        String price = String.format(Locale.ENGLISH, "%.2f", this.price);
        tvPurchaseProductPrice.setText(TextUtils.concat(productDetails.getCurrencySymbol() + price));
        if (productDetails.getTaxArr() != null && productDetails.getTaxArr().size() > 0) {
            fblTaxes.setVisibility(View.VISIBLE);
            fblTaxes.removeAllViews();
            totalTax = 0;
            for (TaxArr taxArr : productDetails.getTaxArr()) {
                double taxPer = Double.parseDouble(taxArr.getTaxPercentage());
                double taxAmount = (this.price * taxPer) / 100;
                totalTax += taxAmount;
                String message = taxArr.getTaxName()
                        + " @"
                        + taxArr.getTaxPercentage()
                        + "% : "
                        + productDetails.getCurrencySymbol()
                        + String.format(Locale.ENGLISH, "%.2f", taxAmount);
//                fblTaxes.addView(taxView(message));
            }
            String finalTax = getString(R.string.total_tax) + " : "
                    + productDetails.getCurrencySymbol()
                    + String.format(Locale.ENGLISH, "%.2f", totalTax);
            fblTaxes.addView(taxView(finalTax));
            double totalPrice = this.price + totalTax + Double.parseDouble(productDetails.getDileveryCharge());
            String finalPrice = "Final Amount" + " : "
                    + productDetails.getCurrencySymbol()
                    + String.format(Locale.ENGLISH, "%.2f", totalPrice);
            tvFinalPrice.setText(finalPrice);

        } else {
            fblTaxes.setVisibility(View.GONE);
        }
    }

    /**
     * function to set the taxes
     */
    private void setHuntTaxes() {
        String price = String.format(Locale.ENGLISH, "%.2f", this.price);
        tvPurchaseProductPrice.setText(TextUtils.concat(huntDeal.getCurrencySymbol() + price));
        if (huntDeal.getTaxArr() != null && huntDeal.getTaxArr().size() > 0) {
            fblTaxes.setVisibility(View.VISIBLE);
            fblTaxes.removeAllViews();
            totalTax = 0;
            for (TaxArr taxArr : huntDeal.getTaxArr()) {
                double taxPer = Double.parseDouble(taxArr.getTaxPercentage());
                double taxAmount = (this.price * taxPer) / 100;
                totalTax += taxAmount;
                String message = taxArr.getTaxName()
                        + " @"
                        + taxArr.getTaxPercentage()
                        + "% : "
                        + huntDeal.getCurrencySymbol()
                        + String.format(Locale.ENGLISH, "%.2f", taxAmount);
                fblTaxes.addView(taxView(message));
            }
            String finalTax = getString(R.string.total_tax) + " : "
                    + huntDeal.getCurrencySymbol()
                    + String.format(Locale.ENGLISH, "%.2f", totalTax);
//            fblTaxes.addView(taxView(finalTax));
            double totalPrice = this.price + totalTax;
            String finalPrice = "Final Amount" + " : "
                    + huntDeal.getCurrencySymbol()
                    + String.format(Locale.ENGLISH, "%.2f", totalPrice);
            tvFinalPrice.setText(finalPrice);

        } else {
            fblTaxes.setVisibility(View.GONE);
        }
    }


    /**
     * method to inflate taxes
     *
     * @param message
     * @return
     */
    private View taxView(String message) {
        final View view = LayoutInflater.from(this).inflate(R.layout.row_taxes, null);
        final CustomTextView textView = view.findViewById(R.id.tv_taxes);
        textView.setText(message);
        textView.setTag(message);
        return textView;
    }

    @OnClick({R.id.iv_menu, R.id.tv_add, R.id.btn_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                finish();
                break;
            case R.id.tv_add:
                startActivityForResult(new Intent(this, EnterAddressActivity.class), Constants.IntentConstant.REQUEST_ADDRESS);
                break;
            case R.id.btn_action:
                if (productDetails != null) {
                    productDetails.setTaxes(String.valueOf(totalTax));
                    if (productDetails.getProductType().equals("1") && etProductQuantity.getText().toString().trim().length() == 0) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_enter_quantity));
                    } else if (productDetails.getProductType().equals("1") && etProductQuantity.getText().toString().trim().equals("0")) {
                        AppUtils.getInstance().showToast(this, getString(R.string.quantity_greater_than_0));
                    } else if (addressList.size() == 0) {
                        AppUtils.getInstance().showToast(this, getString(R.string.please_enter_address));
                    } else {
                        if (!isLoading && AppUtils.getInstance().isInternetAvailable(this)) {
                            getProductQuantity();
                        }
                    }
                }
                if (huntDeal != null) {
                   openPaymentScreen();
                }
                break;
        }
    }

    /**
     * function to hit api to book for quantity
     */
    private void getProductQuantity() {
        isLoading = true;
        AppUtils.getInstance().setButtonLoaderAnimation(PurchaseSummaryActivity.this, btnAction, viewButtonLoader, viewButtonDot, true);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_DEAL_ID, productDetails.getId());
        params.put(Constants.NetworkConstant.PARAM_QUANTITY, productDetails.getProductType().equals("1") ?
                etProductQuantity.getText().toString().trim() : "0");
        params.put(Constants.NetworkConstant.PARAM_SLOT_ID_ARR, new Gson().toJson(
                productDetails.getProductType().equals("1") ? new JSONArray() : getSlotIdArray()));
        params.put(Constants.NetworkConstant.PARAM_TIME_ZONE, TimeZone.getDefault().getID());
        Call<ResponseBody> call = apiInterface.hitGetProductQuantityApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_GET_QUANTITY);
    }



    /**
     * method to get the slot id array
     */
    private ArrayList<HashMap<String, String>> getSlotIdArray() {
        ArrayList<HashMap<String, String>> slotList = new ArrayList<>();
        if (productDetails.getSelectedSlots() != null && !productDetails.getSelectedSlots().equals("")) {
            String[] slotIdArray = productDetails.getSelectedSlots().split(",");
            if (productDetails.getSelectedDates() != null) {
                for (String id : slotIdArray) {
                    for (String date : productDetails.getSelectedDates()) {
                        HashMap<String, String> slotMap = new HashMap<>();
                        slotMap.put(Constants.NetworkConstant.PARAM_SLOT_ID, id);
                        slotMap.put(Constants.NetworkConstant.PARAM_DATE, date);
                        slotList.add(slotMap);
                    }
                }
            }
        }
        return slotList;
    }

    /**
     * function to open payment screen
     */
    private void openPaymentScreen() {
        if (productDetails != null) {
            productDetails.setTaxes(String.valueOf(totalTax));
            if (productDetails.getProductType().equals("1") && etProductQuantity.getText().toString().trim().length() == 0) {
                AppUtils.getInstance().showToast(this, getString(R.string.please_enter_quantity));
            } else if (productDetails.getProductType().equals("1") && etProductQuantity.getText().toString().trim().equals("0")) {
                AppUtils.getInstance().showToast(this, getString(R.string.quantity_greater_than_0));
            } else if (addressList.size() == 0) {
                AppUtils.getInstance().showToast(this, getString(R.string.please_enter_address));
            } else {
                productDetails.setQuantity(etProductQuantity.getText().toString().trim());
                if (productDetails.getProductType().equals("1")) {
                    if (Integer.parseInt(productDetails.getQuantity()) <= totalQuantity) {
//                            hitGenerateChecksumApi();
                        startActivity(new Intent(this, PaymentActivity.class)
                                .putExtra(FirebaseConstants.ROOM_ID, roomId)
                                .putExtra(Constants.IntentConstant.PRODUCT_DETAILS, productDetails)
                                .putExtra(Constants.IntentConstant.DELIVERY_ADDRESS, addressList.get(previousPosition)));
                    } else {
                        AppUtils.getInstance().showToast(this, getString(R.string.sorry_we_have) + " "
                                + totalQuantity + " " + getString(R.string.txt_available));
                    }
                } else {
//                        hitGenerateChecksumApi();
                    startActivity(new Intent(this, PaymentActivity.class)
                            .putExtra(FirebaseConstants.ROOM_ID, roomId)
                            .putExtra(Constants.IntentConstant.PRODUCT_DETAILS, productDetails)
                            .putExtra(Constants.IntentConstant.DELIVERY_ADDRESS, addressList.get(previousPosition)));
                }
            }
        }
        if (huntDeal != null) {
            huntDeal.setTaxes(String.valueOf(totalTax));
            startActivity(new Intent(this, PaymentActivity.class)
                    .putExtra(FirebaseConstants.ROOM_ID, roomId)
                    .putExtra(Constants.IntentConstant.BUDDY_ID, buddy.getUserId())
                    .putExtra(Constants.IntentConstant.HUNT_DEAL, huntDeal));


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_ADDRESS && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null && data.hasExtra(Constants.IntentConstant.DELIVERY_ADDRESS)) {
                com.shopoholic.models.deliveryaddressresponse.Result addressResponse =
                        (com.shopoholic.models.deliveryaddressresponse.Result) data.getExtras().getSerializable(Constants.IntentConstant.DELIVERY_ADDRESS);
                if (addressList.size() > 0) {
                    addressList.get(previousPosition).setSelected(false);
                }
                if (addressList.size() == 3) {
                    addressList.remove(2);
                }
                addressList.add(0, addressResponse);
                addressList.get(0).setSelected(true);
                previousPosition = 0;
                addressAdapter.notifyDataSetChanged();
            }
            if (AppUtils.getInstance().isInternetAvailable(this)) hitGetAddressesApi();
        }
    }


    /**
     * Method to hit the Address api
     */
    private void hitGetAddressesApi() {
        progress.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        Call<ResponseBody> call = apiInterface.hitGetAddressesApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(this, call, this, Constants.NetworkConstant.REQUEST_GET_ADDRESS);

    }

    /**
     * function to get final price
     * @return
     */
    private String getFinalPrice() {
        String finalPrice;
        double taxes = 0.0 , deliveryCharges = 0.0;
        if(productDetails.getTaxes()!=null && !productDetails.getTaxes().equals("")){
            taxes = Double.parseDouble(productDetails.getTaxes());
        }
        if(productDetails.getDileveryCharge()!=null && !productDetails.getDileveryCharge().equals("")){
            deliveryCharges = Double.parseDouble(productDetails.getDileveryCharge());
        }
        finalPrice = String.format(Locale.ENGLISH, "%.2f",
                ((((Double.parseDouble(productDetails.getSellingPrice()) + taxes)
                        * Integer.parseInt(productDetails.getQuantity())) + deliveryCharges)));
        return finalPrice;
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        isLoading = false;
        progress.setVisibility(View.GONE);
        AppUtils.getInstance().setButtonLoaderAnimation(PurchaseSummaryActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_GET_ADDRESS:
                if (responseCode == Constants.NetworkConstant.SUCCESS_CODE) {
                    AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
                    DeliveryAddressResponse addressResponse = new Gson().fromJson(response, DeliveryAddressResponse.class);
                    addressList.clear();
                    addressList.addAll(addressResponse.getResult());
                    addressList.get(0).setSelected(true);
                    previousPosition = 0;
                    addressAdapter.notifyDataSetChanged();
                } else {
                    /*try {
                        AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }
                break;

                case Constants.NetworkConstant.REQUEST_GET_QUANTITY:
                    if (responseCode == Constants.NetworkConstant.SUCCESS_CODE) {
                        openPaymentScreen();
                    } else {
                        try {
                            AppUtils.getInstance().showToast(this, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            rvAddressList.requestFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onError(String response, int requestCode) {
        isLoading = false;
        progress.setVisibility(View.GONE);
        AppUtils.getInstance().setButtonLoaderAnimation(PurchaseSummaryActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        AppUtils.getInstance().showToast(this, response);
    }

    @Override
    public void onFailure() {
        isLoading = false;
        AppUtils.getInstance().setButtonLoaderAnimation(PurchaseSummaryActivity.this, btnAction, viewButtonLoader, viewButtonDot, false);
        progress.setVisibility(View.GONE);
    }
}
