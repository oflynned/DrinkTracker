package com.glassbyte.drinktracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Locale;

public class DrinkTrackerDbHelper extends SQLiteOpenHelper {
    private Context mContext;
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
                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_AMT + " REAL NOT NULL," +
                    "PRIMARY KEY(" + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "," +
                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID+ "), " +
                    "FOREIGN KEY (" + DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + ") REFERENCES " +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + "(" +
                    DrinkTrackerDatabase.BacTable._ID + ")," +
                    "FOREIGN KEY (" + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + ") REFERENCES " +
                    DrinkTrackerDatabase.DrinksTable.TABLE_NAME + " (" + DrinkTrackerDatabase.DrinksTable._ID +"));";


    private String DELETE_DRINKS_TABLE_QUERY = "DROP TABLE IF EXISTS "
            + DrinkTrackerDatabase.DrinksTable.TABLE_NAME;
    private String DELETE_BAC_TABLE_QUERY = "DROP TABLE IF EXISTS "
            + DrinkTrackerDatabase.BacTable.TABLE_NAME;
    private String DELETE_DRINKS_BAC_RELATIONS_QUERY = "DROP TABLE IF EXISTS "
            + DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME;

    public DrinkTrackerDbHelper(Context context) {
        //constructor
        //create database with respect to the version
        super(context, DrinkTrackerDatabase.DATABASE_NAME, null, database_version);
        mContext = context;
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
    public long insertNewDrink(String title, int mlVol, float percentage){
        //method for putting info generated into the table
        //variables instantiated are parametrised and cast into the cols
        SQLiteDatabase sq = this.getWritableDatabase(); //writes data to database
        float units = percentage * mlVol/1000;
        BloodAlcoholContent bac = new BloodAlcoholContent(mContext);
        float bacValue = bac.getEstimatedBloodAlcoholContent(mlVol, percentage);

        ContentValues cv = new ContentValues(); //create instance; id_col = col 0
        cv.put(DrinkTrackerDatabase.DrinksTable.DATE_TIME, System.currentTimeMillis()); //coll 1
        cv.put(DrinkTrackerDatabase.DrinksTable.TITLE, title); //coll 2
        cv.put(DrinkTrackerDatabase.DrinksTable.VOLUME, mlVol); //coll 3
        cv.put(DrinkTrackerDatabase.DrinksTable.PERCENTAGE, percentage); //coll 4
        cv.put(DrinkTrackerDatabase.DrinksTable.BAC, bacValue); //coll 5
        cv.put(DrinkTrackerDatabase.DrinksTable.UNITS, units); //col 6
        long drinkId = sq.insert(DrinkTrackerDatabase.DrinksTable.TABLE_NAME, null, cv);

        BloodAlcoholContent.updateCurrentBac(mContext, bacValue,
                DrinkTrackerDatabase.BacTable.INSERT_NEW_UPDATE, drinkId);

        sq.close();

        return drinkId;
    }

    public void insertDrinkBacRelation(long drinkId, long bacId){
        SQLiteDatabase writeDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID, bacId);
        cv.put(DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID, drinkId);
        writeDb.insert(DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME, null, cv);
    }

    public void removeDrinks(Long[] drinksIds){
        SQLiteDatabase readDB = this.getReadableDatabase();
        SQLiteDatabase writeDB = this.getWritableDatabase();

        String deleteFromDrinksTableQuery = "DELETE * FROM "
                + DrinkTrackerDatabase.DrinksTable.TABLE_NAME + " WHERE "
                + DrinkTrackerDatabase.DrinksTable._ID + "=";
        String deleteFromDrinkBacRelationTableQuery = "DELETE * FROM " +
                DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + " WHERE " +
                DrinkTrackerDatabase.DrinksTable._ID + "=";
        String oldestDrinkIdQuery = "SELECT * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME
                + " WHERE " + DrinkTrackerDatabase.DrinksTable.DATE_TIME + "=(SELECT max("
                + DrinkTrackerDatabase.DrinksTable.DATE_TIME + ") FROM TABLE WHERE ("; // close off with 2 brackets
        for (long drinkId:
             drinksIds) {
            oldestDrinkIdQuery += DrinkTrackerDatabase.DrinksTable._ID + "=" + drinkId + " OR ";
        }
        oldestDrinkIdQuery += "1=0))"; //1=0 to ignore the last dot that is added after the last OR id=x

        //Remove all the entries from the bac table that are younger then the oldest drink being removed
        Cursor c =readDB.rawQuery(oldestDrinkIdQuery, null);
        c.moveToFirst();
        long date = c.getLong(1);
        String deleteAllOlder = "DELETE * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE "
                + DrinkTrackerDatabase.BacTable.DATE_TIME + ">" + Long.toString(date);
        writeDB.execSQL(deleteAllOlder);
        //End of Remove all the entries from the bac table that are younger then the oldest drink being removed

        //Remove all the appriopiate entries from the drinks table and the relation table
        for (long drinkId:
             drinksIds) {
            String query = deleteFromDrinksTableQuery + drinkId;
            writeDB.execSQL(query);

            query = deleteFromDrinkBacRelationTableQuery + drinkId;
            writeDB.execSQL(query);
        }
        //End of Remove all the appriopiate entries from the drinks table and the relation table


        writeDB.close();
        readDB.close();
    }
}
