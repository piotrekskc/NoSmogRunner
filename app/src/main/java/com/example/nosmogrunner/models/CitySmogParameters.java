package com.example.nosmogrunner.models;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

public class CitySmogParameters implements Serializable {
    public ArrayList pm1;
    public ArrayList pm25;
    public ArrayList pm10;
    public ArrayList temperature;
    public String URLAdress;
    public ArrayList routeIndex;
    public ArrayList lastIndex;

    public double cityCenterLat;
    public double cityCenterLng;
    public int cityRadius;

    public ArrayList mediumPm1;
    public ArrayList mediumPm10;
    public ArrayList mediumPm25;

    private ArrayList latitude;
    private ArrayList longitude;
    private ArrayList country;
    private ArrayList city;
    private ArrayList street;
    private ArrayList number;
    private ArrayList displayAdress1;
    private ArrayList displayAdress2;

    public transient JSONArray cityJson;


    public CitySmogParameters() {
        latitude = new ArrayList();
        longitude = new ArrayList();

    }
    public void setJsonArray(JSONArray jsonArray) {
        this.cityJson = jsonArray;
    }

    public JSONArray getCityJson(){
        return  cityJson;
    }

    public int length(){
        return latitude.size();
    }
    public void setLatitudeLongitude(double lat, double lng){
        latitude.add(lat);
        longitude.add(lng);
    }

    public double getLatitude()
    {
        return cityCenterLat;
    }
    public double getLongitude()
    {
        return cityCenterLng;
    }

    public int getCityRadius() {
        return cityRadius;
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

}
