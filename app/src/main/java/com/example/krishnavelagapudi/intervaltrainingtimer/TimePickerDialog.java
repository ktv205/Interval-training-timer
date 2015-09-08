package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;


/**
 * Created by krishnavelagapudi on 9/8/15.
 */
public class TimePickerDialog extends DialogFragment {
    private OnTimePickedListener mOnTimePickedListener;

    public interface OnTimePickedListener {
        void onTimePicked(String workoutName, int minutes, int seconds);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mOnTimePickedListener = (OnTimePickedListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnNumberPickedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_time_picker, container, false);
        getDialog().setTitle(getString(R.string.time_picker));
        NumberPicker minutePicker = (NumberPicker) view.findViewById(R.id.minute_picker);
        NumberPicker secondPicker = (NumberPicker) view.findViewById(R.id.second_picker);
        setMinMaxForPicker(minutePicker, 0, 60);
        setMinMaxForPicker(secondPicker, 0, 59);
        Button okButton = (Button) view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnTimePickedListener.onTimePicked("workout name", 0, 0);
                getDialog().cancel();
            }
        });
        return view;
    }

    private void setMinMaxForPicker(NumberPicker picker, int min, int max) {
        picker.setMinValue(min);
        picker.setMaxValue(max);
        picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
    }
}
