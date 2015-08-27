package com.glassbyte.drinktracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Maciej on 27/08/2015.
 */
public class ImperialEditHeightDialog extends DialogPreference implements DialogInterface.OnClickListener{
    private EditText feetEdit, inchesEdit;
    private SharedPreferences sp;
    private Context mContext;

    public ImperialEditHeightDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTitle("Edit Height");
        this.setPositiveButtonText("Done");
        this.setDialogLayoutResource(R.layout.dialog_imperial_edit_height);

        mContext = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        double heightCm = Double.valueOf(
                sp.getString(
                        mContext.getString(R.string.pref_key_editHeight),"")
        );

        double[] feetAndInches =
                BloodAlcoholContent.MetricSystemConverter.converCmToFeetAndInches(heightCm);

        int feet = (int)BloodAlcoholContent.round(feetAndInches[0],0);
        int inches = (int)BloodAlcoholContent.round(feetAndInches[1],0);

        feetEdit = (EditText)view.findViewById(R.id.edittextfeet);
        inchesEdit = (EditText)view.findViewById(R.id.edittextinches);

        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (!positiveResult)
            return;

        int feet = Integer.valueOf(feetEdit.getText().toString());
        int inches = Integer.valueOf(inchesEdit.getText().toString());
        double[] feetAndInches = new double[2];
        feetAndInches[0] = feet;
        feetAndInches[1] = inches;

        double newHeightInCm = BloodAlcoholContent.MetricSystemConverter.convertFeetAndInchesToCm(feetAndInches);
        int newHeightInCmRounded = (int)BloodAlcoholContent.round(newHeightInCm,0);

        SharedPreferences.Editor e = sp.edit();
        e.putString(mContext.getString(R.string.pref_key_editHeight),String.valueOf(newHeightInCmRounded));
        e.apply();

        this.setSummary(feet + " feet and " + inches + " inches");
    }
}