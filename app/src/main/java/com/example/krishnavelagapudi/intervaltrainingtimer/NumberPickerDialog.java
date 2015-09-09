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
public class NumberPickerDialog extends DialogFragment {


    private OnNumberPickedListener mOnNumberPickedListener;

    public interface OnNumberPickedListener {
        void onNumberPicked(int number, int key);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mOnNumberPickedListener = (OnNumberPickedListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnNumberPickedListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_number_picker, container, false);
        getDialog().setTitle(getString(R.string.number_picker));
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(1);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        Button button = (Button) view.findViewById(R.id.ok_button);
        final int key = getArguments().getInt(getString(R.string.select_workout_number));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnNumberPickedListener.onNumberPicked(numberPicker.getValue(), key);
                getDialog().cancel();

            }
        });
        return view;
    }


}
