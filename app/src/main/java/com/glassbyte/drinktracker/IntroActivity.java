package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class IntroActivity extends Activity {

    private boolean checkedG, checkedUM;

    String mGender, mUM;

    private EditText mWeight, cmHeight, feetHeight, inchesHeight;
    private RadioGroup mRadioGroup, mUnitsMeasurement;
    private Button btnContinue;
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        thisActivity = this;

        cmHeight = (EditText)findViewById(R.id.heightET);
        feetHeight = (EditText)findViewById(R.id.feetHeightET);
        inchesHeight = (EditText)findViewById(R.id.inchesHeightET);
        mWeight = (EditText)findViewById(R.id.weightET);
        mRadioGroup = (RadioGroup)findViewById(R.id.genderGroup);
        mUnitsMeasurement = (RadioGroup)findViewById(R.id.unitsMeasurement);

        btnContinue = (Button) findViewById(R.id.buttonContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String weight = mWeight.getText().toString().trim();

                if (weight.length() == 0) {
                    Toast.makeText(getBaseContext(), "Please fill in weight", Toast.LENGTH_SHORT).show();
                } else if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getBaseContext(), "Please fill in gender", Toast.LENGTH_SHORT).show();
                } else if (mUnitsMeasurement.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getBaseContext(), "Please fill in units", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(thisActivity);
                    SharedPreferences.Editor editor = sp.edit();

                    String gender = mGender;
                    String um = mUM;

                    //store in shared preferences based on set unit system
                    if (um.equalsIgnoreCase("metric")) {
                        String height = cmHeight.getText().toString().trim();
                        if (height.length() > 0) {
                            /**/
                            editor.putString(getResources().getString(R.string.pref_key_run), "true");
                            editor.putString(getResources().getString(R.string.pref_key_editGender), gender);
                            editor.putString(getResources().getString(R.string.pref_key_editHeight), height);
                            editor.putString(getResources().getString(R.string.pref_key_editWeight), weight);
                            editor.putString(getResources().getString(R.string.pref_key_editUnits), um);
                            editor.putFloat(getResources().getString(R.string.pref_key_currentEbac), 0);
                            editor.putInt(getString(R.string.pref_key_last_update_currentEbac), (int)System.currentTimeMillis());
                            editor.apply();

                            Intent intent = new Intent(v.getContext(), SwipeIntro.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getBaseContext(), "Please fill in height", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //imperial system
                        String fHeight = feetHeight.getText().toString().trim();
                        String iHeight = inchesHeight.getText().toString().trim();

                        if (fHeight.length() == 0) {
                            Toast.makeText(getBaseContext(), "Please fill in height (feet)", Toast.LENGTH_SHORT).show();
                        } else if (iHeight.length() == 0) {
                            Toast.makeText(getBaseContext(), "Please fill in height (inches)", Toast.LENGTH_SHORT).show();
                        } else {
                            int kgWeight = (int)BloodAlcoholContent.round(
                                    BloodAlcoholContent.MetricSystemConverter.convertPoundsToKilograms(
                                            Integer.parseInt(weight)
                                    ),0);

                            double[] feetAndInches = new double[2];
                            feetAndInches[0] = Double.parseDouble(fHeight);
                            feetAndInches[1] = Double.parseDouble(iHeight);

                            int cmHeight = (int)BloodAlcoholContent.round(
                                    BloodAlcoholContent.MetricSystemConverter.convertFeetAndInchesToCm(
                                            feetAndInches),0);

                            editor.putString(getResources().getString(R.string.pref_key_run), "true");
                            editor.putString(getResources().getString(R.string.pref_key_editGender), gender);
                            editor.putString(getResources().getString(R.string.pref_key_editHeight), String.valueOf(cmHeight));
                            editor.putString(getResources().getString(R.string.pref_key_editWeight), String.valueOf(kgWeight));
                            editor.putString(getResources().getString(R.string.pref_key_editUnits), um);
                            editor.putFloat(getResources().getString(R.string.pref_key_currentEbac), 0);
                            editor.putInt(getString(R.string.pref_key_last_update_currentEbac), (int) System.currentTimeMillis());
                            editor.apply();

                            Intent intent = new Intent(v.getContext(), SwipeIntro.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    public void onRadioButtonClickedGender(View view) {

        checkedG = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButtonMale:
                if (checkedG)
                    mGender = "male";
                break;
            case R.id.radioButtonFemale:
                if (checkedG)
                    mGender = "female";
                break;
        }
    }

    public void onRadioButtonClickedUnits(View view) {

        checkedUM = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        if(checkedUM) {

            TextView weightUnitT = (TextView)thisActivity.findViewById(R.id.weightUnitT);
            TextView cmT = (TextView)thisActivity.findViewById(R.id.cmT);
            TextView feetT = (TextView)thisActivity.findViewById(R.id.feetHeightT);
            TextView inchesT = (TextView)thisActivity.findViewById(R.id.inchesHeightT);
            EditText heightET = (EditText)thisActivity.findViewById(R.id.heightET);
            EditText feetET = (EditText)thisActivity.findViewById(R.id.feetHeightET);
            EditText inchesET = (EditText)thisActivity.findViewById(R.id.inchesHeightET);

            switch (view.getId()) {
                case R.id.metric:
                    mUM = "metric";

                    weightUnitT.setText("kg");
                    weightUnitT.invalidate();

                    feetT.setVisibility(View.INVISIBLE);
                    feetT.invalidate();

                    feetET.setVisibility(View.INVISIBLE);
                    feetET.invalidate();

                    inchesT.setVisibility(View.INVISIBLE);
                    inchesT.invalidate();

                    inchesET.setVisibility(View.INVISIBLE);
                    inchesET.invalidate();

                    heightET.setVisibility(View.VISIBLE);
                    heightET.invalidate();

                    cmT.setVisibility(View.VISIBLE);
                    cmT.invalidate();
                    break;
                case R.id.imperial:
                    mUM = "imperial";
                    weightUnitT.setText("pounds");
                    weightUnitT.invalidate();

                    feetT.setVisibility(View.VISIBLE);
                    feetT.invalidate();

                    feetET.setVisibility(View.VISIBLE);
                    feetET.invalidate();

                    inchesT.setVisibility(View.VISIBLE);
                    inchesT.invalidate();

                    inchesET.setVisibility(View.VISIBLE);
                    inchesET.invalidate();

                    heightET.setVisibility(View.INVISIBLE);
                    heightET.invalidate();

                    cmT.setVisibility(View.INVISIBLE);
                    cmT.invalidate();
                    break;
            }
        }
    }
}
