package com.housemaid.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.databinding.ActivityPastBookingsBinding;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.MyEditText;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fluper on 31/5/18.
 */

public class PastBookingsAdapter extends RecyclerView.Adapter<PastBookingsAdapter.MyViewHolder> {

    Context context;
    private ArrayList<UserDetailModel> userDetailModels;
    private ActivityPastBookingsBinding binding;
    String accessToken;
    SharedPreference sharedPreference;
    int key;
    float ratingMaid;

    public PastBookingsAdapter(Context context, ArrayList<UserDetailModel> userDetailModels,
                               ActivityPastBookingsBinding binding) {
        this.context = context;
        this.userDetailModels = userDetailModels;
        this.binding = binding;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_past_bookings_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        sharedPreference = SharedPreference.getInstance(context);
        accessToken = sharedPreference.getString("signUp_token", "");
        final UserDetailModel pastBookingDetail = userDetailModels.get(position);

        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            holder.ivListingMenu.setVisibility(View.GONE);
            key = 1;
        } else {
            key = 0;
            if (pastBookingDetail.getCompleted_status() == 1) {
                holder.cardView.setCardBackgroundColor(Color.LTGRAY);
            }
        }

        if (pastBookingDetail.getRated() == 1 || pastBookingDetail.getRated() > 1) {
            holder.btnRatingsAndReview.setText(R.string.review_successfully_posted);
            holder.btnRatingsAndReview.setEnabled(false);
        }


        String maidSkill = "";
        if (pastBookingDetail.getMaidSkillsModels() != null && pastBookingDetail.getMaidSkillsModels().size() > 0) {
            maidSkill = pastBookingDetail.getMaidSkillsModels().get(0).getSkill_name();
        } else maidSkill = "No Skills";

        if (pastBookingDetail.getMaidBookedImageModel() != null && pastBookingDetail.getMaidBookedImageModel().size() > 0) {

            Glide.with(context).load(pastBookingDetail.getMaidBookedImageModel().get(0).getImageModel().getSmall())
                    .centerCrop().into(holder.ivPic);
        } else holder.ivPic.setImageResource(R.drawable.user );

        holder.tvName.setText(pastBookingDetail.getName());
        holder.tvPhoneNo.setText(pastBookingDetail.getMobile());
        holder.tvLastUpdated.setText(pastBookingDetail.getUpdated_at());
        holder.tvJobChoice.setText(maidSkill);

        if (userDetailModels.get(position).getCompleted_status() == 1) {
            holder.ivListingMenu.setEnabled(false);
        }

        holder.ivListingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.completed_item) {

                                completePostBooking(accessToken,
                                        String.valueOf(pastBookingDetail.getMaid_id()),
                                        holder);
                                binding.progress.setVisibility(View.VISIBLE);
                                

                        
}
                        return false;
                    }
                });
                popup.inflate(R.menu.popup_complete);
                popup.show();
            }
        });

        holder.btnRatingsAndReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_ratings_and_reviews_layout);

                TextView tvName = dialog.findViewById(R.id.tvName);
                TextView tvJobChoice = dialog.findViewById(R.id.tvJobChoice);
                CircleImageView civProfile = dialog.findViewById(R.id.civProfilePic);
                final RatingBar ratings = dialog.findViewById(R.id.RatingsBar);

                //TODO: change here if issue of app crash occurs
                final MyEditText etReview = dialog.findViewById(R.id.etReview);
                final ProgressBar progressBar = dialog.findViewById(R.id.progress);


                ratings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        ratingMaid = rating;
                    }
                });

                tvName.setText(pastBookingDetail.getName());

                if (pastBookingDetail.getMaidSkillsModels().size() > 0 && pastBookingDetail.getMaidSkillsModels() != null) {
                    tvJobChoice.setText(pastBookingDetail.getMaidSkillsModels().get(0).getSkill_name());
                } else tvJobChoice.setText(R.string.no_skills);

                if (pastBookingDetail.getMaidBookedImageModel() != null && pastBookingDetail.getMaidBookedImageModel().size() > 0) {

                    Glide.with(context).load(pastBookingDetail.getMaidBookedImageModel().get(0).getImageModel().getSmall())
                            .centerCrop().into(civProfile);
                } else holder.ivPic.setImageResource(R.drawable.user);

                dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btnPost).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (key == 0) {
                            giveRatings(accessToken, String.valueOf(ratingMaid), String.valueOf(key),
                                    String.valueOf(pastBookingDetail.getMaid_id()),
                                    String.valueOf(pastBookingDetail.getJob_id()),
                                    etReview.getText().toString(), dialog, progressBar,
                                    holder);
                        } else {
                            giveRatings(accessToken, String.valueOf(ratingMaid), String.valueOf(key),
                                    String.valueOf(pastBookingDetail.getMaid_id()),
                                    String.valueOf(pastBookingDetail.getJob_id()),
                                    etReview.getText().toString(), dialog, progressBar, holder);
                        }
                    }
                });

                dialog.show();
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);
                }

            }
        });
    }

    private void giveRatings(String access_token, String ratings, String key, String rating_to_id,
                             String rating_to_job_id, String review,
                             final Dialog dialog, final ProgressBar progressBar, final MyViewHolder holder) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;

        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            call = apiService.giveRatingsToJobPost(access_token, ratings, key, rating_to_job_id,
                    review);
        } else
            call = apiService.giveRatingsToMaid(access_token, ratings, key, rating_to_id, review);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {
                        dialog.dismiss();
                        holder.btnRatingsAndReview.setText(R.string.reviews_successfully_posted);
                        holder.btnRatingsAndReview.setEnabled(false);


                    } else {

                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {

                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(context, SelectionActivity.class);
                            context.startActivity(signInIntent);

                        } else {
                            Toast.makeText(context, new Gson().fromJson(response.errorBody().string(),
                                    ErrorResponse.class).getMessage(), Toast.LENGTH_SHORT).show();
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void completePostBooking(String access_token, String maid_id,
                                     final MyViewHolder holder) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.completePastBooking(access_token, maid_id, "1");
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {
                        binding.progress.setVisibility(View.GONE);
                        holder.cardView.setCardBackgroundColor(Color.LTGRAY);

                    } else {

                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {
                        binding.progress.setVisibility(View.GONE);
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(context, SelectionActivity.class);
                            context.startActivity(signInIntent);


                        } else {
                            Toast.makeText(context, new Gson().fromJson(response.errorBody().string(),
                                    ErrorResponse.class).getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return userDetailModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public Button btnRatingsAndReview;
        TextView tvName;
        TextView tvPhoneNo;
        TextView tvLastUpdated;
        TextView tvJobChoice;
        ImageView ivPic;
        ImageView ivListingMenu;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            btnRatingsAndReview = itemView.findViewById(R.id.btnRatingsAndReview);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhoneNo = itemView.findViewById(R.id.tvPhoneNo);
            tvLastUpdated = itemView.findViewById(R.id.tvLastUpdated);
            tvJobChoice = itemView.findViewById(R.id.tvJobChoice);
            ivPic = itemView.findViewById(R.id.ivProfilePic);
            ivListingMenu = itemView.findViewById(R.id.ivListingMenu);
            cardView = itemView.findViewById(R.id.cardViewBooking);
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
