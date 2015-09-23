package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;

/**
 * Created by krishnavelagapudi on 9/9/15.
 */
public class TimerFragment extends Fragment {
    TextView mTimeTextView;
    public int mCurrentSet;
    public ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private TextView mCurrentExerciseNameTextView;
    public int mPauseResumeFlag = 1;
    public int mCurrentExerciseTime;
    public int mTotalSets;
    private TextView mCurrentSetTextView;
    private ImageButton mPauseResumeButton;
    public String mCurrentExerciseName;
    private int mHowToLay;
    private OnInfoBarClickListener onInfoBarClickListener;
    private StyleToolbar mStyleToolbar;
    private boolean mBlink;


    public static TimerFragment newInstance(Bundle bundle) {

        Bundle args = bundle;
        TimerFragment fragment = new TimerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void stopService() {
        if (mBound) {
            mService.stopMessages(true);
            getActivity().unbindService(mConnection);
            mService.stopSelf();
            mBound = false;
        }
    }

    public interface OnInfoBarClickListener {
        void onInfoBarClick(ArrayList<WorkoutModel> workoutModelArrayList, int currentSet,
                            int totalSets, int pauseResumeFlag, int currentTime, String currentExerciseName, String workoutName);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        onInfoBarClickListener = (OnInfoBarClickListener) getActivity();
        mStyleToolbar = (StyleToolbar) getActivity();
        mStyleToolbar.setToolbarStyle(android.R.color.holo_red_light, android.R.color.holo_red_dark, android.R.color.white);
        mHowToLay = getArguments().getInt(getString(R.string.how_to_lay));
        View view;
        if (mHowToLay == getResources().getInteger(R.integer.info_bar)) {
            view = inflater.inflate(R.layout.fragment_timer_info_bar, container, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInfoBarClickListener.onInfoBarClick(mWorkoutModelArrayList, mCurrentSet,
                            mTotalSets, mPauseResumeFlag, mCurrentExerciseTime, mCurrentExerciseName, mWorkoutName);
                }
            });
        } else {
            view = inflater.inflate(R.layout.fragment_timer, container, false);
        }

        initializeViews(view);
        Bundle bundle;
        if (savedInstanceState == null) {
            bundle = getArguments();
            int from = checkSourceFromBundle();
            if (from == getResources().getInteger(R.integer.info_bar) ||
                    from == getResources().getInteger(R.integer.notification)) {

            }
        } else {
            bundle = savedInstanceState;

        }
        initializeFields(bundle);
        fillViews();
        Intent intent = new Intent(getActivity(), TimerService.class);
        if (!Utils.isMyServiceRunning(TimerService.class, getActivity())) {
            getActivity().startService(intent);
        }
        if (mHowToLay == getResources().getInteger(R.integer.info_bar)) {
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar()
                    .setTitle(getString(R.string.app_name));
        } else {
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar()
                    .setTitle(mWorkoutName);
        }
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        return view;
    }

    private void initializeViews(View view) {
        mTimeTextView = (TextView) view.findViewById(R.id.time_text_view);
        mCurrentExerciseNameTextView = (TextView) view.findViewById(R.id.title_text_view);
        mCurrentSetTextView = (TextView) view.findViewById(R.id.sets_textView);
        mPauseResumeButton = (ImageButton) view.findViewById(R.id.pause_resume_button);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void fillViews() {
        mCurrentExerciseNameTextView.setText(mCurrentExerciseName);
        mCurrentSetTextView.setText("Set " + mCurrentSet);
        if (mCurrentExerciseTime <= 5) {
            mTimeTextView.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
        } else {
            if (mHowToLay == getResources().getInteger(R.integer.info_bar)) {
                mTimeTextView.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
            } else {
                mTimeTextView.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            }
        }
        mTimeTextView.setText(String.format("%02d", (mCurrentExerciseTime / 60)) + ":"
                + String.format("%02d", (mCurrentExerciseTime % 60)));
        updatePauseResumeButton();


    }

    private void initializeFields(Bundle bundle) {
        mPauseResumeFlag = bundle.getInt(getString(R.string.timer_state));
        mWorkoutName = bundle.getString(getString(R.string.workout_name));
        mCurrentSet = bundle.getInt(getString(R.string.current_set));
        mWorkoutModelArrayList = bundle.getParcelableArrayList(getString(R.string.workout_model));
        mCurrentExerciseTime = bundle.getInt(getString(R.string.time));
        mTotalSets = bundle.getInt(getString(R.string.set_number));
        mCurrentExerciseName = bundle.getString(getString(R.string.exercise_name));
        mPauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (mPauseResumeFlag == getResources().getInteger(R.integer.stop)) {
                    mService.mPauseResumeFlag = getResources().getInteger(R.integer.resume);
                    mService.initTimer();
                    mPauseResumeFlag = getResources().getInteger(R.integer.resume);
                } else if (mPauseResumeFlag == getResources().getInteger(R.integer.resume)) {
                    mPauseResumeFlag = getResources().getInteger(R.integer.pause);
                    if (mService != null) {
                        mService.updateNotificationActionButton(R.drawable.ic_play_circle_filled_black_18dp, getString(R.string.resume));
                    }
                } else {
                    mPauseResumeFlag = getResources().getInteger(R.integer.resume);
                    if (mService != null) {
                        mService.updateNotificationActionButton(R.drawable.ic_pause_circle_filled_black_18dp, getString(R.string.pause));
                    }
                }
                updatePauseResumeButton();
                mService.mPauseResumeFlag = mPauseResumeFlag;


            }
        });
    }

    private int checkSourceFromBundle() {
        Bundle bundle = getArguments();
        int from = -1;
        if (bundle != null) {
            if (bundle.getBoolean(getString(R.string.from_notification))) {
                from = getResources().getInteger(R.integer.notification);
            } else if (bundle.getBoolean(getString(R.string.from_info_bar))) {
                from = getResources().getInteger(R.integer.info_bar);
            }
        }
        return from;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void updatePauseResumeButton() {
        if (mPauseResumeFlag == getResources().getInteger(R.integer.resume)) {
            if (mHowToLay == getResources().getInteger(R.integer.info_bar)) {
                mPauseResumeButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause_circle_filled_black_18dp));

            } else {
                mPauseResumeButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause_circle_filled_black_48dp));
            }
        } else {
            if (mHowToLay == getResources().getInteger(R.integer.info_bar)) {
                mPauseResumeButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_play_circle_filled_black_18dp));
            } else {
                mPauseResumeButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_play_circle_filled_black_48dp));
            }

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.current_set), mCurrentSet);
        outState.putInt(getString(R.string.time), mCurrentExerciseTime);
        outState.putInt(getString(R.string.timer_state), mPauseResumeFlag);
        outState.putString(getString(R.string.exercise_name), mCurrentExerciseName);
        outState.putInt(getString(R.string.set_number), mTotalSets);
        outState.putString(getString(R.string.workout_name), mWorkoutName);
        outState.putParcelableArrayList(getString(R.string.workout_model), mWorkoutModelArrayList);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBound) {
            mService.stopMessages(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBound) {
            mService.stopMessages(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    private boolean mBound;
    private TimerService mService;
    public String mWorkoutName;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            mService = binder.getService();
            mService.stopMessages(false);
            mBound = true;
            mService.setMessenger(mMessenger);
            if (!mService.isTimerRunning()) {
                mService.setWorkoutArrayList(mWorkoutModelArrayList, mWorkoutName, mTotalSets);
                mService.mPauseResumeFlag = mPauseResumeFlag;
                mService.initTimer();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (bundle.getBoolean(getString(R.string.timer_running))) {
                if (mBlink) {
                    mStyleToolbar.setToolbarStyle(android.R.color.holo_blue_light, android.R.color.holo_blue_dark, android.R.color.white);
                    mBlink = false;
                } else {
                    mStyleToolbar.setToolbarStyle(android.R.color.holo_red_light, android.R.color.holo_red_dark, android.R.color.white);
                    mBlink=true;
                }
                mCurrentExerciseTime = msg.arg1;
                mCurrentSet = msg.arg2;
                if (bundle.getString(getString(R.string.exercise_name)) != null && !bundle.getString(getString(R.string.exercise_name)).isEmpty()) {
                    mCurrentExerciseName = bundle.getString(getString(R.string.exercise_name));
                }
            } else if (bundle.getBoolean(getString(R.string.from_notification))) {
                mPauseResumeFlag = bundle.getInt(getString(R.string.timer_state));
                updatePauseResumeButton();

            } else {
                mStyleToolbar.setToolbarStyle(android.R.color.holo_red_light, android.R.color.holo_red_dark, android.R.color.white);
                mPauseResumeFlag = getResources().getInteger(R.integer.stop);
            }
            fillViews();
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());


}
