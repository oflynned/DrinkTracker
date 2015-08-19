package com.glassbyte.drinktracker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class ListDrinksActivity extends AppCompatActivity {
    DrinksListAdapter drinkListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_drinks);

        GridView gridView = (GridView)findViewById(R.id.listDrinksGridView);

        drinkListAdapter = new DrinksListAdapter(this);

        gridView.setAdapter(drinkListAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(ListDrinksActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
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

    public void selectAll(View view){
        /*CheckBox[] checkboxes = drinkListAdapter.getCheckboxes();

        for (CheckBox checkbox: checkboxes) {
            checkbox.setEnabled(true);
        }
        */
        System.out.println("was here");
        drinkListAdapter.selectAllCheckboxes();
    }

    public void removeSelected(View view){

    }

    public class DrinksListAdapter extends BaseAdapter{
        private Context mContext;
        private DatabaseOperationsUnits dou;
        private Cursor result;
        private final int NUM_COLUMNS = 5;
        private ArrayList<CheckBox> drinkCheckboxes;

        public DrinksListAdapter(Context c){
            mContext = c;
            dou = new DatabaseOperationsUnits(mContext);
            drinkCheckboxes = new ArrayList<>();
            result = dou.getReadableDatabase().rawQuery("SELECT * FROM " + DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME, null);
            result.moveToFirst();
        }

        @Override
        public int getCount() {
            return result.getCount() * 5 + NUM_COLUMNS; //5 columns to be displayed in the list for each row in the database + 5 for the top row
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View cellView = null;

            /*if (!result.isBeforeFirst())
                result.moveToFirst();
            */
            if (i < NUM_COLUMNS) {
                cellView = new TextView(mContext);
               switch (i) {
                   case 0:
                       ((TextView)cellView).setText("Select");
                       break;
                   case 1:
                       ((TextView)cellView).setText("Date");
                       break;
                   case 2:
                       ((TextView)cellView).setText("Title");
                       break;
                   case 3:
                       ((TextView)cellView).setText("Volume");
                       break;
                   case 4:
                       ((TextView)cellView).setText("AlcVol");
                       break;
               }

                ((TextView)cellView).setAllCaps(true);
                ((TextView)cellView).setTypeface(null, Typeface.BOLD);

            } else {
                result.moveToPosition(i/NUM_COLUMNS-1);
                if ((i % NUM_COLUMNS) == 0) {
                    cellView = new CheckBox(mContext);
                    int rowId = result.getInt(0); //index 0 == id column
                    cellView.setId(rowId);
                    drinkCheckboxes.add((CheckBox)cellView);
                }
                else {
                    int colPos = i%NUM_COLUMNS;
                    cellView = new TextView(mContext);
                    ((TextView) cellView).setText(result.getString(colPos)
                            + ((colPos == 3) ? "ml" : "")
                            + ((colPos == 4) ? "%" : ""));

                    if((i % NUM_COLUMNS) == (NUM_COLUMNS-1))
                        result.moveToNext();
                }
            }
            return cellView;
        }

        public CheckBox[] getCheckboxes(){
            CheckBox[] drinkCheckboxesArray = new CheckBox[drinkCheckboxes.size()];
            drinkCheckboxesArray = drinkCheckboxes.toArray(drinkCheckboxesArray);
            return drinkCheckboxesArray;
        }

        public void selectAllCheckboxes(){
            boolean alreadyAllSelected = true;
            Iterator<CheckBox> itr = drinkCheckboxes.iterator();
            while(itr.hasNext()){
                CheckBox checkbox = itr.next();
                if(!checkbox.isChecked()){
                    alreadyAllSelected = false;
                    checkbox.setChecked(true);
                }
            }

            //If all were already selected then deselect all
            if(alreadyAllSelected){
                itr = drinkCheckboxes.iterator();
                while(itr.hasNext()){
                    itr.next().setChecked(false);
                }
            }
        }
    }
}
