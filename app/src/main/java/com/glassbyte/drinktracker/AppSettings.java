package com.glassbyte.drinktracker;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ed on 27/07/15.
 */
public class AppSettings extends Settings {
    private final MyApplication application;

    public static AppSettings getSettings(Activity activity) {
        return getSettings(activity.getApplication());
    }

    public static AppSettings getSettings(Application application) {
        return ((MyApplication) application).settings;
    }

    public AppSettings(MyApplication application) {
        this.application = application;
    }

    public void load() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        load(prefs);
    }

    public void save() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        save(prefs);
    }
}
