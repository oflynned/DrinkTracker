package com.glassbyte.drinktracker;

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

    private boolean checked;

    String mGender;
    EditText mWeight;
    EditText mHeight;
    RadioGroup mRadioGroup;
    Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        sharedPreference = new SharedPreferencesActivity();

        mWeight = (EditText) findViewById(R.id.weightET);
        mHeight = (EditText) findViewById(R.id.heightET);
        mRadioGroup = (RadioGroup) findViewById(R.id.genderGroup);
        btnContinue = (Button) findViewById(R.id.buttonContinue);

        btnContinue.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                if(mHeight.getText().toString().trim().length() == 0){
                    Toast.makeText(getBaseContext(),"Please fill in all details",Toast.LENGTH_SHORT).show();
                }

                if(mWeight.getText().toString().trim().length() == 0){
                    Toast.makeText(getBaseContext(),"Please fill in all details",Toast.LENGTH_SHORT).show();
                }

                if (mRadioGroup.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(getBaseContext(),"Please fill in all details",Toast.LENGTH_SHORT).show();
                }

                else {

                    String gender = mGender;

                    //height
                    String height = mHeight.getText().toString();
                    int fHeight = Integer.parseInt(height);

                    //weight
                    String weight = mWeight.getText().toString();
                    int fWeight = Integer.parseInt(weight);

                    String run = "true";

                    sharedPreference.save(getBaseContext(), run);
                    sharedPreference.save(getBaseContext(), gender);
                    sharedPreference.save(getBaseContext(), weight);
                    sharedPreference.save(getBaseContext(), height);

                    Toast.makeText(getBaseContext(), "SharedPreference saved: " + run, Toast.LENGTH_SHORT).show();

                    //Intent intent = new Intent(this,MainActivity.class);
                    //startActivity(intent);
                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {

        checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonMale:
                if (checked)
                    mGender = "male";
                    break;
            case R.id.radioButtonFemale:
                if (checked)
                    mGender= "female";
                    break;
        }
    }
}
