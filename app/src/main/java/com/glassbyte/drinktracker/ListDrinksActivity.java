package com.glassbyte.drinktracker;

import android.content.Context;
import android.database.Cursor;
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

public class ListDrinksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_drinks);

        GridView gridView = (GridView)findViewById(R.id.listDrinksGridView);
        gridView.setAdapter(new DrinksListAdapter(this));
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

    public class DrinksListAdapter extends BaseAdapter{
        private Context mContext;
        private DatabaseOperationsUnits dou;
        private Cursor result;
        private final int NUM_COLUMNS = 5;

        public DrinksListAdapter(Context c){
            mContext = c;
            dou = new DatabaseOperationsUnits(mContext);
        }

        @Override
        public int getCount() {
            result = dou.getReadableDatabase().rawQuery("SELECT * FROM " + DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME, null);
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
            View test = null;
            if (i < NUM_COLUMNS) {
                test = new TextView(mContext);
               switch (i) {
                   case 0:
                       ((TextView)test).setText("Select");
                       break;
                   case 1:
                       ((TextView)test).setText("Date"); 
                       break;
                   case 2:
                       ((TextView)test).setText("Title");
                       break;
                   case 3:
                       ((TextView)test).setText("Volume");
                       break;
                   case 4:
                       ((TextView)test).setText("AlcVol");
                       break;
               }
                   
            } else {
                if ((i % NUM_COLUMNS) == 0) {
                    test = new CheckBox(mContext);
                }
                else {
                    test = new TextView(mContext);
                    ((TextView) test).setText("Pos: " + i);
                }
            }
            return test;
        }
    }
}
