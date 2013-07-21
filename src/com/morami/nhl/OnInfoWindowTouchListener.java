package com.morami.nhl;

/**
 * Code borrowed from this post on stackoverflow.com:
 * http://stackoverflow.com/questions/14123243/google-maps-api-v2-custom-infowindow-like-in-original-android-google-maps/15040761#15040761
 * Created by morami on 7/6/13.
 */
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.gms.maps.model.Marker;

public abstract class OnInfoWindowTouchListener implements OnTouchListener {

    private final View view;
    private Marker marker;
    private boolean pressed = false;
    private double impactPoint;
    private double distance;

    public OnInfoWindowTouchListener(View view, Marker marker) {
        this.view = view;
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                impactPoint = event.getX();
                startPress();
                break;

            // We need to delay releasing of the view a little so it shows the pressed state on the screen
            case MotionEvent.ACTION_UP:
                distance = impactPoint - event.getX();
                endPress();
                break;

            case MotionEvent.ACTION_CANCEL:
                endPress();
                break;
            default:
                break;
        }
        return false;
    }

    private void startPress() {
        if (!pressed) {
            pressed = true;
        }
    }

    private void endPress() {
        if (pressed) {
            if (distance == 0.0){
                this.pressed = false;
                onTouch(view, marker);
            }
        }
    }

    protected abstract void onTouch(View v, Marker marker);
}