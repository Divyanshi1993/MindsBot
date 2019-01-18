package com.referminds.app.chat.Controller;

import com.referminds.app.chat.Model.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class RestController {

    public interface UserService

    {
        @POST("signup")
        @FormUrlEncoded
        Call<User> signup(@Field("name") String name, @Field("password") String pwd);

        @POST("signin")
        @FormUrlEncoded
        Call<User> signin(@Field("name") String name, @Field("password") String pwd);

        @POST("signout")
        @FormUrlEncoded
        Call<User> signout(@Field("name") String name);


    }

}
