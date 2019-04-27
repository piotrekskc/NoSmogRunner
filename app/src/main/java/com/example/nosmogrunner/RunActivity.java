package com.example.nosmogrunner;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.nosmogrunner.models.UserLocation;
import com.example.nosmogrunner.services.LocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

import static com.example.nosmogrunner.services.LocationService.getUserLocationObject;
import static com.example.nosmogrunner.utils.Constants.MAPVIEW_BUNDLE_KEY;


public class RunActivity extends MainMenuActivity implements OnMapReadyCallback {

    private static final String TAG = "RunActivity";


    //widgets
    private MapView mMapView;
    private FusedLocationProviderClient mFusedLocationClient;
    public GoogleMap drawedMap;
    Button startButton;
    Button endButton;
    LatLng startRunPosition;
    LatLng endRunPosition;

    //vars

    private LatLng mMapPosition;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int LOCATION_UPDATE_INTERVAL = 3000;
    private UiSettings mUiSettings;
    private GeoApiContext mGeoApiContext;

    public RelativeLayout walkOrRunLayout;
    public MarkerOptions defaultMarkerOptions;
    public MarkerOptions markerStartOptions;
    public MarkerOptions markerFinishOptions;
    private static final float DEFAULT_ZOOM = 15f;
    public LatLng currentClickPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run);
        getSupportActionBar().hide();

        mMapView = findViewById(R.id.user_map);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        walkOrRunLayout = findViewById(R.id.walkOrRunLayout);


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    private void setCameraView(LatLng position, GoogleMap map, float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        double longitude = myLocation.Longitude;
        double latitude = myLocation.Latitude;

        mMapPosition = new LatLng(latitude, longitude);
        startLocationService();
        setCameraView(mMapPosition, map, DEFAULT_ZOOM);
        drawedMap = map;


        mUiSettings  = map.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        walkOrRunLayout.setVisibility(View.VISIBLE);
        Button goButton = (Button) findViewById(R.id.GoButton);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng point) {
                Log.d("DEBUG","Map clicked [" + point.latitude + " / " + point.longitude + "]");




                 currentClickPosition = new LatLng(point.latitude,point.longitude);
                // Creating a marker

                defaultMarkerOptions = new MarkerOptions();

                // Setting the position for the marker
                defaultMarkerOptions.position(currentClickPosition);
                // Clears the previously touched position


                // Animating to the touched position
                drawedMap.animateCamera(CameraUpdateFactory.newLatLng(currentClickPosition));
                drawedMap.addMarker(defaultMarkerOptions);

                // Placing a marker on the touched position


                startButton = findViewById(R.id.startPoint);
                endButton= findViewById(R.id.endPoint);


                startButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawedMap.clear();


                        markerStartOptions = new MarkerOptions();

                        // Setting the title for the marker.
                        // This will be displayed on taping the marker
                        markerStartOptions.title("Start Point");
                       // markerOptions.setzIndex(0);
                        markerStartOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        startRunPosition = new LatLng(point.latitude,point.longitude);

                        Log.d("DEBUG","Start point position!!! [" + startRunPosition.latitude + " / " + startRunPosition.longitude + "]");

                        markerStartOptions.position(startRunPosition);
                        drawedMap.addMarker(markerStartOptions);
                        Toast.makeText(RunActivity.this,"You have set the start point!",Toast.LENGTH_SHORT).show();

                        if( endRunPosition != null){
                            markerFinishOptions = new MarkerOptions();
                            markerFinishOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            markerFinishOptions.position(endRunPosition);
                            markerFinishOptions.title("End Point");
                            drawedMap.addMarker(markerFinishOptions);
                        }
                    }
                });

                endButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawedMap.clear();
                        endRunPosition = new LatLng(point.latitude, point.longitude);
                        markerFinishOptions = new MarkerOptions();
                        Toast.makeText(RunActivity.this, "You have set the end point!", Toast.LENGTH_SHORT).show();
                        markerFinishOptions.title("End Point");
                        markerFinishOptions.position(endRunPosition);
                        //markerOptions.zIndex(0);
                        markerFinishOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        drawedMap.addMarker(markerFinishOptions);
                        if( startRunPosition != null){
                            markerStartOptions = new MarkerOptions();
                            markerStartOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            markerStartOptions.position(startRunPosition);
                            markerStartOptions.title("Start Point");
                            drawedMap.addMarker(markerStartOptions);
                        }
                    }

                });







            }
        });



        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startRunPosition != null && endRunPosition != null) {
                    int travelMode = 0;
                    drawedMap.clear();

                    markerFinishOptions = new MarkerOptions();
                    Toast.makeText(RunActivity.this, "You have set the end point!", Toast.LENGTH_SHORT).show();
                    markerFinishOptions.title("End Point");
                    markerFinishOptions.position(endRunPosition);
                    //markerOptions.zIndex(0);
                    markerFinishOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    drawedMap.addMarker(markerFinishOptions);

                        markerStartOptions = new MarkerOptions();
                        markerStartOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        markerStartOptions.position(startRunPosition);
                        markerStartOptions.title("Start Point");
                        drawedMap.addMarker(markerStartOptions);


                    android.support.v7.widget.SwitchCompat s = (android.support.v7.widget.SwitchCompat) findViewById(R.id.switch1);
                    if (s.isChecked())
                    {
                        travelMode = 1;
                    }

                    Toast.makeText(RunActivity.this, "Calculating routes...", Toast.LENGTH_SHORT).show();
                    Log.d("DEBUG","Start point position!!! 2" + startRunPosition.latitude + " / " + startRunPosition.longitude + "]");

                    calculateDirections(startRunPosition,endRunPosition,travelMode);
                }
                else if(startRunPosition == null)
                {
                    Toast.makeText(RunActivity.this, "Please choose your start point", Toast.LENGTH_SHORT).show();

                }
                else if(endRunPosition == null)
                {
                    Toast.makeText(RunActivity.this, "Please choose your finish point", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }





    private void startUserLocationsRunnable() {
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                retrieveUserLocations();
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates() {
        mHandler.removeCallbacks(mRunnable);
    }

    private void retrieveUserLocations() {
        UserLocation updatedUserLocation = getUserLocationObject();

            LatLng updatedLatLng = new LatLng(
                    updatedUserLocation.getUserLatitude(),
                    updatedUserLocation.getUserLongitude()
            );
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                Log.d(TAG, "Starting Location Service");
                RunActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.example.nosmogrunner.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }
    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void calculateDirections(LatLng startRunPosition,LatLng endRunPosition, int travelMode){
        Log.d(TAG, "calculateDirections: calculating directions.");
        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.GOOGLE_MAPS_API_KEY))
                    .build();
        }
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                endRunPosition.latitude,
                endRunPosition.longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);


        directions.mode(TravelMode.WALKING);
        if(travelMode == 1)
        {
            directions.mode(TravelMode.BICYCLING);
        }
        directions.alternatives(true);
        Log.d("DEBUG","Start point position!!! 3" + startRunPosition.latitude + " / " + startRunPosition.longitude + "]");




        directions.origin(
                new com.google.maps.model.LatLng(
                        startRunPosition.latitude,
                        startRunPosition.longitude
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "onResult: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }



    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = drawedMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(RunActivity.this, R.color.gray));
                    polyline.setClickable(true);

                }
            }
        });
    }

}




