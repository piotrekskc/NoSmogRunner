package com.example.nosmogrunner.models;

public class UserLocation {


    private double latitude;
    private double longitude;


    public UserLocation(double latitude, double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;

    }

    public UserLocation() {

    }

    public double getUserLatitude(){

        return latitude;
    }

    public double getUserLongitude(){

        return longitude;
    }

    public void setPosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }



    @Override
    public String toString() {
        return "UserLocation{" +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }



}
