package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Maciej on 12/08/2015.
 */
//public class BloodAlcoholContent {
    /*Based on https://en.wikipedia.org/wiki/Blood_alcohol_content#Estimated_blood_alcohol_content_by_intake
    * weight in kg's
    * drinking period in hr's
    * 1 Standard Drink = 18ml of pure alcohol in a drink
    * */
    /*private final float BODY_WATER_IN_BLOOD = 0.806f;
    private final float CONVERT_FACTOR = 1.2f;
    private final float BODY_WATER_MEN = 0.58f;
    private final float BODY_WATER_WOMEN = 0.49f;
    private final float MEAN_METABOLISM_MEN = 0.015f;
    private final float MEAN_METABOLISM_WOMEN = 0.017f;
    private float bodyWeight;

    public static boolean isMan; //static to let it be altered by any of the activities and affect all other current objects

    public BloodAlcoholContent(boolean isMan, float bodyWeight){
        this.isMan = isMan;
        this.bodyWeight = bodyWeight;
    }

    public float getStandardDrinkFactor(float mlSize, float alcVolPercentage){
        return (mlSize*alcVolPercentage/100f)/18f;
    }

    //drinkingPeriod must be specified in hours
    public float getEstimatedBloodAlcoholContent(float[] mlSize, float[] alcVolPercentage, float drinkingPeriod) throws Exception {
        if(mlSize.length != alcVolPercentage.length)
            throw new java.lang.Exception("The mlSize and alcVolPercentage arrays must be of the same length.");

        float standardDrinkFactor = 0f;

        for(int i = 0; i < mlSize.length; i++){
            standardDrinkFactor += getStandardDrinkFactor(mlSize[i], alcVolPercentage[i]);
        }

        float bodyWater = isMan ? BODY_WATER_MEN : BODY_WATER_WOMEN;
        float metabolismMean = isMan ? MEAN_METABOLISM_MEN : MEAN_METABOLISM_WOMEN;

        return (BODY_WATER_IN_BLOOD * standardDrinkFactor * CONVERT_FACTOR) / (bodyWater * bodyWeight)
                - (metabolismMean * drinkingPeriod);
    }

    public void setIsMan(boolean isMan){this.isMan = isMan;}
    public void setBodyWeight(float bodyWeight){this.bodyWeight = bodyWeight;}
}*/
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

        public MetricSystemConverter(){}

        static public double convertOzToMillilitres(double oz){return oz*IMPERIAL_OZ_IN_ML;}

        static public double convertMillilitresToOz(double ml){return ml/IMPERIAL_OZ_IN_ML;}

        static public double convertPoundsToKilograms(double pounds){return pounds*POUND_IN_KG;}

        static public double convertKilogramsToPounds(double kg){return kg/POUND_IN_KG;}

        static public double convertStoneToKilograms(double stones){return stones*STONE_IN_KG;}

        static public double convertKilogramsToStones(double kg){return kg/STONE_IN_KG;}
    }
}
