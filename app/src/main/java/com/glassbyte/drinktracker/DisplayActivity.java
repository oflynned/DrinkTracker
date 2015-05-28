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

    //Bar Graph
   /* public Intent getIntent2(Context context)
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
    }*/


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
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setDisplayChartValues(true);
        renderer.setDisplayBoundingPoints(true);
        renderer.setPointStrokeWidth(80);
        renderer.setLineWidth(20);
        renderer.setChartValuesTextSize(40);



        //enable zoom stuff
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setXTitle("day ");
        mRenderer.setYTitle("alcohol in units");
        mRenderer.setShowCustomTextGrid(true);
        mRenderer.setShowGridX(true);
        mRenderer.setShowGridY(true);
        mRenderer.setShowAxes(true);
        mRenderer.setGridColor(Color.BLUE);
        mRenderer.setAxisTitleTextSize(40);
        mRenderer.setAxesColor(Color.BLUE);
        mRenderer.setYLabelsColor(0, Color.BLUE);

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
