package com.example.krishnavelagapudi.intervaltrainingtimer;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by krishnavelagapudi on 9/8/15.
 */
public class NewWorkoutFragment extends Fragment {
    private static final java.lang.String NUMBER_DIALOG = "number dialog";
    private ExerciseNumber mExerciseNumber;
    private StyleToolbar mStyleToolbar;


    public interface ExerciseNumber{
        void showExerciseNumberPickerDialog();
    }


    public static NewWorkoutFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        NewWorkoutFragment fragment = new NewWorkoutFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mExerciseNumber=(ExerciseNumber)getActivity();
        mStyleToolbar=(StyleToolbar)getActivity();
        mStyleToolbar.setToolbarStyle(android.R.color.holo_red_light,android.R.color.holo_red_dark,android.R.color.white,getResources().getString(R.string.app_name));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        View view = inflater.inflate(R.layout.fragment_varying_times, container, false);
        Button varyingTimesButton = (Button) view.findViewById(R.id.times_button);
        varyingTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExerciseNumber.showExerciseNumberPickerDialog();
            }
        });
        return view;
    }
}
