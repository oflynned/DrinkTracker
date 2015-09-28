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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * Created by ed on 25/08/15.
 */
public class Statistics extends Activity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        OnDataPointTapListener {

    int orange, counter;
    double totUnits, maxUnits, BAC, maxBAC;
    long BACTime, maxTime, minTime;
    private LineGraphSeries<DataPoint> series;

    Spinner spinner;
    GraphView graph;
    TextView briefInfo, rating, BACinfo, BACrating;

    BloodAlcoholContent bloodAlcoholContent;
    DrinkTrackerDbHelper drinkTrackerDbHelper;

    ChooseDrink chooseDrink;

    String spGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        spGender = (sp.getString(getResources().getString(R.string.pref_key_editGender), ""));

        drinkTrackerDbHelper = new DrinkTrackerDbHelper(this);

        setWeeklyAmounts();
        setMaxUnits(spGender);
        setTotalUnits(totUnits);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        chooseDrink = new ChooseDrink();

        orange = ContextCompat.getColor(this, R.color.orange500);

        briefInfo = (TextView) findViewById(R.id.briefInfo);
        rating = (TextView) findViewById(R.id.rating);
        BACinfo = (TextView) findViewById(R.id.briefInfoBAC);
        BACrating = (TextView) findViewById(R.id.ratingBAC);
        graph = (GraphView) findViewById(R.id.graph);
        spinner = (Spinner) findViewById(R.id.periodSpinner);

        bloodAlcoholContent = new BloodAlcoholContent(this);
        setRecommendations();

        //ad request
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //get the selection from the spinner to display the correct time period
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {

                //Get item from spinner and store in string
                String selected = String.valueOf(parent.getSelectedItemPosition());

                switch (selected) {
                    //current
                    case "0":
                        graph.removeAllSeries();
                        series = new LineGraphSeries<>(getBACTupleCurrent());
                        graph.addSeries(series);
                        styleAxes();
                        break;
                    //weekly
                    case "1":
                        graph.removeAllSeries();
                        break;
                    //monthly
                    case "2":
                        graph.removeAllSeries();
                        break;
                }

            }

            public void onNothingSelected(AdapterView parent) {
                //Do nothing
            }
        });
    }

    private void styleAxes(){
        graph.setTitle("Alcohol Consumption");
        graph.setTitleColor(R.color.red700);
    }

    private DataPoint[] getBACTupleCurrent() {

        // select the prior time where BAC was at 0 the previous time and 
        // set this as a start point to traverse drinks and graph appropriately
        drinkTrackerDbHelper.printTableContents(DrinkTrackerDatabase.BacTable.TABLE_NAME);

        SQLiteDatabase db = drinkTrackerDbHelper.getReadableDatabase();
        String count = "SELECT count(*) FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME;
        Cursor countCursor = db.rawQuery(count, null);
        countCursor.moveToFirst();
        int mCount = countCursor.getInt(0);

        DataPoint[] values = new DataPoint[0];

        if(mCount>0){
            String countQuery =
                "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                        " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME +
                        "=(SELECT MAX(" + DrinkTrackerDatabase.BacTable.DATE_TIME + ")" +
                        " FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                        " WHERE " + DrinkTrackerDatabase.BacTable.BAC + "=0)";
            Cursor cursor = db.rawQuery(countQuery, null);
            cursor.moveToFirst();
            values = new DataPoint[cursor.getCount()];

            long lastZeroBACDate;

            maxBAC = 0;
            maxTime = 0;

            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            TimeZone timeZone = gregorianCalendar.getTimeZone();

            //given empty table

            //given the case that no drinks have been added and BAC was always 0
            if(cursor.getCount() == 0){

                countQuery = "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME;
                cursor = db.rawQuery(countQuery, null);
                cursor.moveToFirst();
                int exists = cursor.getInt(0);

                if(exists > 0){
                    minTime = cursor.getLong(1);
                    values = new DataPoint[cursor.getCount()];
                    counter = 0;

                    //continue the query as there is a populated table with decays and BAC updated
                    //as we need to return the value at a certain count, we need the counter value to offset
                    do {
                        if(counter < cursor.getCount()){
                            cursor.moveToFirst();
                            cursor.move(counter);
                            //store and return appropriate BAC within row
                            BAC = cursor.getDouble(2);
                            //get BAC and convert to an hour as a double
                            BACTime = cursor.getLong(1);

                            int xAxisValueHours = (int) (BACTime / (1000*60*60) % 24)
                                    + (timeZone.getDSTSavings() / (1000*60*60) % 24);
                            int xAxisValueMins = (int) (((BACTime / (1000*60)) % 60) / 0.6);
                            String xAxisValueConcat = Integer.parseInt(Integer.toString(xAxisValueHours))
                                    + "." + Integer.parseInt(Integer.toString(xAxisValueMins));
                            double xAxisValue = Double.parseDouble(xAxisValueConcat);

                            DataPoint v = new DataPoint(xAxisValue, BAC);
                            values[counter] = v;

                            if(BAC > maxBAC){
                                maxBAC = BAC;
                            }
                            counter++;
                        }
                    } while (!cursor.isAfterLast() && cursor.moveToNext());
                }
            }
            else if (cursor.getCount() > 0) {
                //get first value of time
                //a time of 0 is not possible, so if this exists
                //we go into the other loop and terminate
                cursor.moveToFirst();
                lastZeroBACDate = cursor.getLong(1);
                countQuery = "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                        " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME + ">" + lastZeroBACDate;
                cursor = db.rawQuery(countQuery, null);
                cursor.moveToFirst();

                values = new DataPoint[cursor.getCount()];
                counter = 0;
                minTime = cursor.getLong(1);

                //continue the query as there is a populated table with decays and BAC updated
                //as we need to return the value at a certain count, we need the counter value to offset
                do {
                    cursor.move(counter);
                    //store and return appropriate BAC within row
                    BAC = cursor.getDouble(2);
                    BACTime = cursor.getLong(1);
                    int xAxisValueHours = (int) (BACTime / (1000*60*60) % 24)
                            + (timeZone.getDSTSavings() / (1000*60*60) % 24);
                    int xAxisValueMins = (int) ((BACTime / (1000*60)) / (0.6));
                    String xAxisValueConcat = Integer.parseInt(Integer.toString(xAxisValueHours))
                            + "." + Integer.parseInt(Integer.toString(xAxisValueMins));
                    double xAxisValue = Double.parseDouble(xAxisValueConcat);

                    DataPoint v = new DataPoint(xAxisValue, BAC);
                    values[counter] = v;

                    if(BAC > maxBAC){
                        maxBAC = BAC;
                    }
                    counter++;
                } while (!cursor.isAfterLast());
            }
            maxTime = BACTime;
            db.close();
            cursor.close();
        }
        else{
            Toast.makeText(getBaseContext(),getResources().getString(R.string.add_drink),Toast.LENGTH_SHORT).show();
        }

        return values;
    }

    private void setWeeklyAmounts() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        long startOfWeek = cal.getTimeInMillis();

        // start of the next week
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        long startOfNextWeek = cal.getTimeInMillis();

        //col 1 for time
        //col 6 for units
        //sum row of col 6 if its date lies between start and end

        String countQuery = "SELECT  * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME;
        SQLiteDatabase db = drinkTrackerDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        String currUnits;
        totUnits = 0;

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (Long.parseLong(cursor.getString(1)) > startOfWeek &&
                        Long.parseLong(cursor.getString(1)) < startOfNextWeek) {
                    //if date lies within period
                    currUnits = cursor.getString(6);
                    totUnits = totUnits + Double.parseDouble(currUnits);
                } else {
                    //go to next row
                    cursor.moveToNext();
                }
            }
            while (cursor.moveToNext() && Long.parseLong(cursor.getString(1)) < startOfNextWeek);

            setTotalUnits(BloodAlcoholContent.round(totUnits, 2));

            //close operations and sum
            db.close();
            cursor.close();
        }
    }

    private void setRecommendations() {
        //set current
        double BAC = BloodAlcoholContent.round(bloodAlcoholContent.getCurrentEbac(), 3);
        BACinfo.setText(getResources().getString(R.string.current_BAC_level) + " " + BAC);

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

    public void setWarning(double BAC) {
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

    protected void setMaxUnits(String spGender) {
        if (spGender.equals("male") || spGender.equals("Male")) {
            this.maxUnits = 21;
        } else {
            this.maxUnits = 14;
        }
    }

    protected double getMaxUnits() {
        return maxUnits;
    }

    protected void setTotalUnits(double totUnits) {
        this.totUnits = totUnits;
    }

    protected double getTotalUnits() {
        return totUnits;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onTap(Series series, DataPointInterface dataPointInterface) {

    }
}