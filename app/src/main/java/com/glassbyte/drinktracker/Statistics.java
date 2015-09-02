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
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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

    double avgUnits, totUnits, maxUnits, avgTime, BACAchieved;
    int daysDrinking, orange;

    Calendar c;
    DateFormat df;
    String startDate = "", endDate = "";

    String spGender;
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

        bloodAlcoholContent = new BloodAlcoholContent(this);

        setUpCalender();
        setMethods();

        //graph instantiation
        chart = (LineChartView) findViewById(R.id.chart);
        generateData();
        chart.setViewportCalculationEnabled(false);
        setViewport();
        chart.startDataAnimation();
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

        double totUnits = 0;
        String currUnits;

        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("HH:mm:ss dd/MM/yyyy");
        DateTime startDate = dateStringFormat.parseDateTime(whileAfter);
        DateTime endDate = dateStringFormat.parseDateTime(whileBefore);

        cursor.moveToFirst();

        //col 1 for time
        //col 6 for units
        //sum row of col 6 if its date lies between start and end

        do{
            if (dateStringFormat.parseDateTime(cursor.getString(1)).isAfter(startDate) &&
                    dateStringFormat.parseDateTime(cursor.getString(1)).isBefore(endDate)){
                //if date lies within period
                currUnits = cursor.getString(6);
                System.out.println(currUnits);
                totUnits = totUnits + Double.parseDouble(currUnits);
            } else {
                //go to next row
                cursor.moveToNext();
            }
        } while (cursor.moveToNext() && dateStringFormat.parseDateTime(cursor.getString(1)).isBefore(endDate));

        System.out.println(totUnits);
        setTotalUnits(BloodAlcoholContent.round(totUnits, 2));

        //close operations and sum
        db.close();
        cursor.close();
    }

    private void setMethods() {
        //need to replace with proper methods
        setAvgUnits(0);

        //21 for men, 14 for women per week
        setMaxUnits();

        setAvgTime(0);
        setBACAchieved(0);
        setDaysDrinking(0);

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
            BACrating.setText("Mild impairment, lowered inhibitions, lowered caution, exaggeration of behaviour");
            BACrating.setTextColor(Color.GREEN);
        } else if (BAC >= 0.07 && BAC < 0.1) {
            //0.07-0.09
            BACrating.setText("Unable to drive safely; slight impairment of balance, speech, vision and reaction time.");
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

    private void generateData() {
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

    private void setAxes(LineChartData data) {
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
        } else {
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