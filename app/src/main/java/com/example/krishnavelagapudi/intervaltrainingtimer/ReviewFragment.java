package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

/**
 * Created by krishnavelagapudi on 9/8/15.
 */
public class ReviewFragment extends Fragment {

    private static final String TIME_PICKER_DIALOG_TAG = "time picker";
    private static final String NUMBER_PICKER_DIALOG_TAG = "number picker";
    private static final String TAG = ReviewFragment.class.getSimpleName();
    ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private View mView;
    Button mRepeatButton;
    RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private int mNumber=1;

    private OnStartTimerListener mOnStartTimerListener;

    public interface OnStartTimerListener {
        void OnStartTimer(ArrayList<WorkoutModel> workoutModelArrayList, int number);
    }

   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mOnStartTimerListener = (OnStartTimerListener)getActivity();
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnNumberPickedListener");
        }
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mOnStartTimerListener = (OnStartTimerListener)getActivity();
        mView = inflater.inflate(R.layout.fragment_review, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorkoutModelArrayList = getArguments().getParcelableArrayList(getString(R.string.workout_key));
        mRecyclerViewAdapter = new RecyclerViewAdapter(mWorkoutModelArrayList);
        mRecyclerView.setAdapter(new RecyclerViewAdapter(mWorkoutModelArrayList));
        mRepeatButton = (Button) mView.findViewById(R.id.repeat_button);
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
        Button startButton = (Button) mView.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnStartTimerListener.OnStartTimer(mWorkoutModelArrayList, mNumber);

            }
        });

        return mView;
    }

    public void changeTime(int minutes, int seconds, int position) {
        View view = mRecyclerView.getChildAt(position);
        TextView textView = (TextView) view.findViewById(R.id.time_text_view);
        String time = String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds) + " minutes";
        textView.setText(time);
        WorkoutModel model = mWorkoutModelArrayList.get(position);
        mWorkoutModelArrayList.remove(position);
        mWorkoutModelArrayList.add(position, new WorkoutModel(model.getWorkoutName(), minutes, seconds));
    }

    public void updateRepeatTimes(int number) {
        mNumber = number;
        mRepeatButton.setText("Repeat " + number + " times");
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
            viewHolder.workoutTextView.setText(mWorkoutModelList.get(i).getWorkoutName());
            viewHolder.changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog();
                    Bundle bundle = new Bundle();
                    bundle.putInt(getString(R.string.time_picker_key), getResources().getInteger(R.integer.time_changer));
                    bundle.putInt(getString(R.string.list_position), position);
                    timePickerDialog.setArguments(bundle);
                    timePickerDialog.show(getFragmentManager(), TIME_PICKER_DIALOG_TAG);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mWorkoutModelList.size();
        }

        /*public void dataChanged() {
            mWorkoutModelList.clear();
            mWorkoutModelList.addAll(mWorkoutModelArrayList);
            Log.d(TAG,"size->"+mWorkoutModelList.size());
            notifyDataSetChanged();

        }*/


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
