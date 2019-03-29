package com.vicman.jnitest.retrofit;

import android.support.annotation.NonNull;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface Api {

    @FormUrlEncoded
    @PUT("addtask")
    Call<ProcessQueueResult> pushToQueue(
    @Field("app_id") @NonNull String appId,
    @Field("data") @NonNull String data,
    @Field("sign_data") @NonNull String signData,
    @Field("signature") @NonNull String signature);

    @GET("getresult")
    Call<ProcessResult> getResultUri(@Query("request_id") @NonNull String requestId);

}

