package com.example.nosmogrunner.Activities;

import android.os.Bundle;

import com.example.nosmogrunner.R;

public class StatsActivity extends MainMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);
        getSupportActionBar().hide();
    }
}
