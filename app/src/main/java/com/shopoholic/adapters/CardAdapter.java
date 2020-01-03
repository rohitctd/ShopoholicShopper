package com.shopoholic.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shopoholic.R;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.interfaces.RecyclerCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardHolder> {

    @BindView(R.id.iv_visa_logo)
    ImageView ivVisaLogo;
    @BindView(R.id.tv_card_number)
    CustomTextView tvCardNumber;
    @BindView(R.id.tv_user_card_number)
    CustomTextView tvUserCardNumber;
    @BindView(R.id.tv_card_holder)
    CustomTextView tvCardHolder;
    @BindView(R.id.tv_card_holder_name)
    CustomTextView tvCardHolderName;
    @BindView(R.id.tv_expires)
    CustomTextView tvExpires;
    @BindView(R.id.tv_expiry_date)
    CustomTextView tvExpiryDate;
    @BindView(R.id.rl_card_payment)
    RelativeLayout rlCardPayment;
    private Context mContext;
    private RecyclerCallBack recyclerCallBack;

    public CardAdapter(Context mContext, RecyclerCallBack recyclerCallBack) {
        this.mContext = mContext;
        this.recyclerCallBack = recyclerCallBack;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_payments, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class CardHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_card_payment)
        RelativeLayout rlCardPayment;

        CardHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.rl_card_payment})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.rl_card_payment:
                    recyclerCallBack.onClick(getAdapterPosition(), rlCardPayment);
                    break;
            }
        }
    }
}
