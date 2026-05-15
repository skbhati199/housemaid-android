package com.housemaid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
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
import com.housemaid.activities.user.fromHome.MyListingDetailsActivity;
import com.housemaid.databinding.FragmentPassiveListingBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fluper on 1/6/18.
 */

public class MyPassiveListingAdapter extends RecyclerView.Adapter<MyPassiveListingAdapter.MyViewHolder> {

    Context context;
    private ArrayList<SignUpModel> jobListing;
    private FragmentPassiveListingBinding binding;
    SharedPreference sharedPreference;
    private String accessToken;
    private String address;
    private StringBuilder languages;

    public MyPassiveListingAdapter(Context context, ArrayList<SignUpModel> jobListing, FragmentPassiveListingBinding binding) {
        this.context = context;
        this.jobListing = jobListing;
        this.binding = binding;
        languages = new StringBuilder();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.single_mylisting_item_layout, parent,
                false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        sharedPreference = SharedPreference.getInstance(context);
        accessToken = sharedPreference.getString("signUp_token", "");

        holder.tvListingType.setText(R.string.waiting_for_approval);

        holder.tvJobName.setText(jobListing.get(position).getJob_listing_title_name());

        /*holder.tvLanguage.setText(jobListing.get(position).
                getLanguageModels().get(0).getLanguage_name());*/
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

        holder.tvAddress.setText(address);

        holder.ivListingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete_item) {

                                binding.progress.setVisibility(View.VISIBLE);
                                deleteJobPost(accessToken,
                                        String.valueOf(jobListing.get(holder.getAdapterPosition()).getId()),
                                        holder.getAdapterPosition());
                                return true;

                        
}

                        return false;
                    }
                });

                popup.inflate(R.menu.popup_delete);
                popup.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return jobListing.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvListingType;
        private TextView tvJobName;
        private TextView tvLanguage;
        private TextView tvAddress;
        private ImageView ivProfilePic;
        AppCompatImageView ivListingMenu;
        private CardView passiveListingCv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvListingType = itemView.findViewById(R.id.tvListingType);
            tvJobName = itemView.findViewById(R.id.tvJobType);
            tvLanguage = itemView.findViewById(R.id.tvPersonLanguage);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            ivListingMenu = itemView.findViewById(R.id.ivListingMenu);
            passiveListingCv = itemView.findViewById(R.id.card_my_active_list);

            passiveListingCv.setOnClickListener(v->{
                Intent intent = new Intent(context, MyListingDetailsActivity.class);
                //SignUpModel userDetailModel = jobListing.get(getAdapterPosition());
                intent.putExtra("ListingDetails", jobListing.get(getAdapterPosition()));
               /* intent.putExtra("listingAddress",address);
                intent.putExtra("listingSkillName",jobListing.get(getAdapterPosition()).getJob_listing_title_name());
                intent.putExtra("listingImgUrl",jobListing.get(getAdapterPosition()).getImage().getBig());*/
                intent.putExtra("listingLanguages", languages.toString());
                intent.putExtra("fromScreen",2);
                //intent.putExtra("ListingDetailProfilePic",jobListing.get(position).)
                context.startActivity(intent);
            });
        }
    }

    private void deleteJobPost(String accessToken, String jobPostId, final int position) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.deleteJobPost(accessToken, jobPostId);
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
                        jobListing.remove(position);
                        notifyDataSetChanged();

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
                Toast.makeText(context, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });


    }
}
