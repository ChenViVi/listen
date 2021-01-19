package com.yellowzero.Dang.player.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.RequiresPermission;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkUtil {

    public final static int STATE_WIFI = 1;
    public final static int STATE_MOBILE = 2;
    public final static int STATE_OFFLINE = 3;

    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static int getConnectedState(Context context) {
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable())
            return STATE_OFFLINE;
        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            return STATE_MOBILE;
        return STATE_WIFI;
    }
}
