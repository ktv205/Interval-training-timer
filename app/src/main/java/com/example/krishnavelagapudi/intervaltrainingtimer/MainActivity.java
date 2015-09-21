package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NumberPickerDialog.OnNumberPickedListener,
        TimePickerDialog.OnTimePickedListener, ReviewFragment.OnStartTimerListener, NewWorkoutFragment.ExerciseNumber {

    private static final String TIME_DIALOG = "time dialog";
    private ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private int mTotalCount;
    private int mCurrentCount = 1;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean fromNotification = checkIfFromNotification();
        if (savedInstanceState == null) {
            if (!fromNotification) {
                Bundle bundle = new Bundle();
                NewWorkoutFragment newWorkoutFragment = NewWorkoutFragment.newInstance(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.relative_container, newWorkoutFragment, NewWorkoutFragment.class.getSimpleName())
                        .commit();
            } else {
                TimerFragment timerFragment;
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                NewWorkoutFragment newWorkoutFragment = NewWorkoutFragment.newInstance(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.relative_container, newWorkoutFragment, NewWorkoutFragment.class.getSimpleName())
                        .commit();
                timerFragment = TimerFragment.newInstance(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.relative_container, timerFragment, TimerFragment.class.getSimpleName())
                        .addToBackStack(NewWorkoutFragment.class.getSimpleName())
                        .commit();
                Log.d(TAG, "timer state->" + bundle.getInt(getString(R.string.timer_state)));
                getIntent().setData(null);
                setIntent(null);


            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "new intent");
        super.onNewIntent(intent);
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
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.workout_model), workoutModelArrayList);
        bundle.putInt(getString(R.string.set_number), number);
        bundle.putString(getString(R.string.workout_name), workoutName);
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
            getFragmentManager().popBackStack(NewWorkoutFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            setUpPreviewTimerLayout();
        } else {
            super.onBackPressed();
        }
    }

    private void setUpPreviewTimerLayout() {
        Log.d(TAG, "setUpPreviewTimerLayout");
        Intent intent = new Intent(this, TimerService.class);
        if (Utils.isMyServiceRunning(TimerService.class, this)) {
            bindService(intent, mConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void showExerciseNumberPickerDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.pick_number_for), getResources().getInteger(R.integer.exercise_number));
        NumberPickerDialog numberPickerDialog = NumberPickerDialog.newInstance(bundle);
        numberPickerDialog.show(getFragmentManager(), NumberPickerDialog.class.getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBound) {
            mService.stopMessages(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private boolean mBound;
    private TimerService mService;
    private LinearLayout mContainerLayout;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            mBound = true;
            mService = binder.getService();
            mService.setMessenger(mMessenger);
            mService.stopMessages(false);
            mContainerLayout = (LinearLayout) findViewById(R.id.linear_preview_container);
            mContainerLayout.setVisibility(View.VISIBLE);
            mContainerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBound) {
                        mService.stopMessages(true);
                        unbindService(mConnection);
                        mBound = false;
                        TimerFragment timerFragment = TimerFragment.newInstance(new Bundle());
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.relative_container, timerFragment, TimerFragment.class.getSimpleName())
                                .addToBackStack(NewWorkoutFragment.class.getSimpleName())
                                .commit();

                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };


    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (bundle.getBoolean(getString(R.string.timer_running))) {
                int exerciseTime = msg.arg1;
                int currentSet = msg.arg2;
                String exerciseName = bundle.getString(getString(R.string.exercise_name));
                TextView detailsTextView = (TextView) findViewById(R.id.workout_details_text_view);
                TextView timeTextView = (TextView) findViewById(R.id.workout_time_text_view);
                detailsTextView.setText(exerciseName + " Set-" + currentSet);
                timeTextView.setText(String.format("%02d", (exerciseTime / 60)) + ":"
                        + String.format("%02d", (exerciseTime % 60)));
            } else {
                mContainerLayout.setVisibility(View.GONE);
            }

        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

}
