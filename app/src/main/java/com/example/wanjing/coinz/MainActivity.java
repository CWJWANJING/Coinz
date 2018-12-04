package com.example.wanjing.coinz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Location;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;


import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.style.layers.LineLayer;


import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {
    private MapView mapView;
    private MapboxMap map;

    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;

    private final String tag = "MainActivity";

    Date c = Calendar.getInstance().getTime();

    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    private String currentDate = df.format(c);
    private String downloadDate = "";

    private final String preferencesFile = "MyPrefsFile"; // for storing preferences


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Mapbox.getInstance(this, "pk.eyJ1Ijoid2FuamluZyIsImEiOiJjam14aHFzaXYwZGxmM3B0Y2xiMTV5ZDN3In0.mWGG-pa6gzJEisINLaDLRA");
        mapView = findViewById(R.id.mapboxMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        if (mapboxMap == null) {
            Log.d(tag, "[onMapReady] mapBox is null");
        } else {


            map = mapboxMap;
// Set user interface options
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
// Make location information available
            enableLocation();


            Log.d(tag, "geojson file start downloading.");

            try {

                String url = "http://homepages.inf.ed.ac.uk/stg/coinz/" + currentDate + "/coinzmap.geojson";
                DownloadFileTask dlf = new DownloadFileTask();
                String result = dlf.execute(url).get();

                GeoJsonSource source = new GeoJsonSource("geojson", result);
                mapboxMap.addSource(source);
                LineLayer lineLayer = new LineLayer("geojson", "geojson");
                lineLayer.setProperties();
                mapboxMap.addLayer(lineLayer);

                FeatureCollection featureCollection = FeatureCollection.fromJson(result);
                List<Feature> features = featureCollection.features();

                for (Feature f : features) {
                    if (f.geometry() instanceof Point) {
                        mapboxMap.addMarker(new MarkerViewOptions()
                                .position(new LatLng(((Point) f.geometry()).latitude(), ((Point) f.geometry()).longitude()))
                        );
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.d(tag, "geojson rendered.");

        }
    }
//                FeatureCollection featureCollection = FeatureCollection.fromJson(result);
//
//                GeoJsonSource geoJsonSource = new GeoJsonSource("marker-source", featureCollection);
//                mapboxMap.addSource(geoJsonSource);
//
//                Bitmap icon = BitmapFactory.decodeResource(
//                        MainActivity.this.getResources(), R.drawable.ic_maneuver_fork);
//
//                mapboxMap.addImage("my-marker-image", icon);
//
//                SymbolLayer markers = new SymbolLayer("marker-layer", "marker-source")
//                        .withProperties(PropertyFactory.iconImage("my-marker-image"));
//                mapboxMap.addLayer(markers);



    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Log.d(tag, "Permissions are granted");
            initializeLocationEngine();
            initializeLocationLayer();
        } else {
            Log.d(tag, "Permissions are not granted");
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
        locationEngine = new LocationEngineProvider(this) .obtainBestLocationEngineAvailable();
        locationEngine.setInterval(5000); // preferably every 5 seconds
        locationEngine.setFastestInterval(1000); // at most every second
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer() {
        if (mapView == null) {
            Log.d(tag, "mapView is null");
        } else {
            if (map == null) {
                Log.d(tag, "map is null");
            } else {
                locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
                locationLayerPlugin.setLocationLayerEnabled(true);
                locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
                locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
            }
        }
    }

    private void setCameraPosition(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println(location.toString());
        if (location == null) {
            Log.d(tag, "[onLocationChanged] location is null");
        } else {
            Log.d(tag, "[onLocationChanged] location is not null");
            originLocation = location;
            setCameraPosition(location);
        }
    }

    @Override @SuppressWarnings("MissingPermission") public void onConnected() {
        Log.d(tag, "[onConnected] requesting location updates");
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain){
        Log.d(tag, "Permissions: " + permissionsToExplain.toString()); // Present toast or dialog.
    }

    @Override
    public void onPermissionResult(boolean granted) {
        Log.d(tag, "[onPermissionResult] granted == " + granted);
        if (granted) {
            enableLocation();
        } else {
// Open a dialogue with the user
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
        downloadDate = settings.getString("lastDownloadDate", "");
        Log.d(tag, "[onStart] Recalled lastDownloadDate is " + downloadDate );

        mapView.onStart();
        if(locationEngine != null){

            try {
                locationEngine.requestLocationUpdates();
            } catch(SecurityException ignored) {}
            locationEngine.addLocationEngineListener(this);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(tag, "[onStop] Storing lastDownloadDate of " + downloadDate);
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(preferencesFile,
                Context.MODE_PRIVATE);
        // We need an Editor object to make preference changes.
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lastDownloadDate", downloadDate);
        Log.d(tag,downloadDate);
        Log.d(tag,"download date updated");
        // Apply the edits!
        editor.apply();

        mapView.onStop();
        if(locationEngine != null){
            locationEngine.removeLocationEngineListener(this);
            locationEngine.removeLocationUpdates();
        }
    }




    @SuppressLint("StaticFieldLeak")
    private class DownloadFileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) { try {
            return loadFileFromNetwork(urls[0]); } catch (IOException e) {
            return "Unable to load content. Check your network connection"; }
        }
        private String loadFileFromNetwork(String urlString) throws IOException {
            return readStream(downloadUrl(new URL(urlString)));
        }

        // Given a string representation of a URL, sets up a connection and gets an input stream.
        private InputStream downloadUrl(URL url) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); // milliseconds
            conn.setConnectTimeout(15000); // milliseconds
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream();
        }

        @NonNull
        private String readStream(InputStream stream)
                 {
            // Read input from stream, build result as a string
                     java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
                     return s.hasNext() ? s.next() : "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            DownloadCompleteRunner.downloadComplete(result);
        }
    }


    public void Communicaz(View view){
        Intent startNewActivity = new Intent(this,Communicaz_Activity.class);
        startActivity(startNewActivity);
    }

    public void Setting(View view){
        Intent startNewActivity = new Intent(this,Setting.class);
        startActivity(startNewActivity);
    }

}


