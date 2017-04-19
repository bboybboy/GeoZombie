package geozombie.bboybboy.com.geozombie.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefsUtils {

    private static final String WIFI_SSID_KEY = "wifi_ssid";
    private static final String RADIUS_KEY = "radius";

    public static final long NO_RADIUS = -1;

    private SharedPrefsUtils() {
    }

    public static String getWifiSSID(Context context) {
        String value = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(WIFI_SSID_KEY, null);
        }
        return value;
    }

    public static long getRadius(Context context) {
        long value = NO_RADIUS;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getLong(RADIUS_KEY, NO_RADIUS);
        }
        return value;
    }

    public static boolean setWifiSSID(Context context, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(WIFI_SSID_KEY, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean setRadius(Context context, long value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(RADIUS_KEY, value);
            return editor.commit();
        }
        return false;
    }
}
