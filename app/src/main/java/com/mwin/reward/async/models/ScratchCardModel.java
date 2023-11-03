package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class ScratchCardModel {

    @SerializedName("backImage")
    private String backImage;
    @SerializedName("userToken")
    private String userToken;
    @SerializedName("backgroundImage")
    private String backgroundImage;
    @SerializedName("frontImage")
    private String frontImage;
    @SerializedName("homeNote")
    private String homeNote;
    @SerializedName("lastScratchedDate")
    private String lastScratchedDate;
    @SerializedName("message")
    private String message;
    @SerializedName("scratchCardList")
    private List<ScratchCardList> scratchCardList;
    @SerializedName("scratchTime")
    private String scratchTime;
    @SerializedName("status")
    private String status;
    @SerializedName("todayDate")
    private String todayDate;
    @SerializedName("topAds")
    private TopAds topAds;
    @SerializedName("earningPoint")
    private String earningPoint;
    @SerializedName("helpVideoUrl")
    private String helpVideoUrl;
    @SerializedName("adFailUrl")
    private String adFailUrl;
    @SerializedName("tigerInApp")
    private String tigerInApp;
    @SerializedName("totalScratchCardEarning")
    private String totalScratchCardEarning;
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

    public String getBackImage() {
        return backImage;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public String getFrontImage() {
        return frontImage;
    }

    public String getHomeNote() {
        return homeNote;
    }

    public String getLastScratchedDate() {
        return lastScratchedDate;
    }

    public String getMessage() {
        return message;
    }

    public List<ScratchCardList> getScratchCardList() {
        return scratchCardList;
    }

    public String getScratchTime() {
        return scratchTime;
    }

    public String getStatus() {
        return status;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public TopAds getTopAds() {
        return topAds;
    }

    public String getEarningPoint() {
        return earningPoint;
    }

    public String getHelpVideoUrl() {
        return helpVideoUrl;
    }

    public String getAdFailUrl() {
        return adFailUrl;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getTotalScratchCardEarning() {
        return totalScratchCardEarning;
    }
}
