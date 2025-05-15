package com.housemaid.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.adapter.CountryAdapter;
import com.housemaid.model.CountryListModel;

import java.util.ArrayList;

/**
 * Created by fluper on 11/5/18.
 */


public class ValidationUtils {



    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean isOnline(View relativeLayout, Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            final Snackbar snackbar = Snackbar.make(relativeLayout,
                    "No internet connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(Color.WHITE);
            View sbView = snackbar.getView();
            sbView.setAlpha(0.8f);
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
            return false;
        }
        return true;
    }

    public static boolean emailMatch(String mEmail, Context context) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (TextUtils.isEmpty(mEmail)) {
            Toast.makeText(context, "Enter your email Id",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mEmail.matches(emailPattern)) {
            Toast.makeText(context, "Enter a valid email Id",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static boolean mobileMatch(String mMobile, Context context) {
        if (TextUtils.isEmpty(mMobile)) {
            Toast.makeText(context, "Enter mobile number",
                    Toast.LENGTH_SHORT).show();
            return false;
        } if (mMobile.length() < 5){
            Toast.makeText(context, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static boolean userNameEmpty(String mUserName, Context context) {
        if (TextUtils.isEmpty(mUserName)) {
            Toast.makeText(context, "Enter username",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static boolean countryEmpty(String mUserName, Context context) {
        if (TextUtils.isEmpty(mUserName)) {
            Toast.makeText(context, "Please select country",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    } public static boolean stateEmpty(String mUserName, Context context) {
        if (TextUtils.isEmpty(mUserName)) {
            Toast.makeText(context, "Please select state",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }public static boolean nationalityEmpty(String mUserName, Context context) {
        if (TextUtils.isEmpty(mUserName)) {
            Toast.makeText(context, "Please select nationality",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static boolean passwordEmpty(String mPassword, Context context) {
        if (TextUtils.isEmpty(mPassword)) {
            Toast.makeText(context, "Enter the password",
                    Toast.LENGTH_SHORT).show();
            return false;
        }if (mPassword.length() < 8){
            Toast.makeText(context, "Password should be atleast 8 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }   public static boolean conformPasswordEmpty(String mPassword, Context context) {
        if (TextUtils.isEmpty(mPassword)) {
            Toast.makeText(context, "Please enter confirm password",
                    Toast.LENGTH_SHORT).show();
            return false;
        }if (mPassword.length() < 8){
            Toast.makeText(context, "Password should be atleast 8 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static boolean passwordMatch(String mPassword, String mConfirmPassword,Context context) {
        if (!mPassword.equals(mConfirmPassword)){
            Toast.makeText(context, "Password do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static boolean check(CheckBox checkBox, Context context){
        if (!checkBox.isChecked()){
            Toast.makeText(context, "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static boolean otpEmpty(String mUserName, Context context) {
        if (TextUtils.isEmpty(mUserName)) {
            Toast.makeText(context, "Please enter OTP",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
