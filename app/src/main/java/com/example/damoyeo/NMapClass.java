package com.example.damoyeo;

import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

public class NMapClass {
    private NMapContext mapContext;
    private NMapController mapController;
    private NMapView mapView;

    private NMapResourceProvider mapResourceProvider;
    private NMapOverlayManager mapOverlayManager;

    private NMapPOIitem floatingPOIitem;
    private NMapPOIdataOverlay floatingPOIdataOverlay;

    private NMapMyLocationOverlay myLocationOverlay;
    private NMapLocationManager mapLocationManager;
    private NMapCompassManager mapCompassManager;

    public NMapClass() {    }

    public NMapContext getMapContext() {
        return mapContext;
    }

    public void setMapContext(NMapContext mapContext) {
        this.mapContext = mapContext;
    }

    public NMapController getMapController() {
        return mapController;
    }

    public void setMapController(NMapController mapController) {
        this.mapController = mapController;
    }

    public NMapView getMapView() {
        return mapView;
    }

    public void setMapView(NMapView mapView) {
        this.mapView = mapView;
    }

    public NMapResourceProvider getMapResourceProvider() {
        return mapResourceProvider;
    }

    public void setMapResourceProvider(NMapResourceProvider mapResourceProvider) {
        this.mapResourceProvider = mapResourceProvider;
    }

    public NMapOverlayManager getMapOverlayManager() {
        return mapOverlayManager;
    }

    public void setMapOverlayManager(NMapOverlayManager mapOverlayManager) {
        this.mapOverlayManager = mapOverlayManager;
    }

    public NMapPOIitem getFloatingPOIitem() {
        return floatingPOIitem;
    }

    public void setFloatingPOIitem(NMapPOIitem floatingPOIitem) {
        this.floatingPOIitem = floatingPOIitem;
    }

    public NMapPOIdataOverlay getFloatingPOIdataOverlay() {
        return floatingPOIdataOverlay;
    }

    public void setFloatingPOIdataOverlay(NMapPOIdataOverlay floatingPOIdataOverlay) {
        this.floatingPOIdataOverlay = floatingPOIdataOverlay;
    }

    public NMapMyLocationOverlay getMyLocationOverlay() {
        return myLocationOverlay;
    }

    public void setMyLocationOverlay(NMapMyLocationOverlay myLocationOverlay) {
        this.myLocationOverlay = myLocationOverlay;
    }

    public NMapLocationManager getMapLocationManager() {
        return mapLocationManager;
    }

    public void setMapLocationManager(NMapLocationManager mapLocationManager) {
        this.mapLocationManager = mapLocationManager;
    }

    public NMapCompassManager getMapCompassManager() {
        return mapCompassManager;
    }

    public void setMapCompassManager(NMapCompassManager mapCompassManager) {
        this.mapCompassManager = mapCompassManager;
    }
}
