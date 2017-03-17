package edu.np.ece.wetrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import static edu.np.ece.wetrack.R.id.status;

public class Relative implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    public final static Parcelable.Creator<Relative> CREATOR = new Creator<Relative>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Relative createFromParcel(Parcel in) {
            Relative instance = new Relative();
            instance.id = ((int) in.readValue((Integer.class.getClassLoader())));
            instance.username = ((String) in.readValue((String.class.getClassLoader())));
            instance.email = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Relative[] newArray(int size) {
            return (new Relative[size]);
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(username);
        dest.writeValue(email);
//        dest.writeValue(status);
    }

    public int describeContents() {
        return 0;
    }

}