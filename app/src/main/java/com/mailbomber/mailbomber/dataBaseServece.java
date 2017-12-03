package com.mailbomber.mailbomber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by dipanker on 31/07/17.
 */

public class dataBaseServece {
    Context mContext;
    myDataBaseHelper myDataBaseHelper;
    SQLiteDatabase sqLiteDatabase;

    public dataBaseServece(Context context) {
        mContext = context;
        myDataBaseHelper=new myDataBaseHelper(mContext);
    }

    //open
    public void open() throws SQLException{
        sqLiteDatabase=myDataBaseHelper.getReadableDatabase();
    }

    //close
    public void close(){
        sqLiteDatabase.close();
    }

    //insert

    public void insert(Long time){
        ContentValues contentValues=new ContentValues();
        contentValues.put("TIME",time);
        contentValues.put("ID",1);
        sqLiteDatabase.insert("TIMES",null,contentValues);
    }

    //select

    public Cursor select(){
        String qury="SELECT * FROM TIMES WHERE ID=1";
        Cursor cursor=sqLiteDatabase.rawQuery(qury,null);
        cursor.moveToFirst();
        return cursor;
    }

    // update

    public void update(Long time){
        ContentValues contentValues=new ContentValues();
        contentValues.put("TIME",time);
        sqLiteDatabase.update("TIMES",contentValues,"ID=1",null);
    }
}
