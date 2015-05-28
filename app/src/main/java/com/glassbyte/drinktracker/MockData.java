package com.glassbyte.drinktracker;

/**
 * Created by Alex on 28/05/15.
 */
import java.util.Random;

public class MockData {
    public static Point getDataFromReceiver(int x){
        return new Point(x, generateRandomData());
    }
    private static int generateRandomData(){

        Random random = new Random();
        return random.nextInt(40);
    }

}
