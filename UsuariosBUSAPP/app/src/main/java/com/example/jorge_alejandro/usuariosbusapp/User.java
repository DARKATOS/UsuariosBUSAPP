package com.example.jorge_alejandro.usuariosbusapp;

/**
 * Created by JORGE_ALEJANDRO on 21/06/2017.
 */

public class User {

    private double latitude;
    private double longitude;

    public User(double latitude, double longitude)
    {
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
