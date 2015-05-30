package com.glassbyte.drinktracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by root on 27/05/15.
 */

public class PresetDrink extends Fragment implements View.OnClickListener{



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.activity_presetdrink, container, false);

        Button btnDatabase = (Button) V.findViewById(R.id.btnGraphs);
        Button btnGraphs = (Button) V.findViewById(R.id.btnDatabase);

        btnGraphs.setOnClickListener(this);
        btnDatabase.setOnClickListener(this);

        return V;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnDatabase:
                intent = new Intent(getActivity(), AndroidDatabaseManager.class);
                startActivity(intent);
                break;
            case R.id.btnGraphs:
                intent = new Intent(getActivity(), PreferencesActivity.class);
                startActivity(intent);
                break;
        }
    }
}
