package com.glassbyte.drinktracker;
/*
* To do:
* -change all the fonts and dimensions to be expressed in dp or dpi
* */

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Maciej on 27/05/15.
 */

public class ChooseDrink extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SelectionSideBar leftSideBar, rightSideBar;
    private TextView bacDisplay, pbBAC, warningText;
    private final int BAC_DECIMAL_PLACES = 4;
    private final int BAC_FONT_SIZE = 40;
    private final int BAC_FONT_SIZE_SMALL = 30;
    private final int SIDE_BAR_WIDTH = 200;
    private final int PROGESS_BAR_RATIO = 300;
    private BloodAlcoholContent bloodAlcoholContent;
    SharedPreferences sp;

    String spGender, spUnits, units;

    double totUnits, maxUnits, maxBAC, avgABV, avgVol, currBAC;
    int calories;

    //mock id for testing
    private final static String AD_ID = "ca-app-pub-3940256099942544/6300978111";
    int progress;

    WarningDialog warningDialog;
    DrinkTrackerDbHelper drinkTrackerDbHelper;
    CustomProgressBar customProgressBar;
    private AdView adView;

    FloatingActionButton fab1;
    FloatingActionButton fab2;

    Dialog dialog;
    Runnable warningSystem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*Register the sharepreferences listener so that it doesn't get garbage collected*/
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        spGender = (sp.getString(getResources().getString(R.string.pref_key_editGender), ""));
        spUnits = (sp.getString(getResources().getString(R.string.pref_key_editUnits), ""));
        setUnits(spUnits);

        /*End Register the sharepreferences listener*/

        /*Set up the BloodAlcoholLevel object*/
        bloodAlcoholContent = new BloodAlcoholContent(this.getActivity());
        warningDialog = new WarningDialog(this.getActivity());
        drinkTrackerDbHelper = new DrinkTrackerDbHelper(this.getActivity());

        //instantiate warning system by use of a thread
        warningSystem = new Runnable() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        //constantly poll the bac on update via another thread
                        if (bloodAlcoholContent.getCurrentEbac() == 0) {
                            warningDialog.setWarning1(false);
                            warningDialog.setWarning2(false);
                            warningDialog.setWarning3(false);
                            warningDialog.setWarning4(false);
                        } else if ((bloodAlcoholContent.getCurrentEbac() >= 0.07)
                                && (bloodAlcoholContent.getCurrentEbac() < 0.13)
                                && (!warningDialog.getWarning1())) {
                            notificationManager.cancel(warningDialog.NOTIFICATION_ID);
                            warningDialog.setWarning1(true);
                            warningDialog.displayWarning("1");
                        } else if (bloodAlcoholContent.getCurrentEbac() >= 0.13
                                && (bloodAlcoholContent.getCurrentEbac() < 0.17)
                                && (!warningDialog.getWarning2())) {
                            notificationManager.cancel(warningDialog.NOTIFICATION_ID);
                            warningDialog.setWarning2(true);
                            warningDialog.displayWarning("2");
                        } else if (bloodAlcoholContent.getCurrentEbac() >= 0.17
                                && (bloodAlcoholContent.getCurrentEbac() < 0.22)
                                && (!warningDialog.getWarning3())) {
                            notificationManager.cancel(warningDialog.NOTIFICATION_ID);
                            warningDialog.setWarning3(true);
                            warningDialog.displayWarning("3");
                        } else if (bloodAlcoholContent.getCurrentEbac() > 0.22
                                && (!warningDialog.getWarning4())) {
                            notificationManager.cancel(warningDialog.NOTIFICATION_ID);
                            warningDialog.setWarning4(true);
                            warningDialog.displayWarning("4");
                        } else if (bloodAlcoholContent.getCurrentEbac() < 0.07
                                && warningDialog.getWarning1()) {
                            warningDialog.setWarning1(false);
                        } else if (bloodAlcoholContent.getCurrentEbac() < 0.13
                                && warningDialog.getWarning2()) {
                            warningDialog.setWarning2(false);
                        } else if (bloodAlcoholContent.getCurrentEbac() < 0.17
                                && warningDialog.getWarning3()) {
                            warningDialog.setWarning3(false);
                        } else if (bloodAlcoholContent.getCurrentEbac() < 0.22
                                && warningDialog.getWarning4()) {
                            warningDialog.setWarning4(false);
                        }

                        //also update the current stats
                        setUpCalender();
                        setTotalUnits(totUnits);
                        //calories are units*7*8 as 1 unit = 8g where 1g = 7 calories therefore
                        setCalories((int) (totUnits * 56));
                        setAvgABV();
                        setAvgVol();
                        setMaxBAC();
                    }
                });
            }
        };

        warningSystem.run();

        /*End of Set up the BloodAlcoholLevel object*/

        final RelativeLayout rl = new RelativeLayout(this.getActivity());
        rl.setBackgroundColor(getResources().getColor(R.color.orange100));

        leftSideBar = new SelectionSideBar(this.getActivity(), true);
        rightSideBar = new SelectionSideBar(this.getActivity(), false);

        RelativeLayout.LayoutParams leftSideBarParams = new RelativeLayout.LayoutParams(SIDE_BAR_WIDTH, RelativeLayout.LayoutParams.MATCH_PARENT);
        leftSideBarParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftSideBar.setLayoutParams(leftSideBarParams);
        leftSideBar.setId(View.generateViewId());

        RelativeLayout.LayoutParams rightSideBarParams = new RelativeLayout.LayoutParams(SIDE_BAR_WIDTH, RelativeLayout.LayoutParams.MATCH_PARENT);
        rightSideBarParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightSideBar.setLayoutParams(rightSideBarParams);
        rightSideBar.setId(View.generateViewId());

        customProgressBar = new CustomProgressBar(this.getActivity(), null, android.R.style.Widget_DeviceDefault_ProgressBar);
        RelativeLayout.LayoutParams customProgressBarParam = new RelativeLayout.LayoutParams(500, 500);
        customProgressBarParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        int progress = (int) (bloodAlcoholContent.getCurrentEbac() * 200);
        customProgressBar.setProgress(progress);
        customProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_circle));
        customProgressBar.setVisibility(View.VISIBLE);
        customProgressBar.setLayoutParams(customProgressBarParam);
        customProgressBar.setId(View.generateViewId());

        bacDisplay = new TextView(this.getActivity());
        bacDisplay.setTextSize(BAC_FONT_SIZE);
        bacDisplay.setTextColor(Color.BLACK);
        bacDisplay.setGravity(Gravity.CENTER);
        bacDisplay.setText(getResources().getString(R.string.current_BAC));
        RelativeLayout.LayoutParams bacDisplayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bacDisplayParams.addRule(RelativeLayout.LEFT_OF, rightSideBar.getId());
        bacDisplayParams.addRule(RelativeLayout.RIGHT_OF, leftSideBar.getId());
        bacDisplayParams.addRule(RelativeLayout.ABOVE, customProgressBar.getId());
        bacDisplayParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bacDisplay.setLayoutParams(bacDisplayParams);
        bacDisplay.setId(View.generateViewId());

        pbBAC = new TextView(this.getActivity());
        pbBAC.setTextSize(BAC_FONT_SIZE_SMALL);
        pbBAC.setTextColor(Color.BLACK);
        pbBAC.setGravity(Gravity.CENTER);
        pbBAC.setText("" + BloodAlcoholContent.round(bloodAlcoholContent.getCurrentEbac(), BAC_DECIMAL_PLACES));
        RelativeLayout.LayoutParams pbBACParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        pbBACParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        pbBAC.setLayoutParams(pbBACParams);
        pbBAC.setId(View.generateViewId());

        fab1 = new FloatingActionButton(getContext());
        fab2 = new FloatingActionButton(getContext());

        //advert

        adView = new AdView(getContext());
        final RelativeLayout.LayoutParams paramsAds = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsAds.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        paramsAds.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adView.setLayoutParams(paramsAds);
        adView.setId(View.generateViewId());
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(AD_ID);


        //request ads to target emulated device
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .setGender(getGender(spGender));

        //fab1 fab
        final RelativeLayout.LayoutParams paramsFAB1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsFAB1.addRule(RelativeLayout.RIGHT_OF, leftSideBar.getId());
        paramsFAB1.addRule(RelativeLayout.ALIGN_LEFT);
        paramsFAB1.addRule(RelativeLayout.ABOVE, adView.getId());
        fab1.setLayoutParams(paramsFAB1);

        fab1.setButtonSize(FloatingActionButton.SIZE_NORMAL);
        fab1.setImageResource(R.drawable.graphs);
        fab1.setColorRipple(getResources().getColor(R.color.orange300));
        fab1.setColorNormal(getResources().getColor(R.color.orange500));
        fab1.setColorPressed(getResources().getColor(R.color.orange700));
        fab1.setId(View.generateViewId());
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Statistics.class));
            }
        });

        //fab2 fab
        final RelativeLayout.LayoutParams paramsFAB2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsFAB2.addRule(RelativeLayout.LEFT_OF, rightSideBar.getId());
        paramsFAB2.addRule(RelativeLayout.ALIGN_RIGHT);
        paramsFAB2.addRule(RelativeLayout.ABOVE, adView.getId());
        fab2.setLayoutParams(paramsFAB2);

        fab2.setButtonSize(FloatingActionButton.SIZE_NORMAL);
        fab2.setImageResource(R.drawable.info);
        fab2.setColorRipple(getResources().getColor(R.color.orange300));
        fab2.setColorNormal(getResources().getColor(R.color.orange500));
        fab2.setColorPressed(getResources().getColor(R.color.orange700));
        fab2.setId(View.generateViewId());

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open dialog of stats
                dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.detailed_stats_title)
                        .setMessage(
                                getResources().getString(R.string.avg_drink_strength) + "\n" + getAvgABV() + "%" + "\n\n" +
                                        getResources().getString(R.string.avg_drink_volume) + "\n" + getAvgVol() + getUnits() + "\n\n" +
                                        getResources().getString(R.string.avg_calories) + "\n" + getCalories() + " " + getResources().getString(R.string.calories) + "\n\n" +
                                        getResources().getString(R.string.max_bac) + "\n" + getMaxBAC()
                        )
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        rl.addView(leftSideBar);
        rl.addView(rightSideBar);
        rl.addView(bacDisplay);
        rl.addView(customProgressBar);
        rl.addView(pbBAC);
        rl.addView(adView);
        rl.addView(fab1);
        rl.addView(fab2);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                rl.removeView(fab1);
                rl.removeView(fab2);
                paramsFAB1.removeRule(RelativeLayout.ABOVE);
                paramsFAB2.removeRule(RelativeLayout.ABOVE);
                paramsFAB1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                paramsFAB2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                fab1.setLayoutParams(paramsFAB1);
                fab2.setLayoutParams(paramsFAB2);
                rl.addView(fab1);
                rl.addView(fab2);
                rl.invalidate();
            }

            @Override
            public void onAdLoaded() {
                rl.removeView(fab1);
                rl.removeView(fab2);
                paramsFAB1.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                paramsFAB2.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                paramsFAB1.addRule(RelativeLayout.ABOVE, adView.getId());
                paramsFAB2.addRule(RelativeLayout.ABOVE, adView.getId());
                fab1.setLayoutParams(paramsFAB1);
                fab2.setLayoutParams(paramsFAB2);
                rl.addView(fab1);
                rl.addView(fab2);
                rl.invalidate();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                rl.removeView(fab1);
                rl.removeView(fab2);
                paramsFAB1.removeRule(RelativeLayout.ABOVE);
                paramsFAB2.removeRule(RelativeLayout.ABOVE);
                paramsFAB1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                paramsFAB2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                fab1.setLayoutParams(paramsFAB1);
                fab2.setLayoutParams(paramsFAB2);
                rl.addView(fab1);
                rl.addView(fab2);
                rl.invalidate();
            }
        });

        adView.loadAd(adRequestBuilder.build());

        progress = (int) (bloodAlcoholContent.getCurrentEbac() * PROGESS_BAR_RATIO);
        if (progress < 75) {
            customProgressBar.setProgress(progress);
            startAnimation(progress);
        } else {
            progress = 75;
            customProgressBar.setProgress(progress);
            startAnimation(progress);
        }

        System.out.println("totunits in main: " + totUnits);
        return rl;
    }

    protected void setUpCalender() {
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
                    System.out.println("curr units: " + currUnits);
                    totUnits = totUnits + Double.parseDouble(currUnits);
                } else {
                    //go to next row
                    cursor.moveToNext();
                }
            }
            while (cursor.moveToNext() && Long.parseLong(cursor.getString(1)) < startOfNextWeek);

            System.out.println("totunis: " + totUnits);
            setTotalUnits(BloodAlcoholContent.round(totUnits, 2));

            //close operations and sum
            db.close();
            cursor.close();
        }
    }

    private void setAvgABV() {
        //select first row by date fo start of week and sum until it reaches whileNot
        String countQuery = "SELECT  * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME;
        SQLiteDatabase db = drinkTrackerDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        double count = 0;
        double totABV = 0;
        String ABV;
        float currABV;

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (!cursor.isNull(4)) {
                    //if date lies within period
                    ABV = cursor.getString(4);
                    currABV = Float.parseFloat(ABV);
                    totABV = totABV + currABV;
                    count++;
                } else {
                    //go to next row
                    cursor.moveToNext();
                }
            } while (cursor.moveToNext());

            avgABV = BloodAlcoholContent.round((totABV / count), 1);

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
            if (getUnits().equals(getResources().getString(R.string.oz))) {
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

    protected void setUnits(String spUnits) {
        if (spUnits.equals("metric") || spUnits.equals("Metric")) {
            this.units = getResources().getString(R.string.ml);
        } else {
            this.units = getResources().getString(R.string.oz);
        }
    }

    protected String getUnits() {
        return units;
    }

    protected void setTotalUnits(double totUnits) {
        this.totUnits = totUnits;
    }

    protected double getTotalUnits() {
        return totUnits;
    }

    protected void setCalories(int calories) {
        this.calories = calories;
    }

    protected double getCalories() {
        return calories;
    }

    protected void setMaxBAC() {
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

    public int getGender(String spGender) {
        if (spGender.equals("male") || spGender.equals("Male")) {
            return AdRequest.GENDER_MALE;
        } else {
            return AdRequest.GENDER_FEMALE;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    public void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }

    private void startAnimation(float BAC) {
        ProgressBarAnimation localProgressBarAnimation = new ProgressBarAnimation(0.0F, BAC);
        localProgressBarAnimation.setInterpolator(new OvershootInterpolator(0.5F));
        localProgressBarAnimation.setDuration(2000L);
        customProgressBar.startAnimation(localProgressBarAnimation);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s == this.getString(R.string.pref_key_currentEbac)) {
            pbBAC.setText("" + BloodAlcoholContent.round(bloodAlcoholContent.getCurrentEbac(), BAC_DECIMAL_PLACES));
            progress = (int) (bloodAlcoholContent.getCurrentEbac() * PROGESS_BAR_RATIO);
            if (progress < 75) {
                customProgressBar.setProgress(progress);
                startAnimation(progress);
            } else {
                progress = 75;
                customProgressBar.setProgress(progress);
                startAnimation(progress);
            }
            pbBAC.invalidate();
            warningSystem.run();
        }

        if (s == this.getString(R.string.pref_key_editUnits)) {
            SharedPreferences spEditUnits = PreferenceManager.getDefaultSharedPreferences(getContext());

            //acquire new units and convert
            String changedUnits = spEditUnits.getString(s, "");
            Toast.makeText(getContext(), changedUnits, Toast.LENGTH_SHORT).show();
            if (changedUnits.equalsIgnoreCase("metric")) {
                setUnits(getResources().getString(R.string.ml));
                avgVol = BloodAlcoholContent.MetricSystemConverter.convertOzToMillilitres(avgVol);
                avgVol = BloodAlcoholContent.round(avgVol, 2);
            } else {
                setUnits(getResources().getString(R.string.oz));
                avgVol = BloodAlcoholContent.MetricSystemConverter.convertMillilitresToOz(avgVol);
                avgVol = BloodAlcoholContent.round(avgVol, 2);
            }
            fab2.invalidate();
        }
    }

    public class SelectionSideBar extends View {
        private boolean isLeft;
        private Context mContext;
        private Paint textPaint;
        private final String LEFT_SIDE_BAR_TEXT = getResources().getString(R.string.preset_drinks);
        private final String RIGHT_SIDE_BAR_TEXT = getResources().getString(R.string.custom_drinks);
        private final int FONT_SIZE = 60;

        public SelectionSideBar(Context c, boolean isLeft) {
            super(c);
            mContext = c;
            this.isLeft = isLeft;

            //lollipop method of calling
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (isLeft) {
                    this.setBackground(c.getDrawable(R.drawable.choose_drink_left_side_bg));
                } else {
                    this.setBackground(c.getDrawable(R.drawable.choose_drink_right_side_bg));
                }
            }
            //for under lollipop using deprecation
            else {
                if (isLeft)
                    this.setBackground(c.getResources().getDrawable(R.drawable.choose_drink_left_side_bg));
                else
                    this.setBackground(c.getResources().getDrawable(R.drawable.choose_drink_right_side_bg));
            }

            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(FONT_SIZE);
            textPaint.setFakeBoldText(true);
        }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);

            if (isLeft) {
                float textWidth = textPaint.measureText(LEFT_SIDE_BAR_TEXT);
                c.translate(SIDE_BAR_WIDTH / 2 - FONT_SIZE / 2, this.getHeight() / 2 - textWidth / 2);
                c.rotate(90);
                c.drawText(LEFT_SIDE_BAR_TEXT, 0, 0, textPaint);
            } else {
                float textWidth = textPaint.measureText(RIGHT_SIDE_BAR_TEXT);
                c.translate(SIDE_BAR_WIDTH / 2 + FONT_SIZE / 2, this.getHeight() / 2 + textWidth / 2);
                c.rotate(-90);
                c.drawText(RIGHT_SIDE_BAR_TEXT, 0, 0, textPaint);
            }
            c.restore();
        }
    }

    public class CustomProgressBar extends ProgressBar {
        public CustomProgressBar(Context paramContext) {
            super(paramContext);
        }

        public CustomProgressBar(Context paramContext,
                                 AttributeSet paramAttributeSet) {
            super(paramContext, paramAttributeSet);
        }

        public CustomProgressBar(Context paramContext,
                                 AttributeSet paramAttributeSet, int paramInt) {
            super(paramContext, paramAttributeSet, paramInt);
        }

        public void draw(Canvas paramCanvas) {
            int i = getMeasuredWidth();
            int j = getMeasuredHeight();
            paramCanvas.save();
            paramCanvas.rotate(135.0F, i / 2, j / 2);
            super.draw(paramCanvas);
            paramCanvas.restore();
        }
    }

    private class ProgressBarAnimation extends Animation {
        private float from;
        private float to;

        public ProgressBarAnimation(float from, float to) {
            this.from = from;
            this.to = to;
        }

        protected void applyTransformation(float paramFloat, Transformation paramTransformation) {
            super.applyTransformation(paramFloat, paramTransformation);
            float f = this.from + paramFloat * (this.to - this.from);
            customProgressBar.setProgress((int) f);
        }
    }
}
