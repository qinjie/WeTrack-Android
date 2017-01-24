package com.example.hoanglong.wetrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoanglong on 14-Dec-16.
 */

public class Resident implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("nric")
    private String nric;

    @SerializedName("status")
    private int status;

    @SerializedName("beacons")
    private List<BeaconInfo> patientBeacon;

    @SerializedName("thumbnail_path")
    private String avatar;

    @SerializedName("dob")
    private String dob;

    @SerializedName("created_at")
    private String created;

    @SerializedName("latestLocation")
    private List<Location> latestLocation;

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

    public List<BeaconInfo> getPatientBeacon() {
        return patientBeacon;
    }

    public void setPatientBeacon(List<BeaconInfo> patientBeacon) {
        this.patientBeacon = patientBeacon;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public List<Location> getLatestLocation() {
        return latestLocation;
    }

    public void setLatestLocation(List<Location> latestLocation) {
        this.latestLocation = latestLocation;
    }

    protected Resident(Parcel in) {
        fullname = in.readString();
        avatar = in.readString();
        status = in.readInt();
        nric = in.readString();
        dob = in.readString();
        created = in.readString();
        latestLocation= new ArrayList<Location>();
        in.readTypedList(latestLocation,Location.CREATOR);
    }


    public static final Creator<Resident> CREATOR = new Creator<Resident>() {
        @Override
        public Resident createFromParcel(Parcel in)  {
            return new Resident(in);
        }

        @Override
        public Resident[] newArray(int size) {
            return new Resident[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(fullname);
        dest.writeString(avatar);
        dest.writeInt(status);
        dest.writeString(nric);
        dest.writeString(dob);
        dest.writeString(created);
        dest.writeTypedList(latestLocation);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        Resident other = (Resident) obj;

        return fullname.equals(other.fullname) && id == other.id && nric.equals(other.nric);
    }
}
