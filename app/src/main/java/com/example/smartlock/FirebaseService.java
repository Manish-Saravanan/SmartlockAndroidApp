package com.example.smartlock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.constraintlayout.motion.utils.Oscillator;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.protobuf.ByteString;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class FirebaseService extends FirebaseMessagingService {
    public static String data;
    public void onNewToken(@NotNull String token) {
        Log.d(TAG, "onNewToken: " + token);
        SendOpenCommand sendOpenCommand = new SendOpenCommand("lock123", getString(R.string.project_id), "us-central1", "registry-2", "New token: " + token);
        sendOpenCommand.start();
    }
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        data = remoteMessage.getData().get("imageData");

        Intent fullScreenIntent = new Intent(getApplicationContext(), CamView.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //CamView.
        Log.d(Oscillator.TAG, "onCreate: Notification triggered.");
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
                new NotificationCompat.Builder(getApplicationContext(), getString(R.string.channel_name))
                        //.setChannelId(String.valueOf(R.string.channel_name))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Someone is at the door")
                        .setContentText("Open notification to view details.")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setChannelId(getString(R.string.channel_name))
                        .setFullScreenIntent(fullScreenPendingIntent, true);


        Notification incomingCallNotification = notificationBuilder.build();

        Log.d(Oscillator.TAG, "onCreate: Here 1");

        int notificationId = createID();
        //incomingCallNotification.notify();
        //startForegroundService(fullScreenIntent);
        //notificationManager.notify(notificationId, incomingCallNotification);
        startForeground(notificationId, incomingCallNotification);


        Log.d(Oscillator.TAG, "onCreate: Here 2");
        Intent notifierIntent = new Intent(getApplicationContext(), Notifier.class);
        getApplicationContext().startForegroundService(notifierIntent);
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());

    }
    public int createID(){
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
    }

}
