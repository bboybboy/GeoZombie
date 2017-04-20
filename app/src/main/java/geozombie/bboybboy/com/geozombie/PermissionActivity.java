package geozombie.bboybboy.com.geozombie;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import geozombie.bboybboy.com.geozombie.eventbus.Events;
import geozombie.bboybboy.com.geozombie.utils.Utils;

public class PermissionActivity extends AppCompatActivity{

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_WIFI: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    EventBus.getDefault().post(new Events.WifiPermissionGranted());
                }
                return;
            }
        }
    }
}
