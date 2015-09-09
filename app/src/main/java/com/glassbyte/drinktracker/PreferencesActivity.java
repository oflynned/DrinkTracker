package com.glassbyte.drinktracker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by ed on 30/05/15.
 */
public class PreferencesActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        //push to xml in settings
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
    }

    public class PreferenceFragment extends android.preference.PreferenceFragment{
        SharedPreferences sp;
        Activity thisActivity;
        PreferenceScreen preferenceScreen;
        PreferenceCategory profileDetailsCategory;
        Preference editHeightPref, editWeightPref, editGenderPref, editUnitsPref;

        @Override
        public void onCreate(final Bundle savedInstanceBundle){
            super.onCreate(savedInstanceBundle);
            thisActivity = this.getActivity();
            sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

            //Set up the components of the preference Scree
            preferenceScreen = getPreferenceManager().createPreferenceScreen(this.getActivity());

            PreferenceCategory moreCategory = new PreferenceCategory(this.getActivity());
            moreCategory.setTitle(R.string.more_from_glassbyte);
            moreCategory.setKey(getString(R.string.pref_key_contact));
            preferenceScreen.addPreference(moreCategory);

            Preference glassbyteWebsite = new Preference(this.getActivity());
            glassbyteWebsite.setTitle(R.string.visit_website);
            glassbyteWebsite.setSummary(R.string.summer_glassbyte);
            Intent visitGlassbyteWebsiteIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.glassbyte.com"));
            glassbyteWebsite.setIntent(visitGlassbyteWebsiteIntent);

            Preference removeAds = new Preference(this.getActivity());
            removeAds.setTitle(R.string.remove_ads);
            removeAds.setSummary(R.string.summary_ads);
            removeAds.setIntent(visitGlassbyteWebsiteIntent);

            moreCategory.addPreference(glassbyteWebsite);
            moreCategory.addPreference(removeAds);
            
            final PreferenceCategory profileDetailsCategory = new PreferenceCategory(this.getActivity());
            profileDetailsCategory.setTitle(R.string.modify_details);
            profileDetailsCategory.setKey(getString(R.string.pref_key_storage_settings));
            preferenceScreen.addPreference(profileDetailsCategory);

            editGenderPref = new ListPreference(this.getActivity());
            editGenderPref.setTitle(R.string.modify_gender);
            editGenderPref.setSummary(sp.getString(getString(R.string.pref_key_editGender), ""));
            editGenderPref.setKey(getString(R.string.pref_key_editGender));
            ((ListPreference)editGenderPref).setEntries(R.array.optgender);
            ((ListPreference)editGenderPref).setEntryValues(R.array.optgender);

            editUnitsPref = new ListPreference(this.getActivity());
            editUnitsPref.setTitle(R.string.modify_units);
            editUnitsPref.setSummary(sp.getString(getString(R.string.pref_key_editUnits), ""));
            editUnitsPref.setKey(getString(R.string.pref_key_editUnits));
            ((ListPreference)editUnitsPref).setEntries(R.array.optunits);
            ((ListPreference)editUnitsPref).setEntryValues(R.array.optunits);

            String strUnits = sp.getString(getString(R.string.pref_key_editUnits),"");

            if(strUnits.equalsIgnoreCase("metric")) {
                editHeightPref = new EditTextPreference(this.getActivity());
                editHeightPref.setSummary(sp.getString(getString(R.string.pref_key_editHeight),"") + " cm");
            } else {
                editHeightPref = new ImperialEditHeightDialog(this.getActivity(), null);
                int height = Integer.valueOf(sp.getString(getString(R.string.pref_key_editHeight),""));
                double[] feetAndInchesHeight =
                        BloodAlcoholContent.MetricSystemConverter.converCmToFeetAndInches(height);
                editHeightPref.setSummary(String.valueOf((int)feetAndInchesHeight[0]) + " " + getString(R.string.foot_and) + " "
                        + (int)BloodAlcoholContent.round(Double.valueOf(feetAndInchesHeight[1]), 0)
                        + " " + getString(R.string.inches));
            }
            editHeightPref.setTitle(R.string.modify_height);
            editHeightPref.setKey(getString(R.string.pref_key_editHeight));

            editWeightPref = new EditTextPreference(this.getActivity());
            editWeightPref.setTitle(R.string.modify_weight);
            int weight = Integer.parseInt(sp.getString(getString(R.string.pref_key_editWeight), ""));
            if (strUnits.equalsIgnoreCase("metric")) {
                editWeightPref.setSummary(weight+" kg");
            } else {
                editWeightPref.setSummary(
                        (int)BloodAlcoholContent.round(
                                BloodAlcoholContent.MetricSystemConverter.convertKilogramsToPounds(weight)
                                ,0) + " " + getString(R.string.pounds)
                );
            }
            editWeightPref.setKey(getString(R.string.pref_key_editWeight));
            editWeightPref.setPersistent(false); //update the data manually based on the current unit system

            profileDetailsCategory.addPreference(editGenderPref);
            profileDetailsCategory.addPreference(editUnitsPref);
            profileDetailsCategory.addPreference(editWeightPref);
            profileDetailsCategory.addPreference(editHeightPref);


            PreferenceCategory languageCategory = new PreferenceCategory(this.getActivity());
            languageCategory.setTitle(R.string.language_options);
            languageCategory.setKey(getString(R.string.pref_key_irish));
            preferenceScreen.addPreference(languageCategory);

            CheckBoxPreference irishLanguage = new CheckBoxPreference(this.getActivity());
            irishLanguage.setTitle(R.string.irish_language_option);
            irishLanguage.setDefaultValue(false);
            irishLanguage.setSummary(R.string.summer_irish);
            irishLanguage.setKey(getString(R.string.pref_key_irish));

            languageCategory.addPreference(irishLanguage);

            setPreferenceScreen(preferenceScreen);
            //End of Set up the components of the preference Scree


            //Set up onChangeListeners for each preference
            editGenderPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    preference.setSummary(String.valueOf(o));
                    return true;
                }
            });

            editHeightPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    preference.setSummary(String.valueOf(o));
                    return true;
                }
            });

            editUnitsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    preference.setSummary(String.valueOf(o));

                    profileDetailsCategory.removeAll();

                    int height = Integer.valueOf(sp.getString(getString(R.string.pref_key_editHeight),""));
                    if (((String)o).equalsIgnoreCase("metric")) {
                        editHeightPref = new EditTextPreference(thisActivity);
                        editHeightPref.setSummary(sp.getString(getString(R.string.pref_key_editHeight),""));
                    } else {
                        editHeightPref = new ImperialEditHeightDialog(thisActivity,null);
                        double[] feetAndInchesHeight = BloodAlcoholContent.MetricSystemConverter.converCmToFeetAndInches(height);
                        editHeightPref.setSummary(String.valueOf((int)feetAndInchesHeight[0]) + R.string.foot_and
                                + (int)BloodAlcoholContent.round(Double.valueOf(feetAndInchesHeight[1]), 0)
                                + R.string.inches);
                    }
                    editHeightPref.setTitle(getString(R.string.modify_height));
                    editHeightPref.setKey(getString(R.string.pref_key_editHeight));

                    int weight = Integer.valueOf(sp.getString(getString(R.string.pref_key_editWeight),""));
                    if (((String)o).equalsIgnoreCase("metric")) {
                        editWeightPref.setSummary(weight + "kg");
                    } else {
                        editWeightPref.setSummary(
                                (int)BloodAlcoholContent.MetricSystemConverter.convertKilogramsToPounds(weight)
                                        + getString(R.string.pounds));
                    }
                    //Update the view of the profileDetailsCategory
                    profileDetailsCategory.addPreference(editGenderPref);
                    profileDetailsCategory.addPreference(editUnitsPref);
                    profileDetailsCategory.addPreference(editHeightPref);
                    profileDetailsCategory.addPreference(editWeightPref);
                    ((BaseAdapter)preferenceScreen.getRootAdapter()).notifyDataSetChanged();
                    //End of Update the view of the profileDetailsCategory

                    return true;
                }
            });

            editWeightPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    SharedPreferences.Editor e = sp.edit();
                    String strUnits = sp.getString(getString(R.string.pref_key_editUnits),"");
                    if (strUnits.equalsIgnoreCase("metric")) {
                        e.putString(preference.getKey(),String.valueOf(o));
                        preference.setSummary(String.valueOf(o) + " kg");
                    } else {
                        int kgWeight = (int)BloodAlcoholContent.round(
                                BloodAlcoholContent.MetricSystemConverter.convertPoundsToKilograms(
                                        Double.valueOf((String) o)),0);
                        e.putString(preference.getKey(), String.valueOf(kgWeight));
                        preference.setSummary(String.valueOf(o) + getString(R.string.pounds));
                    }
                    e.apply();
                    return true;
                }
            });

            irishLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    preference.setSummary(String.valueOf(o));
                    return true;
                }
            });
            //End of Set up onChangeListeners for each preference

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

    }
}
