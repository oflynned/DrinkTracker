package com.glassbyte.drinktracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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

public class DrinkTrackerDbHelper extends SQLiteOpenHelper {
    public static final String STR_DATE_FORMAT = "HH:mm:ss dd/MM/yyyy";
    public static final Locale DATE_LOCALE = Locale.UK;
    public static final int database_version = 1;
    public String CREATE_DRINKS_TABLE_QUERY =
            "CREATE TABLE " +
                    DrinkTrackerDatabase.DrinksTable.TABLE_NAME + "(" +
                    DrinkTrackerDatabase.DrinksTable._ID  + " INTEGER PRIMARY KEY," +
                    DrinkTrackerDatabase.DrinksTable.DATE_TIME + " INTEGER," +
                    DrinkTrackerDatabase.DrinksTable.TITLE + " TEXT," +
                    DrinkTrackerDatabase.DrinksTable.VOLUME + " INTEGER," +
                    DrinkTrackerDatabase.DrinksTable.PERCENTAGE + " REAL," +
                    DrinkTrackerDatabase.DrinksTable.BAC + " REAL," +
                    DrinkTrackerDatabase.DrinksTable.UNITS + " REAL);";
    public String CREATE_BAC_TABLE_QUERY =
            "CREATE TABLE " +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + "(" +
                    DrinkTrackerDatabase.BacTable._ID  + " INTEGER PRIMARY KEY," +
                    DrinkTrackerDatabase.BacTable.DATE_TIME + " INTEGER," +
                    DrinkTrackerDatabase.BacTable.BAC + " TEXT," +
                    DrinkTrackerDatabase.BacTable.UPDATE_TYPE+ " INTEGER);";
    private String CREATE_DRINKS_BAC_RELATIONS_QUERY =
            "CREATE TABLE " +
                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + "(" +
                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + " INTEGER NOT NULL," +
                    DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + " INTEGER NOT NULL," +
                    "PRIMARY KEY(" + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "," +
                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID+ "), " +
                    "FOREIGN KEY (" + DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + ") REFERENCES " +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + "(" +
                    DrinkTrackerDatabase.BacTable._ID + ")," +
                    "FOREIGN KEY (" + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + ") REFERENCES " +
                    DrinkTrackerDatabase.DrinksTable.TABLE_NAME + " (" + DrinkTrackerDatabase.DrinksTable._ID +"));";


    private String DELETE_DRINKS_TABLE_QUERY = "DROP TABLE IF EXISTS " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME;
    private String DELETE_BAC_TABLE_QUERY = "DROP TABLE IF EXISTS " + DrinkTrackerDatabase.BacTable.TABLE_NAME;
    private String DELETE_DRINKS_BAC_RELATIONS_QUERY = "DROP TABLE IF EXISTS " + DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME;

    public DrinkTrackerDbHelper(Context context) {
        //constructor
        //create database with respect to the version
        super(context, DrinkTrackerDatabase.DATABASE_NAME, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DRINKS_TABLE_QUERY); //create table query
        db.execSQL(CREATE_BAC_TABLE_QUERY);
        db.execSQL(CREATE_DRINKS_BAC_RELATIONS_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_DRINKS_TABLE_QUERY);
        db.execSQL(DELETE_BAC_TABLE_QUERY);
        db.execSQL(DELETE_DRINKS_BAC_RELATIONS_QUERY);
        onCreate(db);
        /*
        * FOR RELEASE VERSION THIS IMPLEMENTATION SHOULD BE MODIFIED SO THAT ANY DATA FROM THE ALREADY
        * EXISITING TABLE GETS MIGRATED TO THE NEW TABLE BEING CREATED
        **/
    }

    //insert new drink into the database
    public long insertNewDrink(String title, int mlVol, double percentage, double bac){
        //method for putting info generated into the table
        //variables instantiated are parametrised and cast into the cols
        SQLiteDatabase sq = this.getWritableDatabase(); //writes data to database
        double units = percentage * mlVol/1000;

        ContentValues cv = new ContentValues(); //create instance; id_col = col 0
        cv.put(DrinkTrackerDatabase.DrinksTable.DATE_TIME, System.currentTimeMillis()); //coll 1
        cv.put(DrinkTrackerDatabase.DrinksTable.TITLE, title); //coll 2
        cv.put(DrinkTrackerDatabase.DrinksTable.VOLUME, mlVol); //coll 3
        cv.put(DrinkTrackerDatabase.DrinksTable.PERCENTAGE, percentage); //coll 4
        cv.put(DrinkTrackerDatabase.DrinksTable.BAC, bac); //coll 5
        cv.put(DrinkTrackerDatabase.DrinksTable.UNITS, units); //col 6

        return sq.insert(DrinkTrackerDatabase.DrinksTable.TABLE_NAME, null, cv);
    }


    public void removeDrinks(Context context, Integer[] drinksIds){
        SQLiteDatabase readDB = this.getReadableDatabase();
        Cursor c = readDB.rawQuery("SELECT * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME,
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
        String sqlQuery = "DELETE FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME
                + " WHERE " + DrinkTrackerDatabase.DrinksTable._ID + "=";
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
}
