package com.referminds.app.chat.data.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    public User() {

    }

    public User(String name, String password) {
        this();
        this.name = name;
        this.password = password;

    }

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("password")
    @Expose
   private String password;
    private  String soketId;

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
