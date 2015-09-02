package com.glassbyte.drinktracker;
/*
* To do:
* - there is a bug in that when remove selected is selected the arraylist of checkboxes contains double
* copy for each checkbox however everything is made to work fine around that bug but be warned that
* such bug exists
* */
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
            drinkListAdapter.clearDrinkCheckboxes();
            gridView.invalidateViews();
            Toast.makeText(this, "Selected rows were deleted successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selection = (String)spinner.getItemAtPosition(i);
        displayLimit = Integer.valueOf(selection);
        drinkListAdapter.setDisplayLimit(displayLimit);
        drinkListAdapter.clearDrinkCheckboxes();
        gridView.invalidateViews();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class DrinksListAdapter extends BaseAdapter{
        private Context mContext;
        private DrinkTrackerDbHelper dou;
        private Cursor result;
        private final int NUM_COLUMNS = 5;
        private ArrayList<CheckBox> drinkCheckboxes;
        private final String SELECT_ALL_SQL_QUERY = "SELECT * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME;
        private String queryWithLimit;
        private SQLiteDatabase db;
        private int displayLimit, currentPage;
        private int numOfGridColumns;
        private BaseAdapter thisBaseAdapter;


        public DrinksListAdapter(Context c, int displayLimit){
            thisBaseAdapter = this;

            mContext = c;
            dou = new DrinkTrackerDbHelper(mContext);
            drinkCheckboxes = new ArrayList<>();
            db = dou.getReadableDatabase();

            this.displayLimit = displayLimit;

            queryWithLimit =generateQueryWithLimit();
            result = db.rawQuery(queryWithLimit, null);
            currentPage=0;

            setCount(result);
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
            //drinkCheckboxes = new ArrayList<>();

            if (i < NUM_COLUMNS) {
                //first row to contain headings for the table of drinks
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

            } else if (i < (NUM_COLUMNS + ((result.getCount()>displayLimit)?displayLimit:result.getCount())*NUM_COLUMNS)) {
                /*HORRIBLE CODE, YES I KNOW, I'M SORRY BUT I WAS TIRED AND JUST DIDN'T WANT TO CHANGE EVERYTHING ELSE SO MADE IT WORK LIKE THAT YUUCK!*/
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
                    previous.setText("Previous");
                    previous.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(currentPage!=0) {
                                currentPage--;
                                queryWithLimit = generateQueryWithLimit();
                                db.close();
                                db = dou.getReadableDatabase();
                                result = db.rawQuery(queryWithLimit, null);
                                setCount(result);
                                thisBaseAdapter.notifyDataSetChanged();
                            }
                        }
                    });

                    if(currentPage==0)
                        previous.setEnabled(false);

                    cellView = previous;
                } else if (i%NUM_COLUMNS==NUM_COLUMNS-1) {
                    Button next = new Button(mContext);
                    next.setText("Next");
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currentPage++;
                            queryWithLimit = generateQueryWithLimit();
                            db.close();
                            db = dou.getReadableDatabase();
                            result = db.rawQuery(queryWithLimit, null);
                            setCount(result);
                            thisBaseAdapter.notifyDataSetChanged();
                        }
                    });

                    if (result.getCount() <= displayLimit)
                        next.setEnabled(false);
                    cellView = next;
                } else {
                    cellView = new View(mContext);
                }
            }
            return cellView;
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
                System.out.println("CheckBox Id: "+cb.getId());
                if(cb.isChecked()) {
                    selectedCheckboxesIds.add(cb.getId());
                }
            }
            if (selectedCheckboxesIds.size()>0) {
                dou.removeDrinks(mContext, selectedCheckboxesIds.toArray(new Integer[selectedCheckboxesIds.size()]));

                //the below 2 lines are necessary to update the data set of the gridview
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
            setCount(result);
        }

        //Sets count for the gridview based on the results from a query where each row represents a drink
        public void setCount(Cursor c){
            if(c.getCount() > displayLimit || currentPage !=0) {
                int numOfDrinksToDisplay = (c.getCount() > displayLimit) ? displayLimit : c.getCount();
                numOfGridColumns = numOfDrinksToDisplay * NUM_COLUMNS + NUM_COLUMNS * 2; //2 aditional rows one for headings and the other for next and previous page buttons
            } else {
                numOfGridColumns = c.getCount() * NUM_COLUMNS + NUM_COLUMNS;
            }
        }

        public String generateQueryWithLimit(){
            return SELECT_ALL_SQL_QUERY + " LIMIT " + displayLimit+1 + " OFFSET " + currentPage*displayLimit;
        }

        public void clearDrinkCheckboxes(){drinkCheckboxes.clear();}
    }
}
