package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

/**
 * Created by krishnavelagapudi on 9/8/15.
 */
public class ReviewFragment extends Fragment {

    private static final String TIME_PICKER_DIALOG_TAG = "time picker";
    private static final String NUMBER_PICKER_DIALOG_TAG = "number picker";
    private static final String TAG = ReviewFragment.class.getSimpleName();
    private static final int SETS_EMPTY = 1;
    private static final int TITLE_EMPTY = 2;
    ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private View mView;
    Button mRepeatButton;
    RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private int mNumber = 0;

    private OnStartTimerListener mOnStartTimerListener;

    public interface OnStartTimerListener {
        void OnStartTimer(ArrayList<WorkoutModel> workoutModelArrayList, int number,String workoutName);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.review));
        this.mOnStartTimerListener = (OnStartTimerListener) getActivity();
        mView = inflater.inflate(R.layout.fragment_review, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorkoutModelArrayList = getArguments().getParcelableArrayList(getString(R.string.workout_key));
        mRecyclerViewAdapter = new RecyclerViewAdapter(mWorkoutModelArrayList);
        mRecyclerView.setAdapter(new RecyclerViewAdapter(mWorkoutModelArrayList));
        mRepeatButton = (Button) mView.findViewById(R.id.repeat_button);
        final Button startButton = (Button) mView.findViewById(R.id.start_button);
        final EditText titleEditText = (EditText) mView.findViewById(R.id.title_edit_text);
        titleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
                startButton.performClick();
                return true;
            }
        });
        mRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
                Bundle bundle = new Bundle();
                bundle.putInt(getResources().getString(R.string.select_workout_number),
                        getResources().getInteger(R.integer.repeat_number));
                numberPickerDialog.setArguments(bundle);
                numberPickerDialog.show(getFragmentManager(), NUMBER_PICKER_DIALOG_TAG);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = checkFields(titleEditText);
                if (value == 0) {
                    mOnStartTimerListener.OnStartTimer(mWorkoutModelArrayList, mNumber,titleEditText.getText().toString());
                } else {
                    String error = null;
                    if (value == TITLE_EMPTY) {
                        error = "Please name the workout";
                    } else if (value == SETS_EMPTY) {
                        error = "Please select sets by clicking the repeat times button";
                    }
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                }

            }
        });

        return mView;
    }

    private int checkFields(EditText title) {
        int value = 0;
        if (mNumber == 0) {
            value = SETS_EMPTY;
        } else if (title.getText().toString().isEmpty()) {
            value = TITLE_EMPTY;
        }
        return value;
    }

    public void changeTime(WorkoutModel workoutModel, int position) {
        View view = mRecyclerView.getChildAt(position);
        TextView TimeTextView = (TextView) view.findViewById(R.id.time_text_view);
        String time = String.format("%02d", workoutModel.getMin()) + ":"
                + String.format("%02d", workoutModel.getSec()) + " minutes";
        TimeTextView.setText(time);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        titleTextView.setText(workoutModel.getExerciseName());
        mWorkoutModelArrayList.remove(position);
        mWorkoutModelArrayList.add(position, workoutModel);
    }

    public void updateRepeatTimes(int number) {
        mNumber = number;
        if (number == 1) {
            mRepeatButton.setText("Don't repeat");
        } else {
            mRepeatButton.setText("Repeat " + number + " times");
        }
    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        ArrayList<WorkoutModel> mWorkoutModelList = new ArrayList<>();

        public RecyclerViewAdapter(ArrayList<WorkoutModel> mWorkoutModelList) {
            this.mWorkoutModelList = mWorkoutModelList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_view, viewGroup, false));
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            final int position = i;
            String time = String.format("%02d", mWorkoutModelList.get(i).getMin()) + ":"
                    + String.format("%02d", mWorkoutModelList.get(i).getSec()) + " minutes";
            viewHolder.timeTextView.setText(time);
            viewHolder.workoutTextView.setText(mWorkoutModelList.get(i).getExerciseName());
            viewHolder.changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog();
                    Bundle bundle = new Bundle();
                    bundle.putInt(getString(R.string.time_picker_key), getResources().getInteger(R.integer.time_changer));
                    bundle.putInt(getString(R.string.list_position), position);
                    bundle.putParcelable(getString(R.string.workout_key), mWorkoutModelList.get(position));
                    timePickerDialog.setArguments(bundle);
                    timePickerDialog.show(getFragmentManager(), TIME_PICKER_DIALOG_TAG);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mWorkoutModelList.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView workoutTextView, timeTextView;
            Button changeButton;

            public ViewHolder(View itemView) {
                super(itemView);
                workoutTextView = (TextView) itemView.findViewById(R.id.workout_text_view);
                timeTextView = (TextView) itemView.findViewById(R.id.time_text_view);
                changeButton = (Button) itemView.findViewById(R.id.change_button);
            }
        }

    }
}
