package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NumberPickerDialog.OnNumberPickedListener,
        TimePickerDialog.OnTimePickedListener, ReviewFragment.OnStartTimerListener, NewWorkoutFragment.ExerciseNumber, TimerFragment.OnInfoBarClickListener, StyleToolbar {

    private static final String TIME_DIALOG = "time dialog";
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private int mTotalCount;
    private int mCurrentCount = 1;
    private boolean mIsInfoBarAdded = false;
    private Toolbar mToolbar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        boolean fromNotification = checkIfFromNotification();
        if (savedInstanceState == null) {
            if (!fromNotification) {
                if (Utils.isMyServiceRunning(TimerService.class, this)) {
                    mIsInfoBarAdded = true;
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.relative_container, NewWorkoutFragment.newInstance(null), NewWorkoutFragment.class.getSimpleName())
                            .commit();
                    Bundle bundle = new Bundle();
                    bundle.putInt(getString(R.string.how_to_lay), getResources().getInteger(R.integer.info_bar));
                    bundle.putBoolean(getString(R.string.from_recent_apps), true);
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.relative_container, TimerFragment.newInstance(bundle), TimerFragment.class.getSimpleName())
                            .commit();
                } else {
                    NewWorkoutFragment newWorkoutFragment = NewWorkoutFragment.newInstance(null);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.relative_container, newWorkoutFragment, NewWorkoutFragment.class.getSimpleName())
                            .commit();
                }
            } else {
                Log.d(TAG,"from notification");
                TimerFragment timerFragment;
                NewWorkoutFragment newWorkoutFragment = NewWorkoutFragment.newInstance(null);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.relative_container, newWorkoutFragment, NewWorkoutFragment.class.getSimpleName())
                        .commit();
                Bundle bundle = getIntent().getExtras();
                timerFragment = TimerFragment.newInstance(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.relative_container, timerFragment, TimerFragment.class.getSimpleName())
                        .addToBackStack(NewWorkoutFragment.class.getSimpleName())
                        .commit();
            }
        } else {
            mTotalCount = savedInstanceState.getInt(getString(R.string.exercise_number), mTotalCount);
            mCurrentCount = savedInstanceState.getInt(getString(R.string.current_count), mCurrentCount);
            mWorkoutModelArrayList = savedInstanceState.getParcelableArrayList(getString(R.string.workout_model));
            mIsInfoBarAdded = savedInstanceState.getBoolean(getString(R.string.how_to_lay));
        }

    }


    private boolean checkIfFromNotification() {
        Intent intent = getIntent();
        boolean fromNotification = false;
        boolean launchedFromHistory = intent != null ? (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0 : false;
        Log.d(TAG, "launched from history->" + launchedFromHistory);
        if (intent != null && !launchedFromHistory) {
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
        outState.putBoolean(getString(R.string.how_to_lay), mIsInfoBarAdded);
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
                    .replace(R.id.relative_container, reviewFragment, ReviewFragment.class.getSimpleName())
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
        Bundle bundle = buildTimerFragmentBundle(workoutModelArrayList, number, 1, getResources().getInteger(R.integer.stop),
                0, workoutModelArrayList.get(0).getExerciseName(), workoutName);
        bundle.putBoolean(getString(R.string.from_review_fragment), true);
        TimerFragment timerFragment = TimerFragment.newInstance(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.relative_container, timerFragment, TimerFragment.class.getSimpleName())
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
            TimerFragment timerFragment = (TimerFragment) getFragmentManager().findFragmentByTag(TimerFragment.class.getSimpleName());
            getFragmentManager().popBackStack(NewWorkoutFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (timerFragment != null) {
                Bundle bundle;
                bundle = buildTimerFragmentBundle(timerFragment.mWorkoutModelArrayList, timerFragment.mTotalSets
                        , timerFragment.mCurrentSet, timerFragment.mPauseResumeFlag, timerFragment.mCurrentExerciseTime, timerFragment.mCurrentExerciseName, timerFragment.mWorkoutName);
                bundle.putInt(getString(R.string.how_to_lay), getResources().getInteger(R.integer.info_bar));
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.relative_container, TimerFragment.newInstance(bundle), TimerFragment.class.getSimpleName())
                        .commit();
                mIsInfoBarAdded = true;
            } else {
                mIsInfoBarAdded = false;
            }

        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void showExerciseNumberPickerDialog() {
        final Bundle bundle = new Bundle();
        if (mIsInfoBarAdded) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your current workout will be stopped.");
            builder.setTitle("Starting New Workout");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TimerFragment timerFragment = (TimerFragment) getFragmentManager().findFragmentByTag(TimerFragment.class.getSimpleName());
                    timerFragment.stopService();
                    getFragmentManager().beginTransaction().remove(timerFragment).commit();
                    bundle.putInt(getString(R.string.pick_number_for), getResources().getInteger(R.integer.exercise_number));
                    NumberPickerDialog numberPickerDialog = NumberPickerDialog.newInstance(bundle);
                    numberPickerDialog.show(getFragmentManager(), NumberPickerDialog.class.getSimpleName());
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            mIsInfoBarAdded = false;
        } else {
            bundle.putInt(getString(R.string.pick_number_for), getResources().getInteger(R.integer.exercise_number));
            NumberPickerDialog numberPickerDialog = NumberPickerDialog.newInstance(bundle);
            numberPickerDialog.show(getFragmentManager(), NumberPickerDialog.class.getSimpleName());
        }
    }

    @Override
    public void onInfoBarClick(ArrayList<WorkoutModel> workoutModelArrayList, int currentSet, int totalSets, int pauseResumeFlag, int currentTime, String currentExerciseName, String workoutName) {
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag(TimerFragment.class.getSimpleName())).commit();
        Bundle bundle = buildTimerFragmentBundle(workoutModelArrayList, totalSets, currentSet, pauseResumeFlag, currentTime, currentExerciseName, workoutName);
        TimerFragment timerFragment = TimerFragment.newInstance(bundle);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.relative_container, timerFragment, TimerFragment.class.getSimpleName())
                .addToBackStack(NewWorkoutFragment.class.getSimpleName())
                .commit();
    }

    private Bundle buildTimerFragmentBundle(ArrayList<WorkoutModel> workoutModelArrayList, int setNumber
            , int currentSet, int pauseResumeFlag, int time, String currentExerciseName, String workoutName) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.workout_model), workoutModelArrayList);
        bundle.putInt(getString(R.string.set_number), setNumber);
        bundle.putInt(getString(R.string.current_set), currentSet);
        bundle.putString(getString(R.string.workout_name), workoutName);
        bundle.putString(getString(R.string.exercise_name), currentExerciseName);
        bundle.putInt(getString(R.string.timer_state), pauseResumeFlag);
        bundle.putInt(getString(R.string.time), time);
        return bundle;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setToolbarStyle(int toolbarColor, int statusBarColor, int textColor, String title) {
        mToolbar.setBackgroundColor(ContextCompat.getColor(this, toolbarColor));
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, textColor));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, statusBarColor));
        mToolbar.setTitle(title);
    }
}
