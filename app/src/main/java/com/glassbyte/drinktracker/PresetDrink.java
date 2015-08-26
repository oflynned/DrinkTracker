package com.glassbyte.drinktracker;

import android.app.DialogFragment;
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

    private float alcPercentage = 0;
    private float alcVolume = 0;

    Button setPercentage, setVolume, drink;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.activity_presetdrink, container, false);

        glass = (ImageView) V.findViewById(R.id.presetDrink);
        glass.setImageResource(R.drawable.ic_launcher);

        setPercentage = (Button) V.findViewById(R.id.presetSetPercentage);
        setPercentage.setOnClickListener(this);

        setVolume = (Button) V.findViewById(R.id.presetSetVolume);
        setVolume.setOnClickListener(this);

        drink = (Button) V.findViewById(R.id.presetAddDrink);
        drink.setOnClickListener(this);

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

        setVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetVolumeDialog dialog = new SetVolumeDialog();
                dialog.show(PresetDrink.this.getActivity().getFragmentManager(), "setVolumeDialog");
                dialog.setSetVolumeDialogListener(new SetVolumeDialog.SetVolumeDialogListener() {
                    @Override
                    public void onDoneClick(DialogFragment dialog) {
                        PresetDrink.this.alcVolume = ((SetVolumeDialog) dialog).getVolume();
                        setVolume(alcVolume);
                    }
                });
            }
        });

        setPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetPercentageDialog dialog = new SetPercentageDialog();
                dialog.show(PresetDrink.this.getActivity().getFragmentManager(), "setPercentageDialog");
                dialog.setSetPercentageDialogListener(new SetPercentageDialog.SetPercentageDialogListener() {
                    @Override
                    public void onDoneClick(DialogFragment dialog) {
                        PresetDrink.this.alcPercentage = ((SetPercentageDialog) dialog).getPercentage();
                        setPercentage(alcPercentage);
                    }
                });
            }
        });

        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double ebac = bloodAlcoholContent.getEstimatedBloodAlcoholContent(getVolume(), getPercentage());
                dou.insertNewDrink(dou.getDateTime(), getTitle(), getVolume(), getPercentage(), ebac);
                bloodAlcoholContent.setCurrentEbac((float) (bloodAlcoholContent.getCurrentEbac() + ebac));

                Toast.makeText(getActivity(), "Drink added successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        bloodAlcoholContent = new BloodAlcoholContent(this.getActivity());

        dou = new DatabaseOperationsUnits(getActivity());

        return V;
    }

    public void setTitle(String title){this.title = title;}
    public String getTitle(){return title;}
    public void setVolume(double mlSize){this.mlSize = mlSize;}
    public double getVolume(){return mlSize;}
    public void setPercentage(double percentage){this.percentage = percentage;}
    public double getPercentage(){return percentage;}

    @Override
    public void onClick(View view) {

    }
}