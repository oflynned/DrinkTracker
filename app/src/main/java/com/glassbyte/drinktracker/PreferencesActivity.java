package com.glassbyte.drinktracker;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by ed on 30/05/15.
 */
public class PreferencesActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        SharedPreferences spGender = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences spUnits = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences spHeight = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences spWeight = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String stringGender = spGender.getString("pref_key_editGender", "");

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
    }

    public static class PreferenceFragment extends android.preference.PreferenceFragment{
        @Override
        public void onCreate(final Bundle savedInstanceBundle){
            super.onCreate(savedInstanceBundle);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
