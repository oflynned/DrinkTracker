package com.glassbyte.drinktracker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        BloodAlcoholContent.updateElapsedBAC(getApplicationContext());
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
