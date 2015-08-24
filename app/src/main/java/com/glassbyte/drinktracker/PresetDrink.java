package com.glassbyte.drinktracker;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by root on 27/05/15.
 */

public class PresetDrink extends Fragment implements View.OnClickListener {

    Button addDrink;
    private DatabaseOperationsUnits dou;
    private Cursor CR;
    private ImageView glass;
    private Spinner drinksChoice;
    private Spinner percentageChoice;
    private TextView addPercentageText;

    private BloodAlcoholContent bloodAlcoholContent;

    //for setting input for database
    private double mlSize;
    private double percentage;
    private String title;

    NumberPicker ml1, ml2, ml3, p1, p2, p3;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.activity_presetdrink, container, false);

        ml1 = (NumberPicker) V.findViewById(R.id.ml1);
        ml2 = (NumberPicker) V.findViewById(R.id.ml2);
        ml3 = (NumberPicker) V.findViewById(R.id.ml3);

        p1 = (NumberPicker) V.findViewById(R.id.p1);
        p2 = (NumberPicker) V.findViewById(R.id.p2);
        p3 = (NumberPicker) V.findViewById(R.id.p3);

        ml1.setMaxValue(9);
        ml1.setMinValue(0);
        ml1.setValue(0);

        ml2.setMaxValue(9);
        ml2.setMinValue(0);
        ml2.setValue(0);

        ml3.setMaxValue(9);
        ml3.setMinValue(0);
        ml3.setValue(0);

        p1.setMaxValue(9);
        p1.setMinValue(0);
        p1.setValue(0);

        p2.setMaxValue(9);
        p2.setMinValue(0);
        p2.setValue(0);

        p3.setMaxValue(9);
        p3.setMinValue(0);
        p3.setValue(0);

        glass = (ImageView) V.findViewById(R.id.presetDrink);
        glass.setImageResource(R.drawable.ic_launcher);

        drinksChoice = (Spinner) V.findViewById(R.id.spinnerPresetDrink);
        drinksChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        addDrink = (Button) V.findViewById(R.id.presetAddDrink);
        addDrink.setOnClickListener(this);

        bloodAlcoholContent = new BloodAlcoholContent(this.getActivity());

        dou = new DatabaseOperationsUnits(getActivity());

        return V;
    }

    //add a preset row to the database
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.presetAddDrink:

                //set title from spinner
                setTitle(drinksChoice.getItemAtPosition(drinksChoice.getSelectedItemPosition()).toString());

                //get each number and concatenate + cast
                String Ml1, Ml2, Ml3;
                Ml1 = String.valueOf(ml1.getValue());
                Ml2 = String.valueOf(ml2.getValue());
                Ml3 = String.valueOf(ml3.getValue());
                String volume = Ml1 + Ml2 + Ml3;
                setMlSize(Double.parseDouble(volume));

                //set percentage
                String P1, P2, P3;
                P1 = String.valueOf(p1.getValue());
                P2 = String.valueOf(p2.getValue());
                P3 = String.valueOf(p3.getValue());
                String Percentage = P1 + P2 + "." + P3;
                setPercentage(Double.parseDouble(Percentage));

                double ebac = bloodAlcoholContent.getEstimatedBloodAlcoholContent(mlSize, percentage);
                dou.insertNewDrink(dou.getDateTime(), getTitle(), getMlSize(), getPercentage(), ebac);
                bloodAlcoholContent.setCurrentEbac((float) (bloodAlcoholContent.getCurrentEbac() + ebac));

                Toast.makeText(getActivity(), "Drink added successfully!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setTitle(String title){this.title = title;}
    public String getTitle(){return title;}
    public void setMlSize(double mlSize){this.mlSize = mlSize;}
    public double getMlSize(){return mlSize;}
    public void setPercentage(double percentage){this.percentage = percentage;}
    public double getPercentage(){return percentage;}
}