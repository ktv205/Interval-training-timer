package com.example.krishnavelagapudi.intervaltrainingtimer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by krishnavelagapudi on 9/8/15.
 */
public class WorkoutModel implements Parcelable {

    String exerciceName;
    int min;
    int sec;

    public String getWorkoutName() {
        return exerciceName;
    }

    public int getMin() {
        return min;
    }

    public int getSec() {
        return sec;
    }

    public WorkoutModel(String workoutName, int min, int sec) {
        this.exerciceName = workoutName;
        this.min = min;
        this.sec = sec;
    }

    protected WorkoutModel(Parcel in) {
        exerciceName = in.readString();
        min = in.readInt();
        sec = in.readInt();
    }

    public static final Creator<WorkoutModel> CREATOR = new Creator<WorkoutModel>() {
        @Override
        public WorkoutModel createFromParcel(Parcel in) {
            return new WorkoutModel(in);
        }

        @Override
        public WorkoutModel[] newArray(int size) {
            return new WorkoutModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(exerciceName);
        dest.writeInt(min);
        dest.writeInt(sec);
    }



}
