package com.example.nosmogrunner;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.nosmogrunner.models.CitySmogParameters;

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

public class AirlyCityGet extends AsyncTask<CitySmogParameters,Void,CitySmogParameters> {

    TextView text;
    String jsonStringResponse;

    @Override
    public CitySmogParameters doInBackground(CitySmogParameters... params) {
        ArrayList pm1Array = new ArrayList();
        ArrayList pm10Array = new ArrayList();
        ArrayList pm25Array = new ArrayList();

        //for (int i = 0; i < params[0].length(); i++) {
            URL airlyEndpoint;
            try {//https://airapi.airly.eu/v2/measurements/nearest?indexType=AIRLY_CAQI
                airlyEndpoint = new URL("https://airapi.airly.eu/v2/measurements/nearest?indexType=AIRLY_CAQI"
                        + "&lat=" + params[0].getLatitude()
                        + "&lng=" + params[0].getLongitude()
                        + "&maxDistanceKM=" + params[0].getCityRadius()
                        + "&maxResults=-1");
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
            StringBuilder response = new StringBuilder();
            ;
            try {
                if (myConnection.getResponseCode() == 200) {

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
                }

                // Success
                // Further processing here
                else {
                    Log.i("Stream data", "Not connected");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            try {
//                if (myConnection.getResponseCode() == 200) {
        JSONArray rootArray= new JSONArray();
        try {
            rootArray = new JSONArray(response.toString());
            //params[0].setJsonArray(rootObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


            ArrayList<JSONObject> measurements = new ArrayList();

        for(int i = 0; i<rootArray.length();i++){
            try {
                measurements.add(i,rootArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i<measurements.size();i++)
        {

        }

//
//                    JSONObject address = object.getJSONObject("current");
//                    JSONArray values = address.getJSONArray("values");
//                    if(!values.isNull(0)) {
//                        JSONArray indexes = address.getJSONArray("indexes");
//
//                        JSONObject CAQIindex = indexes.getJSONObject(0);
//                        ArrayList<String> measurementValues = new ArrayList<>();
//
//                        for (int j = 0; j < values.length(); j++) {
//                            JSONObject readValue = values.getJSONObject(j);
//                            measurementValues.add(j, readValue.getString("value"));
//
//                        }
//
//                        params[0].pm1 = measurementValues.get(0);
//                        params[0].pm25 = measurementValues.get(1);
//                        params[0].pm10 = measurementValues.get(2);
//                        params[0].temperature = measurementValues.get(5);
//
////                for (int i = 0; i<CAQIindex.length();i++){
////                    JSONObject readValue = CAQIindex.getJSONObject(i);
////                    measurementValues.add(i,readValue.getString("value"));
////                }
//                        params[0].indexName = CAQIindex.getString("name");
//                        params[0].indexValue = CAQIindex.getString("value");
//                        params[0].indexLevel = CAQIindex.getString("level");
//                        params[0].indexDescription = CAQIindex.getString("description");
//                        params[0].indexAdvice = CAQIindex.getString("advice");
//                        params[0].indexColor = CAQIindex.getString("color");
//
//
//                        //mPolylinesData.get(params[0].routeIndex).setPm1(Double.parseDouble(params[0].pm1));
//                        //mPolylinesData.get(params[0].routeIndex).setPm10(Double.parseDouble(params[0].pm10));
//                        //mPolylinesData.get(params[0].routeIndex).setPm25(Double.parseDouble(params[0].pm25));
//                        pm1Array.add(Double.parseDouble(measurementValues.get(0)));
//                        pm10Array.add(Double.parseDouble(measurementValues.get(2)));
//                        pm25Array.add(Double.parseDouble(measurementValues.get(1)));
//                    }
//                    else
//                    {
//
//                    }
//                }
//                Log.i("street",address.getString("street"));
//                Log.i("number",address.getString("number"));
//                Log.i("displayAddress1",address.getString("displayAddress1"));
//                Log.i("displayAddress2",address.getString("displayAddress2"));


//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        params[0].mediumPm1 = params[0].calculateMediumPm(pm1Array);
//        params[0].mediumPm10 = params[0].calculateMediumPm(pm10Array);
//        params[0].mediumPm25 = params[0].calculateMediumPm(pm25Array);

        //}
        return params[0];
    }


}




