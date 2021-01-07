package com.yellowzero.Dang;

import android.app.Application;

import com.allen.library.RxHttpUtils;
import com.kunminx.player.DefaultPlayerManager;
import com.kunminx.player.contract.IServiceNotifier;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxHttpUtils
                .getInstance()
                .init(this)
                .config()
                .setBaseUrl("http://api.yellowzero.wblnb.com/");
        DefaultPlayerManager.getInstance().init(this, new IServiceNotifier() {
            @Override
            public void notifyService(boolean startOrStop) {

            }
        });
    }
}
