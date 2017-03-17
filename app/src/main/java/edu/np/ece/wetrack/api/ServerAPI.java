package edu.np.ece.wetrack.api;


import com.google.gson.JsonObject;

import java.util.List;

import edu.np.ece.wetrack.model.EmailInfo;
import edu.np.ece.wetrack.model.Resident;
import edu.np.ece.wetrack.model.UserAccount;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by hoanglong on 10/08/2016.
 */

public interface ServerAPI {
    @GET("v1/resident?expand=beacons,latestLocation,relatives")
    Call<List<Resident>> getPatientList(@Header("Authorization") String authorization);

    @POST("v1/location-history")
    Call<JsonObject> sendBeaconLocation(@Header("Authorization") String authorization, @Header("Content-Type") String type, @Body JsonObject beaconList);

    @POST("v1/user/login-email")
    Call<UserAccount> loginViaEmail(@Body JsonObject email);

    @POST("v1/resident/status")
    Call<Resident> changeStatus(@Header("Authorization") String authorization, @Header("Content-Type") String type,@Body JsonObject residentId);

    @POST("v1/device-token/new")
    Call<UserAccount> sendToken(@Body JsonObject obj);

    @POST("v1/device-token/del")
    Call<JsonObject> deleteToken(@Body JsonObject obj);


}

