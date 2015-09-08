package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

/**
 * Created by krishnavelagapudi on 9/8/15.
 */
public class ReviewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ArrayList<WorkoutModel> workoutModelArrayList = getArguments().getParcelableArrayList(getString(R.string.workout_key));
        recyclerView.setAdapter(new RecyclerViewAdapter(workoutModelArrayList));
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
            String time = mWorkoutModelList.get(i).getMin() + ":" + mWorkoutModelList.get(i).getSec();
            viewHolder.time.setText(time);
            viewHolder.workout.setText(mWorkoutModelList.get(i).getWorkoutName());
        }

        @Override
        public int getItemCount() {
            return mWorkoutModelList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView workout, time;

            public ViewHolder(View itemView) {
                super(itemView);
                workout = (TextView) itemView.findViewById(R.id.workout_text_view);
                time = (TextView) itemView.findViewById(R.id.time_text_view);
            }
        }
    }
}
