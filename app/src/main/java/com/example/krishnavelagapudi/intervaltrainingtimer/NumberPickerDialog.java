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
    private int mNumber=1;

    public static NumberPickerDialog newInstance(Bundle bundle) {
        Bundle args = bundle;
        NumberPickerDialog fragment = new NumberPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

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
        final int pickNumberFor=getArguments().getInt(getString(R.string.pick_number_for));
        if (pickNumberFor == getResources().getInteger(R.integer.exercise_number)) {
            getDialog().setTitle(getString(R.string.exercises_title));
        } else {
            getDialog().setTitle(getString(R.string.sets_title));
        }

        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(1);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        if(savedInstanceState!=null){
            mNumber=savedInstanceState.getInt(getString(R.string.exercise_number));
            numberPicker.setValue(mNumber);
        }
        Button button = (Button) view.findViewById(R.id.ok_button);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mNumber=numberPicker.getValue();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnNumberPickedListener.onNumberPicked(numberPicker.getValue(), pickNumberFor);
                getDialog().cancel();

            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.exercise_number),mNumber);
        super.onSaveInstanceState(outState);
    }
}
