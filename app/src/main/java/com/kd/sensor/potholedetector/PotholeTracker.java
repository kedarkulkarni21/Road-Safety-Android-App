package com.kd.sensor.potholedetector;

import android.location.Location;
import android.support.annotation.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

final class PotholeTracker {

    private static PotholeTracker mPotholeTracker;

    static PotholeTracker getInstance() {
        if (mPotholeTracker == null) {
            mPotholeTracker = new PotholeTracker();
        }
        return mPotholeTracker;
    }

    private TravelPath mCurrentPath = new TravelPath();
    private Set<Pothole> mCurrentPotholes = new LinkedHashSet<>();

    void setSourceLocation(Location location) {
        mCurrentPath.mSourceLocation = location;
        mCurrentPotholes.clear();
    }

    void setDestinationLocation(Location location) {
        mCurrentPath.mDestinationLocation = location;
    }

    void addPothole(@Nullable Location location) {
        if (location != null) {
            mCurrentPotholes.add(new Pothole(location));
        }
    }

    TravelPath getCurrentPath() {
        return mCurrentPath;
    }

    Set<Pothole> getCurrentPotholes() {
        return mCurrentPotholes;
    }

    private PotholeTracker() {}

    static class TravelPath {
        private Location mSourceLocation;
        private Location mDestinationLocation;

        Location getSourceLocation() {
            return mSourceLocation;
        }

        Location getDestinationLocation() {
            return mDestinationLocation;
        }
    }

    static class Pothole {

        private final Location mPotholeLocation;

        Pothole(Location location) {
            mPotholeLocation = location;
        }

        Location getPotholeLocation() {
            return mPotholeLocation;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof Pothole)) {
                return false;
            }

            Pothole p = (Pothole) o;
            return Double.compare(mPotholeLocation.getLatitude(), p.mPotholeLocation.getLatitude()) == 0
                    && Double.compare(mPotholeLocation.getLongitude(), p.mPotholeLocation.getLongitude()) == 0;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + Double.valueOf(mPotholeLocation.getLatitude()).hashCode();
            result = 31 * result + Double.valueOf(mPotholeLocation.getLongitude()).hashCode();
            return result;
        }
    }
}
