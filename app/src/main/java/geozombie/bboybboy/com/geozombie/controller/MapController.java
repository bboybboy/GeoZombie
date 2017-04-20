package geozombie.bboybboy.com.geozombie.controller;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;


import geozombie.bboybboy.com.geozombie.MapReadyListener;
import geozombie.bboybboy.com.geozombie.R;
import geozombie.bboybboy.com.geozombie.settings.SettingsActivity;
import geozombie.bboybboy.com.geozombie.utils.SharedPrefsUtils;

public class MapController implements OnMapReadyCallback {

    public static final long MIN_TIME = 0;
    public static final float MIN_DISTANCE = 0;

    private boolean isInit;
    private int groundColor;
    private float radius = 100;
    private LatLng centerControlledZone;

    private Context context;
    private Circle mapCircle;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private EditText radiusEditText;

    private MapReadyListener listener;

    public MapController(SettingsActivity activity) {
        context = activity;
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
                SharedPrefsUtils.setRadius(context, radius);
                drawZone();
            }
        });
    }

    public void setMapReadyListener(MapReadyListener listener) {
        this.listener = listener;
    }

    private void restoreState(Context context) {
        centerControlledZone = SharedPrefsUtils.getLatLng(context);
        radius = SharedPrefsUtils.getRadius(context);
        radiusEditText.setText(String.valueOf(radius));
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

    public void release() {
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

    public void initMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (listener != null) {
            listener.onMapReady();
        }
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                centerControlledZone = latLng;
                drawZone();
                SharedPrefsUtils.setLatLng(context, centerControlledZone);
            }
        });
        if (centerControlledZone != null)
            initBy(centerControlledZone, false);
    }
}
