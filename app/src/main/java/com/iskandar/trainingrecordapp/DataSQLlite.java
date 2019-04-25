package com.iskandar.trainingrecordapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataSQLlite extends SQLiteOpenHelper {

    private static final String DB_NAME = "trainingData.db"; // name of the DB file in our phone
    private static final String TABLE_NAME = "data"; // name of the data table in the DB
    // columns names & numbers
    public static final int COL_DATE = 0;
    private static final String COL_DATE_NAME = "date";
    public static final int COL_runningTime = 1;
    private static final String COL_runningTime_NAME = "runningTime";
    public static final int COL_runningDistance = 2;
    private static final String COL_runningDistance_NAME = "runningDistance";
    public static final int COL_pushups = 3;
    private static final String COL_pushups_NAME = "pushups";
    public static final int COL_other = 4;
    private static final String COL_other_NAME = "other";

    public static final String DATE_FORMAT_PATTERN = "yyyyMMdd";

    Context context;
    private SQLiteDatabase db; //an instance to our SqliteDatabase

    public DataSQLlite(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        db = this.getWritableDatabase(); //set our data base to read and write mode.
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create data table
        String sqlStatment = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (";
        sqlStatment += COL_DATE_NAME + " TEXT PRIMARY KEY,";
        sqlStatment += COL_runningTime_NAME + " TEXT,";
        sqlStatment += COL_runningDistance_NAME + " TEXT,";
        sqlStatment += COL_pushups_NAME + " TEXT,";
        sqlStatment += COL_other_NAME + " TEXT";
        sqlStatment += ")";
        db.execSQL(sqlStatment);
    }

    private boolean addDataEntry(String date, String runTime, String runDist, String pushups, String other) {
        //create instance of ContentValues to hold our values
        ContentValues myValues = new ContentValues();
        //insert data by key and value
        myValues.put(COL_DATE_NAME, date);
        myValues.put(COL_runningTime_NAME, runTime);
        myValues.put(COL_runningDistance_NAME, runDist);
        myValues.put(COL_pushups_NAME, pushups);
        myValues.put(COL_other_NAME, other);
        //putting our values into table and getting a result which reflect record id
        //if we get -1, we had an error
        long res = db.insert(TABLE_NAME, null, myValues);
        //return true if we not get -1, error
        return res != (-1);
    }


    public boolean addDataEntryToday(String runTime, String runDist, String pushups, String other) {
        return addDataEntry(getTodaysDate(), runTime, runDist, pushups, other);
    }

    private String getTodaysDate() {
        return new SimpleDateFormat(DATE_FORMAT_PATTERN).format(new Date());
    }

    public Cursor getAllData(){
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        return res;
    }

    public Cursor getTodaysData(){
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME+
                " WHERE "+COL_DATE_NAME+"="+getTodaysDate(), null);
        return res;
    }

    public void deleteDataAtDate(String date) {
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE "+COL_DATE_NAME+"=" + date);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}


