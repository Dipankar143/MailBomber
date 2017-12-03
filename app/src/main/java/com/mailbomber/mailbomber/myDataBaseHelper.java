package com.mailbomber.mailbomber;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dipanker on 31/07/17.
 */

public class myDataBaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME="time.db";
    public static final int DB_VER=1;

    public static final String sql="CREATE TABLE TIMES (ID INT PRIMARY KEY NOT NULL,TIME INT);";

    public myDataBaseHelper(Context context) {
        super(context,DB_NAME,null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
