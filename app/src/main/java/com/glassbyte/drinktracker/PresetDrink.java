package com.glassbyte.drinktracker;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
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
import android.widget.Toast;


/**
 * Created by root on 27/05/15.
 */

public class PresetDrink extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private DrinkTrackerDbHelper dou;
    private ImageView glass;
    private Spinner drinksChoice;

    private BloodAlcoholContent bloodAlcoholContent;
    SharedPreferences sp;

    //for setting input for database
    private String spUnits, units, title;

    private float alcPercentage = 0f;
    private int alcVolume = 0;

    Button setPercentage, setVolume, drink;
    TextView percentageChosen, volChosen;

    View V;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        V = inflater.inflate(R.layout.activity_presetdrink, container, false);

        spUnits = (sp.getString(getResources().getString(R.string.pref_key_editUnits), ""));

        if (spUnits.equals("metric") || spUnits.equals("Metric")) {
            setUnits("ml");
        } else {
            setUnits("oz");
        }

        glass = (ImageView) V.findViewById(R.id.presetDrink);
        glass.setImageResource(R.drawable.ic_launcher);

        percentageChosen = (TextView) V.findViewById(R.id.percentageChosen);
        volChosen = (TextView) V.findViewById(R.id.volChosen);

        setPercentage = (Button) V.findViewById(R.id.presetSetPercentage);
        setPercentage.setOnClickListener(this);

        setVolume = (Button) V.findViewById(R.id.presetSetVolume);
        setVolume.setOnClickListener(this);

        drink = (Button) V.findViewById(R.id.presetAddDrink);
        drink.setOnClickListener(this);

        percentageChosen.setText(getPercentage() + "%");
        volChosen.setText(getVolume() + getUnits());

        drinksChoice = (Spinner) V.findViewById(R.id.spinnerPresetDrink);
        drinksChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                setTitle(drinksChoice.getSelectedItem().toString());
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
                        PresetDrink.this.alcVolume = (int)((SetVolumeDialog) dialog).getVolume();
                        setVolume(alcVolume);
                        volChosen.setText(getVolume() + getUnits());
                        Vibrator vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        vb.vibrate(100);
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
                        String currPercentage = String.format("%.2f", alcPercentage);
                        percentageChosen.setText(currPercentage + "%");
                        Vibrator vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        vb.vibrate(100);
                    }
                });
            }
        });

        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dou.insertNewDrink(getTitle(), getVolume(), getPercentage());
                Vibrator vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(100);
                Toast.makeText(getActivity(), "Drink added successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        bloodAlcoholContent = new BloodAlcoholContent(this.getActivity());

        dou = new DrinkTrackerDbHelper(getActivity());

        return V;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setVolume(int alcVolume) {
        this.alcVolume = alcVolume;
    }

    public int getVolume() {
        return alcVolume;
    }

    public void setPercentage(float alcPercentage) {
        this.alcPercentage = alcPercentage;
    }

    public float getPercentage() {
        return alcPercentage;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getUnits() {
        return units;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (spUnits.equals("metric") || spUnits.equals("Metric")) {
            setUnits("ml");
        } else {
            setUnits("oz");
        }
        V.invalidate();
    }
}