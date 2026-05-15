package com.housemaid.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.SignInActivity;
import com.housemaid.activities.SignUpActivity;
import com.housemaid.activities.SingleChatActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.activities.agency.fromHome.HomeAgencyActivity;
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
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fluper on 22/5/18.
 */

public class HomeListingAdapter extends RecyclerView.Adapter<HomeListingAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserDetailModel> maidDetailModel;
    callMethod callMethod;
    SharedPreference sharedPreference;
    int select_page;
    String accessToken;
    int user_id;
    String job_id;
    String accessFrom;
    private String credits;
    private int totalCredits;
    private int maid_id;
    private String reasonToSuggest;
    private Dialog dialog;
    int disableKey;
    private String[] months;
    private String monthName;
    String createTime;

    public HomeListingAdapter(Context context, ArrayList<UserDetailModel> maidDetailModel,
                              callMethod callMethod, String accessFrom) {
        this.context = context;
        this.maidDetailModel = maidDetailModel;
        this.callMethod = callMethod;
        this.accessFrom = accessFrom;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_item_home_list_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        sharedPreference = SharedPreference.getInstance(context);
        totalCredits = sharedPreference.getInteger("TotalCredits", 0);
        select_page = sharedPreference.getInteger("page_selection", 10);
        accessToken = sharedPreference.getString("signUp_token", "");
        job_id = sharedPreference.getString("job_ID", "");
        disableKey = sharedPreference.getInteger("disable_key", 1);


        if (disableKey == 1) {
            holder.layoutFavourite.setEnabled(false);
            holder.tvMessage.setEnabled(false);
        }


        holder.tvName.setText(maidDetailModel.get(position).getName());
        //GET ALL LANGUAGES HERE in "lang" using for loop !.
        StringBuilder lang = new StringBuilder();
        if (maidDetailModel.get(position).getUserLanguageModel().size() > 0) {
            for(int i = 0; i < maidDetailModel.get(position).getUserLanguageModel().size() ; i++) {
                lang = lang.append(maidDetailModel.get(position).getUserLanguageModel().get(i).getLanguage_detail().getName());
                if(i < maidDetailModel.get(position).getUserLanguageModel().size() -1){
                    //TO ADD ',_' after a language but not at last one.
                    lang.append(", ");
                }
            }
            /*holder.tvLanguage.setText(maidDetailModel.get(position).getUserLanguageModel().get(0)
                    .getLanguage_detail().getName());*/
            holder.tvLanguage.setText(lang);
        } else holder.tvLanguage.setText(R.string.no_language);


        String createdAt = maidDetailModel.get(position).getCreated_at();
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

        String maritalStatus = maidDetailModel.get(position).getMarital_status();
        String workStatus = maidDetailModel.get(position).getWork_status();
        int actualAge = getAge(maidDetailModel.get(position).getDob());

/*        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date1 = null;
        try {
            date1 = (Date)formatter.parse(age);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Today is " +date1.getTime());*/

        //holder.tvCreatedAt.setText(day+" "+monthName+" "+year+", "+createTime);
        holder.tvCreatedAt.setText(actualAge+" - "+maritalStatus+" - "+workStatus);

        if (maidDetailModel.get(position).getMaidImageModel().size() > 0
                && !maidDetailModel.get(position).getMaidImageModel().get(0).getImageModel().getSmall().isEmpty()) {
            Glide.with(itemView.getContext()).load(maidDetailModel.get(position).getMaidImageModel().get(0).getImageModel().getSmall())
                    .error(R.drawable.avatar).into(holder.ivProfilePic);
        } else holder.ivProfilePic.setImageResource(R.drawable.user);

        holder.tvAddress.setText(String.format("%s, %s", maidDetailModel.get(position)
                .getState_name(), maidDetailModel.get(position)
                .getCountry_name()));

        holder.layoutSingleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (select_page == 0) {
                    Intent intent = new Intent(context, MaidInfoActivity.class);
                    sharedPreference.putString("list_type", "normal");
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("maidDetail", maidDetailModel.get(holder.getAdapterPosition()));
                    intent.putExtra("accessFrom", accessFrom);
                    context.startActivity(intent);
                } else {
                    openDialog(holder.getAdapterPosition());
                }
            }
        });

        holder.tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.housemaid");
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, "Send To"));
            }
        });

        holder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (accessFrom.equals("withoutSignUp")) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.popup_login_layout);
                    dialog.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            context.startActivity(new Intent(context, SignInActivity.class));
                        }
                    });
                    dialog.findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(context, SignUpActivity.class);
                            context.startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    Window window = dialog.getWindow();
                    if (window != null) {
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.WRAP_CONTENT);
                    }

                } else {
                    if (maidDetailModel.get(holder.getAdapterPosition()).getPaidStatusModel().getMessage_status()
                            .equals("1")) {
                        final UserDetailModel maidDetail = maidDetailModel.get(holder.getAdapterPosition());
                        Intent intent = new Intent(context, SingleChatActivity.class);
                        intent.putExtra("touser_id", String.valueOf(maidDetail.getId()));
                        intent.putExtra("user_name", maidDetail.getName());
                        intent.putExtra("user_image", maidDetail
                                .getMaidImageModel().get(0)
                                .getImageModel()
                                .getBig());
                        intent.putExtra("identifier", maidDetail
                                .getUser_type());
                        context.startActivity(intent);
                    } else getCreditListing(accessToken, "2", "6", holder.getAdapterPosition());
                }
            }
        });


      /*  if (maidDetailModel.get(position).getIs_hired()) {

        }*/

        if (maidDetailModel.get(position).getIs_favourite() == 0) {
            holder.checkfavourite.setChecked(false);
        } else {
            holder.checkfavourite.setChecked(true);
        }

        //when we click on favourite we set the favourite to true.
        // and set the key opposite to boolean value.
        holder.layoutFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isfav = maidDetailModel.get(holder.getAdapterPosition()).getIs_favourite();

                if (isfav == 0) maidDetailModel.get(holder.getAdapterPosition()).setIs_favourite(1);
                else maidDetailModel.get(holder.getAdapterPosition()).setIs_favourite(0);

                int key = isfav == 0 ? 1 : 0;
                int jobListId = maidDetailModel.get(holder.getAdapterPosition()).getId();

                sharedPreference.putInteger("favouritekey", key);

                //we call interface and call the makeFavourite API.
                callMethod.makeFavourite(key, jobListId);
                notifyDataSetChanged();
            }
        });

    }

    private void openDialog(final int position) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_suggest_maid);

        final EditText etReason = dialog.findViewById(R.id.etReason);
        final ProgressBar progressBar = dialog.findViewById(R.id.progress);


        dialog.findViewById(R.id.btnSuggest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonToSuggest = etReason.getText().toString();
                if (reasonToSuggest != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    maid_id = maidDetailModel.get(position).getMaid_id();
                    getCreditListing(accessToken, "11", "0", position);

                } else Toast.makeText(context, R.string.please_write_why_you_want_to_suggest,
                        Toast.LENGTH_SHORT).show();

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
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

                    CreditListingApi registerApi = response.body();
                    CreditListingApi.CreditListingModel creditListingModel =
                            registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {
                        credits = creditListingModel.getCredit();
                        callDialogForMoreThenImages(from, position);
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
                            Toast.makeText(context, new Gson().fromJson(response.errorBody().string(), ErrorResponse.class).getMessage(),
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
                user_id = maidDetailModel.get(position).getId();
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
                        if (key.equals("6")) {
                            final UserDetailModel maidDetail = maidDetailModel.get(position);
                            Intent intent = new Intent(context, SingleChatActivity.class);
                            intent.putExtra("touser_id", String.valueOf(maidDetail.getId()));
                            intent.putExtra("user_name", maidDetail.getName());
                            intent.putExtra("user_image", maidDetail
                                    .getMaidImageModel().get(0)
                                    .getImageModel()
                                    .getBig());
                            intent.putExtra("identifier", maidDetail
                                    .getUser_type());
                            context.startActivity(intent);
                        }
                        if (key.equals("0")) {
                            popupWindowMenuClick(accessToken, user_id,
                                    String.valueOf(maid_id), reasonToSuggest, job_id);
                        }

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

    private void popupWindowMenuClick(String access_token, String user_id, String maid_id,
                                      String reasonToSuggest, String job_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        call = apiService.suggestMaid(access_token, user_id, maid_id, reasonToSuggest, job_id);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {


                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        dialog.dismiss();
                        context.startActivity(new Intent(context, HomeAgencyActivity.class));
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();


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
                                    (response.errorBody().string(), ErrorResponse.class)
                                    .getMessage(), Toast.LENGTH_SHORT).show();
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
                dialog.dismiss();
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

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

    private int getAge(String dobString){

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month+1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }

    @Override
    public int getItemCount() {
        return maidDetailModel.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layoutSingleItem;
        private ImageView ivProfilePic;
        private TextView tvMessage;
        private TextView tvName;
        private TextView tvLanguage;
        private TextView tvCreatedAt;
        private TextView tvAddress;
        private LinearLayout layoutFavourite;
        private CheckBox checkfavourite;
        private CardView cardView;
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
            cardView = itemView.findViewById(R.id.cardView);
        }

    }

    public void setfilter(ArrayList<UserDetailModel> newList) {
        maidDetailModel = new ArrayList<>();
        maidDetailModel.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
