package com.morami.nhl;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.MapFragment;

/**
 * Created by morami on 7/20/13.
 */
public class TeamMapFragment extends MapFragment
        implements MenuItem.OnMenuItemClickListener{
    private NHLActivity nhlActivity;
    private MarkerManager markerManager;
    private MapWrapperLayout mapWrapperLayout;
    private InfoWindowManager infoWindowManager;

    private static final int DEFAULT_MARKER_HEIGHT = 39;
    private static final int INFO_WINDOW_OFFSET = 20;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        nhlActivity = (NHLActivity)getActivity();

        initMap();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    public MarkerManager getMarkerManager() {
        return markerManager;
    }

    public MapWrapperLayout getMapWrapperLayout() {
        return mapWrapperLayout;
    }

    private void initMap(){
        mapWrapperLayout = (MapWrapperLayout)nhlActivity.findViewById(R.id.map_relative_layout);

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(getMap(), getPixelsFromDp(DEFAULT_MARKER_HEIGHT + INFO_WINDOW_OFFSET));
        // Check if we were successful in obtaining the map.

        infoWindowManager = new InfoWindowManager(nhlActivity);
        markerManager = new MarkerManager(nhlActivity, getMap());

        getMap().setInfoWindowAdapter(infoWindowManager);
        getMap().setOnInfoWindowClickListener(infoWindowManager);
        getMap().setOnMarkerClickListener(markerManager);

        //getMap().getUiSettings().setAllGesturesEnabled(true);
        getMap().getUiSettings().setZoomControlsEnabled(false);
        getMap().setMyLocationEnabled(true);
    }

    private int getPixelsFromDp(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
