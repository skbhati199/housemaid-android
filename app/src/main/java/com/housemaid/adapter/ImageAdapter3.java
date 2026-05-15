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
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.activities.maid.fromHome.activity.EditMaidPofileActivity;
import com.housemaid.databinding.ActivityEditMaidPofileBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.CreditListingApi;
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
 * Created by fluper on 31/8/18.
 */

public class ImageAdapter3 extends RecyclerView.Adapter<ImageAdapter3.MyViewHolder> {
    Context context;
    private ArrayList<String> imageList;
    private ArrayList<String> bigImageList;
    private ActivityEditMaidPofileBinding binding;
    SharedPreference sharedPreference;
    private ArrayList<Integer> imageIdList;
    private int i;
    private String credits;
    private int totalCredits;
    String accessToken;
    Dialog dialog;

    public ImageAdapter3(Context context, ArrayList<String> imageList, ArrayList<String> bigImageList,
                         ActivityEditMaidPofileBinding binding, ArrayList<Integer> imageIdList) {
        this.context = context;
        this.imageList = imageList;
        this.bigImageList = bigImageList;
        this.binding = binding;
        this.imageIdList = imageIdList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_image_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        sharedPreference = SharedPreference.getInstance(context);
        accessToken = sharedPreference.getString("signUp_token", "0");
        totalCredits = sharedPreference.getInteger("TotalCredits", 0);

        i = 5 - (imageList.size());

        if (i == 0) {
            if (!imageList.get(position).isEmpty()) {
                Glide.with(itemView.getContext()).load(imageList.get(position))
                        .error(R.drawable.user).into(holder.ivProfilePic);
            } else holder.ivProfilePic.setImageResource(R.drawable.user);
        }
        if (position <= imageList.size() - 1) {
            if (!imageList.get(position).isEmpty()) {
                Glide.with(itemView.getContext()).load(imageList.get(position))
                        .error(R.drawable.user).into(holder.ivProfilePic);
            } else holder.ivProfilePic.setImageResource(R.drawable.user);

            holder.tvCreditPrice.setVisibility(View.GONE);

        } else {
            holder.ivProfilePic.setImageResource(R.drawable.user);
        }

        holder.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() <= bigImageList.size() - 1) {
                    ((EditMaidPofileActivity) context).updateList(holder.getAdapterPosition(), imageIdList);

                    if (!bigImageList.get(holder.getAdapterPosition()).isEmpty()) {
                        Glide.with(itemView.getContext()).load(bigImageList.get(holder.getAdapterPosition()))
                                .error(R.drawable.user).into(binding.ivProfilePicBig);
                    } else binding.ivProfilePicBig.setImageResource(R.drawable.user);

                } else {
                    ((EditMaidPofileActivity) context).updateList(holder.getAdapterPosition(), imageIdList);
                    binding.ivProfilePicBig.setImageResource(R.drawable.user);
                    getCreditListing(accessToken, "1", holder.getAdapterPosition(), holder);
                }
            }
        });

    }

    private void getCreditListing(final String accessToken, final String key, final int position, final ImageAdapter3.MyViewHolder holder) {
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

                        if (position == 1) {
                            credits = creditListingModel.getSlot1();
                        }
                        if (position == 2) {
                            credits = creditListingModel.getSlot2();
                        }
                        if (position == 3) {
                            credits = creditListingModel.getSlot3();
                        }
                        if (position == 4) {
                            credits = creditListingModel.getSlot4();
                        }

                        callDialogForMoreThenImages(position, holder);

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
                            Toast.makeText(context, "" + response.errorBody().toString(),
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

    @Override
    public int getItemCount() {
        return 5;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;

        TextView tvCreditPrice;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvCreditPrice = itemView.findViewById(R.id.tvCreditPrice);
        }

    }

    private void callDialogForMoreThenImages(final int position, final ImageAdapter3.MyViewHolder holder) {
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
                String user_id = sharedPreference.getString("maid_id", "");
                payCredit(accessToken, Integer.parseInt(credits), user_id, position, holder);
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

    private void payCredit(final String accessToken, int credit, String user_id, final int position,
                           final ImageAdapter3.MyViewHolder holder) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.payCreditForImage(accessToken, credit, user_id);
        call.enqueue(new Callback<RegisterApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApi registerApi = response.body();
                    SignUpModel creditStatusModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        int imageId = creditStatusModel.getUserImageModel().getId();
                        imageIdList.set(position, imageId);
                        imageList.add(context.getResources().getDrawable(R.drawable.user).toString());
                        bigImageList.add(context.getResources().getDrawable(R.drawable.user).toString());
                        notifyDataSetChanged();
                        if (position <= imageList.size() - 1) {
                            if (!imageList.get(position).isEmpty()) {
                                Glide.with(itemView.getContext()).load(imageList.get(position))
                                        .error(R.drawable.avatar).into(holder.ivProfilePic);
                            } else holder.ivProfilePic.setImageResource(R.drawable.user);

                            holder.tvCreditPrice.setVisibility(View.GONE);

                        } else {
                            holder.ivProfilePic.setImageResource(R.drawable.user);
                        }

                    } else {

                        Toast.makeText(context,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(context, SelectionActivity.class);
                            context.startActivity(signInIntent);

                        } else {
                            Toast.makeText(context, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class)
                                            .getMessage(),
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
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(context, "Error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}

