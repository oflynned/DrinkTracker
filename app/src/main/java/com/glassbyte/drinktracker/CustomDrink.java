package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Button;

import com.jjoe64.graphview.series.PointsGraphSeries;

/**
 * Created by Maciej on 27/05/15.
 */
public class CustomDrink extends Fragment {
    private final int NUMBER_OF_GLASSES = 4;
    private final int PADDING = 16;

    private final int SHOT_GLASS_ML = 40;
    private final int WATER_GLASS_ML = 250;
    private final int PINT_GLASS_ML = 500;
    private final int WINE_GLASS_ML = 300;

    //private final int DRINK_COLOUR = Color.argb(170, 0 , 100, 255);
    private final int DRINK_COLOUR = Color.argb(255, 109, 140, 160);

    private Activity thisActivity;
    private RelativeLayout rl;

    private int previewHeight;
    private int previewWidth;
    private int chosenViewWidth;
    private int chosenViewHeight;
    private int shotGlassWidth;
    private int shotGlassHeight;
    private int waterGlassWidth;
    private int waterGlassHeight;
    private int pintGlassWidth;
    private int pintGlassHeight;
    private int wineGlassWidth;
    private int wineGlassHeight;
    private int chosenGlassWidth;
    private int chosenGlassHeight;
    private int alcBarWidth;
    private int alcBarHeight;

    private Glass shotGlass,waterGlass, pintGlass, wineGlass, chosenGlass;
    private int chosenGlassViewId;

    private SeekBar alcBar;
    private TextView alcVolDisplay;
    private Button drinkButton;

    private Display display;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisActivity = this.getActivity();

        display = this.getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        alcBarWidth = screenWidth;
        alcBarHeight = screenHeight/20;

        previewWidth = screenWidth/NUMBER_OF_GLASSES;
        previewHeight = screenHeight/4;

        pintGlassHeight = (int)(previewHeight/1.2f);
        pintGlassWidth = (int)(pintGlassHeight/2);

        wineGlassHeight = pintGlassHeight;
        wineGlassWidth = pintGlassWidth;

        shotGlassHeight = (int)(pintGlassHeight/4);
        shotGlassWidth = (int)(shotGlassHeight/1.2f);

        waterGlassHeight = (int)(pintGlassHeight/2);
        waterGlassWidth = (int)(pintGlassWidth/1.2f);

        rl = new RelativeLayout(this.getActivity());
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rl.setPadding(PADDING, PADDING, PADDING, PADDING);
        rl.setLayoutParams(rlParams);
        rl.setBackgroundColor(Color.rgb(244, 245, 231));

        alcVolDisplay = new TextView(thisActivity);
        RelativeLayout.LayoutParams alcVolDisplayParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alcVolDisplayParam.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        alcVolDisplayParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        alcVolDisplay.setLayoutParams(alcVolDisplayParam);
        alcVolDisplay.setId(View.generateViewId());
        alcVolDisplay.setTextColor(DRINK_COLOUR);
        alcVolDisplay.setTypeface(null, Typeface.BOLD);

        drinkButton = new Button(thisActivity);
        drinkButton.setTypeface(null, Typeface.BOLD);
        drinkButton.setTextColor(Color.WHITE);
        drinkButton.setText("Drink!");
        drinkButton.setBackgroundColor(Color.rgb(255, 120, 0));
        drinkButton.setId(View.generateViewId());
        RelativeLayout.LayoutParams drinkButtonParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        drinkButtonParam.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        drinkButtonParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        drinkButton.setLayoutParams(drinkButtonParam);

        chosenViewWidth = screenWidth - PADDING*2;
        chosenViewHeight = screenHeight - previewHeight - PADDING*2 - drinkButton.getHeight();

        alcBar = new SeekBar(thisActivity);
        alcBar.setKeyProgressIncrement(1);
        alcBar.setMax(100);
        alcBar.setProgress(6);
        alcBar.setProgressDrawable(ContextCompat.getDrawable(thisActivity, R.drawable.alc_bar));
        LayerDrawable thumb = (LayerDrawable)ContextCompat.getDrawable(thisActivity, R.drawable.alc_bar_thumb);
        alcBar.setThumb(thumb);
        alcBar.setThumbOffset(0);
        alcBar.setVisibility(View.VISIBLE);
        alcBar.setMinimumHeight(150);
        RelativeLayout.LayoutParams alcBarParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT - alcVolDisplay.getWidth() - drinkButton.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        //alcBarParam.addRule(RelativeLayout., drinkButton.getId());
        alcBarParam.addRule(RelativeLayout.RIGHT_OF, alcVolDisplay.getId());
        alcBarParam.addRule(RelativeLayout.LEFT_OF, drinkButton.getId());
        alcBar.setLayoutParams(alcBarParam);
        alcBar.setId(View.generateViewId());

        alcVolDisplay.setText(alcBar.getProgress() + "% Alc.");

        alcBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                alcVolDisplay.setText(seekBar.getProgress() + "% Alc.");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Create shot glass instance
        shotGlass = new DrinkingGlass(this.getActivity(), previewWidth, previewHeight, shotGlassWidth, shotGlassHeight, (previewWidth-shotGlassWidth)/2, (previewHeight-shotGlassHeight)/2, SHOT_GLASS_ML);
        RelativeLayout.LayoutParams shotGlassParams = new RelativeLayout.LayoutParams(shotGlass.getLayoutParams());
        shotGlassParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        shotGlassParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        shotGlass.setLayoutParams(shotGlassParams);
        shotGlass.setBackground(ContextCompat.getDrawable(thisActivity, R.drawable.preview_glass_bg));
        shotGlass.setId(View.generateViewId());

        waterGlass = new DrinkingGlass(this.getActivity(), previewWidth, previewHeight, waterGlassWidth, waterGlassHeight, (previewWidth-waterGlassWidth)/2, (previewHeight-waterGlassHeight)/2, WATER_GLASS_ML);
        RelativeLayout.LayoutParams waterGlassParams = new RelativeLayout.LayoutParams(waterGlass.getLayoutParams());
        waterGlassParams.addRule(RelativeLayout.RIGHT_OF, shotGlass.getId());
        waterGlassParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        waterGlass.setLayoutParams(waterGlassParams);
        waterGlass.setBackground(ContextCompat.getDrawable(thisActivity, R.drawable.preview_glass_bg));
        //waterGlass.setBackgroundColor(Color.rgb(255, 128, 0));
        waterGlass.setId(View.generateViewId());

        pintGlass = new DrinkingGlass(this.getActivity(), previewWidth, previewHeight, pintGlassWidth, pintGlassHeight, (previewWidth-pintGlassWidth)/2, (previewHeight-pintGlassHeight)/2, PINT_GLASS_ML);
        RelativeLayout.LayoutParams pintGlassParams = new RelativeLayout.LayoutParams(pintGlass.getLayoutParams());
        pintGlassParams.addRule(RelativeLayout.RIGHT_OF, waterGlass.getId());
        pintGlassParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        pintGlass.setLayoutParams(pintGlassParams);
        pintGlass.setBackground(ContextCompat.getDrawable(thisActivity, R.drawable.preview_glass_bg));
        pintGlass.setId(View.generateViewId());

        //Create wine glass instance
        wineGlass = new WineGlass(this.getActivity(), previewWidth, previewHeight, wineGlassWidth, wineGlassHeight, (previewWidth-wineGlassWidth)/2, (previewHeight-wineGlassHeight)/2, WINE_GLASS_ML);
        RelativeLayout.LayoutParams wineGlassParams = new RelativeLayout.LayoutParams(wineGlass.getLayoutParams());
        wineGlassParams.addRule(RelativeLayout.RIGHT_OF, pintGlass.getId());
        wineGlassParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        wineGlass.setLayoutParams(wineGlassParams);
        wineGlass.setBackground(ContextCompat.getDrawable(thisActivity, R.drawable.preview_glass_bg));
        wineGlass.setId(View.generateViewId());


        chosenGlassWidth = getChosenGlassWidth(pintGlassWidth);
        chosenGlassHeight = getChosenGlassHeight(pintGlassHeight);
        chosenGlass = new DrinkingGlass(this.getActivity(), chosenViewWidth, chosenViewHeight, chosenGlassWidth, chosenGlassHeight, (chosenViewWidth-chosenGlassWidth)/2, (chosenViewHeight-chosenGlassHeight)/2, false, true, PINT_GLASS_ML);
        RelativeLayout.LayoutParams chosenGlassParams = new RelativeLayout.LayoutParams(chosenGlass.getLayoutParams());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, shotGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, waterGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, pintGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, wineGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.BELOW, alcBar.getId());
        chosenGlass.setLayoutParams(chosenGlassParams);
        //chosenGlass.setBackgroundColor(Color.rgb(255, 140, 0)); // DARK ORANGE
        chosenGlassViewId = View.generateViewId();
        chosenGlass.setId(chosenGlassViewId);

        rl.addView(alcVolDisplay);
        rl.addView(drinkButton);
        rl.addView(alcBar);
        rl.addView(chosenGlass);
        rl.addView(shotGlass);
        rl.addView(waterGlass);
        rl.addView(pintGlass);
        rl.addView(wineGlass);

        return rl;
    }

    private int getChosenGlassWidth(int previewGlassWidth){
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        chosenViewWidth = screenWidth - PADDING*2;
        return chosenViewWidth/previewWidth*previewGlassWidth;
    }
    private int getChosenGlassHeight(int previewGlassHeight){
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        chosenViewHeight = screenHeight - previewHeight - PADDING*2 - drinkButton.getHeight();
        return chosenViewHeight/previewHeight*previewGlassHeight;
    }

    private void newChosenGlass(Glass glass, boolean isWineGlass){
        //Make sure all other glasses are invalidated
        shotGlass.setIsChosen(false);
        waterGlass.setIsChosen(false);
        pintGlass.setIsChosen(false);
        wineGlass.setIsChosen(false);

        rl.removeView(chosenGlass);

        chosenGlassWidth = getChosenGlassWidth((int) glass.getGlassWidth());
        chosenGlassHeight = getChosenGlassHeight((int)glass.getGlassHeight());
        int chosenGlassMlSize = glass.getMlSize();
        if(!isWineGlass)
            chosenGlass = new DrinkingGlass(thisActivity, chosenViewWidth, chosenViewHeight, chosenGlassWidth, chosenGlassHeight, (chosenViewWidth-chosenGlassWidth)/2, (chosenViewHeight-chosenGlassHeight)/2, false, true, chosenGlassMlSize);
        else
            chosenGlass = new WineGlass(thisActivity, chosenViewWidth, chosenViewHeight, chosenGlassWidth, chosenGlassHeight, (chosenViewWidth-chosenGlassWidth)/2, (chosenViewHeight-chosenGlassHeight)/2, false, true, chosenGlassMlSize);

        RelativeLayout.LayoutParams chosenGlassParams = new RelativeLayout.LayoutParams(chosenGlass.getLayoutParams());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, shotGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, waterGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, pintGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, wineGlass.getId());
        chosenGlass.setLayoutParams(chosenGlassParams);
        chosenGlass.setId(chosenGlassViewId);

        rl.addView(chosenGlass);
        glass.setIsChosen(true);
        glass.invalidate();
    }
    /*
    * Creates a glass of the following shape:
    * \       /
    *  \     /
    *   \___/
    * */
    public class DrinkingGlass extends Glass implements View.OnTouchListener{
        private Line leftEdge;
        private Line rightEdge;
        private float leftTopEdgeX, leftTopEdgeY, rightTopEdgeX, rightTopEdgeY, leftBottomEdgeX,
                leftBottomEdgeY, rightBottomEdgeY, rightBottomEdgeX;
        private float drinkLeftBottomEdgeX, drinkRightBottomEdgeX, drinkBottomEdgeY, drinkTopEdgeY,
                drinkLeftTopEdgeX, drinkRightTopEdgeX;

        public DrinkingGlass(Context context, int viewWidth, int viewHeight, int glassHeight, int glassWidth, int glassX, int glassY, int mlSize){
            this(context, viewWidth, viewHeight, glassHeight, glassWidth, glassX, glassY,(glassHeight/2)-(glassHeight/3), false, false, mlSize);
        }
        public DrinkingGlass(Context context, int viewWidth, int viewHeight, int glassHeight, int glassWidth, int glassX, int glassY, boolean isChosen, int mlSize){
            this(context, viewWidth, viewHeight, glassHeight, glassWidth, glassX, glassY,(glassHeight/2)-(glassHeight/3), isChosen, false, mlSize);
        }
        public DrinkingGlass(Context context, int viewWidth, int viewHeight, int glassHeight, int glassWidth, int glassX, int glassY, boolean isChosen, boolean isMainView, int mlSize){
            this(context, viewWidth, viewHeight, glassHeight, glassWidth, glassX, glassY,(glassHeight/2)-(glassHeight/3), isChosen, isMainView, mlSize);
        }

        /*
        * glassX - where the top-left edge of the glass is to be drawn at the x-axis
        * glassY - where the top-left edge of the glass is to be drawn at the y-axis
        * glassWidth - measured at the glass' widest point, i.e. from the top-left edge to the top-right edge
        * glassHeight - measured from the top to the bottom of the glass
        * edgeSlope - slope of the edge of the glass, i.e. how skew is it supposed to be
        * */
        public DrinkingGlass(Context context, int viewWidth, int viewHeight, int glassWidth, int glassHeight, int glassX, int glassY, float m, boolean isChosen, boolean isMainView, int mlSize){
            super(context, viewWidth, viewHeight, glassWidth, glassHeight, glassX, glassY, glassHeight, isChosen, isMainView, mlSize);
            this.setOnTouchListener(this);

            leftEdge = new Line(super.getGlassX(), super.getGlassY(), 10f);
            rightEdge = new Line(super.getGlassX()+super.getGlassWidth(), super.getGlassY(), -10f);

            //draw glass
            leftTopEdgeX = super.getGlassX();
            leftTopEdgeY = super.getGlassY();

            rightTopEdgeX = leftTopEdgeX + super.getGlassWidth();
            rightTopEdgeY = leftTopEdgeY;

            leftBottomEdgeY = leftTopEdgeY + super.getGlassHeight();
            leftBottomEdgeX = leftEdge.calculateX(leftBottomEdgeY);

            rightBottomEdgeY = leftBottomEdgeY;
            rightBottomEdgeX = rightEdge.calculateX(rightBottomEdgeY);


            drinkLeftBottomEdgeX = leftBottomEdgeX + super.getStrokeWidth();
            drinkRightBottomEdgeX = rightBottomEdgeX - super.getStrokeWidth();
            drinkBottomEdgeY = leftBottomEdgeY - super.getStrokeWidth();
            drinkTopEdgeY = drinkBottomEdgeY - super.getMaxDrinkHeight() + 10;
            drinkLeftTopEdgeX = leftEdge.calculateX(drinkTopEdgeY) + super.getStrokeWidth();
            drinkRightTopEdgeX = rightEdge.calculateX(drinkTopEdgeY) - super.getStrokeWidth();
        }

        @Override
        public void onDraw(Canvas canvas){
            super.onDraw(canvas);

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);

            //left edge of the glass
            canvas.drawLine(leftTopEdgeX, leftTopEdgeY, leftBottomEdgeX, leftBottomEdgeY, super.getGlassPaint());
            //right edge of the glass
            canvas.drawLine(rightTopEdgeX, rightTopEdgeY, rightBottomEdgeX, rightBottomEdgeY, super.getGlassPaint());
            //bottom edge of the glass
            canvas.drawLine(leftBottomEdgeX, leftBottomEdgeY, rightBottomEdgeX, rightBottomEdgeY, super.getGlassPaint());

            if(super.isMainView()) {
            /*FILLING OUT THE GLASS*/
                paint.setColor(DRINK_COLOUR);
                paint.setStyle(Paint.Style.FILL);

                drinkLeftTopEdgeX = leftEdge.calculateX(drinkTopEdgeY) + super.getStrokeWidth();
                drinkRightTopEdgeX = rightEdge.calculateX(drinkTopEdgeY) - super.getStrokeWidth();

                Path p = new Path();
                p.moveTo(drinkLeftTopEdgeX, drinkTopEdgeY);
                p.lineTo(drinkLeftBottomEdgeX, drinkBottomEdgeY);
                p.lineTo(drinkRightBottomEdgeX, drinkBottomEdgeY);
                p.lineTo(drinkRightTopEdgeX, drinkTopEdgeY);
                canvas.drawPath(p, paint);
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                if(super.isChosen())
                    setPreviousTouchY(motionEvent.getRawY());
                return true;
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                if(super.isMainView()) {
                    float newDrinkTopEdgeY = drinkTopEdgeY + (motionEvent.getRawY() - getPreviousTouchY());
                    if (drinkBottomEdgeY > newDrinkTopEdgeY && newDrinkTopEdgeY > (drinkBottomEdgeY - super.getMaxDrinkHeight())) {
                        drinkTopEdgeY = newDrinkTopEdgeY;
                        invalidate();
                    }
                    setPreviousTouchY(motionEvent.getRawY());
                }
                return true;
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (!super.isChosen() && !super.isMainView()) {
                    System.out.println(motionEvent.getRawY() +" < " + super.getHeight() + " && " + "0 < " + motionEvent.getRawY() + " && " + motionEvent.getRawX() + " < " + super.getWidth() + " && 0 < "+motionEvent.getRawX());
                    if (motionEvent.getY() < super.getHeight() && 0 < motionEvent.getY() && motionEvent.getX() < super.getWidth() && 0 < motionEvent.getX()) {
                        newChosenGlass(this, false);
                    }
                }
            }
            System.out.println(MotionEvent.actionToString(motionEvent.getAction()));
            return false;
        }
    }

    /*
    * Glass of the following shape:
    * \  /
    *  \/
    *  |
    * ---
    * */
    public class WineGlass extends Glass implements View.OnTouchListener{
        private Line leftEdge, rightEdge;
        private float leftTopEdgeX = super.getGlassX();
        float leftTopEdgeY = super.getGlassY();

        float rightTopEdgeX, rightTopEdgeY, leftMiddleEdgeY, leftMiddleEdgeX, rightMiddleEdgeX,
                rightMiddleEdgeY, centerMiddleEdgeX, centerMiddleEdgeY, leftBottomEdgeX,
                leftBottomEdgeY, rightBottomEdgeX, rightBottomEdgeY, centerBottomEdgeX,
                centerBottomEdgeY;

        private float drinkTopEdgeY, drinkLeftTopEdgeX, drinkRightTopEdgeX, drinkBottomEdgeY, drinkLeftBottomEdgeX, drinkRightBottomEdgeX;

        public WineGlass(Context context, int viewWidth, int viewHeight, int glassWidth, int glassHeight, int glassX, int glassY, int mlSize){
            this(context, viewWidth, viewHeight, glassWidth, glassHeight, glassX, glassY, false, false, mlSize);
        }
        public WineGlass(Context context, int viewWidth, int viewHeight, int glassWidth, int glassHeight, int glassX, int glassY, boolean isChosen, boolean isMainView, int mlSize) {
            super(context, viewWidth, viewHeight, glassWidth, glassHeight, glassX, glassY, glassHeight / 2, isChosen, isMainView, mlSize);
            this.setOnTouchListener(this);

            leftEdge = new Line(super.getGlassX(), super.getGlassY(), 10);

            leftTopEdgeX = super.getGlassX();
            leftTopEdgeY = super.getGlassY();

            rightTopEdgeX = leftTopEdgeX + super.getGlassWidth() ;
            rightTopEdgeY = leftTopEdgeY;

            leftMiddleEdgeY = leftTopEdgeY + (super.getGlassHeight()/2);
            leftMiddleEdgeX = leftEdge.calculateX(leftMiddleEdgeY);
            rightMiddleEdgeX = leftMiddleEdgeX + super.getGlassWidth() - 2*(leftMiddleEdgeX - leftTopEdgeX);
            rightMiddleEdgeY = leftMiddleEdgeY;
            centerMiddleEdgeX =(rightMiddleEdgeX - leftMiddleEdgeX)/2 + leftMiddleEdgeX;
            centerMiddleEdgeY = leftMiddleEdgeY;

            leftBottomEdgeX = leftMiddleEdgeX;
            leftBottomEdgeY = leftTopEdgeY + super.getGlassHeight();
            rightBottomEdgeX = rightMiddleEdgeX;
            rightBottomEdgeY = leftBottomEdgeY;
            centerBottomEdgeX = centerMiddleEdgeX;
            centerBottomEdgeY = leftBottomEdgeY;

            drinkBottomEdgeY = leftMiddleEdgeY - super.getStrokeWidth();
            drinkTopEdgeY = drinkBottomEdgeY - super.getMaxDrinkHeight() + 0.05f*super.getMaxDrinkHeight();
            drinkLeftBottomEdgeX = leftMiddleEdgeX + super.getStrokeWidth();
            drinkRightBottomEdgeX = rightMiddleEdgeX - super.getStrokeWidth();

            rightEdge = new Line(rightTopEdgeX, rightTopEdgeY, -10);
        }

        @Override
        public void onDraw(Canvas canvas){
            super.onDraw(canvas);


            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            //Left edge of the glass
            canvas.drawLine(leftTopEdgeX, leftTopEdgeY, leftMiddleEdgeX, leftMiddleEdgeY, super.getGlassPaint());
            //Right edge of the glass
            canvas.drawLine(rightTopEdgeX, rightTopEdgeY, rightMiddleEdgeX, rightMiddleEdgeY, super.getGlassPaint());
            //Glass bottom
            canvas.drawLine(leftMiddleEdgeX, leftMiddleEdgeY, rightMiddleEdgeX, rightMiddleEdgeY, super.getGlassPaint());
            //Leg of the glass
            canvas.drawLine(centerMiddleEdgeX, centerMiddleEdgeY, centerBottomEdgeX, centerBottomEdgeY, super.getGlassPaint());
            //Foot of the glass
            canvas.drawLine(leftBottomEdgeX, leftBottomEdgeY, rightBottomEdgeX, rightBottomEdgeY, super.getGlassPaint());

            if(super.isMainView()) {
            /*FILLING OUT THE GLASS*/
                paint.setColor(DRINK_COLOUR);
                paint.setStyle(Paint.Style.FILL);

                drinkLeftTopEdgeX = leftEdge.calculateX(drinkTopEdgeY) + super.getStrokeWidth();
                drinkRightTopEdgeX = rightEdge.calculateX(drinkTopEdgeY) - super.getStrokeWidth();

                Path p = new Path();
                p.moveTo(drinkLeftTopEdgeX, drinkTopEdgeY);
                p.lineTo(drinkLeftBottomEdgeX, drinkBottomEdgeY);
                p.lineTo(drinkRightBottomEdgeX, drinkBottomEdgeY);
                p.lineTo(drinkRightTopEdgeX, drinkTopEdgeY);
                canvas.drawPath(p, paint);
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                if(super.isMainView())
                    setPreviousTouchY(motionEvent.getRawY());
                return true;
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                if(super.isMainView()) {
                    float newDrinkTopEdgeY = drinkTopEdgeY + (motionEvent.getRawY() - getPreviousTouchY());
                    if (drinkBottomEdgeY > newDrinkTopEdgeY && newDrinkTopEdgeY > (drinkBottomEdgeY - super.getMaxDrinkHeight())) {
                        drinkTopEdgeY = newDrinkTopEdgeY;
                        invalidate();
                    }
                    setPreviousTouchY(motionEvent.getRawY());
                }
                return true;
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                System.out.println("isChosen: " + isChosen() + ";  isMainView: " + isMainView());
                if (!super.isChosen() && !super.isMainView()) {
                    System.out.println(motionEvent.getRawY() +" < " + super.getHeight() + " && " + "0 < " + motionEvent.getRawY() + " && " + motionEvent.getRawX() + " < " + super.getWidth() + " && 0 < "+motionEvent.getRawX());
                    if (motionEvent.getY() < super.getHeight() && 0 < motionEvent.getY() && motionEvent.getX() < super.getWidth() && 0 < motionEvent.getX()) {
                        newChosenGlass(this, true);
                    }
                }
            }
            System.out.println(MotionEvent.actionToString(motionEvent.getAction()));
            return false;
        }

    }

    /*
    * super class for all the other types of glasses. All of them will share those properties.
    * */
    public class Glass extends View{
        private int glassHeight, glassWidth, strokeWidth;
        private float x, y;
        private float maxDrinkHeight;//Represents the maximum height of a fluid that can be stored in the glass.
        private boolean isChosen, isMainView;
        private float previousTouchY;
        public Paint paint;
        public int mlSize;

        public Glass(Context context, int viewWidth, int viewHeight, int glassWidth, int glassHeight, float x, float y, float maxDrinkHeight, int mlSize){
            this(context, viewWidth, viewHeight, glassWidth, glassHeight, x, y, maxDrinkHeight, false, false, mlSize);
        }
        public Glass(Context context, int viewWidth, int viewHeight, int glassWidth, int glassHeight, float x, float y, float maxDrinkHeight, boolean isChosen, int mlSize){
            this(context, viewWidth, viewHeight, glassWidth, glassHeight, x, y, maxDrinkHeight, isChosen, false, mlSize);
        }
        public Glass(Context context, int viewWidth, int viewHeight, int glassWidth, int glassHeight, float x, float y, float maxDrinkHeight, boolean isChosen, boolean isMainView, int mlSize){
            super(context);
            this.glassWidth = glassWidth;
            this.glassHeight = glassHeight;
            this.strokeWidth = 10;
            this.x = x;
            this.y = y;
            this.maxDrinkHeight = maxDrinkHeight - this.strokeWidth;
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(this.strokeWidth);
            paint.setColor(Color.BLACK);
            this.isChosen = isChosen;
            this.isMainView = isMainView;
            this.mlSize = mlSize;

            this.setLayoutParams(new RelativeLayout.LayoutParams(viewWidth, viewHeight));
        }


        public void setGlassHeight(int glassHeight){this.glassHeight = glassHeight;}
        public void setGlassWidth(int glassWidth){this.glassWidth = glassWidth;}
        public void setGlassPaint(Paint p){paint = p;}
        public void setGlassX(int x){this.x = x;}
        public void setGlassY(int y){this.y = y;}
        public void setMaxDrinkHeight(float height){this.maxDrinkHeight = height;}
        public void setPreviousTouchY(float y){previousTouchY = y;}
        public void setMlSize(int mlSize){this.mlSize = mlSize;}
        public float getGlassHeight(){return glassHeight;}
        public float getGlassWidth(){return glassWidth;}
        public float getGlassX(){return x;}
        public float getGlassY(){return y;}
        public Paint getGlassPaint(){return paint;}
        public int getStrokeWidth(){return strokeWidth;}
        public float getMaxDrinkHeight(){return maxDrinkHeight;}
        public float getPreviousTouchY(){return previousTouchY;}
        public int getMlSize(){return mlSize;}

        public boolean isChosen(){return isChosen;}
        public void setIsChosen(boolean isChosen){this.isChosen = isChosen;}
        public boolean isMainView(){return isMainView;}
        public void setIsMainView(boolean isMainView){this.isMainView = isMainView;}
    }

    public class Line{
        private boolean slopeUndefined;
        private float m;
        private float c;

        public Line(float x1, float y1, float m){
            this.m = m;
            this.c = y1 - (this.m * x1);
            slopeUndefined = false;
        }
        public Line(float m, float c){
            this.m = m;
            this.c = c;
            slopeUndefined = false;
        }
        public Line(float x1, float y1, float x2, float y2){
            this.m = (y2 - y1) / (x2 - x1);
            this.c = y1 - (this.m * x1);
        }
        public Line(float c, boolean slopeUndefined) throws java.lang.Exception{
            if(!slopeUndefined)
                throw new java.lang.Exception("Line must either have an undefined slope or a valid slope value!");

            this.slopeUndefined = slopeUndefined;
            //NOTE: as of now the class will not work properly with an undefined slope! i.e.: vertical lines
        }

        float calculateX(float y){return ((y-this.c)/this.m);}
        float calculateY(float x){return (this.m*x + this.c);}

        float getC(){return this.c;}
        void setC(float c){this.c = c;}

        float getM(){return this.m;}
        void setM(float m){this.m = m;}
    }
}