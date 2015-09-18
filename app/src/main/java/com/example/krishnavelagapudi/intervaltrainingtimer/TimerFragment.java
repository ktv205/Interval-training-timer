package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

/**
 * Created by krishnavelagapudi on 9/9/15.
 */
public class TimerFragment extends Fragment {
    private static final String TAG = TimerFragment.class.getSimpleName();
    TextView mTimeTextView;
    int mCurrentSet;
    ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private TextView mTitleTextView;
    int mPauseResumeFlag=1;
    int mTotalTime;
    int mTotalSets;
    private TextView mSetsTextView;
    boolean mIsFinished = false;
    private Button mPauseResumeButton;
    private String mExerciseName;


    public static TimerFragment newInstance(Bundle bundle) {

        Bundle args = bundle;

        TimerFragment fragment = new TimerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Intent intent = new Intent(getActivity(), TimerService.class);
        if (!isMyServiceRunning(TimerService.class, getActivity())) {
            getActivity().startService(intent);
        }
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        mTimeTextView = (TextView) view.findViewById(R.id.time_text_view);
        mTitleTextView = (TextView) view.findViewById(R.id.title_text_view);
        mSetsTextView = (TextView) view.findViewById(R.id.sets_textView);
        mPauseResumeButton = (Button) view.findViewById(R.id.pause_resume_button);
        ArrayList<WorkoutModel> workoutModelArrayList = getArguments().getParcelableArrayList(getString(R.string.workout_model));
        mWorkoutModelArrayList = workoutModelArrayList;
        mWorkoutName = getArguments().getString(getString(R.string.workout_name));
        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setTitle(mWorkoutName);
        boolean fromNotification = getArguments().getBoolean(getString(R.string.from_notification));
        if (fromNotification) {
            mTimeTextView.setText(getArguments().getString(getString(R.string.time)));
            mPauseResumeFlag = getArguments().getInt(getString(R.string.timer_state));
            mCurrentSet = getArguments().getInt(getString(R.string.current_set));
            mTotalSets = getArguments().getInt(getString(R.string.set_number));
            mExerciseName = getArguments().getString(getString(R.string.exercise_name));
            mSetsTextView.setText("Set " + (mCurrentSet));
            mTitleTextView.setText(mExerciseName);
        } else {
            mTimeTextView.setText(String.format("%02d", (mTotalTime / 60)) + ":" + String.format("%02d", (mTotalTime % 60)));
        }
        if (savedInstanceState == null) {
            mTotalSets = getArguments().getInt(getString(R.string.set_number));
        } else {
            mTotalTime = savedInstanceState.getInt(getString(R.string.total_time));
            mPauseResumeFlag = savedInstanceState.getInt(getString(R.string.timer_state), mPauseResumeFlag);
            mTotalSets = getArguments().getInt(getString(R.string.set_number));
            mCurrentSet = savedInstanceState.getInt(getString(R.string.current_set));
            mExerciseName = savedInstanceState.getString(getString(R.string.exercise_name));
            mSetsTextView.setText("Set " + (mCurrentSet));
            mTitleTextView.setText(mExerciseName);
            mTimeTextView.setText(String.format("%02d", (mTotalTime / 60)) + ":" + String.format("%02d", (mTotalTime % 60)));
        }
        if (mPauseResumeFlag == getResources().getInteger(R.integer.pause)) {
            mPauseResumeButton.setText(R.string.resume);
        } else if (mPauseResumeFlag == getResources().getInteger(R.integer.stop)) {
            mPauseResumeButton.setText(getString(R.string.start_again));
        }
        mPauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPauseResumeButton.getText().toString().equals(getString(R.string.pause))) {
                    mPauseResumeButton.setText(R.string.resume);
                    mPauseResumeFlag = getResources().getInteger(R.integer.pause);
                    mService.pauseResumeTimer(getResources().getInteger(R.integer.pause));
                } else if (mPauseResumeButton.getText().toString().equals(getString(R.string.resume))) {
                    mPauseResumeButton.setText(R.string.pause);
                    mPauseResumeFlag = getResources().getInteger(R.integer.resume);
                    mService.pauseResumeTimer(getResources().getInteger(R.integer.resume));
                } else {
                    mPauseResumeButton.setText(getString(R.string.pause));
                    mPauseResumeFlag = getResources().getInteger(R.integer.resume);
                    mService.pauseResumeTimer(getResources().getInteger(R.integer.resume));
                    mIsFinished = false;
                }
            }
        });


        return view;
    }

    /*private void reset() {
        mExerciseNumber = 1;
        mTotalTime = -1;
        mRepeatTimes = mTotalSets;
        mTimer.purge();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new IntervalTimerTask(), 0, 1000);
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.current_set), mCurrentSet);
        outState.putInt(getString(R.string.total_time), mTotalTime);
        outState.putInt(getString(R.string.timer_state), mPauseResumeFlag);
        outState.putBoolean(getString(R.string.finished), mIsFinished);
        outState.putString(getString(R.string.exercise_name), mExerciseName);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onStop");
        if (mBound) {
            mService.stopMessages(true);
            getActivity().unbindService(mConnection);
            mBound = false;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean mBound;
    private TimerService mService;
    private String mWorkoutName;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            Log.d(TAG, "onServiceConnected");
            mService = binder.getService();
            mService.stopMessages(false);
            mBound = true;
            mService.setMessenger(mMessenger);
            if (!mService.isTimerRunning()) {
                mService.setWorkoutArrayList(mWorkoutModelArrayList, mWorkoutName, mTotalSets);
                mService.pauseResumeTimer(mPauseResumeFlag);
            }
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
            if (msg.arg1 > 0) {
                mCurrentSet = msg.arg1;
                mSetsTextView.setText("Set " + mCurrentSet);
            } else {
                if (msg.arg1 == -1) {
                    mPauseResumeFlag = getResources().getInteger(R.integer.stop);
                    mIsFinished = true;
                    mPauseResumeButton.setText(getString(R.string.start_again));
                }
            }
            Bundle bundle = msg.getData();
            if (bundle != null) {
                String time = bundle.getString(getString(R.string.time));
                if (time != null) {
                    mTimeTextView.setText(time);
                    mTotalTime = msg.arg2;
                }
                String exerciseName = bundle.getString(getString(R.string.exercise_name));
                if (exerciseName != null) {
                    mTitleTextView.setText(exerciseName);
                    mExerciseName = exerciseName;

                }
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public static boolean isMyServiceRunning(Class<?> serviceClass,
                                             Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        boolean running = false;
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                running = true;
            }
        }
        return running;
    }
}
