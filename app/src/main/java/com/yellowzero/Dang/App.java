package com.yellowzero.Dang;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.allen.library.RxHttpUtils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.kunminx.player.contract.IServiceNotifier;
import com.kunminx.player.helper.PlayerFileNameGenerator;
import com.yellowzero.Dang.util.NetworkChangeReceiver;

import com.yellowzero.Dang.util.PlayerManager;

public class App extends Application {

    private NetworkChangeReceiver networkChangeReceiver;
    private HttpProxyCacheServer proxy;

    @Override
    public void onCreate() {
        super.onCreate();
        AppData.loadData(this);
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

    public void addNetworkListener(NetworkChangeReceiver.NetworkListener listener) {
        networkChangeReceiver.addNetworkListener(listener);
    }

    public HttpProxyCacheServer getProxy(Context context) {
        return proxy == null ? (proxy = newProxy()) : proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .fileNameGenerator(new PlayerFileNameGenerator())
                .maxCacheSize(2147483648L) // 2GB
                .build();
    }
}
