package com.example.smartlock;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.smartlock.MQTTLib;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class ReceiveNotification extends Worker {
    public ReceiveNotification(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void subscribeToTopic(String TopicName,
                                        String deviceId,
                                        String registryId,
                                        String cloudRegion,
                                        Context context)
            throws InvalidKeySpecException, MqttException, NoSuchAlgorithmException, InterruptedException, IOException {
        Log.d(TAG, "subscribeToTopic: subscribeToTopic started.");
        String data = TopicName + deviceId + registryId;
        MQTTLib.listen(context);
        Log.d(TAG, "subscribeToTopic: " + data);
    }

    @Override
    public @NotNull Result doWork() {
        Log.d(TAG, "onStartJob: listener service started.");
        try {
            subscribeToTopic("LockRequest",
                    "phone1",
                    "registry-2",
                    "us-central1",
                    null);
        } catch (InvalidKeySpecException | MqttException | NoSuchAlgorithmException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return Result.success();
    }

}
