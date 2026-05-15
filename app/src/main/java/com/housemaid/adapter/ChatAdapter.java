package com.housemaid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.housemaid.R;
import com.housemaid.activities.SingleChatActivity;
import com.housemaid.model.bean.ChattingModel;
import com.housemaid.utils.SharedPreference;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fluper on 19/7/18.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    String myUserID;
    String myUserName;
    String senderID;
    String receiverID;
    String lastMessage;
    String receiverName;
    String timeStamp;
    String identifier;
    String image;
    String senderName;
    String name;
    String senderProfileImage;
    String receiverProfileImage;
    String receiver_id;

    ArrayList<ChattingModel> recentChatList;
    Context context;
    SharedPreference sharedPreference;

    public ChatAdapter(Context context, ArrayList<ChattingModel> messageList, String myUserID,
                       String myUserName, String senderProfileImage) {
        this.context = context;
        this.recentChatList = messageList;
        this.myUserID = myUserID;
        this.myUserName = myUserName;
        this.senderProfileImage = senderProfileImage;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recent_chat_layout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        sharedPreference = SharedPreference.getInstance(context);


        senderID = recentChatList.get(position).getSenderID();
        receiverID = recentChatList.get(position).getReceiverID();

        if (senderID.equals(myUserID) || receiverID.equals(myUserID)) {

            senderName = recentChatList.get(position).getSenderName();
            receiverName = recentChatList.get(position).getReceiverName();
            lastMessage = recentChatList.get(position).getMessage();
            timeStamp = recentChatList.get(position).getTimeStamp();
            receiverProfileImage = recentChatList.get(position).getReceiverProfileImage();
            senderProfileImage = recentChatList.get(position).getSenderProfileImage();


            if (myUserID.equals(senderID)) {

                receiver_id = receiverID;
                name = receiverName;
                image = receiverProfileImage;
                identifier = recentChatList.get(position).getUserIdentifierReceiver();

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, SingleChatActivity.class);
                        intent.putExtra("touser_id", recentChatList.get(position).getReceiverID());
                        intent.putExtra("user_name", recentChatList.get(position).getReceiverName());
                        intent.putExtra("user_image", recentChatList.get(position).getReceiverProfileImage());
                        intent.putExtra("identifier", recentChatList.get(position).getUserIdentifierReceiver());
                        context.startActivity(intent);
                    }
                });

            } else {

                receiver_id = senderID;
                name = senderName;
                image = senderProfileImage;
                identifier = recentChatList.get(position).getUserIdentifierSender();

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, SingleChatActivity.class);
                        intent.putExtra("touser_id", recentChatList.get(position).getSenderID());
                        intent.putExtra("user_name", recentChatList.get(position).getSenderName());
                        intent.putExtra("user_image", recentChatList.get(position).getSenderProfileImage());
                        intent.putExtra("identifier", recentChatList.get(position).getUserIdentifierSender());
                        context.startActivity(intent);

                    }
                });
            }

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

            if (date.equals(todayDate)) {
                holder.tvTime.setText(time);
            } else holder.tvTime.setText(date);

            Glide.with(context).load(image)
                    .error(R.drawable.user)
                    .into(holder.ivProfilePic);

            holder.tvReceiverName.setText(name);
            holder.tvLastMessage.setText(lastMessage);
            holder.tvUserType.setText(" " + identifier);

            if (identifier.equals("maid")) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorSkyBlue));
            }
            if (identifier.equals("user")) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorLightGreen));
            }
            if (identifier.equals("provider")) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorLightYellow));
            }

        }
    }

    @Override
    public int getItemCount() {
        return recentChatList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;

        TextView tvReceiverName;
        TextView tvTime;
        TextView tvUserType;
        TextView tvLastMessage;
        ImageView ivProfilePic;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewBooking);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvReceiverName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUserType = itemView.findViewById(R.id.tvUserType);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
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
