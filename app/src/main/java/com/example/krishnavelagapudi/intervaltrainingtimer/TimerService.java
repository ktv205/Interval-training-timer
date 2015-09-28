package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by krishnavelagapudi on 9/14/15.
 */
public class TimerService extends Service {

    private static final String TAG = TimerService.class.getSimpleName();
    private final IBinder mBinder = new TimerBinder();
    private ArrayList<WorkoutModel> mWorkoutModelArray;
    private int mTotalSets;
    private int mTimerStateFlag;
    private int mCurrentSet;
    private Messenger mMessenger;
    private Timer mTimer;
    private boolean mGiveHeadsUpTime = true;
    private boolean mIsUpdatePauseAction = false;
    private boolean mReceiverRegistered=false;
    private int mCurrentExerciseTime;
    private String mExerciseName;

    private NotificationManager mNotificationManager;
    private android.support.v4.app.NotificationCompat.Builder mBuilder;
    private String mWorkoutName;

    public void setmTimerStateFlag(int mTimerStateFlag) {
        this.mTimerStateFlag = mTimerStateFlag;
        if(mTimerStateFlag==getResources().getInteger(R.integer.resume)){
            updateNotificationActionButton(R.drawable.pause44,getString(R.string.pause));
        }else{
            updateNotificationActionButton(R.drawable.media23,getString(R.string.resume));
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            initializeFields(bundle);
            registerReceiver();
            startNotificationBuilder();
            return START_STICKY;
        } else {
            startNotificationBuilder();
            stopSelf();
            return START_NOT_STICKY;
        }
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DEFAULT);
        registerReceiver(notificationActionReceiver, intentFilter);
        mReceiverRegistered=true;
    }

    private void initializeFields(Bundle bundle) {
        mWorkoutModelArray = bundle.getParcelableArrayList(getString(R.string.workout_model));
        mTotalSets = bundle.getInt(getString(R.string.set_number));
        mTimerStateFlag = getResources().getInteger(R.integer.resume);
        mWorkoutName = bundle.getString(getString(R.string.workout_name));
        mCurrentSet = 1;

    }

    public void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            IntervalTimerTask intervalTimerTask = new IntervalTimerTask();
            mTimer.schedule(intervalTimerTask, 0, 1000);
        }
    }

    public void setNotificationBuilderToNull() {
        mBuilder=null;
    }


    public class TimerBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMessenger = null;
        return super.onUnbind(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceiverRegistered) {
            unregisterReceiver(notificationActionReceiver);
            mReceiverRegistered = false;
        }
        removeNotification();
    }

    public void setMessenger(Messenger messenger) {
        mMessenger = messenger;
    }

    public class IntervalTimerTask extends TimerTask {

        @Override
        public void run() {
            mCurrentExerciseTime = 0;
            if (mGiveHeadsUpTime) {
                mCurrentExerciseTime = 5;
                while (mCurrentExerciseTime >= 0) {
                    if (mTimerStateFlag == getResources().getInteger(R.integer.resume)) {
                        updateTimerFragment("Get Ready", mCurrentExerciseTime);
                        mIsUpdatePauseAction = true;
                        mCurrentExerciseTime--;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (mIsUpdatePauseAction) {
                        mCurrentExerciseTime++;
                        updateTimerFragment("Get Ready", mCurrentExerciseTime);
                        mIsUpdatePauseAction = false;
                    }

                }
                mGiveHeadsUpTime = false;
            }
            int exerciseNumber = 0;
            mExerciseName = null;
            while (exerciseNumber < mWorkoutModelArray.size()) {
                final WorkoutModel workoutModel = mWorkoutModelArray.get(exerciseNumber);
                mExerciseName = workoutModel.getExerciseName();
                mCurrentExerciseTime = workoutModel.getMin() * 60 + workoutModel.getSec();
                while (mCurrentExerciseTime >= 0) {
                    if (mTimerStateFlag == getResources().getInteger(R.integer.resume)) {
                        updateTimerFragment(mExerciseName, mCurrentExerciseTime);
                        updateNotification(mCurrentExerciseTime, mExerciseName);
                        mCurrentExerciseTime--;
                        mIsUpdatePauseAction = true;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (mIsUpdatePauseAction) {
                        mCurrentExerciseTime++;
                        updateTimerFragment(mExerciseName, mCurrentExerciseTime);
                        updateNotification(mCurrentExerciseTime, mExerciseName);
                        mIsUpdatePauseAction = false;

                    }

                }
                exerciseNumber++;
            }

            if (mCurrentSet == mTotalSets) {
                mCurrentExerciseTime = 0;
                stopTimerAndResetFields();
                updateTimerFragment(mExerciseName, mCurrentExerciseTime);
            }
            mCurrentSet++;


        }
    }

    private void updateTimerFragment(String exerciseName, int currentExerciseTime) {
        if (mMessenger != null) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putBoolean(getString(R.string.timer_running), true);
            message.arg1 = currentExerciseTime;
            message.arg2 = mCurrentSet;
            bundle.putString(getString(R.string.exercise_name), exerciseName);
            bundle.putInt(getString(R.string.timer_state), mTimerStateFlag);
            message.setData(bundle);
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopTimerAndResetFields() {
        mTimer.cancel();
        mTimerStateFlag = getResources().getInteger(R.integer.stop);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startNotificationBuilder() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DEFAULT);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.timer)
                        .addAction(R.drawable.ic_pause_circle_filled_black_18dp, "pause", sentPI);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateNotification(int time, String exerciseName) {
        if(mBuilder!=null) {
            mBuilder.setContentTitle(mWorkoutName + " " + "set " + mCurrentSet + " " + exerciseName)
                    .setContentText(String.format("%02d", (time / 60)) + ":" + String.format("%02d", (time % 60)));
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(getString(R.string.from_notification), true);
            intent.putExtra(getString(R.string.workout_model), mWorkoutModelArray);
            intent.putExtra(getString(R.string.set_number), mTotalSets);
            intent.putExtra(getString(R.string.workout_name), mWorkoutName);
            intent.putExtra(getString(R.string.exercise_name), exerciseName);
            intent.putExtra(getString(R.string.timer_state), mTimerStateFlag);
            intent.putExtra(getString(R.string.current_set), mCurrentSet);
            intent.putExtra(getString(R.string.time), time);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            mNotificationManager.notify(0, notification);
        }
    }


    public void removeNotification() {
        Log.d(TAG,"removed");
        mNotificationManager.cancel(0);
    }



    public void updateNotificationActionButton(int drawableId, String state) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Intent.ACTION_DEFAULT);
        PendingIntent sentPI = PendingIntent.getBroadcast(TimerService.this, 0, broadCastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder =
                new NotificationCompat.Builder(TimerService.this)
                        .setSmallIcon(R.drawable.timer)
                        .addAction(drawableId, state, sentPI);
    }

    private final BroadcastReceiver notificationActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mTimerStateFlag == getResources().getInteger(R.integer.resume)) {
                mTimerStateFlag = getResources().getInteger(R.integer.pause);
                updateNotificationActionButton(R.drawable.media23,getString(R.string.resume));
            } else {
                mTimerStateFlag = getResources().getInteger(R.integer.resume);
                updateNotificationActionButton(R.drawable.pause44,getString(R.string.pause));
            }

        }
    };

    public Bundle getBundle(){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.workout_model), mWorkoutModelArray);
        bundle.putInt(getString(R.string.set_number), mTotalSets);
        bundle.putInt(getString(R.string.current_set), mCurrentSet);
        bundle.putString(getString(R.string.workout_name), mWorkoutName);
        bundle.putString(getString(R.string.exercise_name), mExerciseName);
        bundle.putInt(getString(R.string.timer_state), mTimerStateFlag);
        bundle.putInt(getString(R.string.time), mCurrentExerciseTime);
        return bundle;
    }


}
