package com.penkov.screendreamer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.settings_title);

        ((Button) findViewById(R.id.button)).setOnClickListener(v -> {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent = new Intent(Settings.ACTION_DREAM_SETTINGS);
            } else {
                intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
            }
            startActivity(intent);
        });
    }
}