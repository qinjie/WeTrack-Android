package com.example.hoanglong.wetrack.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hoanglong on 10/08/2016.
 */

public class Beacons {
    @SerializedName("id")
    private int id;

    @SerializedName("resident_id")
    private int residentId;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("major")
    private int major;

    @SerializedName("minor")
    private int minor;

    @SerializedName("status")
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResidentId() {
        return residentId;
    }

    public void setResidentId(int residentId) {
        this.residentId = residentId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
