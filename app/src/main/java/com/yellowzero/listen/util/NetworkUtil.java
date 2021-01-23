package com.yellowzero.listen.util;

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
    public static int CURRENT_STATE = STATE_OFFLINE;

    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static int getConnectedState(Context context) {
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable())
            CURRENT_STATE = STATE_OFFLINE;
        else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            CURRENT_STATE = STATE_MOBILE;
        else
            CURRENT_STATE= STATE_WIFI;
        return CURRENT_STATE;
    }
}
