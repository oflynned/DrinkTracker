package com.glassbyte.drinktracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.Date;
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

    double totUnits, maxUnits, maxBAC, avgABV, avgVol, currBAC;
    int orange, calories;

    String spGender, spUnits, units;
    LineChartView chart;

    TextView briefInfo, rating, BACinfo, BACrating;

    BloodAlcoholContent bloodAlcoholContent;
    DrinkTrackerDbHelper drinkTrackerDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        drinkTrackerDbHelper = new DrinkTrackerDbHelper(this);

        orange = getResources().getColor(R.color.orange500);

        infoButton = (FloatingActionButton) findViewById(R.id.infoButton);
        infoButton.setOnCheckedChangeListener(this);

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
        axisX.setName("Time");
        axisY.setName("BAC");
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

        if (cursor.getCount() != 0) {
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
        }
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
        BACinfo.setText("Current BAC level: " + BAC);

        if (BAC >= 0 && BAC < 0.01) {
            //at 0-0.01
            BACrating.setText("Sober");
            BACrating.setTextColor(Color.GREEN);
        } else if (BAC >= 0.02 && BAC < 0.04) {
            //0.02-0.03
            BACrating.setText("Mildly relaxed and no loss of coordination");
            BACrating.setTextColor(Color.GREEN);
        } else if (BAC >= 0.04 && BAC < 0.07) {
            //0.04-0.06
            BACrating.setText("Slight impairment, lowered inhibitions, lowered caution, exaggeration of behaviour");
            BACrating.setTextColor(Color.GREEN);
        } else if (BAC >= 0.07 && BAC < 0.1) {
            //0.07-0.09
            BACrating.setText("Mild impairment of balance, speech, vision and reaction time.");
            BACrating.setTextColor(Color.YELLOW);
        } else if (BAC >= 0.1 && BAC < 0.13) {
            //0.1-0.129
            BACrating.setText("Significant impairment of coordination and loss of judgement. Feeling of euphoria.");
            BACrating.setTextColor(Color.YELLOW);
        } else if (BAC >= 0.13 && BAC < 0.16) {
            //0.13-0.15
            BACrating.setText("Lack of physical control, loss of balance, and blurring of vision. Reduced euphoria and increased dysphoria. Severe impairment of perception and judgement.");
            BACrating.setTextColor(orange);
        } else if (BAC >= 0.16 && BAC < 0.2) {
            //0.16-0.19
            BACrating.setText("Intensifying of dysphoria, increase in nausea, appears 'more sloppy'");
            BACrating.setTextColor(orange);
        } else if (BAC >= 0.2 && BAC < 0.25) {
            //0.2-0.24
            BACrating.setText("Dazed, confusion, disorientation. May not feel pain if injured. Nausea and vomiting experienced around this level. Gag reflex may be impaired. Blackouts are more likely from this point.");
            BACrating.setTextColor(Color.RED);
        } else if (BAC >= 0.25 && BAC < 0.3) {
            //0.25-0.29
            BACrating.setText("Severe impairment of all sensory, physical and mental functions. Increased risk of asphyxiation by vomiting and injury.");
            BACrating.setTextColor(Color.RED);
        } else if (BAC >= 0.3 && BAC < 0.35) {
            //0.3-0.34
            BACrating.setText("Drunken stupor - you have little comprehension of where you are. Passing out and difficulty to be awoken is very likely.");
            BACrating.setTextColor(Color.RED);
        } else if (BAC >= 0.35 && BAC < 0.4) {
            //0.35-0.39
            BACrating.setText("Equivalent to surgical level anaesthesia");
            BACrating.setTextColor(Color.RED);
        } else {
            //0.4+
            BACrating.setText("Onset of coma and possible death due to respiratory arrest");
            BACrating.setTextColor(Color.RED);
        }

        //set weekly
        briefInfo.setText("Total units drunk this week:\n" + getTotalUnits() + "/" + getMaxUnits() + " units");

        if (getTotalUnits() <= getMaxUnits()) {
            rating.setTextColor(Color.GREEN);
            rating.setText("Below recommended weekly limit");
        } else if (getTotalUnits() > getMaxUnits()) {
            rating.setTextColor(Color.RED);
            rating.setText("Excessive drinking!");
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

    @Override
    public void onCheckedChanged(FloatingActionButton fabView, final boolean isChecked) {
        switch (fabView.getId()) {
            case R.id.infoButton:
                new AlertDialog.Builder(this)
                        .setTitle("Detailed Statistics")
                        .setMessage(
                                "Average strength of drinks:\n" + getAvgABV() + "%" + "\n\n" +
                                        "Average volume of drinks:\n" + getAvgVol() + getUnits() + "\n\n" +
                                        "Alcohol calories this week:\n" + getCalories() + " calories" + "\n\n" +
                                        "Maximum BAC achieved:\n" + getMaxBAC()
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
            System.out.println("avgABV: " + avgABV);

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

    private double getAvgVol() {
        return avgVol;
    }

    private double getAvgABV() {
        return avgABV;
    }

    private double getMaxBAC() {
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

    private String getUnits() {
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

    private double getCalories() {
        return calories;
    }
}