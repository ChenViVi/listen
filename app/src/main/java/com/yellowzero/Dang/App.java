package com.yellowzero.Dang;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.allen.library.RxHttpUtils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.yellowzero.Dang.notify.PlayerService;
import com.yellowzero.Dang.player.DefaultPlayerManager;
import com.yellowzero.Dang.player.contract.IServiceNotifier;
import com.yellowzero.Dang.player.helper.PlayerFileNameGenerator;
import com.yellowzero.Dang.util.NetworkChangeReceiver;

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
        DefaultPlayerManager.getInstance().init(this, new IServiceNotifier() {
            @Override
            public void notifyService(boolean startOrStop) {
                Intent intent = new Intent(App.this, PlayerService.class);
                if (startOrStop) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
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

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .fileNameGenerator(new PlayerFileNameGenerator())
                .maxCacheSize(2147483648L) // 2GB
                .build();
    }
}
