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
public class VaryingTimesFragment extends Fragment {
    private static final java.lang.String NUMBER_DIALOG = "number dialog";


    public static VaryingTimesFragment newInstance() {

        Bundle args = new Bundle();

        VaryingTimesFragment fragment = new VaryingTimesFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        View view = inflater.inflate(R.layout.fragment_varying_times, container, false);
        Button varyingTimesButton = (Button) view.findViewById(R.id.times_button);
        varyingTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
                Bundle bundle = new Bundle();
                bundle.putInt(getActivity().getResources().getString(R.string.select_workout_number),
                        getActivity().getResources().getInteger(R.integer.workout_number));
                numberPickerDialog.setArguments(bundle);
                numberPickerDialog.show(getActivity().getFragmentManager(), NUMBER_DIALOG);
            }
        });

        return view;
    }
}
