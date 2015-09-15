package com.example.krishnavelagapudi.intervaltrainingtimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
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
    private static final int STOP = 2;
    private final IBinder mBinder = new TimerBinder();
    private ArrayList<WorkoutModel> mWorkoutModelArrayList = new ArrayList<>();
    private String mWorkoutName;
    private int mExerciseNumber;
    private int mTotalSets;
    final static int RESUME = 1;
    int mPauseResumeFlag = RESUME;
    private Timer mTimer;
    Messenger mMessenger;
    private int mTotalTime;
    private int mRepeatTimes;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    private int mNumMessages;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service started");
        startNotificationBuilder();
        return START_NOT_STICKY;
    }

    public boolean isTimerRunning() {
        if (mTimer == null) {
            return false;
        }
        return true;
    }

    public class TimerBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void setMessenger(Messenger messenger) {
        mMessenger = messenger;
    }

    public void setWorkoutArrayList(ArrayList<WorkoutModel> workoutArrayList, String workoutName, int totalSets) {
        mWorkoutModelArrayList = workoutArrayList;
        mWorkoutName = workoutName;
        mTotalSets = totalSets;
        initFields();
    }

    private void initFields() {
        mTotalTime = -1;
        mRepeatTimes = mTotalSets;
        mExerciseNumber = 1;
    }

    public void pauseResumeTimer(int pauseResumeFlag) {
        mPauseResumeFlag = pauseResumeFlag;
        initTimer();
    }

    private void startNotificationBuilder() {
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.timer);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNumMessages = 0;
    }

    private void initTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new IntervalTimerTask(), 0, 1000);
        }
    }

    public class IntervalTimerTask extends TimerTask {


        @Override
        public void run() {
            if (mTotalTime == -1) {
                mRepeatTimes--;
                try {
                    sendWorkoutNumber(mTotalSets - mRepeatTimes);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            while (mExerciseNumber <= mWorkoutModelArrayList.size()) {
                final WorkoutModel workoutModel = mWorkoutModelArrayList.get(mExerciseNumber - 1);
                try {
                    sendExerciseName(workoutModel.getExerciseName());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (mTotalTime == -1) {
                    if (workoutModel.getMin() > 0) {
                        mTotalTime = workoutModel.getMin() * 60;
                    }
                    mTotalTime = mTotalTime + workoutModel.getSec();
                }
                while (mTotalTime >= 0) {
                    if (mPauseResumeFlag == RESUME) {
                        final String time = String.format("%02d", (mTotalTime / 60)) + ":" + String.format("%02d", (mTotalTime % 60));
                        try {
                            sendTime(time, mTotalTime);
                            updateNotification(time, workoutModel.getExerciseName(), mTotalSets - mRepeatTimes);
                            mTotalTime--;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mTotalTime == 5) {
                        int resID = getResources().getIdentifier("countdown", "raw", getPackageName());
                        MediaPlayer mediaPlayer = MediaPlayer.create(TimerService.this, resID);
                        mediaPlayer.start();
                    }

                }
                mExerciseNumber++;

            }
            mExerciseNumber = 1;
            mTotalTime = -1;
            if (mRepeatTimes <= 0) {
                try {
                    sendWorkoutNumber(-1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                removeNotification();
                mPauseResumeFlag = STOP;
                mTimer.cancel();
                mTimer = null;
                initFields();
            }
        }
    }

    private void removeNotification() {
        mNotificationManager.cancel(0);
    }

    private void updateNotification(String time, String exerciseName, int mRepeatTimes) {
        mBuilder.setContentTitle(mWorkoutName + " " + "set " + mRepeatTimes + " " + exerciseName)
                .setContentText(time);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(0, notification);
    }

    private void sendTime(String time, int totalTime) throws RemoteException {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.time), time);
        message.arg2 = totalTime;
        message.setData(bundle);
        mMessenger.send(message);
    }

    private void sendExerciseName(String exerciseName) throws RemoteException {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.exercise_name), exerciseName);
        message.setData(bundle);
        mMessenger.send(message);
    }

    private void sendWorkoutNumber(int i) throws RemoteException {
        Message message = new Message();
        message.arg1 = i;
        mMessenger.send(message);
    }


}
