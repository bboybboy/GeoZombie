package geozombie.bboybboy.com.geozombie;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import geozombie.bboybboy.com.geozombie.controller.WifiController;
import geozombie.bboybboy.com.geozombie.settings.SettingsActivity;

public class MainActivity extends PermissionActivity {
    private WifiController wifiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSettingsActivity();
            }
        });

        final ImageView wifiTest = (ImageView) findViewById(R.id.wifiTest);
        wifiTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiController == null) {
                    wifiTest.setColorFilter(Color.RED);
                    wifiController = new WifiController(MainActivity.this, new WifiController.onWifiActionListener() {
                        @Override
                        public void onStatusChange(boolean isFindWifi) {
                            if (isFindWifi) {
                                wifiTest.setColorFilter(Color.GREEN);
                            } else {
                                wifiTest.setColorFilter(Color.RED);
                            }
                        }

                        @Override
                        public void onWifiSelected(String wifiSSID) {
                            Toast.makeText(MainActivity.this, "Selected " + wifiSSID, Toast.LENGTH_SHORT).show();
                        }
                    });
                    wifiController.showAvailableWifiDialog();
                } else {
                    wifiTest.setColorFilter(Color.GRAY);
                    wifiController.release();
                    wifiController = null;
                }
            }
        });
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
