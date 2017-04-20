package geozombie.bboybboy.com.geozombie;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import geozombie.bboybboy.com.geozombie.eventbus.Events;
import geozombie.bboybboy.com.geozombie.utils.Utils;

import static com.google.android.gms.location.places.Places.GEO_DATA_API;
import static com.google.android.gms.location.places.Places.PLACE_DETECTION_API;

public abstract class PermissionActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    protected boolean locationPermissionGranted = false;
    protected GoogleApiClient googleApiClient;

    protected void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(GEO_DATA_API)
                .addApi(PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }

    protected void releaseGoogleApiClient() {
        if (googleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.stopAutoManage(this);
        googleApiClient.disconnect();
        googleApiClient = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_WIFI: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    EventBus.getDefault().post(new Events.WifiPermissionGranted());
                }else {
                    onDeclinePermission();
                }
                break;
            }
            case Utils.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    onLocationPermissionGranted();
                }else {
                    onDeclinePermission();
                }
                break;
            }
        }
    }

    private void onDeclinePermission() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                this);
        builderSingle.setCancelable(false);
        builderSingle.setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                finish();
            }
        });
        builderSingle.setTitle(R.string.alert_title);
        builderSingle.setMessage(R.string.alert_message);
        builderSingle.show();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    protected abstract void onLocationPermissionGranted();
}
