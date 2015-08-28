package com.glassbyte.drinktracker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class IntroActivity extends Activity {

    private boolean checkedG;
    private boolean checkedUM;

    String mGender;
    String mUM;

    private EditText mWeight, mHeight;
    private RadioGroup mRadioGroup, mUnitsMeasurement;
    private Button btnContinue;
    private Activity thisActivity;
    private ViewGroup unitSystemAffectedViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        //unitSystemAffectedViews = (ViewGroup)findViewById(R.id.unit_system_based_views);


        thisActivity = this;

        mWeight = (EditText) findViewById(R.id.weightET);
        mHeight = (EditText) findViewById(R.id.heightET);
        mRadioGroup = (RadioGroup) findViewById(R.id.genderGroup);
        mUnitsMeasurement = (RadioGroup) findViewById(R.id.unitsMeasurement);

        btnContinue = (Button) findViewById(R.id.buttonContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (mHeight.getText().toString().trim().length() == 0) {
                    Toast.makeText(getBaseContext(), "Please fill in height", Toast.LENGTH_SHORT).show();
                } else if (mWeight.getText().toString().trim().length() == 0) {
                    Toast.makeText(getBaseContext(), "Please fill in weight", Toast.LENGTH_SHORT).show();
                } else if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getBaseContext(), "Please fill in gender", Toast.LENGTH_SHORT).show();
                } else if (mUnitsMeasurement.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getBaseContext(), "Please fill in units", Toast.LENGTH_SHORT).show();
                } else {
                    //height
                    String height = mHeight.getText().toString();
                    int fHeight = Integer.parseInt(height);

                    //weight
                    String weight = mWeight.getText().toString();
                    int fWeight = Integer.parseInt(weight);

                    String gender = mGender;

                    String um = mUM;

                    //store in shared preferences
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(thisActivity);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString(getResources().getString(R.string.pref_key_run), "true");
                    editor.putString(getResources().getString(R.string.pref_key_editGender), gender);
                    editor.putString(getResources().getString(R.string.pref_key_editHeight), height);
                    editor.putString(getResources().getString(R.string.pref_key_editWeight), weight);
                    editor.putString(getResources().getString(R.string.pref_key_editUnits), um);
                    editor.putFloat(getResources().getString(R.string.pref_key_currentEbac), 0);
                    editor.apply();

                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    startActivity(intent);
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
