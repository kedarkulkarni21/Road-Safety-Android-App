package com.kd.sensor.potholedetector;

import android.Manifest.permission;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;

    private PotholeTracker mPotholeTracker;
    private LocationTracker mLocationTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isLocationPermissionGranted = LocationTracker.checkLocationPermission(this);
        if (!isLocationPermissionGranted) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        }

        mLocationTracker = LocationTracker.getInstance(this);
        mLocationTracker.connect();

        mPotholeTracker = PotholeTracker.getInstance();

        Button startTracking = (Button) findViewById(R.id.start_tracking);
        startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPotholeTracker.setSourceLocation(mLocationTracker.getCurrentLocation());
                Toast.makeText(MainActivity.this, "Tracking for potholes...", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(MainActivity.this, TrackerService.class);
                startService(serviceIntent);
            }
        });

        Button stopTracking = (Button) findViewById(R.id.stop_tracking);
        stopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Loading pothole locations...", Toast.LENGTH_SHORT).show();
                mPotholeTracker.setDestinationLocation(mLocationTracker.getCurrentLocation());
                stopService(new Intent(MainActivity.this, TrackerService.class));

                Intent mapIntent = new Intent(MainActivity.this, MapPotholesActivity.class);
                startActivity(mapIntent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mLocationTracker.disconnect();
        super.onDestroy();
    }
}
