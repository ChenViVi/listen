package com.yellowzero.Dang.service;

import com.allen.library.bean.BaseData;
import com.yellowzero.Dang.model.Music;
import com.yellowzero.Dang.model.MusicTag;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MusicService {
    @GET("music/tags")
    Observable<BaseData<List<MusicTag>>> tags();

    @GET("music/list")
    Observable<BaseData<List<Music>>> list(@Query("tag_id") int tagId);
}
