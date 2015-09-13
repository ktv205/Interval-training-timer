package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NumberPickerDialog.OnNumberPickedListener,
        TimePickerDialog.OnTimePickedListener, ReviewFragment.OnStartTimerListener, FragmentManager.OnBackStackChangedListener, CurrentFragmentInterface {

    private static final String TIME_DIALOG = "time dialog";
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private int mTimes;
    private int mCount = 0;
    private String mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VaryingTimesFragment varyingTimesFragment = (VaryingTimesFragment) getFragmentManager()
                .findFragmentByTag(VaryingTimesFragment.class.getSimpleName());
        ReviewFragment reviewFragment = (ReviewFragment) getFragmentManager().findFragmentByTag(ReviewFragment.class.getSimpleName());
        TimerFragment timerFragment = (TimerFragment) getFragmentManager().findFragmentByTag(TimerFragment.class.getSimpleName());
        if (savedInstanceState == null) {
            if (varyingTimesFragment == null) {
                varyingTimesFragment = new VaryingTimesFragment();
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, varyingTimesFragment, VaryingTimesFragment.class.getSimpleName())
                    .commit();
            mCurrentFragment = VaryingTimesFragment.class.getSimpleName();
        } else {
            mCount = savedInstanceState.getInt(getString(R.string.current_count));
            mTimes = savedInstanceState.getInt(getString(R.string.exercise_number));
            mWorkoutModelArrayList = savedInstanceState.getParcelableArrayList(getString(R.string.workout_key));
            String tag = savedInstanceState.getString(getString(R.string.current_fragment));

            Fragment fragment = getFragmentManager().findFragmentByTag(tag);


        }
        getFragmentManager().addOnBackStackChangedListener(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(getString(R.string.fragment_added), true);
        outState.putInt(getString(R.string.exercise_number), mTimes);
        outState.putInt(getString(R.string.current_count), mCount);
        outState.putString(getString(R.string.current_fragment), mCurrentFragment);
        outState.putParcelableArrayList(getString(R.string.workout_key), mWorkoutModelArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTimePicked(String workoutName, int minutes, int seconds) {
        if (workoutName.isEmpty()) {
            workoutName = "Workout Name Here";
        }
        mWorkoutModelArrayList.add(new WorkoutModel(workoutName, minutes, seconds));
        mCount++;
        if (mCount == mTimes) {
            ReviewFragment reviewFragment = new ReviewFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.workout_key), mWorkoutModelArrayList);
            reviewFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, reviewFragment, ReviewFragment.class.getSimpleName())
                    .addToBackStack(VaryingTimesFragment.class.getSimpleName())
                    .commit();
            mCount = 0;
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
            Log.d(TAG, "here in the workout number");
            for (int i = number; i > 0; i--) {
                TimePickerDialog timePickerDialog = new TimePickerDialog();
                Bundle bundle = new Bundle();
                bundle.putInt(getString(R.string.time_picker_key), getResources().getInteger(R.integer.time_picker));
                bundle.putInt(getString(R.string.exercise_number), i);
                timePickerDialog.setArguments(bundle);
                timePickerDialog.show(getFragmentManager(), TIME_DIALOG);
            }
        } else {
            ReviewFragment reviewFragment = (ReviewFragment) getFragmentManager().findFragmentByTag(ReviewFragment.class.getSimpleName());
            if (reviewFragment != null) {
                reviewFragment.updateRepeatTimes(number);
            }
        }
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
            getFragmentManager().popBackStack(VaryingTimesFragment.class.getSimpleName(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Called whenever the contents of the back stack change.
     */
    @Override
    public void onBackStackChanged() {
        Log.d(TAG, "backstack changed");
    }

    @Override
    public void currentFragment(String tag) {
        mCurrentFragment = tag;
    }
}
