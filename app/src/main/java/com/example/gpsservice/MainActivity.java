package com.example.gpsservice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
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
    private Button stopServiceButton;
    private Button updateButton;
    private TextView tvLong, tvLat, tvDist, tvSpeed;
    int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    int MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 1;

    @SuppressLint("MissingInflatedId")
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
        stopServiceButton = findViewById(R.id.StopServiceButton);
        updateButton = findViewById(R.id.UpdateButton);
        tvLat = findViewById(R.id.latitude);
        tvLong = findViewById(R.id.longitude);
        tvDist = findViewById(R.id.distance);
        tvSpeed = findViewById(R.id.averageSpeed);

        Intent i = new Intent(this, GPSService.class);

        startServiceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startService(i);
                bindService(i, MainActivity.this, BIND_AUTO_CREATE);
            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                unbindService(MainActivity.this);
                gpsServiceProxy = null;
                //stopService(i);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (gpsServiceProxy != null) {
                    try {
                        double longitude = gpsServiceProxy.getLongitude();
                        double latitude = gpsServiceProxy.getLatitude();
                        double distance = gpsServiceProxy.getDistance();
                        double avgSpeed = gpsServiceProxy.getAverageSpeed();

                        String longS = "Longitude: " + longitude;
                        String latS = "Latitude: " + latitude;
                        String distS = "Distance: " + distance + " m";
                        String speedS = "Average Speed: " + avgSpeed + " m/s";

                        tvLong.setText(longS);
                        tvLat.setText(latS);
                        tvDist.setText(distS);
                        tvSpeed.setText(speedS);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Could not receive data from service");
                    }
                } else {
                    Log.i(TAG, "No proxy");
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "Starting app");
        if (!isGPSEnabled(this)) {
            enableLocationSettings();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            requestLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            requestLocationPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(TAG, "Stopping app");
        //unbindService(MainActivity.this);
        //gpsServiceProxy = null;
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

    public boolean isGPSEnabled (Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION);
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