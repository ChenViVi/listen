package com.yellowzero.Dang;

import android.app.Application;
import android.content.IntentFilter;

import com.allen.library.RxHttpUtils;
import com.kunminx.player.DefaultPlayerManager;
import com.kunminx.player.contract.IServiceNotifier;
import com.yellowzero.Dang.util.NetworkChangeReceiver;
import com.yellowzero.Dang.util.PlayerManager;

public class App extends Application {

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        RxHttpUtils
                .getInstance()
                .init(this)
                .config()
                .setBaseUrl("http://api.yellowzero.wblnb.com/");
        PlayerManager.getInstance().init(this, new IServiceNotifier() {
            @Override
            public void notifyService(boolean startOrStop) {

            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onTerminate() {
        unregisterReceiver(networkChangeReceiver);
        super.onTerminate();
    }
}
