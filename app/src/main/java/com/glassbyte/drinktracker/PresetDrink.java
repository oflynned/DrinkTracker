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
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Created by root on 27/05/15.
 */

public class PresetDrink extends Fragment implements View.OnClickListener {

    Button addDrink;
    private DatabaseOperationsUnits DOU;
    private Cursor CR;
    private ImageView glass;
    private Spinner drinksChoice;
    private Spinner percentageChoice;
    private TextView addPercentageText;

    private BloodAlcoholContent bloodAlcoholContent;

    private float units;
    private float percentage;
    private float BAC;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.activity_presetdrink, container, false);

        glass = (ImageView) V.findViewById(R.id.presetDrink);

        addPercentageText = (TextView) V.findViewById(R.id.percentagePreset);
        addPercentageText.setVisibility(View.GONE);

        percentageChoice = (Spinner) V.findViewById(R.id.spinnerPresetDrinkPercentage);
        percentageChoice.setVisibility(View.GONE);

        drinksChoice = (Spinner) V.findViewById(R.id.spinnerPresetDrink);
        drinksChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                int position = drinksChoice.getSelectedItemPosition();
                switch (position) {
                    //null
                    case 0:
                        glass.setImageResource(R.drawable.ic_launcher);
                        addPercentageText.setVisibility(View.GONE);
                        percentageChoice.setVisibility(View.GONE);
                        break;
                    //beer can
                    case 1:
                        glass.setImageResource(R.drawable.beer_can);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //beer bottle
                    case 2:
                        glass.setImageResource(R.drawable.beer_bottle);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //beer can
                    case 3:
                        glass.setImageResource(R.drawable.beer_can);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //cider bottle
                    case 4:
                        glass.setImageResource(R.drawable.beer_bottle);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //cider bottle
                    case 5:
                        glass.setImageResource(R.drawable.beer_bottle);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //cider can
                    case 6:
                        glass.setImageResource(R.drawable.beer_can);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //glass of wine 150ml
                    case 7:
                        glass.setImageResource(R.drawable.glass_wine);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //glass of wine 300ml
                    case 8:
                        glass.setImageResource(R.drawable.glass_wine);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //single shot
                    case 9:
                        glass.setImageResource(R.drawable.shot);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //double shot
                    case 10:
                        glass.setImageResource(R.drawable.shot);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //triple shot
                    case 11:
                        glass.setImageResource(R.drawable.shot);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //330ml alcopop
                    case 12:
                        glass.setImageResource(R.drawable.beer_bottle);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //330ml alcopop
                    case 13:
                        glass.setImageResource(R.drawable.beer_bottle);
                        addPercentageText.setVisibility(View.VISIBLE);
                        percentageChoice.setVisibility(View.VISIBLE);
                        break;
                    //cocktail
                    case 14:
                        glass.setImageResource(R.drawable.glass_water);
                        addPercentageText.setVisibility(View.GONE);
                        percentageChoice.setVisibility(View.GONE);
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

        /*Set up the bloodAlcoholLevel*/
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String gender = sp.getString(getResources().getString(R.string.pref_key_editGender),"");
        System.out.println("!@DFASASFASF@!!$@!@#$!@$@!$@!     -      " + gender);
        float weight = Float.valueOf(sp.getString(getResources().getString(R.string.pref_key_editWeight), ""));
        System.out.println("!@DFASASFASF@!!$@!@#$!@$@!$@!     -     weight: "+weight);
        boolean isMan = (gender == "male");
        bloodAlcoholContent = new BloodAlcoholContent(isMan, weight);
        /**/

        return V;
    }

    //add a preset row to the database
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.presetAddDrink:

                //instantiate the class's object dynamically and a cursor for choosing the row
                DatabaseOperationsUnits dou = new DatabaseOperationsUnits(getActivity());
                Cursor CR = dou.getInfo(dou);

                //move to the last row as to not override or cause a collision
                CR.moveToLast();

                //insert a row
                //we need:
                //the database instantiated context
                //time
                //percentage
                //bac via the formula
                float[] unitsArray = new float[1];
                unitsArray[0] = bloodAlcoholContent.getStandardDrinkFactor(units, percentage);

                float[] percentageArray = new float[1];
                percentageArray[0] = percentage;

                float ebac = 0f;
                try{
                    ebac = bloodAlcoholContent.getEstimatedBloodAlcoholContent(unitsArray,percentageArray,1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dou.insertNewDrink(
                        dou,
                        dou.getDateTime(), //time
                        units, //units of alcohol
                        percentage, //percentage
                        ebac //bac
                );

                //move to the next row and close the insertion as to not cause an exception
                CR.moveToNext(); //increment table
                CR.close();
                break;
        }
    }
}