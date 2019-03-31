package com.example.nosmogrunner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity
{
    LinearLayout l2;
        Button button;
        Animation uptodown,downtoup;
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide();
            setContentView(R.layout.welcome_screen);
            button = (Button)findViewById(R.id.button2);



            //Creates spinner with city to choose
            Spinner spinner = (Spinner) findViewById(R.id.citySpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.cities_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            spinner.setAdapter(adapter);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(WelcomeActivity.this, "100% Activity 0% Smog!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WelcomeActivity.this, MainMenuActivity.class));

                }
            });


        }


}
