package geozombie.bboybboy.com.geozombie;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import geozombie.bboybboy.com.geozombie.controller.MapController;
import geozombie.bboybboy.com.geozombie.controller.WifiController;
import geozombie.bboybboy.com.geozombie.j4f.AZombiesContainer;
import geozombie.bboybboy.com.geozombie.j4f.CircleZombiesContainer;
import geozombie.bboybboy.com.geozombie.j4f.WalkingZombiesContainer;
import geozombie.bboybboy.com.geozombie.settings.SettingsActivity;
import geozombie.bboybboy.com.geozombie.utils.SharedPrefsUtils;

public class MainActivity extends PermissionActivity {

    private final static int NUMBER_OF_ZOMBIES = 25;
    private ViewGroup rootView;
    private AZombiesContainer currentZombiesContainer;
    private boolean isZombieVisible;


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
        rootView = (ViewGroup) findViewById(R.id.activity_main);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUi();
        initGoogleApiClient();
        restoreLocation();
        checkWifi();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (wifiController != null) {
            wifiController.release();
        }
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
                checkWifi();
                updateWifiStatus(isFindWifi);
            }

            @Override
            public void onWifiSelected(String wifiSSID) {
                Log.d("wifi","ssid = " + wifiSSID);
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
            if (isZombieVisible) {
                onUserInCircle();
            }
            statusText.setText(R.string.in);
            statusText.setTextColor(ContextCompat.getColor(this, R.color.green));
        } else {
            if (isZombieVisible) {
                onUserOutOfCircle();
            }
            statusText.setText(R.string.out);
            statusText.setTextColor(Color.RED);
        }
    }

    private void checkWifi() {
        if (!wifiController.checkWifiIsEnabled()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.alert_title);
            dialog.setMessage(R.string.enable_wifi_message);
            dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
            return;
        }
        String connectedSSID = wifiController.getConnectedSSID();
        String selectedSSID = SharedPrefsUtils.getWifiSSID(MainActivity.this);
        if (selectedSSID != null) {
            updateStatusText(selectedSSID.equals(connectedSSID));
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
        findViewById(R.id.zombies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callZombies();
            }
        });

        distanceStatus = (TextView) findViewById(R.id.distance_status);
    }

    private void callZombies() {
        isZombieVisible = true;
        rootView.post(new Runnable() {
            @Override
            public void run() {
                cancelPreviousZombies();
                onStatusUnknown();
            }
        });
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onLocationPermissionGranted() {
        initWifi();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocation();
    }


    private void onUserInCircle() {
        updateZombiesContainer(new CircleZombiesContainer(rootView, 2 * NUMBER_OF_ZOMBIES, R.mipmap.inside));
    }

    private void onUserConnectedToProperWifi() {
        updateZombiesContainer(new CircleZombiesContainer(rootView, 2 * NUMBER_OF_ZOMBIES, R.mipmap.wifi));
    }

    private void onUserOutOfCircle() {
        updateZombiesContainer(new CircleZombiesContainer(rootView, 2 * NUMBER_OF_ZOMBIES, R.mipmap.outside));
    }

    private void onStatusUnknown() {
        updateZombiesContainer(new WalkingZombiesContainer(rootView, NUMBER_OF_ZOMBIES));
    }

    private void updateZombiesContainer(AZombiesContainer newZombiesContainer) {
        cancelPreviousZombies();
        currentZombiesContainer = newZombiesContainer;
        currentZombiesContainer.populateZombies();
    }

    private void cancelPreviousZombies() {
        if (currentZombiesContainer == null) return;
            currentZombiesContainer.cancel();
    }
}
