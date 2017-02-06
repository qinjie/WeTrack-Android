package edu.np.ece.wetrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class BeaconInfo implements Parcelable {
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

    @SerializedName("created_at")
    private String createdAt;

    public final static Parcelable.Creator<BeaconInfo> CREATOR = new Parcelable.Creator<BeaconInfo>() {
        @SuppressWarnings({
                "unchecked"
        })
        public BeaconInfo createFromParcel(Parcel in) {
            BeaconInfo instance = new BeaconInfo();
            instance.id = ((int) in.readValue((String.class.getClassLoader())));
            instance.residentId = ((int) in.readValue((String.class.getClassLoader())));
            instance.uuid = ((String) in.readValue((String.class.getClassLoader())));
            instance.major = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.minor = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.status = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.createdAt = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public BeaconInfo[] newArray(int size) {
            return (new BeaconInfo[size]);
        }
    };

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

   @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        BeaconInfo other = (BeaconInfo) obj;

        return other.major == major && other.uuid.equals(uuid) && other.minor == minor;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(residentId);
        dest.writeValue(uuid);
        dest.writeValue(major);
        dest.writeValue(minor);
        dest.writeValue(status);
        dest.writeValue(createdAt);
    }

    public int describeContents() {
        return 0;
    }
}
