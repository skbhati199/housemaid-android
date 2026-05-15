package com.housemaid.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.activities.user.fromHome.MyListingDetailsActivity;
import com.housemaid.databinding.FragmentActiveListingBinding;
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
 * Created by fluper on 1/6/18.
 */

public class MyActiveListingAdapter extends RecyclerView.Adapter<MyActiveListingAdapter.MyViewHolder> {

    Context context;
    private ArrayList<SignUpModel> jobListing;
    private String credits;
    private int totalCredits;
    int user_id;
    private int job_id;
    private String accessToken;
    private String address;
    private StringBuilder languages;
    private SharedPreference sharedPreference;
    private FragmentActiveListingBinding binding;

    public MyActiveListingAdapter(Context context, ArrayList<SignUpModel> jobListing,
                                  FragmentActiveListingBinding binding) {
        this.context = context;
        this.jobListing = jobListing;
        this.binding = binding;
        this.languages = new StringBuilder();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_mylisting_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        sharedPreference = SharedPreference.getInstance(context);
        accessToken = sharedPreference.getString("signUp_token", "");
        totalCredits = sharedPreference.getInteger("TotalCredits", 0);
        holder.tvJobName.setText(jobListing.get(position).getJob_listing_title_name());

        /*holder.tvLanguage.setText(jobListing.get(position).getLanguageModels().get(0)
                .getLanguage_name());*/
        for(int i=0;i<jobListing.get(position).getLanguageModels().size();i++){
            languages.append(jobListing.get(position).getLanguageModels().get(i).getLanguage_name());
            if(i<jobListing.get(position).getLanguageModels().size()-1){
                languages.append(", ");
            }
        }

        holder.tvLanguage.setText(languages);

        if (jobListing.get(position).getUserDetailModel().getUserImageModel().size() > 0) {
            /*Glide.with(context).load(jobListing.get(position).getUserDetailModel().getUserImagesModel()
                    .get(0).getImageModel().getBig())
                    .error(R.drawable.user).into(holder.ivProfilePic);*/
            Glide.with(context).load(jobListing.get(position).getImage().getSmall())
                    .error(R.drawable.user).into(holder.ivProfilePic);
        } else {
            holder.ivProfilePic.setImageResource(R.drawable.user);
        }

        address = String.format("%s, %s, %s",
                jobListing.get(position).getCity_name(),
                jobListing.get(position).getDistrict_name(),
                jobListing.get(position).getCountry_name());

        holder.cardMyActiveList.setOnClickListener(v->{
            //Do work here
            Intent intent = new Intent(context, MyListingDetailsActivity.class);
            intent.putExtra("ListingDetails", jobListing.get(position));
            /*intent.putExtra("listingAddress",address);
            intent.putExtra("listingSkillName",jobListing.get(position).getJob_listing_title_name());
            intent.putExtra("listingImgUrl",jobListing.get(position).getImage().getBig());*/
            intent.putExtra("listingLanguages", languages.toString());
            intent.putExtra("fromScreen",1);
            context.startActivity(intent);

        });

        holder.tvAddress.setText(String.format("%s, %s, %s",
                jobListing.get(position).getCity_name(),
                jobListing.get(position).getDistrict_name(),
                jobListing.get(position).getCountry_name()));

        holder.ivListingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(context, view);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.highlight_item) {

                                if (jobListing.get(position).getHighlight_job_status().equals("0")) {
                                    getCreditListing(accessToken, "13", "9", position);
                                    binding.progress.setVisibility(View.VISIBLE);
                                } else Toast.makeText(context, "Your list has been" +
                                        " already highlighted.", Toast.LENGTH_SHORT).show();
                                return true;

                            
} else if (item.getItemId() == R.id.delete_item) {

                                binding.progress.setVisibility(View.VISIBLE);
                                JobPost(accessToken,
                                        String.valueOf(jobListing.get(position).getId()),
                                        position, 2);
                                return true;

                            
} else if (item.getItemId() == R.id.completed_item) {

                                binding.progress.setVisibility(View.VISIBLE);
                                JobPost(accessToken,
                                        String.valueOf(jobListing.get(position).getId()),
                                        position, 1);
                                return true;

                        
}

                        return false;
                    }
                });


                popup.inflate(R.menu.popup_menu);
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobListing.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView tvJobName;
        private TextView tvLanguage;
        private TextView tvAddress;
        private ImageView ivProfilePic;
        private ImageView ivListingMenu;
        private CardView cardMyActiveList;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvJobName = itemView.findViewById(R.id.tvJobType);
            tvLanguage = itemView.findViewById(R.id.tvPersonLanguage);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            ivListingMenu = itemView.findViewById(R.id.ivListingMenu);
            cardMyActiveList = itemView.findViewById(R.id.card_my_active_list);
        }
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
                    binding.progress.setVisibility(View.GONE);
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
                Toast.makeText(context, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callDialogForMoreThenImages(final String key, final int position) {
        final Dialog dialog = new Dialog(context);
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
                user_id = jobListing.get(position).getUser_id();
                job_id = jobListing.get(position).getId();
                payCredit(accessToken, credits, String.valueOf(user_id), key, position,
                        String.valueOf(job_id), dialog);
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
                           final String key, final int position, final String job_id, final Dialog dialog) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditStatusApi> call = apiService.payCreditForListing(accessToken,
                credit, user_id, key, job_id);
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
                        Toast.makeText(context, R.string.your_list_has_been_highlighted, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    } else {

                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
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

    private void JobPost(String accessToken, String jobPostId, final int position, final int key) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        if (key == 1) call = apiService.completeJobPost(accessToken, jobPostId);
        else call = apiService.deleteJobPost(accessToken, jobPostId);
        call.enqueue(new Callback<RegisterApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (response.code() == 200) {
                        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();

                        if (key == 2) {
                            jobListing.remove(position);
                            notifyDataSetChanged();
                        }
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
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
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

}
