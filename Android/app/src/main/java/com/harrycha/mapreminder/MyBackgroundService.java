package com.harrycha.mapreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;

public class MyBackgroundService extends Service implements LocationListener {
    LocationManager locationManager;

    static final String CHANNEL_ID = "appChannel";
    static Notification notification;
    boolean foregroundNotificationTurnedOn;

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (App.isReminderOn) turnOnForegroundNotification();
    }

    void turnOnForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Ready to notify your reminders.")
                .setContentText("This option can be disabled in the settings.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Runnable getLocationAndNotifyNearbySalesRunnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runGetLocationHandler();

                    if (App.isReminderOn && !foregroundNotificationTurnedOn) {
                        turnOnForegroundNotification();
                        foregroundNotificationTurnedOn = true;
                    }
                    if (!App.isReminderOn && foregroundNotificationTurnedOn) {
                        stopForeground(true);
                        foregroundNotificationTurnedOn = false;
                    }
                    if (App.isReminderOn) handleReminders();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        Thread getLocationServiceThread = new Thread(getLocationAndNotifyNearbySalesRunnable);
        getLocationServiceThread.start();

        return Service.START_STICKY;
    }

    void handleReminders() {
        Cursor data = App.remindersHelper.getAll();
        while (data.moveToNext()) {
            long createdDateTime = data.getLong(1);
            double latitude = data.getDouble(2);
            double longitude = data.getDouble(3);
            String title = data.getString(4);
            String description = data.getString(5);
            long[] hasDayOfWeek = new long[7];
            hasDayOfWeek[0] = data.getLong(6);
            hasDayOfWeek[1] = data.getLong(7);
            hasDayOfWeek[2] = data.getLong(8);
            hasDayOfWeek[3] = data.getLong(9);
            hasDayOfWeek[4] = data.getLong(10);
            hasDayOfWeek[5] = data.getLong(11);
            hasDayOfWeek[6] = data.getLong(12);
            long hasEnter = data.getLong(13);
            long hasLeave = data.getLong(14);
            long radius = data.getLong(15);
            long isInRadius = data.getLong(16);

            Calendar cal = Calendar.getInstance();
            int currDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

            if (hasDayOfWeek[currDayOfWeek] == 1) {
                if (hasEnter == 1 && isInRadius == 0 && getDistance(App.myLatitude, latitude, App.myLongitude, longitude) < radius)
                    showNotification(title, description);

                if (hasLeave == 1 && isInRadius == 1 && getDistance(App.myLatitude, latitude, App.myLongitude, longitude) > radius)
                    showNotification(title, description);
            }

            // update locations
            if (getDistance(App.myLatitude, latitude, App.myLongitude, longitude) < radius)
                App.remindersHelper.updateLocation(createdDateTime, 1);
            else
                App.remindersHelper.updateLocation(createdDateTime, 0);
        }
    }

    double getDistance(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }


    private void showNotification(String title, String content) {
        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "channelID");
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel("channelID", "ChannelName", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        int uniqueNumber = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE); // unique number allows multiple notifications
        mNotificationManager.notify(uniqueNumber, mBuilder.build());
    }

    Handler getLocationHandler = new Handler();

    public void runGetLocationHandler() {
        Runnable doDisplayError = new Runnable() {
            public void run() {
                getLocation();
            }
        };
        getLocationHandler.post(doDisplayError);
    }

    void getLocation() {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        App.hasLocation = true;
        App.myLatitude = location.getLatitude();
        App.myLongitude = location.getLongitude();

        ((App) getApplication()).editor.putFloat("Last Latitude", (float) App.myLatitude);
        ((App) getApplication()).editor.putFloat("Last Longitude", (float) App.myLongitude);
        ((App) getApplication()).editor.apply();
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (App.finishedSetUp) {
            App.finishedSetUp = false;
            startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
