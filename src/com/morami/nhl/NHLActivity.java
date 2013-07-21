/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.morami.nhl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
/**
 * This shows how to create a simple activity with a map and a marker on the map.
 * <p>
 * Notice how we deal with the possibility that the Google Play services APK is not
 * installed/enabled/updated on a user's device.
 */
public class NHLActivity extends Activity implements
        LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        OnMenuItemClickListener{

    private MenuManager menuManager;
    private TeamMapFragment mapFrag;
    private boolean isZoomedIn = false;
    private float oldZoom = 0.0f;

    private LocationClient mLocationClient;

    // These settings are the same as the settings for the map. They will in fact give you updates at
    // the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nhl_map);

        mapFrag = (TeamMapFragment)getFragmentManager().findFragmentById(R.id.map);
}

    @Override
    protected void onStart() {
        super.onStart();

        if (mLocationClient != null && !mLocationClient.isConnected() && !mLocationClient.isConnecting()){
            mLocationClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (mLocationClient != null){
            mLocationClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK){
            // Ask the user if they want to exit the app
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setMessage("Do you want to quit the NHL Team Map app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Stop the activity
                            NHLActivity.this.finish();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            dialog.cancel();
                        }
                    }).show();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        menuManager = new MenuManager(this, menu);
        return true;
    }

//    private boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
//            return true;
//        }
//        return false;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menuManager.onOptionsItemSelected(item);
    }

    public TeamMapFragment getMapFrag(){
        return mapFrag;
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.d(NHLActivity.class.getSimpleName(), "Location changed to: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationClient.requestLocationUpdates(REQUEST, this);  // Location listener
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(NHLActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(NHLActivity.this, "Failed connection", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            // If any of the 4 menu items get selected, set the map type
            // otherwise, the prefs menu item click will start the
            // preferences activity
            case R.id.map_type_normal_menu_item:
            case R.id.map_type_hybrid_menu_item:
            case R.id.map_type_satellite_menu_item:
            case R.id.map_type_terrain_menu_item:
                getMapFrag().getMap().setMapType(menuManager.getMapTypeMenuItemAndGoogleConstantHashMap().get(item));
                item.setChecked(true);
                return true;
            case R.id.all_teams_item:
                getMapFrag().getMap().clear();

                getMapFrag().getMarkerManager().filterTeamsOnMap(false, false, null);
                item.setChecked(true);
                return true;
            case R.id.division_a_item:
            case R.id.division_b_item:
            case R.id.division_c_item:
            case R.id.division_d_item:
                getMapFrag().getMap().clear();

                getMapFrag().getMarkerManager().filterTeamsOnMap(true, false, item.getTitle().toString());
                item.setChecked(true);
                return true;
            case R.id.eastern_conf_item:
            case R.id.western_conf_item:
                getMapFrag().getMap().clear();
                getMapFrag().getMarkerManager().filterTeamsOnMap(false, true, item.getTitle().toString());
                item.setChecked(true);
                return true;
            default:
                return true;
        }
    }
/*
    private void checkMarkers(GoogleMap map){
        Projection projection = map.getProjection();
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        HashMap<Marker, Point> points = new HashMap<Marker, Point>();
        for (Marker marker : teamMarkers) {
            if (bounds.contains(marker.getPosition())) {
                points.put(marker, projection.toScreenLocation(marker.getPosition()));
                marker.setVisible(false);
            }
        }
        CheckMarkersTask checkMarkersTask = new CheckMarkersTask();
        checkMarkersTask.execute(points);
    }

    public void checkForClustering(CameraPosition cameraPosition) {
        if (cameraPosition.zoom != oldZoom){
            checkMarkers(teamMap);
        }
        oldZoom = cameraPosition.zoom;
    }

    private class CheckMarkersTask extends AsyncTask<HashMap<Marker, Point>, Void, HashMap<Point, ArrayList<Marker>>> {
        private double findDistance(float x1, float y1, float x2, float y2) {
            return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
        }

        @Override
        protected HashMap<Point, ArrayList<Marker>> doInBackground(HashMap<Marker, Point>... hashMaps) {
            HashMap<Point, ArrayList<Marker>> clusters = new HashMap<Point, ArrayList<Marker>>();
            HashMap<Marker, Point> points = hashMaps[0];
            boolean wasClustered;
            for (Marker marker : points.keySet()) {
                Point point = points.get(marker);
                wasClustered = false;
                for (Point existingPoint : clusters.keySet()) {
                    if (findDistance(point.x, point.y, existingPoint.x, existingPoint.y) < 25) {
                        wasClustered = true;
                        clusters.get(existingPoint).add(marker);
                        break;
                    }
                }
                if (!wasClustered) {
                    ArrayList<Marker> markersForPoint = new ArrayList<Marker>();
                    markersForPoint.add(marker);
                    clusters.put(point, markersForPoint);
                }
            }
            return clusters;
        }

        @Override
        protected void onPostExecute(HashMap<Point, ArrayList<Marker>> clusters) {
            for (Point point : clusters.keySet()) {
                ArrayList<Marker> markersForPoint = clusters.get(point);
                Marker mainMarker = markersForPoint.get(0);
                mainMarker.setTitle(Integer.toString(markersForPoint.size()));
                mainMarker.setVisible(true);
            }
        }
    }*/
}
