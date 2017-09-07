package com.example.sheikhmaps.mapstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap m_map;
    boolean mapReady = false;

    private static final CameraPosition NEWYORK = CameraPosition.builder().
            target(new LatLng(40.7484, -73.9857))
            .zoom(17)
            .bearing(0)
            .tilt(45)
            .build();

    private static final CameraPosition SEATTLE = CameraPosition.builder().
            target(new LatLng(47.6204, -122.3491))
            .zoom(17)
            .bearing(0)
            .tilt(45)
            .build();

    private static final CameraPosition DUBLIN = CameraPosition.builder().
            target(new LatLng(53.3478, -6.2597))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();

    private static final CameraPosition TOKYO = CameraPosition.builder().
            target(new LatLng(35.6895, -139.6917))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMap = (Button) findViewById(R.id.btnSeattle);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapReady) {
                    flyTo(SEATTLE);
                }
            }
        });

        Button btnSatellite = (Button) findViewById(R.id.btnTokyo);
        btnSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapReady) {
                    flyTo(TOKYO);
                }
            }
        });

        Button btnHybrid = (Button) findViewById(R.id.btnDublin);
        btnHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapReady) {
                    flyTo(DUBLIN);
                }
            }
        });

        MapFragment mapFragment =(MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        m_map = googleMap;
        m_map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        flyTo(NEWYORK);
    }

    private void flyTo(CameraPosition target) {

        m_map.animateCamera(CameraUpdateFactory.newCameraPosition(target), 5000, null);
    }
}
