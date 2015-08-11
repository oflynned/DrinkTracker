package com.glassbyte.drinktracker;

/**
 * Created by Alex on 29/05/15.
 *
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RealTimeActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();

    private Runnable mTimer1;
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint>mSeries1;
    private LineGraphSeries<DataPoint>mSeries2;
    private double graph2LastXValue =5d;

    String gender = "";
    String units = "";
    double weight = 0;
    double height = 0;

    private DatabaseOperationsUnits DOU;
    private Cursor CR;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);
        GraphView graph = (GraphView) findViewById(R.id.graphUnits);

        //read in presets
        SharedPreferences sp = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);

        gender = (sp.getString("genderKey", ""));
        units = (sp.getString("unitsKey", ""));
        weight = Integer.parseInt(sp.getString("weightKey", ""));
        height = Integer.parseInt(sp.getString("heightKey", ""));

        DOU = new DatabaseOperationsUnits(this);
        CR = DOU.getInfo(DOU);
        CR.moveToFirst();

        //run indefinitely with recursive updating
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                mSeries1.resetData(generateData());
                mHandler.postDelayed(this,300);
            }
        };

        mHandler.postDelayed(mTimer1, 300);

        mSeries1=new LineGraphSeries<>(generateData());
        graph.addSeries(mSeries1);

        //format label
        graph.setBottom(0);
        graph.setTop((int)0.5);

        graph.setTitle("BAC v Time");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setTextSize(25);
        graph.getLegendRenderer().setBackgroundColor(Color.argb(150, 50, 0, 0));
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));



        mSeries1.setTitle("BAC");
        mSeries1.setDrawDataPoints(true);
        mSeries1.setColor(getResources().getColor(R.color.dt_greenblue));
        mSeries1.setThickness(10);
        mSeries1.setDataPointsRadius(10);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onPause()
    {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }

    @Override
    public void onStop(){
        mHandler.removeCallbacks(mTimer1);
        super.onStop();
    }

    private DataPoint[] generateData()
    {
        int count = CR.getCount();
        DataPoint[] values = new DataPoint[count];

        for(int i=0;i<count;i++)
        {
            double x = i;

            DataPoint v = new DataPoint(x, returnY(i));

            values[i] = v;
        }
        return values;
    }

    private double returnY(int i) {
        double y = 0;

        /*
        try {
            if (!CR.isLast()) {
                CR.moveToPosition(i);
                final int h = i;
                //run indefinitely with recursive updating
                mTimer2 = new Runnable() {
                    @Override
                    public void run() {
                        CR = DOU.getInfo(DOU);
                        CR.moveToLast();

                        int H = h;

                        double BAC = CR.getInt(3);
                        double y = alcoholUnits(gender, units, 10, BAC, H);

                        if (y != 0) {
                            DOU.putInfo(
                                    DOU,
                                    DOU.getDateTime(), //time
                                    20, //units of alcohol
                                    0.4, //percentage
                                    y //bac
                            );

                            CR.moveToNext(); //increment table
                            CR.close();
                            mHandler.postDelayed(this, 5000);
                            mHandler.postDelayed(mTimer2, 5000);
                        }
                    }
                };
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        CR.close();
        mHandler.postDelayed(mTimer2, 5000);
        */

        //the memory leak seems to be occurring here
        //ie it's trying to retrieve rows that don't exist
        //I'll refactor this when I'm not up the walls if you want
        return y;
    }


    private double alcoholUnits(String gender, String units, double weight, double amount, int H){
        double oz = 29.5735;
        double pound = 2.20462;
        double unit = 10; //ml
        double r = 0; //coefficient for gender
        double amountDrunk =0;

        switch(gender){
            case "Male":
                r = 0.73;
                break;
            case "Female":
                r = 0.66;
                break;
        }

        switch(units){
            case "Metric":
                amountDrunk = (((amount/oz)*5.14)/((weight/pound)*r))-0.15*(H/3600);
                break;
            case "Imperial":
                amountDrunk = ((amount*5.14)/(weight*r))-0.15*(H/3600);
                break;
        }
        return amountDrunk;
    }
}

