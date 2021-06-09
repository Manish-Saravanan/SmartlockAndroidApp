package com.example.smartlock;


import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.common.collect.Lists;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MQTTLib {
    public static void listen(Context context) throws IOException {
        String projectId = "vocal-gist-315804j";
        String subscriptionId = "phone1";

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
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloudiot"));

        subscribeAsync(projectId, subscriptionId, context, credential);
    }

    public static void subscribeAsync(String projectId, String subscriptionId, Context context, GoogleCredentials credential) {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);

        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    String CHANNEL_ID = "DoorBellChannel";
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle("Doorbell pressed.")
                            .setContentText(message.toString())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    // Handle incoming message, then ack the received message.
                    System.out.println("Id: " + message.getMessageId());
                    System.out.println("Data: " + message.getData().toStringUtf8());
                    consumer.ack();
                };

        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).setCredentialsProvider(FixedCredentialsProvider.create(credential)).build();
            // Start the subscriber.


            subscriber.startAsync().awaitRunning();
            System.out.printf("Listening for messages on %s:\n", subscriptionName.toString());
            // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
            subscriber.awaitTerminated(30, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            // Shut down the subscriber after 30s. Stop receiving messages.
            subscriber.stopAsync();
        }
    }
}

/*



import android.content.Context;
import java.util.Base64;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Properties;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class MQTTLib {

    static MqttCallback mCallback;

    */
/** Create a Cloud IoT Core JWT for the given project id, signed with the given RSA key. *//*

    private static String createJwtRsa(String projectId)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        DateTime now = new DateTime();
        JwtBuilder jwtBuilder =
                Jwts.builder()
                        .setIssuedAt(now.toDate())
                        .setExpiration(now.plusMinutes(20).toDate())
                        .setAudience(projectId);

        String keyData = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCZ2ZCk+F6TdihcB31NyPuaVEOjX/dSXkR74lXTsWgr6fDuEkwUar0V7ogJYQFYBo+HIlzJrT0CbDxDDC17+TrJk4HDFOwrdEf6UqUMXjssi+iXrpTuByTLleiHhAB+A4rHzOct7S2WEsMqpVhLgXng5LOPKbJOTyExr+C2gPknH5l8uKCXD8PgPJwn6TqMXSLF5JFmqkjmKUx9QGIO0pDR1yfhf3izwbqqBBNyZr1RKTNNepSFtVW1F0zgdwD9djYt6Bg+3Xh2+SVZATuAtkD/gfj4EOBWo56eo8IO9+4QfOlFCO5NGQ0yr17uS9TRyMSDCm3vaQRVJiA7dBUNsbN3AgMBAAECggEAPt5+UjvAgifttDXHSasQze8TOMb1jS5CdBF4mypK9G8D6nWFq2422KiB5LtETlFYA5PSMQPfA4dxVMC8MYYWNNtXUn3RSKya4Ts2gQhczAZcROetqls2+Q7IV+BkRxpxctZTvhvG3iXKpTmuDCTbxkAO9DjIyWFioihJ/qpTpl2MHK80/FiLMAV4GOfu9IfxhvNBoePA2opzUxmz5L+C2zp8cWn7QCmnOKlbTCZaEg/zgqPs9GdGPO3M8DOVJqSanP2BWhVkoJx3ye7YrXrjj1tkyGNhUKqkcJLW9dlTPI85ZtjyofVbG8GZukItnl9meCcJYMvsgCRJYUJP4C+KwQKBgQDL2BxdFVjrMcJqP+q9YgZl2ik3QGBFPx/U73GAnj9EIWz2I8tIRlTzM/r8MJgVjMyLEwV5VhNnQf55VMVAmyLn8l7oHrQxvRrAXIkngqxocK7KVLWaY71uae2kEvXgcbx4kFulCDc2sphcqlGXA4vi7Np0wEIivZ1A8AiY20n3FwKBgQDBNs2Nmle/i6XF+zn+wKVQAaSnShfv4bKf6CZuhFbtuQilzrvlngE3qWtUSVpIInrUWNxKQtWcZJ/Pe6sfrP5EIe+K68ykgm9hvesYjIKaxpBh57sJzZTIX7IT/4rI2BeDgfaF1fz3OraccbpUvfL2fKj07pjSZHjxItXRfy3ioQKBgFa5KMgJTv0w/E+LxeiwjZxDhol1IAAV7QsAm79mk+/+cIb/j3q+QWH+2Wb1nZ59eoAopYmLr1Gc6BDimdD0r5MSsIH8nhkTJRB1zfw7QfJlDlgSVfjLOkqBUpMdZfIJMW/xqqu5pRPmQ6AY7gFbhuYScpZ9PSXemcqI1H+DvCIdAoGBAKDs/U2YpMdED8GTWp2FZOr7Q2GWg9NnX3hySjg4f5KmT76SLsuzy33J3TwpkEmOdlGQ5YHwoqwM31JXxZKvamPjA/noJd1LSmvVXA9pUIqLIcx5zr3+fZJaqh6f878OxJ7iyypzratZOqmYy8IzJz8HW04NwDlUN6tYAVDpJZWhAoGAV62wV1Z+C1spugkpplgsze3Gxbu8RM/BDeB+VGpo6kOeiCxilIEX3qb7FxttIcIdPgGBNDL5ULbyIeBwSGY+yjoGWX3e40bgR8aQdw2GI1U7kyuk2udvntXMNzR/6CN+/ybAg+2X4gzK2xFRARXluXEilG+LP9GgytHOilnLeF8=";

        byte[] keyBytes = keyData.getBytes();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyBytes));
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return jwtBuilder.signWith(SignatureAlgorithm.RS256, kf.generatePrivate(spec)).compact();
    }

    */
/** Create a Cloud IoT Core JWT for the given project id, signed with the given ES key. *//*

    private static String createJwtEs(String projectId)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        DateTime now = new DateTime();
        JwtBuilder jwtBuilder =
                Jwts.builder()
                        .setIssuedAt(now.toDate())
                        .setExpiration(now.plusMinutes(20).toDate())
                        .setAudience(projectId);

        String keyData = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCZ2ZCk+F6TdihcB31NyPuaVEOjX/dSXkR74lXTsWgr6fDuEkwUar0V7ogJYQFYBo+HIlzJrT0CbDxDDC17+TrJk4HDFOwrdEf6UqUMXjssi+iXrpTuByTLleiHhAB+A4rHzOct7S2WEsMqpVhLgXng5LOPKbJOTyExr+C2gPknH5l8uKCXD8PgPJwn6TqMXSLF5JFmqkjmKUx9QGIO0pDR1yfhf3izwbqqBBNyZr1RKTNNepSFtVW1F0zgdwD9djYt6Bg+3Xh2+SVZATuAtkD/gfj4EOBWo56eo8IO9+4QfOlFCO5NGQ0yr17uS9TRyMSDCm3vaQRVJiA7dBUNsbN3AgMBAAECggEAPt5+UjvAgifttDXHSasQze8TOMb1jS5CdBF4mypK9G8D6nWFq2422KiB5LtETlFYA5PSMQPfA4dxVMC8MYYWNNtXUn3RSKya4Ts2gQhczAZcROetqls2+Q7IV+BkRxpxctZTvhvG3iXKpTmuDCTbxkAO9DjIyWFioihJ/qpTpl2MHK80/FiLMAV4GOfu9IfxhvNBoePA2opzUxmz5L+C2zp8cWn7QCmnOKlbTCZaEg/zgqPs9GdGPO3M8DOVJqSanP2BWhVkoJx3ye7YrXrjj1tkyGNhUKqkcJLW9dlTPI85ZtjyofVbG8GZukItnl9meCcJYMvsgCRJYUJP4C+KwQKBgQDL2BxdFVjrMcJqP+q9YgZl2ik3QGBFPx/U73GAnj9EIWz2I8tIRlTzM/r8MJgVjMyLEwV5VhNnQf55VMVAmyLn8l7oHrQxvRrAXIkngqxocK7KVLWaY71uae2kEvXgcbx4kFulCDc2sphcqlGXA4vi7Np0wEIivZ1A8AiY20n3FwKBgQDBNs2Nmle/i6XF+zn+wKVQAaSnShfv4bKf6CZuhFbtuQilzrvlngE3qWtUSVpIInrUWNxKQtWcZJ/Pe6sfrP5EIe+K68ykgm9hvesYjIKaxpBh57sJzZTIX7IT/4rI2BeDgfaF1fz3OraccbpUvfL2fKj07pjSZHjxItXRfy3ioQKBgFa5KMgJTv0w/E+LxeiwjZxDhol1IAAV7QsAm79mk+/+cIb/j3q+QWH+2Wb1nZ59eoAopYmLr1Gc6BDimdD0r5MSsIH8nhkTJRB1zfw7QfJlDlgSVfjLOkqBUpMdZfIJMW/xqqu5pRPmQ6AY7gFbhuYScpZ9PSXemcqI1H+DvCIdAoGBAKDs/U2YpMdED8GTWp2FZOr7Q2GWg9NnX3hySjg4f5KmT76SLsuzy33J3TwpkEmOdlGQ5YHwoqwM31JXxZKvamPjA/noJd1LSmvVXA9pUIqLIcx5zr3+fZJaqh6f878OxJ7iyypzratZOqmYy8IzJz8HW04NwDlUN6tYAVDpJZWhAoGAV62wV1Z+C1spugkpplgsze3Gxbu8RM/BDeB+VGpo6kOeiCxilIEX3qb7FxttIcIdPgGBNDL5ULbyIeBwSGY+yjoGWX3e40bgR8aQdw2GI1U7kyuk2udvntXMNzR/6CN+/ybAg+2X4gzK2xFRARXluXEilG+LP9GgytHOilnLeF8=";

        byte[] keyBytes = keyData.getBytes();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyBytes));
        KeyFactory kf = KeyFactory.getInstance("EC");

        return jwtBuilder.signWith(SignatureAlgorithm.ES256, kf.generatePrivate(spec)).compact();
    }

    */
/** Attaches the callback used when configuration changes occur. *//*


    public static void mqttDeviceDemo(
            String projectId,
            String cloudRegion,
            String registryId,
            String deviceId,
            String algorithm,
            String mqttBridgeHostname,
            short mqttBridgePort,
            String messageType,
            int waitTime,
            Context context,
            String topic)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, MqttException,
            InterruptedException {

        final String mqttServerAddress =
                String.format("ssl://%s:%s", mqttBridgeHostname, mqttBridgePort);

        final String mqttClientId =
                String.format(
                        "projects/%s/locations/%s/registries/%s/devices/%s",
                        projectId, cloudRegion, registryId, deviceId);

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);

        Properties sslProps = new Properties();
        sslProps.setProperty("com.ibm.ssl.protocol", "TLSv1.2");
        connectOptions.setSSLProperties(sslProps);

        connectOptions.setUserName("unused");

        DateTime iat = new DateTime();
        if (algorithm.equals("RS256")) {
            connectOptions.setPassword(createJwtRsa(projectId).toCharArray());
        } else if (algorithm.equals("ES256")) {
            connectOptions.setPassword(createJwtEs(projectId).toCharArray());
        } else {
            throw new IllegalArgumentException(
                    "Invalid algorithm " + algorithm + ". Should be one of 'RS256' or 'ES256'.");
        }

        // [START iot_mqtt_publish]
        // Create a client, and connect to the Google MQTT bridge.
        MqttClient client = new MqttClient(mqttServerAddress, mqttClientId, new MemoryPersistence());

        // Both connect and publish operations may fail. If they do, allow retries but with an
        // exponential backoff time period.
        long initialConnectIntervalMillis = 500L;
        long maxConnectIntervalMillis = 6000L;
        long maxConnectRetryTimeElapsedMillis = 900000L;
        float intervalMultiplier = 1.5f;

        long retryIntervalMs = initialConnectIntervalMillis;
        long totalRetryTimeMs = 0;


        while (!client.isConnected() && totalRetryTimeMs < maxConnectRetryTimeElapsedMillis) {
            try {
                client.connect(connectOptions);
            } catch (MqttException f) {
                try {
                    client.disconnect();
                    client.connect(connectOptions);
                } catch (MqttException e) {
                    int reason = e.getReasonCode();


                    // If the connection is lost or if the server cannot be connected, allow retries, but with
                    // exponential backoff.
                    System.out.println("An error occurred: " + e.getMessage());
                    if (reason == MqttException.REASON_CODE_CONNECTION_LOST
                            || reason == MqttException.REASON_CODE_SERVER_CONNECT_ERROR) {
                        System.out.println("Retrying in " + retryIntervalMs / 1000.0 + " seconds.");
                        Thread.sleep(retryIntervalMs);
                        totalRetryTimeMs += retryIntervalMs;
                        retryIntervalMs *= intervalMultiplier;
                        if (retryIntervalMs > maxConnectIntervalMillis) {
                            retryIntervalMs = maxConnectIntervalMillis;
                        }
                    } else {
                        throw e;
                    }
                }
            }
        }

        String connTopic = String.format("/topics/%s", topic);
        System.out.printf("Listening on %s%n", connTopic);

        try {
            client.subscribe(connTopic, 0);
            client.setCallback(mCallback);
            Log.d(TAG, "mqttDeviceDemo: Connected");
            // Wait for commands to arrive for about two minutes.
            for (int i = 1; i <= waitTime; ++i) {
                System.out.print(".");
                Thread.sleep(1000);
            }
            System.out.println("");

            // Disconnect the client if still connected, and finish the run.
            if (client.isConnected()) {
                client.disconnect();
            }

            System.out.println("Finished loop successfully. Goodbye!");
            client.close();
            //System.exit(0);
            // [END iot_mqtt_publish]
        } catch (MqttException g) {
            Log.d(TAG, "mqttDeviceDemo: " + g.toString());
        } finally {
            client.unsubscribe(connTopic);
        }

        mCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // Do nothing...
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());
                System.out.println("Payload : " + payload);

                String CHANNEL_ID = "DoorBellChannel";
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Doorbell pressed.")
                        .setContentText(payload)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                // The device will receive its latest config when it subscribes to the
                // config topic. If there is no configuration for the device, the device
                // will receive a config with an empty payload.
                if (payload.length() == 0) {
                    return;
                }
                if (isJsonValid(payload)) {
                    JSONObject data = null;
                    data = new JSONObject(payload);
                    Log.d(TAG, "messageArrived: message has been recieved..");
                    //String CHANNEL_ID = "DoorBellChannel";
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle("Doorbell pressed.")
                            .setContentText(data.toString())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Do nothing;
            }
        };

    }

    public static boolean isJsonValid(String data) {
        try {
            new JSONObject(data);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(data);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}*/
