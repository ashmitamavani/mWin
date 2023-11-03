package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("unused")
public class UserProfileModel implements Serializable {
    @Expose
    private String adFailUrl;
    @Expose
    private String isOTPManintance;
    @Expose
    private String message;
    @Expose
    private String userToken;
    @Expose
    private String status;
    @Expose
    private UserProfileDetails userDetails;

    @Expose
    private String homeNote;

    @Expose
    private TopAds topAds;
    @SerializedName("tigerInApp")
    private String tigerInApp;
    @SerializedName("isDefaultAppluck")
    private String isDefaultAppluck;
    @Expose
    private AppLuckAd appLuck;

    public AppLuckAd getAppLuck() {
        return appLuck;
    }
    public String getIsDefaultAppluck() {
        return isDefaultAppluck;
    }

    public String getTigerInApp() {
        return tigerInApp;
    }

    public String getAdFailUrl() {
        return adFailUrl;
    }

    public void setAdFailUrl(String adFailUrl) {
        this.adFailUrl = adFailUrl;
    }

    public String getIsOTPManintance() {
        return isOTPManintance;
    }

    public void setIsOTPManintance(String isOTPManintance) {
        this.isOTPManintance = isOTPManintance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserProfileDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserProfileDetails userDetails) {
        this.userDetails = userDetails;
    }

    public String getHomeNote() {
        return homeNote;
    }

    public void setHomeNote(String homeNote) {
        this.homeNote = homeNote;
    }

    public TopAds getTopAds() {
        return topAds;
    }

    public void setTopAds(TopAds topAds) {
        this.topAds = topAds;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
