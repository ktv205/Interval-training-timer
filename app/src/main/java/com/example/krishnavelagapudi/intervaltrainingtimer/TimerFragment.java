package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        mTimeTextView = (TextView) view.findViewById(R.id.time_text_view);
        mTitleTextView = (TextView) view.findViewById(R.id.title_text_view);
        ArrayList<WorkoutModel> workoutModelArrayList = getArguments().getParcelableArrayList(getString(R.string.workout_key));
        mWorkoutModelArrayList = workoutModelArrayList;
        if (savedInstanceState == null) {
            mWorkoutNumber = 1;
            mTotalTime = 0;
            mRepeatTimes = getArguments().getInt(getString(R.string.select_workout_number));
        } else {
            mWorkoutNumber = savedInstanceState.getInt(getString(R.string.workout_number));
            mTotalTime = savedInstanceState.getInt(getString(R.string.total_time));
            mRepeatTimes = savedInstanceState.getInt(getString(R.string.repeat_times));
            mPauseResumeFlag = savedInstanceState.getInt(getString(R.string.state), mPauseResumeFlag);
        }

        final Button pauseResumeButton = (Button) view.findViewById(R.id.pause_resume_button);
        if (mPauseResumeFlag == PAUSE) {
            pauseResumeButton.setText(R.string.resume);
            mTimeTextView.setText(String.format("%02d", (mTotalTime / 60)) + ":" + String.format("%02d", (mTotalTime % 60)));
        }
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(timer, 0, 1000);
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pauseResumeButton.getText().toString().equals(getString(R.string.pause))) {
                    pauseResumeButton.setText(R.string.resume);
                    mPauseResumeFlag = PAUSE;
                } else {
                    pauseResumeButton.setText(R.string.pause);
                    mPauseResumeFlag = RESUME;
                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.workout_number), mWorkoutNumber);
        outState.putInt(getString(R.string.repeat_times), mRepeatTimes);
        outState.putInt(getString(R.string.total_time), mTotalTime);
        outState.putInt(getString(R.string.state), mPauseResumeFlag);
        super.onSaveInstanceState(outState);

    }

    TimerTask timer = new TimerTask() {

        @Override
        public void run() {
            if (mTotalTime == 0) {
                mRepeatTimes--;
            }
            while (mWorkoutNumber > 0) {
                final WorkoutModel workoutModel = mWorkoutModelArrayList.get(mWorkoutNumber - 1);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTitleTextView.setText(workoutModel.getWorkoutName());
                        }
                    });
                }
                if (mTotalTime == 0) {
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

                }
                mWorkoutNumber--;

            }
            if (mRepeatTimes <= 0) {
                mTimer.cancel();
            }
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }
}
