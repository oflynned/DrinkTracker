package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity {
    private SharedPreferencesActivity sharedPreference;
    private String run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //retrieve data from sharedreferences for initial run setup
        sharedPreference = new SharedPreferencesActivity();
        run = sharedPreference.getValue(getBaseContext());
        DatabaseOperationsUnits DOU = new DatabaseOperationsUnits(getBaseContext());
        Boolean exists;
        Cursor CR = DOU.getInfo(DOU);

        //sample database logging for units
        for(int i = 0; i < 30; i++) {
                CR.moveToLast();
                DOU.putInfo(
                        DOU,
                        i, //units of alcohol
                        DOU.getDateTime(), //start time
                        DOU.getDateTime(), //end time
                        "1" //duration
                );
                CR.moveToNext();

        }

        if (run == "" || run == "true") {
            Toast.makeText(getBaseContext(),"first run being executed",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
        else if (run.equals("true")){
            Toast.makeText(getBaseContext(),"first run already executed",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, AddDrinkActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
