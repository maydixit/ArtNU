package com.example.artnu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ai.fritz.core.Fritz;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
        // Initialize Fritz
        Fritz.configure(this, "5adfcdfd09604cf58e8a92e709d5c0d8");

        FloatingActionButton transformButton = findViewById(R.id.transform_button);
        transformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), StyleTransferLiveActivity.class));
            }
        });
    }
}