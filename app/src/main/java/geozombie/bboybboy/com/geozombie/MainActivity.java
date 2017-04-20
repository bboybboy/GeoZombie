package geozombie.bboybboy.com.geozombie;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import geozombie.bboybboy.com.geozombie.controller.WifiController;
import geozombie.bboybboy.com.geozombie.settings.SettingsActivity;
import geozombie.bboybboy.com.geozombie.utils.SharedPrefsUtils;

public class MainActivity extends PermissionActivity {
    private WifiController wifiController;
    private TextView statusText;
    private TextView statusWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
        initWifi();
    }

    @Override
    protected void onStop() {
        super.onStop();
        wifiController.release();
        wifiController=null;
    }

    private  void updateUi(){
        statusText.setText(R.string.out);
        statusText.setTextColor(Color.RED);
        statusWifi.setTextColor(Color.DKGRAY);
        String wifiName = SharedPrefsUtils.getWifiSSID(this);
        if (wifiName == null) {
            wifiName = getString(R.string.wifi_not_selected);
        }
        statusWifi.setText(wifiName);
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

    private void updateWifiStatus(boolean isFindWifi) {
        if (isFindWifi) {
            statusWifi.setTextColor(ContextCompat.getColor(this, R.color.green));
        } else {
            statusWifi.setTextColor(Color.DKGRAY);
        }
    }

    private void updateStatusText(boolean isZone) {
        if (isZone) {
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
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onLocationPermissionGranted() {
        //nothing to do here
    }
}
