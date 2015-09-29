package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by krishnavelagapudi on 9/23/15.
 */
public class WorkoutHistoryDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "workout.db";

    public WorkoutHistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WORKOUT_TABLE = "CREATE TABLE " +
                WorkoutHistoryContract.WorkoutEntry.TABLE_NAME + " (" +
                WorkoutHistoryContract.WorkoutEntry._ID + " INTEGER PRIMARY KEY," +
                WorkoutHistoryContract.WorkoutEntry.WORKOUT_NAME + " TEXT NOT NULL," +
                WorkoutHistoryContract.WorkoutEntry.SETS + " INT NOT NULL," +
                WorkoutHistoryContract.WorkoutEntry.TIME_CREATED+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL"+
                " );";
        final String SQL_CREATE_EXERCISE_TABLE = "CREATE TABLE " +
                WorkoutHistoryContract.ExerciseEntry.TABLE_NAME + " (" +
                WorkoutHistoryContract.ExerciseEntry._ID + " INTEGER PRIMARY KEY," +
                WorkoutHistoryContract.ExerciseEntry.Exercise_NAME + " TEXT NOT NULL," +
                WorkoutHistoryContract.ExerciseEntry.TIME + " INT NOT NULL" +
                " );";
        final String SQL_CREATE_WORKOUT_EXERCISE_TABLE = "CREATE TABLE " +
                WorkoutHistoryContract.WorkoutExercisesEntry.TABLE_NAME + " (" +
                WorkoutHistoryContract.WorkoutExercisesEntry.WORKOUT_ID + " INTEGER NOT NULL," +
                WorkoutHistoryContract.WorkoutExercisesEntry.EXERCISE_ID + " INTEGER NOT NULL" +
                " );";
        db.execSQL(SQL_CREATE_WORKOUT_TABLE);
        db.execSQL(SQL_CREATE_EXERCISE_TABLE);
        db.execSQL(SQL_CREATE_WORKOUT_EXERCISE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkoutHistoryContract.WorkoutEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WorkoutHistoryContract.ExerciseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WorkoutHistoryContract.WorkoutExercisesEntry.TABLE_NAME);
        onCreate(db);
    }
}
