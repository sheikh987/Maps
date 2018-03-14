package com.example.sheikhmaps.mapstest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    /**
     * Alternative radius for convolution
     */
    private static final int ALT_HEATMAP_RADIUS = 50;

    /**
     * Alternative opacity of heatmap overlay
     */
    private static final double ALT_HEATMAP_OPACITY = 0.4;

    /**
     * Alternative heatmap gradient (blue -> red)
     * Copied from Javascript version
     */
    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 0, 127),
            Color.rgb(255, 0, 0)
    };

//    public float[] ALT_HEATMAP_GRADIENT_START_POINTS; = {
//            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
//    };

//    public  Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
//            ALT_HEATMAP_GRADIENT_START_POINTS);

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;


    private static  final LatLng RAJASTHAN = new LatLng(26.922070, 75.778885);

    ArrayList<HeatmapTileProvider> heatmapTileProviderArrayList = new ArrayList<HeatmapTileProvider>();
    ArrayList<TileOverlay> tileOverlayArrayList = new ArrayList<TileOverlay>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        m_map = googleMap;

        flyTo(RAJASTHAN);
        addHeatMap();
    }

    private void flyTo(LatLng target) {
        m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 5));
    }

    public void changeRadius(View view) {
        if (mDefaultRadius) {
            for (int j=0; j<heatmapTileProviderArrayList.size(); j++) {
                heatmapTileProviderArrayList.get(j).setRadius(ALT_HEATMAP_RADIUS);
                tileOverlayArrayList.get(j).clearTileCache();
            }

        } else {
            for (int j=0; j<heatmapTileProviderArrayList.size(); j++) {
                heatmapTileProviderArrayList.get(j).setRadius(HeatmapTileProvider.DEFAULT_RADIUS);
                tileOverlayArrayList.get(j).clearTileCache();
            }
        }
        mDefaultRadius = !mDefaultRadius;
    }

    public void changeGradient(View view) {
        if (mDefaultGradient) {
            for (int j=0; j<heatmapTileProviderArrayList.size(); j++) {
                float[] ALT_HEATMAP_GRADIENT_START_POINTS = {0f, 0f, 0f, 0f, 0f};
                Gradient ALT_HEATMAP_GRADIENT;
                float min, max;
                min = 9f;
                max = min + (float)(Math.random() * (10 - min));
                int step = 5;
                for (int i = 0; i<step; i++) {
                    ALT_HEATMAP_GRADIENT_START_POINTS[i] = ((max - min) / (float)step) * (i+1);
                }

                ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
                        ALT_HEATMAP_GRADIENT_START_POINTS);

                heatmapTileProviderArrayList.get(j).setGradient(ALT_HEATMAP_GRADIENT);
                tileOverlayArrayList.get(j).clearTileCache();

            }

        } else {
            for (int j=0; j<heatmapTileProviderArrayList.size(); j++) {
                heatmapTileProviderArrayList.get(j).setGradient(HeatmapTileProvider.DEFAULT_GRADIENT);
                tileOverlayArrayList.get(j).clearTileCache();
            }
            //mProvider.setGradient(HeatmapTileProvider.DEFAULT_GRADIENT);
        }

        mDefaultGradient = !mDefaultGradient;
    }

    public void changeOpacity(View view) {
        if (mDefaultOpacity) {
            for (int j=0; j<heatmapTileProviderArrayList.size(); j++) {
                heatmapTileProviderArrayList.get(j).setOpacity(ALT_HEATMAP_OPACITY);
                tileOverlayArrayList.get(j).clearTileCache();
            }

        } else {
            for (int j=0; j<heatmapTileProviderArrayList.size(); j++) {
                heatmapTileProviderArrayList.get(j).setOpacity(HeatmapTileProvider.DEFAULT_OPACITY);
                tileOverlayArrayList.get(j).clearTileCache();
            }
        }
        mOverlay.clearTileCache();
        mDefaultOpacity = !mDefaultOpacity;
    }

    private void addHeatMap() {

        ArrayList<LatLng> list = new ArrayList<LatLng>();

        // Get the data: latitude/longitude positions of districts.
        try {
            list = readItems(R.raw.districs);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of districts.", Toast.LENGTH_LONG).show();
        }

        if (list != null){
            for (int i=0; i<list.size(); i++) {

                ArrayList<LatLng> singleList = new ArrayList<LatLng>();
                singleList.add(list.get(i));
                // Create a heat map tile provider, passing it the latlngs of the districts.
                mProvider = new HeatmapTileProvider.Builder()
                        .data(singleList)
                        .build();
                heatmapTileProviderArrayList.add(mProvider);
                // Add a tile overlay to the map, using the heat map tile provider.
                mOverlay = m_map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider)) ;
                tileOverlayArrayList.add(mOverlay);
            }
        } else {Toast.makeText(this, "Problem reading list",Toast.LENGTH_LONG).show();}

//        // Create a heat map tile provider, passing it the latlngs of the districts.
//        mProvider = new HeatmapTileProvider.Builder()
//                .data(list)
//                .build();
//        // Add a tile overlay to the map, using the heat map tile provider.
//        mOverlay = m_map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }
        return list;
    }
}
