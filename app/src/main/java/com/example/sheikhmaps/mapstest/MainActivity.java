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
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap m_map;
    boolean mapReady = false;
    ArrayList<Integer> listOfDistricts = new ArrayList<Integer>();
    ArrayList<Integer> listOfColors = new ArrayList<Integer>();

    String[] arrayOfDistricts = {"jaipur", "udaipur", "kota", "ajmer", "jodhpur", "bikaner" ,"tonk",
            "jaisalmer", "bhilwara", "bharatpur", "alwar", "barmer"};

    HashMap<String ,Integer> values = new HashMap<String ,Integer>();
    HashMap<String ,Polygon> mapOfPolygons = new HashMap<String, Polygon>();

    Button button;

    private static  final LatLng RAJASTHAN = new LatLng(26.922070, 75.778885);

//    private LatLngBounds RAJASTHAN_BOUNDS = new LatLngBounds(new LatLng(23.086834, 69.482825),
//            new LatLng(30.189796, 78.293860));

    private LatLngBounds RAJASTHAN_BOUNDS = new LatLngBounds(new LatLng(25.688095, 72.163489),
            new LatLng(26.826473, 75.140540));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        button = (Button) findViewById(R.id.changeData);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i=0; i<arrayOfDistricts.length; i++) {
                    values.put(arrayOfDistricts[i], (int)(Math.random() * 100));
                }

                for (HashMap.Entry<String ,Integer> entry : values.entrySet()) {
                    Integer x = entry.getValue();
                    if (x>=0 && x<=20) {
                        Polygon polygon = mapOfPolygons.get(entry.getKey());
                        polygon.setFillColor(Color.GRAY);

                    } else if ((x>20) && (x<=40)) {
                        Polygon polygon = mapOfPolygons.get(entry.getKey());
                        polygon.setFillColor(Color.YELLOW);

                    } else if ((x>40) && (x<=60)) {
                        Polygon polygon = mapOfPolygons.get(entry.getKey());
                        polygon.setFillColor(Color.GREEN);

                    } else if ((x>60) && (x<=80)) {
                        Polygon polygon = mapOfPolygons.get(entry.getKey());
                        polygon.setFillColor(Color.BLUE);

                    } else if ((x>80) && (x<=100)) {
                        Polygon polygon = mapOfPolygons.get(entry.getKey());
                        polygon.setFillColor(Color.RED);

                    }
                }



            }
        });

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        m_map = googleMap;

        flyTo(RAJASTHAN_BOUNDS);

        createPolygon();
    }

    private void flyTo(LatLngBounds target) {
        m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(target.getCenter(), 5));
        m_map.setLatLngBoundsForCameraTarget(RAJASTHAN_BOUNDS);
        m_map.setMinZoomPreference(6.0f);
    }



    private void createPolygon(){
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        PolygonOptions polygonOptions = null;
        try {
            for (int i=0; i<listOfDistricts.size(); i++) {
                list = readItems(listOfDistricts.get(i));
                Polygon polygon = m_map.addPolygon(new PolygonOptions().addAll(list)
                        .strokeColor(Color.TRANSPARENT)
                        .fillColor(listOfColors.get(i)));

                mapOfPolygons.put(arrayOfDistricts[i], polygon);

            }

            Log.i("list2", list.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
