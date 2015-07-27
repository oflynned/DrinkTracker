package com.glassbyte.drinktracker;

import android.database.Cursor;
import android.os.Bundle;
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

    private double units;
    private double percentage;
    private double BAC;

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
                switch (position){
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
                            getUnits(units), //units of alcohol
                            getPercentage(percentage), //percentage
                            getBAC(BAC) //bac
                    );

                    CR.moveToNext(); //increment table
                    CR.close();
                }
                else{
                    DOU.putInfo(
                            DOU,
                            DOU.getDateTime(), //time
                            getUnits(units), //units of alcohol
                            getPercentage(percentage), //percentage
                            getBAC(BAC) //bac
                    );

                    CR.moveToNext(); //increment table
                    CR.close();
                }
                break;
        }
    }

    private double getUnits(double units) {
        return units;
    }

    private void setUnits(double units){
        this.units = units;
    }

    private  double getPercentage(double percentage){
        return percentage;
    }

    private void setPercentage(double percentage){
        this.percentage= percentage;
    }

    private double getBAC(double BAC){
        return BAC;
    }

    private void setBAC(double BAC){
        this.BAC = BAC;
    }
}