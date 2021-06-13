package com.example.smartlock;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.List;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

//import android.view.View;
//import android.widget.QuickContactBadge;
//import static com.example.smartlock.ReceiveNotification.subscribeToTopic;

public class CamView extends AppCompatActivity {
    public static Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



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

}