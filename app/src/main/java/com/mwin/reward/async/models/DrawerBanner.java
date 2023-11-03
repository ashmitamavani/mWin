package com.mwin.reward.async.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DrawerBanner implements Serializable {

    @SerializedName("image")
    private String image;

    @SerializedName("url")
    private String url;

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}