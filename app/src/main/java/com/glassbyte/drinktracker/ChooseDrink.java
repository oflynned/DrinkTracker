package com.glassbyte.drinktracker;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by root on 27/05/15.
 */
/*
public class ChooseDrink extends Fragment implements View.OnClickListener{

    private DatabaseOperationsUnits DOU;
    private Cursor CR;
    protected NotificationManager NM;
    protected Vibrator v;

    //random seed
    Random random = new Random();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup V = (ViewGroup) inflater.inflate(
                R.layout.activity_choosedrink, container, false);

        Button btnDatabase = (Button) V.findViewById(R.id.btnGraphs);
        Button btnGraphs = (Button) V.findViewById(R.id.btnDatabase);
        Button btnAddDBMember = (Button) V.findViewById(R.id.btnAddDBMember);
        Button btnInvokeVibration = (Button) V.findViewById(R.id.btnVibrate);
        Button btnDialogue = (Button) V.findViewById(R.id.btnDialogue);
        Button btnPushNot = (Button) V.findViewById(R.id.btnPushNot);

        btnGraphs.setOnClickListener(this);
        btnDatabase.setOnClickListener(this);
        btnAddDBMember.setOnClickListener(this);
        btnInvokeVibration.setOnClickListener(this);
        btnDialogue.setOnClickListener(this);
        btnPushNot.setOnClickListener(this);

        NM = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        v = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        random.setSeed(123456789);

        return V;
    }

    public int getRandom(int i) {
        return random.nextInt(i);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnDatabase:
                intent = new Intent(getActivity(), AndroidDatabaseManager.class);
                startActivity(intent);
                break;
            case R.id.btnGraphs:
                intent = new Intent(getActivity(), PreferencesActivity.class);
                startActivity(intent);
                break;
            case R.id.btnAddDBMember:
                //add items to database
                DatabaseOperationsUnits DOU = new DatabaseOperationsUnits(getActivity());
                Cursor CR = DOU.getInfo(DOU);

                CR.moveToLast();
                DOU.insertNewDrink(
                        DOU.getDateTime(), //time
                        "title",
                        getRandom(10), //units of alcohol
                        getRandom(10), //percentage
                        getRandom(10) //bac
                );

                CR.moveToNext(); //increment table
                CR.close();
                Toast.makeText(getActivity(), String.valueOf(CR.getCount()), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnVibrate:
                v.vibrate(500);
                break;
            case R.id.btnDialogue:
                DialogueFrag dialog = new DialogueFrag();
                dialog.show(getActivity().getFragmentManager(), "MyDialogFragment");
                break;
            case R.id.btnPushNot:
                //random debug
                int notification = getRandom(4);

                String banner = "";
                String title = "";
                String body = "";
                int severity = 0;

                switch (notification) {
                    case 0:
                        banner = "Unfit to drive!";
                        title = "Don't drive!";
                        body = "Hail a taxi or ask a sober friend to drive you home.";
                        severity = 500;
                        break;
                    case 1:
                        banner = "Slow down! Take a break and drink some water.";
                        title = "Slow down drinking!";
                        body = "Take a break and drink some water.";
                        severity = 1000;
                        break;
                    case 2:
                        banner = "Stop! Take a long break and drink some water.";
                        title = "Take a break from drinking!";
                        body = "Consider taking a break soon.";
                        severity = 2000;
                        break;
                    case 3:
                        banner = "No more drinking! You've had too much.";
                        title = "No more drinking! You've had too much! ";
                        body = "Ensure a friend looks out for you.";
                        severity = 3000;
                        break;
                }

                //vibration didn't work on cheap tablet
                v.vibrate(severity);

                Notification notify = new Notification(R.drawable.glass_water, banner, System.currentTimeMillis());
                PendingIntent pending = PendingIntent.getActivity(getActivity(), 0, new Intent(), 0);
                notify.setLatestEventInfo(getActivity(), title, body, pending);
                NM.notify(0, notify);
                break;
        }
    }
}*/

public class ChooseDrink extends Fragment {
    private SelectionSideBar leftSideBar, rightSideBar;
    private final int SIDE_BAR_WIDTH = 90;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout rl = new RelativeLayout(this.getActivity());
        rl.setBackgroundColor(Color.WHITE);

        leftSideBar = new SelectionSideBar(this.getActivity(), true);
        rightSideBar = new SelectionSideBar(this.getActivity(), false);

        RelativeLayout.LayoutParams leftSideBarParams = new RelativeLayout.LayoutParams(SIDE_BAR_WIDTH, RelativeLayout.LayoutParams.MATCH_PARENT);
        leftSideBarParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftSideBar.setLayoutParams(leftSideBarParams);

        RelativeLayout.LayoutParams rightSideBarParams = new RelativeLayout.LayoutParams(SIDE_BAR_WIDTH, RelativeLayout.LayoutParams.MATCH_PARENT);
        rightSideBarParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightSideBar.setLayoutParams(rightSideBarParams);

        rl.addView(leftSideBar);
        rl.addView(rightSideBar);
        return rl;
    }

    public class SelectionSideBar extends View{
        private boolean isLeft;
        private Context mContext;

        public SelectionSideBar(Context c, boolean isLeft){
            super(c);
            mContext = c;
            this.isLeft = isLeft;
            if(isLeft)
                this.setBackground(c.getDrawable(R.drawable.choose_drink_left_side_bg));
            else
                this.setBackground(c.getDrawable(R.drawable.choose_drink_right_side_bg));
        }

        @Override
        protected void onDraw(Canvas c){
            super.onDraw(c);
        }
    }
}
