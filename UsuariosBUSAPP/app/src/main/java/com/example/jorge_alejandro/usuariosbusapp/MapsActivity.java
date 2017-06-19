package com.example.jorge_alejandro.usuariosbusapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Marker userMarker;
    private ArrayList<Marker> busMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        busMarkers=new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        userLocation();
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    public void addUserMarker(double latitude, double longitude) {
        LatLng coordinates = new LatLng(latitude, longitude);
        CameraUpdate userLocation = CameraUpdateFactory.newLatLngZoom(coordinates, 16);
        if (userMarker != null) userMarker.remove();
        userMarker = map.addMarker(new MarkerOptions().position(coordinates).title("USUARIO").snippet("Estudiante de la U.Caldas").icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus_user)));
        map.animateCamera(userLocation);
    }

    public void locationUpdate(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            addUserMarker(latitude, longitude);
            Log.d("Info", "ACtualizando usuario");
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            locationUpdate(location);
            busUpdate();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void userLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            locationUpdate(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
        }catch(Exception e)
        {
            Toast toast = Toast.makeText(this, "Mensaje: "+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private void busUpdate()
    {
        Log.d("Info","entre a actualizar");
        try {
            String url = "http://"+MainActivity.ip+"/BUSAPP/rest/services/busUpdateGet";
            String response = new WSC().execute(url).get();
            Gson json= new Gson();
            Type type=new TypeToken<ArrayList<BusLocation>>() {}.getType();
            ArrayList<BusLocation> locationsBus=json.fromJson(response,type);
            for (int i=0; i<busMarkers.size();i++)
            {
                if (busMarkers.get(i)!=null)
                {
                    busMarkers.get(i).remove();
                }
            }
            busMarkers=new ArrayList<>();
            for (int i=0; i<locationsBus.size(); i++)
            {
                Log.d("Info", locationsBus.get(i).getLatitude()+", "+locationsBus.get(i).getLongitude()+", "+locationsBus.get(i).getBus().getId());
                LatLng coordinates = new LatLng(locationsBus.get(i).getLatitude(), locationsBus.get(i).getLongitude());
                String url2 = "http://"+MainActivity.ip+"/BUSAPP/rest/services/busWayShow/"+locationsBus.get(i).getBus().getId();
                String response2 = new WSC().execute(url).get();
                Type type2=new TypeToken<ArrayList<BusWay>>() {}.getType();
                ArrayList<BusWay> busWay=json.fromJson(response2,type2);
                String ruta="Ruta\n";
                for (int j=0; j<busWay.size(); j++)
                {
                    ruta+=busWay.get(j).getIdbusWay()+"\n";
                    ruta+=busWay.get(j).getWayName()+"\n";
                }
                Marker marcador=map.addMarker(new MarkerOptions().position(coordinates).title("BUS:"+locationsBus.get(i).getBus().getId()).snippet(ruta).icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus_image)));
                busMarkers.add(marcador);
            }
            Log.d("Info", response);
        } catch (Exception e) {
            Log.d("Error", "Exception: "+e.toString());
        }
    }


}
