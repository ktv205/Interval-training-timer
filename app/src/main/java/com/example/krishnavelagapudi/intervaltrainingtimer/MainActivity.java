package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NumberPickerDialog.OnNumberPickedListener,
        TimePickerDialog.OnTimePickedListener, ReviewFragment.OnStartTimerListener, NewWorkoutFragment.ExerciseNumber {

    private static final String TIME_DIALOG = "time dialog";
    private ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private int mTotalCount;
    private int mCurrentCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean fromNotification = checkIfFromNotification();
        if (!fromNotification) {
            if (savedInstanceState == null) {
                Bundle bundle = new Bundle();
                NewWorkoutFragment newWorkoutFragment = NewWorkoutFragment.newInstance(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, newWorkoutFragment, NewWorkoutFragment.class.getSimpleName())
                        .commit();
            }
        }else{
            Intent intent=getIntent();
            Bundle bundle=intent.getExtras();
            NewWorkoutFragment newWorkoutFragment = NewWorkoutFragment.newInstance(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, newWorkoutFragment, NewWorkoutFragment.class.getSimpleName())
                    .commit();
            TimerFragment timerFragment= TimerFragment.newInstance(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container,timerFragment,TimerFragment.class.getSimpleName())
                    .addToBackStack(NewWorkoutFragment.class.getSimpleName())
                    .commit();


        }
    }

    private boolean checkIfFromNotification() {
        Intent intent = getIntent();
        boolean fromNotification = false;
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.getBoolean(getString(R.string.from_notification))) {
                    fromNotification = true;
                }
            }
        }
        return fromNotification;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.exercise_number), mTotalCount);
        outState.putInt(getString(R.string.current_count), mCurrentCount);
        outState.putParcelableArrayList(getString(R.string.workout_model), mWorkoutModelArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTimePicked(String workoutName, int minutes, int seconds) {
        mWorkoutModelArrayList.add(new WorkoutModel(workoutName, minutes, seconds));

        if (mCurrentCount == mTotalCount) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.workout_model), mWorkoutModelArrayList);
            ReviewFragment reviewFragment = ReviewFragment.newInstance(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, reviewFragment, ReviewFragment.class.getSimpleName())
                    .addToBackStack(NewWorkoutFragment.class.getSimpleName())
                    .commit();
            mCurrentCount = 1;
        } else {
            mCurrentCount++;
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
        mTotalCount = number;
        if (key == getResources().getInteger(R.integer.exercise_number)) {
            mWorkoutModelArrayList.clear();
            mCurrentCount = 1;
            startTimePickerDialog();
        } else {
            ReviewFragment reviewFragment = (ReviewFragment) getFragmentManager().findFragmentByTag(ReviewFragment.class.getSimpleName());
            if (reviewFragment != null) {
                reviewFragment.updateRepeatTimes(number);
            }
        }
    }

    private void startTimePickerDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.pick_time_for), getResources().getInteger(R.integer.pick_time));
        bundle.putInt(getString(R.string.exercise_number), mCurrentCount);
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(bundle);
        timePickerDialog.show(getFragmentManager(), TIME_DIALOG);
    }

    @Override
    public void OnStartTimer(ArrayList<WorkoutModel> workoutModelArrayList, int number, String workoutName) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.workout_model), workoutModelArrayList);
        bundle.putInt(getString(R.string.set_number), number);
        bundle.putString(getString(R.string.workout_name), workoutName);
        TimerFragment timerFragment = TimerFragment.newInstance(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container, timerFragment, TimerFragment.class.getSimpleName())
                .addToBackStack(ReviewFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void pickSetsNumber() {
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.pick_number_for),
                getResources().getInteger(R.integer.set_number));
        NumberPickerDialog numberPickerDialog = NumberPickerDialog.newInstance(bundle);
        numberPickerDialog.show(getFragmentManager(), NumberPickerDialog.class.getSimpleName());
    }

    @Override
    public void editExercise(int position, WorkoutModel workoutModel) {
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.pick_time_for), getResources().getInteger(R.integer.change_time));
        bundle.putInt(getString(R.string.list_position), position);
        bundle.putParcelable(getString(R.string.workout_model), workoutModel);
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(bundle);
        timePickerDialog.show(getFragmentManager(), TimePickerDialog.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack(NewWorkoutFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showExerciseNumberPickerDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.pick_number_for), getResources().getInteger(R.integer.exercise_number));
        NumberPickerDialog numberPickerDialog = NumberPickerDialog.newInstance(bundle);
        numberPickerDialog.show(getFragmentManager(), NumberPickerDialog.class.getSimpleName());
    }
}
