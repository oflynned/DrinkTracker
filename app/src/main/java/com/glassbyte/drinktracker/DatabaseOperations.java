package com.glassbyte.drinktracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseOperations extends SQLiteOpenHelper {
    public static final int database_version = 1;
    public String CREATE_QUERY =
            "CREATE TABLE " +
                    TableData.TableInfo.TABLE_NAME +
                    "(" +
                    TableData.TableInfo.HEIGHT +
                    " TEXT," +
                    TableData.TableInfo.WEIGHT +
                    " TEXT," + //data type for table
                    TableData.TableInfo.UNITS_MEASUREMENT +
                    " TEXT," + //data type for table
                    TableData.TableInfo.GENDER +
                    " TEXT);";
    //; ends query field within query -> ()

    public DatabaseOperations(Context context) {
        super(context, TableData.TableInfo.DATABASE_NAME, null, database_version);
        Log.d("Database operations", "Created database successfully");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY); //create table
        Log.d("Database operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //insert data into database
    public void putInfo(DatabaseOperations DO, int height, int weight, String gender, String units_measurement){
        SQLiteDatabase SQ = DO.getWritableDatabase(); //writes data to database
        ContentValues CV = new ContentValues(); //create instance
        CV.put(TableData.TableInfo.HEIGHT, height); //coll 0
        CV.put(TableData.TableInfo.WEIGHT, weight); //coll 1
        CV.put(TableData.TableInfo.GENDER, gender); //coll 2
        CV.put(TableData.TableInfo.UNITS_MEASUREMENT, units_measurement); //coll 3
        long k = SQ.insert(TableData.TableInfo.TABLE_NAME, null, CV);
        Log.d("Database operations", "1 row inserted into database");
    }

    //retrieve database
    public Cursor getInfo(DatabaseOperations DO){
        //read database
        SQLiteDatabase SQ = DO.getReadableDatabase();
        //columns from database
        String[] col = {TableData.TableInfo.HEIGHT,TableData.TableInfo.WEIGHT, TableData.TableInfo.GENDER};
        //get data
        Cursor CR = SQ.query(TableData.TableInfo.TABLE_NAME,col,null,null,null,null,null);
        return CR;
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
