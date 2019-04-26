package com.example.nosmogrunner;

import android.os.Bundle;

import java.util.ArrayList;

public class StatsActivity extends MainMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);
        getSupportActionBar().hide();
    }

    @Override
    public void onTaskDone(ArrayList... values) {

    }
}
