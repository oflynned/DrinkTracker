package com.glassbyte.drinktracker;

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
import android.widget.Toast;

import java.util.Random;

/**
 * Created by root on 27/05/15.
 */
public class CustomDrink extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup V = (ViewGroup) inflater.inflate(
                R.layout.activity_customdrink, container, false);

        //Create shot glass instance
        Glass shotGlass = new Glass(this.getActivity(),500);
        V.addView(shotGlass);

        return V;
    }

    /*
    * Creates a glass of the following shape:
    * \       /
    *  \     /
    *   \___/
    *
    * */
    public class Glass extends View implements View.OnTouchListener{
        private float height, width, dBottomTopWidth, strokeWidth;
        private Paint paint;

        public Glass(Context context, float height){
            this(context,height, height/3, (height/2)-(height/3));
        }

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

        @Override
        public void onDraw(Canvas canvas){

            //left edge of the glass
            canvas.drawLine(getX(), getY(), getX()+(getGlassDBottomTopWidth()/2), getY()+getGlassHeight(), paint);
            //right edge of the glass
            canvas.drawLine(getX()+(getGlassDBottomTopWidth()*2)+getGlassWidth(), getY(), getX()+getGlassDBottomTopWidth()+getGlassWidth(), getY()+getGlassHeight(), paint);
            //bottom edge of the glass
            canvas.drawLine(getX()+(getGlassDBottomTopWidth()/2), getY()+getGlassHeight(), getX()+(getGlassDBottomTopWidth())+getGlassWidth(), getY()+getGlassHeight(), paint);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }

        public void setGlassHeight(float height){this.height = height;}
        public void setGlassWidth(float width){this.width = width;}
        public void setGlassDBottomTopWidth(float dBottomTopWidth){this.dBottomTopWidth = dBottomTopWidth;}
        public float getGlassHeight(){return height;}
        public float getGlassWidth(){return width;}
        public float getGlassDBottomTopWidth(){return dBottomTopWidth;}
    }
}
