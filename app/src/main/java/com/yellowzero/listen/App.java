package com.yellowzero.listen;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.allen.library.RxHttpUtils;
import com.allen.library.config.OkHttpConfig;
import com.danikula.videocache.HttpProxyCacheServer;
import com.yellowzero.listen.model.entity.DaoMaster;
import com.yellowzero.listen.model.entity.DaoSession;
import com.yellowzero.listen.notify.PlayerService;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.contract.IServiceNotifier;
import com.yellowzero.listen.player.helper.PlayerFileNameGenerator;
import com.yellowzero.listen.util.NetworkChangeReceiver;

import org.greenrobot.greendao.database.Database;

import okhttp3.OkHttpClient;

public class App extends Application {

    private DaoSession daoSession;
    private NetworkChangeReceiver networkChangeReceiver;
    private HttpProxyCacheServer proxy;

    @Override
    public void onCreate() {
        super.onCreate();
        AppData.loadData(this);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "yellowzero");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        OkHttpClient okHttpClient = new OkHttpConfig
                .Builder(this)
                .setDebug(false)
                .build();
        RxHttpUtils
                .getInstance()
                .init(this)
                .config()
                .setOkClient(okHttpClient)
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

    public DaoSession getDaoSession() {
        return daoSession;
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
