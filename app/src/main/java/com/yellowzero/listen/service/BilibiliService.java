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
}
