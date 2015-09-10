package com.glassbyte.drinktracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public void removeDrinks(Integer[] drinksIds){
        //For debugging only
        System.out.println("Remove drinks entered.");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yy HH:mm");
        //End for debugging only

        SQLiteDatabase readDB = this.getReadableDatabase();
        SQLiteDatabase writeDB = this.getWritableDatabase();

        //remove one at a time
        for (int drinkId:
            drinksIds) {
            System.out.println("Drink id being removed: " + drinkId);

            String selectDrinkInsertDateQuery = "SELECT * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME
                    + " WHERE " + DrinkTrackerDatabase.DrinksTable._ID + "=" + Long.toString(drinkId);
            Cursor drinkCursor = readDB.rawQuery(selectDrinkInsertDateQuery, null);
            drinkCursor.moveToFirst();
            long drinkInsertDate = drinkCursor.getLong(1);
            float drinkBac = drinkCursor.getFloat(5);
            drinkCursor.close();
            calendar.setTimeInMillis(drinkInsertDate);
            System.out.println("Date of insertion: " + sdf.format(calendar.getTime()));

            //Get the date of the first time bac was was 0 after inserting the drink that is being deleted
            String selectFirstZeroBacQuery = "SELECT MIN(" + DrinkTrackerDatabase.BacTable.DATE_TIME
                    + ") FROM (SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME
                    + " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME + ">" + drinkInsertDate
                    + " AND " + DrinkTrackerDatabase.BacTable.BAC + "=0.0)";
            Cursor firstZeroBacDateCursor = readDB.rawQuery(selectFirstZeroBacQuery, null);
            firstZeroBacDateCursor.moveToFirst();
            System.out.println(selectFirstZeroBacQuery);
            long firstZeroBacDate = firstZeroBacDateCursor.getLong(0);
            //End of Get the date of the first time bac was was 0 after inserting the drink that is being deleted

            // + DrinkTrackerDatabase.BacTable.TABLE_NAME +"."
            String selectFirstDecayUpdateAffectingTheDrink = "SELECT MIN(" + DrinkTrackerDatabase.BacTable.DATE_TIME + ") FROM (SELECT * FROM " +
                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + " INNER JOIN " +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + " ON " +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + "." + DrinkTrackerDatabase.BacTable._ID +
                    "=" + DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + "." +
                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + " WHERE " +
                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + "." +
                    DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "=" +
                    drinkId + ") WHERE " + DrinkTrackerDatabase.BacTable.UPDATE_TYPE + "=" +
                    DrinkTrackerDatabase.BacTable.DECAY_UPDATE;

            System.out.println(selectFirstDecayUpdateAffectingTheDrink);

            Cursor firstDecayUpdateCursor = readDB.rawQuery(selectFirstDecayUpdateAffectingTheDrink, null);
            firstDecayUpdateCursor.moveToFirst();
            long firstDecayUpdateDate = firstDecayUpdateCursor.getLong(0);

            printTableContents(DrinkTrackerDatabase.DrinksTable.TABLE_NAME);
            printTableContents(DrinkTrackerDatabase.BacTable.TABLE_NAME);
            printTableContents(DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME);

            if (firstZeroBacDate > 0) {
                calendar.setTimeInMillis(firstZeroBacDate);
                System.out.println("Date of first zero bac: " + sdf.format(calendar.getTime()) + " ---- Millis: "+firstZeroBacDate);
                //bac have reached zero since inserting the drink that's being deleted
                //hence modify all the bac entries in between the insert date and the bac 0 date
                System.out.println("The bac have reached 0 since the drink being deleted was inserted.");
            } else {
                //bac never reached zero yet after inserting the drink that's being deleted
                //hence modify all the bac entries from the one that's being deleted till the end
                System.out.println("The bac never reached 0 after inserting the drink being deleted.");
                if (firstDecayUpdateDate > 0) {
                    //there is decay updates affecting the drink being deleted have been performed yet
                    System.out.println("There is decay updates affecting the drink being deleted have been performed yet.");
                } else {
                    //no decay updates affecting the drink being deleted have been performed yet
                    String selectAllBacEntriesAfterTheDrinkQuery = "SELELCT * FROM " +
                            DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE " +
                            DrinkTrackerDatabase.BacTable.DATE_TIME + ">" + Long.toString(drinkInsertDate);

                    Cursor allBacEntriesAfterTheDrink = readDB.rawQuery(selectAllBacEntriesAfterTheDrinkQuery, null);
                    allBacEntriesAfterTheDrink.moveToFirst();

                    for (int i = 0; i < allBacEntriesAfterTheDrink.getCount(); i++) {
                        int bacId = allBacEntriesAfterTheDrink.getInt(0);
                        float modifiedBac = allBacEntriesAfterTheDrink.getFloat(2) - drinkBac;
                        String updateBacQuery = "UPDATE " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                                " SET " + DrinkTrackerDatabase.BacTable.BAC + "=" +
                                Float.toString(modifiedBac) + " WHERE " +
                                DrinkTrackerDatabase.BacTable._ID + "=" + bacId;
                        writeDB.execSQL(updateBacQuery);
                    }

                    //Update Current Bac in the shared preferences
                    String selectBacRelationForTheDrinkQuery = "SELECT * FROM " +
                            DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + " WHERE " +
                            DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "=" +
                            Long.toString(drinkId);
                    Cursor selectBacRelationForTheDrink =
                            readDB.rawQuery(selectBacRelationForTheDrinkQuery, null);
                    int bacId = selectBacRelationForTheDrink.getInt(0);

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                    float newCurrentBac =
                            sp.getFloat(mContext.getString(R.string.pref_key_currentEbac), 0.0f)
                            - drinkBac;
                    SharedPreferences.Editor e = sp.edit();
                    e.putFloat(mContext.getString(R.string.pref_key_currentEbac), drinkBac);
                    //End of Update Current Bac in the shared preferences

                    //Delete the entries in the relations, drinks and bac table
                    String deleteFromRelationTableQuery = "DELETE FROM " +
                            DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + " WHERE " +
                            DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "=" +
                            Integer.toString(drinkId) + " AND " +
                            DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + "=" +
                            Integer.toString(bacId);
                    String deleteFromDrinkTableQuery = "DELETE FROM " +
                            DrinkTrackerDatabase.DrinksTable.TABLE_NAME + " WHERE " +
                            DrinkTrackerDatabase.DrinksTable._ID + "=" + Integer.toString(drinkId);
                    String deleteFromBacTableQuery = "DELETE FROM " +
                            DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE " +
                            DrinkTrackerDatabase.BacTable._ID + "=" + Integer.toString(bacId);
                    writeDB.execSQL(deleteFromRelationTableQuery);
                    writeDB.execSQL(deleteFromDrinkTableQuery);
                    writeDB.execSQL(deleteFromBacTableQuery);
                    //End of Delete the entries in the relations, drinks and bac table
                }
            }
        }

        writeDB.close();
        readDB.close();
    }

    public void printTableContents(String tableName){
        String q = "SELECT * FROM (" + tableName + ")";
        SQLiteDatabase readDb = this.getReadableDatabase();
        Cursor c = readDb.rawQuery(q, null);
        c.moveToFirst();
        int rowNum = c.getCount();
        int colNum = c.getColumnCount();
        System.out.println("Number of rows in " + tableName + " is " + rowNum);
        System.out.println("Number of columns in each row is " + colNum);
        for (int r = 0; r < rowNum; r++) {
            for (int col = 0; col < colNum; col++) {
                System.out.print(col + ". " + c.getString(col) + "; ");
            }
            System.out.print("\n");
            c.moveToNext();
        }
    }
}
