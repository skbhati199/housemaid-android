package com.housemaid.activities;


import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.housemaid.R;
import com.housemaid.adapter.ChatAdapter;
import com.housemaid.databinding.ActivityChatBinding;
import com.housemaid.model.bean.ChattingModel;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private ActivityChatBinding binding;
    private ArrayList<ChattingModel> recentChatList;
    private ChatAdapter chatAdapter;
    private String senderId, senderProfileImage;
    private ArrayList<String> keys = new ArrayList<>();
    private String senderName;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference root, chatRoot, recentChatRoot;
    private boolean a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.my_messages);
        binding.toolbar.ivSearch.setVisibility(View.VISIBLE);
        recentChatList = new ArrayList<>();
        ChattingModel chattingModel = new ChattingModel("", "",
                "", "", "", "",
                "", "", "",
                "", "");
        SharedPreference sharedPreference = SharedPreference.getInstance(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        root = firebaseDatabase.getReference().getRoot();
        int entry_key = sharedPreference.getInteger("entry_key", 0);

        binding.rvRecentChatList.setLayoutManager(
                new LinearLayoutManager(ChatActivity.this));

        if (entry_key == 1) {

            senderId = sharedPreference.getString("maid_id", "");
            senderName = sharedPreference.getString("maid_name", "");
            senderProfileImage = sharedPreference.getString("maid_pic", "");

        } else if (entry_key == 2) {

            senderId = sharedPreference.getString("user_id", "");
            senderName = sharedPreference.getString("user_name", "");
            senderProfileImage = sharedPreference.getString("user_pic", "");

        } else {

            senderId = sharedPreference.getString("agency_Id", "");
            senderName = sharedPreference.getString("agency_name", "");
            senderProfileImage = sharedPreference.getString("agency_pic", "");
        }

        binding.toolbar.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //   filter(editable.toString());
            }
        });

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.toolbar.ivSearch.setOnClickListener(this);
        binding.toolbar.ivCancel.setOnClickListener(this);
        binding.toolbar.ivSearchBack.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.ivSearch:
                binding.toolbar.layoutSearch.setVisibility(View.VISIBLE);
                binding.toolbar.tvTitle.setVisibility(View.GONE);
                binding.toolbar.ivSearch.setVisibility(View.GONE);
                break;

            case R.id.ivCancel:
                binding.toolbar.etSearch.setText("");
                break;

            case R.id.ivSearchBack:
                binding.toolbar.layoutSearch.setVisibility(View.GONE);
                binding.toolbar.tvTitle.setVisibility(View.VISIBLE);
                binding.toolbar.ivSearch.setVisibility(View.VISIBLE);
                ValidationUtils.hideSoftKeyboard(this);
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatMessages();
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void filter(String newText) {


        newText = newText.toLowerCase();
        ArrayList<ChattingModel> newList = new ArrayList<>();
        for (ChattingModel nameList : recentChatList) {

            String name;
            if (senderId.equals(nameList.getSenderID())) {
                name = nameList.getReceiverName().toLowerCase();
            } else name = nameList.getSenderName().toLowerCase();

            if (name.contains(newText))
                newList.add(nameList);

        }
        if (newList.isEmpty()) {
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.tvNoData.setText(R.string.no_maid_found);
        } else binding.tvNoData.setVisibility(View.GONE);
        ChatAdapter chatAdapter1 = new ChatAdapter(ChatActivity.this, newList,
                senderId, senderName, senderProfileImage);
        binding.rvRecentChatList.setAdapter(chatAdapter1);


        //homeListingAdapter.setfilter(newList);
    }

    private void getChatMessages() {

        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users")
                .child("RecentChat");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                binding.progress.setVisibility(View.GONE);

                recentChatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String mDeleteStatus = (String) snapshot.child("isDelete" + senderId).getValue();
                    String mMessage = (String) snapshot.child("message").getValue();
                    String mRecieverName = (String) snapshot.child("receiverName").getValue();
                    String mSenderName = (String) snapshot.child("senderName").getValue();
                    String mReceiverID = (String) snapshot.child("receiverID").getValue();
                    String mProfilePic = (String) snapshot.child("receiverProfileImage").getValue();
                    String mSenderProfilePic = (String) snapshot.child("senderProfileImage").getValue();
                    String mSenderID = (String) snapshot.child("senderID").getValue();
                    String mTimeStamp = (String) snapshot.child("timestamp").getValue();
                    String mUserIdentifierReceiver = (String) snapshot.child("userIdentifierReceiver")
                            .getValue();
                    String mUserIdentifierSender = (String) snapshot.child("userIdentifierSender")
                            .getValue();

                    if (mSenderID.equals(senderId) || mReceiverID.equals(senderId)) {
                        if (mDeleteStatus.equals("0")) {
                            recentChatList.add(new ChattingModel(mMessage, mRecieverName, mSenderName,
                                    mTimeStamp, mSenderID, mReceiverID, mDeleteStatus, mProfilePic, mSenderProfilePic,
                                    mUserIdentifierReceiver, mUserIdentifierSender));
                        }
                    }

                    Collections.sort(recentChatList, new Comparator<ChattingModel>() {
                        @Override
                        public int compare(ChattingModel o1, ChattingModel o2) {
                            return o2.getTimeStamp().compareTo(o1.getTimeStamp());
                        }
                    });
                }
                if (recentChatList.size() > 0) {
                    chatAdapter = new ChatAdapter(ChatActivity.this, recentChatList,
                            senderId, senderName, senderProfileImage);
                    binding.rvRecentChatList.setAdapter(chatAdapter);
                    a = true;

                } else {
                    recentChatList.clear();
                    binding.tvNoData.setVisibility(View.VISIBLE);
                    if (a) {
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(eventListener);
    }
}