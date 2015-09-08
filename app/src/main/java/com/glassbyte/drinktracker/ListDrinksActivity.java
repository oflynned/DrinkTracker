package com.glassbyte.drinktracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ListDrinksActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //private DrinksListAdapter drinkListAdapter;
    private Spinner spinner;
    private final int[] MAX_DISPLAY_SPINNER_ITEMS = {5,10};
    private int displayLimit, currentPage;
    private DrinkTrackerDbHelper dtDb;
    private SQLiteDatabase readDB;
    private final String query = "SELECT * FROM "+DrinkTrackerDatabase.DrinksTable.TABLE_NAME;
    private ArrayList<CheckBox> checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_drinks);

        checkBoxes = new ArrayList<>();

        displayLimit = 5;
        currentPage = 0;

        List<String> list = new ArrayList<String>();
        for (int num : MAX_DISPLAY_SPINNER_ITEMS) {
            list.add(Integer.toString(num));
        }
        spinner = (Spinner)findViewById(R.id.max_display_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        dtDb = new DrinkTrackerDbHelper(this);
        readDB = dtDb.getReadableDatabase();

        populateTable();

        CheckBox selectAllCb = (CheckBox)findViewById(R.id.drinks_list_select_all_cb);
        selectAllCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean allAlreadyChecked = true;
                    Iterator<CheckBox> itr = checkBoxes.iterator();
                    while (itr.hasNext())
                        itr.next().setChecked(true);

                } else {
                    Iterator<CheckBox> itr = checkBoxes.iterator();
                    while (itr.hasNext())
                        itr.next().setChecked(false);
                }
            }
        });
    }

    //returns true if the table was populate with one or more entries
    public boolean populateTable(){
        Button prevB = (Button)findViewById(R.id.drinks_list_prev_button);
        if (currentPage == 0)
            prevB.setEnabled(false);
        else
            prevB.setEnabled(true);
        prevB.invalidate();

        TableLayout tl = (TableLayout)findViewById(R.id.drinks_list_table);

        int currentRowsNum = tl.getChildCount();
        if (currentRowsNum > 1) {
            tl.removeViewsInLayout(1,currentRowsNum-1);
        }

        String queryWLimit = query + " LIMIT " + Integer.toString(displayLimit+1)//to check if the next page exists; whether the next button should be enabled
                + " OFFSET " + currentPage*displayLimit;
        System.out.println(queryWLimit);
        Cursor c = readDB.rawQuery(queryWLimit, null);

        int rowCount = c.getCount();
        System.out.println("Row Count: "+rowCount);
        Button nextB = (Button)findViewById(R.id.drinks_list_next_button);
        if(rowCount == 0)
            return false;
        else if (rowCount == displayLimit+1) {
            nextB.setEnabled(true);
            nextB.invalidate();

            rowCount = displayLimit;
        } else {
            nextB.setEnabled(false);
            nextB.invalidate();
        }

        c.moveToFirst();

        for (int i=0; i < rowCount; i++){
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams trLP = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(trLP);
            if(i %2 == 0)
                tr.setBackgroundColor(Color.LTGRAY);

            CheckBox cb = new CheckBox(this);
            cb.setId(c.getInt(0));
            checkBoxes.add(cb);

            TextView dateTV = new TextView(this);
            Date date = new Date(c.getLong(1));
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yy HH:mm");
            dateTV.setText(sdf.format(date));
            dateTV.setGravity(Gravity.CENTER);

            TextView titleTV = new TextView(this);
            titleTV.setText(c.getString(2));
            titleTV.setGravity(Gravity.CENTER);

            TextView volumeTV = new TextView(this);
            volumeTV.setText(String.valueOf(c.getInt(3)));
            volumeTV.setGravity(Gravity.CENTER);

            TextView percentageTV = new TextView(this);
            percentageTV.setText(Float.toString(c.getFloat(4)));
            percentageTV.setGravity(Gravity.CENTER);

            tr.addView(cb);
            tr.addView(dateTV);
            tr.addView(titleTV);
            tr.addView(volumeTV);
            tr.addView(percentageTV);

            tl.addView(tr);

            c.moveToNext();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void prevPage(View v){
        currentPage--;
        if (populateTable()) {
            TableLayout tl = (TableLayout) findViewById(R.id.drinks_list_table);
            tl.invalidate();
        }
    }

    public void nextPage(View v){
        currentPage++;
        if (populateTable()) {
            TableLayout tl = (TableLayout) findViewById(R.id.drinks_list_table);
            tl.invalidate();
        }
    }

    public void removeSelected(View view){

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selection = (String)spinner.getItemAtPosition(i);
        displayLimit = Integer.valueOf(selection);
        currentPage = 0;
        if (populateTable()) {
            TableLayout tl = (TableLayout) findViewById(R.id.drinks_list_table);
            tl.invalidate();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
