package edu.np.ece.wetrack.model;


/**
 * Created by hoanglong on 16-Dec-16.
 */

public class BeaconLocation {
    private int beacon_id;
    private int user_id;
    private double longitude;
    private double latitude;
    private String created_at;
    private String address;

    public BeaconLocation(int beacon_id, int user_id, double longitude, double latitude, String created_at, String address) {
        this.beacon_id = beacon_id;
        this.user_id = user_id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.created_at = created_at;
        this.address =address;
    }

    public int getBeacon_id() {
        return beacon_id;
    }

    public void setBeacon_id(int beacon_id) {
        this.beacon_id = beacon_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public String getcreated_at() {
        return created_at;
    }

    public void setcreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
