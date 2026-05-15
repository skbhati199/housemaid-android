package com.housemaid.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.agency.fromHome.AgencyProfileActivity;
import com.housemaid.activities.maid.fromHome.activity.MaidHomeProfileActivity;
import com.housemaid.activities.user.fromHome.UserProfileActivity;
import com.housemaid.databinding.ActivityNotificationBinding;
import com.housemaid.model.NotificationListModel;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.MyButton;
import com.housemaid.utils.SharedPreference;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// AQuery removed

/**
 * Created by fluper on 31/5/18.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    Context context;
    private ArrayList<NotificationListModel> notificationListModelArrayList;
    private String senderId;
    String accessToken;
    SharedPreference sharedPreference;
    private ActivityNotificationBinding binding;
    private Date date;

    public NotificationAdapter(Context context, ArrayList<NotificationListModel>
            notificationListModelArrayList, ActivityNotificationBinding binding) {
        this.context = context;
        this.notificationListModelArrayList = notificationListModelArrayList;
        this.binding = binding;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_notification_item, parent,
                false);
        sharedPreference = SharedPreference.getInstance(context);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        accessToken = sharedPreference.getString("signUp_token", "0");
        String senderName = notificationListModelArrayList.get(position).getSender_name();

        if (!notificationListModelArrayList.get(position).getSender_id().isEmpty())
            senderId = notificationListModelArrayList.get(position).getSender_id();

        String time = notificationListModelArrayList.get(position).getCreated_at();

        String notificationType = notificationListModelArrayList.get(position).getNotification_text();
        String notificationMessage = notificationListModelArrayList.get(position).getBody_message();
        String path = notificationListModelArrayList.get(position).getSender_images();

        holder.tvNotificationType.setText(notificationType);
        holder.tvNotificationMessage.setText(notificationMessage);
        if (notificationListModelArrayList.get(position).getNotification_text().equals("Request for Vedio call")) {
            holder.layoutButton.setVisibility(View.VISIBLE);
            holder.tvTime.setVisibility(View.GONE);
        }
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progress.setVisibility(View.VISIBLE);
                sendNotification(accessToken,
                        notificationListModelArrayList.get(holder.getAdapterPosition()).getSender_id()
                        , 1, holder.getAdapterPosition());
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progress.setVisibility(View.VISIBLE);
                sendNotification(accessToken,
                        notificationListModelArrayList.get(holder.getAdapterPosition()).getSender_id(),
                        1, holder.getAdapterPosition());
            }
        });

        holder.tvTime.setText(time);
        holder.tvName.setText(senderName);
        if (!path.isEmpty())
            Glide.with(itemView.getContext()).load(path).error(R.drawable.men_icon).into(holder.civImage);
        else holder.civImage.setImageResource(R.drawable.app_icon);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!notificationListModelArrayList.get(holder.getAdapterPosition()).getSender_id().isEmpty())
                    getUserDetails(accessToken, Integer.parseInt(senderId));

            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationListModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotificationType;
        TextView tvNotificationMessage;
        AppCompatImageView civImage;
        TextView tvTime;
        TextView tvName;
        CardView cardView;
        CardView cardViewImage;
        LinearLayout layoutButton;
        MyButton btnAccept, btnReject;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvNotificationType = itemView.findViewById(R.id.tvNotificationType);
            tvNotificationMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            civImage = itemView.findViewById(R.id.civProfilePic);
            tvName = itemView.findViewById(R.id.tvName);
            cardView = itemView.findViewById(R.id.cardViewNotification);
            cardViewImage = itemView.findViewById(R.id.imageCard);
            layoutButton = itemView.findViewById(R.id.layoutButton);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }

    private void getUserDetails(String access_token, int sender_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getUserDetails(access_token, sender_id);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    SignUpModel signUpModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (signUpModel.getUser_type().equals("user")) {
                            Intent intent = new Intent(context, UserProfileActivity.class);
                            intent.putExtra("userProfile", signUpModel);
                            context.startActivity(intent);
                        } else if (signUpModel.getUser_type().equals("maid")) {
                            Intent intent = new Intent(context, MaidHomeProfileActivity.class);
                            intent.putExtra("maidDetail", signUpModel);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, AgencyProfileActivity.class);
                            intent.putExtra("agencyProfile", signUpModel);
                            context.startActivity(intent);
                        }

                    } else {

                        Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (getContext());
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(getContext(), SelectionActivity.class);
                            context.startActivity(signInIntent);

                        } else {
                            Toast.makeText(getContext(), ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {

                Toast.makeText(context, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotification(String access_token, String user_id, int key, final int position) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        call = apiService.sendAcceptRejectRequest(access_token, user_id, key);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call,
                                   Response<RegisterApi> response) {

                if (response.isSuccessful() && response.code() == 200) {
                    binding.progress.setVisibility(View.GONE);
                    String message = response.body().getMessage();
                    if (message != null) {

                        notificationListModelArrayList.remove(position);
                        notifyDataSetChanged();

                    } else {


                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);

            }
        });
    }
}
