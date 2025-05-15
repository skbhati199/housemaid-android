package com.housemaid.reciever;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActionReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        String sender_id = intent.getStringExtra("sender_id");
        String action=intent.getStringExtra("action");
        String accessToken=intent.getStringExtra("accessToken");


        if(action.equals("action1")){
           sendNotification(accessToken, sender_id,1);

        }
        else if(action.equals("action2")){
            sendNotification(accessToken, sender_id,2);

        }
        //This is used to close the notification tray

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }


    private void sendNotification(String access_token, String user_id, int key) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        call = apiService.sendAcceptRejectRequest(access_token, user_id, key);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call,
                                   Response<RegisterApi> response) {

                if (response.isSuccessful() && response.code()==200) {

                    String message = response.body().getMessage();
                    if (message != null) {

                    } else {


                    }
                } else {

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


            }
        });
    }

}
