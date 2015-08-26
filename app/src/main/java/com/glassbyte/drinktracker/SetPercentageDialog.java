package com.glassbyte.drinktracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Maciej on 25/08/2015.
 */
public class SetPercentageDialog extends DialogFragment {
    private SetPercentageDialogListener listener = null;
    private NumberPicker mostSignificantDigit, sndMostSignificantDigit, sndLeastSignificantDigit, leastSignificantDigit;

    public interface SetPercentageDialogListener{
        void onDoneClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.setPercentage_dialog_title)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDoneClick(SetPercentageDialog.this);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
        RelativeLayout percentagePicker = new RelativeLayout(this.getActivity());
        percentagePicker.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams percentagePickerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        percentagePicker.setLayoutParams(percentagePickerParams);

        mostSignificantDigit = new NumberPicker(this.getActivity());
        mostSignificantDigit.setMinValue(0);
        mostSignificantDigit.setMaxValue(9);
        RelativeLayout.LayoutParams msdParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        msdParams.addRule(RelativeLayout.ALIGN_START, RelativeLayout.TRUE);
        mostSignificantDigit.setLayoutParams(msdParams);
        mostSignificantDigit.setId(View.generateViewId());

        sndMostSignificantDigit = new NumberPicker(this.getActivity());
        sndMostSignificantDigit.setMinValue(0);
        sndMostSignificantDigit.setMaxValue(9);
        RelativeLayout.LayoutParams smsdParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        smsdParams.addRule(RelativeLayout.RIGHT_OF, mostSignificantDigit.getId());
        sndMostSignificantDigit.setLayoutParams(smsdParams);
        sndMostSignificantDigit.setId(View.generateViewId());

        TextView dot = new TextView(this.getActivity());
        dot.setTypeface(null, Typeface.BOLD);
        dot.setText(".");
        dot.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams dotParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dotParams.addRule(RelativeLayout.RIGHT_OF, sndMostSignificantDigit.getId());
        dotParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        dot.setLayoutParams(dotParams);
        dot.setId(View.generateViewId());

        sndLeastSignificantDigit = new NumberPicker(this.getActivity());
        sndLeastSignificantDigit.setMinValue(0);
        sndLeastSignificantDigit.setMaxValue(9);
        RelativeLayout.LayoutParams slsdParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slsdParams.addRule(RelativeLayout.RIGHT_OF, dot.getId());
        sndLeastSignificantDigit.setLayoutParams(slsdParams);
        sndLeastSignificantDigit.setId(View.generateViewId());

        leastSignificantDigit = new NumberPicker(this.getActivity());
        leastSignificantDigit.setMinValue(0);
        leastSignificantDigit.setMaxValue(9);
        RelativeLayout.LayoutParams lsdParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lsdParams.addRule(RelativeLayout.RIGHT_OF, sndLeastSignificantDigit.getId());
        leastSignificantDigit.setLayoutParams(lsdParams);

        percentagePicker.addView(mostSignificantDigit);
        percentagePicker.addView(sndMostSignificantDigit);
        percentagePicker.addView(dot);
        percentagePicker.addView(sndLeastSignificantDigit);
        percentagePicker.addView(leastSignificantDigit);

        builder.setView(percentagePicker);

        return builder.create();
    }

    public float getPercentage(){
        return mostSignificantDigit.getValue()*10 + sndMostSignificantDigit.getValue() +
                sndLeastSignificantDigit.getValue()*0.1f + leastSignificantDigit.getValue() * 0.01f;
    }

    public void setSetPercentageDialogListener(SetPercentageDialogListener listener){
        this.listener = listener;
    }
}
