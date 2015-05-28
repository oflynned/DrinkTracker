package com.glassbyte.drinktracker;

/**
 * Created by ed on 26/05/15.
 */

import android.provider.BaseColumns;

public class TableDataUnits {
    public TableDataUnits(){

    }
    public static abstract class TableInfoUnits implements BaseColumns{
        //column names
        public static final String UNITS = "units";
        public static final String STIME = "stime";
        public static final String ETIME = "etime";
        public static final String DURATION = "duration";
        public static final String DATABASE_NAME = "data_units";
        public static final String TABLE_NAME = "data_logging";
    }
}