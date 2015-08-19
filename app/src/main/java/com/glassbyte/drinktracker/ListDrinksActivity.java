package com.glassbyte.drinktracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListDrinksActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DrinksListAdapter drinkListAdapter;
    private GridView gridView;
    private Spinner spinner;
    private final int[] MAX_DISPLAY_SPINNER_ITEMS = {5,10};
    private int displayLimit = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_drinks);

        gridView = (GridView)findViewById(R.id.listDrinksGridView);

        drinkListAdapter = new DrinksListAdapter(this, displayLimit);

        gridView.setAdapter(drinkListAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(ListDrinksActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        List<String> list = new ArrayList<String>();
        for (int num : MAX_DISPLAY_SPINNER_ITEMS) {
            list.add(Integer.toString(num));
        }
        spinner = (Spinner)findViewById(R.id.max_display_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
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
        drinkListAdapter.selectAllCheckboxes();
    }

    public void removeSelected(View view){
        if(drinkListAdapter.removeSelected()) {
            gridView.invalidateViews();
            Toast.makeText(this, "Selected rows were deleted successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selection = (String)spinner.getItemAtPosition(i);
        displayLimit = Integer.valueOf(selection);
        drinkListAdapter.setDisplayLimit(displayLimit);
        gridView.invalidateViews();

        System.out.println("Seleceted: " + selection);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class DrinksListAdapter extends BaseAdapter{
        private Context mContext;
        private DatabaseOperationsUnits dou;
        private Cursor result;
        private final int NUM_COLUMNS = 5;
        private ArrayList<CheckBox> drinkCheckboxes;
        private final String SELECT_ALL_SQL_QUERY = "SELECT * FROM " + DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME;
        private String queryWithLimit;
        private SQLiteDatabase db;
        private int displayLimit, currentPage;
        private int numOfGridColumns;


        public DrinksListAdapter(Context c, int displayLimit){
            mContext = c;
            dou = new DatabaseOperationsUnits(mContext);
            drinkCheckboxes = new ArrayList<>();
            db = dou.getReadableDatabase();

            this.displayLimit = displayLimit;

            queryWithLimit = SELECT_ALL_SQL_QUERY + " LIMIT " + displayLimit+1 + " OFFSET " + currentPage*displayLimit;
            result = db.rawQuery(queryWithLimit, null);
            currentPage=0;

            if(result.getCount() > displayLimit) {
                numOfGridColumns = displayLimit * NUM_COLUMNS + NUM_COLUMNS * 2; //2 aditional rows one for headings and the other for next and previous page buttons
            } else {
                numOfGridColumns = (result.getCount()-1) * NUM_COLUMNS + NUM_COLUMNS;
            }
        }

        @Override
        public int getCount() {
            return numOfGridColumns;
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

            } else if (i < (NUM_COLUMNS + displayLimit*NUM_COLUMNS)) {
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
            } else {
                //else this is the last row that will contain button in the first and last cell
                if(i%NUM_COLUMNS==0){
                    Button previous = new Button(mContext);
                    if(currentPage==0)
                        previous.setEnabled(false);
                    previous.setText("Previous");
                    cellView = previous;
                } else if (i%NUM_COLUMNS==NUM_COLUMNS-1) {
                    Button next = new Button(mContext);
                    next.setText("Next");
                    cellView = next;
                } else {
                    cellView = new View(mContext);
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

        //returns whether any checkboxes were selected hence whether anything got removed
        public boolean removeSelected(){
            System.out.println("Entered the removeSelected function");
            Iterator<CheckBox> itr = drinkCheckboxes.iterator();
            ArrayList<Integer> selectedCheckboxesIds = new ArrayList<>();
            while(itr.hasNext()){
                CheckBox cb = itr.next();
                if(cb.isChecked()){
                    selectedCheckboxesIds.add(cb.getId());
                }
            }
            if (selectedCheckboxesIds.size()>0) {
                String sqlQuery = "DELETE FROM " + DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME
                        + " WHERE " + DataUnitsDatabaseContractor.DataLoggingTable._ID + "=";
                Iterator<Integer> idsItr = selectedCheckboxesIds.iterator();
                db = dou.getWritableDatabase();
                while (idsItr.hasNext()) {
                    String tmpQuery = sqlQuery + idsItr.next();
                    System.out.println("Executed DELETE query: " + tmpQuery);
                    db.execSQL(tmpQuery);
                }
                db.close();

                db = dou.getReadableDatabase();
                result = db.rawQuery(queryWithLimit, null);
                return true;
            }
            return false;
        }
        
        public void setDisplayLimit(int limit){
            displayLimit = limit;
            currentPage = 0;
            queryWithLimit = SELECT_ALL_SQL_QUERY + " LIMIT " + displayLimit+1 + " OFFSET " + currentPage*displayLimit;
            db.close();
            db = dou.getReadableDatabase();
            result = db.rawQuery(queryWithLimit, null);
            if(result.getCount() > displayLimit) {
                numOfGridColumns = displayLimit * NUM_COLUMNS + NUM_COLUMNS * 2; //2 aditional rows one for headings and the other for next and previous page buttons
            } else {
                numOfGridColumns = (result.getCount()-1) * NUM_COLUMNS + NUM_COLUMNS;
            }


        }
    }
}
