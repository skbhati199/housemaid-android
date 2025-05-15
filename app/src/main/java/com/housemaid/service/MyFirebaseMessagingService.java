package com.housemaid.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.housemaid.activities.LiveConversationActivity;
import com.housemaid.activities.NotificationActivity;
import com.housemaid.activities.SingleChatActivity;
import com.housemaid.reciever.ActionReciever;
import com.housemaid.utils.SharedPreference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.housemaid.R;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String CHANNEL_ID = "channel_01";
    PendingIntent pendingIntent, pIntentAccept, pIntentReject;
    String title;
    String msg;
    SharedPreference sharedPreference;
    String userID;
    String name;
    String image;
    Bitmap userImage;
    String sender_id;
    String identifier;
    int notificationId = new Random().nextInt(60000);

    @Override
    public void onMessageReceived(RemoteMessage message) {
        sharedPreference = SharedPreference.getInstance(this);
        String accessToken = sharedPreference.getString("signUp_token", "");

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            try {
                userID = sharedPreference.getString("maid_id", "");
            }catch (Exception e){
                e.printStackTrace();
                System.out.print("Error My messaging Service:"+ e.toString());
            }
        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            try {
                userID = sharedPreference.getString("user_id", "");
            }catch (Exception e){
                e.printStackTrace();
                System.out.print("Error My messaging Service:"+ e.toString());
            }
        }
        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            try {
                userID = sharedPreference.getString("agency_Id", "");
                }catch (Exception e){
                e.printStackTrace();
                System.out.print("Error My messaging Service:"+ e.toString());
            }
        }

        Set<String> key = message.getData().keySet();
        Map<String, String> map = message.getData();
        sender_id = map.get("sender_id");
        msg = map.get("message");
        title = map.get("notification_type");
        name = map.get("name");
        image = map.get("Image");
        identifier = map.get("identifier");
        if (image != null) {
            try {
                URL url = new URL(image);
                userImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        if (title.equals("Request for Vedio call")) {

            Intent intentAction1 = new Intent(this, ActionReciever.class);
            intentAction1.putExtra("action", "action1");
            intentAction1.putExtra("notificationId", notificationId);
            intentAction1.putExtra("sender_id", sender_id);
            intentAction1.putExtra("accessToken", accessToken);
            Intent intentAction2 = new Intent(this, ActionReciever.class);
            intentAction2.putExtra("action", "action2");
            intentAction2.putExtra("notificationId", notificationId);
            intentAction2.putExtra("sender_id", sender_id);
            intentAction2.putExtra("accessToken", accessToken);


            pIntentAccept = PendingIntent.getBroadcast(this, 1, intentAction1, PendingIntent.FLAG_UPDATE_CURRENT);
            pIntentReject = PendingIntent.getBroadcast(this, 2, intentAction2, PendingIntent.FLAG_UPDATE_CURRENT);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupforLiveConversation(msg, notificationId);
            } else {

                NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Live Conversation Request")
                        .setContentText(msg)
                        //Using this action button I would like to call logTest
                        .addAction(R.drawable.check_selected, "Accept", pIntentAccept)
                        .addAction(R.drawable.check_selected, "Reject", pIntentReject)
                        .setOngoing(true);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(notificationId, notificationBuilder.build());
            }

        } else if (title.equals("VideoCall")) {

            Intent intent = new Intent(this, LiveConversationActivity.class);
            intent.putExtra("connection_key", 1);
            intent.putExtra("channel_name", msg);
            intent.putExtra("profileCaller", image);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference().child("VideoCall").child("users").child("U_" + userID).setValue(1);
            startActivity(intent);

        } else if (title.equals("Disconnect")) {
            Intent intent = new Intent();
            intent.putExtra("connection_key", 2);
            intent.putExtra("channel_name", msg);
            intent.setAction("disconnect");
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference().child("VideoCall").child("users").child("U_" + userID).setValue(0);
            sendBroadcast(intent);

        } else sendMyNotification(message);
    }

    private void sendMyNotification(RemoteMessage message) {


        Log.d("msg", msg);
        if (title.equals("Live Chat")) {
            String runningState = sharedPreference.getString("running", "no");
            if (runningState.equals("no")) {
                title = name;
                Intent intent = new Intent(this, SingleChatActivity.class);
                intent.putExtra("from", 1);
                intent.putExtra("touser_id", sender_id);
                intent.putExtra("user_name", name);
                intent.putExtra("user_image", image);
                intent.putExtra("identifier", identifier);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0,
                        intent, PendingIntent.FLAG_ONE_SHOT);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    setupChannels(msg, pendingIntent);
                } else {
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                            .setSmallIcon(R.drawable.logo)
                            .setLargeIcon(userImage)
                            .setContentTitle(name)
                            .setContentText(msg)
                            .setSound(soundUri)
                            .setPriority(NotificationManager.IMPORTANCE_HIGH)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(notificationId, notificationBuilder.build());
                }
            }

        } else {
            //On click of notification it redirect to this Activity
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_ONE_SHOT);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels(msg, pendingIntent);
            } else {

                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.logo)
                        .setLargeIcon(userImage)
                        .setContentTitle(name)
                        .setContentText(msg)
                        .setSound(soundUri)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        }
        //Setting up Notification channels for android O and above
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(String message, PendingIntent pendingIntent) {
        CharSequence adminChannelName = "Housemaid_Channel";
        String adminChannelDescription = "Housemaid Notification Channel";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(CHANNEL_ID, adminChannelName,
                NotificationManager.IMPORTANCE_DEFAULT);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        adminChannel.setSound(soundUri, audioAttributes);
        NotificationManager notificationManager1 = getSystemService(NotificationManager.class);

        if (notificationManager1 != null) {
            notificationManager1.createNotificationChannel(adminChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(userImage)
                .setContentText(message)
                .setContentTitle(name)
                .setSound(soundUri)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager1.notify(notificationId, notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupforLiveConversation(String message, int notificationId) {
        CharSequence adminChannelName = "Housemaid_Channel";
        String adminChannelDescription = "Housemaid Notification Channel";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(CHANNEL_ID, adminChannelName,
                NotificationManager.IMPORTANCE_DEFAULT);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        adminChannel.setSound(soundUri, audioAttributes);
        NotificationManager notificationManager1 = getSystemService(NotificationManager.class);

        if (notificationManager1 != null) {
            notificationManager1.createNotificationChannel(adminChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Live Conversation Request")
                .setContentText(message)
                .setSound(soundUri)
                //Using this action button I would like to call logTest
                .addAction(R.drawable.check_selected, "Accept", pIntentAccept)
                .addAction(R.drawable.check_selected, "Reject", pIntentReject)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        notificationManager1.notify(notificationId, notificationBuilder.build());

    }


}