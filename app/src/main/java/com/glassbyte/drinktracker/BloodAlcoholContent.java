package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maciej on 12/08/2015.
 */
public class BloodAlcoholContent {
    /*
    * This class is based on: http://www.wikihow.com/Calculate-Blood-Alcohol-Content-%28Widmark-Formula%29
    * */
    public static final float ELAPSED_HOUR_FACTOR = 0.015f;
    private final float DENSITY_OF_ETHANOL = 0.789f; //density of ethanol is 0.789g/ml
    private final float MALE_R = 0.68f;
    private final float FEMALE_R = 0.55f;
    private SharedPreferences sp;
    private boolean isMan;
    private float bodyWeight; // in grams
    private Context mContext;

    public BloodAlcoholContent(Context context){
        mContext = context;

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        String gender = sp.getString(context.getString(R.string.pref_key_editGender),"");

        this.bodyWeight = Float.valueOf(sp.getString(context.getString(R.string.pref_key_editWeight), "")) * 1000;
        this.isMan = (gender == "male");
    }

    // bodyWeight arg must be specified in kilograms
    public BloodAlcoholContent(boolean isMan, float bodyWeight){
        this.isMan = isMan;
        this.bodyWeight = bodyWeight * 1000; //convert kg's to g's
    }

    public float getCurrentEbac(){return sp.getFloat(mContext.getString(R.string.pref_key_currentEbac),0);}

    //The alcVolPercentage arg is to be specified as a real number between 0 and 100
    public float getEstimatedBloodAlcoholContent(int mlSize, float alcVolPercentage){
        float volumeOfEthanol = mlSize*alcVolPercentage/100;
        float massOfAlcohol = volumeOfEthanol * DENSITY_OF_ETHANOL;//in grams
        float r = isMan ? MALE_R : FEMALE_R;

        return massOfAlcohol/(bodyWeight*r)*100;
    }

    public static boolean updateCurrentBac(Context context, int updateType){return updateCurrentBac(context, 0, updateType);}
    public static boolean updateCurrentBac(Context context, float dCurrentBAC, int updateType){
        System.out.println("Entered updateCurrentBac with: dCurrentBAC="+dCurrentBAC+"; updateType="+updateType+";");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        DrinkTrackerDbHelper dbHelper = new DrinkTrackerDbHelper(context);
        SQLiteDatabase readDb = dbHelper.getReadableDatabase();
        SQLiteDatabase writeDb = dbHelper.getWritableDatabase();

        //Get the correct value of current BAC
        long prefLastUpdateDate = sp.getLong(context.getString(R.string.pref_key_last_update_currentEbac), 0);

        String selectMOstRecentQuery = "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME
                + " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME + " = (SELECT max("
                + DrinkTrackerDatabase.BacTable.DATE_TIME + ") FROM "
                + DrinkTrackerDatabase.BacTable.TABLE_NAME +")";
        Cursor c = readDb.rawQuery(selectMOstRecentQuery, null);

        float currentBac = 0;
        if (c.getCount()>0) {
            System.out.println("BacTable had some entry. getCount()>0");
            c.moveToFirst();
            long dbLastUpdateDate = c.getLong(1);
            //check if the currentBac matches the most frequent bac stored in the database
            if (dbLastUpdateDate > prefLastUpdateDate) {
                //the currentBac stored in the sp doesn't match the most frequent bac from the db
                currentBac = c.getFloat(2);
                System.out.println("The currentBac stored in the sp DOESN'T match the most frequent bac from the db");
            } else {
                System.out.println("The currentBac stored in the sp MATCH the most frequent bac from the db");
                currentBac = sp.getFloat(context.getString(R.string.pref_key_currentEbac), 0);
            }

        } else {currentBac = sp.getFloat(context.getString(R.string.pref_key_currentEbac), 0);System.out.println("BacTable had no entries.");}
        //End of Get the correct value of current BAC
        System.out.println("currentBac="+currentBac+";");

        float newCurrentBac = 0;
        long newLastUpdateDate = System.currentTimeMillis();

        if (updateType == DrinkTrackerDatabase.BacTable.INSERT_NEW_UPDATE) {
            System.out.println("updateType == INSERT_NEW_DRINK");
            //Calculate new current bac
            newCurrentBac = currentBac + dCurrentBAC;
            System.out.println("newCurrentBac="+newCurrentBac+";");

        } else if (updateType == DrinkTrackerDatabase.BacTable.DECAY_UPDATE) {
            System.out.println("updateType == DECAY_UPDATE");

            if (currentBac > 0) {
                System.out.println("currentBac > 0");
                String query = "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME
                        + " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME + " = (SELECT max("
                        + DrinkTrackerDatabase.BacTable.DATE_TIME + ") FROM "
                        + DrinkTrackerDatabase.BacTable.TABLE_NAME + ") AND "
                        + DrinkTrackerDatabase.BacTable.UPDATE_TYPE + "=" + DrinkTrackerDatabase.BacTable.DECAY_UPDATE;
                Cursor cur = readDb.rawQuery(query, null);
                cur.moveToFirst();

                if (cur.getCount()>0) {
                    System.out.println("BacTable included some DECAY_UPDATE entry");
                    long lastUpdateDate = cur.getLong(1);
                    long timeDiffInMin = TimeUnit.MILLISECONDS.convert((newLastUpdateDate - lastUpdateDate), TimeUnit.MINUTES);

                    dCurrentBAC = timeDiffInMin / 60 * (float) ELAPSED_HOUR_FACTOR;
                } else {
                    System.out.println("No DECAY_UPDATE entry in the bacTable yet.");
                    //This will be the first DECAY_UPDATE entry in the database so assume this has happened after 15 minutes
                    dCurrentBAC = 0.25f * (float)ELAPSED_HOUR_FACTOR;
                }

                if (dCurrentBAC < currentBac)
                    newCurrentBac = currentBac - dCurrentBAC;
                System.out.println("newCurrentBac="+newCurrentBac+";");
            } else {System.out.println("return false;");return false;}
        }

        //Store the newly calculated bac in the SP
        SharedPreferences.Editor e = sp.edit();
        e.putFloat(context.getString(R.string.pref_key_currentEbac), newCurrentBac);
        e.putLong(context.getString(R.string.pref_key_last_update_currentEbac), newLastUpdateDate);
        e.apply();
        //End of Store the newly calculated bac in the SP
        //Store the newly calculated bac in the DB
        ContentValues cv = new ContentValues();
        cv.put(DrinkTrackerDatabase.BacTable.DATE_TIME, newLastUpdateDate);
        cv.put(DrinkTrackerDatabase.BacTable.BAC, newCurrentBac);
        cv.put(DrinkTrackerDatabase.BacTable.UPDATE_TYPE, updateType);
        writeDb.insert(DrinkTrackerDatabase.BacTable.TABLE_NAME, null, cv);
        //End of Store the newly calculated bac in the DB

        //Clean up
        readDb.close();
        writeDb.close();

        System.out.println("return true;");
        return true;
    }

    /*Taken the below method from: http://stackoverflow.com/a/2808648 */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static class MetricSystemConverter{
        static public final double IMPERIAL_OZ_IN_ML = 28.4131;
        static private final double POUND_IN_KG = 0.453592;
        static private final double STONE_IN_KG = 6.35029;
        static private final double FOOT_IN_CM = 30.48;
        static private final double INCH_IN_CM = 2.54;

        public MetricSystemConverter(){}

        //returns an array of lenght 2 where the first element(index 0) is the feet factor
        //and the second element is the inch factor
        static public double[] converCmToFeetAndInches(double cm){
            double[] feetInches = new double[2];
            feetInches[0] = Math.floor(cm/FOOT_IN_CM);
            feetInches[1] = (cm-feetInches[0]*FOOT_IN_CM)/INCH_IN_CM;
            return feetInches;
        }

        static public double convertFeetAndInchesToCm(double[] feetInches){
            return feetInches[0]*FOOT_IN_CM + feetInches[1]*INCH_IN_CM;
        }

        static public double convertOzToMillilitres(double oz){return oz*IMPERIAL_OZ_IN_ML;}

        static public double convertMillilitresToOz(double ml){return ml/IMPERIAL_OZ_IN_ML;}

        static public double convertPoundsToKilograms(double pounds){return pounds*POUND_IN_KG;}

        static public double convertKilogramsToPounds(double kg){return kg/POUND_IN_KG;}

        static public double convertStoneToKilograms(double stones){return stones*STONE_IN_KG;}

        static public double convertKilogramsToStones(double kg){return kg/STONE_IN_KG;}
    }
}
