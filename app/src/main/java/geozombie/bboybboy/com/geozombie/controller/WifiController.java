package geozombie.bboybboy.com.geozombie.controller;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import geozombie.bboybboy.com.geozombie.R;
import geozombie.bboybboy.com.geozombie.utils.SharedPrefsUtils;

public class WifiController {

    private static String TAG = WifiController.class.getSimpleName();
    private final static int PROGRESS_INTERVAL = 10000; //10 second

    private Context context;
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private WifiManager wifiManager;
    private Handler scanningHandler = new Handler();
    private Handler progressDialogHandler = new Handler();
    private String wifiSSID;
    private onWifiActionListener onWifiActionListener;
    private List<ScanResult> wifiAvailableList;
    private boolean isNeedShowWifiDialog;
    private ProgressDialog progressDialog;

    public WifiController(Context context, onWifiActionListener onWifiActionListener) {
        this.context = context;
        this.onWifiActionListener = onWifiActionListener;
        init();
    }

    public void showAvailableWifiDialog() {
        if (wifiAvailableList != null && !wifiAvailableList.isEmpty()) {
            stopProgressTask();
            showWifiListDialog(wifiAvailableList);
            isNeedShowWifiDialog = false;
        } else {
            startProgressTask();
            wifiManager.startScan();
            isNeedShowWifiDialog = true;
        }
    }

    private void onWifiListUpdated() {
        if (isNeedShowWifiDialog) {
            stopProgressTask();
            showWifiListDialog(wifiAvailableList);
            isNeedShowWifiDialog = false;
        }
    }

    private void onWifiFindAction(boolean isFindWifiNow) {
            if (onWifiActionListener != null)
                onWifiActionListener.onStatusChange(isFindWifiNow);
    }

    private void init() {
        wifiAvailableList = new ArrayList<>();
        wifiSSID = SharedPrefsUtils.getWifiSSID(context);
        Log.d(TAG, "init: ");

        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiBroadcastReceiver = new WifiBroadcastReceiver();
        context.registerReceiver(wifiBroadcastReceiver, filter);
    }

    public void release() {
        Log.d(TAG, "release: ");
        if (wifiBroadcastReceiver != null) {
            context.unregisterReceiver(wifiBroadcastReceiver);
            wifiBroadcastReceiver = null;
        }
        wifiManager = null;
    }

    private Runnable dismissProgressTask = new Runnable() {
        @Override
        public void run() {
            isNeedShowWifiDialog = false;
            dismissProgress();
            Toast.makeText(context, R.string.choose_wifi_dialog_alert, Toast.LENGTH_LONG).show();
        }
    };

    public String getConnectedSSID() {
        if (checkWifiIsEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String SSID = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1);
            return wifiInfo.getSupplicantState() == SupplicantState.COMPLETED ? SSID : "";
        } else {
            return "";
        }
    }

    public boolean checkWifiIsEnabled() {
        return wifiManager.isWifiEnabled();
    }

    //Delayed task
    private void startProgressTask() {
        Log.d(TAG, "startProgressTask: ");
        showProgress();
        progressDialogHandler.postDelayed(dismissProgressTask, PROGRESS_INTERVAL);
    }

    private void stopProgressTask() {
        progressDialogHandler.removeCallbacks(dismissProgressTask);
        dismissProgress();
    }

    private class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                boolean isConnected = wifi != null && wifi.isConnectedOrConnecting();
                if (isConnected) {
                    String wifiSSIDNew = wifiManager.getConnectionInfo().getSSID();
                    if (wifiSSIDNew != null) {
                        wifiSSIDNew = wifiSSIDNew.substring(1, wifiSSIDNew.length() - 1);
                        Log.d(TAG, "onReceive: wifiSSID = " + wifiSSID);
                        if (wifiSSID != null && wifiSSID.equals(wifiSSIDNew)) {
                            onWifiFindAction(true);
                            return;
                        }
                    }
                }
                onWifiFindAction(false);
            } else if (intent != null && intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (wifiManager != null) {
                    Log.d(TAG, "onReceive: wifiManager.getScanResults() " + wifiManager.getScanResults().toString());
                    wifiAvailableList = wifiManager.getScanResults();
                    onWifiListUpdated();
                }
            }
        }
    }

    private void showWifiListDialog(List<ScanResult> results) {
        Collections.sort(results, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return rhs.level > lhs.level ? 1 : rhs.level < lhs.level ? -1 : 0;
            }
        });
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                context);
        final WifiAdapter arrayAdapter = new WifiAdapter(
                context,
                android.R.layout.select_dialog_item, results);

        builderSingle.setNegativeButton(context.getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onWifiActionListener != null)
                            onWifiActionListener.onWifiSelected(arrayAdapter.getItem(which).SSID);
                    }
                });
        AlertDialog dialog = builderSingle.create();
        dialog.show();
    }

    private void showProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        int style;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            style = android.R.style.Theme_Material_Light_Dialog;
        } else {
            //noinspection deprecation
            style = ProgressDialog.THEME_HOLO_LIGHT;
        }

        progressDialog = new ProgressDialog(context, style);
        progressDialog.setMessage(context.getResources().getString(R.string.wifi_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public interface onWifiActionListener {
        void onStatusChange(boolean isFindWifi);

        void onWifiSelected(String wifiSSID);
    }
}
