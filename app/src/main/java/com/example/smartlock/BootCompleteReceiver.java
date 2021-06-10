package com.example.smartlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class BootCompleteReceiver extends BroadcastReceiver {
    public static Context appContext;

    public void onReceive(Context context, Intent intent ) {
        if (intent.getAction() == null || !intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            return;

        Log.d(TAG, "onReceive: " + context.toString());

        WorkRequest myWorkRequest = OneTimeWorkRequest.from(ListenerService.class);
        //myWorkRequest.
        WorkManager.getInstance(context).enqueue(myWorkRequest);

        appContext = context;

        /*
        Intent fullScreenIntent = new Intent(this, CallActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(appContext, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(appContext, String.valueOf(R.string.channel_name))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Someone at the door.")
                        .setContentText("There is a delivery agent at your door.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)

                        // Use a full-screen intent only for the highest-priority alerts where you
                        // have an associated activity that you would like to launch after the user
                        // interacts with the notification. Also, if your app targets Android 10
                        // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                        // order for the platform to invoke this notification.
                        .setFullScreenIntent(fullScreenPendingIntent, true);

        Notification incomingCallNotification = notificationBuilder.build();
*/
    }
}
