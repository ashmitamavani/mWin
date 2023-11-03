package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class AppLuckAd implements Serializable {
    @Expose
    private String screenNo;
    @Expose
    private String url;
    @Expose
    private String image;

    public String getScreenNo() {
        return screenNo;
    }

    public void setScreenNo(String screenNo) {
        this.screenNo = screenNo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

