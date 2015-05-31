package com.glassbyte.drinktracker;

import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by root on 27/05/15.
 */

public class PresetDrink extends Fragment implements View.OnClickListener{

    private DatabaseOperationsUnits DOU;
    private Cursor CR;
    protected NotificationManager NM;
    protected Vibrator v;

    AlertDialog.Builder alertDialogBuilder;

    //random seed
    Random random = new Random();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.activity_presetdrink, container, false);

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

        NM=(NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        v = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);


        random.setSeed(123456789);

        return V;
    }

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
                DOU.putInfo(
                        DOU,
                        DOU.getDateTime(), //time
                        getRandom(10), //units of alcohol
                        getRandom(10), //percentage
                        getRandom(10) //bac
                );

                CR.moveToNext(); //increment table
                CR.close();
                Toast.makeText(getActivity(),String.valueOf(CR.getCount()), Toast.LENGTH_SHORT).show();
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
                int notification = getRandom(3);

                String banner = "";
                String title = "";
                String body = "";
                int severity = 0;

                switch(notification){
                    case 0:
                        banner = "Stop! Take a break and drink some water.";
                        title = "Stop drinking!";
                        body = "Take a break and drink some water.";
                        severity = 500;
                    break;
                    case 1:
                        banner = "Slow down! Take a break and drink some water.";
                        title = "Slow down drinking!";
                        body = "Consider taking a break soon.";
                        severity = 1500;
                    break;
                    case 2:
                        banner = "No more drinking! You've had too much.";
                        title = "No more drinking! You've had too much! ";
                        body = "Ensure a friend looks out for you.";
                        severity = 3000;
                        break;
                }
                v.vibrate(severity);
                Notification notify=new Notification(R.drawable.glass_water,banner,System.currentTimeMillis());
                PendingIntent pending= PendingIntent.getActivity(getActivity(), 0, new Intent(), 0);
                notify.setLatestEventInfo(getActivity(), title, body,pending);
                NM.notify(0, notify);
                break;
        }
    }

    public int getRandom(int i){
        return random.nextInt(i);
    }
}
