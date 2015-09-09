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

    public void insertDrinkBacRelation(long drinkId, long bacId, float bacAmt){
        SQLiteDatabase writeDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID, bacId);
        cv.put(DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID, drinkId);
            cv.put(DrinkTrackerDatabase.DrinksBacRelationTable.BAC_AMT, bacAmt);
        writeDb.insert(DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME, null, cv);
    }

    public void removeDrinks(Long[] drinksIds){
        System.out.println("Remove drinks entered.");

        SQLiteDatabase readDB = this.getReadableDatabase();
        SQLiteDatabase writeDB = this.getWritableDatabase();

        //remove one at a time
        for (long drinkId:
            drinksIds) {
            System.out.println("Drink id being removed: " + drinkId);

            String selectDrinkInsertDateQuery = "SELECT * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME
                    + " WHERE " + DrinkTrackerDatabase.DrinksTable._ID + "=" + Long.toString(drinkId);
            Cursor drinkCursor = readDB.rawQuery(selectDrinkInsertDateQuery, null);
            long drinkInsertDate = drinkCursor.getLong(1);
            drinkCursor.close();

            //Get the date of the first time bac was was 0 after inserting the drink that is being deleted
            String selectFirstZeroBacQuery = "SELECT MIN(" + DrinkTrackerDatabase.BacTable.DATE_TIME
                    + ") FROM (SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME
                    + " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME + ">" + drinkInsertDate
                    + " AND " + DrinkTrackerDatabase.BacTable.BAC + "=0)";
            Cursor firstZeroBacDateCursor = readDB.rawQuery(selectFirstZeroBacQuery, null);
            //End of Get the date of the first time bac was was 0 after inserting the drink that is being deleted

            String selectFirstDecayUpdateAffectingTheDrink = "SELECT MIN(" + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                    "." + DrinkTrackerDatabase.BacTable.DATE_TIME + ") FROM (SELECT * FROM " +
                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + " WHERE " +
                    DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "=" +
                    drinkId + " JOIN " + DrinkTrackerDatabase.BacTable.TABLE_NAME + " ON " +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + "." + DrinkTrackerDatabase.BacTable._ID +
                    "=" + DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + "." +
                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + ") WHERE " +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + "." + DrinkTrackerDatabase.BacTable.UPDATE_TYPE +
                    "=" + DrinkTrackerDatabase.BacTable.DECAY_UPDATE;

            Cursor firstDecayUpdateCursor = readDB.rawQuery(selectFirstDecayUpdateAffectingTheDrink, null);

            if (firstZeroBacDateCursor.getCount() != 0) {
                long firstZeroBacDate = firstZeroBacDateCursor.getLong(0);
                //bac have reached zero since inserting the drink that's being deleted
                //hence modify all the bac entries in between the insert date and the bac 0 date
                System.out.println("The bac have reached 0 since the drink being deleted was inserted.");
            } else {
                //bac never reached zero yet after inserting the drink that's being deleted
                //hence modify all the bac entries from the one that's being deleted till the end
                System.out.println("The bac never reached 0 after inserting the drink being deleted.");
                if (firstDecayUpdateCursor.getCount() == 0) {
                    //no decay updates affecting the drink being deleted have been performed yet
                    System.out.println("No decay updates affecting the drink being deleted have been performed yet.");
                }
            }
        }

        writeDB.close();
        readDB.close();
    }
}
