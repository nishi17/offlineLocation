package com.demo.offlinelocation.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.demo.offlinelocation.Common;

/**
 * Created by Nishi on 5/2/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private SharedPreferences sharedPreferences;
    private boolean isfirsttime;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences(Common.getsharedPref, Context.MODE_PRIVATE);

        isfirsttime = sharedPreferences.getBoolean(Common.isfiesttime, false);

        Intent background = new Intent(context, LocationTracker.class);
        //  context.startService(background);
        if (isfirsttime) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                context.startForegroundService(background/*new Intent(context, LocationTracker.class)*/);
                sharedPreferences.edit().putBoolean(Common.isfiesttime, false);
            } else {
                context.startService(background/*new Intent(context, LocationTracker.class)*/);
            }
        } else {
            context.startService(background);
        }
    }

}