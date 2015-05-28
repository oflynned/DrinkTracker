package com.glassbyte.drinktracker;

/**
 * Created by Alex on 26/05/15.
 *
 */
import android.app.Activity;
import android.database.Cursor;
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



        //Customize Graph Options
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setShowGridX(true);
        mRenderer.setShowGridY(true);
        mRenderer.setShowAxes(true);
        mRenderer.setShowCustomTextGrid(true);
        mRenderer.setXTitle("Day ");
        mRenderer.setYTitle("Alcohol in units");
        mRenderer.setAxesColor(Color.BLUE);
        mRenderer.setLabelsColor(Color.BLUE);
        mRenderer.setLabelsTextSize(40);
        mRenderer.setGridColor(Color.BLUE);
        mRenderer.setAxisTitleTextSize(40);
        mRenderer.setYLabelsColor(0, Color.BLUE);
        mRenderer.setXLabelsColor(Color.BLUE);

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
