package geozombie.bboybboy.com.geozombie.controller;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import geozombie.bboybboy.com.geozombie.R;
import geozombie.bboybboy.com.geozombie.settings.SettingsActivity;
import geozombie.bboybboy.com.geozombie.eventbus.Events;
import geozombie.bboybboy.com.geozombie.utils.SharedPrefsUtils;

import static com.google.android.gms.location.places.Places.GEO_DATA_API;
import static com.google.android.gms.location.places.Places.PLACE_DETECTION_API;

public class MapController implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    public static final long MIN_TIME = 400;
    public static final float MIN_DISTANCE = 1000;

    private boolean isInit;
    private int groundColor;
    private float radius = 100;
    private LatLng centerControlledZone;

    private GoogleApiClient googleApiClient;

    private Circle mapCircle;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private EditText radiusEditText;

    public MapController(SettingsActivity activity) {
        initGoogleApiClient(activity);
        initUI(activity);
        restoreState(activity);
    }

    private void initUI(FragmentActivity activity) {
        groundColor = activity.getResources().getColor(R.color.red_transparent);

        mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        radiusEditText = (EditText) activity.findViewById(R.id.radius_chooser);
        radiusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (radiusEditText.getText() == null || radiusEditText.getText().toString().isEmpty()) {
                    removeZone();
                    return;
                }
                radius = Float.parseFloat(radiusEditText.getText().toString());
                drawZone();
            }
        });
    }

    private void restoreState(Context context) {
        centerControlledZone = SharedPrefsUtils.getLatLng(context);
        radius = SharedPrefsUtils.getRadius(context);
        radiusEditText.setText(String.valueOf(radius));
    }

    private void saveState(Context context) {
        SharedPrefsUtils.setLatLng(context, centerControlledZone);
        SharedPrefsUtils.setRadius(context, radius);
    }

    private void initGoogleApiClient(FragmentActivity activity) {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(GEO_DATA_API)
                .addApi(PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }

    public void initBy(LatLng latLng, boolean withAnimation) {
        if (isInit) return;

        isInit = true;
        centerControlledZone = latLng;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        if (withAnimation)
            map.animateCamera(cameraUpdate);
        else
            map.moveCamera(cameraUpdate);

        drawZone();
    }

    public void release(Context context) {
        saveState(context);
        googleApiClient.disconnect();
        googleApiClient = null;

        map = null;
    }

    public GoogleMap getMap() {
        return map;
    }

    private void removeZone() {
        if (mapCircle != null) {
            mapCircle.remove();
        }
    }

    private void drawZone() {
        if (map == null) return;
        removeZone();
        mapCircle = map.addCircle(new CircleOptions()
                .center(centerControlledZone)
                .radius(radius)
                .strokeColor(Color.RED)
                .fillColor(groundColor));
    }

    private void initMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                centerControlledZone = latLng;
                drawZone();
            }
        });
        if (centerControlledZone != null)
            initBy(centerControlledZone, false);

        EventBus.getDefault().post(new Events.LocationPermissionCheck());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
