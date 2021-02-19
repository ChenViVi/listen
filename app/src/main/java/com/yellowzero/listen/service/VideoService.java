package com.yellowzero.listen.service;

import com.allen.library.bean.BaseData;
import com.yellowzero.listen.model.VideoTag;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface VideoService {
    @GET("video/tags")
    Observable<BaseData<List<VideoTag>>> tags();
}
