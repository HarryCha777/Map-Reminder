package com.harrycha.mapreminder;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class PostReminderActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText titleEditText, descriptionEditText;
    long[] hasDayOfWeek;
    Button[] dayOfWeekButtons;
    CheckBox enterCheckBox, leaveCheckBox;
    long radius;
    TextView radiusTextView;
    SeekBar reminderAreaRadiusSeekBar;
    Button createButton, editButton, deleteButton;

    GoogleMap postReminderGoogleMap;
    double latitude, longitude;
    Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_reminder);

        SupportMapFragment postReminderMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.postReminderMapFragment);
        postReminderMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initViews(googleMap);

        postReminderGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13.0f));

        if (App.isEditing) {
            ArrayList<String> markerInfo = (ArrayList<String>) App.viewReminderMarker.getTag();
            long viewReminderCreatedDateTime = Long.parseLong(markerInfo.get(0));

            Cursor data = App.remindersHelper.get(viewReminderCreatedDateTime);
            data.moveToNext();
            String title = data.getString(4);
            String description = data.getString(5);
            hasDayOfWeek[0] = data.getLong(6);
            hasDayOfWeek[1] = data.getLong(7);
            hasDayOfWeek[2] = data.getLong(8);
            hasDayOfWeek[3] = data.getLong(9);
            hasDayOfWeek[4] = data.getLong(10);
            hasDayOfWeek[5] = data.getLong(11);
            hasDayOfWeek[6] = data.getLong(12);
            long hasEnter = data.getLong(13);
            long hasLeave = data.getLong(14);
            radius = data.getLong(15);

            titleEditText.setText(title);
            descriptionEditText.setText(description);

            for (int i = 0; i < 7; i++) {
                if (hasDayOfWeek[i] == 0) {
                    dayOfWeekButtons[i].setBackgroundResource(R.drawable.gray_round_button);
                    dayOfWeekButtons[i].setTextColor(Color.parseColor("#000000"));
                }
            }

            if (hasEnter == 0) enterCheckBox.setChecked(false);
            if (hasLeave == 0) leaveCheckBox.setChecked(false);

            reminderAreaRadiusSeekBar.setProgress(radiusToProgress(radius));
            radiusTextView.setText(radiusToText(radius));
            circle.setRadius(radius);

            createButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < 7; i++) {
            final int finalI = i;
            dayOfWeekButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasDayOfWeek[finalI] == 1) {
                        dayOfWeekButtons[finalI].setBackgroundResource(R.drawable.gray_round_button);
                        dayOfWeekButtons[finalI].setTextColor(Color.parseColor("#000000"));
                        hasDayOfWeek[finalI] = 0;
                    } else {
                        dayOfWeekButtons[finalI].setBackgroundResource(R.drawable.accent_round_button);
                        dayOfWeekButtons[finalI].setTextColor(Color.parseColor("#FFFFFF"));
                        hasDayOfWeek[finalI] = 1;
                    }
                }
            });
        }

        reminderAreaRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = progressToRadius(progress);
                radiusTextView.setText(radiusToText(radius));
                circle.setRadius(radius);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleEditText.getText().toString().equals("")) {
                    Toast.makeText(PostReminderActivity.this, "The title cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar cal = Calendar.getInstance();
                long createdDateTime = cal.getTimeInMillis();

                long enterCheckBoxChecked = 0;
                if (enterCheckBox.isChecked()) enterCheckBoxChecked = 1;
                long leaveCheckBoxChecked = 0;
                if (leaveCheckBox.isChecked()) leaveCheckBoxChecked = 1;

                App.remindersHelper.add(createdDateTime, latitude, longitude, titleEditText.getText().toString(), descriptionEditText.getText().toString(),
                        hasDayOfWeek[0], hasDayOfWeek[1], hasDayOfWeek[2], hasDayOfWeek[3], hasDayOfWeek[4], hasDayOfWeek[5], hasDayOfWeek[6],
                        enterCheckBoxChecked, leaveCheckBoxChecked, radius);
                new MapActivity().showAllReminders();

                Toast.makeText(getApplicationContext(), "Reminder created!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleEditText.getText().toString().equals("")) {
                    Toast.makeText(PostReminderActivity.this, "The title cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                long enterCheckBoxChecked = 0;
                if (enterCheckBox.isChecked()) enterCheckBoxChecked = 1;
                long leaveCheckBoxChecked = 0;
                if (leaveCheckBox.isChecked()) leaveCheckBoxChecked = 1;

                ArrayList<String> markerInfo = (ArrayList<String>) App.viewReminderMarker.getTag();
                long viewReminderCreatedDateTime = Long.parseLong(markerInfo.get(0));

                App.remindersHelper.edit(viewReminderCreatedDateTime, titleEditText.getText().toString(), descriptionEditText.getText().toString(),
                        hasDayOfWeek[0], hasDayOfWeek[1], hasDayOfWeek[2], hasDayOfWeek[3], hasDayOfWeek[4], hasDayOfWeek[5], hasDayOfWeek[6],
                        enterCheckBoxChecked, leaveCheckBoxChecked, radius);
                new MapActivity().showAllReminders();

                Toast.makeText(getApplicationContext(), "Reminder edited!", Toast.LENGTH_SHORT).show();
                App.isEditing = false;
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> markerInfo = (ArrayList<String>) App.viewReminderMarker.getTag();
                long viewReminderCreatedDateTime = Long.parseLong(markerInfo.get(0));

                App.remindersHelper.delete(viewReminderCreatedDateTime);
                new MapActivity().showAllReminders();

                Toast.makeText(getApplicationContext(), "Reminder deleted!", Toast.LENGTH_SHORT).show();
                App.isEditing = false;
                finish();
            }
        });
    }

    void initViews(GoogleMap googleMap) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (App.isEditing)
            getSupportActionBar().setTitle("Edit Reminder");
        else
            getSupportActionBar().setTitle("Create Reminder");

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        hasDayOfWeek = new long[7];
        for (int i = 0; i < 7; i++) hasDayOfWeek[i] = 1;
        dayOfWeekButtons = new Button[7];
        dayOfWeekButtons[0] = findViewById(R.id.sundayButton);
        dayOfWeekButtons[1] = findViewById(R.id.mondayButton);
        dayOfWeekButtons[2] = findViewById(R.id.tuesdayButton);
        dayOfWeekButtons[3] = findViewById(R.id.wednesdayButton);
        dayOfWeekButtons[4] = findViewById(R.id.thursdayButton);
        dayOfWeekButtons[5] = findViewById(R.id.fridayButton);
        dayOfWeekButtons[6] = findViewById(R.id.saturdayButton);
        enterCheckBox = findViewById(R.id.enterCheckBox);
        leaveCheckBox = findViewById(R.id.leaveCheckBox);
        reminderAreaRadiusSeekBar = findViewById(R.id.reminderAreaRadiusSeekBar);
        radius = progressToRadius(reminderAreaRadiusSeekBar.getProgress());
        radiusTextView = findViewById(R.id.radiusTextView);
        radiusTextView.setText(radiusToText(radius));
        createButton = findViewById(R.id.createButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);

        postReminderGoogleMap = googleMap;
        postReminderGoogleMap.setMyLocationEnabled(true);
        postReminderGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng point = App.viewReminderMarker.getPosition();
        latitude = point.latitude;
        longitude = point.longitude;

        postReminderGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        circle = postReminderGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(radius)
                .strokeColor(Color.BLACK)
                .fillColor(0x30ff0000)
                .strokeWidth(2));
    }

    int radiusToProgress(long radius) {
        return (int) radius / 100;
    }

    long progressToRadius(int progress) {
        return (progress + 1) * 100;
    }

    String radiusToText(long radius) {
        if (radius >= 1000) return radius / 1000.0 + " km";
        return radius + " m";
    }

    // on action bar's back button pressed
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        App.isEditing = false;
        finish();
    }
}
