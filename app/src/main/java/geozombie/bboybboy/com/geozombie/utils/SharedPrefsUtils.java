package geozombie.bboybboy.com.geozombie.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

public class SharedPrefsUtils {

    private static final String WIFI_SSID_KEY = "wifi_ssid";
    private static final String RADIUS_KEY = "radius";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";

    public static final long NO_RADIUS = 100;
    public static final long NO_COORD = 0;

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

    public static float getRadius(Context context) {
        float value = NO_RADIUS;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getFloat(RADIUS_KEY, NO_RADIUS);
        }
        return value;
    }

    public static LatLng getLatLng(Context context) {
        double longitude = NO_COORD;
        double latitude = NO_COORD;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            latitude = getDouble(preferences, LATITUDE_KEY, NO_COORD);
            longitude = getDouble(preferences, LONGITUDE_KEY, NO_COORD);
        }
        if (latitude == NO_COORD && longitude == NO_COORD) return null;
        return new LatLng(latitude, longitude);
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

    public static boolean setRadius(Context context, float value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(RADIUS_KEY, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean setLatLng(Context context, LatLng value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            putDouble(editor, LATITUDE_KEY, value.latitude);
            putDouble(editor, LONGITUDE_KEY, value.longitude);
            return editor.commit();
        }
        return false;
    }

    private static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    private static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}
