package com.glassbyte.drinktracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DatabaseOperationsUnits extends SQLiteOpenHelper {
    public static final int database_version = 2;
    public String CREATE_QUERY =
            "CREATE TABLE " +
                    TableDataUnits.TableInfoUnits.TABLE_NAME +
                    "(" +
                    TableDataUnits.TableInfoUnits.TIME +
                    " TEXT," +
                    TableDataUnits.TableInfoUnits.UNITS +
                    " TEXT," +
                    TableDataUnits.TableInfoUnits.PERCENTAGE +
                    " TEXT," +
                    TableDataUnits.TableInfoUnits.BAC +
                    " TEXT);";
    //; ends query field within query -> ()

    public DatabaseOperationsUnits(Context context) {
        super(context, TableDataUnits.TableInfoUnits.DATABASE_NAME, null, database_version);

        Log.d("Database operations", "Created database successfully");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY); //create table
        Log.d("Database operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //insert data into database
    public void putInfo(DatabaseOperationsUnits DOU, String time, double units, double percentage, double bac){
        SQLiteDatabase SQ = DOU.getWritableDatabase(); //writes data to database
        ContentValues CV = new ContentValues(); //create instance
        CV.put(TableDataUnits.TableInfoUnits.TIME, time); //coll 0
        CV.put(TableDataUnits.TableInfoUnits.UNITS, units); //coll 1
        CV.put(TableDataUnits.TableInfoUnits.PERCENTAGE, percentage); //coll 2
        CV.put(TableDataUnits.TableInfoUnits.BAC, bac); //coll 3
        long k = SQ.insert(TableDataUnits.TableInfoUnits.TABLE_NAME, null, CV);
        Log.d("Database operations", "1 row inserted into database");
    }

    //retrieve database
    public Cursor getInfo(DatabaseOperationsUnits DOU){
        //read database
        SQLiteDatabase SQ = DOU.getReadableDatabase();
        //columns from database
        String[] col = {
                TableDataUnits.TableInfoUnits.TIME,
                TableDataUnits.TableInfoUnits.UNITS,
                TableDataUnits.TableInfoUnits.PERCENTAGE,
                TableDataUnits.TableInfoUnits.BAC
        };
        //get data
        Cursor CR = SQ.query(TableDataUnits.TableInfoUnits.TABLE_NAME, col, null, null, null, null, null);
        return CR;
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }

    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.UK);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MINUTES);
    }
}
