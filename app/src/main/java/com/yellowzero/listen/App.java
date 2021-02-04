package com.yellowzero.listen;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.multidex.MultiDexApplication;

import com.allen.library.RxHttpUtils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.yellowzero.listen.notify.PlayerService;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.contract.IServiceNotifier;
import com.yellowzero.listen.player.helper.PlayerFileNameGenerator;
import com.yellowzero.listen.util.NetworkChangeReceiver;

import java.util.HashSet;

public class App extends MultiDexApplication {

    private NetworkChangeReceiver networkChangeReceiver;
    private HttpProxyCacheServer proxy;

    @Override
    public void onCreate() {
        super.onCreate();
        AppData.loadData(this);
        HashSet<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setRequestListeners(requestListeners)
                .build();
        Fresco.initialize(this, config);
        FLog.setMinimumLoggingLevel(FLog.ERROR);
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
