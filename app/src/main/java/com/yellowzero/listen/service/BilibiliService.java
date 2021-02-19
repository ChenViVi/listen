package com.yellowzero.listen.service;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BilibiliService {
    @GET("x/space/arc/search")
    Observable<String> getVideos(@Query("mid") String mid,
                                 @Query("ps") int pageSize,
                                 @Query("pn") int pageNumber,
                                 @Query("order") String order,
                                 @Query("jsonp") String jsonp);
    @GET("x/v1/medialist/resource/list")
    Observable<String> list(@Query("biz_id") long biz_id,
                                 @Query("type") int type,
                                 @Query("ps") int ps);
}
