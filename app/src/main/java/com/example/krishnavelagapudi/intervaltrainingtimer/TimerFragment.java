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
    private static final String TAG = TimerFragment.class.getSimpleName();
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
    private boolean mBound;
    private TimerService mService;
    public String mWorkoutName;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    public boolean mFromRecentApps;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setMessenger(mMessenger);
            if(mFromRecentApps){
                Bundle bundle=mService.getBundle();
                initializeFields(bundle);
                fillViews();
            }
            mService.startTimer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public void stopService() {
        if(mBound){
            mService.setMessenger(null);
            mService.setNotificationBuilderToNull();
            getActivity().unbindService(mConnection);
            mService.stopSelf();
            mBound=false;

        }
    }


    public interface OnInfoBarClickListener {
        void onInfoBarClick(ArrayList<WorkoutModel> workoutModelArrayList, int currentSet,
                            int totalSets, int pauseResumeFlag, int currentTime, String currentExerciseName, String workoutName);
    }


    public static TimerFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        TimerFragment fragment = new TimerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        onInfoBarClickListener = (OnInfoBarClickListener) getActivity();
        mStyleToolbar = (StyleToolbar) getActivity();
        mHowToLay = getArguments().getInt(getString(R.string.how_to_lay));
        mFromRecentApps=getArguments().getBoolean(getString(R.string.from_recent_apps));
        View view;
        if (mHowToLay == getResources().getInteger(R.integer.info_bar)) {
            view = inflater.inflate(R.layout.fragment_timer_info_bar, container, false);
            if(mFromRecentApps){
                bindToService();
            }
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
        Bundle bundle = null;
        if (savedInstanceState == null) {
            if(!mFromRecentApps) {
                bundle = getArguments();
            }
        } else {
            bundle = savedInstanceState;

        }
        if(bundle!=null) {
            initializeFields(bundle);
            fillViews();
        }
        styleToolbar(android.R.color.holo_blue_light, android.R.color.holo_blue_dark);
        if(!mFromRecentApps) {
            if (bundle.getBoolean(getString(R.string.from_review_fragment))) {
                startAndBindToService(bundle);
            } else if (Utils.isMyServiceRunning(TimerService.class, getActivity())) {
                bindToService();
            }
        }
        return view;
    }

    private void bindToService() {
        Intent intent = new Intent(getActivity(), TimerService.class);
        getActivity().bindService(intent, mConnection, 0);
    }

    private void startAndBindToService(Bundle bundle) {
        Intent intent = new Intent(getActivity(), TimerService.class);
        intent.putExtras(bundle);
        if (!Utils.isMyServiceRunning(TimerService.class, getActivity())) {
            getActivity().startService(intent);
        }
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void styleToolbar(int light, int dark) {
        if (mHowToLay == getResources().getInteger(R.integer.info_bar)) {
            mStyleToolbar.setToolbarStyle(light, dark, android.R.color.white, getString(R.string.app_name));
        } else {
            mStyleToolbar.setToolbarStyle(light, dark, android.R.color.white, mWorkoutName);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            mService.setMessenger(null);
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    private void initializeViews(View view) {
        mTimeTextView = (TextView) view.findViewById(R.id.time_text_view);
        mCurrentExerciseNameTextView = (TextView) view.findViewById(R.id.title_text_view);
        mCurrentSetTextView = (TextView) view.findViewById(R.id.sets_textView);
        mPauseResumeButton = (ImageButton) view.findViewById(R.id.pause_resume_button);
        mPauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (mPauseResumeFlag == getResources().getInteger(R.integer.stop)) {
                    mPauseResumeFlag = getResources().getInteger(R.integer.resume);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(getString(R.string.workout_model), mWorkoutModelArrayList);
                    bundle.putInt(getString(R.string.set_number), mTotalSets);
                    bundle.putString(getString(R.string.workout_name), mWorkoutName);
                    startAndBindToService(bundle);
                } else if (mPauseResumeFlag == getResources().getInteger(R.integer.resume)) {
                    mPauseResumeFlag = getResources().getInteger(R.integer.pause);
                } else {
                    mPauseResumeFlag = getResources().getInteger(R.integer.resume);

                }
                if (mBound) {
                    mService.setmTimerStateFlag(mPauseResumeFlag);
                }
                updatePauseResumeButton();


            }
        });
    }


    private void initializeFields(Bundle bundle) {
        mPauseResumeFlag = bundle.getInt(getString(R.string.timer_state));
        mWorkoutName = bundle.getString(getString(R.string.workout_name));
        mCurrentSet = bundle.getInt(getString(R.string.current_set));
        mWorkoutModelArrayList = bundle.getParcelableArrayList(getString(R.string.workout_model));
        mCurrentExerciseTime = bundle.getInt(getString(R.string.time));
        mTotalSets = bundle.getInt(getString(R.string.set_number));
        mCurrentExerciseName = bundle.getString(getString(R.string.exercise_name));

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
        outState.putInt(getString(R.string.how_to_lay), mHowToLay);
        super.onSaveInstanceState(outState);

    }


    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            mCurrentExerciseTime = msg.arg1;
            mCurrentSet = msg.arg2;
            mCurrentExerciseName = bundle.getString(getString(R.string.exercise_name));
            mPauseResumeFlag = bundle.getInt(getString(R.string.timer_state));
            updatePauseResumeButton();
            fillViews();
            if (mPauseResumeFlag == getResources().getInteger(R.integer.stop)) {
                stopService();
            }

        }
    }


}
