package com.demo.offlinelocation.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nishi on 4/30/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    public static final int DAABASE_VERSION = 2;


    public static final String DATABASE_NAME = "location.db";

    public static final String TABLE_LOCATION = "location";


    public static final String l_ID = "ID";
    public static final String L_LAT = "LATITUDE";
    public static final String L_LONG = "LONGITUTE";
    public static final String L_ADDRESS = "ADDRESS";
    public static final String L_TIME = "TIME";


    public static final String CREATE_TABLE_LOCATION = " CREATE TABLE "
            + TABLE_LOCATION +
            " ( " +
            l_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            L_TIME + " DATETIME NOT NULL, " +
            L_LAT + " TEXT NOT NULL, " +
            L_LONG + " TEXT NOT NULL, " +
            L_ADDRESS + " CHAR(50) " +
            " ) ";


    public DatabaseHandler(Context context) {
        super(/*new DatabaseContext(context)*/context, DATABASE_NAME, null, DAABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        DBUpdation.tableUpgrade(sqLiteDatabase, TABLE_LOCATION, CREATE_TABLE_LOCATION);

    }
}
