package com.example.hoanglong.wetrack.api;


import com.example.hoanglong.wetrack.model.Resident;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by hoanglong on 10/08/2016.
 */

public interface ServerAPI {
    @GET("v1/resident?expand=beacons")
    Call<List<Resident>> getPatientList();

    @POST("v1/location-history/create")
    Call<JsonObject> sendBeaconLocation(@Header("Authorization") String authorization, @Header("Content-Type") String type, @Body JsonObject beaconList);


}

