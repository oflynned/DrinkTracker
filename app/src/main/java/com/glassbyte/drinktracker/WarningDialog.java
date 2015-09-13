package com.glassbyte.drinktracker;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by ed on 09/09/15.
 */
public class WarningDialog extends DialogFragment {

    public boolean warning1, warning2, warning3, warning4;

    public void displayWarning(String warningTier){

        String warning = "";
        switch(warningTier){

            /*4 tiers of warnings:
            * 1: You're enroute to getting drunk, maybe take it easy if you're feeling a bit intoxicated
            * 2: You've reached quite a drunk stage, watch yourself and make sure you don't lose personal belongings
            * 3: Hangover-ville
            * 4: You're extremely drunk, drink any more and you're endangering yourself!
            * */

            case "warning1":
                warning = getString(R.string.warning1);
                break;
            case "warning2":
                warning = getString(R.string.warning2);
                break;
            case "warning3":
                warning = getString(R.string.warning3);
                break;
            case "warning4":
                warning = getString(R.string.warning4);
                break;
        }

        //now case
        new AlertDialog.Builder(getContext())
                //set title
                .setTitle(R.string.warning)
                //depending on BAC we set the tier
                .setMessage(warning)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
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
