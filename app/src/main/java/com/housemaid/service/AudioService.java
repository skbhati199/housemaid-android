package com.housemaid.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.housemaid.R;

/**
 * Created by fluper on 27/7/18.
 */

@SuppressLint("Registered")
public class AudioService extends Service {

    MediaPlayer mp;

    public AudioService() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    public void onCreate()
    {
        mp = MediaPlayer.create(this, R.raw.samsung_original);
        mp.setLooping(false);

    }
    public void onDestroy()
    {
        mp.stop();
    }
    public void onStart(Intent intent,int startid){

        Log.d("TAG", "On start");
        mp.start();
    }
    public void startAudio(){
        mp = MediaPlayer.create(this, R.raw.samsung_original);
        mp.setLooping(false);
        mp.start();
    }
}
