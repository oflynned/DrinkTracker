package com.glassbyte.drinktracker;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
public class Statistics extends Activity {

    int orange;

    LineChartView chart;

    TextView briefInfo, rating, BACinfo, BACrating;

    BloodAlcoholContent bloodAlcoholContent;
    DrinkTrackerDbHelper drinkTrackerDbHelper;

    ChooseDrink chooseDrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        chooseDrink = new ChooseDrink();

        orange = ContextCompat.getColor(this, R.color.orange500);

        briefInfo = (TextView) findViewById(R.id.briefInfo);
        rating = (TextView) findViewById(R.id.rating);
        BACinfo = (TextView) findViewById(R.id.briefInfoBAC);
        BACrating = (TextView) findViewById(R.id.ratingBAC);

        bloodAlcoholContent = new BloodAlcoholContent(this);
        setMethods();

        //ad request
        /*
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        */

        //graph instantiation
        chart = (LineChartView) findViewById(R.id.chart);
        //graphValues();
        chart.setViewportCalculationEnabled(false);
        chart.startDataAnimation();
    }

    private void graphValues() {
        //select first row by date fo start of week and sum until it reaches whileNot
        String countQuery = "SELECT  * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME;
        SQLiteDatabase db = drinkTrackerDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        String bac;
        float bacPoint;
        int count = 0;
        List<PointValue> values = new ArrayList<>();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(2)) {
                    //if date lies within period
                    bac = cursor.getString(2);
                    bacPoint = Float.parseFloat(bac);
                    values.add(new PointValue(count, bacPoint));
                    count++;
                } else {
                    //go to next row
                    cursor.moveToNext();
                }
            } while (cursor.moveToNext());

            Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
            List<Line> lines = new ArrayList<>();
            lines.add(line);

            LineChartData data = new LineChartData();
            data.setLines(lines);
            setAxes(data);
            chart.setLineChartData(data);

            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = 0;
            v.top = 0.4f;
            v.left = 0;
            v.right = cursor.getCount();
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);
            chart.setScrollX(1);

            //close operations and sum
            db.close();
            cursor.close();
        }
    }

    private void setAxes(LineChartData data) {
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName(getResources().getString(R.string.time));
        axisY.setName(getResources().getString(R.string.BAC));
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
    }

    private void setMethods() {
        //set current
        double BAC = BloodAlcoholContent.round(bloodAlcoholContent.getCurrentEbac(), 3);
        BACinfo.setText(getResources().getString(R.string.current_BAC_level) + " " + BAC);

        setWarning(BAC);

        //set weekly
        briefInfo.setText(getResources().getString(R.string.pollunits) +
                "\n" + chooseDrink.getTotalUnits() + "/" + chooseDrink.getMaxUnits() + " " +
                getResources().getString(R.string.units));

        if (chooseDrink.getTotalUnits() <= chooseDrink.getMaxUnits()) {
            rating.setTextColor(Color.GREEN);
            rating.setText(R.string.belowlimit);
        } else if (chooseDrink.getTotalUnits() > chooseDrink.getMaxUnits()) {
            rating.setTextColor(Color.RED);
            rating.setText(R.string.abovelimit);
        }
    }

    public void setWarning(double BAC){
        if (BAC >= 0 && BAC < 0.01) {
            //at 0-0.01
            BACrating.setText(R.string.tier0);
            BACrating.setTextColor(Color.GREEN);
        } else if (BAC >= 0.01 && BAC < 0.04) {
            //0.01-0.04
            BACrating.setText(R.string.tier1);
            BACrating.setTextColor(Color.GREEN);
        } else if (BAC >= 0.04 && BAC < 0.07) {
            //0.04-0.06
            BACrating.setText(R.string.tier2);
            BACrating.setTextColor(Color.GREEN);
        } else if (BAC >= 0.07 && BAC < 0.1) {
            //0.07-0.1
            BACrating.setText(R.string.tier3);
            BACrating.setTextColor(orange);
        } else if (BAC >= 0.1 && BAC < 0.13) {
            //0.1-0.129
            BACrating.setText(R.string.tier4);
            BACrating.setTextColor(orange);
        } else if (BAC >= 0.13 && BAC < 0.16) {
            //0.13-0.15
            BACrating.setText(R.string.tier5);
            BACrating.setTextColor(orange);
        } else if (BAC >= 0.16 && BAC < 0.2) {
            //0.16-0.19
            BACrating.setText(R.string.tier6);
            BACrating.setTextColor(orange);
        } else if (BAC >= 0.2 && BAC < 0.25) {
            //0.2-0.24
            BACrating.setText(R.string.tier7);
            BACrating.setTextColor(Color.RED);
        } else if (BAC >= 0.25 && BAC < 0.3) {
            //0.25-0.29
            BACrating.setText(R.string.tier8);
            BACrating.setTextColor(Color.RED);
        } else if (BAC >= 0.3 && BAC < 0.35) {
            //0.3-0.34
            BACrating.setText(R.string.tier9);
            BACrating.setTextColor(Color.RED);
        } else if (BAC >= 0.35 && BAC < 0.4) {
            //0.35-0.39
            BACrating.setText(R.string.tier10);
            BACrating.setTextColor(Color.RED);
        } else {
            //0.4+
            BACrating.setText(R.string.tier11);
            BACrating.setTextColor(Color.RED);
        }
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
}