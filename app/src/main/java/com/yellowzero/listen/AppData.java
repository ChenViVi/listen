package com.yellowzero.listen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppData {
    public static boolean ENABLE_MUSIC_MOBILE = false;
    public static int MUSIC_REPEAT_MODE = 0;
    public static final String MUSIC_NETEASE_PATH = "sdcard/netease/cloudmusic/Music";
    public static final String MUSIC_QQ_PATH = "sdcard/qqmusic/song";
    public static final String MUSIC_MOO_PATH = "sdcard/blackkey/download";
    public static final String MUSIC_KUGOU_PATH = "sdcard/kgmusic/download";


    public static void loadData(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AppData.ENABLE_MUSIC_MOBILE = preferences.getBoolean("enable_music_mobile",false);
        AppData.MUSIC_REPEAT_MODE = preferences.getInt("music_repeat_mode",0);
    }

    public static void saveData(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("enable_music_mobile", AppData.ENABLE_MUSIC_MOBILE);
        editor.putInt("music_repeat_mode", AppData.MUSIC_REPEAT_MODE);
        editor.apply();
    }
}
