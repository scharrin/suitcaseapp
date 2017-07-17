package org.androidtown.location;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    double lat;
    double lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //GPSMainActivity.java 에서 Intent된 위도 경도 값
        Intent intent = getIntent();
        String slat=intent.getStringExtra("lat");
        String slon=intent.getStringExtra("lon");
        Toast.makeText(getApplicationContext(), "위도 "+slat+"  경도 "+slon, Toast.LENGTH_SHORT).show();

        lat=Double.parseDouble(slat);
        lon=Double.parseDouble(slon);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(lat, lon)  // 위도, 경도
        ));


        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        googleMap.animateCamera(zoom);

        MarkerOptions marker = new MarkerOptions();
        marker .position(new LatLng(lat, lon))
                .title("가방")
                .snippet("Suitcase");
        googleMap.addMarker(marker).showInfoWindow();



    }
}



