package geozombie.bboybboy.com.geozombie.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import geozombie.bboybboy.com.geozombie.MapReadyListener;
import geozombie.bboybboy.com.geozombie.PermissionActivity;
import geozombie.bboybboy.com.geozombie.R;
import geozombie.bboybboy.com.geozombie.controller.MapController;

public class SettingsActivity extends PermissionActivity implements MapReadyListener {

    private MapController mapController;
    private WifiPresenter wifiPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initGoogleApiClient();
        mapController = new MapController(this);
        wifiPresenter = new WifiPresenter(this);

        mapController.setMapReadyListener(this);
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
    }

    @Override
    protected void onLocationPermissionGranted() {
        if (mapController != null && mapController.getMap() != null) {
            updateMap();
        }
    }

    private void updateMap() {
        if (mapController.getMap() == null) {
            return;
        }

        GoogleMap googleMap = mapController.getMap();
        //just to be sure
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            LocationRequest locationRequest = new LocationRequest()
                    .setInterval(MapController.MIN_TIME)
                    .setSmallestDisplacement(MapController.MIN_DISTANCE);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mapController.initBy(new LatLng(location.getLatitude(), location.getLongitude()), true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mapController.initMap();
    }

    @Override
    public void onMapReady() {
        updateMap();
    }
}
