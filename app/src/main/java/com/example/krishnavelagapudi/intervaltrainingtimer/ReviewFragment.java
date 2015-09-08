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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ArrayList<WorkoutModel> workoutModelArrayList = getArguments().getParcelableArrayList(getString(R.string.workout_key));
        recyclerView.setAdapter(new RecyclerViewAdapter(workoutModelArrayList));
        Button repeatButton = (Button) view.findViewById(R.id.repeat_button);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
                numberPickerDialog.show(getFragmentManager(), NUMBER_PICKER_DIALOG_TAG);
            }
        });
        return view;
    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        ArrayList<WorkoutModel> mWorkoutModelList = new ArrayList<>();

        public RecyclerViewAdapter(ArrayList<WorkoutModel> parcelableArrayList) {
            mWorkoutModelList = parcelableArrayList;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_view, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            String time = String.format("%2d", mWorkoutModelList.get(i).getMin()) + ":"
                    + String.format("%2d", mWorkoutModelList.get(i).getSec()) + " minutes";
            viewHolder.timeTextView.setText(time);
            viewHolder.workoutTextView.setText(mWorkoutModelList.get(i).getWorkoutName());
            viewHolder.changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog();
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
