package com.housemaid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.housemaid.R;
import com.housemaid.model.bean.ChattingModel;
import com.housemaid.utils.SharedPreference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fluper on 19/7/18.
 */

public class SingleChatAdapter extends RecyclerView.Adapter<SingleChatAdapter.MyViewHolder> {

    private String myUserID;
    String message;
    private ArrayList<ChattingModel> messageList;
    Context context;
    SharedPreference sharedPreference;

    public SingleChatAdapter(Context context, ArrayList<ChattingModel> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_chat_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        sharedPreference = SharedPreference.getInstance(context);

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            myUserID = sharedPreference.getString("maid_id", "");
        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            myUserID = sharedPreference.getString("user_id", "");
        }
        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            myUserID = sharedPreference.getString("agency_Id", "");
        }

        String senderID = messageList.get(position).getSenderID();
        message = messageList.get(position).getMessage();
        String timeStamp = messageList.get(position).getTimeStamp();

        Date today = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = simpleDateFormat.format(today);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
        String dateString = formatter.format(new Date(Long.parseLong(timeStamp)));

        String[] a = dateString.split(",");
        String date = a[0];
        String time = a[1];

        if (myUserID.equals(senderID)) {
            holder.layoutSend.setVisibility(View.VISIBLE);

            if (date.equals(todayDate)) {
                holder.tvSendTime.setText(time);
            } else holder.tvSendTime.setText(date);

            holder.tvSendMessages.setText(message);

        } else {
            holder.layoutReceive.setVisibility(View.VISIBLE);

            if (date.equals(todayDate)) {
                holder.tvReceiveTime.setText(time);
            } else holder.tvReceiveTime.setText(date);

            holder.tvReceiveMessages.setText(message);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutSend;
        LinearLayout layoutReceive;
        TextView tvSendMessages;
        TextView tvSendTime;
        TextView tvReceiveMessages;
        TextView tvReceiveTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            layoutSend = itemView.findViewById(R.id.layoutSend);
            layoutReceive = itemView.findViewById(R.id.layoutRecieve);
            tvSendMessages = itemView.findViewById(R.id.tvChatTextSend);
            tvSendTime = itemView.findViewById(R.id.tvChatTimeSend);
            tvReceiveMessages = itemView.findViewById(R.id.tvChatTextRecieve);
            tvReceiveTime = itemView.findViewById(R.id.tvChatTimeRecieve);

        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
