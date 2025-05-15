package com.housemaid.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.SingleChatActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.activities.user.fromHome.MaidInfoActivity;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.StatusModel;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.CreditListingApi;
import com.housemaid.model.response.CreditStatusApi;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fluper on 26/6/18.
 */

public class MyFavoritesMaidAdapter extends RecyclerView.Adapter<MyFavoritesMaidAdapter.MyViewHolder> {


    Context context;
    private ArrayList<UserDetailModel> favouriteListing;
    private callMethod callMethod;
    SharedPreference sharedPreference;
    private String accessFrom;
    private String credits;
    private int totalCredits;
    int user_id;
    String accessToken;
    Dialog dialog;
    String monthName;
    String[] months;
    String createTime;

    public MyFavoritesMaidAdapter(Context context, ArrayList<UserDetailModel> favouriteListing,
                                  callMethod callMethod, String accessFrom) {
        this.context = context;
        this.favouriteListing = favouriteListing;
        this.callMethod = callMethod;
        this.accessFrom = accessFrom;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_item_home_list_layout, parent,
                false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        sharedPreference = SharedPreference.getInstance(context);
        accessToken = sharedPreference.getString("signUp_token", "0");

        totalCredits = sharedPreference.getInteger("TotalCredits", 0);
        holder.tvName.setText(favouriteListing.get(position).getName());

        if (favouriteListing.get(position).getUserLanguageModel().size() > 0) {
            holder.tvLanguage.setText(favouriteListing.get(position).
                    getUserLanguageModel().get(0).getLanguage_detail().getName());
        } else holder.tvLanguage.setText("No Language");



        String createdAt = favouriteListing.get(position).getCreated_at();
        String[] a = createdAt.split(" ");
        String date = a[0];
        String time = a[1];

        String[] b = date.split("-");
        String year = b[0];
        String month = b[1];
        String day = b[2];


        String[] c = time.split(":");
        String hour = c[0];
        String minute = c[1];

        months = context.getResources().getStringArray(R.array.month);
        monthName = months[Integer.parseInt(month)-1];

        if (Integer.parseInt(hour)<12)  createTime = hour+":"+minute+" "+"am";
        else {
            hour = String.valueOf(Integer.parseInt(hour)-12);
            createTime = hour+":"+minute+" "+"pm";
        }

        holder.tvCreatedAt.setText(day+" "+monthName+" "+year+", "+createTime);

        if (favouriteListing.get(position).getMaidImageModel().size() > 0) {

            Picasso.get().load(favouriteListing.get(position).getMaidImageModel()
                    .get(0).getImageModel().getSmall())
                    .fit()
                    .error(R.drawable.user).
                    into(holder.ivProfilePic);
        } else holder.ivProfilePic.setImageResource(R.drawable.user);

        holder.tvAddress.setText(String.format("%s, %s",
                favouriteListing.get(position).getState_name(),
                favouriteListing.get(position).getCountry_name()));

        holder.tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=com.housemaid");
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, "Send To"));
            }
        });


        holder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouriteListing.get(holder.getAdapterPosition()).getPaidStatusModel().getMessage_status()
                        .equals("1")) {
                    Intent intent = new Intent(context, SingleChatActivity.class);
                    intent.putExtra("touser_id", String.valueOf(favouriteListing
                            .get(holder.getAdapterPosition()).getId()));
                    intent.putExtra("user_name", favouriteListing.get(holder.getAdapterPosition())
                            .getName());
                    intent.putExtra("user_image", favouriteListing.get(holder.getAdapterPosition())
                            .getMaidImageModel().get(0)
                            .getImageModel().getBig());
                    intent.putExtra("identifier", favouriteListing.get(holder.getAdapterPosition())
                            .getUser_type());
                    context.startActivity(intent);

                } else getCreditListing(accessToken, "2", "6", holder.getAdapterPosition());

            }
        });

        holder.layoutSingleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MaidInfoActivity.class);
                sharedPreference.putString("list_type", "favourite");
                intent.putExtra("favouriteListingDetail", favouriteListing.get(holder.getAdapterPosition()));
                intent.putExtra("accessFrom", accessFrom);
                context.startActivity(intent);
            }
        });

        if (favouriteListing.get(position).getIs_favourite() == 0) {
            holder.checkfavourite.setChecked(false);
        } else {

            holder.checkfavourite.setChecked(true);
        }
        //when we click on favourite we set the favourite to true.
        // and set the key opposite to boolean value.
        holder.layoutFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isfav = favouriteListing.get(holder.getAdapterPosition()).getIs_favourite();

                if (isfav == 0)
                    favouriteListing.get(holder.getAdapterPosition()).setIs_favourite(1);
                else favouriteListing.get(holder.getAdapterPosition()).setIs_favourite(0);

                int key = isfav == 0 ? 1 : 0;
                int jobListId = favouriteListing.get(holder.getAdapterPosition()).getId();

                sharedPreference.putInteger("favouritekey", key);

                //we call interface and call the makeFavourite API.
                callMethod.makeUnfavourite(key, favouriteListing.get(holder.getAdapterPosition()).getId(),
                        holder.getAdapterPosition());

            }
        });
    }

    private void getCreditListing(final String accessToken, final String key, final String from,
                                  final int position) {
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

                        credits = creditListingModel.getCredit();
                        callDialogForMoreThenImages(from, position);

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
            public void onFailure(retrofit2.Call<CreditListingApi> call, Throwable t) {
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

        dialog.show();
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                user_id = favouriteListing.get(position).getId();
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
                        intent.putExtra("touser_id", String.valueOf(favouriteListing
                                .get(position).getId()));
                        intent.putExtra("user_name", favouriteListing.get(position)
                                .getName());
                        intent.putExtra("user_image", favouriteListing.get(position)
                                .getMaidImageModel().get(0)
                                .getImageModel().getBig());
                        intent.putExtra("identifier", favouriteListing.get(position)
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
        return favouriteListing.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layoutSingleItem;
        private ImageView ivProfilePic;
        private TextView tvMessage;
        private TextView tvName;
        private TextView tvLanguage;
        private TextView tvCreatedAt;
        private TextView tvAddress;
        private LinearLayout layoutFavourite;
        private CheckBox checkfavourite;
        private TextView tvShare;

        public MyViewHolder(View itemView) {
            super(itemView);
            layoutSingleItem = itemView.findViewById(R.id.layoutSingleItem);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvName = itemView.findViewById(R.id.tvPersonName);
            tvLanguage = itemView.findViewById(R.id.tvPersonLanguage);
            tvCreatedAt = itemView.findViewById(R.id.tvUpdatedAt);
            tvAddress = itemView.findViewById(R.id.tvPersonAddress);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            layoutFavourite = itemView.findViewById(R.id.layoutFavorite);
            checkfavourite = itemView.findViewById(R.id.checkboxFavorite);
            tvShare = itemView.findViewById(R.id.tvShare);
        }
    }
}