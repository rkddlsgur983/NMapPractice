package com.example.damoyeo;

import android.content.Intent;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;

public class NMapFragmentPresenter {
    private final String TAG = "NMapViewer";
    private NMapFragment view;
    private NMapClass mapClass;

    public NMapFragmentPresenter(NMapFragment view, NMapClass nMapClass) {
        this.view = view;
        this.mapClass = nMapClass;
    }

    public void init(){
        // Fragment에 포함된 NMapView 객체 찾기
        mapClass.setMapView(findMapView(view.getView()));
        if (mapClass.getMapView() == null) {
            throw new IllegalArgumentException("NMapFragment dose not have an instance of NMapView.");
        }

        // NMapView mapView = (NMapView)getView().findViewById(R.id.mapView);
        mapClass.getMapView().setClientId(view.getResources().getString(R.string.NAVER_API_KEY));// 클라이언트 아이디 설정

        // initialize map view
        mapClass.getMapView().setClickable(true);

        // use built in zoom controls
        //nMapClass.getmMapView().setBuiltInZoomControls(true, null);

        // register listener for map state changes
        //nMapClass.getmMapView().setOnMapStateChangeListener(onMapViewStateChangeListener);
        //nMapClass.getmMapView().setOnMapViewTouchEventListener(onMapViewTouchEventListener);

        // NMapActivity를 상속하지 않는 경우에는 NMapView 객체 생성후 반드시 setupMapView()를 호출해야함.
        mapClass.getMapContext().setupMapView(mapClass.getMapView());

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
        mapClass.setMapController(mapClass.getMapView().getMapController());
        mapClass.getMapController().setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);     //Default Data

        mapClass.setMapResourceProvider(new NMapViewerResourceProvider(view.getContext()));
        mapClass.setMapOverlayManager(new NMapOverlayManager(view.getContext(), mapClass.getMapView(), mapClass.getMapResourceProvider()));

        // set data provider listener
        mapClass.getMapContext().setMapDataProviderListener(onDataProviderListener);

        // register callout overlay listener to customize it.
        mapClass.getMapOverlayManager().setOnCalloutOverlayListener(onCalloutOverlayListener);

        // location manager
        mapClass.setMapLocationManager(new NMapLocationManager(view.getContext()));;
        mapClass.getMapLocationManager().setOnLocationChangeListener(onMyLocationChangeListener);

        // compass manager
        mapClass.setMapCompassManager(new NMapCompassManager(view.getActivity()));

        // create my location overlay
        mapClass.setMyLocationOverlay(mapClass.getMapOverlayManager().createMyLocationOverlay(mapClass.getMapLocationManager(), mapClass.getMapCompassManager()));;
    }

    /**
     * Fragment에 포함된 NMapView 객체를 반환함
     */
    public NMapView findMapView(View v) {
        if (!(v instanceof ViewGroup)) {
            return null;
        }

        ViewGroup vg = (ViewGroup)v;
        if (vg instanceof NMapView) {
            return (NMapView)vg;
        }

        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (!(child instanceof ViewGroup)) {
                continue;
            }
            NMapView mapView = findMapView(child);
            if (mapView != null) {
                return mapView;
            }
        }
        return null;
    }

    /* Test Functions */
    public void startMyLocation() {
        if (mapClass.getMyLocationOverlay() != null) {
            if (!mapClass.getMapOverlayManager().hasOverlay(mapClass.getMyLocationOverlay())) {
                mapClass.getMapOverlayManager().addOverlay(mapClass.getMyLocationOverlay());
            }

            if (mapClass.getMapLocationManager().isMyLocationEnabled()) {

                if (!mapClass.getMapView().isAutoRotateEnabled()) {
                    mapClass.getMyLocationOverlay().setCompassHeadingVisible(true);

                    mapClass.getMapCompassManager().enableCompass();

                    mapClass.getMapView().setAutoRotateEnabled(true, false);

                    //mMapContainerView.requestLayout();
                } else {
                    stopMyLocation();
                }

                mapClass.getMapView().postInvalidate();
            } else {
                boolean isMyLocationEnabled = mapClass.getMapLocationManager().enableMyLocation(true);
                if (!isMyLocationEnabled) {
                    Toast.makeText(view.getContext(), "Please enable a My Location source in system settings",
                            Toast.LENGTH_LONG).show();

                    Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    view.startActivity(goToSettings);

                    return;
                }
            }
        }
    }

    public void stopMyLocation() {
        if (mapClass.getMyLocationOverlay() != null) {
            mapClass.getMapLocationManager().disableMyLocation();

            if (mapClass.getMapView().isAutoRotateEnabled()) {
                mapClass.getMyLocationOverlay().setCompassHeadingVisible(false);

                mapClass.getMapCompassManager().disableCompass();

                mapClass.getMapView().setAutoRotateEnabled(false, false);

                //mMapContainerView.requestLayout();
            }
        }
    }

    public void initLocation() {
        // 초기화
        if (mapClass.getMyLocationOverlay() != null) {
            stopMyLocation();
            mapClass.getMapOverlayManager().removeOverlay(mapClass.getMyLocationOverlay());
        }

        mapClass.getMapController().setMapViewMode(NMapView.VIEW_MODE_VECTOR);

        mapClass.getMapOverlayManager().clearOverlays();

        testPOIdataOverlay();
    }

    public void testPOIdataOverlay(){
        int markerId = NMapPOIflagType.PIN;

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(2, mapClass.getMapResourceProvider());
        poiData.beginPOIdata(2);
        poiData.addPOIitem(127.108099, 37.366034, "출발", markerId, 0).setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);
        poiData.addPOIitem(127.106279, 37.366380, "도착", markerId, 0);
        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mapClass.getMapOverlayManager().createPOIdataOverlay(poiData, null);
        // 해당 오버레이 객체에 포함된 전체 아이템이 화면에 표시되도록 지도 중심 및 축적 레벨을 변경하려면 아래와 같이 구현합니다.
        poiDataOverlay.showAllPOIdata(0);
        // 아이템의 선택 상태가 변경되거나 말풍선이 선택되는 경우를 처리하기 위하여 이벤트 리스너를 등록합니다.
        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

        poiDataOverlay.selectPOIitem(0, true);
    }

    // 경로 표시
    public void testPathDataOverlay() {
        // set path data points
        NMapPathData pathData = new NMapPathData(9);

        pathData.initPathData();
        pathData.addPathPoint(127.108099, 37.366034, NMapPathLineStyle.TYPE_SOLID);
        pathData.addPathPoint(127.108088, 37.366043, 0);
        pathData.addPathPoint(127.108079, 37.365619, 0);
        pathData.addPathPoint(127.107458, 37.365608, 0);
        pathData.addPathPoint(127.107232, 37.365608, 0);
        pathData.addPathPoint(127.106904, 37.365624, 0);
        pathData.addPathPoint(127.105933, 37.365621, NMapPathLineStyle.TYPE_DASH);
        pathData.addPathPoint(127.105929, 37.366378, 0);
        pathData.addPathPoint(127.106279, 37.366380, 0);
        pathData.endPathData();

        NMapPathDataOverlay pathDataOverlay = mapClass.getMapOverlayManager().createPathDataOverlay(pathData);

        // show all path data
        // 경로 전체보기
        pathDataOverlay.showAllPathData(0);
    }

    public void testPathPOIdataOverlay() {

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(4, mapClass.getMapResourceProvider(), true);
        poiData.beginPOIdata(4);
        poiData.addPOIitem(349652983, 149297368, "Pizza 124-456", NMapPOIflagType.FROM, null);
        poiData.addPOIitem(349652966, 149296906, null, NMapPOIflagType.NUMBER_BASE + 1, null);
        poiData.addPOIitem(349651062, 149296913, null, NMapPOIflagType.NUMBER_BASE + 999, null);
        poiData.addPOIitem(349651376, 149297750, "Pizza 000-999", NMapPOIflagType.TO, null);
        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mapClass.getMapOverlayManager().createPOIdataOverlay(poiData, null);

        // set event listener to the overlay
        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

    }

    // 지도 위 오버레이(마커) 아이템 위치 이동
    public void testFloatingPOIdataOverlay() {
        // Markers for POI item
        int marker1 = NMapPOIflagType.PIN;

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(1, mapClass.getMapResourceProvider());
        poiData.beginPOIdata(1);
        NMapPOIitem item = poiData.addPOIitem(null, "Touch & Drag to Move", marker1, 0);
        if (item != null) {
            // initialize location to the center of the map view.
            item.setPoint(mapClass.getMapController().getMapCenter());
            // set floating mode
            item.setFloatingMode(NMapPOIitem.FLOATING_TOUCH | NMapPOIitem.FLOATING_DRAG);
            // show right button on callout
            item.setRightButton(true);

            mapClass.setFloatingPOIitem(item);
        }
        poiData.endPOIdata();


        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mapClass.getMapOverlayManager().createPOIdataOverlay(poiData, null);
        if (poiDataOverlay != null) {
            poiDataOverlay.setOnFloatingItemChangeListener(onPOIdataFloatingItemChangeListener);

            // set event listener to the overlay
            poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

            poiDataOverlay.selectPOIitem(0, false);

            mapClass.setFloatingPOIdataOverlay(poiDataOverlay);
        }
    }

    /* NMapDataProvider Listener */
    private final com.nhn.android.maps.NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {
            //if (DEBUG) {
            Log.i(TAG, "onReverseGeocoderResponse: placeMark="
                    + ((placeMark != null) ? placeMark.toString() : null));
            //}

            if (errInfo != null) {
                Log.e(TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

                Toast.makeText(view.getContext(), errInfo.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            if (mapClass.getFloatingPOIitem() != null && mapClass.getFloatingPOIdataOverlay() != null) {
                mapClass.getFloatingPOIdataOverlay().deselectFocusedPOIitem();

                if (placeMark != null) {
                    mapClass.getFloatingPOIitem().setTitle(placeMark.toString());
                }
                mapClass.getFloatingPOIdataOverlay().selectPOIitemBy(mapClass.getFloatingPOIitem().getId(), false);
            }
        }
    };

    /* MyLocation Listener */
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
            if (mapClass.getMapController() != null) {
                mapClass.getMapController().animateTo(myLocation);
            }

            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
            // stop location updating
            //			Runnable runnable = new Runnable() {
            //				public void run() {
            //					stopMyLocation();
            //				}
            //			};
            //			runnable.run();

            Toast.makeText(view.getContext(), "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
            Toast.makeText(view.getContext(), "Your current location is unavailable area.", Toast.LENGTH_LONG).show();
            stopMyLocation();
        }
    };

    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {
        @Override
        public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
            if (nMapPOIitem != null) {
                Log.i(TAG, "onFocusChanged: " + nMapPOIitem.toString());
            } else {
                Log.i(TAG, "onFocusChanged: ");
            }
        }

        @Override
        public void onCalloutClick(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
            Toast.makeText(view.getContext(), "onCalloutClick: " + nMapPOIitem.getTitle(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "onFocusChanged: " + nMapPOIitem.toString());

        }
    };

    private final NMapPOIdataOverlay.OnFloatingItemChangeListener onPOIdataFloatingItemChangeListener = new NMapPOIdataOverlay.OnFloatingItemChangeListener() {
        @Override
        public void onPointChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            NGeoPoint point = item.getPoint();

            //if (DEBUG) {
            Log.i(TAG, "onPointChanged: point=" + point.toString());
            //}

            mapClass.getMapContext().findPlacemarkAtLocation(point.longitude, point.latitude);

            item.setTitle(null);

        }
    };

    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
            Log.e(TAG, "OnMapStateChangeListener onMapInitHandler : ");
            if (nMapError == null) { // success
                mapClass.getMapController().setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);
            } else { // fail
                Log.e(TAG, "onMapInitHandler: error=" + nMapError.toString());
            }
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {
            Log.e(TAG, "OnMapStateChangeListener onMapCenterChange : " + nGeoPoint.getLatitude() + " ㅡ  " + nGeoPoint.getLongitude());
        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {
            Log.e(TAG, "OnMapStateChangeListener onMapCenterChangeFine : ");
        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i) {
            Log.e(TAG, "OnMapStateChangeListener onZoomLevelChange : " + i);
        }

        @Override
        public void onAnimationStateChange(NMapView nMapView, int i, int i1) {
            Log.e(TAG, "OnMapStateChangeListener onAnimationStateChange : ");
        }
    };

    private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {
        @Override
        public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onLongPress : ");
        }

        @Override
        public void onLongPressCanceled(NMapView nMapView) {
            Log.e(TAG, "OnMapViewTouchEventListener onLongPressCanceled : ");
        }

        @Override
        public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onTouchDown : ");
        }

        @Override
        public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onTouchUp : ");
        }

        @Override
        public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {
            Log.e(TAG, "OnMapViewTouchEventListener onScroll : ");
        }

        @Override
        public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onSingleTapUp : ");
        }
    };

    // 말풍선 모양
    private NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {
        @Override
        public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay nMapOverlay, NMapOverlayItem nMapOverlayItem, Rect rect) {
            // use custom callout overlay
            return new NMapCalloutCustomOverlay(nMapOverlay, nMapOverlayItem, rect, mapClass.getMapResourceProvider());

            // set basic callout overlay
            //return new NMapCalloutBasicOverlay(nMapOverlay, nMapOverlayItem, rect);
        }
    };
}
