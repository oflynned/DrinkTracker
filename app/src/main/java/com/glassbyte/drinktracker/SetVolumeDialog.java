package com.glassbyte.drinktracker;

//DRINK VOLUME NOT AUDIO VOLUME - LOL

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
public class SetVolumeDialog extends DialogFragment {
    private SetVolumeDialogListener listener = null;
    private NumberPicker mostSignificantDigit, sndMostSignificantDigit, sndLeastSignificantDigit, leastSignificantDigit;

    public interface SetVolumeDialogListener{
        void onDoneClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.setVolume_dialog_title)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDoneClick(SetVolumeDialog.this);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        RelativeLayout volumePicker = new RelativeLayout(this.getActivity());
        volumePicker.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams volumePickerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        volumePicker.setLayoutParams(volumePickerParams);

        mostSignificantDigit = new NumberPicker(this.getActivity());
        mostSignificantDigit.setMinValue(0);
        mostSignificantDigit.setMaxValue(9);
        RelativeLayout.LayoutParams msdParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        msdParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
        mostSignificantDigit.setLayoutParams(msdParams);
        mostSignificantDigit.setId(View.generateViewId());

        sndMostSignificantDigit = new NumberPicker(this.getActivity());
        sndMostSignificantDigit.setMinValue(0);
        sndMostSignificantDigit.setMaxValue(9);
        RelativeLayout.LayoutParams smsdParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        smsdParams.addRule(RelativeLayout.RIGHT_OF, mostSignificantDigit.getId());
        sndMostSignificantDigit.setLayoutParams(smsdParams);
        sndMostSignificantDigit.setId(View.generateViewId());

        sndLeastSignificantDigit = new NumberPicker(this.getActivity());
        sndLeastSignificantDigit.setMinValue(0);
        sndLeastSignificantDigit.setMaxValue(9);
        RelativeLayout.LayoutParams slsdParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slsdParams.addRule(RelativeLayout.RIGHT_OF, sndMostSignificantDigit.getId());
        sndLeastSignificantDigit.setLayoutParams(slsdParams);
        sndLeastSignificantDigit.setId(View.generateViewId());

        TextView dot = new TextView(this.getActivity());
        dot.setTypeface(null, Typeface.BOLD);
        dot.setText(".");
        dot.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams dotParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dotParams.addRule(RelativeLayout.RIGHT_OF, sndLeastSignificantDigit.getId());
        dotParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        dot.setLayoutParams(dotParams);
        dot.setId(View.generateViewId());

        leastSignificantDigit = new NumberPicker(this.getActivity());
        leastSignificantDigit.setMinValue(0);
        leastSignificantDigit.setMaxValue(9);
        RelativeLayout.LayoutParams lsdParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lsdParams.addRule(RelativeLayout.RIGHT_OF, dot.getId());
        leastSignificantDigit.setLayoutParams(lsdParams);
        leastSignificantDigit.setId(View.generateViewId());

        TextView units = new TextView(this.getActivity());
        units.setTypeface(null, Typeface.BOLD);
        units.setText("ml");
        units.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams unitsParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        unitsParams.addRule(RelativeLayout.RIGHT_OF, leastSignificantDigit.getId());
        unitsParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        units.setLayoutParams(unitsParams);
        units.setId(View.generateViewId());

        volumePicker.addView(mostSignificantDigit);
        volumePicker.addView(sndMostSignificantDigit);
        volumePicker.addView(sndLeastSignificantDigit);
        volumePicker.addView(dot);
        volumePicker.addView(leastSignificantDigit);
        volumePicker.addView(units);

        builder.setView(volumePicker);

        return builder.create();
    }

    public float getVolume(){
        return mostSignificantDigit.getValue()*100f + sndMostSignificantDigit.getValue()*10f +
                sndLeastSignificantDigit.getValue() + leastSignificantDigit.getValue() * 0.1f;
    }

    public void setSetVolumeDialogListener(SetVolumeDialogListener listener){
        this.listener = listener;
    }
}