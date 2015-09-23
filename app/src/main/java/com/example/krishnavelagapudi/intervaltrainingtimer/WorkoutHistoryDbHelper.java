package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by krishnavelagapudi on 9/23/15.
 */
public class WorkoutHistoryDbHelper extends SQLiteOpenHelper {


    public WorkoutHistoryDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
