package com.example.smartlock;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import static android.content.ContentValues.TAG;

public class FirebaseService extends FirebaseMessagingService {
    public void onNewToken(@NotNull String token) {
        Log.d(TAG, "onNewToken: " + token);
        SendOpenCommand sendOpenCommand = new SendOpenCommand("lock123", getString(R.string.project_id), "us-central1", "registry-2", token);
        sendOpenCommand.start();
    }
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());

    }
}
