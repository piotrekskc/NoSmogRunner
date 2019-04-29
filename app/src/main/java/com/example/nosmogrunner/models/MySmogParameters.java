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

        public double mediumPm1;
        public double mediumPm10;
        public double mediumPm25;

        private ArrayList latitude;
        private ArrayList longitude;
        public String indexName;
        public String indexValue;
        public String indexLevel;
        public String indexDescription;
        public String indexAdvice;
        public String indexColor;


    public MySmogParameters() {
latitude = new ArrayList();
longitude = new ArrayList();

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
        public double getLLongitude(int index)
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

}
