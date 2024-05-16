// IGPSService.aidl
package com.example.gpsservice;

// Declare any non-default types here with import statements

interface IGPSService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    double getLatitude();
    double getLongitude();
    double getDistance();
    double getAverageSpeed();
}