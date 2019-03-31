package com.example.nosmogrunner;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainMenuActivity extends WelcomeActivity
{
    ConstraintLayout mainMenu,runView,statsView,SmogView,MapView;
    Animation uptodown;


@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.main_menu);



        ImageButton mapB = (ImageButton)findViewById(R.id.mapButton);
        ImageButton runB = (ImageButton)findViewById(R.id.runButton);
        ImageButton chartB = (ImageButton)findViewById(R.id.statsButton);
        ImageButton smogB = (ImageButton)findViewById(R.id.smogButton);


        smogB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainMenuActivity.this, "Smog Pollution Charts",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainMenuActivity.this, SmogActivity.class));

            }
        });

        chartB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainMenuActivity.this, "Running Stats",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainMenuActivity.this, StatsActivity.class));

            }
        });

        runB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainMenuActivity.this, "Run Nav",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainMenuActivity.this, RunActivity.class));

            }
        });

        mapB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainMenuActivity.this, "Maps",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainMenuActivity.this, MapActivity.class));

            }
        });


    }





}

