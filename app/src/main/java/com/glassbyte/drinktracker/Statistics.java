package com.glassbyte.drinktracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by ed on 25/08/15.
 */
public class Statistics extends Activity implements FloatingActionButton.OnCheckedChangeListener {

    private FloatingActionButton infoButton;

    double avgUnits;
    double totUnits;
    double maxUnits;
    double avgTime;
    double BACAchieved;
    int daysDrinking;

    String spGender;
    LineChartView chart;

    TextView briefInfo;
    TextView rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        infoButton = (FloatingActionButton) findViewById(R.id.infoButton);
        infoButton.setOnCheckedChangeListener(this);

        briefInfo = (TextView) findViewById(R.id.briefInfo);
        rating = (TextView) findViewById(R.id.rating);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        spGender = (sp.getString(getResources().getString(R.string.pref_key_editGender),""));

        setMethods();

        //graph instantiation
        chart = (LineChartView) findViewById(R.id.chart);
        generateData();
        chart.setViewportCalculationEnabled(false);
        setViewport();
        chart.startDataAnimation();
    }

    private void setMethods(){
        //need to replace with proper methods
        setAvgUnits(0);
        setTotalUnits(0);

        //21 for men, 14 for women per week
        setMaxUnits();

        setAvgTime(0);
        setBACAchieved(0);
        setDaysDrinking(0);

        briefInfo.setText("Total units drunk this week:\n" + getTotalUnits() + "/" + getMaxUnits() + " units");

        if (getTotalUnits() <= getMaxUnits()) {
            rating.setTextColor(Color.GREEN);
            rating.setText("Below recommended weekly limit");
        }
        else if(getTotalUnits() > getMaxUnits()) {
            rating.setTextColor(Color.RED);
            rating.setText("Excessive drinking!");
        }
    }

    private void generateData(){
        List<PointValue> values = new ArrayList<>();
        values.add(new PointValue(0, 0));
        values.add(new PointValue(1, 0.1f));
        values.add(new PointValue(2, 0.2f));
        values.add(new PointValue(3, 0.3f));

        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        setAxes(data);
        chart.setLineChartData(data);
    }

    private void setAxes(LineChartData data){
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Time");
        axisY.setName("BAC");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
    }

    private void setViewport() {
        // Reset viewport height range to (0,0.5)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 0.5f;
        v.left = 0;
        v.right = 12 - 1; //assuming a random value of 12
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(FloatingActionButton fabView, final boolean isChecked) {
        switch (fabView.getId()) {
            case R.id.infoButton:
                new AlertDialog.Builder(this)
                        .setTitle("Detailed Statistics")
                        .setMessage(
                                "Total units drunk this week:\n" + getTotalUnits() + "/" + getMaxUnits() + " units" + "\n\n" +
                                        "Average units drunk per week:\n" + getAvgUnits() + "/" + getMaxUnits() + " units" + "\n\n" +
                                        "Average time spent drinking:\n" + getAvgTime() + " hours" + "\n\n" +
                                        "# of days drinking this week:\n" + getDaysDrinking() + " days" + "\n\n" +
                                        "Maximum BAC achieved this week:\n" + getBACAchieved()
                        )
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            default:
                break;
        }
    }

    private void setAvgUnits(double avgUnits) {
        this.avgUnits = avgUnits;
    }

    private double getAvgUnits() {
        return avgUnits;
    }

    private void setBACAchieved(double BACAchieved) {
        this.BACAchieved = BACAchieved;
    }

    private double getBACAchieved() {
        return BACAchieved;
    }

    private void setDaysDrinking(int daysDrinking) {
        this.daysDrinking = daysDrinking;
    }

    private int getDaysDrinking() {
        return daysDrinking;
    }

    private void setAvgTime(double avgTime) {
        this.avgTime = avgTime;
    }

    private double getAvgTime() {
        return avgTime;
    }

    private void setMaxUnits() {
        if (spGender.equals("male") || spGender.equals("Male")) {
            this.maxUnits = 21;
        }
        else{
            this.maxUnits = 14;
        }
    }

    private double getMaxUnits() {
        return maxUnits;
    }

    private void setTotalUnits(double totUnits) {
        this.totUnits = totUnits;
    }

    private double getTotalUnits() {
        return totUnits;
    }
}