package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;


/**
 * Created by krishnavelagapudi on 9/8/15.
 */
public class TimePickerDialog extends DialogFragment {
    private static final int WORKOUT_FIELD_EMPTY = 1;
    private static final int TIME_FIELD_EMPTY = 2;
    private static final String TAG = TimePickerDialog.class.getSimpleName();

    private OnTimePickedListener mOnTimePickedListener;
    private int mMin = 0;
    private int mSec = 0;


    public interface OnTimePickedListener {
        void onTimePicked(String workoutName, int minutes, int seconds);

        void onTimePicked(String workoutName, int minutes, int seconds, int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    public static TimePickerDialog newInstance(Bundle bundle) {
        Bundle args = bundle;
        TimePickerDialog fragment = new TimePickerDialog();
        fragment.setArguments(args);
        return fragment;
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
        TextView titleTextView=(TextView)view.findViewById(R.id.title_text_view);
        final int key = getArguments().getInt(getString(R.string.pick_time_for));
        final NumberPicker minutePicker = (NumberPicker) view.findViewById(R.id.minute_picker);
        final NumberPicker secondPicker = (NumberPicker) view.findViewById(R.id.second_picker);
        final EditText editText = (EditText) view.findViewById(R.id.exercise_name_edit_text);
        final Button okButton = (Button) view.findViewById(R.id.ok_button);
        setCancelable(false);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    okButton.performClick();
                    return true;
                }
                return false;
            }
        });
        if (savedInstanceState != null) {
            mMin = savedInstanceState.getInt(getString(R.string.minutes));
            mSec = savedInstanceState.getInt(getString(R.string.seconds));
        }
        setMinMaxForPicker(minutePicker, 0, 60, mMin);
        setMinMaxForPicker(secondPicker, 0, 59, mSec);
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mMin = newVal;
            }
        });
        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mSec = newVal;
            }
        });

        if (key == getResources().getInteger(R.integer.pick_time)) {
            titleTextView.setText(getString(R.string.create_exercise_title) + " " + getArguments().getInt(getString(R.string.exercise_number)));
        } else {
            titleTextView.setText(getString(R.string.edit_exercise_title));
            WorkoutModel workoutModel = getArguments().getParcelable(getString(R.string.workout_model));
            minutePicker.setValue(workoutModel.getMin());
            secondPicker.setValue(workoutModel.getSec());
            editText.setText(workoutModel.getExerciseName());
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = -1;
                if (key == getResources().getInteger(R.integer.change_time)) {
                    position = getArguments().getInt(getString(R.string.list_position));
                }
                int value = checkFields(editText, minutePicker, secondPicker);
                String error = null;
                if (value == WORKOUT_FIELD_EMPTY) {
                    editText.setHint(getString(R.string.exercise_empty_error));
                    editText.setHintTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
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
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }


    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.minutes), mMin);
        outState.putInt(getString(R.string.seconds), mSec);
        super.onSaveInstanceState(outState);

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

    private void setMinMaxForPicker(NumberPicker picker, int min, int max, int value) {
        picker.setMinValue(min);
        picker.setMaxValue(max);
        picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        picker.setValue(value);
    }


}
