package com.example.nosmogrunner;

import android.os.Bundle;

public class MapActivity extends MainMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        getSupportActionBar().hide();
    }
}
