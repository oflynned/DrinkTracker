package com.glassbyte.drinktracker;

/**
 * Created by ed on 25/05/15.
 * Edited by Alex on 25/05/15
 */
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

import java.lang.reflect.Array;
import java.util.Arrays;


public class GraphsActivity extends Activity{
//called when activity is first created

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

    }
    public void lineGraphHandler(View view) {
        DisplayActivity line =new DisplayActivity();
        Intent lineIntent = line.getIntent(this);
        startActivity(lineIntent);

    }
    public void barGraphHandler(View view){

    }
    public void pieGraphHandler(View view){

    }
    public void scatterGraphHandler(View view){

    }




}
