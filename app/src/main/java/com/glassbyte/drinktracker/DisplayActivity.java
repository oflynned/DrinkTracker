package com.glassbyte.drinktracker;

/**
 * Created by Alex on 26/05/15.
 *
 */
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.content.Intent;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class DisplayActivity{

    public Intent getIntent(Context context)
    {
        int [] x = {1,2,3,4,5,6,7,8,9,10};
        int [] y = {30,34,45,57,77,89,100,111,123,145};

        TimeSeries series = new TimeSeries("Drink");
        for(int i=0; i<x.length;i++)
        {
            series.add(x[i],y[i]);
        }

        int [] x1 = {11,12,13,14,15,16,17,18,19,20};
        int [] y1 = {145,130,115,100,85,65,45,30,15,0};

        TimeSeries series1 = new TimeSeries("Sobriety");
        for(int i=0; i<x1.length;i++)
        {
            series.add(x1[i],y1[i]);
        }






        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);
        //XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
        dataset.addSeries(series1);





        //Drunk level line
        XYSeriesRenderer renderer =new XYSeriesRenderer();
        renderer.setColor(Color.RED);
        renderer.setLineWidth(50);
        renderer.setPointStyle(PointStyle.SQUARE);
        renderer.setFillPoints(true);
        renderer.setDisplayChartValues(true);
        //renderer.setChartValuesTextSize(60);

        //Sobriety line
        XYSeriesRenderer renderer1 =new XYSeriesRenderer();
        renderer1.setColor(Color.GREEN);
        renderer1.setLineWidth(50);
        renderer1.setPointStyle(PointStyle.SQUARE);
        renderer1.setFillPoints(true);
        renderer1.setDisplayChartValues(true);
       // renderer1.setChartValuesTextSize(60);






        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

        mRenderer.addSeriesRenderer(renderer);
        mRenderer.addSeriesRenderer(renderer1);

        mRenderer.setChartTitle("Units of Alcohol Drank");
        mRenderer.setChartValuesTextSize(50);
        mRenderer.addXTextLabel(50, "Time");
        mRenderer.setAxesColor(Color.BLUE);



        Intent intent = ChartFactory.getLineChartIntent(context,dataset,mRenderer,"LINE GRAPH TITLE");
        return intent;
    }

}
