package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by ed on 25/08/15.
 */
public class Statistics extends Activity {

    double totUnits, maxUnits, maxBAC, avgABV, avgVol, currBAC;
    int orange, calories;

    String spGender, spUnits, units;
    LineChartView chart;

    TextView briefInfo, rating, BACinfo, BACrating;

    BloodAlcoholContent bloodAlcoholContent;
    DrinkTrackerDbHelper drinkTrackerDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        drinkTrackerDbHelper = new DrinkTrackerDbHelper(this);

        orange = ContextCompat.getColor(this,R.color.orange500);

        briefInfo = (TextView) findViewById(R.id.briefInfo);
        rating = (TextView) findViewById(R.id.rating);
        BACinfo = (TextView) findViewById(R.id.briefInfoBAC);
        BACrating = (TextView) findViewById(R.id.ratingBAC);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        spGender = (sp.getString(getResources().getString(R.string.pref_key_editGender), ""));
        spUnits = (sp.getString(getResources().getString(R.string.pref_key_editUnits), ""));

        bloodAlcoholContent = new BloodAlcoholContent(this);

        setUpCalender();
        setMethods();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //graph instantiation
        chart = (LineChartView) findViewById(R.id.chart);
        graphValues();
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
            v.top = 0.6f;
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

    private void setMaxBAC() {

        String countQuery = "SELECT  * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME;
        SQLiteDatabase db = drinkTrackerDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            maxBAC = 0;
            do {
                currBAC = Double.parseDouble(cursor.getString(2));
                if (currBAC > maxBAC) {
                    maxBAC = currBAC;
                } else {
                    //go to next row
                    cursor.moveToNext();
            }
        } while (cursor.moveToNext());

        maxBAC = BloodAlcoholContent.round(maxBAC, 3);

        //close operations and sum
        db.close();
        cursor.close();
    }

}

    private void setUpCalender() {
        // Get calendar set to current date and time
        Calendar c = Calendar.getInstance();

        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Print start and end of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        //1 day before week start at 23:59:59
        c.add(Calendar.DATE, -1);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        String whileAfter = df.format(c.getTime());
        //1 day after week end at 00:00:00
        c.add(Calendar.DATE, 8);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        String whileBefore = df.format(c.getTime());

        //select first row by date fo start of week and sum until it reaches whileNot
        String countQuery = "SELECT  * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME;
        SQLiteDatabase db = drinkTrackerDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        String currUnits;

        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("HH:mm:ss dd/MM/yyyy");
        DateTime startDate = dateStringFormat.parseDateTime(whileAfter);
        DateTime endDate = dateStringFormat.parseDateTime(whileBefore);

        //col 1 for time
        //col 6 for units
        //sum row of col 6 if its date lies between start and end

        /*if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (dateStringFormat.parseDateTime(cursor.getString(1)).isAfter(startDate) &&
                        dateStringFormat.parseDateTime(cursor.getString(1)).isBefore(endDate)) {
                    //if date lies within period
                    currUnits = cursor.getString(6);
                    System.out.println(currUnits);
                    totUnits = totUnits + Double.parseDouble(currUnits);
                } else {
                    //go to next row
                    cursor.moveToNext();
                }
            }
            while (cursor.moveToNext() && dateStringFormat.parseDateTime(cursor.getString(1)).isBefore(endDate));

            System.out.println(totUnits);
            setTotalUnits(BloodAlcoholContent.round(totUnits, 2));

            //close operations and sum
            db.close();
            cursor.close();
        }*/
    }

    private void setMethods() {
        setUnits();
        setMaxUnits();
        setMaxBAC();
        //calories are units*7*8 as 1 unit = 8g where 1g = 7 calories therefore
        setCalories((int) (totUnits * 56));
        setAvgABV();
        setAvgVol();

        //set current
        double BAC = BloodAlcoholContent.round(bloodAlcoholContent.getCurrentEbac(), 3);
        BACinfo.setText(getResources().getString(R.string.current_BAC_level) + BAC);

        setWarning(BAC);

        //set weekly
        briefInfo.setText(getResources().getString(R.string.pollunits) +
                "\n" + getTotalUnits() + "/" + getMaxUnits() + " " +
                getResources().getString(R.string.units));

        if (getTotalUnits() <= getMaxUnits()) {
            rating.setTextColor(Color.GREEN);
            rating.setText(R.string.belowlimit);
        } else if (getTotalUnits() > getMaxUnits()) {
            rating.setTextColor(Color.RED);
            rating.setText(R.string.abovelimit);
        }
    }

    public void setWarning(double BAC){
        if (BAC >= 0 && BAC < 0.01) {
            //at 0-0.01
            BACrating.setText(R.string.tier0);
            BACrating.setTextColor(Color.GREEN);
        } else if (BAC >= 0.02 && BAC < 0.04) {
            //0.02-0.03
            BACrating.setText(R.string.tier1);
            BACrating.setTextColor(Color.GREEN);
        } else if (BAC >= 0.04 && BAC < 0.07) {
            //0.04-0.06
            BACrating.setText(R.string.tier2);
            BACrating.setTextColor(Color.GREEN);
        } else if (BAC >= 0.07 && BAC < 0.1) {
            //0.07-0.09
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

    private void setAvgABV() {
        //select first row by date fo start of week and sum until it reaches whileNot
        String countQuery = "SELECT  * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME;
        SQLiteDatabase db = drinkTrackerDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = 0;
        int totABV = 0;
        String ABV;
        float currABV;

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(4)) {
                    //if date lies within period
                    ABV = cursor.getString(4);
                    currABV = Float.parseFloat(ABV);
                    totABV = (int) (totABV + currABV);
                    count++;
                } else {
                    //go to next row
                    cursor.moveToNext();
                }
            } while (cursor.moveToNext());

            avgABV = totABV / count;

            //close operations and sum
            db.close();
            cursor.close();
        }
    }

    private void setAvgVol() {
        //select first row by date fo start of week and sum until it reaches whileNot
        String countQuery = "SELECT  * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME;
        SQLiteDatabase db = drinkTrackerDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = 0;
        int totVol = 0;
        String ABV;
        float currVol;

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(3)) {
                    //if date lies within period
                    ABV = cursor.getString(3);
                    currVol = Float.parseFloat(ABV);
                    totVol = (int) (totVol + currVol);
                    count++;
                } else {
                    //go to next row
                    cursor.moveToNext();
                }
            } while (cursor.moveToNext());

            //ml
            avgVol = totVol / count;

            //convert to oz if imperial as they are stored in ml regardless of preference
            if (getUnits().equals("oz")) {
                avgVol = BloodAlcoholContent.MetricSystemConverter.convertMillilitresToOz(avgVol);
                avgVol = BloodAlcoholContent.round(avgVol, 2);
            }

            //close operations and sum
            db.close();
            cursor.close();
        }
    }

    protected double getAvgVol() {
        return avgVol;
    }

    protected double getAvgABV() {
        return avgABV;
    }

    protected double getMaxBAC() {
        return maxBAC;
    }

    private void setMaxUnits() {
        if (spGender.equals("male") || spGender.equals("Male")) {
            this.maxUnits = 21;
        } else {
            this.maxUnits = 14;
        }
    }

    private double getMaxUnits() {
        return maxUnits;
    }

    private void setUnits() {
        if (spUnits.equals("metric") || spUnits.equals("Metric")) {
            this.units = "ml";
        } else {
            this.units = "oz";
        }
    }

    protected String getUnits() {
        return units;
    }

    private void setTotalUnits(double totUnits) {
        this.totUnits = totUnits;
    }

    private double getTotalUnits() {
        return totUnits;
    }

    private void setCalories(int calories) {
        this.calories = calories;
    }

    protected double getCalories() {
        return calories;
    }
}