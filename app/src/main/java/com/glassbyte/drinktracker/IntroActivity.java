package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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


public class IntroActivity extends ActionBarActivity{
    SharedPreferencesActivity sharedPreference;

    private boolean checkedG;
    private boolean checkedUM;

    String mGender;
    String mUM;

    EditText mWeight;
    EditText mHeight;
    private RadioGroup mRadioGroup;
    private RadioGroup mUnitsMeasurement;
    Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        sharedPreference = new SharedPreferencesActivity();

        mWeight = (EditText) findViewById(R.id.weightET);
        mHeight = (EditText) findViewById(R.id.heightET);
        mRadioGroup = (RadioGroup) findViewById(R.id.genderGroup);
        mUnitsMeasurement = (RadioGroup) findViewById(R.id.unitsMeasurement);

        btnContinue = (Button) findViewById(R.id.buttonContinue);

        btnContinue.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                if(mHeight.getText().toString().trim().length() == 0){
                    Toast.makeText(getBaseContext(),"Please fill in height",Toast.LENGTH_SHORT).show();
                }

                if(mWeight.getText().toString().trim().length() == 0){
                    Toast.makeText(getBaseContext(),"Please fill in weight",Toast.LENGTH_SHORT).show();
                }

                if (mRadioGroup.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(getBaseContext(),"Please fill in gender",Toast.LENGTH_SHORT).show();
                }

                if (mUnitsMeasurement.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(getBaseContext(),"Please fill in units",Toast.LENGTH_SHORT).show();
                }

                else {
                    //height
                    String height = mHeight.getText().toString();
                    int fHeight = Integer.parseInt(height);

                    //weight
                    String weight = mWeight.getText().toString();
                    int fWeight = Integer.parseInt(weight);

                    String gender = mGender;
                    Toast.makeText(getBaseContext(),gender,Toast.LENGTH_SHORT).show();

                    String um = mUM;
                    Toast.makeText(getBaseContext(),um,Toast.LENGTH_SHORT).show();

                    String run = "true";

                    sharedPreference.save(getBaseContext(), run);

                    //setup database inputs
                    DatabaseOperations DO = new DatabaseOperations(getBaseContext()); //pass context to class

                    //insert this data into the table
                    DO.putInfo(DO, fHeight, fWeight, gender, um);
                    Toast.makeText(getBaseContext(),"Details successfully inputted",Toast.LENGTH_LONG).show();
                    DO.close();
                    finish(); //end activity

                    Toast.makeText(getBaseContext(), "SharedPreference saved: " + run, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(v.getContext(), AddDrinkActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void onRadioButtonClickedGender(View view) {

        checkedG = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonMale:
                if (checkedG)
                    mGender = "male";
                    break;
            case R.id.radioButtonFemale:
                if (checkedG)
                    mGender= "female";
                    break;
        }
    }

    public void onRadioButtonClickedUnits(View view) {

        checkedUM = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
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
