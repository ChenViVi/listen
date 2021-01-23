package com.yellowzero.listen.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private ArrayList<NetworkListener> listeners = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        for (NetworkListener listener : listeners)
            listener.onChangeState(NetworkUtil.getConnectedState(context));
    }

    public void addNetworkListener(NetworkListener listener) {
        listeners.add(listener);
    }

    public interface NetworkListener {
        void onChangeState(int state);
    }
}

