package com.housemaid.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.housemaid.constants.Constants;

/**
 * Created by fluper on 8/5/18.
 */

public class  SharedPreference {
    private static SharedPreference pref;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;

    private SharedPreference(Context ctx) {
        sharedPreference=ctx.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE);
        editor=sharedPreference.edit();
    }

    public static SharedPreference getInstance(Context ctx) {
        if(pref==null) {
            pref=new SharedPreference(ctx);
        }
        return pref;
    }

    public void putString(String key,String value) {
        editor.putString(key, value);
        editor.commit();
    }
    public void putStrings(String key,String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putBoolean(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putInteger(String key,int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putFloat(String key,Float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public String getString(String key,String defValue) {
        return sharedPreference.getString(key, defValue);
    }

    public boolean getBoolean(String key, Boolean defValue) {
        return sharedPreference.getBoolean(key, defValue);
    }

    public int getInteger(String key,int defValue) {
        return sharedPreference.getInt(key, defValue);
    }

    public Float getFloat(String key,Float defValue) {
        return sharedPreference.getFloat(key, defValue);
    }

    public void deletePreference() {
        editor.clear();
        editor.commit();
    }

    public void deletePreference(String key) {
        editor.remove(key);
        editor.commit();
    }
}
