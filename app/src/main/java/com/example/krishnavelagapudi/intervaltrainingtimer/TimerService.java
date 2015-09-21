package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.krishnavelagapudi.intervaltrainingtimer.models.WorkoutModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by krishnavelagapudi on 9/14/15.
 */
public class TimerService extends Service {
    private final IBinder mBinder = new TimerBinder();
    private ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private String mWorkoutName;
    private int mCurrentExerciseTime = -1;
    private int mExerciseNumber = 1;
    private int mTotalSets;
    private int mCurrentSet = 0;
    int mPauseResumeFlag = 1;
    private Timer mTimer;
    Messenger mMessenger;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    private boolean stopFlag = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotificationBuilder();
        return START_NOT_STICKY;
    }

    public boolean isTimerRunning() {
        if (mTimer == null) {
            return false;
        }
        return true;
    }

    public void stopMessages(boolean b) {
        stopFlag = b;

    }

    public class TimerBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancel(0);
    }

    public void setMessenger(Messenger messenger) {
        mMessenger = messenger;
    }

    public void setWorkoutArrayList(ArrayList<WorkoutModel> workoutArrayList, String workoutName, int totalSets) {
        mWorkoutModelArrayList = workoutArrayList;
        mWorkoutName = workoutName;
        mTotalSets = totalSets;

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startNotificationBuilder() {
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.timer);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void initTimer() {
        if (mTimer == null && mPauseResumeFlag == getResources().getInteger(R.integer.resume)) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new IntervalTimerTask(), 0, 1000);
        }
    }

    public class IntervalTimerTask extends TimerTask {


        @Override
        public void run() {
            if (mCurrentExerciseTime == -1) {
                mCurrentSet++;
            }
            while (mExerciseNumber <= mWorkoutModelArrayList.size()) {
                final WorkoutModel workoutModel = mWorkoutModelArrayList.get(mExerciseNumber - 1);
                if (mCurrentExerciseTime == -1) {
                    if (workoutModel.getMin() > 0) {
                        mCurrentExerciseTime = workoutModel.getMin() * 60;
                    }
                    mCurrentExerciseTime = mCurrentExerciseTime + workoutModel.getSec();
                }
                while (mCurrentExerciseTime >= 0) {
                    final String time = String.format("%02d", (mCurrentExerciseTime / 60)) + ":" + String.format("%02d", (mCurrentExerciseTime % 60));
                    updateNotification(time, workoutModel.getExerciseName(), mCurrentSet);
                    updateTimerFragment(workoutModel.getExerciseName());
                    if (mPauseResumeFlag == getResources().getInteger(R.integer.resume)) {
                        mCurrentExerciseTime--;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mCurrentExerciseTime == 5) {
                        int resID = getResources().getIdentifier("countdown", "raw", getPackageName());
                        MediaPlayer mediaPlayer = MediaPlayer.create(TimerService.this, resID);
                        mediaPlayer.start();
                    }

                }
                mExerciseNumber++;

            }
            mExerciseNumber = 1;
            mCurrentExerciseTime = -1;
            if (mCurrentSet == mTotalSets) {
                removeNotification();
                stopUpdatingTimerFragment();
                mPauseResumeFlag = getResources().getInteger(R.integer.stop);
                mTimer.cancel();
                mTimer = null;
                mCurrentSet = 0;
            }
        }
    }

    private void stopUpdatingTimerFragment() {
        if (!stopFlag) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putBoolean(getString(R.string.timer_running), false);
            message.setData(bundle);
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTimerFragment(String exerciseName) {
        if (!stopFlag) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putBoolean(getString(R.string.timer_running), true);
            message.arg1 = mCurrentExerciseTime;
            message.arg2 = mCurrentSet;
            bundle.putString(getString(R.string.exercise_name), exerciseName);
            message.setData(bundle);
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    private void removeNotification() {
        mNotificationManager.cancel(0);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateNotification(String time, String exerciseName, int mRepeatTimes) {
        mBuilder.setContentTitle(mWorkoutName + " " + "set " + mRepeatTimes + " " + exerciseName)
                .setContentText(time);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getString(R.string.from_notification), true);
        intent.putExtra(getString(R.string.workout_model), mWorkoutModelArrayList);
        intent.putExtra(getString(R.string.set_number), mTotalSets);
        intent.putExtra(getString(R.string.workout_name), mWorkoutName);
        intent.putExtra(getString(R.string.exercise_name), exerciseName);
        intent.putExtra(getString(R.string.timer_state), mPauseResumeFlag);
        intent.putExtra(getString(R.string.current_set), mCurrentSet);
        intent.putExtra(getString(R.string.time), mCurrentExerciseTime);
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
