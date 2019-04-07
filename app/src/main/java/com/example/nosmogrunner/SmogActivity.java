package com.example.nosmogrunner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SmogActivity extends MainMenuActivity {

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smog);
        getSupportActionBar().hide();







    }

    public void getMessage(View view){
            AirlyAsync getSmogData = new AirlyAsync();
        TextView tv = (TextView)findViewById(R.id.textView5);
        tv.setText("Retrieving data...");
        String data;
        
        getSmogData.execute();

//this to set delegate/listener back to this class


        };



    public class AirlyAsync extends AsyncTask<Void,String, String> {

        String jsonStringResponse;
        @Override
        public String doInBackground(Void...voids) {

            URL airlyEndpoint;
            try {
                airlyEndpoint = new URL("https://airapi.airly.eu/v2/measurements/nearest?indexType=AIRLY_CAQI&lat=52.229932&lng=21.056501&maxDistanceKM=35");
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
                jsonStringResponse = response.toString();
//                JSONObject address  = object.getJSONObject("address");
//                Log.i("country",address.getString("country"));
//                Log.i("city",address.getString("city"));
//                Log.i("street",address.getString("street"));
//                Log.i("number",address.getString("number"));
//                Log.i("displayAddress1",address.getString("displayAddress1"));
//                Log.i("displayAddress2",address.getString("displayAddress2"));
                return jsonStringResponse;

            }
            catch(JSONException e){
                throw new RuntimeException(e);
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView tv = (TextView)findViewById(R.id.textView5);
            tv.setText(jsonStringResponse);
        }
    }




}
