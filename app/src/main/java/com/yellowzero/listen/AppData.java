package com.yellowzero.listen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;

public class AppData {
    public static boolean ENABLE_MUSIC_MOBILE = false;
    public static int MUSIC_REPEAT_MODE = 0;
    public static int MUSIC_LOCAL_COUNT = 0;
    public static int LAST_UPDATE_CODE = 0;

    public static final String MUSIC_NETEASE_PATH = "/storage/emulated/0/netease/cloudmusic/Music";
    public static final String MUSIC_QQ_PATH = "/storage/emulated/0/qqmusic/song";
    public static final String MUSIC_MOO_PATH = "/storage/emulated/0/blackkey/download";
    public static final String MUSIC_KUGOU_PATH = "/storage/emulated/0/kgmusic/download";
    public static final String QQ_FILES_1 = "/storage/emulated/0/tencent/QQfile_recv";
    public static final String QQ_FILES_2 = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv";
    public static final String MUSIC_MIGU = "/storage/emulated/0/12530/download";
    public static final String BAIDU_DISK = "/storage/emulated/0/BaiduNetdisk";
    public static final String FORMAT_MUSIC_ID = "%d_%s";

    public static String CACHE_DIR = "";
    public static String CACHE_IMAGE_DIR = "";
    public static String CACHE_COVER_DIR = "";
    public static String CACHE_MUSIC_DIR = "";


    public static void loadData(Context context) {
        CACHE_DIR = context.getExternalCacheDir().getPath();
        CACHE_IMAGE_DIR = CACHE_DIR + File.separator + "image_manager_disk_cache" + File.separator;
        CACHE_COVER_DIR = CACHE_DIR + File.separator + "cover" + File.separator;
        CACHE_MUSIC_DIR = CACHE_DIR + File.separator + "video-cache" + File.separator;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AppData.ENABLE_MUSIC_MOBILE = preferences.getBoolean("enable_music_mobile",false);
        AppData.MUSIC_REPEAT_MODE = preferences.getInt("music_repeat_mode",0);
        AppData.MUSIC_LOCAL_COUNT = preferences.getInt("music_local_count",0);
        AppData.LAST_UPDATE_CODE = preferences.getInt("last_update_code",0);
    }

    public static void saveData(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("enable_music_mobile", AppData.ENABLE_MUSIC_MOBILE);
        editor.putInt("music_repeat_mode", AppData.MUSIC_REPEAT_MODE);
        editor.putInt("music_local_count", AppData.MUSIC_LOCAL_COUNT);
        editor.putInt("last_update_code", AppData.LAST_UPDATE_CODE);
        editor.apply();
    }
}
