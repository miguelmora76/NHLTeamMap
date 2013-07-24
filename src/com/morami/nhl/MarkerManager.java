package com.morami.nhl;

import android.content.Context;
import android.content.res.TypedArray;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by morami on 7/20/13.
 */
public class MarkerManager implements GoogleMap.OnMarkerClickListener{
    private GoogleMap teamMap;
    private NHLActivity nhlActivity;
    private Map<String, NHLTeam> teamHashMap;
    private List<Marker> markers;
    private LatLng lastLocationCenter;
    private boolean isZoomedIn = false;

    public MarkerManager(Context context, GoogleMap map){
        nhlActivity = (NHLActivity)context;
        teamMap = map;
        teamHashMap = Collections.synchronizedMap(new HashMap<String, NHLTeam>());
        markers = new ArrayList<Marker>();
        setupTeamMarkers();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void addMarker(NHLTeam team) {
        String snippet = team.getArenaName() + "\n" + team.getAddress()
                + "\n" + "Capacity: " + team.getCapacity();
        //Log.d("NHLActivity", "Setting icon for division: " + team.getDivision());
        markers.add(teamMap.addMarker(new MarkerOptions().position(new LatLng(team.getLatitude(), team.getLongitude()))
                .title(team.getName()).snippet(snippet).icon(getBitmapDescriptorFromDivision(team.getDivision()))));
    }

    public void filterTeamsOnMap(boolean divFilter, boolean confFilter, String filterStr){
        if (markers == null){
            markers = new ArrayList<Marker>();
        }else{
            markers.clear();
        }

        Set<String> keys = getTeamHashMap().keySet();
        for (String teamName : keys){
            NHLTeam team = getTeamHashMap().get(teamName);
            if (!divFilter && !confFilter){
                // Add this marker, because it's the 'all' option
                addMarker(team);
            }else{
                // if divFilter is true, it means we're filtering by division
                if (divFilter && team.getDivision().equals(filterStr)){
                    addMarker(team);
                }else if (confFilter && team.getConference().equals(filterStr)){
                    // if conf filter is true, it means we're filtering by conference
                    addMarker(team);
                }
            }
        }

        //checkForClustering(teamMap.getCameraPosition());
    }

    public void clickedInfoWindow(Marker marker){
        float zoomLevel = isZoomedIn == true ? 0 : 17.0f;

        teamMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoomLevel));

        isZoomedIn = !isZoomedIn;
    }

    private BitmapDescriptor getBitmapDescriptorFromDivision(String divName){
        if (divName.equals(nhlActivity.getString(R.string.DIVISION_A))){
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
        }else if (divName.equals(nhlActivity.getString(R.string.DIVISION_B))){
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        }else if(divName.equals(nhlActivity.getString(R.string.DIVISION_C))){
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        }else if (divName.equals(nhlActivity.getString(R.string.DIVISION_D))){
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        }else{
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        }
    }

    private void loadNHLTeams(){
        // Load the JSON for the NHL teams, and create Markers from the lat/long values of the arena
        // information
        InputStream inputStream = nhlActivity.getResources().openRawResource(R.raw.teams);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            //Log.e("loadNHLTeams", e.getMessage());
        }

        try {
            // Parse the data into json object to get original data in form of json.
            JSONObject jObject = new JSONObject(
                    byteArrayOutputStream.toString());

            TypedArray teamLogosTypedArray = nhlActivity.getResources().obtainTypedArray(R.array.logos_names_array);

            for (int i = 0; i < teamLogosTypedArray.length(); i++){
                String teamName = teamLogosTypedArray.getString(i);
                JSONObject data = jObject.getJSONObject(teamName);

                NHLTeam nhlTeam = new NHLTeam();
                nhlTeam.setName(teamName);

                nhlTeam.setArenaName(data.getString("arena"));

                nhlTeam.setLatitude(data.getDouble("lat"));
                nhlTeam.setLongitude(data.getDouble("long"));
                nhlTeam.setAddress(data.getString("address"));
                nhlTeam.setCapacity(data.getInt("capacity"));
                nhlTeam.setWebsite(data.getString("website"));
                nhlTeam.setAbbr(data.getString("abbr"));
                nhlTeam.setDivision(data.getString("div"));
                nhlTeam.setConference(data.getString("conf"));

                // Grab the resource id based on the 'abbr' value, which matches the name
                // of the associated png file
                nhlTeam.setPngID(nhlActivity.getResources().getIdentifier(nhlTeam.getAbbr(), "drawable",
                        nhlActivity.getPackageName()));

                getTeamHashMap().put(teamName, nhlTeam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p>
     * This should only be called once and when we are sure that {@link #teamMap}.
     */
    private void setupTeamMarkers() {
        loadNHLTeams();
        // Center of the US
        double latitude = 39.8282d;
        double longitude = -98.5795d;

        lastLocationCenter = new LatLng(latitude, longitude);
        teamMap.animateCamera(CameraUpdateFactory.newLatLng(lastLocationCenter));

        // Add all the markers for the NHL teams
        filterTeamsOnMap(false, false, null);
    }

    public Map<String, NHLTeam> getTeamHashMap() {
        return teamHashMap;
    }
}
