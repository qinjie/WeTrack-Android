package edu.np.ece.wetrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hoanglong on 23-Jan-17.
 */

public class Location implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("beacon_id")
    private int beaconId;

    @SerializedName("locator_id")
    private int locatorId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("address")
    private String address;

    @SerializedName("created_at")
    private String createdAt;

    public final static Parcelable.Creator<Location> CREATOR = new Creator<Location>() {
        @SuppressWarnings({
                "unchecked"
        })
        public Location createFromParcel(Parcel in) {
            Location instance = new Location();
            instance.id = ((int) in.readValue((Integer.class.getClassLoader())));
            instance.beaconId = ((int) in.readValue((Integer.class.getClassLoader())));
            instance.locatorId = ((int) in.readValue((Integer.class.getClassLoader())));
            instance.userId = ((int) in.readValue((String.class.getClassLoader())));
            instance.longitude = ((double) in.readValue((Double.class.getClassLoader())));
            instance.latitude = ((double) in.readValue((Double.class.getClassLoader())));
            instance.address = ((String) in.readValue((String.class.getClassLoader())));
            instance.createdAt = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Location[] newArray(int size) {
            return (new Location[size]);
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(int beaconId) {
        this.beaconId = beaconId;
    }

    public int getLocatorId() {
        return locatorId;
    }

    public void setLocatorId(int locatorId) {
        this.locatorId = locatorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(beaconId);
        dest.writeValue(locatorId);
        dest.writeValue(userId);
        dest.writeValue(longitude);
        dest.writeValue(latitude);
        dest.writeValue(address);
        dest.writeValue(createdAt);
    }


    public int describeContents() {
        return 0;
    }

}
