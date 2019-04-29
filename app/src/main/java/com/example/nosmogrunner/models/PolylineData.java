package com.example.nosmogrunner.models;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;

import java.util.ArrayList;

public class PolylineData {

    private Polyline polyline;
    private DirectionsLeg leg;
    private ArrayList pm1 = new ArrayList();
    private ArrayList pm25= new ArrayList();
    private ArrayList pm10= new ArrayList();


    public PolylineData(Polyline polyline, DirectionsLeg leg) {
        this.polyline = polyline;
        this.leg = leg;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public DirectionsLeg getLeg() {
        return leg;
    }

    public void setLeg(DirectionsLeg leg) {
        this.leg = leg;
    }

    @Override
    public String toString() {
        return "PolylineData{" +
                "polyline=" + polyline +
                ", leg=" + leg +
                '}';
    }

    public ArrayList getPm1() {
        return pm1;
    }

    public ArrayList getPm10() {
        return pm10;
    }

    public ArrayList getPm25() {
        return pm25;
    }

    public void setPm1(double pm) {
        pm1.add(pm);
    }

    public void setPm10(double pm) {
        pm10.add(pm);
    }

    public void setPm25(double pm) {
        pm25.add(pm);
    }



}
