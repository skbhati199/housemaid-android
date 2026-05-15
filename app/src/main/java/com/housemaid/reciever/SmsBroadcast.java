package com.housemaid.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SmsBroadcast extends BroadcastReceiver {
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey("pdus")) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);
            String senderNumber = sms.getOriginatingAddress();
            String message = sms.getMessageBody();
            if(message.contains("MAID"))
            {
                String numberOnly= message.replaceAll("[^0-9]", "");
                Intent intent1 = new Intent("VERIFY");
                intent1.putExtra("number",numberOnly);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
            }
        }


    }


}
