package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NumberPickerDialog.OnNumberPickedListener, TimePickerDialog.OnTimePickedListener {

    private static final String TIME_DIALOG = "time dialog";
    private static final String VARYING_TIMES_TAG = "varying times";
    private static final String REVIEW_TAG = "review workout";
    private ArrayList<WorkoutModel> workoutModelList = new ArrayList<>();
    private int nTimes;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VaryingTimesFragment varyingTimesFragment = new VaryingTimesFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_container, varyingTimesFragment, VARYING_TIMES_TAG).commit();
    }

    @Override
    public void onNumberPicked(int number) {
        nTimes = number;
        for (int i = 0; i < number; i++) {
            TimePickerDialog timePickerDialog = new TimePickerDialog();
            timePickerDialog.show(getFragmentManager(), TIME_DIALOG);
        }


    }

    @Override
    public void onTimePicked(String workoutName, int minutes, int seconds) {
        workoutModelList.add(new WorkoutModel(workoutName, minutes, seconds));
        count++;
        if (count == nTimes) {
            ReviewFragment reviewFragment = new ReviewFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.workout_key), workoutModelList);
            reviewFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.frame_container, reviewFragment, REVIEW_TAG).commit();
        }

    }
}
