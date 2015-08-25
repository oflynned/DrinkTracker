package com.glassbyte.drinktracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Maciej on 25/08/2015.
 */
public class UpdateCurrentBACBootReceiver extends BroadcastReceiver {
    UpdateCurrentBACAlarmReceiver alarm = new UpdateCurrentBACAlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            alarm.setAlarm(context);
        }
    }
}
