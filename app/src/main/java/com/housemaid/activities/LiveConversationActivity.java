package com.housemaid.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.housemaid.R;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveConversationActivity extends BaseActivity {
    private static final String LOG_TAG = LiveConversationActivity.class.getSimpleName();

    SharedPreference sharedPreference;
    int toCallerId;
    String callerId;
    String callerName;
    int calling_key;
    String toCallerName;
    String accessToken;
    int connect_key = 0;
    MediaPlayer mp;
    Dialog dialog;
    Dialog dialog1;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference root, recentCallRoot;
    String senderId;
    String recieverID;
    String key;
    TextView tvCall;
    ArrayList<String> statusList;
    String channelName;
    LinearLayout layoutButtons;
    boolean flag;
    String profileToCaller;
    String profileCaller;

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("disconnect")) {
                onBackPressed();
            }
        }
    };

    IntentFilter intentFilter = new IntentFilter();


    private RtcEngine mRtcEngine;// Tutorial Step 1
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) { // Tutorial Step 5
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) { // Tutorial Step 10
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_conversation);
        intentFilter.addAction("disconnect");
        registerReceiver(broadcastReceiver, intentFilter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        root = firebaseDatabase.getReference();
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");

        toCallerId = getIntent().getIntExtra("caller_id", 0);
        toCallerName = getIntent().getStringExtra("to_caller_name");
        channelName = getIntent().getStringExtra("channel_name");
        String[] a = channelName.split("_");
        callerName = a[0];
        callerId = a[1];
        connect_key = getIntent().getIntExtra("connection_key", 0);
        calling_key = getIntent().getIntExtra("calling_key", 0);

        profileToCaller = getIntent().getStringExtra("profileToCaller");
        profileCaller = getIntent().getStringExtra("profileCaller") ;

        statusList = new ArrayList<>();

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            senderId = sharedPreference.getString("maid_id", "");
        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            senderId = sharedPreference.getString("user_id", "");
        }
        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            senderId = sharedPreference.getString("agency_Id", "");
        }

        recieverID = String.valueOf(toCallerId);
        recentCallRoot = root.child("VideoCall").child("users").child("U_" + senderId);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_calling_layout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.popup_start_calling_layout);
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        tvCall = findViewById(R.id.tvCalling);
        layoutButtons = findViewById(R.id.layoutButtons);

        if (connect_key == 2) {
            dialog.dismiss();
            finish();
        }
        if (calling_key == 1) {
            getCallStatus();

        } else {
            mp = MediaPlayer.create(this, R.raw.samsung_original);
            mp.start();
            showPopUp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    }


    private void checkStatus(int status) {
        if (status == 1) {
            Toast.makeText(LiveConversationActivity.this, R.string.call_busy_please_call_after_sometime,
                    Toast.LENGTH_LONG).show();
            finish();
        } else {

            sendNotification(accessToken, String.valueOf(toCallerId),
                    channelName, "1", "Connect");

            tvCall.setVisibility(View.GONE);
            startCallingPopUP();
            recentCallRoot.setValue(1);

        }
    }

    private void getCallStatus() {
        final DatabaseReference databaseReference = firebaseDatabase.getReference("VideoCall")
                .child("users").child("U_" + recieverID);
  /*      ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    key = snapshot.getKey();

                    String mReceiverID = (String) snapshot.child("receiverID").getValue();
                    String mSenderID = (String) snapshot.child("senderID").getValue();
                    String mStatus = (String) snapshot.child("status").getValue();

                    if (mSenderID.equals(senderId) || mReceiverID.equals(senderId)) {

                        if (mStatus.equals("1")){
                            statusList.add(mStatus);

                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };*/


        // databaseReference.addListenerForSingleValueEvent();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d("TAG", "data: " + dataSnapshot.toString());
                    checkStatus(Integer.parseInt(dataSnapshot.getValue().toString()));
                } else {
                    checkStatus(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startCallingPopUP() {
        dialog1.setCanceledOnTouchOutside(false);
        TextView tvCallingtext = dialog1.findViewById(R.id.tvCallingText);
        CircleImageView ivProfilePic = dialog1.findViewById(R.id.ivProfilePic);

        Glide.with(this)
                .load(profileToCaller)
                .error(R.drawable.user_c)
                .into(ivProfilePic);

        tvCallingtext.setText("Calling " + toCallerName + "...");

        dialog1.findViewById(R.id.btnEndCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification(accessToken, String.valueOf(toCallerId),
                        channelName, "2", "Disconnect");
            }
        });

        dialog1.show();
        Window window = dialog1.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void showPopUp() {

        dialog.setCanceledOnTouchOutside(false);
        TextView tvCallingtext = dialog.findViewById(R.id.tvCallingText);
        CircleImageView ivProfilePic = dialog.findViewById(R.id.ivProfilePic);

        Glide.with(this)
                .load(profileCaller)
                .error(R.drawable.user_c)
                .into(ivProfilePic);

        tvCallingtext.setText(callerName + " " + getString(R.string.is_calling_you));
        dialog.findViewById(R.id.btnStartCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mp.stop();

                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO,
                        PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA,
                        PERMISSION_REQ_ID_CAMERA)) {
                    initAgoraEngineAndJoinChannel();
                    layoutButtons.setVisibility(View.VISIBLE);


                }
            }
        });

        dialog.findViewById(R.id.btnEndCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentCallRoot.setValue(0);
                root.child("VideoCall").child("users").child("U_" + recieverID).setValue(0);
                sendNotification(accessToken, String.valueOf(callerId),
                        channelName, "2", "Disconnect");
                dialog.dismiss();
                mp.stop();
                finish();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        setupVideoProfile();         // Tutorial Step 2
        setupLocalVideo();           // Tutorial Step 3
        joinChannel();               // Tutorial Step 4
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA);
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
            case PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                    layoutButtons.setVisibility(View.VISIBLE);
                } else {
                    showLongToast("No permission for " + Manifest.permission.CAMERA);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (calling_key != 1) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        } else {
            recentCallRoot.setValue(0);
            root.child("VideoCall").child("users").child("U_" + recieverID).setValue(0);
        }
        super.onDestroy();


        RtcEngine.destroy();
        mRtcEngine = null;
    }

    // Tutorial Step 10
    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.setImageDrawable(getDrawable(R.drawable.video_off));
        } else {
            iv.setSelected(true);
            iv.setImageDrawable(getDrawable(R.drawable.video_on));
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
    }

    // Tutorial Step 9
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.setImageDrawable(getDrawable(R.drawable.mute));
        } else {
            iv.setSelected(true);
            iv.setImageDrawable(getDrawable(R.drawable.unmute));
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Tutorial Step 8
    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    // Tutorial Step 6
    public void onEncCallClicked(View view) {
        if (calling_key == 1) {
            recentCallRoot.setValue(0);
            root.child("VideoCall").child("users").child("U_" + recieverID).setValue(0);
            finish();

        } else {
            recentCallRoot.setValue(0);
            root.child("VideoCall").child("users").child("U_" + recieverID).setValue(0);
            finish();
        }
        leaveChannel();
    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {

        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id),
                    mRtcEventHandler);

            //New
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" +
                    Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_STREAM_HIGH, false);

        VideoEncoderConfiguration videoEncoderConfiguration =
                new VideoEncoderConfiguration(
                        VideoEncoderConfiguration.VD_1280x720,
                        VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                        VideoEncoderConfiguration.STANDARD_BITRATE,
                        VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT);

        mRtcEngine.setVideoEncoderConfiguration(videoEncoderConfiguration);
    }

    // Tutorial Step 3
    private void setupLocalVideo() {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT,
                0));
    }

    // Tutorial Step 4
    private void joinChannel() {
        if (connect_key == 0) {
            root.child("VideoCall").child("users").child("U_" + recieverID).setValue(1);
        }
        mRtcEngine.joinChannel(null, channelName,
                "Extra Optional Data", 0);
        // if you do not specify the uid, we will generate the uid for you
    }

    // Tutorial Step 5
    private void setupRemoteVideo(int uid) {
        if (connect_key == 0) {
            root.child("VideoCall").child("users").child("U_" + recieverID).setValue(1);
            dialog1.dismiss();
        }
        FrameLayout container1 = (FrameLayout) findViewById(R.id.local_video_view_container);
        container1.setVisibility(View.VISIBLE);
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {

            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));

        surfaceView.setTag(uid); // for mark purpose
        TextView tipMsg = findViewById(R.id.tvCalling); // optional UI
        tipMsg.setVisibility(View.GONE);
    }

    // Tutorial Step 6
    private void leaveChannel() {
        root.child("VideoCall").child("users").child("U_" + senderId)
                .setValue(0);
        root.child("VideoCall").child("users").child("U_" + recieverID).setValue(0);
        if (calling_key == 1) {
           mRtcEngine.leaveChannel();
        }
        else  finish();
    }

    // Tutorial Step 7
    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        container.removeAllViews();
        root.child("VideoCall").child("users").child("U_" + senderId)
                .setValue(0);
        root.child("VideoCall").child("users").child("U_" + recieverID).setValue(0);
        finish();
        TextView tipMsg = findViewById(R.id.tvCalling); // optional UI
        tipMsg.setText(R.string.disconnectiong);
        tipMsg.setVisibility(View.VISIBLE);
    }

    // Tutorial Step 10
    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void sendNotification(String access_token, String user_id, String call_text,
                                  String key, final String Status) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        call = apiService.videoCall(access_token, user_id, call_text, key);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call,
                                   Response<RegisterApi> response) {

                if (response.isSuccessful()) {

                    String message = response.body().getMessage();
                    if (message != null) {

                        if (Status.equals("Connect")) {
                            Toast.makeText(LiveConversationActivity.this, "Calling " +
                                            toCallerName
                                    , Toast.LENGTH_SHORT).show();
                            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO,
                                    PERMISSION_REQ_ID_RECORD_AUDIO)
                                    && checkSelfPermission(Manifest.permission.CAMERA,
                                    PERMISSION_REQ_ID_CAMERA)) {
                                initAgoraEngineAndJoinChannel();
                                layoutButtons.setVisibility(View.VISIBLE);
                            }

                        } else {
                            recentCallRoot.setValue(0);
                            root.child("VideoCall").child("users").child("U_" + recieverID).setValue(0);
                            Toast.makeText(LiveConversationActivity.this, "Disconnected "
                                            + toCallerName
                                    , Toast.LENGTH_SHORT).show();
                            leaveChannel();
                            recentCallRoot.setValue(0);
                            // close this activity
                            finish();
                        }
                    } else {

                        Toast.makeText(LiveConversationActivity.this, "Fails"
                                , Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(LiveConversationActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(LiveConversationActivity.this, response.errorBody().string()
                                    , Toast.LENGTH_SHORT).show();
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

                Toast.makeText(LiveConversationActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(LOG_TAG, "new intent");
    }

}