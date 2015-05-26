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
import android.widget.Toast;


public class IntroActivity extends ActionBarActivity{

    RadioButton mGender;
    EditText mWeight;
    EditText mHeight;
    Button btnContinue;
    final String firstRun = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mWeight = (EditText) findViewById(R.id.weightET);
        mHeight = (EditText) findViewById(R.id.heightET);
        btnContinue = (Button) findViewById(R.id.buttonContinue);

        btnContinue.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                Toast.makeText(getBaseContext(),"Button clicked",Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(this,AddDrinkActivity.class);
                //startActivity(intent);
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonMale:
                if (checked)
                    //male
                    break;
            case R.id.radioButtonFemale:
                if (checked)
                    //female
                    break;
        }
    }
}
