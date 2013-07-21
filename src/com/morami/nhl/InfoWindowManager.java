package com.morami.nhl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by morami on 7/20/13.
 */
public class InfoWindowManager implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener{
    private NHLActivity nhlActivity;
    private TeamMapFragment mapFragment;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView capacityView;
    private TextView addressView;
    private TextView arenaView;
    private ImageButton infoButton;
    private OnInfoWindowTouchListener infoButtonListener;

    public InfoWindowManager(Context context){
        nhlActivity = (NHLActivity)context;
        mapFragment = nhlActivity.getMapFrag();

        this.infoWindow = (ViewGroup)nhlActivity.getLayoutInflater().inflate(R.layout.team_info, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        this.capacityView = (TextView)infoWindow.findViewById(R.id.capacity);
        this.addressView = (TextView)infoWindow.findViewById(R.id.address);
        this.arenaView = (TextView)infoWindow.findViewById(R.id.arena);
        this.infoButton = (ImageButton)infoWindow.findViewById(R.id.team_logo);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        final NHLTeam team = mapFragment.getMarkerManager().getTeamHashMap().get(marker.getTitle());

        if (team != null){
            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if the user clicks on the image, it will
                    // trigger a browser to come up with the
                    // team's website.
                    Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                    myWebLink.setData(Uri.parse(team.getWebsite()));
                    nhlActivity.startActivity(myWebLink);
                }
            });

            // Setting custom OnTouchListener which deals with the pressed state
            // so it shows up

            this.infoButton.setOnTouchListener(new OnInfoWindowTouchListener(infoButton, marker) {
                @Override
                protected void onTouch(View v, Marker marker) {
                    // Here we can perform some action triggered after clicking the button
                    Toast.makeText(nhlActivity, "Going to the " + marker.getTitle() + " website...", Toast.LENGTH_SHORT).show();
                }
            });

            infoButton.setBackgroundResource(team.getPngID());
            infoTitle.setText(team.getName());
            arenaView.setText("Arena: " + team.getArenaName());
            addressView.setText(team.getAddress());
            capacityView.setText("Capacity: " + String.valueOf(team.getCapacity()));


        }
        // We must call this to set the current marker and infoWindow references
        // to the MapWrapperLayout
        mapFragment.getMapWrapperLayout().setMarkerWithInfoWindow(marker, infoWindow);
        return infoWindow;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        mapFragment.getMarkerManager().clickedInfoWindow(marker);
    }
}
