package com.example.sidharth.googlemapsdemo;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mgoogleMap;
    GoogleApiClient mgoogleApiClient;
    View vieww;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googleservicesavailable()) {
            Toast.makeText(this, "PERFECT!!!!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
            initMap();
        } else {
            Toast.makeText(this, "Google Maps not supported", Toast.LENGTH_SHORT).show();
        }

        EditText edittext=(EditText)findViewById(R.id.editText);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        geolocate(vieww);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
            });
    }

    private void initMap() {
        MapFragment mapfrag = (MapFragment) getFragmentManager().findFragmentById(R.id.MapFragment);
        mapfrag.getMapAsync(this);
    }

    public boolean googleservicesavailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isavailable = api.isGooglePlayServicesAvailable(this);
        if (isavailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isavailable)) {
            Dialog dialog = api.getErrorDialog(this, isavailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "CANT CONNECT TO GOOGLE SERVICES", Toast.LENGTH_LONG).show();

        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mgoogleMap.setMyLocationEnabled(true);
        //goToLocationZoom(13.0216515,77.6004307,15);
        mgoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mgoogleApiClient.connect();

    }

    private void goToLocation(double v, double v1) {
        LatLng ll = new LatLng(v, v1);
        CameraUpdate camera = CameraUpdateFactory.newLatLng(ll);
        mgoogleMap.moveCamera(camera);
    }

    private void goToLocationZoom(double v, double v1, float zoom) {
        LatLng ll = new LatLng(v, v1);
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mgoogleMap.moveCamera(camera);
    }

    public void geolocate(View view) throws IOException {
        EditText ed = (EditText) findViewById(R.id.editText);
        String loc = ed.getText().toString();
        if(loc.length()==0){
            Toast.makeText(this,"Nothing entered yet",Toast.LENGTH_SHORT).show();;
        }
        else {
            Geocoder gd = new Geocoder(this);
            List<android.location.Address> list = gd.getFromLocationName(loc, 1);
            android.location.Address address = list.get(0);
            String locality = address.getLocality();

            Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();

            double lat = address.getLatitude();
            double lon = address.getLongitude();
            goToLocationZoom(lat, lon, 15);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapNone:
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;

            case R.id.mapNormal:
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case R.id.mapTerrain:
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;

            case R.id.mapSatellite:
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.mapHybrid:
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    LocationRequest mlocationrequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mlocationrequest = LocationRequest.create();
        mlocationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationrequest.setInterval(1000);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mlocationrequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location==null){
            Toast.makeText(this,"Cant get current location",Toast.LENGTH_SHORT);
        } else{

            LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(ll,15);
            mgoogleMap.animateCamera(cameraUpdate);
        }

    }
}
