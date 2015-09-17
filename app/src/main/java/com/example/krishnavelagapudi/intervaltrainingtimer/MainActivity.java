package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NumberPickerDialog.OnNumberPickedListener,
        TimePickerDialog.OnTimePickedListener, ReviewFragment.OnStartTimerListener {

    private static final String TIME_DIALOG = "time dialog";
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private int mTimes;
    private int mCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VaryingTimesFragment varyingTimesFragment = (VaryingTimesFragment) getFragmentManager()
                .findFragmentByTag(VaryingTimesFragment.class.getSimpleName());
        TimerFragment timerFragment = (TimerFragment) getFragmentManager().findFragmentByTag(TimerFragment.class.getSimpleName());
        if (getIntent().getExtras() != null) {
            if (timerFragment == null) {
                timerFragment = new TimerFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(getString(R.string.workout_key), getIntent().getParcelableArrayListExtra(getString(R.string.workout_key)));
                bundle.putInt(getString(R.string.repeat_times), getIntent().getIntExtra(getString(R.string.repeat_times), 0));
                bundle.putString(getString(R.string.workout_title), getIntent().getStringExtra(getString(R.string.workout_title)));
                timerFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new VaryingTimesFragment(), VaryingTimesFragment.class.getSimpleName()).commit();
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, timerFragment, TimerFragment.class.getSimpleName())
                        .addToBackStack(VaryingTimesFragment.class.getSimpleName())
                        .commit();
            }
        }else {
            if (savedInstanceState == null) {
                if (varyingTimesFragment == null) {
                    varyingTimesFragment = new VaryingTimesFragment();
                }
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, varyingTimesFragment, VaryingTimesFragment.class.getSimpleName())
                        .commit();
            } else {
                mCount = savedInstanceState.getInt(getString(R.string.current_count));
                mTimes = savedInstanceState.getInt(getString(R.string.exercise_number));
                mWorkoutModelArrayList = savedInstanceState.getParcelableArrayList(getString(R.string.workout_key));

            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.exercise_number), mTimes);
        outState.putInt(getString(R.string.current_count), mCount);
        outState.putParcelableArrayList(getString(R.string.workout_key), mWorkoutModelArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTimePicked(String workoutName, int minutes, int seconds) {
        if (workoutName.isEmpty()) {
            workoutName = "Workout Name Here";
        }
        mWorkoutModelArrayList.add(new WorkoutModel(workoutName, minutes, seconds));

        if (mCount == mTimes) {
            ReviewFragment reviewFragment = new ReviewFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.workout_key), mWorkoutModelArrayList);
            reviewFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, reviewFragment, ReviewFragment.class.getSimpleName())
                    .addToBackStack(VaryingTimesFragment.class.getSimpleName())
                    .commit();
            mCount = 1;
        } else {
            mCount++;
            startTimePickerDialog();
        }

    }

    @Override
    public void onTimePicked(String workoutName, int minutes, int seconds, int position) {
        ReviewFragment reviewFragment = (ReviewFragment) getFragmentManager().findFragmentByTag(ReviewFragment.class.getSimpleName());
        if (reviewFragment != null) {
            reviewFragment.changeTime(new WorkoutModel(workoutName, minutes, seconds), position);
        }
    }


    @Override
    public void onNumberPicked(int number, int key) {
        mTimes = number;
        if (key == getResources().getInteger(R.integer.workout_number)) {
            mWorkoutModelArrayList.clear();
            mCount = 1;
            startTimePickerDialog();
        } else {
            ReviewFragment reviewFragment = (ReviewFragment) getFragmentManager().findFragmentByTag(ReviewFragment.class.getSimpleName());
            if (reviewFragment != null) {
                reviewFragment.updateRepeatTimes(number);
            }
        }
    }

    private void startTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.time_picker_key), getResources().getInteger(R.integer.time_picker));
        bundle.putInt(getString(R.string.exercise_number), mCount);
        timePickerDialog.setArguments(bundle);
        timePickerDialog.show(getFragmentManager(), TIME_DIALOG);
    }

    @Override
    public void OnStartTimer(ArrayList<WorkoutModel> workoutModelArrayList, int number, String workoutName) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.workout_key), workoutModelArrayList);
        bundle.putInt(getString(R.string.repeat_times), number);
        bundle.putString(getString(R.string.workout_title), workoutName);
        TimerFragment timerFragment = new TimerFragment();
        timerFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container, timerFragment, TimerFragment.class.getSimpleName())
                .addToBackStack(ReviewFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack(VaryingTimesFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }
    }

}
