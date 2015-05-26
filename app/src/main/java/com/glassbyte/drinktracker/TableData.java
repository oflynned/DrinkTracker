package com.glassbyte.drinktracker;

/**
 * Created by ed on 26/05/15.
 */

import android.provider.BaseColumns;

public class TableData {
    public TableData(){

    }
    public static abstract class TableInfo implements BaseColumns{
        //column names
        public static final String HEIGHT = "height";
        public static final String WEIGHT = "weight";
        public static final String GENDER = "gender";
        public static final String DATABASE_NAME = "setup_prefs";
        public static final String TABLE_NAME = "reg_info";
    }
}