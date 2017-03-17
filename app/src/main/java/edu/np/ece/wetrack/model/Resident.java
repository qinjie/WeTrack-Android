package edu.np.ece.wetrack.model;

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

    @SerializedName("dob")
    private String dob;

    @SerializedName("nric")
    private String nric;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("thumbnail_path")
    private String thumbnailPath;

    @SerializedName("status")
    private int status;

    @SerializedName("remark")
    private String remark;

    @SerializedName("reported_at")
    private String reportedAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("beacons")
    private List<BeaconInfo> beacons = new ArrayList<BeaconInfo>();

    @SerializedName("relatives")
    private List<Relative> relatives = new ArrayList<Relative>();

    @SerializedName("locations")
    private List<Location> locations = new ArrayList<Location>();

    @SerializedName("latestLocation")
    private List<Location> latestLocation = new ArrayList<Location>();

    @SerializedName("locationHistories")
    private List<Location> locationHistories = new ArrayList<Location>();

    public final static Parcelable.Creator<Resident> CREATOR = new Creator<Resident>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Resident createFromParcel(Parcel in) {
            Resident instance = new Resident();
            instance.id = ((int) in.readValue((int.class.getClassLoader())));
            instance.fullname = ((String) in.readValue((String.class.getClassLoader())));
            instance.dob = ((String) in.readValue((String.class.getClassLoader())));
            instance.nric = ((String) in.readValue((String.class.getClassLoader())));
            instance.imagePath = ((String) in.readValue((String.class.getClassLoader())));
            instance.thumbnailPath = ((String) in.readValue((String.class.getClassLoader())));
            instance.status = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.remark = ((String) in.readValue((String.class.getClassLoader())));
            instance.reportedAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.createdAt = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.beacons, (edu.np.ece.wetrack.model.BeaconInfo.class.getClassLoader()));
            in.readList(instance.relatives, (edu.np.ece.wetrack.model.Relative.class.getClassLoader()));
            in.readList(instance.locations, (edu.np.ece.wetrack.model.Location.class.getClassLoader()));
            in.readList(instance.latestLocation, (edu.np.ece.wetrack.model.Location.class.getClassLoader()));
            in.readList(instance.locationHistories, (edu.np.ece.wetrack.model.Location.class.getClassLoader()));
            return instance;
        }

        public Resident[] newArray(int size) {
            return (new Resident[size]);
        }
    };

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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(String reportedAt) {
        this.reportedAt = reportedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<BeaconInfo> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<BeaconInfo> beacons) {
        this.beacons = beacons;
    }

    public List<Relative> getRelatives() {
        return relatives;
    }

    public void setRelatives(List<Relative> relatives) {
        this.relatives = relatives;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Location> getLatestLocation() {
        return latestLocation;
    }

    public void setLatestLocation(List<Location> latestLocation) {
        this.latestLocation = latestLocation;
    }



    public List<Location> getLocationHistories() {
        return locationHistories;
    }

    public void setLocationHistories(List<Location> locationHistories) {
        this.locationHistories = locationHistories;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(fullname);
        dest.writeValue(dob);
        dest.writeValue(nric);
        dest.writeValue(imagePath);
        dest.writeValue(thumbnailPath);
        dest.writeValue(status);
        dest.writeValue(remark);
        dest.writeValue(reportedAt);
        dest.writeValue(createdAt);
        dest.writeList(beacons);
        dest.writeList(relatives);
        dest.writeList(locations);
        dest.writeList(latestLocation);
        dest.writeList(locationHistories);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        Resident other = (Resident) obj;

        return fullname.equals(other.getFullname()) && id == other.getId() && nric.equals(other.getNric());
    }

}
