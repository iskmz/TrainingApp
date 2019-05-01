package com.iskandar.trainingrecordapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSQLlite extends SQLiteOpenHelper {

    private static final String DB_NAME = "trainingData.db"; // name of the DB file in our phone
    private static final String DEFAULT_TABLE_NAME = "data"; // name of the data table in the DB
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
    private static final String TAG_YEAR_RANDOM = "1888";


    Context context;
    private SQLiteDatabase db; //an instance to our SqliteDatabase
    private List<String> usersTableList;

    public DataSQLlite(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        db = this.getWritableDatabase(); //set our data base to read and write mode.
        usersTableList = getTables();

        // dev. check
        Log.e("tbls","TABLES"+getListStr(usersTableList));
        // for dev. db access
        provideAccessToDev();
    }

    private String getListStr(List<String> usersTableList) {
        String str = "";
        for(String st : usersTableList) str+="\n"+st;
        return str;
    }

    private List<String> getTables() {
        List<String> lst = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master " +
                "WHERE type='table' AND name!='android_metadata'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() )
            {
                lst.add(c.getString(0));
                c.moveToNext();
            }
        }
        return lst;
    }

    private void provideAccessToDev() {
        if (BuildConfig.DEBUG)
        {
            new File(db.getPath()).setReadable(true, false);
        }

        //@terminal: adb -d pull //data/data/com.iskandar.trainingrecordapp/databases/trainingData.db
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create data table // DEFAULT one ! //
        String sqlStatment = "CREATE TABLE IF NOT EXISTS " + DEFAULT_TABLE_NAME + " (";
        sqlStatment += COL_DATE_NAME + " TEXT PRIMARY KEY,";
        sqlStatment += COL_runningTime_NAME + " TEXT,";
        sqlStatment += COL_runningDistance_NAME + " TEXT,";
        sqlStatment += COL_pushups_NAME + " TEXT,";
        sqlStatment += COL_other_NAME + " TEXT";
        sqlStatment += ")";
        db.execSQL(sqlStatment);
    }

    public void addUserTable(String tableName)
    {
        // add new data table for specified user //
        String sqlStatment = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
        sqlStatment += COL_DATE_NAME + " TEXT PRIMARY KEY,";
        sqlStatment += COL_runningTime_NAME + " TEXT,";
        sqlStatment += COL_runningDistance_NAME + " TEXT,";
        sqlStatment += COL_pushups_NAME + " TEXT,";
        sqlStatment += COL_other_NAME + " TEXT";
        sqlStatment += ")";
        db.execSQL(sqlStatment);

        // refresh userTablesList //
        usersTableList = getTables();
    }

    public Cursor getAllDataForUser(String userName){
        if(!usersTableList.contains(userName))
        {
            Log.e("err","no such username: ["+userName+"] exists!");
            return null;
        }
        Cursor res = db.rawQuery("SELECT * FROM "+ userName, null);
        return res;
    }

    public Cursor getTodaysDataForUser(String userName){
        return getDataAtDateForUser(getTodaysDate(),userName);
    }

    public Cursor getDataAtDateForUser(String date,String userName)
    {
        Cursor res = db.rawQuery("SELECT * FROM "+ userName +
                " WHERE "+COL_DATE_NAME+"="+date, null);
        return res;
    }

    public boolean addDataEntry(String date, String runTime, String runDist, String pushups, String other) {
        //create instance of ContentValues to hold our values
        ContentValues myValues = new ContentValues();
        //insert data by key and value
        myValues.put(COL_DATE_NAME, date);
        myValues.put(COL_runningTime_NAME, runTime);
        myValues.put(COL_runningDistance_NAME, runDist);
        myValues.put(COL_pushups_NAME, pushups);
        myValues.put(COL_other_NAME, other);

        // UPDATE if exist, otherwise INSERT new row //
        // put values in table and get res (row id) // if res = -1 then ERROR //
        long res;
        if(isEntryExist(date))
        {
            try {
                res = db.update(DEFAULT_TABLE_NAME, myValues, null, null);
            }
            catch (Exception e)
            {
                Log.e("sql",e.getMessage());
                res = db.insertWithOnConflict(DEFAULT_TABLE_NAME,null,myValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
        else
        {
            res = db.insert(DEFAULT_TABLE_NAME, null, myValues);
        }
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
        Cursor res = db.rawQuery("SELECT * FROM "+ DEFAULT_TABLE_NAME, null);
        return res;
    }

    public Cursor getTodaysData(){
        return getDataAtDate(getTodaysDate());
    }

    public Cursor getDataAtDate(String date)
    {
        Cursor res = db.rawQuery("SELECT * FROM "+ DEFAULT_TABLE_NAME +
                " WHERE "+COL_DATE_NAME+"="+date, null);
        return res;
    }

    public void deleteDataAtDate(String date) {
        db.execSQL("DELETE FROM "+ DEFAULT_TABLE_NAME +" WHERE "+COL_DATE_NAME+"=" + date);
    }

    public boolean isEntryExist(String date)
    {
        Cursor tmp = db.rawQuery("SELECT * FROM "+ DEFAULT_TABLE_NAME +
                " WHERE "+COL_DATE_NAME+"="+date, null);
        return tmp.getCount()!=0;
    }


    // for check purposes
    public void insertRandomData(int howMuch)
    {
        // deleteAllRows(); // to start FRESH , when errors occur //

        for(int i=0; i< howMuch; i+=1) {
            int randomMonth=i%12+1,randomDay=i%28+1;
            String randomMonthStr = (randomMonth<10?"0":"")+randomMonth;
            String randomDayStr = (randomDay<10?"0":"")+randomDay;
            String dt = TAG_YEAR_RANDOM + randomMonthStr + randomDayStr;
            String runTime = "" + (int) (Math.random() * InputActivity.MAX_MINUTES + 1);
            String runDist = "" + (int) (Math.random() * InputActivity.MAX_KILOMETERS + 1);
            String pushups = "" + (int) (Math.random() * InputActivity.MAX_PUSHUPS + 1);
            addDataEntry(dt, runTime, runDist, pushups, i % 3 == 0 ? "check check check!" : "");
        }
    }

    // for check purposes
    public void deleteAllRandomData() {

        db.delete(DEFAULT_TABLE_NAME, COL_DATE_NAME + " LIKE ?", new String[]{TAG_YEAR_RANDOM + '%'});
        /*
        db.delete(DEFAULT_TABLE_NAME, COL_DATE_NAME + " LIKE ?", new String[]{TAG_YEAR_RANDOM + '%' + "01"});
        db.delete(DEFAULT_TABLE_NAME, COL_DATE_NAME + " LIKE ?", new String[]{TAG_YEAR_RANDOM + '%' + "02"});
        db.delete(DEFAULT_TABLE_NAME, COL_DATE_NAME + " LIKE ?", new String[]{TAG_YEAR_RANDOM + '%' + "03"});
        */
    }

    // for check purposes
    private void deleteAllRows() {
        db.execSQL("DELETE FROM "+ DEFAULT_TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}


