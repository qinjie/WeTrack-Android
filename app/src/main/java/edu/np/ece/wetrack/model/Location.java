package edu.np.ece.wetrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hoanglong on 23-Jan-17.
 */

public class Location implements Parcelable {
    @SerializedName("id")
    private int beaconID;

    @SerializedName("address")
    private String addr;

    @SerializedName("created_at")
    private String created;

    public Location(int beaconID, String addr, String created) {
        this.beaconID = beaconID;
        this.addr = addr;
        this.created = created;
    }

    public int getBeaconID() {
        return beaconID;
    }

    public void setBeaconID(int beaconID) {
        this.beaconID = beaconID;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(beaconID);
        dest.writeString(addr);
        dest.writeString(created);

    }

    protected Location(Parcel in) {
        beaconID = in.readInt();
        addr = in.readString();
        created = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel parcel) {
            return new Location(parcel);
        }

        @Override
        public Location[] newArray(int i) {
            return new Location[i];
        }
    };

}
