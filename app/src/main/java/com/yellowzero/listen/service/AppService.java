package com.yellowzero.listen.service;

import com.allen.library.bean.BaseData;
import com.yellowzero.listen.model.AppInfo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface AppService {
    @GET("app/list")
    Observable<BaseData<List<AppInfo>>> list();
}
