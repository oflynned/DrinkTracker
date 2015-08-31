package com.glassbyte.drinktracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DatabaseOperationsUnits extends SQLiteOpenHelper {
    public static final String STR_DATE_FORMAT = "HH:mm:ss dd/MM/yyyy";
    public static final Locale DATE_LOCALE = Locale.UK;
    public static final int database_version = 1;
    public String CREATE_QUERY =
            "CREATE TABLE " +
                    DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME +
                    "(" +
                    DataUnitsDatabaseContractor.DataLoggingTable._ID  + " INTEGER PRIMARY KEY," +
                    //col for time
                    DataUnitsDatabaseContractor.DataLoggingTable.TIME + " TEXT," +
                    //col for title
                    DataUnitsDatabaseContractor.DataLoggingTable.TITLE + " TEXT," +
                    //col for units
                    DataUnitsDatabaseContractor.DataLoggingTable.UNITS + " REAL," +
                    //col for percentage abv of alcohol
                    DataUnitsDatabaseContractor.DataLoggingTable.PERCENTAGE + " REAL," +
                    //col for bac to generate from formula
                    DataUnitsDatabaseContractor.DataLoggingTable.BAC + " REAL);";
    //; ends query field within query -> ()
    private String DELETE_QUERY = "DROP TABLE IF EXISTS " + DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME;

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
        db.execSQL(DELETE_QUERY);
        onCreate(db);
        /*
        * FOR RELEASE VERSION THIS IMPLEMENTATION SHOULD BE MODIFIED SO THAT ANY DATA FROM THE ALREADY
        * EXISITING TABLE GETS MIGRATED TO THE NEW TABLE BEING CREATED
        **/
    }

    //insert new drink into the database
    public long insertNewDrink(String time, String title, double mlVol, double percentage, double bac){
        //method for putting info generated into the table
        //variables instantiated are parametrised and cast into the cols
        SQLiteDatabase sq = this.getWritableDatabase(); //writes data to database

        ContentValues cv = new ContentValues(); //create instance
        cv.put(DataUnitsDatabaseContractor.DataLoggingTable.TIME, time); //coll 0
        cv.put(DataUnitsDatabaseContractor.DataLoggingTable.TITLE, title); //coll 1
        cv.put(DataUnitsDatabaseContractor.DataLoggingTable.UNITS, mlVol); //coll 2
        cv.put(DataUnitsDatabaseContractor.DataLoggingTable.PERCENTAGE, percentage); //coll 3
        cv.put(DataUnitsDatabaseContractor.DataLoggingTable.BAC, bac); //coll 4

        return sq.insert(DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME, null, cv);
    }

    public void removeDrinks(Context context, Integer[] drinksIds){
        SQLiteDatabase readDB = this.getReadableDatabase();
        Cursor c = readDB.rawQuery("SELECT * FROM " + DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME,
                null);
        c.moveToFirst();

        int idColIndex = 0;
        int bacColIndex = 5;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        float currentEbac =  sp.getFloat(context.getString(R.string.pref_key_currentEbac), 0);
        float tmpCurrentEbac = currentEbac;

        //Make a list of indexes of drinks that affect the currentBAC
        ArrayList<Float[]> indexesAffectingCurrentEbac = new ArrayList<>();

        Float[] tuplet = new Float[2];
        tuplet[0] = (float)c.getInt(idColIndex);
        tuplet[1] = c.getFloat(bacColIndex);
        indexesAffectingCurrentEbac.add(tuplet);

        while ( tmpCurrentEbac > 0 && c.moveToNext()) {
            tuplet = new Float[2];
            tuplet[0] = (float)c.getInt(idColIndex);
            float tmpBAC = c.getFloat(bacColIndex);
            if (tmpCurrentEbac - tmpBAC >= 0)
                tuplet[1] = tmpBAC;
            else
                tuplet[1] = tmpCurrentEbac;
            indexesAffectingCurrentEbac.add(tuplet);

            tmpCurrentEbac -= tmpBAC;
            System.out.println("Affects currentBAC: "+tuplet[0]);
        }
        //End of Make a list of indexes of drinks that affect the currentBAC

        //Run delete queries for each drink
        String sqlQuery = "DELETE FROM " + DataUnitsDatabaseContractor.DataLoggingTable.TABLE_NAME
                + " WHERE " + DataUnitsDatabaseContractor.DataLoggingTable._ID + "=";
        SQLiteDatabase writeDB = this.getWritableDatabase();
        int test = 0; System.out.println("Size of drinksIds: "+drinksIds.length);
        for (int id : drinksIds) {
            test++;
            System.out.println("Iteration: " + test);
            //Check if the drink selected to remove affects the currentBAC
            Iterator<Float[]> itr = indexesAffectingCurrentEbac.iterator();

            while (itr.hasNext()) {
                Float[] drink = itr.next();
                System.out.println("Id: " + id  + " == drink[0]: " + drink[0]);
                if (id == drink[0]) {
                    //drink with the id affects the current bac
                    currentEbac -= drink[1];
                    indexesAffectingCurrentEbac.remove((Float[])drink);
                    System.out.println("Id: " + id + " --------- BAC: " + drink[1]);
                    break;
                }
            }

            String removeDrinkQuery = sqlQuery + id;
            writeDB.execSQL(removeDrinkQuery);
        }
        //End of Run delete queries for each drink

        SharedPreferences.Editor e = sp.edit();
        e.putFloat(context.getString(R.string.pref_key_currentEbac), currentEbac);
        e.apply();
        writeDB.close();
        readDB.close();
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat(STR_DATE_FORMAT, DATE_LOCALE);
        Date date = new Date();
        return dateFormat.format(date);
    }

    //returns difference in minutes
    public static long getDateDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(diffInMillies);
    }
}
