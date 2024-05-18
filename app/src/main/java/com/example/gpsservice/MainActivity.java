package com.example.gpsservice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private final static String TAG = MainActivity.class.getCanonicalName();
    private IGPSService gpsServiceProxy = null;

    private Button startServiceButton;
    private TextView tvLong, tvLat;
    int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

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

        startServiceButton = findViewById(R.id.StartServiceButton);
        tvLat = findViewById(R.id.latitude);
        tvLong = findViewById(R.id.longitude);

        Intent i = new Intent (this, GPSService.class);

        startServiceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startService(i);

                if (gpsServiceProxy!=null) {
                    Log.i("onClick", "starting service");
                    startService(i);
                } else {
                    Log.i("onClick", "no proxy ");
                }
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();

        if (!isGPSEnabled(this)) {
            enableLocationSettings();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            requestLocationPermission();
        }
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

    public boolean isGPSEnabled (Context mContext){
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
    }

    private void enableLocationSettings() {
        new AlertDialog.Builder(this)
                .setTitle("enable GPS")
                .setMessage("GPS currently disabled. Do you want me to enable GPS?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(settingsIntent);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}