package com.example.smartlock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.TopicName;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class ListenerService extends Worker {
    public static ByteString data;
    public static Intent notificationIntent;

    public ListenerService(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public @NotNull Result doWork() {
        Log.d(TAG, "onStartJob: listener service started.");
        try {
            listen(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success();
    }

    public static void listen(Context context) throws IOException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String device_id = prefs.getString("device_id", "manish");
        String lock_uuid = prefs.getString("lock_uuid", "");

        if (device_id.equals("")) {
            return;
        }

        String projectId = context.getString(R.string.project_id);
        String subscriptionId = "phone" + device_id;
        String topicId = "LockRequest";// + lock_uuid;

        if (!(subscriptionCreated(subscriptionId, projectId, topicId))) {
            createSubscription(projectId, subscriptionId, topicId);
        }

        String credentialJSONFileData = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"vocal-gist-315804\",\n" +
                "  \"private_key_id\": \"d48cf3f9e13e5071a35b92df827cd72c99630472\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC2QyUP65Ddy8R2\\nuDKJ1/az4EMfzxMWFyAluf2rH7gMsQSAq+BkbxdlbjHE62Q6TeIpSOGOpce8Hwzm\\nHv2WfTuQEqbFqDrdigNbmjSOQq+5XaiImeeWRyVkhCz11q0ICSzACrTeAEEUTBRS\\nZgIYvaJ6NqaqCAoiFydRl1eqdgSAbzSAtCk0xD8wCH+eadqyZ824593TUiCZ5Ofb\\nXF4stsJYiTd4pAl165CygOpXl5l1wSQmzkfUFgTsFJ39ntLkY/VplviCWZ8WYq0k\\nH8l1E27npy8DIzNycTcqj72j9l+92456uUiJjEygvuKKj91G7PGC/bWMalu1BNCX\\nDadv/2BRAgMBAAECggEACvI9Z5deO/Pg+MBXSLAPwP3XCjIFk24QIDPraDufciOv\\ngYrRDzcc0JqtooYAnv/OdWxLH6YVONckjy2NTVx4F08HdReFcYi4auf4ABNBOoDF\\nfm6/2ear+D139cvO2Iy0g1QhSLP7NJzdG5XEyneZKn3dxJh3A1Ugp2XPfg1Yd94b\\n42O1s7zRKoWyeFAHS2wzmF5y2U+WFjew7k0XOsSuzM1S0bWAFGl+JMI7hqJLyXMf\\n7OUclfVqnuTKX9fQYJ+gjCmIayQzkQyjWUdWFyW66f1vnxqSWpM9zgRvdzPHH1Xc\\nlHFD9a68sy8aNceU8XjB8SFr1UasQFb3IfQ3BOKBgQKBgQDpNXCxkgXSbfgiVCBw\\nl5wO2b6jOnw+ue1TVtIEZNSEc6xyu7dx1QNlENOwpPM+rCEfx8WQnRe0GGOp9+tZ\\nXsZqM/01mbGdfc/rD1yd8KhGX7s4xwvR2Lnr+A5xyfOBRs5pWyXRlp6FfCRmorot\\nlRuMzGZTkrcqA8CNhEH/cmDpVwKBgQDIExg1lyICImXktEVHBLb/GEOOYpAyqwnY\\nz5iPYip3CH8idZ5tW5n0lz7l3n2xcDMt/vdy2j7MYkxyriTkKP9J7Bl2oycMs53r\\nUoGmaaItwJ1JtjmgvtDD1LNpRgcj/HNk+TZM3vsmwgPCWjCQ7UjYn3pxJkjyzB1F\\nnYEvuVtylwKBgCttVCr88jCZYdeiqtfMo7bwPVvQDErx/IvEYx+GVzzC/Z3v7t03\\nuYYceA8w6EsHarOsU9H2Lu/OiuCf1ieySmiEfeZ5VGXrg8hV9/1BT1SrhwWvqzfM\\nj/KtIqN14+5y7QUZgBVjYumgkHa8XscWOSci+PsfGxSr1jOEKXYUKo/JAoGAfrF4\\ntxbI9kkJ9NUiuWIG72pR/Sv02+DrD1wF42XqNg4vU4bfl6hVyUitD6rgNSgzgIDf\\n+N6yvYHRnx0fR0uevRTA2xtdcvGaopVQfCyQAiDiePbnbx7l29wo8BTkGKmX+OzG\\nsc7Rgy2Aa+Xosx1O772tL1FrCDSwpJCkabAadGkCgYAjy29M0HgQS32yN3YT0mK3\\nu1gIJPEGIGe0Om0JUxgSaU8dFvD26cNSUC9TPN3R7Nl0r4+99wp4CccyLRabnnzF\\nDP4P6pN2VMV8fKDEwFDzWm0CfQH9F0T2eTt2WTnXlTXU2nI/zEpvOGfwVLQPSoA2\\nYWy6zVsosyIpv/Sw6H4USA==\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"lock-655@vocal-gist-315804.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"113701493789411947251\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/lock-655%40vocal-gist-315804.iam.gserviceaccount.com\"\n" +
                "}\n";
        InputStream credentialStream = new ByteArrayInputStream(credentialJSONFileData.getBytes());
        GoogleCredentials credential = GoogleCredentials.fromStream(credentialStream)
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/pubsub"));

        subscribeAsync(projectId, subscriptionId, credential, context);
    }

    public static void subscribeAsync(String projectId, String subscriptionId, GoogleCredentials credential, Context context) {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);

        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    Log.d(TAG, "subscribeAsync: Message received.");
                    consumer.ack();
                    System.out.println("Id: " + message.getMessageId());
                    System.out.println("Data: " + message.getData().toStringUtf8());

                    data = message.getData();

                    Intent intent = new Intent(context, Notifier.class);
                    notificationIntent = intent;
                    context.startService(intent);
                    // Handle incoming message, then ack the received message.
                    System.out.println("Id: " + message.getMessageId());
                    System.out.println("Data: " + message.getData().toStringUtf8());

                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    context.stopService(intent);
                };

        //HttpTransport transport = new NetHttpTransport();
        //GoogleCredential credential = new GoogleCredential.Builder() .setTransport(transport) .setJsonFactory(JSON_FACTORY) .setServiceAccountId(serviceAccount) .setServiceAccountScopes(Arrays.asList("https://www.googleapis.com/auth/pubsub")) .setServiceAccountPrivateKeyFromP12File(new File(keyFile)) .build();



        Subscriber subscriber = null;
        subscriber = Subscriber.newBuilder(subscriptionName, receiver).setCredentialsProvider(FixedCredentialsProvider.create(credential)).build();
        //.newBuilder(subscriptionName, receiver).setCredentialsProvider(FixedCredentialsProvider.create(credential)).build();
        // Start the subscriber.


        subscriber.startAsync().awaitRunning();
        System.out.printf("Listening for messages on %s:\n", subscriptionName.toString());
        // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
    }

    private static boolean subscriptionCreated(String subscriptionId, String projectId, String topicId) throws IOException {
        String credentialJSONFileData = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"vocal-gist-315804\",\n" +
                "  \"private_key_id\": \"d48cf3f9e13e5071a35b92df827cd72c99630472\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC2QyUP65Ddy8R2\\nuDKJ1/az4EMfzxMWFyAluf2rH7gMsQSAq+BkbxdlbjHE62Q6TeIpSOGOpce8Hwzm\\nHv2WfTuQEqbFqDrdigNbmjSOQq+5XaiImeeWRyVkhCz11q0ICSzACrTeAEEUTBRS\\nZgIYvaJ6NqaqCAoiFydRl1eqdgSAbzSAtCk0xD8wCH+eadqyZ824593TUiCZ5Ofb\\nXF4stsJYiTd4pAl165CygOpXl5l1wSQmzkfUFgTsFJ39ntLkY/VplviCWZ8WYq0k\\nH8l1E27npy8DIzNycTcqj72j9l+92456uUiJjEygvuKKj91G7PGC/bWMalu1BNCX\\nDadv/2BRAgMBAAECggEACvI9Z5deO/Pg+MBXSLAPwP3XCjIFk24QIDPraDufciOv\\ngYrRDzcc0JqtooYAnv/OdWxLH6YVONckjy2NTVx4F08HdReFcYi4auf4ABNBOoDF\\nfm6/2ear+D139cvO2Iy0g1QhSLP7NJzdG5XEyneZKn3dxJh3A1Ugp2XPfg1Yd94b\\n42O1s7zRKoWyeFAHS2wzmF5y2U+WFjew7k0XOsSuzM1S0bWAFGl+JMI7hqJLyXMf\\n7OUclfVqnuTKX9fQYJ+gjCmIayQzkQyjWUdWFyW66f1vnxqSWpM9zgRvdzPHH1Xc\\nlHFD9a68sy8aNceU8XjB8SFr1UasQFb3IfQ3BOKBgQKBgQDpNXCxkgXSbfgiVCBw\\nl5wO2b6jOnw+ue1TVtIEZNSEc6xyu7dx1QNlENOwpPM+rCEfx8WQnRe0GGOp9+tZ\\nXsZqM/01mbGdfc/rD1yd8KhGX7s4xwvR2Lnr+A5xyfOBRs5pWyXRlp6FfCRmorot\\nlRuMzGZTkrcqA8CNhEH/cmDpVwKBgQDIExg1lyICImXktEVHBLb/GEOOYpAyqwnY\\nz5iPYip3CH8idZ5tW5n0lz7l3n2xcDMt/vdy2j7MYkxyriTkKP9J7Bl2oycMs53r\\nUoGmaaItwJ1JtjmgvtDD1LNpRgcj/HNk+TZM3vsmwgPCWjCQ7UjYn3pxJkjyzB1F\\nnYEvuVtylwKBgCttVCr88jCZYdeiqtfMo7bwPVvQDErx/IvEYx+GVzzC/Z3v7t03\\nuYYceA8w6EsHarOsU9H2Lu/OiuCf1ieySmiEfeZ5VGXrg8hV9/1BT1SrhwWvqzfM\\nj/KtIqN14+5y7QUZgBVjYumgkHa8XscWOSci+PsfGxSr1jOEKXYUKo/JAoGAfrF4\\ntxbI9kkJ9NUiuWIG72pR/Sv02+DrD1wF42XqNg4vU4bfl6hVyUitD6rgNSgzgIDf\\n+N6yvYHRnx0fR0uevRTA2xtdcvGaopVQfCyQAiDiePbnbx7l29wo8BTkGKmX+OzG\\nsc7Rgy2Aa+Xosx1O772tL1FrCDSwpJCkabAadGkCgYAjy29M0HgQS32yN3YT0mK3\\nu1gIJPEGIGe0Om0JUxgSaU8dFvD26cNSUC9TPN3R7Nl0r4+99wp4CccyLRabnnzF\\nDP4P6pN2VMV8fKDEwFDzWm0CfQH9F0T2eTt2WTnXlTXU2nI/zEpvOGfwVLQPSoA2\\nYWy6zVsosyIpv/Sw6H4USA==\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"lock-655@vocal-gist-315804.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"113701493789411947251\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/lock-655%40vocal-gist-315804.iam.gserviceaccount.com\"\n" +
                "}\n";
        InputStream credentialStream = new ByteArrayInputStream(credentialJSONFileData.getBytes());
        GoogleCredentials credential = GoogleCredentials.fromStream(credentialStream)
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/pubsub"));
        TopicAdminSettings topicAdminSettings = TopicAdminSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credential)).build();
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create(topicAdminSettings)) {
            TopicName topicName = TopicName.of(projectId, topicId);
            for (String subscription : topicAdminClient.listTopicSubscriptions(topicName).iterateAll()) {
                Log.d(TAG, "subscriptionCreated: " + subscription);
                if (subscription.equals("projects/"+projectId+"/subscriptions/"+subscriptionId)) {
                    Log.d(TAG, "subscriptionCreated: Subscription found.");
                    return true;
                }
            }
        }
        Log.d(TAG, "subscriptionCreated: Subscription not found.");
        return false;
    }

    private static void createSubscription(String projectId, String subscriptionId, String topicId) throws IOException {
        String credentialJSONFileData = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"vocal-gist-315804\",\n" +
                "  \"private_key_id\": \"d48cf3f9e13e5071a35b92df827cd72c99630472\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC2QyUP65Ddy8R2\\nuDKJ1/az4EMfzxMWFyAluf2rH7gMsQSAq+BkbxdlbjHE62Q6TeIpSOGOpce8Hwzm\\nHv2WfTuQEqbFqDrdigNbmjSOQq+5XaiImeeWRyVkhCz11q0ICSzACrTeAEEUTBRS\\nZgIYvaJ6NqaqCAoiFydRl1eqdgSAbzSAtCk0xD8wCH+eadqyZ824593TUiCZ5Ofb\\nXF4stsJYiTd4pAl165CygOpXl5l1wSQmzkfUFgTsFJ39ntLkY/VplviCWZ8WYq0k\\nH8l1E27npy8DIzNycTcqj72j9l+92456uUiJjEygvuKKj91G7PGC/bWMalu1BNCX\\nDadv/2BRAgMBAAECggEACvI9Z5deO/Pg+MBXSLAPwP3XCjIFk24QIDPraDufciOv\\ngYrRDzcc0JqtooYAnv/OdWxLH6YVONckjy2NTVx4F08HdReFcYi4auf4ABNBOoDF\\nfm6/2ear+D139cvO2Iy0g1QhSLP7NJzdG5XEyneZKn3dxJh3A1Ugp2XPfg1Yd94b\\n42O1s7zRKoWyeFAHS2wzmF5y2U+WFjew7k0XOsSuzM1S0bWAFGl+JMI7hqJLyXMf\\n7OUclfVqnuTKX9fQYJ+gjCmIayQzkQyjWUdWFyW66f1vnxqSWpM9zgRvdzPHH1Xc\\nlHFD9a68sy8aNceU8XjB8SFr1UasQFb3IfQ3BOKBgQKBgQDpNXCxkgXSbfgiVCBw\\nl5wO2b6jOnw+ue1TVtIEZNSEc6xyu7dx1QNlENOwpPM+rCEfx8WQnRe0GGOp9+tZ\\nXsZqM/01mbGdfc/rD1yd8KhGX7s4xwvR2Lnr+A5xyfOBRs5pWyXRlp6FfCRmorot\\nlRuMzGZTkrcqA8CNhEH/cmDpVwKBgQDIExg1lyICImXktEVHBLb/GEOOYpAyqwnY\\nz5iPYip3CH8idZ5tW5n0lz7l3n2xcDMt/vdy2j7MYkxyriTkKP9J7Bl2oycMs53r\\nUoGmaaItwJ1JtjmgvtDD1LNpRgcj/HNk+TZM3vsmwgPCWjCQ7UjYn3pxJkjyzB1F\\nnYEvuVtylwKBgCttVCr88jCZYdeiqtfMo7bwPVvQDErx/IvEYx+GVzzC/Z3v7t03\\nuYYceA8w6EsHarOsU9H2Lu/OiuCf1ieySmiEfeZ5VGXrg8hV9/1BT1SrhwWvqzfM\\nj/KtIqN14+5y7QUZgBVjYumgkHa8XscWOSci+PsfGxSr1jOEKXYUKo/JAoGAfrF4\\ntxbI9kkJ9NUiuWIG72pR/Sv02+DrD1wF42XqNg4vU4bfl6hVyUitD6rgNSgzgIDf\\n+N6yvYHRnx0fR0uevRTA2xtdcvGaopVQfCyQAiDiePbnbx7l29wo8BTkGKmX+OzG\\nsc7Rgy2Aa+Xosx1O772tL1FrCDSwpJCkabAadGkCgYAjy29M0HgQS32yN3YT0mK3\\nu1gIJPEGIGe0Om0JUxgSaU8dFvD26cNSUC9TPN3R7Nl0r4+99wp4CccyLRabnnzF\\nDP4P6pN2VMV8fKDEwFDzWm0CfQH9F0T2eTt2WTnXlTXU2nI/zEpvOGfwVLQPSoA2\\nYWy6zVsosyIpv/Sw6H4USA==\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"lock-655@vocal-gist-315804.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"113701493789411947251\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/lock-655%40vocal-gist-315804.iam.gserviceaccount.com\"\n" +
                "}\n";
        InputStream credentialStream = new ByteArrayInputStream(credentialJSONFileData.getBytes());
        GoogleCredentials credential = GoogleCredentials.fromStream(credentialStream)
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/pubsub"));
        SubscriptionAdminSettings subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credential)).build();
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(subscriptionAdminSettings)) {
            TopicName topicName = TopicName.of(projectId, topicId);
            ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);
            // Create a pull subscription with default acknowledgement deadline of 10 seconds.
            // Messages not successfully acknowledged within 10 seconds will get resent by the server.
            Subscription subscription = subscriptionAdminClient.createSubscription(subscriptionName, topicName, PushConfig.getDefaultInstance(), 120);
            Log.d(TAG, "createSubscription: Subscription created.");
        }
    }
}
