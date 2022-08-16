package com.example.triptracker;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TrackedSession {

        private static final String TAG = "TrackedSession";

        private String title = "";
        private String description = "";
        private float distance = 0;
        private float averageSpeed = 0;
        private float averageSpeedFromSUVAT = 0;
        private long timeElapsedBetweenStartStops = 0;
        private float timeElapsedBetweenLocations = 0;
        private float elevation = 0;
        private Date timeCreated;
        private Date timeStarted;
        private Date timeStopped;
        private ArrayList<Location> locations = new ArrayList<>();
        private boolean paused = false;

        public TrackedSession() {
        }

        public void addLocation(Location location) {
            if (!paused) {
                if (locations.isEmpty()) {
                    averageSpeed = location.getSpeed();
                } else {
                    Location lastLocation = locations.get(locations.size() - 1);
                    distance += location.distanceTo(lastLocation);
                    Log.d(TAG, "addLocation Distance: " + location.distanceTo(lastLocation));
                    elevation += Math.abs(lastLocation.getAltitude() - location.getAltitude());
                    timeElapsedBetweenLocations += Math.abs(lastLocation.getTime() - location.getTime());
                    averageSpeedFromSUVAT = ((distance / (float) 1000) / (getCurrentTimeMillis() / (float) 3600000));
                }

            } else {
                paused = false;
                Log.d(TAG, "addTLocation: Unpaused");
            }
            locations.add(location);
        }

        public ArrayList<Location> getLocations() {
            return locations;
        }

        public boolean isCreated() {
            return !(timeCreated == null);
        }

        public void startTrackingActivity() {
            timeCreated = new Date(System.currentTimeMillis());
            timeStarted = new Date(System.currentTimeMillis());
        }

        public void resumeTrackingActivity() {
            if (timeCreated == null) {
                Log.d(TAG, "resumeTrackingActivity: No tracking activity to resume: " +
                        "must call startTrackingActivity first.");
            } else {
                timeStarted = new Date(System.currentTimeMillis());
            }
        }

        public void stopTrackingActivity() {
            timeStopped = new Date(System.currentTimeMillis());
            timeElapsedBetweenStartStops += timeStopped.getTime() - timeStarted.getTime();
            averageSpeedFromSUVAT = distance / (timeElapsedBetweenStartStops / 1000);
            paused = true;
        }

        public String getLocationsAsString() {
            String locationsString = "";

            int id = 0;
            for (Location location : locations) {
                locationsString = locationsString.concat("Location id=" + (id++) + location.toString() + "\n");
            }

            return locationsString;
        }

        public long getCurrentTimeMillis() {
            if (paused) return timeElapsedBetweenStartStops;
            else return timeElapsedBetweenStartStops + (System.currentTimeMillis() - timeStarted.getTime());
        }

        boolean isPaused() {
            return this.paused;
        }

        void setTitle(String title) {
            this.title = title;
        }

        String getTitle() {
            return title;
        }

        void setDescription(String description) {
            this.description = description;
        }

        String getDescription() {
            return description;
        }

        String getTimeString() {
            long timeInMilliseconds = getCurrentTimeMillis();
            return timeToString(timeInMilliseconds);
        }

        static String timeToString(long timeInMilliseconds) {
            long timeSwapBuff = 0L;
            long updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs %= 60;
            int hrs = mins / 60;
            mins %= 60;
            int milliseconds = (int) timeInMilliseconds % 1000;
            int centisecs = milliseconds / 10;

            if (hrs == 0) {
                if (mins == 0) {
                    return String.format(Locale.UK, "%d.%02ds", secs, centisecs);
                } else {
                    return String.format(Locale.UK, "%dm %02ds", mins, secs, centisecs);
                }
            }
            return String.format(Locale.UK, "%dh %02dm", hrs, mins);

        }

        public float getDistance() {
            return distance;
        }

        String getDistanceString() {
            return distanceToString(distance, true);
        }

        static String distanceToString(float distance, boolean labelled) {
            int metres = Math.round(distance);
            int km = metres / 1000;
            metres = metres % 1000;
            String format = "%d";

            if (km == 0) {
                if (labelled) {
                    format += "m";
                }
                return String.format(Locale.UK, format, metres);
            }

            metres = metres/10;
            format = "%d.%02d";
            if (labelled) {
                format += "km";
            }
            return String.format(Locale.UK, format, km, metres);
        }

        public float getAverageSpeed() {
            return averageSpeedFromSUVAT;
        }

        String getAverageSpeedString() {
            return String.format(Locale.UK, "%.2f", averageSpeed);
        }

        public float getElevation() {
            return elevation;
        }

        String getElevationString() {
            return distanceToString(elevation, true);
        }

        Date getTimeCreated() {
            return timeCreated;
        }

        @NonNull
        public String toString() {
            String distanceStr = "Distance: " + distance + "m";
            String averageSpeedString = "Average Speed (Measured): " + averageSpeed + "m/s";
            String averageSpeedFromSUVATString = "Average Speed (Calculated): " + averageSpeedFromSUVAT + "m/s";
            String elevationString = "Elevation: " + elevation + "m";
            String durationString = "Duration Between Track Points: " + timeElapsedBetweenLocations / 1000 + "s";
            String timeCreatedString = "Start Time: " + timeCreated.toString();
            String timeStoppedString = "Stop Time: " + timeStopped.toString();
            String durationElapsedString = "Duration Between Start and Stop: " + timeElapsedBetweenStartStops / 1000 + "s";
            String locationsSizeString = "Track Points Stored: " + locations.size();
            String locationsString = getLocationsAsString();

            return distanceStr + "\n" +
                    averageSpeedString + "\n" +
                    averageSpeedFromSUVATString + "\n" +
                    elevationString + "\n" +
                    durationString + "\n" +
                    timeCreatedString + "\n" +
                    timeStoppedString + "\n" +
                    durationElapsedString + "\n" +
                    locationsSizeString + "\n\n" +
                    "TRACK POINTS: \n" +
                    locationsString;
        }
}
