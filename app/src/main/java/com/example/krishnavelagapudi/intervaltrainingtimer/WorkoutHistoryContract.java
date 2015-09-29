package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.provider.BaseColumns;

/**
 * Created by krishnavelagapudi on 9/23/15.
 */
public class WorkoutHistoryContract {

    public static final class WorkoutEntry implements BaseColumns {
        public static final String TABLE_NAME="workout";
        public static final String WORKOUT_NAME = "workout_name";
        public static final String SETS = "sets";
        public static final String TIME_CREATED="time_stamp";

    }

    public static final class ExerciseEntry implements BaseColumns {
        public static final String TABLE_NAME="exercise";
        public static final String Exercise_NAME = "exercise_name";
        public static final String TIME = "time";

    }

    public static final class WorkoutExercisesEntry {
        public static final  String TABLE_NAME="workout_exercises";
        public static final String WORKOUT_ID = "workout_id";
        public static final String EXERCISE_ID = "exercise_id";
    }


}
