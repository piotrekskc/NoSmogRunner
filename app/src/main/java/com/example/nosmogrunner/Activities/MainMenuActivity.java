package com.example.nosmogrunner.Activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.nosmogrunner.AirlyCityGet;
import com.example.nosmogrunner.R;
import com.example.nosmogrunner.models.CitySmogParameters;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainMenuActivity extends WelcomeActivity {

    private static final String TAG = "MainMenuActivity";

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Boolean mLocationPermissionGranted = false;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    public myFirstKnownLocation myLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.main_menu);
        myLocation = new myFirstKnownLocation(52.224485,21.0933817);

        ImageButton runB = (ImageButton) findViewById(R.id.runButton);
        ImageButton chartB = (ImageButton) findViewById(R.id.statsButton);
        ImageButton smogB = (ImageButton) findViewById(R.id.smogButton);
        CitySmogParameters myCityParams = new CitySmogParameters();
        myCityParams.cityCenterLat = 52.22993199999999;
        myCityParams.cityCenterLng = 21.05650100000002;
        myCityParams.cityRadius = 40;
        AirlyCityGet cityGetter = new AirlyCityGet();
        cityGetter.execute(myCityParams);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        smogB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainMenuActivity.this, "Smog Pollution Charts", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainMenuActivity.this, SmogActivity.class));

            }
        });

        chartB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainMenuActivity.this, "Running Stats", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainMenuActivity.this, StatsActivity.class));

            }
        });


        runB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainMenuActivity.this, "Run Nav",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainMenuActivity.this, RunActivity.class);
                i.putExtra("citySmogParameter", myCityParams);
                startActivity(i);

            }
        });


        ImageButton mapB = (ImageButton) findViewById(R.id.mapButton);
        mapB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainMenuActivity.this, "Maps", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainMenuActivity.this, MapActivity.class));

            }
        });

    }

    public void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d(TAG,"Longitude: "+location.getLongitude()+" Latitude: "+ location.getLatitude());
                            myLocation.Latitude = location.getLatitude();
                            myLocation.Longitude= location.getLongitude();

                        }
                    }
                });

    }






    @Override
    protected void onResume(){
    super.onResume();

    if(checkMapServices()){
        if(mLocationPermissionGranted){
            getLastKnownLocation();
            //getChatrooms();
            getLastKnownLocation();
        }
        else{
            getLocationPermission();
        }
    }


    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //getChatrooms();
            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainMenuActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainMenuActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    //getChatrooms();
                    getLastKnownLocation();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }



    public class myFirstKnownLocation{
        double Latitude;
        double Longitude;

        public myFirstKnownLocation(double latitude, double longitude) {
            Latitude = latitude;
            Longitude = longitude;
        }
    }



}












