package com.rakesh.geoalarm;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Objects;

import static com.rakesh.geoalarm.MainActivity.ACCURACY_LEVEL_MEDIUM;
import static com.rakesh.geoalarm.MainActivity.TAG;
import static com.rakesh.geoalarm.MainActivity.roundOff;

public class LocationService extends Service {
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6378.137;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    int accuracy;
    Bundle bundle;
    boolean ifReached = false;
    Notification notification;
//    NotificationManager notificationManager;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener: " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            checkIfReached(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bundle = intent.getExtras();
        if (bundle != null) {
            accuracy = bundle.getInt("accuracy", ACCURACY_LEVEL_MEDIUM);
        }
        Log.e(TAG, "onStartCommand");

        if (Objects.equals(intent.getAction(), "start")) {

            Intent notificationIntent = new Intent(this, MainActivity.class);
            intent.setAction("start");

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Intent i = new Intent(this, MainActivity.class);
            intent.setAction("cancel");
            PendingIntent cancelIntent = PendingIntent.getService(this, 0,
                    i, 0);

//            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            notification = new NotificationCompat.Builder(this, "")
                    .setContentTitle("GeoAlarm")
                    .setContentText("Upcoming Alarm")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_alarm), 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .addAction(R.drawable.ic_bubble, "Cancel", cancelIntent)
                    .build();
            startForeground(101, notification);
        } else if (Objects.equals(intent.getAction(), "cancel")) {
            Log.i(TAG, "Pressed cancel");
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception e) {
                    Log.e(TAG, "fail to remove location listeners, ignore", e);
                }
            }
        }
        Intent j = new Intent(this, MainActivity.class);
        j.putExtra("ifReached", ifReached);
        j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(j);
        super.onDestroy();
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void checkIfReached(Location location) {
        Log.e(TAG, "checking...: " + location
                + "\n" + "setLat ::" + bundle.getDouble("lat")
                + "\n" + "setLat ::" + bundle.getDouble("lng"));

        Double lat = roundOff(bundle.getDouble("lat"), accuracy);
        Double lng = roundOff(bundle.getDouble("lng"), accuracy);
        Double currentLat = roundOff(location.getLatitude(), accuracy);
        Double currentLng = roundOff(location.getLongitude(), accuracy);
        if (Objects.equals(currentLat, lat) && Objects.equals(currentLng, lng)) {
            ifReached = true;
            stopSelf();
        } else {
//            StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
//            for (StatusBarNotification notification : notifications) {
//                if (notification.getId() == 101
//                        && ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)) != null) {
            double distance = calculateDistance(currentLat, currentLng, lat, lng);
            Log.i(TAG, "Distance : " + distance);

//                    //TODO Update Notification
//                }
//            }
        }
    }

    public double calculateDistance(double userLat, double userLng,
                                    double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));

    }

}
