package com.example.gpsservice;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

public class GPSService extends Service implements LocationListener {
    private final String TAG = GPSService.class.getCanonicalName();

    private LocationManager locationManager;
    long minTime = 1000;
    int minDistance = 10;
    double longitude, latitude;

    private GPSServiceImpl impl;

    @Override
    public void onLocationChanged(@NonNull Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        Log.i("onLocationChanged", "longitude: " + Double.toString(longitude));
        Log.i("onLocationChanged", "latitude: " + Double.toString(latitude));

        exportLocationToFile(longitude, latitude);

        //tvLong.setText(Double.toString(longitude));
        //tvLat.setText(Double.toString(latitude));
    }

    private class GPSServiceImpl extends IGPSService.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
        @Override
        public double getLatitude(){
            return 1.0;
        }
        @Override
        public double getLongitude(){
            return 1.0;
        }
        @Override
        public double getDistance(){
            return 1.0;
        }
        @Override
        public double getAverageSpeed(){
            return 1.0;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("onBind","binding service");
        return (IBinder) impl;
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    @Override
    public void onCreate() {
        Log.i("onCreate", "creating service");
        super.onCreate();
        impl = new GPSServiceImpl();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
    }

    @Override
    public void onDestroy() {
        Log.i("onDestroy", "destroying service");
        super.onDestroy();

        locationManager.removeUpdates(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("onStartCommand","starting service ");
        return super.onStartCommand(intent, flags, startId);
    }

    private void exportLocationToFile(double longitude, double latitude) {
        try {
            String filename = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/location_history.txt";
            String entry = "longitude: " + longitude + ", latitude: " + latitude;

            // construct file metadata
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            // request to write the file to the media store
            ContentResolver resolver = this.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri == null) {
                Log.i(TAG, "Failed to obtain an uri to write to");
                return;
            }

            OutputStream outputStream = resolver.openOutputStream(uri, "wa");

            if (outputStream == null) {
                Log.i(TAG, "Failed to obtain an output stream to write to");
                return;
            }

            Log.i(TAG, "Writing history to media store");

            PrintWriter out = new PrintWriter(outputStream);
            out.println(entry);
            out.close();

            //FileOutputStream outputStream = new FileOutputStream(filename, true);
            //byte[] strToBytes = entry.getBytes();
            //outputStream.write(strToBytes);

            Log.i(TAG, "History is written to media store");
        } catch (IOException e) {
            Log.i(TAG, "Failed to export history:\n" + e);
        }
    }
}
