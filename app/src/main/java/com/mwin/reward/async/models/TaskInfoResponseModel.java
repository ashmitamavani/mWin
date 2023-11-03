package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class TaskInfoResponseModel {
    @SerializedName("isDefaultAppluck")
    private String isDefaultAppluck;
    @Expose
    private String adFailUrl;
    @Expose
    private TopAds topAds;
    @Expose
    private String homeNote;
    @Expose
    private String userToken;
    @Expose
    private String status;
    @Expose
    private String isShowInterstitial;
    @Expose
    private String message;
    @Expose
    private TaskDetails taskDetails;
    @SerializedName("tigerInApp")
    private String tigerInApp;
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

    public void setTigerInApp(String tigerInApp) {
        this.tigerInApp = tigerInApp;
    }

    public String getAdFailUrl() {
        return adFailUrl;
    }

    public void setAdFailUrl(String adFailUrl) {
        this.adFailUrl = adFailUrl;
    }

    public String getHomeNote() {
        return homeNote;
    }

    public void setHomeNote(String homeNote) {
        this.homeNote = homeNote;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TaskDetails getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(TaskDetails taskDetails) {
        this.taskDetails = taskDetails;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TopAds getTopAds() {
        return topAds;
    }

    public void setTopAds(TopAds topAds) {
        this.topAds = topAds;
    }

    public String getIsShowInterstitial() {
        return isShowInterstitial;
    }

    public void setIsShowInterstitial(String isShowInterstitial) {
        this.isShowInterstitial = isShowInterstitial;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
