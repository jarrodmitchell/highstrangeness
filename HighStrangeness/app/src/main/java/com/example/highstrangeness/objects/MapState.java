package com.example.highstrangeness.objects;

import com.google.android.gms.maps.model.CameraPosition;

import java.util.Map;

public class MapState {
    public static MapState savedState = null;

    CameraPosition cameraPosition;
    float maxZoom;
    float minZoom;
    int mapType;

    public MapState(CameraPosition cameraPosition, float maxZoom, float minZoom, int mapType) {
        this.cameraPosition = cameraPosition;
        this.maxZoom = maxZoom;
        this.minZoom = minZoom;
        this.mapType = mapType;
    }

    public CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public float getMinZoom() {
        return minZoom;
    }

    public int getMapType() {
        return mapType;
    }
}
