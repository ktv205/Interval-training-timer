package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        return view;
    }

    TimerTask timer = new TimerTask() {

        @Override
        public void run() {
            mRepeatTimes--;
            for (WorkoutModel workoutModel : mWorkoutModelArrayList) {
                mTitleTextView.setText(workoutModel.getWorkoutName());
                int totalNumber = 0;
                if (workoutModel.getMin() > 0) {
                    totalNumber = workoutModel.getMin() * 60;
                }
                totalNumber = totalNumber + workoutModel.getSec();
                for (int i = totalNumber; i >= 0; i--) {
                    final String time = String.format("%02d", (i / 60)) + ":" + String.format("%02d", (i % 60));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTimeTextView.setText(time);
                        }
                    });
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


}
