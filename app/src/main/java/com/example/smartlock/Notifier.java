package com.example.smartlock;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

public class Notifier extends Worker {
    private Context cont;
    public Notifier(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        cont = context;
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {

        Intent fullScreenIntent = new Intent(cont, CamView.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(cont, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification =
                new NotificationCompat.Builder(cont, String.valueOf(R.string.channel_name))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Incoming call")
                        .setContentText("(919) 555-1234")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)

                        // Use a full-screen intent only for the highest-priority alerts where you
                        // have an associated activity that you would like to launch after the user
                        // interacts with the notification. Also, if your app targets Android 10
                        // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                        // order for the platform to invoke this notification.
                        .setFullScreenIntent(fullScreenPendingIntent, true)
                        .build();
        
        return null;
    }

    @NonNull
    public ForegroundInfo sendNotification() {



        //Notification incomingCallNotification = notificationBuilder.build();

        return new ForegroundInfo(notification);
    }
}
