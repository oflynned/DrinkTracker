package com.glassbyte.drinktracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by ed on 09/09/15.
 */
public class WarningDialog {

    public boolean warning1, warning2, warning3, warning4;
    private int notification_id = 1;
    Context context;
    InterstitialAd mInterstitialAd;



    public WarningDialog (Context context) {
        this.context = context;
    }

    public void displayWarning(String warningTier){

        String warning = "";
        Vibrator v;

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

        switch(warningTier){

            /*
            * 4 tiers of warnings:
            * 1: You're enroute to getting drunk, maybe take it easy if you're feeling a bit intoxicated
            * 2: You've reached quite a drunk stage, watch yourself and make sure you don't lose personal belongings
            * 3: Hangover-ville
            * 4: You're extremely drunk, drink any more and you're endangering yourself!
            *
            * 1:0.07
            * 2:0.13
            * 3:0.17
            * 4:0.22
            * */

            case "1":
                warning = context.getResources().getString(R.string.warning1);
                dialogNotification(warning);
                notify(1, R.drawable.indicator_dot_white, context.getResources().getString(R.string.notice), warning);
                v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(300);
                break;
            case "2":
                warning = context.getResources().getString(R.string.warning2);
                dialogNotification(warning);
                notify(1, R.drawable.indicator_dot_white, context.getResources().getString(R.string.notice), warning);
                v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(600);
                break;
            case "3":
                warning = context.getResources().getString(R.string.warning3);
                dialogNotification(warning);
                notify(1, R.drawable.indicator_dot_white, context.getResources().getString(R.string.notice), warning);
                v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(900);
                break;
            case "4":
                warning = context.getResources().getString(R.string.warning4);
                dialogNotification(warning);
                notify(1, R.drawable.indicator_dot_white, context.getResources().getString(R.string.notice), warning);
                v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1200);
                break;
        }
    }

    public void dialogNotification(String warning){
        new AlertDialog.Builder(context)
                //set title
                .setTitle(R.string.warning)
                        //depending on BAC we set the tier
                .setMessage(warning)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (mInterstitialAd.isLoaded())
                        {
                            mInterstitialAd.show();
                        }

                    }
                })
                .show();
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void notify(int visibility, int icon, String title, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Notification notification = new NotificationCompat.Builder(context)
                    .setCategory(Notification.CATEGORY_ALARM)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(icon)
                    .setAutoCancel(true)
                    .setVisibility(visibility).build();
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notification_id, notification);
        } else {
            Notification notification  = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(icon)
                    .setAutoCancel(true)
                    .setVisibility(visibility).build();
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notification_id, notification);
        }
    }

    public void setWarning1(boolean warning1){
        this.warning1 = warning1;
    }

    public void setWarning2(boolean warning2){
        this.warning2 = warning2;
    }

    public void setWarning3(boolean warning3){
        this.warning3 = warning3;
    }

    public void setWarning4(boolean warning4){
        this.warning4 = warning4;
    }

    public boolean getWarning1(){
        return warning1;
    }

    public boolean getWarning2(){
        return warning2;
    }

    public boolean getWarning3(){
        return warning3;
    }

    public boolean getWarning4(){
        return warning4;
    }
}
