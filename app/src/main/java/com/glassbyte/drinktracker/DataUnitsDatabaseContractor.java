package com.glassbyte.drinktracker;

/**
 * Created by ed on 26/05/15.
 */

import android.provider.BaseColumns;

public class DataUnitsDatabaseContractor {
    public static final String DATABASE_NAME = "data_units";

    public DataUnitsDatabaseContractor(){}

    public static abstract class DataLoggingTable implements BaseColumns{
        //column names
        public static final String UNITS = "units";
        public static final String TIME = "time";
        public static final String PERCENTAGE = "percentage";
        public static final String BAC = "bac";
        public static final String TABLE_NAME = "data_logging";
    }
}