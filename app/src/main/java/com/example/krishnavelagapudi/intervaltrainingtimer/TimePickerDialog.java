package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;


/**
 * Created by krishnavelagapudi on 9/8/15.
 */
public class TimePickerDialog extends DialogFragment {
    private static final int WORKOUT_FIELD_EMPTY = 1;
    private static final int TIME_FIELD_EMPTY = 2;
    private OnTimePickedListener mOnTimePickedListener;

    public interface OnTimePickedListener {
        void onTimePicked(String workoutName, int minutes, int seconds);

        void onTimePicked(String workoutName, int minutes, int seconds, int position);
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
        final View view = inflater.inflate(R.layout.dialog_time_picker, container, false);
        final int key = getArguments().getInt(getString(R.string.time_picker_key));

        final NumberPicker minutePicker = (NumberPicker) view.findViewById(R.id.minute_picker);
        final NumberPicker secondPicker = (NumberPicker) view.findViewById(R.id.second_picker);
        final EditText editText = (EditText) view.findViewById(R.id.label_edit_text);
        setMinMaxForPicker(minutePicker, 0, 60);
        setMinMaxForPicker(secondPicker, 0, 59);
        if (key == getResources().getInteger(R.integer.time_picker)) {
            getDialog().setTitle(getString(R.string.time_picker));
        } else {
            getDialog().setTitle(getString(R.string.change_title));
            WorkoutModel workoutModel = getArguments().getParcelable(getString(R.string.workout_key));
            minutePicker.setValue(workoutModel.getMin());
            secondPicker.setValue(workoutModel.getSec());
            editText.setText(workoutModel.getWorkoutName());
        }
        Button okButton = (Button) view.findViewById(R.id.ok_button);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = -1;
                if (key == getResources().getInteger(R.integer.time_changer)) {
                    position = getArguments().getInt(getString(R.string.list_position));
                }
                int value = checkFields(editText, minutePicker, secondPicker);
                String error = null;
                if (value == WORKOUT_FIELD_EMPTY) {
                    error = "Please enter a name for the exercise";
                } else if (value == TIME_FIELD_EMPTY) {
                    error = "Please enter the time for the exercise";
                }
                if (position == -1) {
                    if (value != 0) {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    } else {
                        mOnTimePickedListener.onTimePicked(editText.getText().toString(), minutePicker.getValue(), secondPicker.getValue());
                        getDialog().cancel();
                    }
                } else {
                    if (value != 0) {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    } else {
                        mOnTimePickedListener.onTimePicked(editText.getText().toString(), minutePicker.getValue(), secondPicker.getValue(), position);
                        getDialog().cancel();
                    }
                }


            }
        });
        return view;
    }

    private int checkFields(EditText editText, NumberPicker min, NumberPicker sec) {
        int value = 0;
        if (editText.getText().toString().isEmpty()) {
            value = WORKOUT_FIELD_EMPTY;
        } else if (min.getValue() == 0 && sec.getValue() == 0) {
            value = TIME_FIELD_EMPTY;
        }
        return value;
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
