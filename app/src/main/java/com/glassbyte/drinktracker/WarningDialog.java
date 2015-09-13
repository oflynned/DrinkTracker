package com.glassbyte.drinktracker;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by ed on 09/09/15.
 */
public class WarningDialog extends DialogFragment {

    public void displayWarning(String tier){

        String warning = "";
        switch(tier){

            /*4 tiers of warnings:
            * 1:
            * */

            case "tier0":
                warning = getString(R.string.tier0);
                break;
            case "tier1":
                warning = getString(R.string.tier1);
                break;
            case "tier2":
                warning = getString(R.string.tier2);
                break;
            case "tier3":
                warning = getString(R.string.tier3);
                break;
            case "tier4":
                warning = getString(R.string.tier4);
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
}
