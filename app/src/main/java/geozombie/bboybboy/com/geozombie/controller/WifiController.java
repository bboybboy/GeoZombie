package geozombie.bboybboy.com.geozombie.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import geozombie.bboybboy.com.geozombie.R;
import geozombie.bboybboy.com.geozombie.eventbus.Events;
import geozombie.bboybboy.com.geozombie.utils.CastomProgressDialog;
import geozombie.bboybboy.com.geozombie.utils.Utils;

public class WifiController {

    private static String TAG = WifiController.class.getSimpleName();
    private final static int INTERVAL = 1000; //1 second

    private Context context;
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private WifiManager wifiManager;
    private EventBus bus = EventBus.getDefault();
    private Handler scanningHandler = new Handler();
    private boolean isFindWifi;
    private String wifiSSID;
    private onWifiActionListener onWifiActionListener;
    private List<ScanResult> wifiAvailableList;
    private boolean isNeedShowWifiDialog;

    public WifiController(Context context, onWifiActionListener onWifiActionListener) {
        this.context = context;
        this.onWifiActionListener = onWifiActionListener;
        init();
    }

    public void showAvailableWifiDialog() {
        if (wifiAvailableList != null && !wifiAvailableList.isEmpty()) {
            CastomProgressDialog.dismiss();
            showWifiListDialog(wifiAvailableList);
            isNeedShowWifiDialog = false;
        } else {
            CastomProgressDialog.show(context, R.string.wifi_wait);
            isNeedShowWifiDialog = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events.WifiPermissionGranted event) {
        startScanningTask();
    }

    private void onWifiFindAction(boolean isFindWifiNow) {
        if (isNeedShowWifiDialog)
            showAvailableWifiDialog();

        if (isFindWifi != isFindWifiNow) {
            isFindWifi = isFindWifiNow;
            if (onWifiActionListener != null)
                onWifiActionListener.onStatusChange(isFindWifi);
        }
    }

    private void init() {
        isFindWifi = false;
        wifiAvailableList = new ArrayList<>();
//        wifiSSID = SharedPrefsUtils.getWifiSSID(context);
        //TODO remove after test
        wifiSSID = "litslink 5";
            Log.d(TAG, "init: ");
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            wifiManager =
                    (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiBroadcastReceiver = new WifiBroadcastReceiver();
            context.registerReceiver(wifiBroadcastReceiver, filter);
            bus.register(this);
            Utils.checkWifiPermission((Activity) context);
    }

    public void release() {
        Log.d(TAG, "release: ");
        stopScanningTask();
        bus.unregister(this);
        if (wifiBroadcastReceiver != null) {
            context.unregisterReceiver(wifiBroadcastReceiver);
            wifiBroadcastReceiver = null;
        }
        wifiManager = null;
    }

    private Runnable scanningTask = new Runnable() {
        @Override
        public void run() {
            wifiManager.startScan();
        }
    };

    //Delayed task
    private void startScanningTask() {
        Log.d(TAG, "startScanningTask: ");
        scanningHandler.postDelayed(scanningTask, INTERVAL);
    }

    private void stopScanningTask() {
        scanningHandler.removeCallbacks(scanningTask);
    }

    private class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiAvailableList = wifiManager.getScanResults();
            wifiAvailableList.addAll(wifiManager.getScanResults());
            wifiAvailableList.addAll(wifiManager.getScanResults());
            wifiAvailableList.addAll(wifiManager.getScanResults());
            Log.d(TAG, "_____________________________________");
            Log.d(TAG, "onReceive: scanList.size = " + wifiAvailableList.size());
            if (wifiSSID != null) {
                for (ScanResult result : wifiAvailableList) {
                    Log.d(TAG, "result: = " + result.SSID);
                    if (wifiSSID.equals(result.SSID)) {
                        onWifiFindAction(true);
                        return;
                    }
                }
                onWifiFindAction(false);
                Log.d(TAG, "_____________________________________");
                startScanningTask();
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

    public interface onWifiActionListener {
        void onStatusChange(boolean isFindWifi);

        void onWifiSelected(String wifiSSID);
    }
}
