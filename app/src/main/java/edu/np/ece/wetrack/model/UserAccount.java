package edu.np.ece.wetrack.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hoanglong on 07-Feb-17.
 */

public class UserAccount {
    @SerializedName("result")
    private String result;

    @SerializedName("user_id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("token")
    private String token;

    @SerializedName("role")
    private int role;

    public UserAccount(String result, int id, String username, String email, String token, int role) {
        this.result = result;
        this.id = id;
        this.username = username;
        this.email = email;
        this.token = token;
        this.role = role;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
