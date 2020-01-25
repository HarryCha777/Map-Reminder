package com.harrycha.mapreminder;

import android.app.Application;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class App extends Application {

    // global variables
    public static boolean isReminderOn;
    public static boolean hasLocation;
    public static boolean finishedSetUp;
    public static double myLatitude;
    public static double myLongitude;
    public static boolean isEditing;

    public static GoogleMap mMap;
    public static Marker viewReminderMarker;

    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;

    public static RemindersHelper remindersHelper;

    // global methods
    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }
}
