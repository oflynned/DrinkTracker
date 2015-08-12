package com.glassbyte.drinktracker;

/**
 * Created by Maciej on 12/08/2015.
 */
public class BloodAlcoholContent {
    /*Based on https://en.wikipedia.org/wiki/Blood_alcohol_content#Estimated_blood_alcohol_content_by_intake
    * weight in kg's
    * drinking period in hr's
    * 1 Standard Drink = 18ml of pure alcohol in a drink
    * */
    private final float BODY_WATER_IN_BLOOD = 0.806f;
    private final float CONVERT_FACTOR = 1.2f;
    private final float BODY_WATER_MEN = 0.58f;
    private final float BODY_WATER_WOMEN = 0.49f;
    private final float MEAN_METABOLISM_MEN = 0.015f;
    private final float MEAN_METABOLISM_WOMEN = 0.017f;
    private boolean isMan;
    private float bodyWeight;

    public BloodAlcoholContent(boolean isMan, float bodyWeight){
        this.isMan = isMan;
        this.bodyWeight = bodyWeight;
    }

    public float getStandardDrinkFactor(float mlSize, float alcVolPercentage){
        return (mlSize*alcVolPercentage/100f)/18f;
    }

    /*drinkingPeriod must be specified in hours*/
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
}
