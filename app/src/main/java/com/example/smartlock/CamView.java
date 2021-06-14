package com.example.smartlock;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.List;
import java.util.concurrent.Executor;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

//import android.view.View;
//import android.widget.QuickContactBadge;
//import static com.example.smartlock.ReceiveNotification.subscribeToTopic;

public class CamView extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;

    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_view);

        Button openButton = findViewById(R.id.openButton);
        openButton.setOnClickListener(v -> {
            biometricPrompt.authenticate(promptInfo);
        });

        Button ignoreButton = findViewById(R.id.ignoreButton);
        ignoreButton.setOnClickListener(v -> {
            stopService(ListenerService.notificationIntent);
            finish();
        });

        try {
            ImageView imageView = findViewById(R.id.imageView);
            byte[] decodedString = Base64.decode(ListenerService.data.toStringUtf8(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        } catch (RuntimeException e) {
            Log.d(TAG, "onCreate: " + e.toString());
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(CamView.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                SendOpenCommand sendCommand = new SendOpenCommand("lock123", "vocal-gist-315804", "us-central1", "registry-2", "OpenDoor");
                sendCommand.start();
                stopService(ListenerService.notificationIntent);
                Toast.makeText(getApplicationContext(), "The door will be opened.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= 30) {
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Approve access")
                    .setDescription("Use your biometric credential.")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                    .build();
        }
        else {
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Approve access")
                    .setDescription("Use your biometric credential.")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG)
                    .setNegativeButtonText("Cancel")
                    .build();
        }

    }

}