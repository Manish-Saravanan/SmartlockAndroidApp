package com.example.smartlock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
//import android.view.View;
import android.util.Log;
import android.widget.Button;
//import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class CamView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_view);


        //Intent listenerService = new Intent(this, ReceiveNotification.class);
        //startService(listenerService);


        WorkRequest myWorkRequest = OneTimeWorkRequest.from(ReceiveNotification.class);
        //myWorkRequest.

        WorkManager.getInstance(this).enqueue(myWorkRequest);

        createNotificationChannel();

        Button openButton = findViewById(R.id.openButton);
        openButton.setOnClickListener(v -> {
            Toast.makeText(this, "Button pressed", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onCreate: Button was pressed.");
            SendOpenCommand sendCommand = new SendOpenCommand("lock123", "vocal-gist-315804", "us-central1", "registry-2", "OpenDoor");
            sendCommand.start();
            });

        Button ignoreButton = findViewById(R.id.ignoreButton);
        ignoreButton.setOnClickListener(v -> finish());


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel doorBellChannel = new NotificationChannel("DoorBellChannel", name, importance);
        doorBellChannel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(doorBellChannel);
    }
}