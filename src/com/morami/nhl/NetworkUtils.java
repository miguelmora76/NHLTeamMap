package com.morami.nhl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by morami on 7/14/13.
 */
public class NetworkUtils {
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork){
            return activeNetwork.getType();
        }
        return -1;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        switch (conn){
            case ConnectivityManager.TYPE_WIFI:
                return context.getString(R.string.WIFI_ENABLED);
            case ConnectivityManager.TYPE_MOBILE:
                return context.getString(R.string.MOBILE_ENABLED);
            case -1:
            default:
                return context.getString(R.string.NOT_CONNECTED);
        }
    }
}
