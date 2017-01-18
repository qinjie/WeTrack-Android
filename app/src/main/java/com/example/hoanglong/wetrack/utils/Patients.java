package com.example.hoanglong.wetrack.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hoanglong on 14-Dec-16.
 */

public class Patients implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("nric")
    private String nric;

    @SerializedName("status")
    private int status;

    @SerializedName("beacons")
    private List<Beacons> patientBeacon;

    @SerializedName("thumbnail_path")
    private String avatar;

    @SerializedName("dob")
    private String dob;

    @SerializedName("created_at")
    private String created;

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

    protected Patients(Parcel in) {
        fullname = in.readString();
        avatar = in.readString();
        status = in.readInt();
        nric = in.readString();
        dob = in.readString();
        created = in.readString();
    }


    public static final Creator<Patients> CREATOR = new Creator<Patients>() {
        @Override
        public Patients createFromParcel(Parcel in)  {
            return new Patients(in);
        }

        @Override
        public Patients[] newArray(int size) {
            return new Patients[size];
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

    }
}
