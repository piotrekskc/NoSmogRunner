package com.example.nosmogrunner;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapActivity extends MainMenuActivity implements OnMapReadyCallback {
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.map);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);



    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Warsaw,
        // and move the map's camera to the same location.

        LatLng warsaw = new LatLng(52.2319237, 21.0067265);
        LatLng northeast = new LatLng(52.4836,21.2837);
        LatLng southwest = new LatLng(51.9871,20.7452);

        googleMap.addMarker(new MarkerOptions().position(warsaw)
                .title("Marker in Warsaw"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(warsaw));


        PolylineOptions borderPoly = new PolylineOptions();


        ArrayList<LatLng> myBorderArray2 = new ArrayList<LatLng>();
        myBorderArray2 = getJSON();
        borderPoly.addAll(myBorderArray2);
        borderPoly.width(5f);
        borderPoly.color(Color.RED);
        googleMap.addPolyline(borderPoly);

        LatLngBounds warsawBounds = new LatLngBounds(southwest,northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(warsawBounds,0));
    }
public ArrayList<LatLng> getJSON()
{
    ArrayList<LatLng> myBorderArray = new ArrayList<LatLng>();
    String json;
    try
    {
        InputStream is = getAssets().open("boundaries.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        json = new String(buffer, "UTF-8");
        JSONArray jsonArray = new JSONArray(json);


        double[] Latitudes = new double[935];
        double[] Longitudes = new double[935];
        for (int i = 0; i<jsonArray.length();i++)
        {
            JSONArray value = jsonArray.getJSONArray(i);
            Latitudes[i] = value.getDouble(0);
            Longitudes[i] = value.getDouble(1);
            myBorderArray.add(i,new LatLng( Longitudes[i],Latitudes[i]));

        }


      //  JSONObject lastObj = obj.getJSONObject("0");


    }
    catch(IOException e)
    {
        e.printStackTrace();
    }
    catch (JSONException j)
    {
        j.printStackTrace();
    }
    return myBorderArray;
}





}


