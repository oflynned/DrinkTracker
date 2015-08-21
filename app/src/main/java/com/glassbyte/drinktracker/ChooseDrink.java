package com.glassbyte.drinktracker;
/*
* To do:
* -change all the fonts and dimensions to be expressed in dp or dpi
* -display actual current EBAC
* */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by Maciej on 27/05/15.
 */

public class ChooseDrink extends Fragment implements BloodAlcoholContent.OnBloodAlcoholContentChangeListener{
    private SelectionSideBar leftSideBar, rightSideBar;
    private TextView bacDisplay;
    private final int BAC_DECIMAL_PLACES = 2;
    private final int BAC_FONT_SIZE= 40;
    private final int SIDE_BAR_WIDTH = 200;
    private BloodAlcoholContent bloodAlcoholContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*Set up the BloodAlcoholLevel object*/
        bloodAlcoholContent = new BloodAlcoholContent(this.getActivity());
        bloodAlcoholContent.setOnBloodAlcoholContentChangeListener(this);
        /*End of Set up the BloodAlcoholLevel object*/

        RelativeLayout rl = new RelativeLayout(this.getActivity());
        rl.setBackgroundColor(Color.WHITE);

        leftSideBar = new SelectionSideBar(this.getActivity(), true);
        rightSideBar = new SelectionSideBar(this.getActivity(), false);

        RelativeLayout.LayoutParams leftSideBarParams = new RelativeLayout.LayoutParams(SIDE_BAR_WIDTH, RelativeLayout.LayoutParams.MATCH_PARENT);
        leftSideBarParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftSideBar.setLayoutParams(leftSideBarParams);
        leftSideBar.setId(View.generateViewId());

        RelativeLayout.LayoutParams rightSideBarParams = new RelativeLayout.LayoutParams(SIDE_BAR_WIDTH, RelativeLayout.LayoutParams.MATCH_PARENT);
        rightSideBarParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightSideBar.setLayoutParams(rightSideBarParams);
        rightSideBar.setId(View.generateViewId());

        bacDisplay = new TextView(this.getActivity());
        bacDisplay.setTextSize(BAC_FONT_SIZE);
        bacDisplay.setTextColor(Color.BLACK);
        bacDisplay.setGravity(Gravity.CENTER);
        bacDisplay.setText("Your\ncurrent\nBAC is:\n" + BloodAlcoholContent.round(bloodAlcoholContent.getCurrentEbac(), BAC_DECIMAL_PLACES));
        RelativeLayout.LayoutParams bacDisplayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        bacDisplayParams.addRule(RelativeLayout.LEFT_OF, rightSideBar.getId());
        bacDisplayParams.addRule(RelativeLayout.RIGHT_OF, leftSideBar.getId());
        bacDisplayParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        bacDisplayParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bacDisplay.setLayoutParams(bacDisplayParams);

        ImageView swipeArrowView = new ImageView(this.getActivity());
        swipeArrowView.setImageDrawable(this.getActivity().getDrawable(R.drawable.swipe_arrow));
        RelativeLayout.LayoutParams swipeArrowViewParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        swipeArrowViewParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        swipeArrowViewParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        swipeArrowView.setLayoutParams(swipeArrowViewParam);

        rl.addView(leftSideBar);
        rl.addView(rightSideBar);
        rl.addView(bacDisplay);
        rl.addView(swipeArrowView);
        return rl;
    }

    @Override
    public void onBloodAlcoholContentChange() {
        bacDisplay.setText("Your\ncurrent\nBAC is:\n" + BloodAlcoholContent.round(bloodAlcoholContent.getCurrentEbac(), BAC_DECIMAL_PLACES));
        bacDisplay.invalidate();
    }

    public class SelectionSideBar extends View{
        private boolean isLeft;
        private Context mContext;
        private Paint textPaint;
        private final String LEFT_SIDE_BAR_TEXT = "PRESET DRINKS";
        private final String RIGHT_SIDE_BAR_TEXT = "CUSTOM DRINKS";
        private final int FONT_SIZE = 60;

        public SelectionSideBar(Context c, boolean isLeft){
            super(c);
            mContext = c;
            this.isLeft = isLeft;
            if(isLeft)
                this.setBackground(c.getDrawable(R.drawable.choose_drink_left_side_bg));
            else
                this.setBackground(c.getDrawable(R.drawable.choose_drink_right_side_bg));

            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(FONT_SIZE);
            textPaint.setFakeBoldText(true);
        }

        @Override
        protected void onDraw(Canvas c){
            super.onDraw(c);

            if(isLeft) {
                float textWidth = textPaint.measureText(LEFT_SIDE_BAR_TEXT);
                c.translate(SIDE_BAR_WIDTH/2-FONT_SIZE/2, this.getHeight()/2-textWidth/2);
                c.rotate(90);
                c.drawText(LEFT_SIDE_BAR_TEXT, 0, 0, textPaint);
            } else {
                float textWidth = textPaint.measureText(RIGHT_SIDE_BAR_TEXT);
                c.translate(SIDE_BAR_WIDTH/2+FONT_SIZE/2, this.getHeight()/2+textWidth/2);
                c.rotate(-90);
                c.drawText(RIGHT_SIDE_BAR_TEXT,0,0, textPaint);
            }
            c.restore();
        }
    }
}
