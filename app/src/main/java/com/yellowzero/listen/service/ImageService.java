package com.yellowzero.listen.service;

import com.allen.library.bean.BaseData;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.model.ImageTag;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ImageService {
    @GET("image/tags")
    Observable<BaseData<List<ImageTag>>> tags();
    @GET("image/tags")
    Observable<BaseData<List<ImageTag>>> tags(@Query("image_id") int imageId);

    @GET("image/list")
    Observable<BaseData<List<Image>>> list(@Query("tag_id") Integer tagId, @Query("page") int page, @Query("size") int size);

    @GET("image/view")
    Observable<String> view(@Query("image_id") int imageId);
}
