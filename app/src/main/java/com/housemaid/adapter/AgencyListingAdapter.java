package com.housemaid.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.AgencyDetailsActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.SingleChatActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.databinding.ActivityAgencyBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.StatusModel;
import com.housemaid.model.response.CreditListingApi;
import com.housemaid.model.response.CreditStatusApi;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fluper on 29/5/18.
 */

public class AgencyListingAdapter extends RecyclerView.Adapter<AgencyListingAdapter.MyViewHolder> {

    ArrayList<SignUpModel> agencyDetailModelList;
    Context context;
    String accessToken;
    ActivityAgencyBinding binding;
    SharedPreference sharedPreference;
    private String credits;
    private int totalCredits;
    int user_id;
    Dialog dialog;
    String defaultImage;

    public AgencyListingAdapter(Context context, ArrayList<SignUpModel> agencyDetailModelList,
                                ActivityAgencyBinding binding) {
        this.context = context;
        this.agencyDetailModelList = agencyDetailModelList;
        this.binding = binding;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_list_item_agency, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        sharedPreference = SharedPreference.getInstance(context);
        accessToken = sharedPreference.getString("signUp_token", "0");
        totalCredits = sharedPreference.getInteger("TotalCredits", 0);
        defaultImage = context.getResources().getDrawable(R.drawable.user).toString();

        holder.tvName.setText(agencyDetailModelList.get(position).getName());
        holder.tvAddress.setText(String.format("%s, %s",
                agencyDetailModelList.get(position).getState_name(),
                agencyDetailModelList.get(position).getCountry_name()));


        if (!agencyDetailModelList.get(position).getUserImagesModel().isEmpty() &&
                !agencyDetailModelList.get(position).getUserImagesModel().get(0).getImageModel().getSmall().isEmpty()) {
            Glide.with(context).load(agencyDetailModelList.get(position).getUserImagesModel().get(0).getImageModel().getSmall())
                    .error(R.drawable.user).into(holder.ivProfilePic);
        } else {
            holder.ivProfilePic.setImageResource(R.drawable.user);
        }

        holder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (agencyDetailModelList.get(holder.getAdapterPosition()).getPaidStatusModel().getMessage_status()
                        .equals("1")) {
                    Intent intent = new Intent(context, SingleChatActivity.class);
                    intent.putExtra("touser_id", String.valueOf(agencyDetailModelList
                            .get(holder.getAdapterPosition()).getId()));
                    intent.putExtra("user_name", agencyDetailModelList
                            .get(holder.getAdapterPosition()).getName());
                    if (agencyDetailModelList.get(holder.getAdapterPosition()).getUserImagesModel().isEmpty()) {
                        intent.putExtra("user_image", defaultImage);
                    } else intent.putExtra("user_image", agencyDetailModelList
                            .get(holder.getAdapterPosition()).getUserImagesModel().get(0)
                            .getImageModel()
                            .getBig());
                    intent.putExtra("identifier", agencyDetailModelList.get(holder.getAdapterPosition())
                            .getUser_type());
                    context.startActivity(intent);

                } else {
                    binding.progress.setVisibility(View.VISIBLE);
                    getCreditListing(accessToken, "2", "6", holder);
                }

            }
        });
        holder.layoutAgency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AgencyDetailsActivity.class);
                intent.putExtra("agencyDetail", agencyDetailModelList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
        holder.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AgencyDetailsActivity.class);
                intent.putExtra("agencyDetail", agencyDetailModelList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });

       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MaidInfoActivity.class);
                context.startActivity(intent);
            }
        });*/
       /* holder.binding.setUserBean(userBeans.get(position));
        holder.binding.setHandler(new HomeListingHandler(context, userBeans.get(position)));
        holder.binding.executePendingBindings();*/
    }

    private void getCreditListing(final String accessToken, final String key, final String from,
                                  final MyViewHolder holder) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditListingApi> call = apiService.getCreditListing(accessToken, key);
        call.enqueue(new Callback<CreditListingApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditListingApi> call, Response<CreditListingApi> response) {

                if (response.isSuccessful()) {

                    CreditListingApi registerApi = response.body();
                    CreditListingApi.CreditListingModel creditListingModel =
                            registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {
                        binding.progress.setVisibility(View.GONE);
                        credits = creditListingModel.getCredit();
                        callDialogForMoreThenImages(from, holder.getAdapterPosition());

                    } else {

                        Toast.makeText(context,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        binding.progress.setVisibility(View.GONE);
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(context,
                                    SelectionActivity.class);
                            context.startActivity(signInIntent);

                        } else {
                            Toast.makeText(context, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CreditListingApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(context, "Error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callDialogForMoreThenImages(final String key, final int position) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_pay_credits);
        dialog.setCancelable(true);
        TextView tvCurrentCredits = dialog.findViewById(R.id.tvCurrentCredits);
        TextView tvPayCredit = dialog.findViewById(R.id.tvPayCredit);
        TextView btnPay = dialog.findViewById(R.id.btnPay);
        TextView btnByCredit = dialog.findViewById(R.id.btnByCredit);
        tvPayCredit.setText(credits);
        tvCurrentCredits.setText(String.valueOf(totalCredits));


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                user_id = agencyDetailModelList.get(position).getId();
                payCredit(accessToken, credits, String.valueOf(user_id), key, position);
            }
        });
        btnByCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,
                        UpgradeMemberShipActivity.class));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void payCredit(final String accessToken, String credit, final String user_id,
                           final String key, final int position) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditStatusApi> call = apiService.payCredit(accessToken, credit, user_id, key);
        call.enqueue(new Callback<CreditStatusApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditStatusApi> call, Response<CreditStatusApi> response) {

                if (response.isSuccessful()) {

                    CreditStatusApi registerApi = response.body();
                    StatusModel creditSatusModel = registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {

                        getTotalCredits(accessToken);

                        Intent intent = new Intent(context, SingleChatActivity.class);
                        intent.putExtra("touser_id", String.valueOf(agencyDetailModelList
                                .get(position).getId()));
                        intent.putExtra("user_name", agencyDetailModelList
                                .get(position).getName());
                        if (agencyDetailModelList.get(position).getUserImagesModel().isEmpty()) {
                            intent.putExtra("user_image", defaultImage);
                        } else intent.putExtra("user_image", agencyDetailModelList
                                .get(position).getUserImagesModel().get(0)
                                .getImageModel()
                                .getBig());
                        intent.putExtra("identifier", agencyDetailModelList.get(position)
                                .getUser_type());
                        context.startActivity(intent);
                        dialog.dismiss();

                    } else {

                        Toast.makeText(context,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(context,
                                    SelectionActivity.class);
                            context.startActivity(signInIntent);

                        } else {
                            Toast.makeText(context, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CreditStatusApi> call, Throwable t) {
                Toast.makeText(context, "Error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalCredits(String accessToken) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getTotalCredit(accessToken);
        call.enqueue(new Callback<RegisterApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    SignUpModel signUpModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        sharedPreference.putInteger("TotalCredits", signUpModel.getTotal_credit());
                        totalCredits = signUpModel.getTotal_credit();

                    } else {

                        Toast.makeText(context,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(context,
                                    SelectionActivity.class);
                            context.startActivity(signInIntent);

                        } else {
                            Toast.makeText(context, ""
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
                Toast.makeText(context, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });


    }


    @Override
    public int getItemCount() {
        return agencyDetailModelList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMessage;
        LinearLayout layoutAgency;
        TextView tvAddress;
        TextView tvName;
        ImageView ivProfilePic;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            layoutAgency = itemView.findViewById(R.id.layoutAgency);
            tvAddress = itemView.findViewById(R.id.tvPersonAddress);
            tvName = itemView.findViewById(R.id.tvPersonName);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);

        }
    }
}
