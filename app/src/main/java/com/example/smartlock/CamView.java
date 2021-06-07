package com.example.smartlock;

import androidx.appcompat.app.AppCompatActivity;

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

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class CamView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_view);


        Button btn = findViewById(R.id.openButton);
        btn.setOnClickListener(v -> {
            Toast.makeText(this, "Button pressed", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onCreate: Button was pressed.");
            SendOpenCommand sendCommand = new SendOpenCommand("lock123", "vocal-gist-315804", "us-central1", "registry-2", "OpenDoor");
            sendCommand.start();
            TextView textView = findViewById(R.id.textView);
            textView.setText(SendOpenCommand.responseString);

        });
    }
}