package com.cq.hhxk.voice.voiceoperationsystem.http;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HttpInterface {

    /**
     * 新闻
     * @return
     */
    @GET("news")
    Call<ResponseBody> news();

    /**
     * 人数
     * @return
     */
    @GET("personNum")
    Call<ResponseBody> personNum();

    /**
     * 展馆人流量
     * @return
     */
    @GET("flowrate")
    Call<ResponseBody> flowrate();

    /**
     * 巡查事件
     * @return
     */
    @GET("patrol")
    Call<ResponseBody> patrol();

    /**
     * 日程安排
     * @return
     */
    @GET("schedule")
    Call<ResponseBody> schedule();

}
