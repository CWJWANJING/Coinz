package com.example.wanjing.coinz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Location;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;



import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
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
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;

import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;

import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;

import java.net.URL;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {
    // a general tag
    private final String tag = "MainActivity";

    // this is for the mapbox
    private MapView mapView;
    private MapboxMap map;

    // this is for getting the user location
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;

    // this is for getting the current date
    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    private String currentDate = df.format(c);

    // for shared reference file
    private String downloadDate = "";
    private final String preferencesFile = "MyPrefsFile"; // for storing preferences

    // for store data in firestore
    private FirebaseFirestore firestore;
    private DocumentReference firestoreBank;
    private static final String t = "Bank";
    private static final String COLLECTION_KEY = "Bank";

    // get the user email from firestore
    String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    // default coins is 0
    Float coinz = 0f;
    Map<String, Object> userMap = new HashMap<>();
    // default distance is 0
    Float d = 0f;

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

        // firestore
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        firestoreBank = firestore.collection(COLLECTION_KEY).document(COLLECTION_KEY);

    }

    // downloading the file from url
    String url = "http://homepages.inf.ed.ac.uk/stg/coinz/" + currentDate + "/coinzmap.geojson";
    DownloadFileTask dlf = new DownloadFileTask();
    String result;

    {
        try {
            result = dlf.execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

                GeoJsonSource source = new GeoJsonSource("geojson", result);
                mapboxMap.addSource(source);

                // prepare icons for each currency
                IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                Icon icon0 = iconFactory.fromResource(R.drawable.icon0);
                Icon icon1 = iconFactory.fromResource(R.drawable.icon1);
                Icon icon2 = iconFactory.fromResource(R.drawable.icon2);
                Icon icon3 = iconFactory.fromResource(R.drawable.icon3);

                // get features from geojson string
                FeatureCollection featureCollection = FeatureCollection.fromJson(result);
                List<Feature> features = featureCollection.features();

                // get the currency rates from geojson file
                JSONObject myObject = new JSONObject(result);
                JSONObject json_rates = myObject.getJSONObject("rates");
                String rS = json_rates.get("SHIL").toString();
                String rP = json_rates.get("PENY").toString();
                String rQ = json_rates.get("QUID").toString();
                String rD = json_rates.get("QUID").toString();


                float rateq = Float.parseFloat(rQ);
                float rates = Float.parseFloat(rS);
                float ratep = Float.parseFloat(rP);
                float rated = Float.parseFloat(rD);
                // loop through feature collection to add each feature with different icons according to their currencies,
                // with the snippet showing the actual gold coins value, by dividing the currency value by the corresponding currency rate
                for (Feature f : features) {
                    if (f.geometry() instanceof Point) {
                        if (f.getStringProperty("currency").equals("QUID")){
                                mapboxMap.addMarker(new MarkerViewOptions()
                                        .position(new LatLng(((Point) f.geometry()).latitude(), ((Point) f.geometry()).longitude()))
                                        .icon(icon0)
                                        .snippet(Float.toString(Float.parseFloat(f.getStringProperty("value"))/rateq))
                                        .title(f.getStringProperty("id"))
                                );
                        }
                        else if (f.getStringProperty("currency").equals("SHIL")){
                            mapboxMap.addMarker(new MarkerViewOptions()
                                    .position(new LatLng(((Point) f.geometry()).latitude(), ((Point) f.geometry()).longitude()))
                                    .icon(icon1)
                                    .snippet(Float.toString(Float.parseFloat(f.getStringProperty("value"))/rates))
                                    .title(f.getStringProperty("id"))
                            );
                        }
                        else if (f.getStringProperty("currency").equals("PENY")){
                            mapboxMap.addMarker(new MarkerViewOptions()
                                    .position(new LatLng(((Point) f.geometry()).latitude(), ((Point) f.geometry()).longitude()))
                                    .icon(icon2)
                                    .snippet(Float.toString(Float.parseFloat(f.getStringProperty("value"))/ratep))
                                    .title(f.getStringProperty("id"))
                            );
                        }
                        else if (f.getStringProperty("currency").equals("DOLR")){
                            mapboxMap.addMarker(new MarkerViewOptions()
                                    .position(new LatLng(((Point) f.geometry()).latitude(), ((Point) f.geometry()).longitude()))
                                    .icon(icon3)
                                    .snippet(Float.toString(Float.parseFloat(f.getStringProperty("value"))/rated))
                                    .title(f.getStringProperty("id"))
                            );
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(tag, "geojson rendered.");
        }
    }


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
            // if the original location is not null, then calculate the distance between the changed location and
            // original location, with cumulative sum of distance d
            if (originLocation != null){
                d = d + originLocation.distanceTo(location);
            }
            originLocation = location;
            setCameraPosition(location);

            // get features from geojson string
            FeatureCollection featureCollection = FeatureCollection.fromJson(result);
            List<Feature> features = featureCollection.features();

            // calculate the distances between each feature and the current location and store them into an array
            Float[] distanceInMeters = new Float[features.size()];
            for (int i = 0; i < features.size(); i++){
                Location loc = new Location("");
                loc.setLatitude(((Point) features.get(i).geometry()).latitude());
                loc.setLongitude(((Point) features.get(i).geometry()).longitude());
                if(originLocation != null){
                    distanceInMeters[i] = originLocation.distanceTo(loc);
                }
            }

            // get the diatance array which is not sorted
            Float[] disnotsort = distanceInMeters;

            // sort the array which store the distances
            Arrays.sort(distanceInMeters, new Comparator<Float>() {
                @Override
                public int compare(Float o1, Float o2) {
                    if (o1 == null && o2 == null) {
                        return 0;
                    }
                    if (o1 == null) {
                        return 1;
                    }
                    if (o2 == null) {
                        return -1;
                    }
                    return o1.compareTo(o2);
                }});

            // get the currency rates from geojson file
            JSONObject myObject = null;
            try {
                myObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject json_rates = null;
            try {
                json_rates = myObject.getJSONObject("rates");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String rS = null;
            try {
                rS = json_rates.get("SHIL").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String rP = null;
            try {
                rP = json_rates.get("PENY").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String rQ = null;
            try {
                rQ = json_rates.get("QUID").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String rD = null;
            try {
                rD = json_rates.get("QUID").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            float rateq = Float.parseFloat(rQ);
            float rates = Float.parseFloat(rS);
            float ratep = Float.parseFloat(rP);
            float rated = Float.parseFloat(rD);

            if (userEmail != null) {
                Log.d(t,"userEmial is not null");

                // after sorting the distance array, the minimum distance should be at the first position
                // get the index of closest marker
                // if user is close enough to a marker, then the value of coins is added to variable coinz
                if (distanceInMeters[0] != null && distanceInMeters[0] <= 25) {
                    Float min = distanceInMeters[0];
                    String mins = Float.toString(min);
                    Log.d(tag,mins);
                    int minindex = findIndex(disnotsort,min);
                    String mini = Integer.toString(minindex);
                    Log.d(tag,mini);
                    if (features.get(minindex).getStringProperty("currency").equals("QUID")) {
                        String coins = features.get(minindex).getStringProperty("value");
                        coinz = coinz + Float.parseFloat(coins)/rateq;
                        Toast.makeText(getApplicationContext(),
                                "Coins QUID met!",
                                Toast.LENGTH_LONG).show();
                    }
                    else if(features.get(minindex).getStringProperty("currency").equals("SHIL")){
                        String coins = features.get(minindex).getStringProperty("value");
                        coinz = coinz + Float.parseFloat(coins)/rates;
                        Toast.makeText(getApplicationContext(),
                                "Coins SHIL met!",
                                Toast.LENGTH_LONG).show();
                    }else if (features.get(minindex).getStringProperty("currency").equals("PENY")){
                        String coins = features.get(minindex).getStringProperty("value");
                        coinz = coinz + Float.parseFloat(coins)/ratep;
                        Toast.makeText(getApplicationContext(),
                                "Coins PENY met!",
                                Toast.LENGTH_LONG).show();
                    }else if (features.get(minindex).getStringProperty("currency").equals("DOLR")){
                        String coins = features.get(minindex).getStringProperty("value");
                        coinz = coinz + Float.parseFloat(coins)/rated;
                        Toast.makeText(getApplicationContext(),
                                "Coins DOLR met!",
                                Toast.LENGTH_LONG).show();
                    }
                    // once coins collected, same coin cannot be collected again
                    features.remove(minindex);
                }
            }else{Log.d(t,"user is null");}
        }
    }

    // this method is just for finding the index of a given value in an array
    private int findIndex(Float[] array, Float value){
        int index = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i].equals(value)){
                index = i;
            }
        }
        return index;
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
            Toast.makeText(getApplicationContext(),
                    "Please grant the permission!",
                    Toast.LENGTH_LONG).show();
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


//        Float precoin = 0f;
//        Map<String, Object> preuser = firestoreBank.get().getResult().getData();
//        if(preuser != null && preuser.get(userEmail) != null){
//           precoin = (Float) preuser.get(userEmail);
//            if (coinz > 25){
//                Float spare = coinz - 25;
//                coinz = 25 + precoin;
//                userMap.put(userEmail, coinz);
//                firestoreBank.update(userMap);
//                Toast.makeText(getApplicationContext(),
//                        "Coins reach maximum, transfer " + spare + "to your friend! Distance walked: " + d + "meters",
//                        Toast.LENGTH_LONG).show();
//            }else{
//                coinz = coinz + precoin;
//                userMap.put(userEmail, coinz);
//                firestoreBank.update(userMap);
//                Toast.makeText(getApplicationContext(),
//                        coinz + " Gold coins collected, distance walked: " + d + "meters!",
//                        Toast.LENGTH_LONG).show();
//            }
//           Log.d(t,precoin.toString());
//        }else{


            // check if the number of coins reaches 25,
            // if 25 is reached, then put 25 into database and inform user to transfer how much amount to other user
            // if not reach 25, then just store the coinz amount,
            // both situations shows how much distances users have walked while they're using the main activity with the 'Toast'.
            if (coinz > 25){
                Float spare = coinz - 25;
                userMap.put(userEmail, Float.parseFloat("25"));
                firestoreBank.update(userMap);
                Toast.makeText(getApplicationContext(),
                        "Coins reach maximum, transfer " + spare + "to your friend! Distance walked: " + d + "meters",
                        Toast.LENGTH_LONG).show();
            }else{
                userMap.put(userEmail, coinz);
                firestoreBank.update(userMap);
                Toast.makeText(getApplicationContext(),
                        coinz + " Gold coins collected, distance walked: " + d + "meters!",
                        Toast.LENGTH_LONG).show();
            }
        //}
    }



    // the following class is just the class given in the lecture with readStream filled in.
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

    public void Setting(View view){
        Intent startNewActivity = new Intent(this,Setting.class);
        startActivity(startNewActivity);
    }

}


