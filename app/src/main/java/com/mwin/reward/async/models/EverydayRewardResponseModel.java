package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class EverydayRewardResponseModel {

    @SerializedName("data")
    private List<EverydayRewardDataItem> data;
    @Expose
    private String isShowInterstitial;
    @Expose
    private String message;
    @Expose
    private String status;
    @Expose
    private String totalPoints;
    @Expose
    private String adFailUrl;

    @SerializedName("tigerInApp")
    private String tigerInApp;
    @Expose
    private TopAds topAds;
    @Expose
    private String userToken;
    @Expose
    private String homeNote;
    @Expose
    private String earningPoint;
    @Expose
    private String btnColor;
    @Expose
    private String btnName;
    @Expose
    private String note;
    @Expose
    private String todayDate;
    @Expose
    private String endDate;
    @Expose
    private String isClaimedDailyReward;
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

    public String getIsClaimedDailyReward() {
        return isClaimedDailyReward;
    }

    public List<EverydayRewardDataItem> getData() {
        return data;
    }

    public void setData(List<EverydayRewardDataItem> dailyRewardData) {
        this.data = dailyRewardData;
    }

    public String getIsShowInterstitial() {
        return isShowInterstitial;
    }

    public void setIsShowInterstitial(String isShowInterstitial) {
        this.isShowInterstitial = isShowInterstitial;
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

    public String getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(String totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getAdFailUrl() {
        return adFailUrl;
    }

    public String getTigerInApp() {
        return tigerInApp;
    }

    public TopAds getTopAds() {
        return topAds;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getHomeNote() {
        return homeNote;
    }

    public String getEarningPoint() {
        return earningPoint;
    }

    public String getBtnColor() {
        return btnColor;
    }

    public String getBtnName() {
        return btnName;
    }

    public String getNote() {
        return note;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
