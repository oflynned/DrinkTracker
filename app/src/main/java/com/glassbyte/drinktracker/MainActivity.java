package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private SharedPreferencesActivity sharedPreference;
    private String run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreference = new SharedPreferencesActivity();
        run = sharedPreference.getValue(getBaseContext());
        Toast.makeText(this,run,Toast.LENGTH_SHORT).show();

        if (run == "") {
            Toast.makeText(getBaseContext(),"first run being executed",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
        else if (run == null) {
            Toast.makeText(getBaseContext(),"first run being executed",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
        else if (run.equals("true")){
            Toast.makeText(getBaseContext(),"first run already executed",Toast.LENGTH_SHORT).show();

            //Intent intent = new Intent(this, AddDrinkActivity.class);
            //startActivity(intent);
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
