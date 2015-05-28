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
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.GraphicalView;




public class DisplayActivity{
    //Line Graph
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
    //Bar Graph
    public Intent getIntent2(Context context)
    {
        int [] y2 = {30,34,45,57,77,89,100,111,123,145};

        CategorySeries series = new CategorySeries("Bar Graph");
        for(int i=0; i<y2.length;i++)
        {
            series.add("Bar" +(i+1),y2[i]);
        }

        XYMultipleSeriesDataset dataset2 = new XYMultipleSeriesDataset();
        dataset2.addSeries(series.toXYSeries());

        XYMultipleSeriesRenderer mRenderer1 = new XYMultipleSeriesRenderer();
        XYSeriesRenderer renderer2= new XYSeriesRenderer();
        mRenderer1.addSeriesRenderer(renderer2);

        Intent intent1 = ChartFactory.getBarChartIntent(context,dataset2, mRenderer1, BarChart.Type.DEFAULT);

        return intent1;
    }


    private GraphicalView view;


    private TimeSeries dataset = new TimeSeries("Units Drank");
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYSeriesRenderer renderer =new XYSeriesRenderer();
    private  XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();



    public DisplayActivity(){
        //add data set
        mDataset.addSeries(dataset);
        //customize line 1
        renderer.setColor(Color.RED);
        renderer.setPointStyle(PointStyle.SQUARE);
        renderer.setFillPoints(true);
        //enable zoom stuff
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setXTitle("day #");
        mRenderer.setYTitle("alcohol in units");


        //add a single render to multiple render
        mRenderer.addSeriesRenderer(renderer);

    }
    public GraphicalView getView(Context context){

        view = ChartFactory.getLineChartView(context, mDataset,mRenderer);
        return view;

    }

    public void addNewPoints(Point p){

        dataset.add(p.getX(),p.getY());


    }








}
