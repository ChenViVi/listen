package com.yellowzero.Dang;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppData {
    public static boolean ENABLE_MUSIC_MOBILE = false;

    public static void loadData(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AppData.ENABLE_MUSIC_MOBILE = preferences.getBoolean("enable_music_mobile",false);
    }

    public static void saveData(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("enable_music_mobile", AppData.ENABLE_MUSIC_MOBILE);
        editor.apply();
    }
}
