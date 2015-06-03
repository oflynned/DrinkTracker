package com.glassbyte.drinktracker;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by root on 27/05/15.
 */
public class CustomDrink extends Fragment {
    private final int SHOT_GLASS_ID = 9;
    private final int WINE_GLASS_ID = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RelativeLayout rl = new RelativeLayout(this.getActivity());
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rl.setPadding(16,16,16,16);
        rl.setLayoutParams(rlParams);

        //Create shot glass instance
        Glass shotGlass = new DrinkingGlass(this.getActivity(),500);
        RelativeLayout.LayoutParams shotGlassParams = new RelativeLayout.LayoutParams(shotGlass.getLayoutParams());
        shotGlassParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        shotGlass.setLayoutParams(shotGlassParams);
        shotGlass.setBackgroundColor(Color.GREEN);
        shotGlass.setId(View.generateViewId());

        //Create wine glass instance
        Glass wineGlass = new WineGlass(this.getActivity(),300);
        RelativeLayout.LayoutParams wineGlassParams = new RelativeLayout.LayoutParams(wineGlass.getLayoutParams());
        wineGlassParams.addRule(RelativeLayout.RIGHT_OF, shotGlass.getId());
        wineGlass.setLayoutParams(wineGlassParams);
        wineGlass.setBackgroundColor(Color.BLUE);
        wineGlass.setId(View.generateViewId());

        rl.addView(shotGlass);
        rl.addView(wineGlass);


        return rl;
    }

    /*
    * Creates a glass of the following shape:
    * \       /
    *  \     /
    *   \___/
    * */
    public class DrinkingGlass extends Glass implements View.OnTouchListener{

        public DrinkingGlass(Context context, int height){
            this(context,height, height/3, (height/2)-(height/3));
        }

        public DrinkingGlass(Context context, int height, int width, int dBottomTopWidth){
            super(context, height, width, dBottomTopWidth);
        }

        @Override
        public void onDraw(Canvas canvas){
            super.onDraw(canvas);
            int x = getStrokeWidth()/2;
            int y = getStrokeWidth()/2;

            //left edge of the glass
            canvas.drawLine(x, 0, x+(getGlassDBottomTopWidth()/2), y+getGlassHeight(), getGlassPaint());
            //right edge of the glass
            canvas.drawLine(getGlassWidth()+getGlassDBottomTopWidth()*2-x, 0, getGlassDBottomTopWidth()+getGlassWidth(), y+getGlassHeight(), getGlassPaint());
            //bottom edge of the glass
            canvas.drawLine(x+getGlassDBottomTopWidth()/2, getGlassHeight()-y, getGlassDBottomTopWidth()+getGlassWidth(), getGlassHeight()-y, getGlassPaint());
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
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

        public WineGlass(Context context, int height){
            this(context,height, height/2);
        }

        public WineGlass (Context context, int height, int width){
            super(context, height, width, 0);
        }

        @Override
        public void onDraw(Canvas canvas){
            super.onDraw(canvas);
            int x = getStrokeWidth()/2;
            int y = getStrokeWidth()/2;

            //Left edge of the glass
            canvas.drawLine(x, 0, getGlassWidth()/2, getGlassHeight()/2, getGlassPaint());
            //Right edge of the glass
            canvas.drawLine(getGlassWidth()-x, 0, getGlassWidth()/2, getGlassHeight()/2, getGlassPaint());
            //Leg of the glass
            canvas.drawLine(getGlassWidth()/2, getGlassHeight()/2, getGlassWidth()/2, getGlassHeight()-y, getGlassPaint());
            //Foot of the glass
            canvas.drawLine(x, getGlassHeight()-y, getGlassWidth()-x, getGlassHeight()-y, getGlassPaint());
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
        private int height, width, dBottomTopWidth, strokeWidth;
        private Paint paint;

        public Glass(Context context, int height, int width, int dBottomTopWidth){
            super(context);
            this.height = height;
            this.width = width;
            this.dBottomTopWidth = dBottomTopWidth;
            this.strokeWidth = 10;

            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(this.strokeWidth);
            paint.setColor(Color.BLACK);

            //32 is for padding!!! 16 on each side
            this.setLayoutParams(new RelativeLayout.LayoutParams((int) width+2*(int)dBottomTopWidth, (int) height));
        }


        public void setGlassHeight(int height){this.height = height;}
        public void setGlassWidth(int width){this.width = width;}
        public void setGlassDBottomTopWidth(int dBottomTopWidth){this.dBottomTopWidth = dBottomTopWidth;}
        public void setGlassPaint(Paint p){paint = p;}
        public int getGlassHeight(){return height;}
        public int getGlassWidth(){return width;}
        public int getGlassDBottomTopWidth(){return dBottomTopWidth;}
        public Paint getGlassPaint(){return paint;}
        public int getStrokeWidth(){return strokeWidth;}
    }
}
