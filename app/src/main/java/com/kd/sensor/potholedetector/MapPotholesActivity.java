package com.kd.sensor.potholedetector;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Set;

public class MapPotholesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String mMapShareUrl = "https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=800x800";

    private PotholeTracker mPotholeTracker;
    private StringBuilder mPotHolesData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_potholes);

        mPotholeTracker = PotholeTracker.getInstance();
        mPotHolesData = new StringBuilder("Potholes in the path:").append(mPotholeTracker.getCurrentPotholes().size()).append("\n");

        stopService(new Intent(this, TrackerService.class));

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.potholes_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        PotholeTracker.TravelPath path = mPotholeTracker.getCurrentPath();

        Location start = path.getSourceLocation();
        mPotHolesData.append(mMapShareUrl);
        if (start != null) {
            LatLng startLatLng = new LatLng(start.getLatitude(), start.getLongitude());
            map.addMarker(new MarkerOptions().position(startLatLng).title("Start"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 13));
            mPotHolesData.append("&markers=label:S|").append(startLatLng.latitude).append(",").append(startLatLng.longitude);
        }

        mPotHolesData.append("&markers=");
        Set<PotholeTracker.Pothole> potholes = mPotholeTracker.getCurrentPotholes();
        boolean isFirstElement = true;
        for (PotholeTracker.Pothole pothole : potholes) {
            Location location = pothole.getPotholeLocation();
            LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions().position(locationLatLng).title("Pothole"));
            if (!isFirstElement) {
                mPotHolesData.append("|");
            }
            mPotHolesData.append(locationLatLng.latitude).append(",").append(locationLatLng.longitude);
            isFirstElement = false;
        }

        Location end = path.getDestinationLocation();
        if (end != null) {
            LatLng endLatLng = new LatLng(end.getLatitude(), end.getLongitude());
            map.addMarker(new MarkerOptions().position(endLatLng).title("End"));
            mPotHolesData.append("&markers=label:E|").append(endLatLng.latitude).append(",").append(endLatLng.longitude);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.send_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                sendPotholesData();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendPotholesData() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mPotHolesData.toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
