package com.yellowzero.Dang;

import android.app.Application;

import com.allen.library.RxHttpUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxHttpUtils
                .getInstance()
                .init(this)
                .config()
                .setBaseUrl("http://api.yellowzero.wblnb.com/");
    }
}
