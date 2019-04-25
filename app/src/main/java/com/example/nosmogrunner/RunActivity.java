package com.example.nosmogrunner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import static com.example.nosmogrunner.utils.Constants.MAPVIEW_BUNDLE_KEY;


public class RunActivity extends MainMenuActivity implements OnMapReadyCallback,TaskLoadedCallback {

    private static final String TAG = "RunActivity";


    //widgets
    private MapView mMapView;
     private UiSettings mUiSettings;
    private EditText searchBar;
    private Button startButton;
    private Button endButton;

    private Polyline currentPolyline;

    //vars
    LatLng startRunPosition;
    LatLng endRunPosition;
    private LatLng mMapPosition;
    GoogleMap drawedMap;

    RelativeLayout foundPointLayout;
    RelativeLayout addressLayout;



    private static final float DEFAULT_ZOOM = 15f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run);
        getSupportActionBar().hide();

        mMapView = findViewById(R.id.user_map);
        foundPointLayout = findViewById(R.id.relLayout2);
        foundPointLayout.setVisibility(View.INVISIBLE);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        searchBar = findViewById(R.id.input_destination_search);
       initGoogleMap(mapViewBundle);
addressLayout = findViewById(R.id.relLayout1);

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

    private void setCameraView(LatLng position, GoogleMap map,float zoom){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,zoom));
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
    public void onMapReady(final GoogleMap map) {
        drawedMap = map;
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

        mMapPosition = new LatLng(latitude,longitude);
        setCameraView(mMapPosition,map,DEFAULT_ZOOM);

        mUiSettings  = map.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng point) {
                Log.d("DEBUG","Map clicked [" + point.latitude + " / " + point.longitude + "]");
                foundPointLayout.setVisibility(View.VISIBLE);

                LatLng currentClickPosition = new LatLng(point.latitude,point.longitude);
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(currentClickPosition);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title("Choose whether it is start or a stop point");

                // Clears the previously touched position


                // Animating to the touched position
                map.animateCamera(CameraUpdateFactory.newLatLng(currentClickPosition));

                // Placing a marker on the touched position
                map.addMarker(markerOptions);

                startButton = findViewById(R.id.startButton);
                endButton= findViewById(R.id.endButton);


                startButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startRunPosition = new LatLng(point.latitude,point.longitude);

                        Toast.makeText(RunActivity.this,"You have set the start point!",Toast.LENGTH_SHORT).show();

                        if(startRunPosition != null && endRunPosition != null){
                            new FetchURL(RunActivity.this).execute(getUrl(startRunPosition,endRunPosition, "driving"), "driving");
                            map.clear();
                            map.addMarker(new MarkerOptions().
                                    position(startRunPosition).
                                    title("Start"));
                            map.addMarker(new MarkerOptions().
                                    position(endRunPosition).
                                    title("Start"));
                            addressLayout.setVisibility(View.INVISIBLE);

                        }
                    }
                });

                endButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        endRunPosition = new LatLng(point.latitude,point.longitude);

                        Toast.makeText(RunActivity.this,"You have set the end point!",Toast.LENGTH_SHORT).show();


                        if(startRunPosition != null && endRunPosition != null){
                            new FetchURL(RunActivity.this).execute(getUrl(startRunPosition,endRunPosition, "driving"), "driving");
                            map.clear();
                            map.addMarker(new MarkerOptions().
                                    position(startRunPosition).
                                    title("Start"));
                            map.addMarker(new MarkerOptions().
                                    position(endRunPosition).
                                    title("Start"));
                            addressLayout.setVisibility(View.INVISIBLE);
                        }

                    }

                });




            }
        });


    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyBS2nd3ts5S8Lpkzb3yYOymUgzVySjeWbQ";
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = drawedMap.addPolyline((PolylineOptions) values[0]);
        zoomRoute(currentPolyline.getPoints());
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute){
       if(drawedMap == null || lstLatLngRoute ==null || lstLatLngRoute.isEmpty()) return;

       LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
       for (LatLng latLngPoint: lstLatLngRoute)
       {
           boundsBuilder.include(latLngPoint);
       }
       int routePadding = 80;
       LatLngBounds latLngBounds = boundsBuilder.build();

       drawedMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,routePadding),
               600,
               null);
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


//        if(mGeoApiContext == null){
//            mGeoApiContext = new GeoApiContext.Builder()
//                    .apiKey(getString(R.string.google_maps_key))
//                    .build();
//        }



      
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


}




