package com.morami.nhl;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;

/**
 * Created by morami on 7/12/13.
 */
public class MenuManager {
    private NHLActivity context;
    private Menu menu;
    private HashMap<MenuItem, Integer> mapTypeMenuItemAndGoogleConstantHashMap;
    private HashMap<MenuItem, Integer> confTypeHashMap;
    private HashMap<MenuItem, Integer> divTypeHashMap;

    public MenuManager(Context context, Menu menu) {
        this.context = (NHLActivity) context;
        this.menu = menu;
        init();
    }

    private void init() {
        initMapTypeMenuItem();
        initFilterMenuItems();
    }

    private void initMapTypeMenuItem() {
        mapTypeMenuItemAndGoogleConstantHashMap = new HashMap<MenuItem, Integer>();

        mapTypeMenuItemAndGoogleConstantHashMap.put(menu.findItem(R.id.map_type_normal_menu_item), GoogleMap.MAP_TYPE_NORMAL);
        mapTypeMenuItemAndGoogleConstantHashMap.put(menu.findItem(R.id.map_type_satellite_menu_item), GoogleMap.MAP_TYPE_SATELLITE);
        mapTypeMenuItemAndGoogleConstantHashMap.put(menu.findItem(R.id.map_type_terrain_menu_item), GoogleMap.MAP_TYPE_TERRAIN);
        mapTypeMenuItemAndGoogleConstantHashMap.put(menu.findItem(R.id.map_type_hybrid_menu_item), GoogleMap.MAP_TYPE_HYBRID);
        for (MenuItem item : mapTypeMenuItemAndGoogleConstantHashMap.keySet()) {
            item.setOnMenuItemClickListener(context);
        }
    }

    private void initFilterMenuItems() {
        confTypeHashMap = new HashMap<MenuItem, Integer>();

        confTypeHashMap.put(menu.findItem(R.id.all_teams_item), R.string.ALL_TEAMS);
        confTypeHashMap.put(menu.findItem(R.id.eastern_conf_item), R.string.EASTERN_CONFERENCE);
        confTypeHashMap.put(menu.findItem(R.id.western_conf_item), R.string.WESTERN_CONFERENCE);

        for (MenuItem item : confTypeHashMap.keySet()) {
            item.setOnMenuItemClickListener(context);
        }

        divTypeHashMap = new HashMap<MenuItem, Integer>();

        divTypeHashMap.put(menu.findItem(R.id.division_a_item), R.string.DIVISION_A);
        divTypeHashMap.put(menu.findItem(R.id.division_b_item), R.string.DIVISION_B);
        divTypeHashMap.put(menu.findItem(R.id.division_c_item), R.string.DIVISION_C);
        divTypeHashMap.put(menu.findItem(R.id.division_d_item), R.string.DIVISION_D);
        for (MenuItem item : divTypeHashMap.keySet()) {
            item.setOnMenuItemClickListener(context);
        }
    }

    public HashMap<MenuItem, Integer> getMapTypeMenuItemAndGoogleConstantHashMap() {
        return mapTypeMenuItemAndGoogleConstantHashMap;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return true;
        }
    }

    public HashMap<MenuItem, Integer> getConfTypeHashMap() {
        return confTypeHashMap;
    }

    public HashMap<MenuItem, Integer> getDivTypeHashMap() {
        return divTypeHashMap;
    }
}
