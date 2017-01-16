package com.example.hoanglong.wetrack.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hoanglong on 10/08/2016.
 */

public class RetrofitUtils {
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Constant.BASE_URL).addConverterFactory(GsonConverterFactory.create());

    public static Retrofit get() {
        return builder.build();
    }


}
