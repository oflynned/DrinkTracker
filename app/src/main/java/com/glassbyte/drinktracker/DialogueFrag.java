package com.glassbyte.drinktracker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ed on 31/05/15.
 */
public class DialogueFrag extends DialogFragment {

    public DialogueFrag() {
    }

    Button buttonDismiss;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialogue_water,
                new LinearLayout(getActivity()), false);

        // Retrieve layout elements
        TextView title = (TextView) view.findViewById(R.id.dialogTitle);

        // Set values
        title.setText("Take a Break");

        // Build dialog
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);

        buttonDismiss = (Button) view.findViewById(R.id.btnDismiss);
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return builder;
    }
}
