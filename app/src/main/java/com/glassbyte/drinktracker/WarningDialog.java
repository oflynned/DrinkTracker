package com.glassbyte.drinktracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ed on 09/09/15.
 */
public class WarningDialog {

    public boolean warning1, warning2, warning3, warning4;
    public final int NOTIFICATION_ID = 1;
    Context context;
    InterstitialAd mInterstitialAd;

    public AlertDialog alertDialog;

    public WarningDialog(Context context) {
        this.context = context;
    }

    public static Bitmap getLargeIcon(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_beer);
    }

    public void displayWarning(String warningTier) {

        String warning;
        Vibrator v;

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

        switch (warningTier) {

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
                notify(1, R.drawable.ic_error_outline_white_48dp, context.getResources().getString(R.string.notice), warning);
                v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(300);
                break;
            case "2":
                warning = context.getResources().getString(R.string.warning2);
                dialogNotification(warning);
                notify(1, R.drawable.ic_error_outline_white_48dp, context.getResources().getString(R.string.notice), warning);
                v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(600);
                break;
            case "3":
                warning = context.getResources().getString(R.string.warning3);
                dialogNotification(warning);
                notify(1, R.drawable.ic_error_outline_white_48dp, context.getResources().getString(R.string.notice), warning);
                v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(900);
                break;
            case "4":
                warning = context.getResources().getString(R.string.warning4);
                dialogNotification(warning);
                notify(1, R.drawable.ic_error_outline_white_48dp, context.getResources().getString(R.string.notice), warning);
                v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1200);
                break;
        }
    }

    public void dialogNotification(String warning) {
        alertDialog = new AlertDialog.Builder(context)
                //set title
                .setTitle(R.string.warning)
                        //depending on BAC we set the tier
                .setMessage(warning)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Random rand = new Random();
                        int number = rand.nextInt(3);
                        if (number == 0) {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
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
            // Creates an explicit intent for an ResultActivity to receive.

            Notification notification = new NotificationCompat.Builder(context)
                    .setCategory(Notification.CATEGORY_EVENT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(icon)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                    .setLargeIcon(getLargeIcon(context))
                    .setAutoCancel(true)
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(icon)
                    .setLargeIcon(getLargeIcon(context))
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                    .setVisibility(visibility)
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    public void setWarning1(boolean warning1) {
        this.warning1 = warning1;
    }

    public void setWarning2(boolean warning2) {
        this.warning2 = warning2;
    }

    public void setWarning3(boolean warning3) {
        this.warning3 = warning3;
    }

    public void setWarning4(boolean warning4) {
        this.warning4 = warning4;
    }

    public boolean getWarning1() {
        return warning1;
    }

    public boolean getWarning2() {
        return warning2;
    }

    public boolean getWarning3() {
        return warning3;
    }

    public boolean getWarning4() {
        return warning4;
    }
}
