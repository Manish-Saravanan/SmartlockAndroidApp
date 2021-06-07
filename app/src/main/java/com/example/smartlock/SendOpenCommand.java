package com.example.smartlock;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.Lists;
import com.google.api.services.cloudiot.v1.CloudIot;
import com.google.api.services.cloudiot.v1.CloudIotScopes;
import com.google.api.services.cloudiot.v1.model.SendCommandToDeviceRequest;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Date;

//import org.eclipse.paho.android.sample.activity.Connection;
import org.eclipse.paho.android.service.MqttAndroidClient;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

//import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class SendOpenCommand extends Thread {
        public static String responseString;
        private final String deviceId;
        private final String projectId;
        private final String cloudRegion;
        private final String registryName;
        private final String data;

        public SendOpenCommand(String deviceId, String projectId, String cloudRegion, String registryName, String data) {
                this.deviceId = deviceId;
                this.projectId = projectId;
                this.cloudRegion = cloudRegion;
                this.registryName = registryName;
                this.data = data;
        }



        public void sendOpenCommand() throws IOException {

                //System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "com.example.smartlock.vocal-gist-315804-d48cf3f9e13e.json");
                //Date expiryDate = new Date(10000000);
                //AccessToken accessToken = new AccessToken("AIzaSyCaSatwfe8kg0xzH2C0rDgJBBAnL5uyLOs", expiryDate);
                //GoogleCredentials credential = GoogleCredentials.create(accessToken);
//                GoogleCredentials credential = GoogleCredentials.getApplicationDefault().createScoped(CloudIotScopes.all());


                //FileInputStream credentialJSONFile = new FileInputStream("vocal-gist-315804-d48cf3f9e13e.json");

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

                Log.d(TAG, "sendOpenCommand: " + credential);

                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                HttpRequestInitializer init = new HttpCredentialsAdapter(credential);
                //HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
                final CloudIot service =
                        new CloudIot.Builder(new com.google.api.client.http.javanet.NetHttpTransport(), jsonFactory, init)
                                .setApplicationName("Smartlock")
                                .build();



                final String devicePath =
                        String.format(
                                "projects/%s/locations/%s/registries/%s/devices/%s",
                                this.projectId, this.cloudRegion, this.registryName, this.deviceId);

                SendCommandToDeviceRequest req = new SendCommandToDeviceRequest();

                // Data sent through the wire has to be base64 encoded.
                Base64.Encoder encoder = Base64.getEncoder();
                String encPayload = encoder.encodeToString(data.getBytes(StandardCharsets.UTF_8.name()));
                req.setBinaryData(encPayload);
                Log.d(TAG, "sendOpenCommand: Sending command to " + devicePath);

                service
                        .projects()
                        .locations()
                        .registries()
                        .devices()
                        .sendCommandToDevice(devicePath, req)
                        .execute();

                Log.d(TAG, "sendOpenCommand: sent");


        }

        @Override
        public void run() {
                try {
                        sendOpenCommand();
                } catch (IOException e) {
                        responseString = e.toString();
                        e.printStackTrace();
                }

        }

        /*                RequestQueue requestQueue = Volley.newRequestQueue(context);

                String projectId = "vocal-gist-315804-d48cf3f9e13e";
                String cloudRegion = "us-central1";
                String registryName = "registry2";
                String deviceId = "lock123";

                JSONObject jsonBody = new JSONObject();
                String jsonBodyString = jsonBody.toString();

                String APIKey = "AIzaSyCaSatwfe8kg0xzH2C0rDgJBBAnL5uyLOs";

                String devicePath = String.format("projects/%s/locations/%s/registries/%s/devices/%s:sendCommandToDevice", projectId, cloudRegion, registryName, deviceId);
                jsonBody.put("name", devicePath);
                jsonBody.put("binary_data", "Hello");

                String url = String.format("https://cloudiot.googleapis.com/v1/%s:sendCommandToDevice?key=%s", devicePath, APIKey);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,  new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                                Log.i("VOLLEY", response);
                                responseString = response;
                        }
                }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                                responseString = error.toString();
                                Log.e("VOLLEY", error.toString());


                        }
                }) {
                        @Override
                        public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                                try {
                                        responseString = jsonBodyString.toString();
                                        return jsonBodyString.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonBodyString, "utf-8");
                                        return null;
                                }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                String responseString = "";
                                if (response != null) {
                                        responseString = String.valueOf(response.statusCode);
                                        // can get more details such as response.headers
                                }
                                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                };

                requestQueue.add(stringRequest);*/
}
