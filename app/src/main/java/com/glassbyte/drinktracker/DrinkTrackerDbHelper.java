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
            long firstZeroBacDate = firstZeroBacDateCursor.getLong(0);
            firstZeroBacDateCursor.close();
            //End of Get the date of the first time bac was was 0 after inserting the drink that is being deleted

            printTableContents(DrinkTrackerDatabase.DrinksTable.TABLE_NAME);
            printTableContents(DrinkTrackerDatabase.BacTable.TABLE_NAME);
            printTableContents(DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME);

            //Set up string queries for later use
            String deleteFromRelationsTable = "DELETE FROM " +
                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME;
            String deleteFromBacTableWhithId = "DELETE FROM " +
                    DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE " +
                    DrinkTrackerDatabase.BacTable._ID + "=";
            String deleteFromDrinksTableWithId = "DELETE FROM " +
                    DrinkTrackerDatabase.DrinksTable.TABLE_NAME + " WHERE " +
                    DrinkTrackerDatabase.DrinksTable._ID + "=";
            //End of Set up string queries for later use

            Cursor listOfBacEntriesToBeRecalculated;
            if (firstZeroBacDate > 0) {
                calendar.setTimeInMillis(firstZeroBacDate);
                System.out.println("Date of first zero bac: " + sdf.format(calendar.getTime()) + " ---- Millis: " + firstZeroBacDate);
                //bac have reached zero since inserting the drink that's being deleted
                //hence modify all the bac entries in between the insert date and the bac 0 date
                System.out.println("The bac have reached 0 since the drink being deleted was inserted.");



                String selectAllBacEntriesBetweenTheDrinkAndFirstZeroBacEntry =
                        "SELECT * FROM " + DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                " INNER JOIN " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                                " ON " + DrinkTrackerDatabase.BacTable.TABLE_NAME + "." +
                                DrinkTrackerDatabase.BacTable._ID + "=" +
                                DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + " WHERE " +
                                DrinkTrackerDatabase.BacTable.TABLE_NAME + "." +
                                DrinkTrackerDatabase.BacTable.DATE_TIME + " BETWEEN " +
                                Long.toString(drinkInsertDate) + " AND " +
                                Long.toString(firstZeroBacDate) + " ORDER BY (" +
                                DrinkTrackerDatabase.BacTable.TABLE_NAME + "." +
                                DrinkTrackerDatabase.BacTable.DATE_TIME + ") ASC";
                listOfBacEntriesToBeRecalculated = readDB.rawQuery(
                        selectAllBacEntriesBetweenTheDrinkAndFirstZeroBacEntry,
                        null
                );
            }
            else {
                System.out.println("Bac never reached zero yet after inserting");
                //bac never reached zero yet after inserting the drink that's being deleted
                //hence modify all the bac entries from the one that's being deleted till the end
                String selectAllBacEntriesFromTheDrinkToTheLast =
                        "SELECT * FROM " + DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                " INNER JOIN " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                                " ON " + DrinkTrackerDatabase.BacTable.TABLE_NAME + "." +
                                DrinkTrackerDatabase.BacTable._ID + "=" +
                                DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + " WHERE " +
                                DrinkTrackerDatabase.BacTable.TABLE_NAME + "." +
                                DrinkTrackerDatabase.BacTable.DATE_TIME + ">" +
                                Long.toString(drinkInsertDate) + " ORDER BY (" +
                                DrinkTrackerDatabase.BacTable.TABLE_NAME + "." +
                                DrinkTrackerDatabase.BacTable.DATE_TIME + ") ASC";
                listOfBacEntriesToBeRecalculated = readDB.rawQuery(
                        selectAllBacEntriesFromTheDrinkToTheLast,
                        null
                );
            }

            //listOfBacEntries must be relation drink bac table inner joined with bac table:
            //0=Relation.BacId; 1=Relation.DrinkId; 2=Relation.BacAmt;
            // 3=BacTable.BacId; 4=BacTable.DateTime; 5=BacTable.Bac; 6=BacTable.UpdateType;
            listOfBacEntriesToBeRecalculated.moveToFirst();


            //Deal with the first insertion entry of the drink being deleted
            int bacId = listOfBacEntriesToBeRecalculated.getInt(0);
            float currentBacAtTheTime = listOfBacEntriesToBeRecalculated.getFloat(5);
            float drinkBacAtInsertion = listOfBacEntriesToBeRecalculated.getFloat(2);
            currentBacAtTheTime -= drinkBacAtInsertion;
            System.out.println("Deletion of first drink -- reset currentBacAtTheTime = " + currentBacAtTheTime + " -= " + drinkBacAtInsertion);

            String deleteInsertionFromRelationTable =
                    deleteFromRelationsTable + " WHERE " +
                            DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + "=" +
                            Integer.toString(bacId) + " AND " +
                            DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "="
                            + Integer.toString(drinkId);
            String deleteInsertionFromDrinksTable = deleteFromDrinksTableWithId +
                    Integer.toString(drinkId);
            String deleteInsertionFromBacTable = deleteFromBacTableWhithId +
                    Integer.toString(bacId);
            System.out.println(deleteInsertionFromBacTable);

            writeDB.execSQL(deleteInsertionFromRelationTable);
            writeDB.execSQL(deleteInsertionFromBacTable);
            writeDB.execSQL(deleteInsertionFromDrinksTable);
            //End of Deal with the first insertion entry of the drink being deleted

            //Get all the drink inserted between the date of the insertion of the one being
            // deleted and the first 0 bac entry (does not include the one deleted)
            String selectAllDrinksInsertedAfterTheDrinkAndBeforeFirstZeroBac =
                    "SELECT * FROM " + DrinkTrackerDatabase.DrinksTable.TABLE_NAME + " WHERE " +
                            DrinkTrackerDatabase.DrinksTable.DATE_TIME + " BETWEEN " +
                            Long.toString(drinkInsertDate) + " AND " + Long.toString(firstZeroBacDate);
            Cursor drinksInsertedBetweenTheDrinkAndFirstZeroBac =
                    readDB.rawQuery(selectAllDrinksInsertedAfterTheDrinkAndBeforeFirstZeroBac, null);
            drinksInsertedBetweenTheDrinkAndFirstZeroBac.moveToFirst();
            //0=drinkId;1=dateTime;2=title;3=vol;4=percentage;5=bac;6=units;
            int drinksCount = 0;
            //End of Get all the drink inserted between the date of the insertion of...

            float presentDrinkBacAtTheTime = 0f;
            int presentDrinkId = -1;
            if (drinksCount < drinksInsertedBetweenTheDrinkAndFirstZeroBac.getCount()) {
                presentDrinkBacAtTheTime = drinksInsertedBetweenTheDrinkAndFirstZeroBac.getFloat(5);
                presentDrinkId =
                        drinksInsertedBetweenTheDrinkAndFirstZeroBac.getInt(0);
            }

            boolean isPastTheDrinksFirstDecayEntry = false;
            for (int i = 1; i < listOfBacEntriesToBeRecalculated.getCount(); i++) {
                //Looping through all the bac entries between the drink inserted(incl.) and
                // the first zero bac after(incl.)
                bacId = listOfBacEntriesToBeRecalculated.getInt(0);
                int updateType = listOfBacEntriesToBeRecalculated.getInt(6);
                System.out.println("Iteration: " + i+"; BacId: "+ bacId+"; updateType: " + updateType );

                if (updateType == DrinkTrackerDatabase.BacTable.DECAY_UPDATE) {
                    System.out.println("type: DECAY_UPDATE");
                    if (isPastTheDrinksFirstDecayEntry) {
                        System.out.println("Past the drink's being deleted first decay entry.");
                        //The bac entry being checked is a decay update affecting the drink being deleted
                        if (presentDrinkBacAtTheTime == 0f &&
                                drinksCount < drinksInsertedBetweenTheDrinkAndFirstZeroBac.getCount()) {
                            //Get the next drink to be decayed if the last one was fully decayed and there were drinks inserted after the last one
                            drinksInsertedBetweenTheDrinkAndFirstZeroBac.moveToNext();
                            presentDrinkBacAtTheTime = drinksInsertedBetweenTheDrinkAndFirstZeroBac.getFloat(5);
                            drinksCount++;
                        }

                        if (presentDrinkBacAtTheTime > 0) {
                            presentDrinkId =
                                    drinksInsertedBetweenTheDrinkAndFirstZeroBac.getInt(0);
                            //Decay the remainder of the drink that is being decayed
                            float bacAmt = listOfBacEntriesToBeRecalculated.getFloat(2);

                            if (bacAmt < presentDrinkBacAtTheTime) {
                                //The bacAmt to be decayed is less than the remaining present bac of the drink

                                // Change the entry in the relation table to reference this drink instead of the one being deleted
                                int thisDrinkId = drinksInsertedBetweenTheDrinkAndFirstZeroBac.getInt(0);
                                String modifyRelationEntryDrinkId = "UPDATE " +
                                        DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                        " SET " + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                        "=" + Integer.toString(thisDrinkId) + " WHERE " +
                                        DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "=" +
                                        Integer.toString(drinkId) + " AND " +
                                        DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + "=" +
                                        Integer.toString(bacId);
                                writeDB.execSQL(modifyRelationEntryDrinkId);

                                currentBacAtTheTime -= bacAmt;
                                presentDrinkBacAtTheTime -= bacAmt;
                                continue;
                            } else {
                                //The bacAmt to be decayed is LARGER than the bac of the
                                // drink to be decayed

                                //Remove the relation table entry of the drink being deleted
                                String deleteRelationTableEntry = deleteFromRelationsTable +
                                        " WHERE " + DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID +
                                        "=" + Integer.toString(bacId) + " AND " +
                                        DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "=" +
                                        Integer.toString(drinkId);
                                writeDB.execSQL(deleteRelationTableEntry);

                                //Deal with the first drink inserted without moving the cursor to the next
                                presentDrinkBacAtTheTime =
                                        drinksInsertedBetweenTheDrinkAndFirstZeroBac.getFloat(5);
                                //Make new entry in the relation table to decay the drink by the drink bac amt
                                ContentValues newRelationTableEntryValues = new ContentValues();
                                newRelationTableEntryValues.put(
                                        DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID,
                                        presentDrinkId
                                );
                                newRelationTableEntryValues.put(
                                        DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID,
                                        bacId
                                );
                                newRelationTableEntryValues.put(
                                        DrinkTrackerDatabase.DrinksBacRelationTable.BAC_AMT,
                                        presentDrinkBacAtTheTime
                                );
                                writeDB.insert(
                                        DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME,
                                        null,
                                        newRelationTableEntryValues
                                );

                                bacAmt -= drinksInsertedBetweenTheDrinkAndFirstZeroBac.getFloat(5);
                                drinksCount++;
                                //End of Deal with the first drink inserted without moving ...

                                while (bacAmt > 0 && drinksCount < drinksInsertedBetweenTheDrinkAndFirstZeroBac.getCount()) {
                                    drinksInsertedBetweenTheDrinkAndFirstZeroBac.moveToNext();
                                    presentDrinkId =
                                            drinksInsertedBetweenTheDrinkAndFirstZeroBac.getInt(0);
                                    presentDrinkBacAtTheTime =
                                            drinksInsertedBetweenTheDrinkAndFirstZeroBac.getFloat(5);

                                    if (presentDrinkBacAtTheTime < bacAmt) {
                                        bacAmt -= drinksInsertedBetweenTheDrinkAndFirstZeroBac.getFloat(5);
                                        //Make new entry in the relation table to decay the drink by the drink bac amt
                                        newRelationTableEntryValues = new ContentValues();
                                        newRelationTableEntryValues.put(
                                                DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID,
                                                presentDrinkId
                                        );
                                        newRelationTableEntryValues.put(
                                                DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID,
                                                bacId
                                        );
                                        newRelationTableEntryValues.put(
                                                DrinkTrackerDatabase.DrinksBacRelationTable.BAC_AMT,
                                                presentDrinkBacAtTheTime
                                        );
                                        writeDB.insert(
                                                DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME,
                                                null,
                                                newRelationTableEntryValues
                                        );
                                    } else {
                                        //Make new entry in the relation table to decay the drink by the bac amt
                                        newRelationTableEntryValues = new ContentValues();
                                        newRelationTableEntryValues.put(
                                                DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID,
                                                presentDrinkId
                                        );
                                        newRelationTableEntryValues.put(
                                                DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID,
                                                bacId
                                        );
                                        newRelationTableEntryValues.put(
                                                DrinkTrackerDatabase.DrinksBacRelationTable.BAC_AMT,
                                                bacAmt
                                        );
                                        writeDB.insert(
                                                DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME,
                                                null,
                                                newRelationTableEntryValues
                                        );

                                        bacAmt = 0;
                                    }
                                    drinksCount++;
                                }

                                if (bacAmt > 0) {
                                    //Modify the value of bac in the bac entry by subtracting the bacAmt remainder
                                    float bacEntryBacValue =
                                            listOfBacEntriesToBeRecalculated.getFloat(5);
                                    bacEntryBacValue -= bacAmt;
                                    String modifyBacEntry = "UPDATE " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                                            " SET " + DrinkTrackerDatabase.BacTable.BAC + "=" +
                                            bacEntryBacValue + " WHERE " + DrinkTrackerDatabase.BacTable._ID +
                                            "=" + Integer.toString(bacId);
                                    writeDB.execSQL(modifyBacEntry);
                                }
                            }
                        } else {
                            //no more drinks inserted after that need to be decayed

                            float bacAffectedByDrinkAmt =
                                    listOfBacEntriesToBeRecalculated.getFloat(2);

                            //check if the bac entry is related only to the drink from the relation entry being checked
                            String selectAllRelationsWithTheBacId =
                                    "SELECT * FROM " + DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                            " WHERE " + DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID +
                                            "=" + Integer.toString(bacId);
                            Cursor allRelationsWithTheBacId =
                                    readDB.rawQuery(selectAllRelationsWithTheBacId, null);
                            allRelationsWithTheBacId.moveToFirst();
                            boolean isBacEntryRelatedToMoreThanOneDrink =
                                    allRelationsWithTheBacId.getCount() > 0;
                            allRelationsWithTheBacId.close();

                            //if yes then delete that bac entry --- else subtract the bacAmt in the relation entry from the bac in the bac entry
                            if (isBacEntryRelatedToMoreThanOneDrink) {
                                float tmpBac = listOfBacEntriesToBeRecalculated.getFloat(5)
                                        - bacAffectedByDrinkAmt;
                                String modifyBacValueInTheBacEntry = "UPDATE " +
                                        DrinkTrackerDatabase.BacTable.TABLE_NAME + " SET " +
                                        DrinkTrackerDatabase.BacTable.BAC + "=" +
                                        Float.toString(tmpBac);
                                writeDB.execSQL(modifyBacValueInTheBacEntry);
                            } else {
                                String deleteTheBacEntry = deleteFromBacTableWhithId +
                                        Integer.toString(bacId);
                                writeDB.execSQL(deleteTheBacEntry);
                            }

                            //delete the relation entry row
                            String deleteRelationEntry = deleteFromRelationsTable + " WHERE " +
                                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + "=" +
                                    Integer.toString(bacId) + " AND " +
                                    DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID + "="
                                    + Integer.toString(presentDrinkId);
                            writeDB.execSQL(deleteRelationEntry);

                            currentBacAtTheTime -= bacAffectedByDrinkAmt;
                        }
                    } else {
                        if (listOfBacEntriesToBeRecalculated.getInt(1) == drinkId) {
                            //This is the first decay entry of the drink being deleted
                            isPastTheDrinksFirstDecayEntry = true;

                            if (drinksInsertedBetweenTheDrinkAndFirstZeroBac.getCount() > 0) {
                                System.out.println("There are drinks that were inserted after the one being deleted");
                                //There are drinks that were inserted after the one being deleted
                                float bacAmt = listOfBacEntriesToBeRecalculated.getFloat(2);
                                if (presentDrinkBacAtTheTime == 0f) {
                                    presentDrinkBacAtTheTime = drinksInsertedBetweenTheDrinkAndFirstZeroBac.getFloat(5);
                                    drinksCount++;
                                }

                                if (bacAmt < presentDrinkBacAtTheTime) {
                                    System.out.println("The bacAmt to be decayed is less than the remaining present bac of the drink");
                                    //The bacAmt to be decayed is less than the remaining present bac of the drink

                                    //Change the entry in the relation table to reference this drink instead of the one being deleted
                                    String modifyRelationDrinkId = "UPDATE " +
                                            DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                            " SET " +
                                            DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                            "=" + Integer.toString(presentDrinkId) +
                                            " WHERE " +
                                            DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                            "=" + Integer.toString(drinkId) + " AND " +
                                            DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID +
                                            "=" + Integer.toString(bacId);
                                    writeDB.execSQL(modifyRelationDrinkId);

                                    currentBacAtTheTime -= bacAmt;
                                    presentDrinkBacAtTheTime -= bacAmt;
                                    continue;
                                } else {
                                    System.out.println("The bacAmt to be decayed is LARGER than the bac of the drink to be decayed");
                                    //The bacAmt to be decayed is LARGER than the bac of the
                                    // drink to be decayed

                                    //Remove the relation table entry of the drink being deleted
                                    String deleteTheRelation = deleteFromRelationsTable +
                                            " WHERE " + DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                            "=" + Integer.toString(drinkId) + " AND " +
                                            DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID +
                                            "=" + Integer.toString(bacId);
                                    writeDB.execSQL(deleteTheRelation);

                                    //Deal with the first drink inserted without moving the cursor to the next
                                    presentDrinkId =
                                            drinksInsertedBetweenTheDrinkAndFirstZeroBac.getInt(0);
                                    //Make new entry in the relation table to decay the drink by the drink bac amt
                                    ContentValues newRelationEntryValues = new ContentValues();
                                    newRelationEntryValues.put(
                                            DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID,
                                            bacId
                                    );
                                    newRelationEntryValues.put(
                                            DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID,
                                            presentDrinkId
                                    );
                                    newRelationEntryValues.put(
                                            DrinkTrackerDatabase.DrinksBacRelationTable.BAC_AMT,
                                            presentDrinkBacAtTheTime
                                    );
                                    writeDB.insert(
                                            DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME,
                                            null,
                                            newRelationEntryValues
                                    );

                                    bacAmt -= presentDrinkBacAtTheTime;
                                    currentBacAtTheTime -= presentDrinkBacAtTheTime;
                                    drinksCount++;
                                    //End of Deal with the first drink inserted without moving ...

                                    while (bacAmt > 0 && drinksCount < drinksInsertedBetweenTheDrinkAndFirstZeroBac.getCount()) {
                                        drinksInsertedBetweenTheDrinkAndFirstZeroBac.moveToNext();

                                        presentDrinkId =
                                                drinksInsertedBetweenTheDrinkAndFirstZeroBac.getInt(0);
                                        presentDrinkBacAtTheTime =
                                                drinksInsertedBetweenTheDrinkAndFirstZeroBac.getFloat(5);

                                        if (presentDrinkBacAtTheTime < bacAmt) {
                                            bacAmt -= drinksInsertedBetweenTheDrinkAndFirstZeroBac.getFloat(5);
                                            //Make new entry in the relation table to decay the drink by the drink bac amt
                                            newRelationEntryValues = new ContentValues();
                                            newRelationEntryValues.put(
                                                    DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID,
                                                    presentDrinkId
                                            );
                                            newRelationEntryValues.put(
                                                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID,
                                                    bacId
                                            );
                                            newRelationEntryValues.put(
                                                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_AMT,
                                                    presentDrinkBacAtTheTime
                                            );
                                            writeDB.insert(
                                                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME,
                                                    null,
                                                    newRelationEntryValues
                                            );
                                        } else {
                                            //Make new entry in the relation table to decay the drink by the bac amt
                                            newRelationEntryValues = new ContentValues();
                                            newRelationEntryValues.put(
                                                    DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID,
                                                    presentDrinkId
                                            );
                                            newRelationEntryValues.put(
                                                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID,
                                                    bacId
                                            );
                                            newRelationEntryValues.put(
                                                    DrinkTrackerDatabase.DrinksBacRelationTable.BAC_AMT,
                                                    bacAmt
                                            );
                                            writeDB.insert(
                                                    DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME,
                                                    null,
                                                    newRelationEntryValues
                                            );

                                            bacAmt = 0;
                                        }
                                        drinksCount++;

                                        bacAmt -= drinksInsertedBetweenTheDrinkAndFirstZeroBac.getFloat(5);
                                        currentBacAtTheTime -= presentDrinkBacAtTheTime;
                                        drinksCount++;
                                    }

                                    if (bacAmt > 0) {
                                        //Modify the value of bac in the bac entry by subtracting the bacAmt remainder
                                        float bacEntryBacValue =
                                                listOfBacEntriesToBeRecalculated.getFloat(5);
                                        bacEntryBacValue -= bacAmt;
                                        String modifyBacEntry = "UPDATE " + DrinkTrackerDatabase.BacTable.TABLE_NAME +
                                                " SET " + DrinkTrackerDatabase.BacTable.BAC + "=" +
                                                bacEntryBacValue + " WHERE " + DrinkTrackerDatabase.BacTable._ID +
                                                "=" + Integer.toString(bacId);
                                        writeDB.execSQL(modifyBacEntry);
                                    }
                                }

                            } else {
                                System.out.println("No drinks inserted after the one being deleted and before the 0 bac entry");
                                //No drinks inserted after the one being deleted
                                //remove bac relation
                                String deleteBacRelation = deleteFromRelationsTable + " WHERE " +
                                        DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID + "=" +
                                        Integer.toString(bacId) + " AND " +
                                        DrinkTrackerDatabase.DrinksBacRelationTable.DRINK_ID +
                                        "=" + Integer.toString(drinkId);
                                writeDB.execSQL(deleteBacRelation);

                                String selectAllRelationsWithBacIdRemaining =
                                        "SELECT * FROM " +
                                                DrinkTrackerDatabase.DrinksBacRelationTable.TABLE_NAME +
                                                " WHERE " +
                                                DrinkTrackerDatabase.DrinksBacRelationTable.BAC_ID +
                                                "=" + Integer.toString(bacId);
                                Boolean areRelationsLeftWithBacId =
                                        readDB.rawQuery(
                                                selectAllRelationsWithBacIdRemaining, null
                                        ).getCount() > 0;

                                if (areRelationsLeftWithBacId) {
                                    System.out.println("There are other relation entries with the bac id.");
                                    //subtract the bac amt from the relation table from the bac in the bac entry
                                    float newBacValue = listOfBacEntriesToBeRecalculated.getFloat(5)
                                            - listOfBacEntriesToBeRecalculated.getFloat(2);

                                    String modifyBacEntryBacValue = "UPDATE " +
                                            DrinkTrackerDatabase.BacTable.TABLE_NAME + " SET " +
                                            DrinkTrackerDatabase.BacTable.BAC + "=" +
                                            Float.toString(newBacValue) + " WHERE " +
                                            DrinkTrackerDatabase.BacTable._ID + "=" + Integer.toString(bacId);

                                    writeDB.execSQL(modifyBacEntryBacValue);
                                } else {
                                    System.out.println("No other relation entries with the bac id.");
                                    //remove bac table entry if the bac relation entry affects only this drink
                                    String deleteBacTableEntry = deleteFromBacTableWhithId +
                                            Integer.toString(bacId);
                                    writeDB.execSQL(deleteBacTableEntry);
                                }

                                currentBacAtTheTime += listOfBacEntriesToBeRecalculated.getFloat(2);
                            }

                        } else {
                            //SEEMS DONE
                            //The entry is a decay entry that happened before the first decay of the drink being deleted
                            //Change the bac value of the bac entry by subtracting

                            //Add the bac of the insertion to the currentBac before the insertion at the time
                            currentBacAtTheTime += listOfBacEntriesToBeRecalculated.getFloat(2);
                            //End add the bac of the insertion...

                            //Modify the bac value of the bac entry of the currently checked entry
                            String modifyBacValueInTheBacEntry = "UPDATE " +
                                    DrinkTrackerDatabase.BacTable.TABLE_NAME + " SET " +
                                    DrinkTrackerDatabase.BacTable.BAC + "=" +
                                    Float.toString(currentBacAtTheTime) + " WHERE " +
                                    DrinkTrackerDatabase.BacTable._ID + "=" +
                                    Integer.toString(bacId);
                            writeDB.execSQL(modifyBacValueInTheBacEntry);
                            //End of Modify the bac value of the bac entry of the currently checked entry
                        }

                    }
                }

                else if (updateType == DrinkTrackerDatabase.BacTable.INSERT_NEW_UPDATE){
                    System.out.println("type: INSERT_NEW_UPDATE");
                    //SEEMS DONE
                    //Change the bac value of the bac entry by subtracting

                    //Add the bac of the insertion to the currentBac before the insertion at the time
                    currentBacAtTheTime += listOfBacEntriesToBeRecalculated.getFloat(2);
                    //End add the bac of the insertion...

                    //Modify the bac value of the bac entry of the currently checked entry
                    String modifyBacValueInTheBacEntry = "UPDATE " +
                            DrinkTrackerDatabase.BacTable.TABLE_NAME + " SET " +
                            DrinkTrackerDatabase.BacTable.BAC + "=" +
                            Float.toString(currentBacAtTheTime) + " WHERE " +
                            DrinkTrackerDatabase.BacTable._ID + "=" +
                            Integer.toString(bacId);
                    writeDB.execSQL(modifyBacValueInTheBacEntry);
                    //End of Modify the bac value of the bac entry of the currently checked entry

                    System.out.println("BacId: "+bacId + "; CurrentBacAtTheTimeL " +currentBacAtTheTime +"; -- " + modifyBacValueInTheBacEntry);
                }
                listOfBacEntriesToBeRecalculated.moveToNext();
            }
            listOfBacEntriesToBeRecalculated.close();
            drinksInsertedBetweenTheDrinkAndFirstZeroBac.close();
        }

        SharedPreferences.Editor e = sp.edit();
        e.putFloat(mContext.getString(R.string.pref_key_currentEbac), getCurrentBacFromDb());
        e.apply();
    }

    public float getCurrentBacFromDb(){
        System.out.println("CurrentBac as the most recent value from the bac table has been requested.");
        SQLiteDatabase readDb = this.getReadableDatabase();
        String selectMostRecentBacEntryQuery = "SELECT (" + DrinkTrackerDatabase.BacTable.BAC
                + ") FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME + " WHERE "
                + DrinkTrackerDatabase.BacTable.DATE_TIME + "="
                + "(SELECT MAX(" + DrinkTrackerDatabase.BacTable.DATE_TIME + ") FROM "
                + DrinkTrackerDatabase.BacTable.TABLE_NAME + ")";
        Cursor mostRecentBacEntry = readDb.rawQuery(selectMostRecentBacEntryQuery, null);
        mostRecentBacEntry.moveToFirst();
        float currentBac = 0f;
        if (mostRecentBacEntry.getCount() > 0)
            currentBac = mostRecentBacEntry.getFloat(0);
        mostRecentBacEntry.close();

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
        c.close();
    }
}