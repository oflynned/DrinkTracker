package com.glassbyte.drinktracker;

/**
 * Created by Alex on 29/05/15.
 *
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
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

public class RealTimeActivity extends android.support.v4.app.Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_realtime, container, false);
        GraphView graph = (GraphView) rootView.findViewById(R.id.graphUnits);
        mSeries1=new LineGraphSeries<>(generateData());
        graph.addSeries(mSeries1);

        graph.setTitle("BAC v Time");
        mSeries1.setTitle("BAC");
        mSeries1.setDrawDataPoints(true);
        mSeries1.setColor(getResources().getColor(R.color.dt_greenblue));

        //read in presets
        SharedPreferences spGender = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences spUnits = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences spWeight = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences spHeight = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);

        gender = (spGender.getString("genderKey", ""));
        units = (spUnits.getString("unitsKey", ""));
        weight = Integer.parseInt(spWeight.getString("weightKey", ""));
        height = Integer.parseInt(spHeight.getString("heightKey", ""));

        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach( activity);
        DOU = new DatabaseOperationsUnits(getActivity());
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

    }
    @Override
    public void onPause()
    {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }

    @Override
    public void onDetach(){
        mHandler.removeCallbacks(mTimer1);
        super.onDetach();
    }

    private DataPoint[] generateData()
    {
        int count = CR.getCount();
        DataPoint[] values = new DataPoint[count];

        for(int i=0;i<count;i++)
        {
            double x = i;

            DataPoint v = new DataPoint(x, returnY(i));;

            values[i] = v;
        }
        return values;
    }

    private double returnY(int i){
        double y=0;
        CR = DOU.getInfo(DOU);
        try {
            if(!CR.isLast()){
                CR.moveToPosition(i);
                //y = CR.getInt(3); //set to current bac at pos i at getInt(3)
                y = alcoholUnits(gender,units,weight,CR.getInt(1));
            }
        }
        catch (Exception ex){
            y = 0;
            ex.getStackTrace();
            Log.i("error","error in CR");
        }
        finally {
            CR.close();
        }
        return y;
    }

    private long timeDiff() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.UK);
        Date time1 = null;
        Date time2 = null;

        if(CR.isFirst()){
            CR.moveToFirst();
            try {
                time1 = formatter.parse(CR.getString(0));
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                CR.close();
            }
            CR.moveToNext();
            try {
                time2 = formatter.parse(CR.getString(0));
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                CR.close();
            }
        }
        else {
            CR.moveToPrevious();
            try {
                time1 = formatter.parse(CR.getString(0));
                Toast.makeText(getActivity(),String.valueOf(time1),Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                CR.close();
            }

            CR.moveToNext();
            try {
                time2 = formatter.parse(CR.getString(0));
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                CR.close();
            }
        }
        if(time1 == null || time2 == null){
            return 0;
        }
        return ((time2.getTime()/60000) - (time1.getTime()/60000));
    }

    private double alcoholUnits(String gender, String units, double weight, double amount){
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
                amountDrunk = (((amount/oz)*5.14)/((weight/pound)*r))-0.15*timeDiff();
                break;
            case "Imperial":
                amountDrunk = ((amount*5.14)/(weight*r))-0.15*timeDiff();
                break;
        }
        return amountDrunk;
    }
}

