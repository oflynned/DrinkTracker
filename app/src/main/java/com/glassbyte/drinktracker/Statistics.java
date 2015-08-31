package com.glassbyte.drinktracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by ed on 25/08/15.
 */
public class Statistics extends FragmentActivity implements FloatingActionButton.OnCheckedChangeListener {

    private FloatingActionButton infoButton;
    PresetDrink presetDrink;

    double avgUnits;
    double totUnits;
    double maxUnits;
    double avgTime;
    double BACAchieved;
    int daysDrinking;

    String spGender;
    Chart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        presetDrink = new PresetDrink();

        infoButton = (FloatingActionButton) findViewById(R.id.infoButton);
        infoButton.setOnCheckedChangeListener(this);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        spGender = (sp.getString(getResources().getString(R.string.pref_key_editGender),""));

        //need to replace with proper methods
        setAvgUnits(0);
        setTotalUnits(0);

        //21 for men, 14 for women per week
        setMaxUnits();

        setAvgTime(0);
        setBACAchieved(0);
        setDaysDrinking(0);

        TextView briefInfo = (TextView) findViewById(R.id.briefInfo);
        briefInfo.setText("Total units drunk this week:\n" + getTotalUnits() + "/" + getMaxUnits() + " units");
        TextView rating = (TextView) findViewById(R.id.rating);

        if (getTotalUnits() <= getMaxUnits()) {
            rating.setTextColor(Color.GREEN);
            rating.setText("Below recommended weekly limit");
        }
        else if(getTotalUnits() > getMaxUnits()) {
            rating.setTextColor(Color.RED);
            rating.setText("Excessive drinking!");
        }

        //graph instantiation
        chart = (Chart) findViewById(R.id.chart);

        List<PointValue> values = new ArrayList<>();
        values.add(new PointValue(0, 2));
        values.add(new PointValue(1, 4));
        values.add(new PointValue(2, 3));
        values.add(new PointValue(3, 4));

        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        LineChartView chart = new LineChartView(this);
        chart.setLineChartData(data);
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