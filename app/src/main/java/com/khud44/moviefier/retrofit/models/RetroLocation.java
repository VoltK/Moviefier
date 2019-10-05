package com.khud44.moviefier.retrofit.models;

import com.google.gson.annotations.SerializedName;

public class RetroLocation {

    @SerializedName("countryCode")
    private String location;

    public RetroLocation(String location) {
        this.location = location;
    }

    //Retrieve the data using setter/getter methods//
    public String getLocation() {
        return location;
    }

    public void setLocation(String name) {
        this.location = name;
    }
}
