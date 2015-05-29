package com.glassbyte.drinktracker;

/**
 * Created by ed on 25/05/15.
 * Edited by Alex on 25/05/15
 */
import android.app.Activity;
import android.os.Bundle;

import org.achartengine.GraphicalView;


public class GraphsActivity extends Activity{
//called when activity is first created

    private static GraphicalView view;
    private DisplayActivity line1 = new DisplayActivity();
    private static Thread thread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);


        thread =new Thread(){
            public void run() {
                for (int i = 0; i < 31; i++)
                {
                   try
                   {
                       Thread.sleep(1000);
                   }
                   catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    Point p = MockData.getDataFromReceiver(i);
                    line1.addNewPoints(p);
                    view.repaint();
                }
            }

        };
        thread.start();

    }

    @Override
    protected void onStart(){
        super.onStart();
        view = line1.getView(this);
        setContentView(view);
    }
}
