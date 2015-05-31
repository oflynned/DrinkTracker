package com.glassbyte.drinktracker;

/**
 * Created by Alex on 29/05/15.
 *
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class RealTimeActivity extends android.support.v4.app.Fragment {

    private final Handler mHandler = new Handler();

    private Runnable mTimer1;
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint>mSeries1;
    private LineGraphSeries<DataPoint>mSeries2;
    private double graph2LastXValue =5d;

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
            //double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            //double f = mRand.nextDouble()*0.15+0.3;

            DataPoint v = new DataPoint(x, returnY(i));

            //Log.i("gen data","value returned: "+returnY(i));

            values[i] = v;
        }
        return values;
    }

    private double returnY(int i){
        int y=0;
        CR = DOU.getInfo(DOU);
        try {
            if(!CR.isLast()){
                CR.moveToPosition(i);
                y = CR.getInt(3); //set to current bac at pos i at getInt(3)
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
}

