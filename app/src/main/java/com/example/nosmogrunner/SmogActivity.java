package com.example.nosmogrunner;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SmogActivity extends MainMenuActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smog);
        getSupportActionBar().hide();


    }

    public void getMessage(View view) {
        AirlyAsync getSmogData = new AirlyAsync();
        MyTaskParams params = new MyTaskParams();



        params.URLAdress = "https://airapi.airly.eu/v2/measurements/nearest?indexType=AIRLY_CAQI&lat=52.229932&lng=21.056501&maxDistanceKM=10";
        String data;
        getSmogData.execute(params);



        };

 //  https://airapi.airly.eu/v2/installations/nearest?lat=50.062006&lng=19.940984&maxDistanceKM=3&maxResults=1
 private static class MyTaskParams {
     String pm1;
     String pm25;
     String pm10;
     String temperature;
     String URLAdress;

     String indexName;
     String indexValue;
     String indexLevel;
     String indexDescription;
     String indexAdvice;
     String indexColor;


 }

    public class AirlyAsync extends AsyncTask<MyTaskParams,Void, MyTaskParams> {


     TextView text;
        String jsonStringResponse;
        @Override
        public MyTaskParams doInBackground(MyTaskParams...params) {

            URL airlyEndpoint;
            try {
                airlyEndpoint = new URL(params[0].URLAdress);
            }
            catch(MalformedURLException e)
            {
                throw new RuntimeException(e);
            }
            // Create connection
            HttpsURLConnection myConnection;

            try{
                myConnection = (HttpsURLConnection) airlyEndpoint.openConnection();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            myConnection.setRequestProperty("Accept",
                    "application/json");
            myConnection.setRequestProperty("Accept-Language",
                    "en");
            myConnection.setRequestProperty("apikey",
                    "smD7lvjAAYAmArkXDcACunq0p0OldmTT");
            StringBuffer response = new StringBuffer();;
            try {
                if (myConnection.getResponseCode() == 200) {

                    Log.i("happy", "CONNECTION SUCCESS!");
                    BufferedReader in = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                     response.append(inputLine);
                    }


                    Log.i("Stream data",response.toString());
                    in.close();

                }
                    // Success
                    // Further processing here
                 else {
                    // Error handling code goes here
                }
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
            try {
                //JSONObject rootObject = new JSONObject(response.toString());

                JSONObject object = new JSONObject(response.toString());

                JSONObject address  = object.getJSONObject("current");
                JSONArray values  = address.getJSONArray("values");
                JSONArray indexes  = address.getJSONArray("indexes");
                JSONObject CAQIindex = indexes.getJSONObject(0);
                ArrayList<String> measurementValues = new ArrayList<>();

                for (int i = 0; i<values.length();i++)
                {
                   JSONObject readValue = values.getJSONObject(i);
                   measurementValues.add(i,readValue.getString("value"));

                }
                params[0].pm1 = measurementValues.get(0);
                params[0].pm25 = measurementValues.get(1);
                params[0].pm10 = measurementValues.get(2);
                params[0].temperature = measurementValues.get(3);

//                for (int i = 0; i<CAQIindex.length();i++){
//                    JSONObject readValue = CAQIindex.getJSONObject(i);
//                    measurementValues.add(i,readValue.getString("value"));
//                }
                params[0].indexName = CAQIindex.getString("name");
                params[0].indexValue = CAQIindex.getString("value");
                params[0].indexLevel = CAQIindex.getString("level");
                params[0].indexDescription = CAQIindex.getString("description");
                params[0].indexAdvice = CAQIindex.getString("advice");
                params[0].indexColor = CAQIindex.getString("color");




//                Log.i("street",address.getString("street"));
//                Log.i("number",address.getString("number"));
//                Log.i("displayAddress1",address.getString("displayAddress1"));
//                Log.i("displayAddress2",address.getString("displayAddress2"));


            }
            catch(JSONException e){
                throw new RuntimeException(e);
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(MyTaskParams params) {

            text = (TextView)findViewById(R.id.CAQIValue);
            text.setText(params.indexValue);
            text = (TextView)findViewById(R.id.CAQILevelValue);
            text.setText(params.indexLevel);
            text.setTextColor(Color.parseColor(params.indexColor));
            text = (TextView)findViewById(R.id.descriptionValue);
            text.setText(params.indexDescription);
            text.setTextColor(Color.parseColor(params.indexColor));
            text = (TextView)findViewById(R.id.adviceValue);
            text.setText(params.indexAdvice);
            text.setTextColor(Color.parseColor(params.indexColor));
            text = (TextView)findViewById(R.id.pm1Value);
            text.setText(params.pm1);
            text.setTextColor(Color.parseColor(params.indexColor));
            text = (TextView)findViewById(R.id.pm10Value);
            text.setText(params.pm10);
            text.setTextColor(Color.parseColor(params.indexColor));
            text = (TextView)findViewById(R.id.pm25Value);
            text.setText(params.pm25);
            text.setTextColor(Color.parseColor(params.indexColor));



        }
    }




}
