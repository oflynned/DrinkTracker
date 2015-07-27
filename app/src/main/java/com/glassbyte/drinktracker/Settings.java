package com.glassbyte.drinktracker;

import android.content.SharedPreferences;

/**
 * Created by ed on 27/07/15.
 */
public class Settings {
    private static final String GENDER = "pref_key_editGender";
    private static final String UNITS = "pref_key_editUnits";
    private static final String HEIGHT = "pref_key_editHeight";
    private static final String WEIGHT = "pref_key_editWeight";
    private static final String DATA = "pref_key_data";

    private String pref_key_editGender;
    private String pref_key_editUnits;
    private String pref_key_editHeight;
    private String pref_key_editWeight;
    private boolean pref_key_data;

    public String getGender() {
        return pref_key_editGender;
    }

    public void setGender(String pref_key_editGender) {
        this.pref_key_editGender = pref_key_editGender;
    }

    public String getUnits() {
        return pref_key_editGender;
    }

    public void setUnits(String pref_key_editUnits) {
        this.pref_key_editUnits = pref_key_editUnits;
    }

    public String getHeight() {
        return pref_key_editHeight;
    }

    public void setHeight(String pref_key_editHeight) {
        this.pref_key_editHeight = pref_key_editHeight;
    }

    public String getWeight() {
        return pref_key_editWeight;
    }

    public void setWeight(String pref_key_editWeight) {
        this.pref_key_editWeight = pref_key_editWeight;
    }

    public boolean isSetData(boolean pref_key_data) {
        return pref_key_data;
    }

    public void load(SharedPreferences prefs) {
        pref_key_editGender = prefs.getString(UNITS, "");
        pref_key_editUnits = prefs.getString(GENDER, "");
        pref_key_editHeight = prefs.getString(HEIGHT, "");
        pref_key_editWeight = prefs.getString(WEIGHT, "");
        pref_key_data = prefs.getBoolean(DATA, false);
    }

    public void save(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        save(editor);
        editor.apply();
    }

    public void save(SharedPreferences.Editor editor) {
        editor.putString(GENDER, pref_key_editGender);
        editor.putString(UNITS, pref_key_editUnits);
        editor.putString(HEIGHT, pref_key_editHeight);
        editor.putString(WEIGHT, pref_key_editWeight);
        editor.putBoolean(DATA, pref_key_data);
    }
}
