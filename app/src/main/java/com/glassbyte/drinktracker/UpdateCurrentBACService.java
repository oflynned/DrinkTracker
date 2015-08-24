package com.glassbyte.drinktracker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

/**
 * Created by Maciej on 24/08/2015.
 */
public class UpdateCurrentBACService extends IntentService {
    SharedPreferences sp;

    public UpdateCurrentBACService() {
        super("UpdateCurrentBACService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Context ctx = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        float currentBAC = sp.getFloat(this.getString(R.string.pref_key_currentEbac), 0);
        float subtrahend = (float)(BloodAlcoholContent.ELAPSED_HOUR_FACTOR); //get 15 minutes

        while (currentBAC != 0) {

            if (currentBAC-subtrahend > 0)
                currentBAC -= subtrahend;
            else
                currentBAC = 0;

            SharedPreferences.Editor e = sp.edit();
            e.putFloat(this.getString(R.string.pref_key_currentEbac), currentBAC);
            e.apply();

            synchronized (this) {
                try {
                    wait(60*1000/4); // for testing purposes i change it every 15 sec
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("Current ebac: "+currentBAC);
            //The currentBAC could have changed during the wait time, so before running the loop check again
            currentBAC = sp.getFloat(this.getString(R.string.pref_key_currentEbac), 0);
        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
