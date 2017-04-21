package geozombie.bboybboy.com.geozombie.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import geozombie.bboybboy.com.geozombie.R;
import geozombie.bboybboy.com.geozombie.controller.WifiController;
import geozombie.bboybboy.com.geozombie.utils.SharedPrefsUtils;

class WifiPresenter {
    private EditText wifiSSIDEditText;
    private WifiController wifiController;

    WifiPresenter(SettingsActivity activity) {
        initWifi(activity);
        initUI(activity);
    }

    private void initWifi(final Activity activity) {
        wifiController = new WifiController(activity, new WifiController.onWifiActionListener() {
            @Override
            public void onStatusChange(boolean isFindWifi) {

            }

            @Override
            public void onWifiSelected(String wifiSSID) {
                wifiSSIDEditText.setText(wifiSSID);
                SharedPrefsUtils.setWifiSSID(activity, wifiSSID);
            }
        });
    }

    private void initUI(final Activity activity) {
        wifiSSIDEditText = (EditText) activity.findViewById(R.id.wifi_ssid);
        String savedWifiSSID = SharedPrefsUtils.getWifiSSID(activity);
        if (savedWifiSSID != null)
            wifiSSIDEditText.setText(savedWifiSSID);
        FrameLayout chooserWifiButton = (FrameLayout) activity.findViewById(R.id.choose_wifi);
        wifiSSIDEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                SharedPrefsUtils.setWifiSSID(activity, editable.toString());
            }
        });
        chooserWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiController.checkWifiIsEnabled()) {
                    wifiController.showAvailableWifiDialog();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setTitle(R.string.alert_title);
                    dialog.setMessage(R.string.enable_wifi_message);
                    dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }

            }
        });
    }

    void release() {
        wifiController.release();
        wifiController = null;
    }
}
