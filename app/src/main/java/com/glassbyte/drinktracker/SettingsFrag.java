package com.glassbyte.drinktracker;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ed on 30/05/15.
 */
public class SettingsFrag extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.activity_settings, container, false);
        return V;
    }
}
