package com.example.smartlock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class Notifier extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Context context = this;

        Intent fullScreenIntent = new Intent(context, CamView.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //CamView.
        Log.d(TAG, "onCreate: Notification triggered.");
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel doorBellChannel = new NotificationChannel(getString(R.string.channel_name), name, importance);
        doorBellChannel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(doorBellChannel);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, getString(R.string.channel_name))
                        //.setChannelId(String.valueOf(R.string.channel_name))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Someone is at the door")
                        .setContentText("Open notification to view details.")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setChannelId(getString(R.string.channel_name))
                        .setFullScreenIntent(fullScreenPendingIntent, true);


        Notification incomingCallNotification = notificationBuilder.build();

        Log.d(TAG, "onCreate: Here 1");

        int notificationId = createID();
        //incomingCallNotification.notify();
        //startForegroundService(fullScreenIntent);
        //notificationManager.notify(notificationId, incomingCallNotification);
        startForeground(notificationId, incomingCallNotification);


        Log.d(TAG, "onCreate: Here 2");

/*

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopForeground(true);*/

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        /*
        Notification notification = new NotificationCompat.Builder(this, String.valueOf(R.string.channel_name))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

         */


        //(createID(), notification);

        return super.onStartCommand(intent, flags, startId);
    }

    public int createID(){
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}