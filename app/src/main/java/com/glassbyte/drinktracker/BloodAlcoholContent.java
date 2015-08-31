package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    /*
    * bodyWeight arg must be specified in kilograms
    * */
    public BloodAlcoholContent(boolean isMan, double bodyWeight){
        this.isMan = isMan;
        this.bodyWeight = bodyWeight * 1000; //convert kg's to g's
    }

    /*WAY TO ACCESS THE SHARED STATIC VARIABLE OF CURRENTEBAC*/
    public void setCurrentEbac(float ebac){
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(activity.getString(R.string.pref_key_currentEbac),ebac);
        editor.putString(activity.getString(R.string.pref_key_last_updated_currentEbac), DatabaseOperationsUnits.getDateTime());
        editor.apply();
    }
    public float getCurrentEbac(){return sp.getFloat(activity.getString(R.string.pref_key_currentEbac),0);}

    /*
    * The alcVolPercentage arg is to be specified as a real number between 0 and 100
    * */
    public double getEstimatedBloodAlcoholContent(double mlSize, double alcVolPercentage){
        double volumeOfEthanol = mlSize*alcVolPercentage/100;
        double massOfAlcohol = volumeOfEthanol * DENSITY_OF_ETHANOL;//in grams
        double r = isMan ? MALE_R : FEMALE_R;

        return massOfAlcohol/(bodyWeight*r)*100;
    }

    public static void updateElapsedBAC(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        float currentEbac = sp.getFloat(context.getString(R.string.pref_key_currentEbac),0);
        if (currentEbac > 0) {
            String strLastUpdatedBAC = sp.getString(context.getString(R.string.pref_key_last_updated_currentEbac), "");
            String strCurrentDateTime = DatabaseOperationsUnits.getDateTime();

            DateFormat lastUpdatedBAC = new SimpleDateFormat(DatabaseOperationsUnits.STR_DATE_FORMAT,
                    DatabaseOperationsUnits.DATE_LOCALE);
            DateFormat currentDateTime = new SimpleDateFormat(DatabaseOperationsUnits.STR_DATE_FORMAT,
                    DatabaseOperationsUnits.DATE_LOCALE);

            Date lastUpdatedBACDate = null;
            Date currentDate = null;
            try {
                lastUpdatedBACDate = lastUpdatedBAC.parse(strLastUpdatedBAC);
                currentDate = currentDateTime.parse(strCurrentDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long minutesDifference = DatabaseOperationsUnits.getDateDiff(currentDate, lastUpdatedBACDate);
            double ebacSubtrahend = (minutesDifference / 60) * ELAPSED_HOUR_FACTOR;
            float newCurrentBAC = (currentEbac >= (float)ebacSubtrahend) ? currentEbac-(float)ebacSubtrahend : 0.0f;

            SharedPreferences.Editor e = sp.edit();
            e.putFloat(context.getString(R.string.pref_key_currentEbac), newCurrentBAC);
            e.putString(context.getString(R.string.pref_key_last_updated_currentEbac), strCurrentDateTime);
            e.apply();
        }
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
