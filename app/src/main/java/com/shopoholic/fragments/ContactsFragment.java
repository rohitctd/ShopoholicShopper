package com.shopoholic.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.adapters.ContactsAdapter;
import com.shopoholic.customviews.CustomEditText;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;
import com.shopoholic.models.contactsresponse.ContactsResponse;
import com.shopoholic.models.contactsresponse.Result;
import com.shopoholic.network.ApiCall;
import com.shopoholic.network.ApiInterface;
import com.shopoholic.network.NetworkListener;
import com.shopoholic.network.RestApi;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.shopoholic.utils.Constants.NetworkConstant.REQUEST_SMS;


public class ContactsFragment extends Fragment implements NetworkListener {


    @BindView(R.id.et_search)
    CustomEditText etSearch;
    @BindView(R.id.iv_cross)
    ImageView ivCross;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
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
    private Unbinder unbinder;
    private ContactsAdapter contactsAdapter;
    private ArrayList<Result> contactsList = new ArrayList<>();
    private AppCompatActivity mActivity;
    private boolean isLoading;
    private boolean isMoreData;
    private int count = 0;
    private LinearLayoutManager linearLayoutManager;
    private boolean isPagination;


    public ContactsFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        unbinder = ButterKnife.bind(this, view);
        mActivity = (AppCompatActivity) getActivity();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        setAdapters();
        setListeners();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            count = 0;
            progressBar.setVisibility(View.VISIBLE);
            hitGetContactsListApi();
        }
        return view;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * method to set adapter in views
     */
    private void setAdapters() {
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        contactsAdapter = new ContactsAdapter(mActivity, contactsList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                if (view.getId() == R.id.iv_friend_contact_add) {
                    if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                        progressBar.setVisibility(View.VISIBLE);
                        hitSendMessageApi(contactsList.get(position).getMobile());
                    }
                }
            }
        });
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setAdapter(contactsAdapter);
    }


    /**
     * method to set listener on view
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    count = 0;
                    hitGetContactsListApi();
                }
            }
        });
        recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMoreData && !isLoading && !isPagination) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalVisibleItems = linearLayoutManager.getItemCount();
                    if (firstVisibleItemPosition + totalVisibleItems >= contactsList.size() - 4) {
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            isPagination = true;
                            hitGetContactsListApi();
                        }
                    }
                }
            }

        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                ivCross.setVisibility(etSearch.getText().toString().length() > 0 ? View.VISIBLE : View.GONE);
                if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    count = 0;
                    hitGetContactsListApi();
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * methods to get contact list api
     */
    private void hitGetContactsListApi() {
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.DEVICE_ID, Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID));
        params.put(Constants.NetworkConstant.TYPE, "");
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        params.put(Constants.NetworkConstant.PARAM_SEARCH, etSearch.getText().toString().trim());
        Call<ResponseBody> call = apiInterface.contacts(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_FRIENDS);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_FRIENDS:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            ContactsResponse contactResult = new Gson().fromJson(response, ContactsResponse.class);
                            if (!isPagination) {
                                contactsList.clear();
                            } else {
                                isPagination = false;
                            }
                            contactsList.addAll(contactResult.getResult());
                            contactsAdapter.notifyDataSetChanged();
                            isMoreData = contactResult.getNext() != -1;
                            if (isMoreData) count = contactResult.getNext();
                            if (contactsList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;

                        case Constants.NetworkConstant.NO_DATA:
                            contactsList.clear();
                            contactsAdapter.notifyDataSetChanged();
                            if (contactsList.size() == 0) {
                                layoutNoDataFound.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoDataFound.setVisibility(View.GONE);
                            }
                            break;
                    }
                    break;

                case REQUEST_SMS:
                    try {
                        AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            contactsAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (requestCode == Constants.NetworkConstant.REQUEST_FRIENDS) {
                if (contactsList.size() == 0) {
                    layoutNoDataFound.setVisibility(View.VISIBLE);
                } else {
                    layoutNoDataFound.setVisibility(View.GONE);
                }
            }
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            isPagination = false;
            progressBar.setVisibility(View.GONE);
            contactsAdapter.notifyDataSetChanged();
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            /*if (contactsList.size() == 0) {
                layoutNoDataFound.setVisibility(View.VISIBLE);
            } else {
                layoutNoDataFound.setVisibility(View.GONE);
            }*/
        }
    }

    @OnClick(R.id.iv_cross)
    public void onViewClicked() {
        if (etSearch.getText().toString().trim().length() > 0) {
            etSearch.setText("");
        }
    }



    /**
     * Method to hit the change language api
     * @param mobileNumber
     */
    private void hitSendMessageApi(final String mobileNumber) {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        String language = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE_CODE);
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_MOBILE_NO, mobileNumber);
        params.put(Constants.NetworkConstant.PARAM_LANGUAGE, language.equals("") ? "1" : language);
        Call<ResponseBody> call = apiInterface.hitSendMessageApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, REQUEST_SMS);
    }
}
