package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.style.DashAnimation;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by ed on 25/08/15.
 */
public class Statistics extends Activity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    Locale locale;
    String language;

    private LineChartView lineChartView;
    //we'll use an arraylist to dynamically set n amounts of values
    //and then copy this list to an array of size n when onCreate() is called
    private ArrayList<Float> BACLevelArray = new ArrayList<>();
    private ArrayList<Time> BACTimeArray = new ArrayList<>();
    private float[] BACvalues = null;
    private String[] BACtime = null;

    float[] yValues = null;
    String[] xValues = null;

    int orange, counter;
    double totUnits, maxUnits, BAC, maxBAC;

    //Spinner spinner;
    TextView briefInfo, rating, BACinfo, BACrating;

    BloodAlcoholContent bloodAlcoholContent;
    DrinkTrackerDbHelper drinkTrackerDbHelper;

    ChooseDrink chooseDrink;
    String spGender;

    double maxYValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //each vertical line depends on the array not being null
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        spGender = (sp.getString(getResources().getString(R.string.pref_key_editGender), ""));

        //set irish if the checkbox is checked in settings
        boolean irish = false;
        boolean nds = false;
        boolean irishChosen = sp.getBoolean(getResources().getString(R.string.pref_key_irish), irish);
        boolean ndsChosen = sp.getBoolean(getResources().getString(R.string.pref_key_nds), nds);

        if (irishChosen) {
            setLocale("ga");
        } else if (ndsChosen) {
            setLocale("nds");
        }

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

        bloodAlcoholContent = new BloodAlcoholContent(this);
        setRecommendations();

        //ad request
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //chart
        lineChartView = (LineChartView) findViewById(R.id.linechart);
        showChart(lineChartView);
    }

    private void getBACTupleCurrent() {
        // select the prior time where BAC was at 0 the previous time and
        // set this as a start point to traverse drinks and graph appropriately
        drinkTrackerDbHelper.printTableContents(DrinkTrackerDatabase.BacTable.TABLE_NAME);

        SQLiteDatabase db = drinkTrackerDbHelper.getReadableDatabase();
        String count = "SELECT count(*) FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME;
        Cursor countCursor = db.rawQuery(count, null);
        countCursor.moveToFirst();
        int mCount = countCursor.getInt(0);

        //if the table exists
        if (mCount > 0) {
            String countQuery =
                    "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                            " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME +
                            "=(SELECT MAX(" + DrinkTrackerDatabase.BacTable.DATE_TIME + ")" +
                            " FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                            " WHERE " + DrinkTrackerDatabase.BacTable.BAC + "=0)";
            Cursor cursor = db.rawQuery(countQuery, null);
            cursor.moveToFirst();
            long lastZeroBACDate;

            //given the case that no drinks have been added and BAC was always 0
            if (cursor.getCount() == 0) {

                countQuery = "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME;
                cursor = db.rawQuery(countQuery, null);
                cursor.moveToFirst();
                int exists = cursor.getInt(0);

                //given empty table where drinks have now been added
                if (exists > 0) {
                    //continue the query as there is a populated table with decays and BAC updated
                    //as we need to return the value at a certain count, we need the counter value to offset
                    cursor.moveToFirst();
                    lastZeroBACDate = cursor.getLong(1);
                    countQuery = "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                            " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME + ">" + lastZeroBACDate;
                    cursor = db.rawQuery(countQuery, null);
                    cursor.moveToFirst();

                    //initialise array to size of arraylist over 2 rows
                    //where one row is bac value and the other is the date time value
                    counter = cursor.getCount() + 1;
                    BACvalues = new float[counter];
                    BACtime = new String[counter];

                    //continue the query as there is a populated table with decays and BAC updated
                    //as we need to return the value at a certain count, we need the counter value to offset
                    while (!cursor.isAfterLast() && cursor.moveToNext()) {
                        //store and return appropriate BAC & time within row
                        Time time = new Time(cursor.getLong(1));
                        BACTimeArray.add(time);
                        //traverse and return the value of the current float of the row
                        BACLevelArray.add(cursor.getFloat(2) * 40);

                        if (BAC > maxBAC) {
                            maxBAC = BAC;
                        }
                        cursor.moveToNext();
                    }
                }
                //initial value has to be set to BAC of 0 at the time of first drink added...
                BACvalues = new float[1];
                BACtime = new String[1];

                if(BACLevelArray.size() > 1) {
                    BACvalues[0] = 0;
                    BACtime[0] = String.valueOf(returnFirstNullValue());
                } else {
                    BACvalues[0] = 0;
                    BACtime[0] = "";
                }

                float[] tempLevel = new float[BACLevelArray.size()];
                String[] tempTime = new String[BACTimeArray.size()];

                for (int i = 0; i < tempLevel.length; i++) {
                    tempLevel[i] = BACLevelArray.get(i);
                    tempTime[i] = String.valueOf(BACTimeArray.get(i));
                }

                //now concatenate both initial and raw arrays
                yValues = concatenateFloats(BACvalues, tempLevel);
                xValues = concatenateStrings(BACtime, tempTime);

                xValues[0] = "";

                for(int i = 2; i < xValues.length - 1; i++) {
                    xValues[i] = "";
                }
            }
            //else if the table has been populated before and needs to retrieve new values
            else if (cursor.getCount() > 0) {

                countQuery = "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME;
                cursor = db.rawQuery(countQuery, null);
                cursor.moveToFirst();
                int exists = cursor.getInt(0);

                if (exists > 0) {
                    //get first value of time
                    //a time of 0 is not possible, so if this exists
                    //we go into the other loop and terminate
                    cursor.moveToFirst();
                    lastZeroBACDate = cursor.getLong(1);
                    countQuery = "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                            " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME + ">" + lastZeroBACDate;
                    cursor = db.rawQuery(countQuery, null);
                    cursor.moveToFirst();

                    //initialise array to size of arraylist over 2 rows
                    //where one row is bac value and the other is the date time value
                    counter = cursor.getCount() + 1;

                    //continue the query as there is a populated table with decays and BAC updated
                    //as we need to return the value at a certain count, we need the counter value to offset
                    while (!cursor.isAfterLast() && cursor.moveToNext()) {
                        //store and return appropriate BAC & time within row
                        Time time = new Time(cursor.getLong(1));
                        BACTimeArray.add(time);
                        //traverse and return the value of the current float of the row
                        BACLevelArray.add(cursor.getFloat(2)*40);

                        if (BAC > maxBAC) {
                            maxBAC = BAC;
                        }
                        cursor.moveToNext();
                    }
                }
                //initial value has to be set to BAC of 0 at the time of first drink added...
                BACvalues = new float[1];
                BACtime = new String[1];

                if(cursor.getCount()>2) {
                    BACvalues[0] = 0;
                    BACtime[0] = String.valueOf(returnFirstNullValue());
                } else {
                    BACvalues[0] = 0;
                    BACtime[0] = "";
                }

                float[] tempLevel = new float[BACLevelArray.size()];
                String[] tempTime = new String[BACTimeArray.size()];

                for (int i = 0; i < tempLevel.length; i++) {
                    tempLevel[i] = BACLevelArray.get(i);
                    tempTime[i] = String.valueOf(BACTimeArray.get(i));
                }

                //now concatenate both initial and raw arrays
                yValues = concatenateFloats(BACvalues, tempLevel);
                xValues = concatenateStrings(BACtime, tempTime);

                xValues[0] = "";

                for(int i = 2; i < xValues.length - 1; i++) {
                    xValues[i] = "";
                }
            }
            db.close();
            cursor.close();
        } else {
            Toast.makeText(getBaseContext(),
                    getResources().getString(R.string.add_drink),
                    Toast.LENGTH_SHORT).show();
            xValues = new String[1];
            xValues[0] = "";
            yValues = new float[1];
            yValues[0] = 0;
        }
    }

    public static float[] concatenateFloats(float[] ... parms) {
        // calculate size of target array
        int size = 0;
        for (float[] array : parms) {
            size += array.length;
        }

        float[] result = new float[size];

        int j = 0;
        for (float[] array : parms) {
            for (float s : array) {
                result[j++] = s;
            }
        }
        return result;
    }

    public static String[] concatenateStrings(String [] ... parms) {
        // calculate size of target array
        int size = 0;
        for (String[] array : parms) {
            size += array.length;
        }

        String[] result = new String[size];

        int j = 0;
        for (String[] array : parms) {
            for (String s : array) {
                result[j++] = s;
            }
        }
        return result;
    }

    private Time returnFirstNullValue() {
        //first time stored in array
        return BACTimeArray.get(0);
    }

    private void showChart(final LineChartView chart) {
        Runnable action = new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        updateChart(lineChartView);
                    }
                }, 500);
            }
        };

        produce(chart, action);
    }

    private void updateChart(final LineChartView chart) {
        update(chart);
    }

    public void produce(LineChartView chart, Runnable action) {
        //retrieve BAC values from the table
        getBACTupleCurrent();
        //assign to base dataset for line of BAC and appropriately logged times
        LineSet dataset = new LineSet(xValues, yValues);

        int[] colours = {R.color.green, R.color.red500};

        //modify
        dataset.setColor(getResources().getColor(R.color.orange500))
                .setSmooth(true)
                .setDashed(new float[]{20, 20})
                .setGradientFill(colours,null);
        chart.addData(dataset);

        //gridview using paint
        Paint gridPaint = new Paint();
        gridPaint.setColor(getResources().getColor(R.color.white));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

            chart.setTopSpacing(Tools.fromDpToPx(10))
                    .setBorderSpacing(Tools.fromDpToPx(0))
                    .setAxisBorderValues(0, 10, 1)
                    .setXLabels(AxisController.LabelPosition.OUTSIDE)
                    .setYLabels(AxisController.LabelPosition.OUTSIDE)
                    .setLabelsColor(getResources().getColor(R.color.white))
                    .setXAxis(false)
                    .setYAxis(false)
                    .setGrid(LineChartView.GridType.HORIZONTAL, gridPaint)
                    .canScrollHorizontally(0);

        //initial animation and dashes continuous animation
        Animation anim = new Animation().setStartPoint(0, 0);
        chart.show(anim);
        chart.animateSet(0, new DashAnimation());
    }

    public void update(LineChartView chart) {
        getBACTupleCurrent();
        chart.notifyDataUpdate();
    }

    @Override
    public void onPause() {
        drinkTrackerDbHelper.close();
        super.onPause();
    }

    public void onResume() {
        drinkTrackerDbHelper = new DrinkTrackerDbHelper(this);
        super.onResume();
    }

    public void onDestroy() {
        drinkTrackerDbHelper.close();
        super.onDestroy();
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
        briefInfo.setText(this.getResources().getString(R.string.pollunits) +
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

    public void setLocale(String language) {
        this.language = language;
        locale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }
}