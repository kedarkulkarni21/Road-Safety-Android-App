package com.kd.sensor.potholedetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

final class PotholeDetector implements SensorEventListener {

    private static final String TAG = "PotholeDetector";

    private static final float GFORCE_THRESHOLD = 3.25F;
    private static final long SLOP_TIME_MS = 500;

    private final Context mContext;
    private final LocationTracker mLocationTracker;
    private final PotholeTracker mPotholeTracker;

    private long mDetectionTimestamp;

    PotholeDetector(Context context) {
        mContext = context;
        mLocationTracker = LocationTracker.getInstance(context);
        mPotholeTracker = PotholeTracker.getInstance();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > GFORCE_THRESHOLD) {
            final long now = System.currentTimeMillis();
            // ignore events too close to each other (500ms)
            if (mDetectionTimestamp + SLOP_TIME_MS > now) {
                return;
            }
            mDetectionTimestamp = now;

            Location location = mLocationTracker.getCurrentLocation();
            mPotholeTracker.addPothole(location);
            Toast.makeText(
                    mContext,
                    (location != null) ? "Pothole located!!" : "Location not found!!",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Pothole located: " + location);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing.
    }
}
