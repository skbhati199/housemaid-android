package com.housemaid.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.housemaid.R;
import com.housemaid.model.CountryListModel;

import java.util.ArrayList;

/**
 * Created by fluper on 24/5/18.
 */


public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.MyViewHolder> implements View.OnClickListener {

    ArrayList<CountryListModel> items;
    OnItemCheckListener onItemClick;
    // public static ArrayList<String> nameList = new ArrayList<>();
    // public static ArrayList<String> idList = new ArrayList<>();


    @Override
    public void onClick(View v) {

    }

    public interface OnItemCheckListener {
        void onItemCheck(String name, String id);

        void onItemUncheck(String name, String id);
    }

    public EducationAdapter(ArrayList<CountryListModel> items, @NonNull OnItemCheckListener onItemCheckListener) {
        this.items = items;
        this.onItemClick = onItemCheckListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_list_checkbox_item, parent,
                false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final EducationAdapter.MyViewHolder holder, final int position) {
        holder.tvEducation.setText(items.get(position).getName());

        if (items.get(position).checked) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckBox checkBox;
        TextView tvEducation;
        RelativeLayout parent;

        public MyViewHolder(final View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkboxItem);
            tvEducation = itemView.findViewById(R.id.tvItem);
            parent = itemView.findViewById(R.id.relativeLayout);
            parent.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.relativeLayout:
                    String currentItem = items.get(getAdapterPosition()).getName();
                    int currentId = items.get(getAdapterPosition()).getId();
                    items.get(getAdapterPosition()).checked = !items.get(getAdapterPosition()).checked;

                    if (items.get(getAdapterPosition()).checked) {

                        onItemClick.onItemCheck(currentItem, String.valueOf(currentId));

                    } else {
                        onItemClick.onItemUncheck(currentItem, String.valueOf(currentId));

                    }
                    notifyDataSetChanged();
                    break;
            }
        }
    }
}