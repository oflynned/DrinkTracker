package com.glassbyte.drinktracker;

/**
 * Created by ed on 26/05/15.
 */

import android.provider.BaseColumns;

public class DrinkTrackerDatabase {
    public static final String DATABASE_NAME = "drink_tracker_db";

    public DrinkTrackerDatabase(){}

    public static abstract class DrinksTable implements BaseColumns{
        //column names
        public static final String TABLE_NAME = "drinks_log";
        public static final String DATE_TIME = "time"; //int
        public static final String TITLE = "title"; //string
        public static final String PERCENTAGE = "percentage"; //double
        public static final String VOLUME = "drink_volume"; //int
        public static final String BAC = "bac"; //double
        public static final String UNITS = "units"; //double
    }

    public static abstract class BacTable implements BaseColumns {
        public static final String TABLE_NAME = "bac_log";
        public static final String DATE_TIME = "time"; //int - date in milliseconds stored as integer
        public static final String BAC = "bac"; //double
        public static final String UPDATE_TYPE = "update_type"; //int - TYPES: INSERT_DRINK_UPDATE=0; DECAY_UPDATE=1;

        //UPDATE_TYPES
        public static final int INSERT_NEW_UPDATE = 0;
        public static final int DECAY_UPDATE = 1;
    }

    public static abstract class DrinksBacRelationTable implements BaseColumns {
        public static final String TABLE_NAME = "dirnk_bac_relations";
        public static final String BAC_ID = "bac_id"; //int
        public static final String DRINK_ID = "drink_id"; //int
    }
}