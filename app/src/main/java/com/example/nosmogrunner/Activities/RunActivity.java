package com.example.nosmogrunner.Activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nosmogrunner.R;
import com.example.nosmogrunner.models.CitySmogParameters;
import com.example.nosmogrunner.models.CustomInfoWindowAdapter;
import com.example.nosmogrunner.models.MySmogParameters;
import com.example.nosmogrunner.models.PolylineData;
import com.example.nosmogrunner.models.UserLocation;
import com.example.nosmogrunner.services.LocationService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static com.example.nosmogrunner.services.LocationService.getUserLocationObject;
import static com.example.nosmogrunner.utils.Constants.MAPVIEW_BUNDLE_KEY;


public class RunActivity extends MainMenuActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener,GoogleMap.OnPolylineClickListener {

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
    private ArrayList<PolylineData> mPolylinesData=new ArrayList<>();

    public RelativeLayout walkOrRunLayout;
    public MarkerOptions defaultMarkerOptions;
    public MarkerOptions markerStartOptions;
    public MarkerOptions markerFinishOptions;
    private static final float DEFAULT_ZOOM = 15f;
    public LatLng currentClickPosition;
    private Marker infoRouteMarker;
    private ImageButton refreshButton;
    private int travelMode = 0;
    //public MySmogParameters mSmogParameters;
    public ArrayList<MySmogParameters> mSmogParameters;

    public TextView pm1RouteValue;
    public TextView pm10RouteValue;
    public TextView pm25RouteValue;
    public TextView caqiLevel;
    public TextView cleanestRouteText;


    public ProgressBar pm1Progress;
    public ProgressBar pm25Progress;
    public ProgressBar pm10Progress;


    public RelativeLayout smogStatsLayout;
    public RelativeLayout smogComparisonLayout;
    public RelativeLayout historicalLayout;



    public ImageButton beforeButton;

    public ProgressBar loadingCircle;

    public ImageView cleanestRouteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        CitySmogParameters myCityParams = (CitySmogParameters)i.getSerializableExtra("citySmogParameter");
        //JSONArray currentArray = myCityParams.getCityJson();
        setContentView(R.layout.run);
        getSupportActionBar().hide();


        loadingCircle = findViewById(R.id.circularProgress);
        loadingCircle.setVisibility(View.INVISIBLE);

        mMapView = findViewById(R.id.user_map);





        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawedMap != null) {
                    drawedMap.clear();
                    smogStatsLayout.setVisibility(View.INVISIBLE);

                }
            }
        });


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        smogStatsLayout = findViewById(R.id.statsLayout);
        smogStatsLayout.setVisibility(View.INVISIBLE);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        walkOrRunLayout = findViewById(R.id.walkOrRunLayout);
        pm1RouteValue = findViewById(R.id.pm1RouteValue);
        pm10RouteValue = findViewById(R.id.pm10RouteValue);
        pm25RouteValue = findViewById(R.id.pm25RouteValue);
        pm1Progress = findViewById(R.id.PM1RunProgressBar);
        pm25Progress= findViewById(R.id.PM25RunProgressBar);
        pm10Progress= findViewById(R.id.PM10RunProgressBar);
        cleanestRouteImage = findViewById(R.id.cleanestRouteImage);
        cleanestRouteText = findViewById(R.id.cleanestRouteText);
        cleanestRouteImage.setVisibility(View.INVISIBLE);
        cleanestRouteText.setVisibility(View.INVISIBLE);
        historicalLayout = findViewById(R.id.historyLayout);
        historicalLayout.setVisibility(View.INVISIBLE);

        beforeButton = findViewById(R.id.imageBefore);


        beforeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(historicalLayout.getVisibility() == View.INVISIBLE)
                {
                    historicalLayout.setVisibility(View.VISIBLE);
                    beforeButton.setImageResource(R.drawable.ic_next);
                }
                if(historicalLayout.getVisibility() == View.VISIBLE)
                {
                    historicalLayout.setVisibility(View.INVISIBLE);
                    beforeButton.setImageResource(R.drawable.ic_before);
                }
            }
        });


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
        startUserLocationsRunnable();
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
        map.setOnPolylineClickListener(this);

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
//                        LayoutInflater inflater = getLayoutInflater();
//                        View layout = inflater.inflate(R.layout.activity_toast_default_view,
//                                (ViewGroup) findViewById(R.id.toast_layout_root));
//
//
//                        TextView text = (TextView) layout.findViewById(R.id.text);
//                        text.setText("You have set the start point!");
//
//                        Toast toast = new Toast(getApplicationContext());
//                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                        toast.setDuration(Toast.LENGTH_LONG);
//                        toast.setView(layout);
//                        toast.show();

                        showCustomToast("You have set the start point!");

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
                        //View toastView = getLayoutInflater().inflate(R.layout.activity_toast_default_view, null);


                        //Toast endPoint = Toast.makeText(RunActivity.this, "You have set the end point!",Toast.LENGTH_SHORT);
//                        LayoutInflater inflater = getLayoutInflater();
//                        View layout = inflater.inflate(R.layout.activity_toast_default_view,
//                                (ViewGroup) findViewById(R.id.toast_layout_root));
//
//
//                        TextView text = (TextView) layout.findViewById(R.id.text);
//                        text.setText("You have set the end point!");
//
//                        Toast toast = new Toast(getApplicationContext());
//                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                        toast.setDuration(Toast.LENGTH_LONG);
//                        toast.setView(layout);
//                        toast.show();

                        showCustomToast("You have set the end point!");

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
                if (connected()) {
                    if (startRunPosition != null && endRunPosition != null) {

                        drawedMap.clear();

                        markerFinishOptions = new MarkerOptions();
//                        LayoutInflater inflater = getLayoutInflater();
//                        View layout = inflater.inflate(R.layout.activity_toast_default_view,
//                                (ViewGroup) findViewById(R.id.toast_layout_root));
//
//
//                        TextView text = (TextView) layout.findViewById(R.id.text);
//                        text.setText("You have set the end point!");
//
//                        Toast toast = new Toast(getApplicationContext());
//                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                        toast.setDuration(Toast.LENGTH_LONG);
//                        toast.setView(layout);
//                        toast.show();
                        showCustomToast("You have set the end point!");

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
                        if (s.isChecked()) {
                            travelMode = 1;
                        }

//                        LayoutInflater inflater2 = getLayoutInflater();
//                        View layout2 = inflater2.inflate(R.layout.activity_toast_default_view,
//                                (ViewGroup) findViewById(R.id.toast_layout_root));
//
//
//                        TextView text2 = (TextView) layout2.findViewById(R.id.text);
//                        text2.setText("Calculating routes...");
//
//                        Toast toast2 = new Toast(getApplicationContext());
//                        toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                        toast2.setDuration(Toast.LENGTH_LONG);
//                        toast2.setView(layout2);
//                        toast2.show();
                        showCustomToast("Calculating routes...");
                        Log.d("DEBUG", "Start point position!!! 2" + startRunPosition.latitude + " / " + startRunPosition.longitude + "]");

                        calculateDirections(startRunPosition, endRunPosition, travelMode);
                    } else if (startRunPosition == null) {
//                        LayoutInflater inflater2 = getLayoutInflater();
//                        View layout2 = inflater2.inflate(R.layout.activity_toast_default_view,
//                                (ViewGroup) findViewById(R.id.toast_layout_root));
//
//
//                        TextView text2 = (TextView) layout2.findViewById(R.id.text);
//                        text2.setText("Please choose your start point");
//
//                        Toast toast2 = new Toast(getApplicationContext());
//                        toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                        toast2.setDuration(Toast.LENGTH_LONG);
//                        toast2.setView(layout2);
//                        toast2.show();
                        showCustomToast("Please choose your start point");

                    } else if (endRunPosition == null) {
//                         Toast ToastMessage = Toast.makeText(RunActivity.this, "Please choose your finish point", Toast.LENGTH_SHORT);
//                        ToastMessage.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
//                        View toastView = ToastMessage.getView();
                        showCustomToast("Please choose your finish point");


                    }
                }
                else{
//                    Toast ToastMessage = Toast.makeText(RunActivity.this, "Please check your internet connection", Toast.LENGTH_LONG);
//                    ToastMessage.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
//                    ToastMessage.show();
                    showCustomToast("Please check your internet connection");
                }

            }



        });
    }



    private boolean connected(){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null && activeNetworkInfo.isConnected();
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
                if (mPolylinesData.size() > 0) {
                    for (PolylineData polylineData : mPolylinesData) {
                        polylineData.getPolyline().remove();

                    }
                    mPolylinesData.clear();
                    mPolylinesData = new ArrayList<>();

                }


                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

                        // Log.d(TAG, "run: latlng: " + latLng.toString());
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = drawedMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(RunActivity.this, R.color.gray));
                    polyline.setClickable(true);

                    mPolylinesData.add(new PolylineData(polyline, route.legs[0]));
                }
                mSmogParameters = new ArrayList<>();
                int index = 0;


                MySmogParameters params;
                for (PolylineData polylineData : mPolylinesData) {

                    int middleIndex = (polylineData.getPolyline().getPoints().size() - 1) / 2;
                    int lastIndex = polylineData.getPolyline().getPoints().size() - 1;
                    params = new MySmogParameters();
                    mSmogParameters.add(params);
                    mSmogParameters.get(index).routeIndex = index;
                    mSmogParameters.get(index).lastIndex = lastIndex;

                    mSmogParameters.get(index).setLatitudeLongitude(polylineData.getPolyline().getPoints().get(1 * lastIndex / 6).latitude, polylineData.getPolyline().getPoints().get(1 * lastIndex / 6).longitude);
                    mSmogParameters.get(index).setLatitudeLongitude(polylineData.getPolyline().getPoints().get(2 * lastIndex / 6).latitude, polylineData.getPolyline().getPoints().get(2 * lastIndex / 6).longitude);
                    mSmogParameters.get(index).setLatitudeLongitude(polylineData.getPolyline().getPoints().get(3 * lastIndex / 6).latitude, polylineData.getPolyline().getPoints().get(3 * lastIndex / 6).longitude);
                    mSmogParameters.get(index).setLatitudeLongitude(polylineData.getPolyline().getPoints().get(4 * lastIndex / 6).latitude, polylineData.getPolyline().getPoints().get(4 * lastIndex / 6).longitude);
                    mSmogParameters.get(index).setLatitudeLongitude(polylineData.getPolyline().getPoints().get(5 * lastIndex / 6).latitude, polylineData.getPolyline().getPoints().get(5 * lastIndex / 6).longitude);

                    AirlyInterpolateAsync myTask = new AirlyInterpolateAsync();
                    myTask.execute(mSmogParameters.get(index));



                    index++;
                }

                mSmogParameters.get(0).numberOfRoutes = index - 1;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(mPolylinesData.get(0).getPolyline().getPoints().get(0));
                int lastIndex = mPolylinesData.get(0).getPolyline().getPoints().size() - 1;
                builder.include(mPolylinesData.get(0).getPolyline().getPoints().get(lastIndex));
                LatLngBounds bounds = builder.build();


                drawedMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));


            }
        });


    }




    @Override
    public void onInfoWindowClick(final Marker marker) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(RunActivity.this);
            builder.setMessage("Open Google maps application to continue navigation?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            String travelmode = "walking";
                            if(travelMode==1){
                                travelmode = "cycling";
                            }
                            int tripIndex = 0;
                        for(int i = 0; i < mPolylinesData.size();i++)
                        {

                            String title = "Trip number: "+i;
                            if(marker.getTitle().equals(title)){
                                tripIndex = i;
                            }
                        }
                        int polylineSize = mPolylinesData.get(tripIndex).getPolyline().getPoints().size()-1;
                        List<LatLng> currentPolylineWaypoints = mPolylinesData.get(tripIndex).getPolyline().getPoints();

                            String url;
                            if(polylineSize > 6) {
                                url = "https://www.google.com/maps/dir/?api=1&origin="
                                        + startRunPosition.latitude
                                        + ","
                                        + startRunPosition.longitude
                                        + "&destination="
                                        + endRunPosition.latitude
                                        + ","
                                        + endRunPosition.longitude
                                        + "&travelmode="
                                        + travelmode
                                        + "&waypoints="
                                        + currentPolylineWaypoints.get(2 * polylineSize / 6).latitude + "," + currentPolylineWaypoints.get(2 * polylineSize / 6).longitude
                                        + "|"
                                        + currentPolylineWaypoints.get(3 * polylineSize / 6).latitude + "," + currentPolylineWaypoints.get(3 * polylineSize / 6).longitude
                                        + "|"
                                        + currentPolylineWaypoints.get(4 * polylineSize / 6).latitude + "," + currentPolylineWaypoints.get(4 * polylineSize / 6).longitude
                                        + "|"
                                        + currentPolylineWaypoints.get(5 * polylineSize / 6).latitude + "," + currentPolylineWaypoints.get(5 * polylineSize / 6).longitude;
                            }
                             else
                            {
                                url = "https://www.google.com/maps/dir/?api=1&origin="
                                        + startRunPosition.latitude
                                        + ","
                                        + startRunPosition.longitude
                                        + "&destination="
                                        + endRunPosition.latitude
                                        + ","
                                        + endRunPosition.longitude
                                        + "&travelmode="
                                        + travelmode
                                        + "&waypoints="
                                        + currentPolylineWaypoints.get(polylineSize / 2).latitude + "," + currentPolylineWaypoints.get(polylineSize / 2).longitude;

                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);


//                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + endRunPosition.latitude + "," + endRunPosition.longitude);
//                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                            mapIntent.setPackage("com.google.android.apps.maps");

                            try{
                                if (intent.resolveActivity(RunActivity.this.getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            }catch (NullPointerException e){
                                Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                Toast.makeText(RunActivity.this, "Couldn't open map", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
    }





    @Override
    public void onPolylineClick(Polyline polyline) {


        int index = 0;
        for (PolylineData polylineData : mPolylinesData) {
            if (mSmogParameters.get(index).responseCode == 200) {
                Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
                if (polyline.getId().equals(polylineData.getPolyline().getId())) {


                        cleanestRouteText.setVisibility(View.INVISIBLE);
                        cleanestRouteImage.setVisibility(View.INVISIBLE);



                    polylineData.getPolyline().setColor(ContextCompat.getColor(RunActivity.this, R.color.blue));
                    polylineData.getPolyline().setZIndex(1);
                    int middleIndex = (polylineData.getPolyline().getPoints().size() - 1) / 2;

                    drawedMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(RunActivity.this));

                    LatLng middleLocation = polylineData.getPolyline().getPoints().get(middleIndex);

                    if (infoRouteMarker != null) {
                        infoRouteMarker.remove();
                    }
                    infoRouteMarker = drawedMap.addMarker(new MarkerOptions().
                            position(middleLocation).
                            title("Trip number: " + index).
                            snippet("Duration:  " + polylineData.getLeg().duration + "\n Distance:  " + polylineData.getLeg().distance + "\n Press this message to start navigation..."));

                    drawedMap.setOnInfoWindowClickListener(this);

                    infoRouteMarker.showInfoWindow();
//
//                Toast toast = Toast.makeText(RunActivity.this,"Calculating smog route coefficients...",Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
//                toast.show();

                    DecimalFormat format = new DecimalFormat("##.00");


                    int percentPm1 = (int) ((mSmogParameters.get(index).mediumPm1 / 25) * 100);
                    int percentPm10 = (int) ((mSmogParameters.get(index).mediumPm10 / 50) * 100);
                    int percentPm25 = (int) ((mSmogParameters.get(index).mediumPm25 / 25) * 100);


                    pm1Progress.setProgress(percentPm1);


                    pm10Progress.setProgress(percentPm10);
                    pm25Progress.setProgress(percentPm25);

                    double mediumPmCurrent = (mSmogParameters.get(index).mediumPm1 + mSmogParameters.get(index).mediumPm10 + mSmogParameters.get(index).mediumPm25)/3;

                    double lowestMediumPM = 1000;
                    for(int i =0; i<mSmogParameters.get(0).numberOfRoutes; i++ ){


                         double   mediumPm = (mSmogParameters.get(i).mediumPm1 + mSmogParameters.get(i).mediumPm10 + mSmogParameters.get(i).mediumPm25) / 3;

                        if(lowestMediumPM > mediumPm){
                            lowestMediumPM = mediumPm;
                        }
                    }


                    if(lowestMediumPM == mediumPmCurrent) {
                        cleanestRouteText.setVisibility(View.VISIBLE);
                        cleanestRouteImage.setVisibility(View.VISIBLE);
                    }


                    ArrayList hours = new ArrayList();
                    Calendar calendar = Calendar.getInstance();
                    //calendar.setTime(yourdate);
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                    for(int i = 24;i>0;i--)
                    {
                        hours.add((float)(-i));
                    }
                    int indicate = index;



                    List<Entry> pmEntries1 = new ArrayList<Entry>();
                    List<Entry> pmEntries10 = new ArrayList<Entry>();
                    List<Entry> pmEntries25 = new ArrayList<Entry>();


                            LineChart pmChart = (LineChart) findViewById(R.id.pmChart);

                            for(int i = 0; i<mSmogParameters.get(indicate).pm1HistoryMedium.size();i++)
                            {
                                Double number1 =(Double) mSmogParameters.get(indicate).pm1HistoryMedium.get(i);
                                Double number10 =(Double) mSmogParameters.get(indicate).pm10HistoryMedium.get(i);
                                Double number25 =(Double) mSmogParameters.get(indicate).pm25HistoryMedium.get(i);
                                pmEntries1.add(new Entry((float)hours.get(i),number1.floatValue()));
                                pmEntries10.add(new Entry((float)hours.get(i),number10.floatValue()));
                                pmEntries25.add(new Entry((float)hours.get(i),number25.floatValue()));
                            }
                            LineDataSet dataSet1 = new LineDataSet(pmEntries1, "PM1");
                            LineDataSet dataSet10 = new LineDataSet(pmEntries1, "PM 10");
                            LineDataSet dataSet25 = new LineDataSet(pmEntries1, "PM 25");

                            dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
                            dataSet1.setColor(R.color.blue);
                            dataSet10.setAxisDependency(YAxis.AxisDependency.LEFT);
                            dataSet10.setColor(R.color.bright_red);
                            dataSet25.setAxisDependency(YAxis.AxisDependency.LEFT);
                            dataSet25.setColor(R.color.green);
//                            LineData lineData = new LineData(dataSet1);
//                            LineData lineData2 = new LineData(dataSet10);
//                            LineData lineData3 = new LineData(dataSet25);
                            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                            dataSets.add(dataSet1);
                            dataSets.add(dataSet10);
                            dataSets.add(dataSet25);

                            LineData data = new LineData(dataSets);

                            pmChart.setData(data);
                           // pmChart.setData(lineData2);
                           // pmChart.setData(lineData3);
                            pmChart.setVisibility(View.VISIBLE);

                            pmChart.invalidate();




//                if(mSmogParameters.get(index).indexColor == null)
//                {
//                    mSmogParameters.get(index).indexColor = "#000";
//                }
                    pm1RouteValue.setText(String.valueOf(format.format(mSmogParameters.get(index).mediumPm1)));
                    pm1RouteValue.setTextColor(Color.parseColor(mSmogParameters.get(index).indexColor));

                    pm10RouteValue.setText(String.valueOf(format.format(mSmogParameters.get(index).mediumPm10)));
                    pm10RouteValue.setTextColor(Color.parseColor(mSmogParameters.get(index).indexColor));

                    pm25RouteValue.setText(String.valueOf(format.format(mSmogParameters.get(index).mediumPm25)));
                    pm25RouteValue.setTextColor(Color.parseColor(mSmogParameters.get(index).indexColor));

                    historicalLayout.animate();
                    historicalLayout.setVisibility(View.VISIBLE);
                    smogStatsLayout.animate();
                    smogStatsLayout.setVisibility(View.VISIBLE);



                } else {
                    polylineData.getPolyline().setColor(ContextCompat.getColor(RunActivity.this, R.color.gray));
                    polylineData.getPolyline().setZIndex(0);
                }

                index++;


            }




        }
    }



    public class AirlyInterpolateAsync extends AsyncTask<MySmogParameters,Void, MySmogParameters> {

        MySmogParameters passedParameter = new MySmogParameters();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingCircle.setVisibility(View.VISIBLE);
        }

        TextView text;
        String jsonStringResponse;
        @Override
        public MySmogParameters doInBackground(MySmogParameters...params) {
            ArrayList pm1Array = new ArrayList();
            ArrayList pm10Array = new ArrayList();
            ArrayList pm25Array = new ArrayList();
            ArrayList CAQIArray = new ArrayList();

                ArrayList pm1HistoryArray = new ArrayList();
                ArrayList pm10HistoryArray = new ArrayList();
                ArrayList pm25HistoryArray = new ArrayList();
                int index = params[0].routeIndex;
                for (int i = 0; i < params[0].length(); i++) {
                    URL airlyEndpoint;
                    try {
                        airlyEndpoint = new URL("https://airapi.airly.eu/v2/measurements/point?indexType=AIRLY_CAQI"
                                + "&lat=" + params[0].getLatitude(i)
                                + "&lng=" + params[0].getLongitude(i));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    // Create connection
                    HttpsURLConnection myConnection;

                    try {
                        myConnection = (HttpsURLConnection) airlyEndpoint.openConnection();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    myConnection.setRequestProperty("Accept",
                            "application/json");
                    myConnection.setRequestProperty("Accept-Language",
                            "en");
                    myConnection.setRequestProperty("apikey",
                            "smD7lvjAAYAmArkXDcACunq0p0OldmTT");
                    StringBuffer response = new StringBuffer();
                    ;
                    try {
                        if (myConnection.getResponseCode() == 200) {

                            mSmogParameters.get(index).responseCode = 200;
                            Log.i("happy", "CONNECTION SUCCESS!");
                            BufferedReader in = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                            String inputLine;

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }


                            Log.i("Stream data", response.toString());
                            in.close();

                        }
                        if (myConnection.getResponseCode() == 429) {

                            Log.i("Stream data", "TO MANY CONNECTION TRIES");
                            mSmogParameters.get(index).responseCode = 429;
                        }

                        // Success
                        // Further processing here
                        else {
                            // Error handling code goes here
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        if (myConnection.getResponseCode() == 200) {
                            //JSONObject rootObject = new JSONObject(response.toString());

                            JSONObject object = new JSONObject(response.toString());

                            JSONObject address = object.getJSONObject("current");
                            JSONArray historical = object.getJSONArray("history");


                            ArrayList<String> historicalPm1 = new ArrayList<>();
                            ArrayList<String> historicalPm10 = new ArrayList<>();
                            ArrayList<String> historicalPm25 = new ArrayList<>();
                            ArrayList<String> historicalTemperature = new ArrayList<>();
                    for(int j = 0; j<historical.length();j++) {

                        JSONObject historicalReadings = historical.getJSONObject(j);
                        JSONArray historicalValues = historicalReadings.getJSONArray("values");
                        ArrayList<String> measurementValues = new ArrayList<>();
                        for (int k = 0; k < historicalValues.length(); k++) {
                            JSONObject readValue = historicalValues.getJSONObject(k);
                            measurementValues.add(k, readValue.getString("value"));

                        }

                        historicalPm1.add(measurementValues.get(0));
                        historicalPm25.add(measurementValues.get(1));
                        historicalPm10.add(measurementValues.get(2));
                        historicalTemperature.add(measurementValues.get(5));
                    }
                            mSmogParameters.get(index).setPm1History(historicalPm1);
                            mSmogParameters.get(index).setPm25History(historicalPm1);
                            mSmogParameters.get(index).setPm10History(historicalPm1);
                            mSmogParameters.get(index).setTemperatureHistory(historicalPm1);
                            JSONArray values = address.getJSONArray("values");
                            if (!values.isNull(0)) {
                                JSONArray indexes = address.getJSONArray("indexes");

                                JSONObject CAQIindex = indexes.getJSONObject(0);
                                ArrayList<String> measurementValues = new ArrayList<>();

                                for (int j = 0; j < values.length(); j++) {
                                    JSONObject readValue = values.getJSONObject(j);
                                    measurementValues.add(j, readValue.getString("value"));

                                }


                                mSmogParameters.get(index).pm1 = measurementValues.get(0);
                                mSmogParameters.get(index).pm25 = measurementValues.get(1);
                                mSmogParameters.get(index).pm10 = measurementValues.get(2);
                                mSmogParameters.get(index).temperature = measurementValues.get(5);
//                                params[0].pm1 = measurementValues.get(0);
//                                params[0].pm25 = measurementValues.get(1);
//                                params[0].pm10 = measurementValues.get(2);
//                                params[0].temperature = measurementValues.get(5);

//                for (int i = 0; i<CAQIindex.length();i++){
//                    JSONObject readValue = CAQIindex.getJSONObject(i);
//                    measurementValues.add(i,readValue.getString("value"));
//                }
                                mSmogParameters.get(index).indexName = CAQIindex.getString("name");
                                //mSmogParameters.get(index).indexValue = CAQIindex.getString("value");
                                mSmogParameters.get(index).indexLevel = CAQIindex.getString("level");
                                mSmogParameters.get(index).indexDescription = CAQIindex.getString("description");
                                mSmogParameters.get(index).indexAdvice = CAQIindex.getString("advice");
                                mSmogParameters.get(index).indexColor = CAQIindex.getString("color");


                                //mPolylinesData.get(params[0].routeIndex).setPm1(Double.parseDouble(params[0].pm1));
                                //mPolylinesData.get(params[0].routeIndex).setPm10(Double.parseDouble(params[0].pm10));
                                //mPolylinesData.get(params[0].routeIndex).setPm25(Double.parseDouble(params[0].pm25));
                                pm1Array.add(Double.parseDouble(measurementValues.get(0)));
                                pm10Array.add(Double.parseDouble(measurementValues.get(2)));
                                pm25Array.add(Double.parseDouble(measurementValues.get(1)));
                                CAQIArray.add(Double.parseDouble(CAQIindex.getString("value")));
                                pm1HistoryArray.add(Double.parseDouble(measurementValues.get(0)));
                            } else {

                            }
                        }
//                Log.i("street",address.getString("street"));
//                Log.i("number",address.getString("number"));
//                Log.i("displayAddress1",address.getString("displayAddress1"));
//                Log.i("displayAddress2",address.getString("displayAddress2"));


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                if(mSmogParameters.get(index).responseCode == 200) {
                    mSmogParameters.get(index).mediumPm1 = params[0].calculateMediumPm(pm1Array);
                    mSmogParameters.get(index).mediumPm10 = params[0].calculateMediumPm(pm10Array);
                    mSmogParameters.get(index).mediumPm25 = params[0].calculateMediumPm(pm25Array);
                    mSmogParameters.get(index).indexValue = params[0].calculateMediumPm(CAQIArray);
                    mSmogParameters.get(index).pm1HistoryMedium = mSmogParameters.get(index).calculateMediumHistory(mSmogParameters.get(index).pm1History);
                    mSmogParameters.get(index).pm10HistoryMedium = mSmogParameters.get(index).calculateMediumHistory(mSmogParameters.get(index).pm10History);
                    mSmogParameters.get(index).pm25HistoryMedium = mSmogParameters.get(index).calculateMediumHistory(mSmogParameters.get(index).pm25History);
                    mSmogParameters.get(index).temperatureHistoryMedium = mSmogParameters.get(index).calculateMediumHistory(mSmogParameters.get(index).temperatureHistory);
                }
                    passedParameter = params[0];

            return params[0];
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            loadingCircle.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(MySmogParameters params) {

            if (mSmogParameters.get(0).responseCode == 429) {

                AlertDialog alertDialog = new AlertDialog.Builder(RunActivity.this).create();
                alertDialog.setTitle("Please buy full version!");
                alertDialog.setMessage("Unfortunately you have exceeded your daily limit of route searches. Please buy full version to get unlimited ammount of running!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                System.exit(0);
                            }
                        });
                alertDialog.show();


            }
//            DecimalFormat format = new DecimalFormat("##.00");
//
//            smogStatsLayout.animate();
//            smogStatsLayout.setVisibility(View.VISIBLE);
//
//
//            int percentPm1 = (int)((params.mediumPm1 / 25) *100);
//            int percentPm10 = (int)((params.mediumPm10 / 50)*100);
//            int percentPm25 = (int)((params.mediumPm25 / 25)*100);
//
//
//            pm1Progress.setProgress(percentPm1);
//
//
//            pm10Progress.setProgress(percentPm10);
//            pm25Progress.setProgress(percentPm25);
//
//
//            pm1RouteValue.setText(String.valueOf(format.format(params.mediumPm1)));
//            pm1RouteValue.setTextColor(Color.parseColor(params.indexColor));
//
//            pm10RouteValue.setText(String.valueOf(format.format(params.mediumPm10)));
//            pm10RouteValue.setTextColor(Color.parseColor(params.indexColor));
//
//            pm25RouteValue.setText(String.valueOf(format.format(params.mediumPm25)));
//            pm25RouteValue.setTextColor(Color.parseColor(params.indexColor));
            loadingCircle.setVisibility(View.INVISIBLE);
        }


        public MySmogParameters returnParameter()
        {
            return passedParameter;
        }

    }

    public void showCustomToast(String text)
    {
        LayoutInflater inflater2 = getLayoutInflater();
        View layout2 = inflater2.inflate(R.layout.activity_toast_default_view,
                (ViewGroup) findViewById(R.id.toast_layout_root));


        TextView text2 = (TextView) layout2.findViewById(R.id.text);
        text2.setText(text);

        Toast toast2 = new Toast(getApplicationContext());
        toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast2.setDuration(Toast.LENGTH_SHORT);
        toast2.setView(layout2);
        toast2.show();
    }
}




