package com.glassbyte.drinktracker;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ed on 30/05/15.
 */
public class PreferencesActivity extends PreferenceActivity {

    public static final String PREF_FILE_NAME = "Settings";

    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        //push to xml in settings
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
    }

    public static class PreferenceFragment extends android.preference.PreferenceFragment{


        @Override
        public void onCreate(final Bundle savedInstanceBundle){
            super.onCreate(savedInstanceBundle);
            addPreferencesFromResource(R.xml.preferences);

            Preference gender = findPreference(getResources().getString(R.string.pref_key_editGender));

            Toast.makeText(getActivity(),gender.toString(),Toast.LENGTH_SHORT).show();
            Log.i("GENDER LOG:",gender.toString());

            gender.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(String.valueOf(newValue));
                    return true;
                }
            });

            Preference units = findPreference(getResources().getString(R.string.pref_key_editUnits));
            units.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(String.valueOf(newValue));
                    return true;
                }
            });

            Preference height = findPreference(getResources().getString(R.string.pref_key_editHeight));
            height.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(String.valueOf(newValue));
                    return true;
                }
            });

            Preference weight = findPreference(getResources().getString(R.string.pref_key_editWeight));
            weight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(String.valueOf(newValue));
                    return true;
                }
            });

            Preference data = findPreference(getResources().getString(R.string.pref_key_dataCollection));
            data.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(String.valueOf(newValue));
                    return true;
                }
            });
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

            Preference gender = findPreference(getResources().getString(R.string.pref_key_editGender));
            gender.setSummary(sharedPref.getString(gender.getKey(), ""));

            Preference units = findPreference(getResources().getString(R.string.pref_key_editUnits));
            units.setSummary(sharedPref.getString(units.getKey(), ""));

            Preference height = findPreference(getResources().getString(R.string.pref_key_editHeight));
            height.setSummary(sharedPref.getString(height.getKey(), ""));

            Preference weight = findPreference(getResources().getString(R.string.pref_key_editWeight));
            weight.setSummary(sharedPref.getString(weight.getKey(), ""));

            Preference data = findPreference(getResources().getString(R.string.pref_key_dataCollection));
            data.setSummary(sharedPref.getString(data.getKey(), ""));
        }
    }
}
