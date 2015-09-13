package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by krishnavelagapudi on 9/9/15.
 */
public class TimerFragment extends Fragment {
    private static final String TAG = TimerFragment.class.getSimpleName();
    private static final int STOP = 2;
    TextView mTimeTextView;
    int mRepeatTimes;
    ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private TextView mTitleTextView;
    private Timer mTimer;
    final static int PAUSE = 0;
    final static int RESUME = 1;
    int mPauseResumeFlag = RESUME;
    int mTotalTime;
    int mWorkoutNumber;
    int mTotalSets;
    private TextView mSetsTextView;
    boolean mIsFinished = false;
    private Button mPauseResumeButton;
    private CurrentFragmentInterface mCurrentFragmentInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "oncreate");
        this.mCurrentFragmentInterface = (CurrentFragmentInterface) getActivity();
        mCurrentFragmentInterface.currentFragment(TimerFragment.class.getSimpleName());
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        mTimeTextView = (TextView) view.findViewById(R.id.time_text_view);
        mTitleTextView = (TextView) view.findViewById(R.id.title_text_view);
        mSetsTextView = (TextView) view.findViewById(R.id.sets_textView);
        ArrayList<WorkoutModel> workoutModelArrayList = getArguments().getParcelableArrayList(getString(R.string.workout_key));
        mWorkoutModelArrayList = workoutModelArrayList;
        if (savedInstanceState == null) {
            mWorkoutNumber = 1;
            mTotalTime = -1;
            mRepeatTimes = getArguments().getInt(getString(R.string.repeat_times));
            mTotalSets = mRepeatTimes;
        } else {
            mWorkoutNumber = savedInstanceState.getInt(getString(R.string.workout_number));
            mTotalTime = savedInstanceState.getInt(getString(R.string.total_time));
            mRepeatTimes = savedInstanceState.getInt(getString(R.string.repeat_times));
            mPauseResumeFlag = savedInstanceState.getInt(getString(R.string.state), mPauseResumeFlag);
            mTotalSets = getArguments().getInt(getString(R.string.repeat_times));
            mSetsTextView.setText("Set " + (mTotalSets - mRepeatTimes));
        }
        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setTitle(getArguments().getString(getString(R.string.workout_title)));


        mPauseResumeButton = (Button) view.findViewById(R.id.pause_resume_button);
        if (mPauseResumeFlag == PAUSE) {
            mPauseResumeButton.setText(R.string.resume);
            mTimeTextView.setText(String.format("%02d", (mTotalTime / 60)) + ":" + String.format("%02d", (mTotalTime % 60)));
        }
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new IntervalTimerTask(), 0, 1000);
        mPauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPauseResumeButton.getText().toString().equals(getString(R.string.pause))) {
                    mPauseResumeButton.setText(R.string.resume);
                    mPauseResumeFlag = PAUSE;
                } else if (mPauseResumeButton.getText().toString().equals(getString(R.string.resume))) {
                    mPauseResumeButton.setText(R.string.pause);
                    mPauseResumeFlag = RESUME;
                } else {
                    mPauseResumeButton.setText(getString(R.string.pause));
                    mPauseResumeFlag = RESUME;
                    reset();
                }
            }
        });

        return view;
    }

    private void reset() {
        mWorkoutNumber = 1;
        mTotalTime = -1;
        mRepeatTimes = mTotalSets;
        mTimer.purge();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new IntervalTimerTask(), 0, 1000);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.workout_number), mWorkoutNumber);
        outState.putInt(getString(R.string.repeat_times), mRepeatTimes);
        outState.putInt(getString(R.string.total_time), mTotalTime);
        outState.putInt(getString(R.string.state), mPauseResumeFlag);
        outState.putBoolean(getString(R.string.finished), mIsFinished);
        super.onSaveInstanceState(outState);

    }

    public class IntervalTimerTask extends TimerTask {


        @Override
        public void run() {
            if (mTotalTime == -1) {
                mRepeatTimes--;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSetsTextView.setText("Set " + (mTotalSets - mRepeatTimes));
                        }
                    });
                }
            }
            while (mWorkoutNumber <= mWorkoutModelArrayList.size()) {
                final WorkoutModel workoutModel = mWorkoutModelArrayList.get(mWorkoutNumber - 1);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTitleTextView.setText(workoutModel.getWorkoutName());
                        }
                    });
                }
                if (mTotalTime == -1) {
                    if (workoutModel.getMin() > 0) {
                        mTotalTime = workoutModel.getMin() * 60;
                    }
                    mTotalTime = mTotalTime + workoutModel.getSec();
                }
                while (mTotalTime >= 0) {
                    if (mPauseResumeFlag == RESUME) {
                        final String time = String.format("%02d", (mTotalTime / 60)) + ":" + String.format("%02d", (mTotalTime % 60));
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTimeTextView.setText(time);
                                }
                            });
                            mTotalTime--;
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mTotalTime == 5) {
                        int resID = getResources().getIdentifier("countdown", "raw", getActivity().getPackageName());
                        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), resID);
                        mediaPlayer.start();
                    }

                }
                mWorkoutNumber++;

            }
            mWorkoutNumber = 1;
            mTotalTime = -1;
            if (mRepeatTimes <= 0) {
                mIsFinished = true;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPauseResumeButton.setText(getString(R.string.start_again));
                    }
                });

                mPauseResumeFlag = STOP;
                mTimer.cancel();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        if(mTimer!=null) {
            mTimer.cancel();
        }
    }
}
