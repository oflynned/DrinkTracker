package com.glassbyte.drinktracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Random;

/**
 * Created by root on 27/05/15.
 */

public class PresetDrink extends Fragment implements View.OnClickListener{

    private DatabaseOperationsUnits DOU;
    private Cursor CR;

    //random seed
    Random random = new Random();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.activity_presetdrink, container, false);

        Button btnDatabase = (Button) V.findViewById(R.id.btnGraphs);
        Button btnGraphs = (Button) V.findViewById(R.id.btnDatabase);
        Button btnAddDBMember = (Button) V.findViewById(R.id.btnAddDBMember);

        btnGraphs.setOnClickListener(this);
        btnDatabase.setOnClickListener(this);
        btnAddDBMember.setOnClickListener(this);

        random.setSeed(123456789);

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
            case R.id.btnAddDBMember:

                //add items to database
                DatabaseOperationsUnits DOU = new DatabaseOperationsUnits(getActivity());
                Cursor CR = DOU.getInfo(DOU);

                CR.moveToLast();
                DOU.putInfo(
                        DOU,
                        DOU.getDateTime(), //time
                        getRandom(10), //units of alcohol
                        getRandom(10), //percentage
                        getRandom(10) //bac
                );
                CR.moveToNext(); //increment table
                CR.close();
                break;
        }
    }

    public int getRandom(int i){
        return random.nextInt(i);
    }
}
