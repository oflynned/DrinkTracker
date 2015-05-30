package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by ed on 30/05/15.
 */
public class PreferencesActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        PreferenceManager.setDefaultValues(getBaseContext(),R.xml.preferences,false);
        addPreferencesFromResource(R.xml.preferences);
    }
}
