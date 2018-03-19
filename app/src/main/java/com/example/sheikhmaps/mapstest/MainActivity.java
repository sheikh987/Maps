package com.example.sheikhmaps.mapstest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap m_map;
    boolean mapReady = false;
    ArrayList<Integer> listOfDistricts = new ArrayList<Integer>();
    ArrayList<Integer> listOfColors = new ArrayList<Integer>();
    //    private static final CameraPosition SEATTLE = CameraPosition.builder().
//            target(new LatLng(47.6204, -122.3491))
//            .zoom(10)
//            .bearing(0)
//            .tilt(45)
//            .build();
    private static  final LatLng RAJASTHAN = new LatLng(26.922070, 75.778885);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        renton = new MarkerOptions()
//                .position(new LatLng(47.489805, -122.120502))
//                .title("Renton");
//
//        kirkland = new MarkerOptions()
//                .position(new LatLng(47.7301986, -122.1768858))
//                .title("Kirkland");
//
//        everett = new MarkerOptions()
//                .position(new LatLng(47.978478, -122.202001))
//                .title("Everett");

        listOfDistricts.add(R.raw.jaipur);
        listOfDistricts.add(R.raw.udaipur);
        listOfDistricts.add(R.raw.kota);
        listOfDistricts.add(R.raw.ajmer);
        listOfDistricts.add(R.raw.jodhpur);
        listOfDistricts.add(R.raw.bikaner);
        listOfDistricts.add(R.raw.tonk);
        listOfDistricts.add(R.raw.jaisalmer);
        listOfDistricts.add(R.raw.bhilwara);
        listOfDistricts.add(R.raw.bharatpur);
        listOfDistricts.add(R.raw.alwar);
        listOfDistricts.add(R.raw.barmer);

        listOfColors.add(Color.RED);
        listOfColors.add(Color.BLUE);
        listOfColors.add(Color.DKGRAY);
        listOfColors.add(Color.GREEN);
        listOfColors.add(Color.GRAY);
        listOfColors.add(Color.CYAN);
        listOfColors.add(Color.LTGRAY);
        listOfColors.add(Color.GREEN);
        listOfColors.add(Color.YELLOW);
        listOfColors.add(Color.BLUE);
        listOfColors.add(Color.GRAY);
        listOfColors.add(Color.RED);



        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        m_map = googleMap;
//        m_map.addMarker(renton);
//        m_map.addMarker(kirkland);
//        m_map.addMarker(everett);
        flyTo(RAJASTHAN);
        createPolygon();
    }

    private void flyTo(LatLng target) {
        m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 5));
    }

    private void createPolygon(){
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        PolygonOptions polygonOptions = null;
        try {
            for (int i=0; i<listOfDistricts.size(); i++) {
                list = readItems(listOfDistricts.get(i));
                Polygon polygon =m_map.addPolygon(new PolygonOptions().addAll(list)
                        .strokeColor(Color.TRANSPARENT)
                        .fillColor(listOfColors.get(i)));

            }

            Log.i("list2", list.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        for (int i=0; i<list.size(); i++) {
//            Log.i("list(i)", list.get(i).toString());
//            polygonOptions = new PolygonOptions().add(list.get(i));
//        }
//
//        Polygon polygon = m_map.addPolygon(polygonOptions.strokeColor(Color.RED));
    }

    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("long");
            list.add(new LatLng(lat, lng));
        }
        Log.i("list",list.toString());
        return list;
    }


}
