package com.harrycha.mapreminder;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    TextView titleTextView, contentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About");

        String version = "";
        try {
            PackageInfo pInfo = getApplication().getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {
        }

        String title = "Welcome to\nMap Reminder!";
        titleTextView.setText(title);

        String content =
                "This app is designed to assist you to map out your notes.\n\n" +
                        "You have the option to have the app notify you whenever you enter and/or leave the area around your note.\n\n" +
                        "You can define the area around your note using a scroll bar.\n\n" +
                        "In addition, you can restrict the reminder to be notified on only certain days of the week.\n\n" +
                        "By automatically notifying you of nearby notes, the app helps you get reminded of whatever you wrote down.\n\n" +
                        "For any questions or suggestions, please don’t hesitate to reach out to the developer at mapreminderapp@gmail.com.\n\n" +
                        "App version: " + version;
        contentTextView.setText(content);
    }

    // on action bar's back button pressed
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
