package com.glassbyte.drinktracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * Created by Maciej on 25/08/2015.
 */
public class UpdateCurrentBACAlarmReceiver extends WakefulBroadcastReceiver{
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, UpdateCurrentBACService.class);
        startWakefulService(context, service);
    }

    public void setAlarm(Context context){
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intet = new Intent(context, UpdateCurrentBACAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intet, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + (15 * 60 * 1000)); //set fire off time to 15 minutes from right now

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                alarmIntent);

        /**NEXT IMPLEMENT THE BOOTRECEIVER SO THAT THE ALARM AUTOMATICALLY RESTARTS THE ALARM WHEN THE DEVICE IS REBOOTED**/
        ComponentName receiver = new ComponentName(context, UpdateCurrentBACBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context){
        if (alarmMgr!=null) {
            alarmMgr.cancel(alarmIntent);
        }

        /**DISABLE THE BOOT RECEIVER SO THE ALARM WONT BE RESET AT THE BOOT**/
        ComponentName receiver = new ComponentName(context, UpdateCurrentBACBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}

