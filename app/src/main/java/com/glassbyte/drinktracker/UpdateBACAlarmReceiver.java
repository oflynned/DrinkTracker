package com.glassbyte.drinktracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * Created by Maciej on 25/08/2015.
 */
public class UpdateBACAlarmReceiver extends WakefulBroadcastReceiver{
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, UpdateCurrentBACService.class);
        startWakefulService(context, service);
    }

    public void setAlarm(Context context){
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intet = new Intent(context, UpdateBACAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intet, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis()+(15*60*1000)); //set fire off time to 15 minutes from right now

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);

        /**NEXT IMPLEMENT TO BOOTRECEIVER SO THAT THE ALARM AUTOMATICALLY RESTARTS THE ALARM WHEN THE DEVICE IS REBOOTED**/
    }

    public void cancelAlarm(Context context){
        if (alarmMgr!=null) {
            alarmMgr.cancel(alarmIntent);
        }

        /**WHEN BOOTRECEIVER IMPLEMENTED WRITE CODE HERE TO DISABLE THE BOOT RECEIVER SO THE ALARM WONT BE RESET AT THE BOOT**/
    }
}

