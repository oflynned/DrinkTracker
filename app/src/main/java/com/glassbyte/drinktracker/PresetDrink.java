package com.glassbyte.drinktracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

/**
 * Created by root on 27/05/15.
 */

public class PresetDrink extends Fragment implements View.OnClickListener {

    Button addDrink;
    private DatabaseOperationsUnits DOU;
    private Cursor CR;
    private ImageView glass;
    private Spinner spinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.activity_presetdrink, container, false);

        glass = (ImageView) V.findViewById(R.id.presetDrink);

        spinner = (Spinner) V.findViewById(R.id.spinnerPresetDrink);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                int position = spinner.getSelectedItemPosition();
                switch (position){
                    //beer
                    case 0:
                        glass.setImageResource(R.drawable.beer_bottle);
                        break;
                    //wine
                    case 1:
                        glass.setImageResource(R.drawable.glass_wine);
                        break;
                    case 2:
                        glass.setImageResource(R.drawable.shot);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        addDrink = (Button) V.findViewById(R.id.presetAddDrink);
        addDrink.setOnClickListener(this);

        return V;
    }

    //add a preset row to the database
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