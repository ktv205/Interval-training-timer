package com.example.krishnavelagapudi.intervaltrainingtimer;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by krishnavelagapudi on 9/8/15.
 */
public class VaryingTimesFragment extends Fragment {
    private static final java.lang.String NUMBER_DIALOG = "number dialog";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_varying_times, container, false);
        Button varyingTimesButton = (Button) view.findViewById(R.id.times_button);
        varyingTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
                numberPickerDialog.show(getActivity().getFragmentManager(), NUMBER_DIALOG);
            }
        });

        return view;
    }
}
