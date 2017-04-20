package geozombie.bboybboy.com.geozombie.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;

import geozombie.bboybboy.com.geozombie.eventbus.Events;

public class Utils {
    public static final int MY_PERMISSIONS_REQUEST_WIFI = 11;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public static void checkWifiPermission(Activity activity) {
        if (activity == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_WIFI);
            }
        } else {
            EventBus.getDefault().post(new Events.WifiPermissionGranted());
        }
    }

    public static void checkLocationPermission(Activity activity) {
        if (activity == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Utils.MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            EventBus.getDefault().post(new Events.LocationPermissionGranted());
        }
    }
}
