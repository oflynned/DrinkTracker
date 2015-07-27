package com.glassbyte.drinktracker;

import android.app.Application;

/**
 * Created by ed on 27/07/15.
 */
public class MyApplication extends Application {

    public final AppSettings settings = new AppSettings(this);

    @Override
    public void onCreate() {
        super.onCreate();
        settings.load();
    }
}
