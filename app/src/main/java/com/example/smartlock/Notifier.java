package com.example.smartlock;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class Notifier extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent fullScreenIntent = new Intent(this, CamView.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, String.valueOf(R.string.channel_name))
                        .setChannelId(String.valueOf(R.string.channel_name))
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
                        .setFullScreenIntent(fullScreenPendingIntent, true);

        Notification incomingCallNotification = notificationBuilder.build();

        startForeground(createID(), incomingCallNotification);

        return super.onStartCommand(intent, flags, startId);
    }

    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}