package geozombie.bboybboy.com.geozombie;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import geozombie.bboybboy.com.geozombie.eventbus.Events;
import geozombie.bboybboy.com.geozombie.utils.Utils;

public abstract class PermissionActivity extends AppCompatActivity {

    protected boolean locationPermissionGranted = false;
    private boolean askedAboutLocationMannerly = false;

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

    protected abstract void onLocationPermissionGranted();
}
