package geozombie.bboybboy.com.geozombie;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import geozombie.bboybboy.com.geozombie.controller.MapController;
import geozombie.bboybboy.com.geozombie.controller.WifiController;
import geozombie.bboybboy.com.geozombie.settings.SettingsActivity;
import geozombie.bboybboy.com.geozombie.utils.SharedPrefsUtils;

public class MainActivity extends PermissionActivity {
    private WifiController wifiController;

    private TextView statusText;
    private TextView statusWifi;
    private TextView distanceStatus;

    private LatLng centerZone;
    private Location lastCurrentLocation;
    private float radius = 0;
    private boolean isZone;
    private int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUi();
        initWifi();
        initGoogleApiClient();
        restoreLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        wifiController.release();
        wifiController = null;
        releaseGoogleApiClient();
    }

    private void updateUi() {
        statusText.setText(R.string.out);
        statusText.setTextColor(Color.RED);
        statusWifi.setTextColor(Color.DKGRAY);
        String wifiName = SharedPrefsUtils.getWifiSSID(this);
        if (wifiName == null) {
            wifiName = getString(R.string.wifi_not_selected);
        }
        statusWifi.setText(wifiName);
    }

    private void restoreLocation() {
        centerZone = SharedPrefsUtils.getLatLng(this);
        radius = SharedPrefsUtils.getRadius(this);
        if (lastCurrentLocation == null || centerZone == null) return;
        int distance = (int) getDistance(lastCurrentLocation);
        distanceStatus.setText(String.valueOf(distance));
        updateStatusText(distance);
    }

    private void initWifi() {
        wifiController = new WifiController(this, new WifiController.onWifiActionListener() {
            @Override
            public void onStatusChange(boolean isFindWifi) {
                updateStatusText(isFindWifi);
                updateWifiStatus(isFindWifi);
            }

            @Override
            public void onWifiSelected(String wifiSSID) {

            }
        });
    }

    private void initLocation() {
        if (centerZone == null) return;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(MapController.MIN_TIME)
                .setSmallestDisplacement(MapController.MIN_DISTANCE)
                .setNumUpdates(300);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(final Location location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lastCurrentLocation = location;
                int distance = (int) getDistance(location);
                distanceStatus.setText(String.valueOf(distance));
                updateStatusText(distance);
            }
        });
    }

    private float getDistance(Location location) {
        Location centerZoneLocation = new Location("");
        centerZoneLocation.setLatitude(centerZone.latitude);
        centerZoneLocation.setLongitude(centerZone.longitude);
        return location.distanceTo(centerZoneLocation);
    }

    private void updateWifiStatus(boolean isFindWifi) {
        if (isFindWifi) {
            statusWifi.setTextColor(ContextCompat.getColor(this, R.color.green));
        } else {
            statusWifi.setTextColor(Color.DKGRAY);
        }
    }

    private void updateStatusText(boolean isZone) {
        this.isZone = isZone;
        checkConditions();
    }

    private void updateStatusText(int distance) {
        this.distance = distance;
        checkConditions();
    }

    private void checkConditions() {
        if ((distance < radius) || isZone) {
            statusText.setText(R.string.in);
            statusText.setTextColor(ContextCompat.getColor(this, R.color.green));
        } else {
            statusText.setText(R.string.out);
            statusText.setTextColor(Color.RED);
        }
    }

    private void initUI() {
        statusText = (TextView) findViewById(R.id.status_text);
        statusWifi = (TextView) findViewById(R.id.wifi_status);

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSettingsActivity();
            }
        });

        distanceStatus = (TextView) findViewById(R.id.distance_status);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onLocationPermissionGranted() {
        //nothing to do here
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocation();
    }
}
