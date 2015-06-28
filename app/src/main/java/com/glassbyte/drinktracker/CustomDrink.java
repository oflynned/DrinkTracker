package com.glassbyte.drinktracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.graphics.Path;

/**
 * Created by Maciej on 27/05/15.
 */
public class CustomDrink extends Fragment {
    private final int NUMBER_OF_GLASSES = 4;
    private final int PADDING = 16;

    private int previewHeight, previewWidth,
            chosenViewWidth, chosenViewHeight,
            shotGlassWidth, shotGlassHeight,
            waterGlassWidth, waterGlassHeight,
            pintGlassWidth, pintGlassHeight,
            wineGlassWidth, wineGlassHeight,
            chosenGlassWidth, chosenGlassHeight;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Display display = this.getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        previewWidth = screenWidth/NUMBER_OF_GLASSES;
        previewHeight = screenHeight/4;

        chosenViewWidth = screenWidth - PADDING*2;
        chosenViewHeight = screenHeight - previewHeight - PADDING*2;

        shotGlassWidth = previewWidth/4;
        shotGlassHeight = previewHeight/4;

        waterGlassWidth = previewWidth/3;
        waterGlassHeight = previewHeight/3;

        pintGlassWidth = previewWidth/2;
        pintGlassHeight = previewHeight/2;

        wineGlassWidth = previewWidth/2;
        wineGlassHeight = previewHeight/2;

        System.out.println("Width: " + size.x);
        System.out.println("Width: " + size.x);
        System.out.println("Width: " + size.x);
        System.out.println("Height: " + size.y);
        System.out.println("Height: " + size.y);
        System.out.println("Height: "+size.y);


        RelativeLayout rl = new RelativeLayout(this.getActivity());
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rl.setBackground(getResources().getDrawable(R.drawable.bg3));
        rl.setPadding(PADDING, PADDING, PADDING, PADDING);
        rl.setLayoutParams(rlParams);

        //Create shot glass instance
        Glass shotGlass = new DrinkingGlass(this.getActivity(), previewWidth, previewHeight, shotGlassWidth, shotGlassHeight, (previewWidth-shotGlassWidth)/2, (previewHeight-shotGlassHeight)/2);
        RelativeLayout.LayoutParams shotGlassParams = new RelativeLayout.LayoutParams(shotGlass.getLayoutParams());
        shotGlassParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        shotGlassParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        shotGlass.setLayoutParams(shotGlassParams);
        shotGlass.setBackgroundColor(Color.BLUE);
        shotGlass.setId(View.generateViewId());

        Glass waterGlass = new DrinkingGlass(this.getActivity(), previewWidth, previewHeight, waterGlassWidth, waterGlassHeight, (previewWidth-waterGlassWidth)/2, (previewHeight-waterGlassHeight)/2);
        RelativeLayout.LayoutParams waterGlassParams = new RelativeLayout.LayoutParams(waterGlass.getLayoutParams());
        waterGlassParams.addRule(RelativeLayout.RIGHT_OF, shotGlass.getId());
        waterGlassParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        waterGlass.setLayoutParams(waterGlassParams);
        waterGlass.setBackgroundColor(Color.GREEN);
        waterGlass.setId(View.generateViewId());

        Glass pintGlass = new DrinkingGlass(this.getActivity(), previewWidth, previewHeight, pintGlassWidth, pintGlassHeight, (previewWidth-pintGlassWidth)/2, (previewHeight-pintGlassHeight)/2);
        RelativeLayout.LayoutParams pintGlassParams = new RelativeLayout.LayoutParams(pintGlass.getLayoutParams());
        pintGlassParams.addRule(RelativeLayout.RIGHT_OF, waterGlass.getId());
        pintGlassParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        pintGlass.setLayoutParams(pintGlassParams);
        pintGlass.setId(View.generateViewId());

        //Create wine glass instance
        Glass wineGlass = new WineGlass(this.getActivity(), previewWidth, previewHeight, wineGlassWidth, wineGlassHeight, (previewWidth-wineGlassWidth)/2, (previewHeight-wineGlassHeight)/2);
        RelativeLayout.LayoutParams wineGlassParams = new RelativeLayout.LayoutParams(wineGlass.getLayoutParams());
        wineGlassParams.addRule(RelativeLayout.RIGHT_OF, pintGlass.getId());
        wineGlassParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        wineGlass.setLayoutParams(wineGlassParams);
        wineGlass.setId(View.generateViewId());


        chosenGlassWidth = getChosenGlassWidth(pintGlassWidth);
        chosenGlassHeight = getChosenGlassHeight(pintGlassHeight);
        Glass chosenGlass = new DrinkingGlass(this.getActivity(), chosenViewWidth, chosenViewHeight, chosenGlassWidth, chosenGlassHeight, (chosenViewWidth-chosenGlassWidth)/2, (chosenViewHeight-chosenGlassHeight)/2);
        RelativeLayout.LayoutParams chosenGlassParams = new RelativeLayout.LayoutParams(chosenGlass.getLayoutParams());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, shotGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, waterGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, pintGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.ABOVE, wineGlass.getId());
        chosenGlassParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        chosenGlass.setLayoutParams(chosenGlassParams);
        chosenGlass.setBackgroundColor(Color.CYAN);
        chosenGlass.setId(View.generateViewId());

        rl.addView(shotGlass);
        rl.addView(waterGlass);
        rl.addView(pintGlass);
        rl.addView(wineGlass);
        rl.addView(chosenGlass);
        return rl;
    }

    private int getChosenGlassWidth(int previewGlassWidth){
        return chosenViewWidth/previewWidth*previewGlassWidth;
    }
    private int getChosenGlassHeight(int previewGlassHeight){
        return chosenViewHeight/previewHeight*previewGlassHeight;
    }
    /*
    * Creates a glass of the following shape:
    * \       /
    *  \     /
    *   \___/
    * */
    public class DrinkingGlass extends Glass implements View.OnTouchListener{

        public DrinkingGlass(Context context, int viewWidth, int viewHeight, int glassHeight, int glassWidth, int glassX, int glassY){
            this(context, viewWidth, viewHeight, glassHeight, glassWidth, glassX, glassY,(glassHeight/2)-(glassHeight/3));
        }

        /*
        * glassX - where the top-left edge of the glass is to be drawn at the x-axis
        * glassY - where the top-left edge of the glass is to be drawn at the y-axis
        * glassWidth - measured at the glass' widest point, i.e. from the top-left edge to the top-right edge
        * glassHeight - measured from the top to the bottom of the glass
        * edgeSlope - slope of the edge of the glass, i.e. how skew is it supposed to be
        * */
        public DrinkingGlass(Context context, int viewWidth, int viewHeight, int glassHeight, int glassWidth, int glassX, int glassY, int dBottomTopglassWidth){
            super(context, viewWidth, viewHeight, glassHeight, glassWidth, glassX, glassY, dBottomTopglassWidth);
            this.setOnTouchListener(this);
        }

        @Override
        public void onDraw(Canvas canvas){
            super.onDraw(canvas);

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);

            //left edge of the glass
            canvas.drawLine(getGlassX(), getGlassY(), getGlassX()+getGlassDBottomTopWidth(), getGlassY()+getGlassHeight(), getGlassPaint());
            //right edge of the glass
            canvas.drawLine(getGlassX()+getGlassWidth()+getGlassDBottomTopWidth()*2, getGlassY(), getGlassX()+getGlassDBottomTopWidth()+getGlassWidth(), getGlassY()+getGlassHeight(), getGlassPaint());
            //bottom edge of the glass
            canvas.drawLine(getGlassX() + getGlassDBottomTopWidth(), getGlassY() + getGlassHeight(), getGlassX() + getGlassDBottomTopWidth() + getGlassWidth(), getGlassY() + getGlassHeight(), getGlassPaint());

            /*FILLING OUT THE GLASS*/
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);

            Path p = new Path();
            p.moveTo(20,20);
            p.lineTo(40,200);
            p.lineTo(240, 200);
            p.lineTo(260, 20);
            canvas.drawPath(p, paint);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                System.out.println("X: " + motionEvent.getRawX());
                System.out.println("Y: " + motionEvent.getRawY());
                return true;
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                System.out.println("Y: " + motionEvent.getRawY());
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

        public WineGlass(Context context, int viewWidth, int viewHeight, int glassWidth, int glassHeight, int glassX, int glassY){
            super(context, viewWidth, viewHeight, glassWidth, glassHeight, glassX, glassY, 0);
            this.setOnTouchListener(this);
        }

        @Override
        public void onDraw(Canvas canvas){
            super.onDraw(canvas);

            //Left edge of the glass
            canvas.drawLine(getGlassX(), getGlassY(), getGlassX()+getGlassWidth()/2, getGlassY()+getGlassHeight()/2, getGlassPaint());
            //Right edge of the glass
            canvas.drawLine(getGlassX()+getGlassWidth(), getGlassY(), getGlassX()+getGlassWidth()/2, getGlassY()+getGlassHeight()/2, getGlassPaint());
            //Leg of the glass
            canvas.drawLine(getGlassX()+getGlassWidth()/2, getGlassY()+getGlassHeight()/2, getGlassX()+getGlassWidth()/2, getGlassY()+getGlassHeight(), getGlassPaint());
            //Foot of the glass
            canvas.drawLine(getGlassX(), getGlassY()+getGlassHeight(), getGlassX()+getGlassWidth(), getGlassY()+getGlassHeight(), getGlassPaint());
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }

    }

    /*
    * super class for all the other types of glasses. All of them will share those properties.
    * */
    public class Glass extends View{
        private int glassHeight, glassWidth, dBottomTopWidth, strokeWidth, x, y;
        public Paint paint;

        public Glass(Context context, int viewWidth, int viewHeight, int glassWidth, int glassHeight, int x, int y, int dBottomTopWidth){
            super(context);
            this.glassWidth = glassWidth;
            this.glassHeight = glassHeight;
            this.dBottomTopWidth = dBottomTopWidth;
            this.strokeWidth = 10;
            this.x = x;
            this.y = y;
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(this.strokeWidth);
            paint.setColor(Color.BLACK);

            this.setLayoutParams(new RelativeLayout.LayoutParams(viewWidth, viewHeight));
        }


        public void setGlassHeight(int glassHeight){this.glassHeight = glassHeight;}
        public void setGlassWidth(int glassWidth){this.glassWidth = glassWidth;}
        public void setGlassDBottomTopWidth(int dBottomTopWidth){this.dBottomTopWidth = dBottomTopWidth;}
        public void setGlassPaint(Paint p){paint = p;}
        public void setGlassX(int x){this.x = x;}
        public void setGlassY(int y){this.y = y;}
        public int getGlassHeight(){return glassHeight;}
        public int getGlassWidth(){return glassWidth;}
        public int getGlassX(){return x;}
        public int getGlassY(){return y;}
        public int getGlassDBottomTopWidth(){return dBottomTopWidth;}
        public Paint getGlassPaint(){return paint;}
        public int getStrokeWidth(){return strokeWidth;}
    }
}