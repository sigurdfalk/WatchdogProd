package no.ntnu.idi.watchdogprod.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sigurdhf on 15.04.2015.
 */
public class SharedPreferencesHelper {
    public static final String KEY_SHOW_ERROR_SIGN = "showErrorSign";
    public static final String KEY_SHOW_INFO_SIGN = "showInfoSign";

    public static boolean doShowAppWarningSign(final Context context, final String packageName) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(KEY_SHOW_ERROR_SIGN + ":" + packageName, false);
    }

    public static void setDoShowAppWarningSign(final Context context, final String packageName, boolean doShow) {
        SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sharedPrefEditor.putBoolean(KEY_SHOW_ERROR_SIGN + ":" + packageName, doShow);
        sharedPrefEditor.commit();
    }

    public static boolean doShowAppInfoSign(final Context context, final String packageName) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(KEY_SHOW_INFO_SIGN + ":" + packageName, true);
    }

    public static void setDoShowAppInfoSign(final Context context, final String packageName, boolean doShow) {
        SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sharedPrefEditor.putBoolean(KEY_SHOW_INFO_SIGN + ":" + packageName, doShow);
        sharedPrefEditor.commit();
    }
}
