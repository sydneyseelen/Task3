package com.example.gpsservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    final private String TAG = MainActivity.class.getCanonicalName();
    private IGPSService gpsServiceProxy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent i = new Intent (this, GPSService.class);
        Button startServiceButton = findViewById(R.id.StartServiceButton);
        startServiceButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (gpsServiceProxy!=null){
                    Log.i("onClick", "starting service");
                    startService(i);
                }else{
                    Log.i("onClick", "no proxy ");
                }
            }
        });

    }
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.i(TAG, "Service connected");
        gpsServiceProxy = IGPSService.Stub.asInterface(iBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.i(TAG, "Service disconnected");
        gpsServiceProxy = null;
    }

}