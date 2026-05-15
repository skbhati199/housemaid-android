package com.housemaid.activities;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.agency.fromHome.AgencyProfileActivity;
import com.housemaid.activities.agency.fromHome.HomeAgencyActivity;
import com.housemaid.activities.maid.fromHome.activity.HomeForMaidActivity;
import com.housemaid.activities.maid.fromHome.activity.MaidHomeProfileActivity;
import com.housemaid.activities.user.fromHome.HomeUserActivity;
import com.housemaid.activities.user.fromHome.UserProfileActivity;
import com.housemaid.adapter.SingleChatAdapter;
import com.housemaid.databinding.ActivitySingleChatBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.bean.ChattingModel;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// AQuery removed

public class SingleChatActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySingleChatBinding binding;
    private SingleChatAdapter chatAdapter;
    private ArrayList<ChattingModel> messageList = new ArrayList<>();
    private String senderId, recieverID, user_name, userMessage, senderName;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference root, chatRoot, recentChatRoot, blockUserRoot,
            unreadConversationSender, unreadConversationReceiver;
    private String temp_key;
    private String chat_msg, chat_user_name;
    private SharedPreference sharedPreference;
    private String timeStamp;
    private String key;
    private String receiverProfileImage;
    private String senderProfileImage;
    private String userIdentifierReceiver;
    private String userIdentifierSender;
    private String accessToken;
    private String node;
    private int delState = 0;
    private PopupMenu popup;
    private int blockByStatus = 0;
    private int blockToStatus = 0;
    private int from;
    private int entry_key;
    private ValueEventListener eventListener;
    private DatabaseReference chatMsgRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_chat);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        chatAdapter = new SingleChatAdapter(SingleChatActivity.this, messageList);
        binding.rvMessageListing.setLayoutManager(
                new LinearLayoutManager(SingleChatActivity.this));
        binding.rvMessageListing.setAdapter(chatAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        root = firebaseDatabase.getReference();
        sharedPreference = SharedPreference.getInstance(this);
        sharedPreference.putString("running", "yes");
        user_name = getIntent().getStringExtra("user_name");
        recieverID = getIntent().getStringExtra("touser_id");
        receiverProfileImage = getIntent().getStringExtra("user_image");

        //here null is coming when agency panel or maid panel is open check this one
        // Priority -------
        userIdentifierReceiver = getIntent().getStringExtra("identifier");//null is coming

        entry_key = sharedPreference.getInteger("entry_key", 0);

        if (getIntent() != null && getIntent().hasExtra("from"))
            from = getIntent().getIntExtra("from", 0);

        accessToken = sharedPreference.getString("signUp_token", "0");

        if (entry_key == 1) {
            senderId = sharedPreference.getString("maid_id", "");
            senderName = sharedPreference.getString("maid_name", "");
            senderProfileImage = sharedPreference.getString("maid_pic", "");
            userIdentifierSender = "maid";
        }
        if (entry_key == 2) {
            senderId = sharedPreference.getString("user_id", "");
            senderName = sharedPreference.getString("user_name", "");
            senderProfileImage = sharedPreference.getString("user_pic", "");
            userIdentifierSender = "user";

        }
        if (entry_key == 3) {
            senderId = sharedPreference.getString("agency_Id", "");
            senderName = sharedPreference.getString("agency_name", "");
            senderProfileImage = sharedPreference.getString("agency_pic", "");
            userIdentifierSender = "provider";
        }
        /*chatRoot = root.child("User").child("Message").child(senderId+"_"+recieverID);*/
        chatRoot = root.child("Users").child("Message").child(getConversationKey(
                Integer.parseInt(senderId), Integer.parseInt(recieverID)));

        blockUserRoot = root.child("Users").child("BlockUser").child(getConversationKey(
                Integer.parseInt(senderId), Integer.parseInt(recieverID)));

        recentChatRoot = root.child("Users").child("RecentChat").child(getConversationKey(
                Integer.parseInt(senderId), Integer.parseInt(recieverID)));

        unreadConversationSender = root.child("Users").child("Unread_Conversation").child("User_" + senderId)
                .child(getConversationKey(Integer.parseInt(senderId), Integer.parseInt(recieverID)));

        unreadConversationReceiver = root.child("Users").child("Unread_Conversation").child("User_" + recieverID)
                .child(getConversationKey(Integer.parseInt(senderId), Integer.parseInt(recieverID)));


        binding.toolbar.ivMore.setVisibility(View.VISIBLE);
        binding.toolbar.tvTitle.setVisibility(View.VISIBLE);
        binding.toolbar.tvTitle.setText(user_name);

        long tsLong = System.currentTimeMillis();
        timeStamp = Long.toString(tsLong);

        /*createBlockNode();*/
        binding.progress.setVisibility(View.VISIBLE);
        checkBlockedState();
    }

    private void createBlockNode() {
        final Map<String, Object> map = new HashMap<>();
        map.put("blockID", "0");
        map.put("isBlock" + senderId, "");
        map.put("isBlock" + recieverID, "");
        blockUserRoot.updateChildren(map);
    }

    private void checkBlockedState() {
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users")
                .child("BlockUser").child(getConversationKey(
                        Integer.parseInt(senderId), Integer.parseInt(recieverID)));
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String blockBy = (String) dataSnapshot.child("blockID").getValue();
                    String senderStatus = (String) dataSnapshot.child("isBlock" + senderId).getValue();
                    String receiverStatus = (String) dataSnapshot.child("isBlock" + recieverID).getValue();

                    if ((senderStatus.equals("1")) && (receiverStatus.equals("1"))) {
                        blockToStatus = 1;
                        blockByStatus = 1;
                        binding.progress.setVisibility(View.GONE);
                        binding.messageArea.messageArea.setText(user_name + getString(R.string.is_blocked));
                        binding.messageArea.messageArea.setTextColor(getResources()
                                .getColor(R.color.colorPrimary));
                        binding.messageArea.messageArea.setEnabled(false);
                        binding.messageArea.sendButton.setClickable(false);
                    } else {

                        if (senderStatus.equals("1")) {
                            blockByStatus = 1;
                            binding.progress.setVisibility(View.GONE);
                            binding.messageArea.messageArea.setText(getString(R.string.you_are_blocked_by) + user_name);
                            binding.messageArea.messageArea.setTextColor(getResources()
                                    .getColor(R.color.colorPrimary));
                            binding.messageArea.messageArea.setEnabled(false);
                            binding.messageArea.sendButton.setClickable(false);
                        }
                        if (receiverStatus.equals("1")) {
                            blockToStatus = 1;
                            binding.progress.setVisibility(View.GONE);
                            binding.messageArea.messageArea.setText(user_name + getString(R.string.is_blocked));
                            binding.messageArea.messageArea.setTextColor(getResources()
                                    .getColor(R.color.colorPrimary));
                            binding.messageArea.messageArea.setEnabled(false);
                            binding.messageArea.sendButton.setClickable(false);
                        }
                    }
                } else {
                    blockByStatus = 0;
                    blockToStatus = 0;
                    binding.messageArea.messageArea.setText("");
                    binding.messageArea.messageArea.setEnabled(true);
                    binding.messageArea.sendButton.setClickable(true);
                    binding.progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    public void initControls() {
        super.initControls();

        binding.messageArea.sendButton.setOnClickListener(this);
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.toolbar.ivMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivMore) {

                openPopUp(v);
                
            
} else if (v.getId() == R.id.ivBack) {

                sharedPreference.putString("running", "no");
                if (from == 1) {
                    if (entry_key == 1) startActivity(new Intent(this, HomeForMaidActivity.class));
                    else if (entry_key == 2)
                        startActivity(new Intent(this, HomeUserActivity.class));
                    else startActivity(new Intent(this, HomeAgencyActivity.class));
                } else
                    onBackPressed();
                
            
} else if (v.getId() == R.id.sendButton) {

                if (TextUtils.isEmpty(binding.messageArea.messageArea.getText().toString().trim()))
                    Toast.makeText(this, R.string.please_enter_a_message, Toast.LENGTH_SHORT).show();
                else {
                    userMessage = binding.messageArea.messageArea.getText().toString();
                    onSendButtonClick();
                }
                
        
}
    }

    @Override
    public void onBackPressed() {
        sharedPreference.putString("running", "no");
        ValidationUtils.hideSoftKeyboard(this);
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        sharedPreference.putString("running", "no");
        chatMsgRef.removeEventListener(eventListener);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        sharedPreference.putString("running", "no");
        chatMsgRef.removeEventListener(eventListener);
        super.onPause();
    }

    private void openPopUp(View view) {
        popup = new PopupMenu(this, view, Gravity.START);
        popup.inflate(R.menu.popup_chat_menu);

        if (blockToStatus == 0) {
            popup.getMenu().findItem(R.id.block_item).setVisible(true);
            popup.getMenu().findItem(R.id.unblock_item).setVisible(false);

        } else {
            popup.getMenu().findItem(R.id.unblock_item).setVisible(true);
            popup.getMenu().findItem(R.id.block_item).setVisible(false);
        }


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.profile_item) {

                        binding.progress.setVisibility(View.VISIBLE);
                        getUserDetails(accessToken, Integer.parseInt(recieverID));
                        
                    
} else if (item.getItemId() == R.id.block_item) {

                        onBlockUser();
                        
                    
} else if (item.getItemId() == R.id.unblock_item) {

                        onUnBlockUser();
                        
                    
} else if (item.getItemId() == R.id.delete_item) {

                        delState = 1;
                        binding.progress.setVisibility(View.VISIBLE);
                        onDeleteChat();
                        

                
}
                return false;
            }
        });

        popup.show();

    }

    private void onBlockUser() {
        blockToStatus = 1;
        popup.getMenu().findItem(R.id.block_item).setVisible(false);
        popup.getMenu().findItem(R.id.unblock_item).setVisible(true);
        final Map<String, Object> map = new HashMap<>();
        map.put("blockID", "0");

        if (blockByStatus == 1) map.put("isBlock" + senderId, "1");
        else map.put("isBlock" + senderId, "");

        map.put("isBlock" + recieverID, "1");
        blockUserRoot.updateChildren(map);
        binding.messageArea.messageArea.setText(user_name + getString(R.string.is_blocked));
        binding.messageArea.messageArea.setEnabled(false);
        binding.messageArea.messageArea.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.messageArea.sendButton.setClickable(false);
    }

    private void onUnBlockUser() {
        blockToStatus = 0;
        popup.getMenu().findItem(R.id.block_item).setVisible(true);
        popup.getMenu().findItem(R.id.unblock_item).setVisible(false);
        final Map<String, Object> map = new HashMap<>();
        map.put("blockID", "0");

        if (blockByStatus == 1) map.put("isBlock" + senderId, "1");
        else map.put("isBlock" + senderId, "");

        map.put("isBlock" + recieverID, "");
        blockUserRoot.updateChildren(map);

        if (blockByStatus == 1) {
            binding.messageArea.messageArea.setText(getString(R.string.you_are_blocked_by) + user_name);
            binding.messageArea.messageArea.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.messageArea.messageArea.setEnabled(false);
            binding.messageArea.sendButton.setClickable(false);
        } else {
            binding.messageArea.messageArea.setText("");
            binding.messageArea.messageArea.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.messageArea.messageArea.setEnabled(true);
            binding.messageArea.sendButton.setClickable(true);
        }
    }

    private void onDeleteChat() {
        final Map<String, Object> map = new HashMap<>();
        map.put("isDelete" + senderId, "1");
        recentChatRoot.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        chatRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    node = snapshot.getKey();
                    chatRoot.child(node).updateChildren(map);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        messageList.clear();

    }

    private String getConversationKey(int id1, int id2) {
        if (id1 < id2) {
            key = id1 + "_" + id2;
        } else key = id2 + "_" + id1;

        return key;
    }

    private void onSendButtonClick() {

        Map<String, Object> map = new HashMap<>();
        temp_key = chatRoot.push().getKey();
        chatRoot.updateChildren(map);

        DatabaseReference message_root = chatRoot.child(temp_key);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("isDelete" + senderId, "0");
        map2.put("isDelete" + recieverID, "0");
        map2.put("receiverName", user_name);
        map2.put("message", binding.messageArea.messageArea.getText().toString().trim());
        map2.put("senderID", senderId);
        map2.put("senderName", senderName);
        map2.put("receiverID", recieverID);
        map2.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map2.put("receiverProfileImage", receiverProfileImage);
        map2.put("senderProfileImage", senderProfileImage);
        map2.put("userIdentifierReceiver", userIdentifierReceiver);
        map2.put("userIdentifierSender", userIdentifierSender);

        message_root.updateChildren(map2);
        recentChatRoot.updateChildren(map2);
        unreadConversationReceiver.setValue("0");

        sendNotification(accessToken, recieverID,
                binding.messageArea.messageArea.getText().toString());

        binding.messageArea.messageArea.setText("");

    }

    public void getLayoutChanged() {

        binding.rvMessageListing.addOnLayoutChangeListener(new View.OnLayoutChangeListener()

        {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                if (bottom < oldBottom) {
                    binding.rvMessageListing.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (messageList.size() > 0) {
                                binding.rvMessageListing.smoothScrollToPosition(
                                        binding.rvMessageListing.getAdapter().getItemCount() - 1);
                            }
                        }
                    }, 100);
                }
            }
        });
    }

    @Override
    protected void onResume() {

        if (delState == 1) {
            delState = 0;
        } else {
            getChatMessages();
            getLayoutChanged();
            binding.progress.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    private void getChatMessages() {
        chatMsgRef = firebaseDatabase.getReference("Users").child("Message")
                .child(getConversationKey(Integer.parseInt(senderId), Integer.parseInt(recieverID)));
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sharedPreference.putString("running", "yes");
                binding.progress.setVisibility(View.GONE);
                unreadConversationSender.setValue("1");

                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("dataShot", "" + snapshot.child("message").getValue());

                    String mDeleteStatus = (String) snapshot.child("isDelete" + senderId).getValue();
                    String mMessage = (String) snapshot.child("message").getValue();
                    String mRecieverName = (String) snapshot.child("receiverName").getValue();
                    String mSenderName = (String) snapshot.child("senderName").getValue();
                    String mTimeStamp = (String) snapshot.child("timestamp").getValue();
                    String mSenderID = (String) snapshot.child("senderID").getValue();
                    String mReceiverID = (String) snapshot.child("receiverID").getValue();
                    String mRecProfilePic = (String) snapshot.child("receiverProfileImage").getValue();
                    String mSenderProfilePic = (String) snapshot.child("senderProfileImage").getValue();
                    String mUserIdentifierReceiver = (String) snapshot.child("userIdentifierReceiver")
                            .getValue();
                    String mUserIdentifierSender = (String) snapshot.child("userIdentifierSender")
                            .getValue();

                    if (mDeleteStatus.equals("0")) {
                        messageList.add(new ChattingModel(mMessage, mRecieverName, mSenderName, mTimeStamp,
                                mSenderID, mReceiverID, mDeleteStatus, mRecProfilePic, mSenderProfilePic,
                                mUserIdentifierReceiver, mUserIdentifierSender));
                    }

                }
                if (messageList.size() != 0) {


                    chatAdapter.notifyDataSetChanged();

                    binding.rvMessageListing.smoothScrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                binding.progress.setVisibility(View.GONE);
            }
        };
        chatMsgRef.addValueEventListener(eventListener);
    }

    private void sendNotification(String accessToken, String recieverID, String message) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        call = apiService.liveChatNotification(accessToken, recieverID, message);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call,
                                   Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    SignUpModel registerApi = response.body().getSignUpModel();
                    String message = registerApi.getMessage();
                    if (message != null) {

                    } else {

                        Toast.makeText(SingleChatActivity.this, response.errorBody().toString()
                                , Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {

                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(SingleChatActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(SingleChatActivity.this, "" + response.errorBody()
                                    .string(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SingleChatActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getUserDetails(String access_token, int sender_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getUserDetails(access_token, sender_id);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    SignUpModel signUpModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {
                        binding.progress.setVisibility(View.GONE);
                        popup.dismiss();
                        if (signUpModel.getUser_type().equals("user")) {
                            Intent intent = new Intent(SingleChatActivity.this, UserProfileActivity.class);
                            intent.putExtra("userProfile", signUpModel);
                            intent.putExtra("fromPage", "chatScreen");
                            startActivity(intent);
                        } else if (signUpModel.getUser_type().equals("maid")) {
                            Intent intent = new Intent(SingleChatActivity.this, MaidHomeProfileActivity.class);
                            intent.putExtra("maidDetail", signUpModel);
                            intent.putExtra("fromPage", "chatScreen");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SingleChatActivity.this, AgencyProfileActivity.class);
                            intent.putExtra("agencyProfile", signUpModel);
                            intent.putExtra("fromPage", "chatScreen");
                            startActivity(intent);
                        }

                    } else {
                        binding.progress.setVisibility(View.GONE);
                        Toast.makeText(SingleChatActivity.this, response.errorBody().toString()
                                , Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (SingleChatActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(SingleChatActivity.this, SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(SingleChatActivity.this,
                                    "" + response.errorBody().string(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(SingleChatActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}