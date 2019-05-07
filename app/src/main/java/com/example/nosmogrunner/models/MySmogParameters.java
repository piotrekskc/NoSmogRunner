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

    public ArrayList<ArrayList<String>> pm1History;
    public ArrayList<ArrayList<String>> pm10History;
    public ArrayList<ArrayList<String>> pm25History;
    public ArrayList<ArrayList<String>> temperatureHistory;

    public ArrayList pm1HistoryMedium;
    public ArrayList pm10HistoryMedium;
    public ArrayList pm25HistoryMedium;
    public ArrayList temperatureHistoryMedium;


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
        pm1HistoryMedium = new ArrayList();
        pm10HistoryMedium= new ArrayList();
        pm25HistoryMedium= new ArrayList();
        temperatureHistoryMedium= new ArrayList();
        pm1History = new ArrayList<ArrayList<String>>();
        pm10History = new ArrayList<ArrayList<String>>();
        pm25History = new ArrayList<ArrayList<String>>();
        temperatureHistory = new ArrayList<ArrayList<String>>();
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


    public void setPm1History(ArrayList pm1) {

        pm1History.add(pm1);
    }

    public void setPm10History(ArrayList pm10) {
        pm10History.add(pm10);
    }

    public void setPm25History(ArrayList pm25) {

        pm25History.add(pm25);
    }
    public void setTemperatureHistory(ArrayList temp)
    {
        temperatureHistory.add(temp);
    }

    public ArrayList calculateMediumHistory(ArrayList<ArrayList<String>> myArray)
    {
        ArrayList mediumHistory = new ArrayList();

            int firstArraySize = myArray.size();
            for(int j = 0; j<myArray.get(0).size();j++)
            {
                double medium = 0;
                for(int i = 0; i<firstArraySize;i++) {
                   medium =medium + (Double.parseDouble(myArray.get(i).get(j)));

                }
                double calculatedMedium = medium/firstArraySize;
                mediumHistory.add(calculatedMedium);
            }






        return mediumHistory;
    }


}
