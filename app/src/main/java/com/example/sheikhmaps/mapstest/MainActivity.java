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

    private List<LatLng> list = null;

//    MarkerOptions renton;
//    MarkerOptions kirkland;
//    MarkerOptions everett;

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

    public float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };

    public  Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;

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
        addHeatMap();
    }

    private void flyTo(LatLng target) {
        m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 5));
    }

    public void changeRadius(View view) {
        if (mDefaultRadius) {
            mProvider.setRadius(ALT_HEATMAP_RADIUS);
        } else {
            mProvider.setRadius(HeatmapTileProvider.DEFAULT_RADIUS);
        }
        mOverlay.clearTileCache();
        mDefaultRadius = !mDefaultRadius;
    }

    public void changeGradient(View view) {
//        ArrayList<LatLng> points = new ArrayList<LatLng>();
        if (mDefaultGradient) {
//            for (int j=0; j<list.size(); j++) {
//                dataAtLatlng(points, list.get(j),(int) (Math.random() * 10));
//            }
//
//            mProvider.setData(points);

//            float[] ALT_HEATMAP_GRADIENT_START_POINTS = {0f, 0f, 0f, 0f, 0f};
//            Gradient ALT_HEATMAP_GRADIENT;
//            float min, max;
//            min = 5f;
//            max = min + (float)(Math.random() * (10 - min));
//            int step = 5;
//            for (int i = 0; i<step; i++) {
//                ALT_HEATMAP_GRADIENT_START_POINTS[i] = ((max - min) / (float)step) * (i+1);
//            }

            ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
                    ALT_HEATMAP_GRADIENT_START_POINTS);

            mProvider.setGradient(ALT_HEATMAP_GRADIENT);

//            Log.i("list",points.toString());
        } else {
            mProvider.setGradient(HeatmapTileProvider.DEFAULT_GRADIENT);
        }
        mOverlay.clearTileCache();
        mDefaultGradient = !mDefaultGradient;
    }

    public void changeOpacity(View view) {
        if (mDefaultOpacity) {
            mProvider.setOpacity(ALT_HEATMAP_OPACITY);
        } else {
            mProvider.setOpacity(HeatmapTileProvider.DEFAULT_OPACITY);
        }
        mOverlay.clearTileCache();
        mDefaultOpacity = !mDefaultOpacity;
    }

    public void changeData(View view) {
        ArrayList<LatLng> points = new ArrayList<LatLng>();

        for (int j=0; j<list.size(); j++) {
            dataAtLatlng(points, list.get(j),(int) (Math.random() * 10));
        }

        mProvider.setData(points);
        mOverlay.clearTileCache();
    }

    private void addHeatMap() {
//        List<LatLng> list = null;

        // Get the data: latitude/longitude positions of districts.
        try {
            list = readItems(R.raw.districs);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        // Create a heat map tile provider, passing it the latlngs of the districts.
        mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = m_map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
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

    private void dataAtLatlng(ArrayList<LatLng> listOfDataPoints, LatLng latlng, int count) {
         for (int i=0; i<count; i++) {
             double lat = latlng.latitude;
             double lng = latlng.longitude;
             if (Math.random()>0.5) {
                 lat += Math.random() / 4;
             } else {
                 lat -= Math.random() / 4;
             }

             if (Math.random()>0.5) {
                 lng += Math.random() / 4;
             } else {
                 lng -= Math.random() / 4;
             }
             listOfDataPoints.add(new LatLng(lat, lng));

         }
    }
}
