package com.example.nosmogrunner;

import android.os.Bundle;


public class RunActivity extends MainMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run);
        getSupportActionBar().hide();

    }
}
