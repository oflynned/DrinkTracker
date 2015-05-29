package com.glassbyte.drinktracker;

/**
 * Created by Alex on 29/05/15.
 *
 */

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;





public class RealTimeActivity extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_realtime, container, false);
        GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]
                {
                        new DataPoint(0, 1),
                        new DataPoint(1, 5),
                        new DataPoint(2, 3),
                        new DataPoint(3, 4),
                        new DataPoint(4, 6),
                        new DataPoint(5, 3)
                });
        graph.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[]
                {
                        new DataPoint(0, 0),
                        new DataPoint(1, 3),
                        new DataPoint(2, 2),
                        new DataPoint(3, 6),
                        new DataPoint(4, 7),
                        new DataPoint(5, 6)
                });
        graph.addSeries(series2);


        //edditing graph
        series.setTitle("foo");
        series.setThickness(20);


        series2.setTitle("bar");
        series.setThickness(10);




        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        //graph.getFitsSystemWindows();
        graph.setBackgroundColor(Color.LTGRAY);
        graph.setTitleColor(Color.BLACK);
        graph.setTitle("GRAPH TITLE");


        return rootView;
    }
}

