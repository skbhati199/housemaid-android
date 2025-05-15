package com.housemaid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.housemaid.R;
import com.housemaid.activities.PaymentGatwayActivity;
import com.housemaid.model.SignUpModel;
import com.housemaid.utils.SharedPreference;

import java.util.ArrayList;

/**
 * Created by fluper on 31/5/18.
 */

public class UpgradeMembershipAdapter extends RecyclerView.Adapter<UpgradeMembershipAdapter.MyViewHolder> {

    Context context;
    private ArrayList<SignUpModel> offersList;
    SharedPreference sharedPreference;

    public UpgradeMembershipAdapter(Context context, ArrayList<SignUpModel> offersList) {
        this.offersList = offersList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_subscription_item_layout, parent, false);
        sharedPreference = SharedPreference.getInstance(context);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {


        holder.tvPrice.setText(offersList.get(position).getPrice() + "TL");
        holder.tvCredit.setText(offersList.get(position).getCredit_value() + " Credits");

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            holder.tvDescription1.setText("Apply on " + offersList.get(position).getApply_listing()
                    + " listings");

            holder.tvDescription2.setText("Highlight profile "
                    + offersList.get(position).getHighlight_profile_status());

        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            holder.tvDescription1.setText("Add " + offersList.get(position).getJob_listing()
                    + " job listing");

            holder.tvDescription2.setVisibility(View.GONE);

        }
        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            holder.tvDescription1.setText("Add " + offersList.get(position).getAdd_maid()
                    + " maids");

            holder.tvDescription2.setText("Highlight profile "
                    + offersList.get(position).getHighlight_profile_status());

        }

        holder.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PaymentGatwayActivity.class);
                intent.putExtra("offerid", offersList.get(position).getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Button btnBuy;
        TextView tvPrice, tvCredit, tvDescription1, tvDescription2;

        public MyViewHolder(View itemView) {

            super(itemView);
            btnBuy = itemView.findViewById(R.id.btnBuy);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCredit = itemView.findViewById(R.id.tvCredits);
            tvDescription1 = itemView.findViewById(R.id.tvDescription1);
            tvDescription2 = itemView.findViewById(R.id.tvDescription2);
        }
    }
}
