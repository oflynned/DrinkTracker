package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Random;


public class IntroActivity extends Activity {

    private boolean checkedG;
    private boolean checkedUM;

    String mGender;
    String mUM;

    EditText mWeight;
    EditText mHeight;
    private RadioGroup mRadioGroup;
    private RadioGroup mUnitsMeasurement;
    Button btnContinue;

    public static final String Prefs = "Settings";
    public static final String Run = "runKey";
    public static final String Gender = "genderKey";
    public static final String Height = "heightKey";
    public static final String Weight = "weightKey";
    public static final String Units = "unitsKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mWeight = (EditText) findViewById(R.id.weightET);
        mHeight = (EditText) findViewById(R.id.heightET);
        mRadioGroup = (RadioGroup) findViewById(R.id.genderGroup);
        mUnitsMeasurement = (RadioGroup) findViewById(R.id.unitsMeasurement);

        btnContinue = (Button) findViewById(R.id.buttonContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (mHeight.getText().toString().trim().length() == 0) {
                    Toast.makeText(getBaseContext(), "Please fill in height", Toast.LENGTH_SHORT).show();
                }

                if (mWeight.getText().toString().trim().length() == 0) {
                    Toast.makeText(getBaseContext(), "Please fill in weight", Toast.LENGTH_SHORT).show();
                }

                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getBaseContext(), "Please fill in gender", Toast.LENGTH_SHORT).show();
                }

                if (mUnitsMeasurement.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getBaseContext(), "Please fill in units", Toast.LENGTH_SHORT).show();
                } else {
                    //height
                    String height = mHeight.getText().toString();
                    int fHeight = Integer.parseInt(height);

                    //weight
                    String weight = mWeight.getText().toString();
                    int fWeight = Integer.parseInt(weight);

                    String gender = mGender;
                    Toast.makeText(getBaseContext(), gender, Toast.LENGTH_SHORT).show();

                    String um = mUM;
                    Toast.makeText(getBaseContext(), um, Toast.LENGTH_SHORT).show();

                    //store in shared preferences
                    SharedPreferences sp = getSharedPreferences(Prefs, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString(Run, "true");
                    editor.putString(Gender, gender);
                    editor.putString(Height, height);
                    editor.putString(Weight, weight);
                    editor.putString(Units, um);
                    editor.apply();

                    //add items to database
                    DatabaseOperationsUnits DOU = new DatabaseOperationsUnits(getBaseContext());
                    Cursor CR = DOU.getInfo(DOU);

                    //sample database logging for units
                    for(int i = 0; i < 31; i++) {
                        CR.moveToLast();
                        DOU.putInfo(
                                DOU,
                                Integer.toString(i), //DOU.getDateTime(), //time
                                (float) Math.pow(i, 2), //units of alcohol
                                i, //percentage
                                i //bac
                        );
                        CR.moveToNext(); //increment table
                    }
                    //move to first for test read in values to graph
                    CR.moveToFirst();

                    Intent intent = new Intent(v.getContext(), AddDrinkActivity.class);
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
        switch (view.getId()) {
            case R.id.metric:
                if (checkedUM)
                    mUM = "metric";
                break;
            case R.id.imperial:
                if (checkedUM)
                    mUM = "imperial";
                break;
        }
    }
}
