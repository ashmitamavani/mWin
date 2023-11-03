package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NotificationsModel implements Serializable {

    @SerializedName("homeNote")
    private String homeNote;
    @SerializedName("userToken")
    private String userToken;

    @SerializedName("notificationList")
    private List<NotificationListItem> notificationList;

    @Expose
    private TopAds topAds;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    @Expose
    private String isShowInterstitial;
    @SerializedName("adFailUrl")
    private String adFailUrl;
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

    public String getHomeNote() {
        return homeNote;
    }

    public void setHomeNote(String homeNote) {
        this.homeNote = homeNote;
    }

    public List<NotificationListItem> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<NotificationListItem> notificationList) {
        this.notificationList = notificationList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TopAds getTopAds() {
        return topAds;
    }

    public void setTopAds(TopAds topAds) {
        this.topAds = topAds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}