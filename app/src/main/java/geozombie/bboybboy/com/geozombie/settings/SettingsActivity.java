package geozombie.bboybboy.com.geozombie.settings;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import geozombie.bboybboy.com.geozombie.PermissionActivity;
import geozombie.bboybboy.com.geozombie.R;
import geozombie.bboybboy.com.geozombie.controller.MapController;
import geozombie.bboybboy.com.geozombie.eventbus.Events;
import geozombie.bboybboy.com.geozombie.utils.Utils;

public class SettingsActivity extends PermissionActivity{

    private MapController mapController;
    private WifiPresenter wifiPresenter;

    private EventBus bus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initGoogleApiClient();
        mapController = new MapController(this);
        wifiPresenter=new WifiPresenter(this);
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapController.release();
        wifiPresenter.release();
        mapController = null;
        bus.unregister(this);
    }

    @Override
    protected void onLocationPermissionGranted() {
        updateMap();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.LocationPermissionCheck event) {
        Utils.checkLocationPermission(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.LocationPermissionGranted event) {
        locationPermissionGranted = true;
        updateMap();
    }

    @SuppressWarnings({"MissingPermission"})
    private void updateMap() {
        if (mapController.getMap() == null) {
            return;
        }

        GoogleMap googleMap = mapController.getMap();
        if (locationPermissionGranted) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            LocationRequest locationRequest = new LocationRequest()
                    .setInterval(MapController.MIN_TIME)
                    .setSmallestDisplacement(MapController.MIN_DISTANCE);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        } else {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mapController.initBy(new LatLng(location.getLatitude(), location.getLongitude()), true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mapController.initMap();
    }
}
