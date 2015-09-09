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

/**
 * Created by krishnavelagapudi on 9/9/15.
 */
public class TimerFragment extends Fragment {
    private static final String TAG = TimerFragment.class.getSimpleName();
    TextView mTimeTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        mTimeTextView = (TextView) view.findViewById(R.id.time_text_view);
        ArrayList<WorkoutModel> workoutModelArrayList = getArguments().getParcelableArrayList(getString(R.string.workout_key));
        int number = getArguments().getInt(getString(R.string.select_workout_number));
        for (int i = 0; i < number; i++) {
            for (WorkoutModel workoutModel : workoutModelArrayList) {
                for (int j = 0; j < workoutModel.getMin(); j++) {
                    //TODO;
                }
            }
        }
        return view;
    }


}
