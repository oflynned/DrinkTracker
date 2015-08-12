package com.glassbyte.drinktracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                    DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME +
                    "(" +
                    //col for time
                    DataUnitsDatabaseContractor.DataLoggingTable.TIME +
                    " TEXT," +
                    //col for units
                    DataUnitsDatabaseContractor.DataLoggingTable.UNITS +
                    " TEXT," +
                    //col for percentage abv of alcohol
                    DataUnitsDatabaseContractor.DataLoggingTable.PERCENTAGE +
                    " TEXT," +
                    //col for bac to generate from formula
                    DataUnitsDatabaseContractor.DataLoggingTable.BAC +
                    " TEXT);";
    //; ends query field within query -> ()

    public DatabaseOperationsUnits(Context context) {
        //constructor
        //create database with respect to the version
        super(context, DataUnitsDatabaseContractor.DATABASE_NAME, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY); //create table query
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //haven't done anything with this
    }

    //insert data into database
    public void putInfo(DatabaseOperationsUnits DOU, String time, double units, double percentage, double bac){
        //method for putting info generated into the table
        //variables instantiated are parametrised and cast into the cols
        SQLiteDatabase SQ = DOU.getWritableDatabase(); //writes data to database
        ContentValues CV = new ContentValues(); //create instance
        CV.put(DataUnitsDatabaseContractor.DataLoggingTable.TIME, time); //coll 0
        CV.put(DataUnitsDatabaseContractor.DataLoggingTable.UNITS, units); //coll 1
        CV.put(DataUnitsDatabaseContractor.DataLoggingTable.PERCENTAGE, percentage); //coll 2
        CV.put(DataUnitsDatabaseContractor.DataLoggingTable.BAC, bac); //coll 3
    }

    //retrieve database
    public Cursor getInfo(DatabaseOperationsUnits DOU){
        //read database
        SQLiteDatabase SQ = DOU.getReadableDatabase();
        //read columns from database into a 4 element array
        String[] col = {
                DataUnitsDatabaseContractor.DataLoggingTable.TIME,
                DataUnitsDatabaseContractor.DataLoggingTable.UNITS,
                DataUnitsDatabaseContractor.DataLoggingTable.PERCENTAGE,
                DataUnitsDatabaseContractor.DataLoggingTable.BAC
        };
        //get data
        return SQ.query(DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME, col, null, null, null, null, null);
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<>(2);
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

    //unused and possible not needed
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MINUTES);
    }
}
