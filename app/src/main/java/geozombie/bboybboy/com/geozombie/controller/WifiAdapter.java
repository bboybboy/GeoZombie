package geozombie.bboybboy.com.geozombie.controller;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import geozombie.bboybboy.com.geozombie.R;

public class WifiAdapter extends ArrayAdapter<ScanResult> {
    Context context;

    public WifiAdapter(Context context, int resource, List<ScanResult> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.wifi_item, parent, false);
        }
        ScanResult result = getItem(position);
        ((TextView) convertView.findViewById(R.id.wifi_name)).setText(formatSSDI(result));
        ((ImageView) convertView.findViewById(R.id.wifi_img)).setImageLevel(getNormalizedLevel(result));
        return convertView;
    }

    private int getNormalizedLevel(ScanResult r) {
        int level = WifiManager.calculateSignalLevel(r.level,
                5);
        return level;
    }

    private String formatSSDI(ScanResult r) {
        if (r == null || r.SSID == null || "".equalsIgnoreCase(r.SSID.trim())) {
            return "no data";
        }
        return r.SSID.replace("\"", "");
    }
}
