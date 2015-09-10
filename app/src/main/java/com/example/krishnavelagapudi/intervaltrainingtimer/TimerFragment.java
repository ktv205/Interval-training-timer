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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        mTimeTextView = (TextView) view.findViewById(R.id.time_text_view);
        mTitleTextView = (TextView) view.findViewById(R.id.title_text_view);
        ArrayList<WorkoutModel> workoutModelArrayList = getArguments().getParcelableArrayList(getString(R.string.workout_key));
        mWorkoutModelArrayList = workoutModelArrayList;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(timer, 0, 1000);
        mRepeatTimes = getArguments().getInt(getString(R.string.select_workout_number));
        final Button pauseResumeButton = (Button) view.findViewById(R.id.pause_resume_button);
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

    TimerTask timer = new TimerTask() {

        @Override
        public void run() {
            mRepeatTimes--;
            for (final WorkoutModel workoutModel : mWorkoutModelArrayList) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTitleTextView.setText(workoutModel.getWorkoutName());
                        }
                    });
                }

                int totalNumber = 0;
                if (workoutModel.getMin() > 0) {
                    totalNumber = workoutModel.getMin() * 60;
                }
                totalNumber = totalNumber + workoutModel.getSec();
                int i = totalNumber;
                while (i >= 0) {
                    if (mPauseResumeFlag == RESUME) {
                        final String time = String.format("%02d", (i / 60)) + ":" + String.format("%02d", (i % 60));
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTimeTextView.setText(time);
                                }
                            });
                            i--;
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

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
