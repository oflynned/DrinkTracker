package com.glassbyte.drinktracker;

/**
 * Created by Alex on 29/05/15.
 *
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;



public class RealTimeActivity extends android.support.v4.app.Fragment {

    private final Handler mHandler = new Handler();

    private Runnable mTimer1;
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint>mSeries1;
    private LineGraphSeries<DataPoint>mSeries2;
    private double graph2LastXValue =5d;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_realtime, container, false);
        GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
        mSeries1=new LineGraphSeries<DataPoint>(generateData());
        graph.addSeries(mSeries1);
        return rootView;

    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach( activity);
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
    private DataPoint[] generateData()
    {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for(int i=0;i<count;i++)
        {
            double x =i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;

            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }


}
