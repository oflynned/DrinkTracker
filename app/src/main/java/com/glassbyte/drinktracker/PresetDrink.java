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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;


/**
 * Created by root on 27/05/15.
 */

public class PresetDrink extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private DrinkTrackerDbHelper dou;
    private ImageView glass;
    private Spinner drinksChoice;
    private BloodAlcoholContent bloodAlcoholContent;
    private AdView adView;
    SharedPreferences sp;

    //for setting input for database
    private String spUnits, units, title;
    private float alcPercentage = 0f;
    private int alcVolume = 0;

    private final static String AD_ID = "ca-app-pub-3940256099942544/6300978111";

    Button setPercentage, setVolume, drink;
    TextView percentageChosen, volChosen;

    View V;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final RelativeLayout rl = new RelativeLayout(this.getActivity());
        rl.setBackgroundColor(getResources().getColor(R.color.orange100));

        adView = new AdView(getContext());
        RelativeLayout.LayoutParams paramsAds = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsAds.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        paramsAds.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adView.setLayoutParams(paramsAds);
        adView.setId(View.generateViewId());
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(AD_ID);

        //request ads to target emulated device
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        rl.addView(adView);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        });

        adView.loadAd(adRequestBuilder.build());
























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

      /*  AdView mAdView = (AdView) V.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/


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
                Toast.makeText(getActivity(), R.string.drink_added, Toast.LENGTH_SHORT).show();
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