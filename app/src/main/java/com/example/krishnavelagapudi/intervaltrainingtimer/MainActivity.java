package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NumberPickerDialog.OnNumberPickedListener,
        TimePickerDialog.OnTimePickedListener, ReviewFragment.OnStartTimerListener {

    private static final String TIME_DIALOG = "time dialog";
    private static final String VARYING_TIMES_FRAGMENT_TAG = "varying times";
    private static final String REVIEW_FRAGMENT_TAG = "review workout";
    private static final String TIMER_FRAGMENT_TAG = "timer fragment";
    private ArrayList<WorkoutModel> workoutModelList = new ArrayList<>();
    private int nTimes;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VaryingTimesFragment varyingTimesFragment = (VaryingTimesFragment) getFragmentManager().findFragmentByTag(VARYING_TIMES_FRAGMENT_TAG);
        ReviewFragment reviewFragment = (ReviewFragment) getFragmentManager().findFragmentByTag(REVIEW_FRAGMENT_TAG);
        if (reviewFragment == null) {
            if (varyingTimesFragment == null) {
                varyingTimesFragment = new VaryingTimesFragment();
            }
            getFragmentManager().beginTransaction().replace(R.id.frame_container, varyingTimesFragment, VARYING_TIMES_FRAGMENT_TAG).commit();
        }
    }


    @Override
    public void onTimePicked(String workoutName, int minutes, int seconds) {
        if (workoutName.isEmpty()) {
            workoutName = "Workout Name Here";
        }
        workoutModelList.add(new WorkoutModel(workoutName, minutes, seconds));
        count++;
        if (count == nTimes) {
            ReviewFragment reviewFragment = new ReviewFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.workout_key), workoutModelList);
            reviewFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.frame_container, reviewFragment, REVIEW_FRAGMENT_TAG).commit();
        }

    }

    @Override
    public void onTimePicked(String workoutName, int minutes, int seconds, int position) {
        ReviewFragment reviewFragment = (ReviewFragment) getFragmentManager().findFragmentByTag(REVIEW_FRAGMENT_TAG);
        if (reviewFragment != null) {
            reviewFragment.changeTime(minutes, seconds, position);
        }
    }

    @Override
    public void onNumberPicked(int number, int key) {
        nTimes = number;
        if (key == getResources().getInteger(R.integer.workout_number)) {
            for (int i = 0; i < number; i++) {
                TimePickerDialog timePickerDialog = new TimePickerDialog();
                Bundle bundle = new Bundle();
                bundle.putInt(getString(R.string.time_picker_key), getResources().getInteger(R.integer.time_picker));
                timePickerDialog.setArguments(bundle);
                timePickerDialog.show(getFragmentManager(), TIME_DIALOG);
            }
        } else {
            ReviewFragment reviewFragment = (ReviewFragment) getFragmentManager().findFragmentByTag(REVIEW_FRAGMENT_TAG);
            if (reviewFragment != null) {
                reviewFragment.updateRepeatTimes(number);
            }
        }
    }

    @Override
    public void OnStartTimer(ArrayList<WorkoutModel> workoutModelArrayList, int number) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.workout_key), workoutModelArrayList);
        bundle.putInt(getString(R.string.select_workout_number), number);
        TimerFragment timerFragment = new TimerFragment();
        timerFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frame_container, timerFragment, TIMER_FRAGMENT_TAG).commit();
    }
}
