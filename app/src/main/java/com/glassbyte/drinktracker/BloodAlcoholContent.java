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
    public static final double ELAPSED_HOUR_FACTOR = 0.015;
    private final double DENSITY_OF_ETHANOL = 0.789; //density of ethanol is 0.789g/ml
    private final double MALE_R = 0.68;
    private final double FEMALE_R = 0.55;
    private SharedPreferences sp;
    private boolean isMan;
    private double bodyWeight; // in grams
    private Activity activity;

    public BloodAlcoholContent(Activity activity){
        this.activity = activity;

        sp = PreferenceManager.getDefaultSharedPreferences(activity);
        String gender = sp.getString(activity.getString(R.string.pref_key_editGender),"");

        this.bodyWeight = Double.valueOf(sp.getString(activity.getString(R.string.pref_key_editWeight), "")) * 1000;
        this.isMan = (gender == "male");
    }

    // bodyWeight arg must be specified in kilograms
    public BloodAlcoholContent(boolean isMan, double bodyWeight){
        this.isMan = isMan;
        this.bodyWeight = bodyWeight * 1000; //convert kg's to g's
    }

    public float getCurrentEbac(){return sp.getFloat(activity.getString(R.string.pref_key_currentEbac),0);}

    //The alcVolPercentage arg is to be specified as a real number between 0 and 100
    public double getEstimatedBloodAlcoholContent(double mlSize, double alcVolPercentage){
        double volumeOfEthanol = mlSize*alcVolPercentage/100;
        double massOfAlcohol = volumeOfEthanol * DENSITY_OF_ETHANOL;//in grams
        double r = isMan ? MALE_R : FEMALE_R;

        return massOfAlcohol/(bodyWeight*r)*100;
    }

    public static boolean updateCurrentBac(Context context, int updateType){return updateCurrentBac(context, 0, updateType);}
    public static boolean updateCurrentBac(Context context, float dCurrentBAC, int updateType){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        DrinkTrackerDbHelper dbHelper = new DrinkTrackerDbHelper(context);
        SQLiteDatabase readDb = dbHelper.getReadableDatabase();
        SQLiteDatabase writeDb = dbHelper.getWritableDatabase();

        //Get the correct value of current BAC
        int prefLastUpdateDate = sp.getInt(context.getString(R.string.pref_key_last_update_currentEbac), 0);

        String selectMOstRecentQuery = "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME
                + " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME + " = (SELECT max("
                + DrinkTrackerDatabase.BacTable.DATE_TIME + ") FROM "
                + DrinkTrackerDatabase.BacTable.TABLE_NAME +")";
        Cursor c = readDb.rawQuery(selectMOstRecentQuery, null);

        float currentBac = 0;
        if (c.getCount()>0) {
            c.moveToFirst();
            float dbLastUpdateDate = c.getFloat(1);
            //check if the currentBac matches the most frequent bac stored in the database
            if (dbLastUpdateDate > prefLastUpdateDate)
                //the currentBac stored in the sp doesn't match the most frequent bac from the db
                currentBac = c.getFloat(2);
            else
                currentBac = sp.getFloat(context.getString(R.string.pref_key_currentEbac), 0);

        } else currentBac = sp.getFloat(context.getString(R.string.pref_key_currentEbac), 0);
        //End of Get the correct value of current BAC

        float newCurrentBac = 0;
        int newLastUpdateDate = (int)System.currentTimeMillis();

        if (updateType == DrinkTrackerDatabase.BacTable.INSERT_NEW_UPDATE) {

            //Calculate new current bac
            newCurrentBac = currentBac + dCurrentBAC;

        } else if (updateType == DrinkTrackerDatabase.BacTable.DECAY_UPDATE) {

            if (currentBac > 0) {
                String query = "SELECT * FROM " + DrinkTrackerDatabase.BacTable.TABLE_NAME
                        + " WHERE " + DrinkTrackerDatabase.BacTable.DATE_TIME + " = (SELECT max("
                        + DrinkTrackerDatabase.BacTable.DATE_TIME + ") FROM "
                        + DrinkTrackerDatabase.BacTable.TABLE_NAME + ") AND "
                        + DrinkTrackerDatabase.BacTable.UPDATE_TYPE + "=" + DrinkTrackerDatabase.BacTable.DECAY_UPDATE;
                Cursor cur = readDb.rawQuery(query, null);
                if (cur != null) {
                    int lastUpdateDate = cur.getInt(1);
                    newLastUpdateDate = (int) System.currentTimeMillis();
                    int timeDiffInMin = (int) TimeUnit.MILLISECONDS.convert((newLastUpdateDate - lastUpdateDate), TimeUnit.MINUTES);

                    dCurrentBAC = timeDiffInMin / 60 * (float) ELAPSED_HOUR_FACTOR;
                    if (dCurrentBAC <= currentBac)
                        newCurrentBac = currentBac - dCurrentBAC;
                }
            } else return false;
        }

        //Store the newly calculated bac in the SP
        SharedPreferences.Editor e = sp.edit();
        e.putFloat(context.getString(R.string.pref_key_currentEbac), newCurrentBac);
        e.putInt(context.getString(R.string.pref_key_last_update_currentEbac), newLastUpdateDate);
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
