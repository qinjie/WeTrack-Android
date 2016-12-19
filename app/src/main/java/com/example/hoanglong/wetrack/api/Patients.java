package com.example.hoanglong.wetrack.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hoanglong on 14-Dec-16.
 */

public class Patients {
    @SerializedName("id")
    private int id;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("nirc")
    private String nric;

    @SerializedName("status")
    private int status;

    @SerializedName("beacons")
    private List<Beacons> patientBeacon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Beacons> getPatientBeacon() {
        return patientBeacon;
    }

    public void setPatientBeacon(List<Beacons> patientBeacon) {
        this.patientBeacon = patientBeacon;
    }
}
