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
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador1;
    private ArrayList<Marker> marcadoresBuses;
    double latitud=0.0;
    double longitud=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        marcadoresBuses=new ArrayList<>();
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
        mMap = googleMap;
        miUbicacion();
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    public void agregarMarcadorUsuario(double latitud, double longitud) {
        LatLng coordenadas = new LatLng(latitud, longitud);
        CameraUpdate miubicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador1 != null) marcador1.remove();
        marcador1 = mMap.addMarker(new MarkerOptions().position(coordenadas).title("USUARIO").snippet("Estudiante de la U.Caldas").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
        mMap.animateCamera(miubicacion);
    }

    public void actualizarUbicacion(Location location) {
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            agregarMarcadorUsuario(latitud, longitud);

        }
    }

    LocationListener escucharlocation = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
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

    private void miUbicacion() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            actualizarUbicacion(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, escucharlocation);
        }catch(Exception e)
        {
            Toast toast = Toast.makeText(this, "Mensaje: "+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private void busUpdate()
    {
        try {
            String url = "http://192.168.1.57:8084/BUSAPP/rest/services/busUpdateGet";
            String response = new WSC().execute(url).get();
            Gson json= new Gson();
            Type type=new TypeToken<ArrayList<LocationBus>>() {}.getType();
            ArrayList<LocationBus> locationsBus=json.fromJson(response,type);
            for (int i=0; i<marcadoresBuses.size();i++)
            {
                if (marcadoresBuses.get(i)!=null)
                {
                    marcadoresBuses.get(i).remove();
                }
            }
            marcadoresBuses=new ArrayList<>();
            for (int i=0; i<locationsBus.size(); i++)
            {
                Log.d("Info", locationsBus.get(i).getLatitude()+", "+locationsBus.get(i).getLongitude()+", "+locationsBus.get(i).getBus().getId());
                LatLng coordenadas = new LatLng(locationsBus.get(i).getLatitude(), locationsBus.get(i).getLongitude());
                Marker marcador=mMap.addMarker(new MarkerOptions().position(coordenadas).title("BUS").snippet("id del bus: "+locationsBus.get(i).getBus().getId()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
                marcadoresBuses.add(marcador);
            }

            Log.d("Info", response);
        } catch (Exception e) {
            Log.d("Error", "Exception: "+e.toString());
        }
    }


}
