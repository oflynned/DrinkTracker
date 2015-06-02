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

        ViewGroup V = (ViewGroup) inflater.inflate(
                R.layout.activity_customdrink, container, false);


        RelativeLayout l = (RelativeLayout) V.findViewById(R.id.customDrinkLayout);


        //Create shot glass instance
        Glass shotGlass = new DrinkingGlass(this.getActivity(),500);
        RelativeLayout.LayoutParams shotGlassParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        shotGlassParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
        shotGlass.setLayoutParams(shotGlassParams);
        shotGlass.setId(SHOT_GLASS_ID);

        //Create wine glass instance
        Glass wineGlass = new WineGlass(this.getActivity(),500);
        RelativeLayout.LayoutParams wineGlassParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        wineGlassParams.addRule(RelativeLayout.RIGHT_OF, shotGlass.getId());
        wineGlass.setLayoutParams(wineGlassParams);
        wineGlass.setId(WINE_GLASS_ID);

        V.addView(shotGlass);
        V.addView(wineGlass);

        System.out.println("Shot Glass ID: "+shotGlass.getId());
        System.out.println("Wine Glass ID: "+wineGlass.getId());
        return V;
    }

    /*
    * Creates a glass of the following shape:
    * \       /
    *  \     /
    *   \___/
    * */
    public class DrinkingGlass extends Glass implements View.OnTouchListener{

        public DrinkingGlass(Context context, float height){
            this(context,height, height/3, (height/2)-(height/3));
        }

        public DrinkingGlass(Context context, float height, float width, float dBottomTopWidth){
            super(context, height, width, dBottomTopWidth);
        }

        @Override
        public void onDraw(Canvas canvas){

            //left edge of the glass
            canvas.drawLine(getX(), getY(), getX()+(getGlassDBottomTopWidth()/2), getY()+getGlassHeight(), getGlassPaint());
            //right edge of the glass
            canvas.drawLine(getX()+(getGlassDBottomTopWidth()*2)+getGlassWidth(), getY(), getX()+getGlassDBottomTopWidth()+getGlassWidth(), getY()+getGlassHeight(), getGlassPaint());
            //bottom edge of the glass
            canvas.drawLine(getX()+(getGlassDBottomTopWidth()/2), getY()+getGlassHeight(), getX()+(getGlassDBottomTopWidth())+getGlassWidth(), getY()+getGlassHeight(), getGlassPaint());
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

        public WineGlass(Context context, float height){
            this(context,height, height/2);
        }

        public WineGlass (Context context, float height, float width){
            super(context, height, width, 0);
        }

        @Override
        public void onDraw(Canvas canvas){
            //Left edge of the glass
            canvas.drawLine(getX(), getY(), getX()+getGlassWidth()/2, getY()+(getGlassHeight()/2), getGlassPaint());
            //Right edge of the glass
            canvas.drawLine(getX()+getGlassWidth(), getY(), getX()+(getGlassWidth()/2), getY()+(getGlassHeight()/2), getGlassPaint());
            //Leg of the glass
            canvas.drawLine(getX()+(getGlassWidth()/2), getY()+(getGlassHeight()/2), getX()+(getGlassWidth()/2), getY()+getGlassHeight(), getGlassPaint());
            //Foot of the glass
            canvas.drawLine(getX(), getY()+getGlassHeight(), getX()+getGlassWidth(), getY()+getGlassHeight(), getGlassPaint());
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
        private float height, width, dBottomTopWidth, strokeWidth;
        private Paint paint;

        public Glass(Context context, float height, float width, float dBottomTopWidth){
            super(context);
            this.height = height;
            this.width = width;
            this.dBottomTopWidth = dBottomTopWidth;
            this.strokeWidth = 10;

            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(this.strokeWidth);
            paint.setColor(Color.BLACK);
        }


        public void setGlassHeight(float height){this.height = height;}
        public void setGlassWidth(float width){this.width = width;}
        public void setGlassDBottomTopWidth(float dBottomTopWidth){this.dBottomTopWidth = dBottomTopWidth;}
        public void setGlassPaint(Paint p){paint = p;}
        public float getGlassHeight(){return height;}
        public float getGlassWidth(){return width;}
        public float getGlassDBottomTopWidth(){return dBottomTopWidth;}
        public Paint getGlassPaint(){return paint;}
    }
}
