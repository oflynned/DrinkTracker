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
                    DrinkTrackerDatabase.BacTable.BAC + " REAL," +
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
        * EXISTING TABLE GETS MIGRATED TO THE NEW TABLE BEING CREATED
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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);

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
            firstZeroBacDateCursor.close();
            //End of Get the date of the first time bac was was 0 after inserting the drink that is being deleted

            //select all the decay bac entries from the bac table that are associated with the drink being deleted
            String selectAllTheDrinksBacEntriesQuery = "SELECT * FROM " +
                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + " INNER JOIN " +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + " ON " +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + "." +
                    DrinkTrackerDatabase.BacTable._ID + "=" +
                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + " WHERE " +
                    DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "=" +
                    Long.toString(drinkId) + " AND " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                    "." + DrinkTrackerDatabase.BacTable.UPDATE_TYPE + "=" +
                    DrinkTrackerDatabase.BacTable.DECAY_UPDATE + " ORDER BY (" +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + "." +
                    DrinkTrackerDatabase.BacTable.DATE_TIME + ") ASC";

            System.out.println(selectAllTheDrinksBacEntriesQuery);

            Cursor allTheDrinksBacDecayEntries = readDB.rawQuery(selectAllTheDrinksBacEntriesQuery, null);
            allTheDrinksBacDecayEntries.moveToFirst();
            //End of select all the decay bac entries from the bac table that are associated with the drink being deleted

            printTableContents(DrinkTrackerDatabase.DrinksTable.TABLE_NAME);
            printTableContents(DrinkTrackerDatabase.BacTable.TABLE_NAME);
            printTableContents(selectAllTheDrinksBacEntriesQuery);
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
                if (allTheDrinksBacDecayEntries.getCount() > 0) {
                    //Decay updates affecting the drink being deleted have been already performed
                    System.out.println("Decay updates affecting the drink being deleted have been already performed.");

                    float totalDrinkDecay = 0f;
                    for (int i = 0; i < allTheDrinksBacDecayEntries.getCount(); i++) {
                        totalDrinkDecay += allTheDrinksBacDecayEntries.getFloat(2);
                        allTheDrinksBacDecayEntries.moveToNext();
                    } System.out.println("Total drink decay: "+totalDrinkDecay);

                    if (totalDrinkDecay >= drinkBac) {
                        //The drink being deleted has been already fully decayed
                        System.out.println("The drink being deleted has been already fully decayed.");

                    } else {
                        //The drink being deleted has not been fully decayed yet
                        System.out.println("The drink being deleted has not been fully decayed yet.");
                        //hence remove the bac entry where the drink was inserted and all the bac decay entries until insertion of another drink
                        //check if any drink was inserted after the drink being deleted
                        //if there was then change all the decays that happened after the insertion of the other drink to decay that other drink instead of the one being deleted

                        //Get all the drinks that were inserted after the one being deleted to see whether decay updates should be transferred to those
                        String selectDrinksInsertedAfterTheDrinkQuery = "SELECT * FROM " +
                                DrinkTrackerDatabase.DrinksTable.TABLE_NAME + " WHERE " +
                                DrinkTrackerDatabase.DrinksTable.DATE_TIME + ">" +
                                "(SELECT (" + DrinkTrackerDatabase.DrinksTable.DATE_TIME + ") FROM " +
                                DrinkTrackerDatabase.DrinksTable.TABLE_NAME + " WHERE " +
                                DrinkTrackerDatabase.DrinksTable._ID + "=" +
                                Integer.toString(drinkId) + ") ORDER BY (" +
                                DrinkTrackerDatabase.DrinksTable.DATE_TIME + ") ASC";
                        Cursor drinksInsertedAfterTheDrink =
                                readDB.rawQuery(selectDrinksInsertedAfterTheDrinkQuery, null);
                        drinksInsertedAfterTheDrink.moveToFirst();

                        printTableContents(selectDrinksInsertedAfterTheDrinkQuery);
                        if (drinksInsertedAfterTheDrink.getCount() > 0) {
                            //THIS IS DONE
                            //This if statement should take care of all the following cases:
                            // - First decay of the drink being deleted happened AFTER the insertion of the next drink
                            // - The last decay of the drink being deleted happened AFTER the insertion of the next drink
                            // - The last decay of the drink being deleted happened BEFORE the next drink
                            //there were drinks inserted after the drink being deleted in each of these cases

                            //Check if any of the decays happened before the insertion of the drink inserted after
                            long firstDrinkInsertedAfterDate = drinksInsertedAfterTheDrink.getLong(1);
                            allTheDrinksBacDecayEntries.moveToLast(); //because in ascending order thus the oldest is last
                            allTheDrinksBacDecayEntries.moveToFirst();

                            float bacDecayedOfTheDrink = 0f;
                            float tmpDrinksAfterDecayTotal = 0f; int drinkInsertedAfterCount = 0;
                            float newCurrentBac = sp.getFloat(mContext.getString(R.string.pref_key_currentEbac), 0f);

                            //for all the bac decay entries of the drink being deleted
                            for (int i = 1; i < allTheDrinksBacDecayEntries.getCount(); i++) {
                                int bacId = allTheDrinksBacDecayEntries.getInt(0);
                                long bacUpdateDate = allTheDrinksBacDecayEntries.getLong(4);

                                if (bacUpdateDate > firstDrinkInsertedAfterDate) {
                                    //Update the currentBac by adding to it the drink's decay updates that are going to be deleted
                                    float affectedBacAmt = allTheDrinksBacDecayEntries.getFloat(2);
                                    newCurrentBac += affectedBacAmt;
                                    //End of Update the currentBac by the drink's decay updates that are going to be deleted

                                    //Delete the entry from the relations table that is associated
                                    // with the drink being deleted and the bacId of the decay
                                    // update currently looked at
                                    String deleteRelationTableEntry = "DELETE FROM " +
                                            DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                            " WHERE " + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                            "=" + Integer.toString(drinkId) + " AND " +
                                            DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + "=" +
                                            Integer.toString(bacId);
                                    writeDB.execSQL(deleteRelationTableEntry);
                                    //End of delete the entry from the relations table

                                    if (i == 0) {
                                        //for the first bac decay entry check if it was affecting only
                                        //check if it's only related to one drink in the relation table
                                        String selectAllRelationsEntryWTheBacId = "SELECT * FROM " +
                                                DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                                " WHERE " +
                                                DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID +
                                                "=" + Integer.toString(bacId);

                                        Cursor allRelationsEntriesWTheBacId =
                                                readDB.rawQuery(selectAllRelationsEntryWTheBacId, null);
                                        allRelationsEntriesWTheBacId.moveToFirst();

                                        if (allRelationsEntriesWTheBacId.getCount() > 0) {
                                            //The first bac entry affects also other drinks than the one being deleted
                                            //therefore since at this point only this drink was left to decay after the previous
                                            //have finished decaying and there was no insertions after set the bac entry to 0
                                            String modifyDecayBacEntry = "UPDATE " +
                                                    DrinkTrackerDatabase.BacTable.TABLE_NAME + " SET " +
                                                    DrinkTrackerDatabase.BacTable.BAC + "=0.0";
                                            writeDB.execSQL(modifyDecayBacEntry);
                                        } else {
                                            //The first bac entry does not affect other drinks
                                            String deleteDecayBacEntry = "DELETE FROM " +
                                                    DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE " +
                                                    DrinkTrackerDatabase.BacTable._ID + "=" + Integer.toString(bacId);
                                            writeDB.execSQL(deleteDecayBacEntry);
                                        }
                                    } else {
                                        //It's not the first bac decay entry for the entry fro the drink
                                        // and the drink was not fully decayed yet so it was only associated
                                        // with the drink being deleted so safe to delete it
                                        String deleteDecayBacEntry = "DELETE FROM " +
                                                DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE " +
                                                DrinkTrackerDatabase.BacTable._ID + "=" + Integer.toString(bacId);
                                        writeDB.execSQL(deleteDecayBacEntry);
                                    }

                                    bacDecayedOfTheDrink += allTheDrinksBacDecayEntries.getFloat(2);

                                    allTheDrinksBacDecayEntries.moveToNext();
                                } else {
                                    //Handle all the drink's bac decay entries that happened
                                    // after the insertion of the next drink
                                    int drinkInsertedAfterId = drinksInsertedAfterTheDrink.getInt(0);
                                    float drinkInsertedAfterBac = drinksInsertedAfterTheDrink.getFloat(5);
                                    float relationDecayAmt = drinksInsertedAfterTheDrink.getFloat(2);

                                    tmpDrinksAfterDecayTotal += relationDecayAmt;
                                    if (tmpDrinksAfterDecayTotal <= drinkInsertedAfterBac) {
                                        //The next drink inserted is not to be decayed yet since
                                        // this one is still not decayed
                                        String modifyRelationTable = "UPDATE " +
                                                DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                                " SET " + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                                "=" + Integer.toString(drinkInsertedAfterId) +
                                                " WHERE " + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                                "=" + Integer.toString(drinkId) + " AND " +
                                                DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID +
                                                "=" + Integer.toString(bacId);
                                        writeDB.execSQL(modifyRelationTable);
                                    } else {
                                        //Either split this decay between this and the next drink inserted
                                        // if there is one and there is still something to be decayed from the current one being decayed
                                        // or just decay the remainder from this one and set bac entry to 0
                                        // and delete the remainder of bac decay entries
                                        float remainderToDecayOfTheDrink = drinkInsertedAfterBac - tmpDrinksAfterDecayTotal;

                                        if (remainderToDecayOfTheDrink > 0f) {
                                            //Make the last decay to the drink
                                            String modifyRelationTable = "UPDATE " +
                                                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                                    " SET " + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                                    "=" + Integer.toString(drinkInsertedAfterId) +
                                                    "," + DrinkTrackerDatabase.DrinksBacRelationTable.BAC_AMT +
                                                    "=" + Float.toString(remainderToDecayOfTheDrink) +
                                                    " WHERE " + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                                    "=" + Integer.toString(drinkId) + " AND " +
                                                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID +
                                                    "=" + Integer.toString(bacId);
                                            writeDB.execSQL(modifyRelationTable);
                                        }
                                        float decayRemainder = relationDecayAmt - remainderToDecayOfTheDrink;

                                        while (decayRemainder > 0f) {
                                            drinkInsertedAfterCount++;
                                            if (drinkInsertedAfterCount < drinksInsertedAfterTheDrink.getCount()) {
                                                drinksInsertedAfterTheDrink.moveToNext();
                                                float nextDrinkBac = drinksInsertedAfterTheDrink.getFloat(5);
                                                float decayAmt = 0f;
                                                if (nextDrinkBac >= decayRemainder) {
                                                    decayAmt = decayRemainder;
                                                    decayRemainder = 0f;
                                                } else {
                                                    decayAmt = nextDrinkBac;
                                                    decayRemainder -= nextDrinkBac;
                                                }
                                                ContentValues cv = new ContentValues();
                                                cv.put(DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID,
                                                        bacId);
                                                cv.put(DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID,
                                                        drinkId);
                                                cv.put(DrinkTrackerDatabase.DrinksBacRelationTable.BAC_AMT,
                                                        decayAmt);
                                                writeDB.insert(
                                                        DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME,
                                                        null,
                                                        cv
                                                );
                                            } else {
                                                //Set the decay remainder to break the loop
                                                decayRemainder = 0f;
                                            }
                                        }
                                    }
                                }
                                allTheDrinksBacDecayEntries.moveToNext();
                            }

                            //Modify the bac value of all the bac entries that
                            // happened after and including the next inserted drink being deleted
                            if (drinksInsertedAfterTheDrink.getCount() > 0) {
                                //Get the difference between the bac of the drink and the bac of the
                                // drink that was decayed until the next drink was inserted.
                                // The rest of the decays is ignored because they are getting transferred
                                // to the next drink inserted instead of getting deleted.
                                float bacDecayRemain = drinkBac - bacDecayedOfTheDrink;
                                //End of get the difference

                                //Select all the bac entries after and including the next inserted
                                // drink being deleted
                                String selectAllBacEntriesAfterNextDrink = "SELECT * FROM " +
                                        DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE " +
                                        DrinkTrackerDatabase.BacTable.DATE_TIME + ">=" +
                                        Long.toString(firstDrinkInsertedAfterDate);
                                Cursor allBacEntriesAfterLastDrinksDecay =
                                        readDB.rawQuery(selectAllBacEntriesAfterNextDrink, null);
                                //End of select all the bac entries after and ...

                                //Update the value of bac in all the selected entries in the bac table
                                for (int i = 0; i < allBacEntriesAfterLastDrinksDecay.getCount(); i++) {
                                    int bacId = allBacEntriesAfterLastDrinksDecay.getInt(0);
                                    float bac = allBacEntriesAfterLastDrinksDecay.getFloat(2);
                                    bac -= bacDecayRemain;
                                    bac = (bac < 0f) ? 0f : bac; //if bac < 0 set it to 0
                                    String modifyBacEntryQuery = "UPDATE " +
                                            DrinkTrackerDatabase.BacTable.TABLE_NAME + " SET " +
                                            DrinkTrackerDatabase.BacTable.BAC + "=" +
                                            Float.toString(bac) + " WHERE " +
                                            DrinkTrackerDatabase.BacTable._ID + "=" +
                                            Integer.toString(bacId);
                                    writeDB.execSQL(modifyBacEntryQuery);
                                    allBacEntriesAfterLastDrinksDecay.moveToNext();
                                }
                                allBacEntriesAfterLastDrinksDecay.close();
                                //End of Update the value of bac in all the selected entries in the bac table
                            }
                            //End of Modify the bac value of all the bac entries of the insertions that happened after the drink being deleted

                            //Modify any decay bac entries relating to previous insertions of drinks
                            // that happened after the insertion of the drink being deleted and
                            // before it started to be decayed to contain correct bac
                            String selectAllBacEntriesAfterTheDrinkQuery = "SELECT * FROM " +
                                    DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE " +
                                    DrinkTrackerDatabase.BacTable.DATE_TIME + ">" +
                                    Long.toString(drinkInsertDate);
                            if (drinksInsertedAfterTheDrink.getCount() > 0) {
                                //Expand the selection query if there are drink inserted after the
                                // one being deleted
                                selectAllBacEntriesAfterTheDrinkQuery += " AND " +
                                        DrinkTrackerDatabase.BacTable.DATE_TIME + "<" +
                                        Long.toString(firstDrinkInsertedAfterDate);
                            }

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
                            allBacEntriesAfterTheDrink.close();
                            //End of Modify any bac entries relating to previous insertions or ...

                            //Delete the insertion bac entry and the drinks table entry
                            //Get bac id of the entry modified by the insertion
                            String selectBacRelationForTheDrinkQuery = "SELECT * FROM " +
                                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + " WHERE " +
                                    DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "=" +
                                    Long.toString(drinkId);
                            Cursor selectBacRelationForTheDrink =
                                    readDB.rawQuery(selectBacRelationForTheDrinkQuery, null);
                            selectBacRelationForTheDrink.moveToFirst();
                            int bacId = selectBacRelationForTheDrink.getInt(0);
                            selectBacRelationForTheDrink.close();
                            //End of Get bac id of the entry modified by the insertion

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
                            //End of Delete the insertion bac entry and the drinks table entry

                            //Update the current bac in the shared preferences
                            newCurrentBac -= drinkBac;
                            SharedPreferences.Editor e = sp.edit();
                            e.putFloat(mContext.getString(R.string.pref_key_currentEbac), newCurrentBac);
                            e.apply();
                            //End of update the current bac in the shared preferences

                            //End of Check if any of the decays happened before the insertion of the drink inserted after

                        } else {
                            //THIS IS DONE
                            //there was no drinks inserted after the drink being deleted

                            //Delete all the drink's decay bac entries
                            allTheDrinksBacDecayEntries.moveToFirst();
                            for (int i = 0; i < allTheDrinksBacDecayEntries.getCount(); i++) {
                                int bacId = allTheDrinksBacDecayEntries.getInt(0);

                                String deleteRelationTableEntry = "DELETE FROM " +
                                        DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                        " WHERE " + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                        "=" + Integer.toString(drinkId) + " AND " +
                                        DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + "=" +
                                        Integer.toString(bacId);

                                writeDB.execSQL(deleteRelationTableEntry);

                                //Start of make sure that the first decay entry being deleted affects only the
                                //drink deleted because it may be affecting multiple in case one drink
                                //was being fully decayed at one update and the reminder was decayed
                                //from the drink being deleted
                                if (i == 0) {
                                    String selectAllRelationsEntryWTheBacId = "SELECT * FROM " +
                                            DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                            " WHERE " +
                                            DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID +
                                            "=" + Integer.toString(bacId);

                                    Cursor allRelationsEntriesWTheBacId =
                                            readDB.rawQuery(selectAllRelationsEntryWTheBacId, null);
                                    allRelationsEntriesWTheBacId.moveToFirst();

                                    if (allRelationsEntriesWTheBacId.getCount() > 0) {
                                        //The first bac entry affects also other drinks than the one being deleted
                                        //therefore since at this point only this drink was left to decay after the previous
                                        //have finished decaying and there was no insertions after set the bac entry to 0
                                        String modifyDecayBacEntry = "UPDATE " +
                                                DrinkTrackerDatabase.BacTable.TABLE_NAME + " SET " +
                                                DrinkTrackerDatabase.BacTable.BAC + "=0.0";
                                        writeDB.execSQL(modifyDecayBacEntry);
                                    } else {
                                        //The first bac entry does not affect other drinks
                                        String deleteDecayBacEntry = "DELETE FROM " +
                                                DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE " +
                                                DrinkTrackerDatabase.BacTable._ID + "=" + Integer.toString(bacId);
                                        writeDB.execSQL(deleteDecayBacEntry);
                                    }
                                    allRelationsEntriesWTheBacId.close();
                                }
                                //End of make sure that the first decay entry being deleted affects only the drink deleted

                                allTheDrinksBacDecayEntries.moveToNext();
                            }
                            allTheDrinksBacDecayEntries.close();
                            //End of delete

                            //Modify any decay bac entries relating to previous insertions to contain correct bac
                            String selectAllBacEntriesAfterTheDrinkQuery = "SELECT * FROM " +
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
                            allBacEntriesAfterTheDrink.close();
                            //End of Modify any bac entries relating to previous insertions or insert entries that happened after

                            //Delete the insertion bac entry and the drinks table entry
                            //Get bac id of the entry modified by the insertion
                            String selectBacRelationForTheDrinkQuery = "SELECT * FROM " +
                                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + " WHERE " +
                                    DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "=" +
                                    Long.toString(drinkId);
                            Cursor selectBacRelationForTheDrink =
                                    readDB.rawQuery(selectBacRelationForTheDrinkQuery, null);
                            selectBacRelationForTheDrink.moveToFirst();
                            int bacId = selectBacRelationForTheDrink.getInt(0);
                            selectBacRelationForTheDrink.close();
                            //End of Get bac id of the entry modified by the insertion

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
                            //End of Delete the insertion bac entry and the drinks table entry

                            //Update the current bac in the shared preferences
                            float newCurrentBac = getCurrentBacFromDb();
                            SharedPreferences.Editor e = sp.edit();
                            e.putFloat(mContext.getString(R.string.pref_key_currentEbac), newCurrentBac);
                            e.apply();
                            //End of update the current bac in the shared preferences
                        }
                    }
                } else {
                    //THIS SECTION IS DONE
                    //no decay updates affecting the drink being deleted have been performed yet
                    System.out.println("No decay updates affecting the drink being deleted have been performed yet.");

                    //Modify any bac entries relating to previous insertions or insert entries that happened after
                    String selectAllBacEntriesAfterTheDrinkQuery = "SELECT * FROM " +
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
                    allBacEntriesAfterTheDrink.close();
                    //End of Modify any bac entries relating to previous insertions or insert entries that happened after

                    //Update Current Bac in the shared preferences
                    float newCurrentBac =
                            sp.getFloat(mContext.getString(R.string.pref_key_currentEbac), 0.0f)
                            - drinkBac;
                    SharedPreferences.Editor e = sp.edit();
                    e.putFloat(mContext.getString(R.string.pref_key_currentEbac), newCurrentBac);
                    e.apply();
                    //End of Update Current Bac in the shared preferences

                    //Delete the entries in the relations, drinks and bac table
                    String selectBacRelationForTheDrinkQuery = "SELECT * FROM " +
                            DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME + " WHERE " +
                            DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "=" +
                            Long.toString(drinkId);
                    Cursor selectBacRelationForTheDrink =
                            readDB.rawQuery(selectBacRelationForTheDrinkQuery, null);
                    selectBacRelationForTheDrink.moveToFirst();
                    int bacId = selectBacRelationForTheDrink.getInt(0);
                    selectBacRelationForTheDrink.close();
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


                    System.out.println("Tables state after removing adequate rows associated with drink id: " + drinkId);
                    printTableContents(DrinkTrackerDatabase.DrinksTable.TABLE_NAME);
                    printTableContents(DrinkTrackerDatabase.BacTable.TABLE_NAME);
                    printTableContents(DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME);
                }
            }
        }

        writeDB.close();
        readDB.close();
    }

    public float getCurrentBacFromDb(){
        SQLiteDatabase readDb = this.getReadableDatabase();
        String selectMostRecentBacEntryQuery = "SELECT (" + DrinkTrackerDatabase.BacTable.BAC
                + ") FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE "
                + DrinkTrackerDatabase.BacTable.DATE_TIME + "="
                + "(SELECT MAX(" + DrinkTrackerDatabase.BacTable.DATE_TIME + ") FROM "
                + DrinkTrackerDatabase.BacTable.TABLE_NAME + ")";
        Cursor mostRecentBacEntry = readDb.rawQuery(selectMostRecentBacEntryQuery, null);
        float currentBac = 0f;
        if (mostRecentBacEntry.getCount() > 0)
            currentBac = mostRecentBacEntry.getFloat(0);

        return currentBac;
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
