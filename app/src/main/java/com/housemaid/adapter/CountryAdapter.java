package com.housemaid.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.housemaid.R;
import com.housemaid.model.CountryListModel;
import com.housemaid.utils.MyBoldTextView;
import com.housemaid.utils.SharedPreference;

import java.util.ArrayList;

/**
 * Created by fluper on 15/5/18.
 */

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder> {
    Context context;
    private ArrayList<CountryListModel> listCountry;
    private String countryName;
    SharedPreference sharedPreference;
    private ArrayList<CountryListModel> newList;


    public CountryAdapter(Context context, ArrayList<CountryListModel> listCountry) {
        this.context = context;
        this.listCountry = listCountry;
        newList = listCountry;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.signle_row_country_layout, parent,
                false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvCountry.setText(listCountry.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return listCountry.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        MyBoldTextView tvCountry;
        RelativeLayout relativeLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countryName = listCountry.get(getAdapterPosition()).getName();

                }
            });

        }
    }

    public void setfilter(ArrayList<CountryListModel> newList) {
        listCountry = new ArrayList<>();
        listCountry.addAll(newList);
        notifyDataSetChanged();
    }
}
