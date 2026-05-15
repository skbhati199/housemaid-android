package com.housemaid.constants;

import android.content.Context;
import android.util.Log;

import com.housemaid.utils.SharedPreference;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by fluper on 8/5/18.
 */

public class Constants {

    private static final String TAG = "Constants";

    public static final int AUTOCOMPLETE_REQUEST_CODE = 121;
    public static Object BINDING = null;
    public static final String MY_PREFERENCES = "prefs";
    public static final String DEVICE_TYPE = "1";
    public static final String TIMEZONE = "Asia/calcutta";
    public static final String LOCALE = "en";

    public static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 10;

    /**
     * Gets the FCM token asynchronously.
     * Replaces the deprecated null /* TODO: use FirebaseMessaging.getInstance().getToken() */.
     */
    public static void getFcmToken(FcmTokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM token failed",
                                task.getException());
                        if (callback != null) {
                            callback.onTokenReceived(null);
                        }
                        return;
                    }
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                    if (callback != null) {
                        callback.onTokenReceived(token);
                    }
                });
    }

    public interface FcmTokenCallback {
        void onTokenReceived(String token);
    }

    public static String userType(Context context) {
        SharedPreference sharedPreference;
        sharedPreference = SharedPreference.getInstance(context);

        if (sharedPreference.getInteger("entry_key", 0) == 1)
            return "2";
        if (sharedPreference.getInteger("entry_key", 0) == 2)
            return "1";
        if (sharedPreference.getInteger("entry_key", 0) == 3)
            return "3";
        else
            return "0";
    }
}
