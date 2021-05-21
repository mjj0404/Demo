package com.jaej.demo.ui.home;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.jaej.demo.MainActivity;
import com.jaej.demo.R;
import com.jaej.demo.model.Record;
import com.jaej.demo.model.Task;
import com.jaej.demo.util.Constants;
import com.jaej.demo.util.Utility;

import java.util.Timer;
import java.util.TimerTask;

public class CountdownService extends Service {

    private String taskName = "Countdown Timer";
    private boolean isTimerForceStopped = false;
    private Task currentTask;
    private Record currentRecord;
    private String currentTaskString, currentRecordString;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Integer[] timeRemaining = {intent.getIntExtra(Constants.TIMER_TIME_VALUE, 0)};
        boolean isTaskTimer = intent.getBooleanExtra(Constants.TIMER_TASK_TIMER, true);

        currentTaskString = intent.getStringExtra(Constants.TASK_FROM_FRAGMENT);
        currentRecordString = intent.getStringExtra(Constants.RECORD_FROM_FRAGMENT);

        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                boolean isTaskFinished = false;

                Intent runningTimerIntent = new Intent();
                runningTimerIntent.setAction(Constants.COUNTER);

                Gson gson = new Gson();
                currentTask = gson.fromJson(intent.getStringExtra(Constants.TASK_FROM_FRAGMENT),Task.class);
                currentRecord = gson.fromJson(intent.getStringExtra(Constants.RECORD_FROM_FRAGMENT), Record.class);

                timeRemaining[0] = timeRemaining[0] - 1;
                //fires notification with current remaining time
                taskNotificationUpdate(timeRemaining[0]);

                if (timeRemaining[0] <= 0 && !isTimerForceStopped) {
                    //timer finished
                    timer.cancel();
                    isTaskFinished = true;
                    stopForeground(true);
                    stopSelf();

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = powerManager.newWakeLock((PowerManager.ACQUIRE_CAUSES_WAKEUP |
                                PowerManager.PARTIAL_WAKE_LOCK), "WAKE_UP");
                        wakeLock.acquire(1000);
                        wakeLock.release();
                    }
                }
                else if (isTimerForceStopped) {
                    //stopping timer
                    timeRemaining[0] = 0;
                    timer.cancel();
                    stopForeground(true);
                    stopSelf();
                }

                runningTimerIntent.putExtra(Constants.TIMER_TIME_REMAINING, timeRemaining[0]);
                runningTimerIntent.putExtra(Constants.TIMER_FINISHED, isTaskFinished);
                runningTimerIntent.putExtra(Constants.TIMER_BROADCAST, isTaskTimer);

                runningTimerIntent.putExtra(Constants.TIMER_TASK, currentTaskString);
                runningTimerIntent.putExtra(Constants.TIMER_RECORD, currentRecordString);
                //sending broadcast to doTimedTaskFragment
                sendBroadcast(runningTimerIntent);
            }
        };

        if (intent.getAction().equals(Constants.START_TASK_FOREGROUND)) {
            //start task timer
            taskName = intent.getStringExtra(Constants.TIMER_TASK_NAME);
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
        }
        else if (intent.getAction().equals(Constants.START_REST_FOREGROUND)) {
            //start rest timer
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
        }
        else if (intent.getAction().equals(Constants.STOP_FOREGROUND)) {
            //when force stopped
            isTimerForceStopped = true;
            timerTask.cancel();
            timer.cancel();
            timer.purge();

            stopForeground(true);
            stopSelfResult(startId);
        }
        return START_REDELIVER_INTENT;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void taskNotificationUpdate(Integer timeLeft){
        try {

            Intent relaunchIntent = new Intent(this, MainActivity.class);
            Gson gson = new Gson();
            String currentTaskString = gson.toJson(currentTask);
            String currentRecordString = gson.toJson(currentRecord);
            relaunchIntent.putExtra(Constants.TASK_FROM_SERVICE, currentTaskString);
            relaunchIntent.putExtra(Constants.RECORD_FROM_SERVICE, currentRecordString);

            relaunchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .setAction(Intent.ACTION_MAIN)
                    .addCategory(Constants.TASK_NOTIFICATION);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, relaunchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            String timeLeftFormatted = Utility.secondStringFormatHelper(timeLeft);

            final Notification[] notification = {new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                    .setContentTitle(taskName)
                    .setContentText("Time Left : " + timeLeftFormatted)
                    .setSmallIcon(R.drawable.ic_logo_bw)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent)
                    .build()};
            startForeground(Constants.CHANNEL, notification[0]);
            NotificationChannel notificationChannel = new NotificationChannel(
                    Constants.CHANNEL_ID,
                    Constants.COUNTER_SERVICE_NOTIFICATION,
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);


        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
