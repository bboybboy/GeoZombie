package geozombie.bboybboy.com.geozombie;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.greysonparrelli.permiso.Permiso;
import com.greysonparrelli.permiso.PermisoActivity;

import android.support.annotation.NonNull;

import static com.google.android.gms.location.places.Places.GEO_DATA_API;
import static com.google.android.gms.location.places.Places.PLACE_DETECTION_API;

public abstract class PermissionActivity extends PermisoActivity implements LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    protected GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {
                    onLocationPermissionGranted();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PermissionActivity.this);
                    dialog.setCancelable(false);
                    dialog.setTitle(R.string.alert_title);
                    dialog.setMessage(R.string.alert_message);
                    dialog.setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog(getString(R.string.alert_message), getString(R.string.permission_dialog_text), null, callback);
            }
        }, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

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
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    protected abstract void onLocationPermissionGranted();
}
