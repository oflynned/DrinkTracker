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
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by ed on 25/08/15.
 */
public class Statistics extends Activity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    Locale locale;
    String language;

    private LineChartView lineChartView;
    private boolean updateView;
    private final String[] mLabelsOne= {"", "10-15", "", "15-20", "", "20-25", "", "25-30", "", "30-35", ""};
    private final float[][] mValuesOne = {{3.5f, 4.7f, 4.3f, 8f, 6.5f, 10f, 7f, 8.3f, 7.0f, 7.3f, 5f},
            {2.5f, 3.5f, 3.5f, 7f, 5.5f, 8.5f, 6f, 6.3f, 5.8f, 6.3f, 4.5f},
            {1.5f, 2.5f, 2.5f, 4f, 2.5f, 5.5f, 5f, 5.3f, 4.8f, 5.3f, 3f}};

    int orange, counter;
    double totUnits, maxUnits, BAC, maxBAC;
    long BACTime, maxTime, minTime;
    private LineGraphSeries<DataPoint> series;

    //Spinner spinner;
    TextView briefInfo, rating, BACinfo, BACrating;

    BloodAlcoholContent bloodAlcoholContent;
    DrinkTrackerDbHelper drinkTrackerDbHelper;

    ChooseDrink chooseDrink;
    String spGender;
    java.text.DateFormat dateTimeFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dateTimeFormatter = DateFormat.getTimeFormat(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        spGender = (sp.getString(getResources().getString(R.string.pref_key_editGender), ""));

        //set irish if the checkbox is checked in settings
        boolean irish = false;
        boolean nds = false;
        boolean irishChosen = sp.getBoolean(getResources().getString(R.string.pref_key_irish), irish);
        boolean ndsChosen = sp.getBoolean(getResources().getString(R.string.pref_key_nds), nds);

        if(irishChosen) {
            setLocale("ga");
        }
        else if(ndsChosen) {
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
        //spinner = (Spinner) findViewById(R.id.periodSpinner);

        bloodAlcoholContent = new BloodAlcoholContent(this);
        setRecommendations();

        //ad request
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //chart
        lineChartView = (LineChartView) findViewById(R.id.linechart);
        showChart(0, lineChartView);

        /*
        //gets current spinner selection, removed for time being as lacking time
        //get the selection from the spinner to display the correct time period
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {

                //Get item from spinner and store in string
                String selected = String.valueOf(parent.getSelectedItemPosition());

                switch (selected) {
                    //current
                    case "0":

                        break;
                    //weekly
                    case "1":

                        break;
                    //monthly
                    case "2":

                        break;
                }
            }

            public void onNothingSelected(AdapterView parent) {
                //Do nothing
            }
        });*/
    }

    private void showChart(final int tag, final LineChartView chart){
        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        updateChart(0, lineChartView);
                    }
                }, 500);
            }
        };

        switch(tag){
            case 0:
                produce(chart, action); break;
            default:
        }
    }

    private void updateChart(final int tag, final LineChartView chart){

        switch(tag){
            case 0:
                update(chart);
                break;
            default:
        }
    }

    public void produce(LineChartView chart, Runnable action){

        LineSet dataset = new LineSet(mLabelsOne, mValuesOne[1]);
        dataset.setColor(getResources().getColor(R.color.orange500))
                .setSmooth(true)
                .setDashed(new float[]{20, 20});
        chart.addData(dataset);

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

                .setGrid(LineChartView.GridType.FULL, gridPaint);

        Animation anim = new Animation().setStartPoint(0, 0);
        chart.show(anim);
        chart.animateSet(0, new DashAnimation());
    }

    public void update(LineChartView chart){
        float[][] newValues = {{3.5f, 4.7f, 4.3f, 8f, 6.5f, 10f, 7f, 8.3f, 7.0f, 7.3f, 5f},
                {1f, 2f, 2f, 3.5f, 2f, 5f, 4.5f, 4.8f, 4.3f, 4.8f, 2.5f}};
        chart.updateValues(0, newValues[0]);
        chart.notifyDataUpdate();
    }

    @Override
    public void onPause(){
        drinkTrackerDbHelper.close();
        super.onPause();
    }

    public void onResume(){
        drinkTrackerDbHelper = new DrinkTrackerDbHelper(this);
        super.onResume();
    }

    public void onDestroy(){
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

    public void setLocale(String language){
        this.language = language;
        locale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }
}