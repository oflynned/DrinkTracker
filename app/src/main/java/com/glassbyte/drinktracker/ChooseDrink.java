package com.glassbyte.drinktracker;
/*
* To do:
* -change all the fonts and dimensions to be expressed in dp or dpi
* */

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
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
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;


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

    int progress;

    CustomProgressBar customProgressBar;

    private List<FloatingActionMenu> menus = new ArrayList<>();
    private Handler mUiHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*Register the sharepreferences listener so that it doesn't get garbage collected*/
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        /*End Register the sharepreferences listener*/

        /*Set up the BloodAlcoholLevel object*/
        bloodAlcoholContent = new BloodAlcoholContent(this.getActivity());
        /*End of Set up the BloodAlcoholLevel object*/

        RelativeLayout rl = new RelativeLayout(this.getActivity());
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

        //fab layout
        final FloatingActionMenu menu = new FloatingActionMenu(getContext());
        final FloatingActionButton fab1 = new FloatingActionButton(getContext());
        final FloatingActionButton fab2 = new FloatingActionButton(getContext());

        menu.setMenuButtonColorRipple(getResources().getColor(R.color.orange300));
        menu.setMenuButtonColorNormal(getResources().getColor(R.color.orange500));
        menu.setMenuButtonColorPressed(getResources().getColor(R.color.orange700));

        menu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle(true);
            }
        });

        fab1.setButtonSize(FloatingActionButton.SIZE_NORMAL);
        fab1.setLabelText("Graphs & Effects");
        fab1.setImageResource(R.drawable.ic_action_clock);
        menu.addMenuButton(fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open graph
                Toast.makeText(getContext(), fab1.getLabelText(), Toast.LENGTH_SHORT).show();
            }
        });

        fab2.setButtonSize(FloatingActionButton.SIZE_NORMAL);
        fab2.setLabelText("Detailed Stats");
        fab2.setImageResource(R.drawable.ic_action_info);
        menu.addMenuButton(fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open dialog of stats
                Statistics statistics = new Statistics();
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.detailed_stats_title)
                        .setMessage(
                                getResources().getString(R.string.avg_drink_strength) + "\n" + statistics.getAvgABV() + "%" + "\n\n" +
                                        getResources().getString(R.string.avg_drink_volume) + "\n" + statistics.getAvgVol() + statistics.getUnits() + "\n\n" +
                                        getResources().getString(R.string.avg_calories) + "\n" + statistics.getCalories() + " " + getResources().getString(R.string.calories) + "\n\n" +
                                        getResources().getString(R.string.max_bac) + "\n" + statistics.getMaxBAC()
                        )
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        menu.invalidate();
        RelativeLayout.LayoutParams fabParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        fabParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        fabParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fabParams.setMargins(0, 0, 0, 16);
        menu.setLayoutParams(fabParams);
        menu.setClosedOnTouchOutside(true);
        menus.add(menu);

        int delay = 400;
        for (final FloatingActionMenu menu0 : menus) {
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menu0.showMenuButton(true);
                }
            }, delay);
            delay += 150;
        }

        rl.addView(leftSideBar);
        rl.addView(rightSideBar);
        rl.addView(bacDisplay);
        rl.addView(customProgressBar);
        rl.addView(pbBAC);
        rl.addView(menu);

        bacDisplay.invalidate();

        progress = (int) (bloodAlcoholContent.getCurrentEbac() * PROGESS_BAR_RATIO);
        if (progress < 75) {
            customProgressBar.setProgress(progress);
            startAnimation(progress);
        } else {
            progress = 75;
            customProgressBar.setProgress(progress);
            startAnimation(progress);
        }

        return rl;
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
