package com.example.gpsservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

public class GPSService extends Service {
    private GPSServiceImpl impl;

    private class GPSServiceImpl extends IGPSService.Stub{

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

    @Override
    public void onCreate(){
        Log.i("onCreate", "creating service");
        super.onCreate();
        impl= new GPSServiceImpl();
    }

    @Override
    public void onDestroy(){
        Log.i("onDestroy", "destroying service");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i("onStartCommand","starting service ");
        return super.onStartCommand(intent, flags, startId);
    }
}
