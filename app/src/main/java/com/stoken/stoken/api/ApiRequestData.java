package com.stoken.stoken.api;

import com.stoken.stoken.model.ResponsModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiRequestData {

    // login
    @FormUrlEncoded
    @POST("user/login")
    Call<ResponsModel> login(@Field("email") String email,
                             @Field("password") String password);

    // appoint
    @FormUrlEncoded
    @POST("firebase/appoint")
    Call<ResponsModel> appoint(@Field("user_id") String user_id,
                               @Field("firebase_key") String firebase_key);

    // cancel appoint
    @FormUrlEncoded
    @POST("firebase/cancel_appoint")
    Call<ResponsModel> cancelAppoint(@Field("firebase_key") String firebase_key);

    // documents
    @GET("document/list")
    Call<ResponsModel> documentList();
}