package com.example.nosmogrunner.models;

import java.util.ArrayList;

public class MySmogParameters {

           public String pm1;
    public String pm25;
    public String pm10;
    public String temperature;
    public String URLAdress;
    public int routeIndex;
    public int lastIndex;
    public int numberOfRoutes;


    public int responseCode;

    public double mediumPm1;
    public double mediumPm10;
    public double mediumPm25;

    private ArrayList pm1History;
    private ArrayList pm10History;
    private ArrayList pm25History;
    private ArrayList temperatureHistory;

    private ArrayList latitude;
    private ArrayList longitude;
    public String indexName;
    public double indexValue;
    public String indexLevel;
    public String indexDescription;
    public String indexAdvice;
    public String indexColor;


    public MySmogParameters() {
        latitude = new ArrayList();
        longitude = new ArrayList();
        pm1History = new ArrayList();
        pm10History = new ArrayList();
        pm25History = new ArrayList();
        temperatureHistory = new ArrayList();
    }

    public int length(){
        return latitude.size();
    }
    public void setLatitudeLongitude(double lat, double lng){
        latitude.add(lat);
        longitude.add(lng);
    }

    public double getLatitude(int index)
    {
        return (double)latitude.get(index);
    }
    public double getLongitude(int index)
    {
        return (double)longitude.get(index);
    }

    public double calculateMediumPm(ArrayList pm)
    {
        double sum = 0;
        for(int i = 0; i<pm.size();i++){
            sum = sum + (double)pm.get(i);
        }
        double medium = sum / pm.size();

        return medium;
    }

    public void setPm1History(String pm1) {
        pm1History.add(pm1);
    }

    public void setPm10History(String pm10) {
        pm10History.add(pm10);
    }

    public void setPm25History(String pm25) {
        pm25History.add(pm25);
    }
    public void setTemperatureHistory(String temp)
    {
        temperatureHistory.add(temperature);
    }

}
