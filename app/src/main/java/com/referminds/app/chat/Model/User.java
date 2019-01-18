package com.referminds.app.chat.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    public User() {

    }

    public User(String name, String socketid) {
        this();
        this.name = name;
        this.soketId = socketid;

    }

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("password")
    @Expose
    String password;
    String soketId;

    public String getName() {
        return name;
    }

    public String getPwd() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPwd(String pwd) {
        this.password = pwd;
    }

    public String getSoketId() {
        return soketId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
