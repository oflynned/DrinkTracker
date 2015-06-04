package com.glassbyte.drinktracker;

import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by root on 27/05/15.
 */

public class PresetDrink extends Fragment implements View.OnClickListener {

    Button addDrink;
    private DatabaseOperationsUnits DOU;
    private Cursor CR;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.activity_presetdrink, container, false);


        addDrink = (Button) V.findViewById(R.id.presetAddDrink);
        addDrink.setOnClickListener(this);

        return V;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.presetAddDrink:

                DatabaseOperationsUnits DOU = new DatabaseOperationsUnits(getActivity());
                Cursor CR = DOU.getInfo(DOU);

                CR.moveToLast();

                if(CR.getString(3) == "0") {
                    DOU.putInfo(
                            DOU,
                            DOU.getDateTime(), //time
                            addUnits(), //units of alcohol
                            drinkPercentage(), //percentage
                            BACformula() //bac
                    );

                    CR.moveToNext(); //increment table
                    CR.close();
                }
                else{
                    DOU.putInfo(
                            DOU,
                            DOU.getDateTime(), //time
                            addUnits(), //units of alcohol
                            drinkPercentage(), //percentage
                            updateBAC() //bac
                    );

                    CR.moveToNext(); //increment table
                    CR.close();
                }
                break;

        }
    }

    private double addUnits() {
        return 20;
    }

    private double drinkPercentage() {
        return 0.4;
    }

    private double BACformula() {
        return 0;
    }

    private double updateBAC() {
        return 10;
    }
}
