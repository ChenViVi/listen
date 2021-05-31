package com.yellowzero.listen.service;

import com.allen.library.bean.BaseData;
import com.yellowzero.listen.model.Schedule;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ScheduleService {
    @GET("schedule/list")
    Observable<BaseData<List<Schedule>>> list();
}
