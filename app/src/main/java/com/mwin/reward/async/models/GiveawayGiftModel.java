package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class GiveawayGiftModel {
    @Expose
    private String earningPoint;
    @Expose
    private TopAds topAds;
    @Expose
    private String userToken;
    @Expose
    private String homeNote;
    @Expose
    private String message;
    @Expose
    private String status;
    @Expose
    private String note;
    @Expose
    private String couponPoints;
    @Expose
    private String helpVideoUrl;
    @Expose
    private String screenNo;
    @SerializedName("isDefaultAppluck")
    private String isDefaultAppluck;
    @Expose
    private List<IconListItem> socialMedia;
    @Expose
    private String adFailUrl;
    @SerializedName("tigerInApp")
    private String tigerInApp;
    @Expose
    private AppLuckAd appLuck;
    @Expose
    private String btnName;

    public String getBtnName() {
        return btnName;
    }

    public AppLuckAd getAppLuck() {
        return appLuck;
    }
    @Expose
    private List<HomeDataItem> giveawayCodeList;

    public String getIsDefaultAppluck() {
        return isDefaultAppluck;
    }

    public String getScreenNo() {
        return screenNo;
    }

    public String getTigerInApp() {
        return tigerInApp;
    }

    public void setTigerInApp(String tigerInApp) {
        this.tigerInApp = tigerInApp;
    }

    public String getEarningPoint() {
        return earningPoint;
    }

    public void setEarningPoint(String earningPoint) {
        this.earningPoint = earningPoint;
    }

    public TopAds getTopAds() {
        return topAds;
    }

    public void setTopAds(TopAds topAds) {
        this.topAds = topAds;
    }

    public String getHomeNote() {
        return homeNote;
    }

    public void setHomeNote(String homeNote) {
        this.homeNote = homeNote;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<IconListItem> getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(List<IconListItem> socialPlatforms) {
        this.socialMedia = socialPlatforms;
    }

    public String getCouponPoints() {
        return couponPoints;
    }

    public void setCouponPoints(String couponPoints) {
        this.couponPoints = couponPoints;
    }

    public String getHelpVideoUrl() {
        return helpVideoUrl;
    }

    public void setHelpVideoUrl(String helpVideoUrl) {
        this.helpVideoUrl = helpVideoUrl;
    }

    public String getAdFailUrl() {
        return adFailUrl;
    }

    public void setAdFailUrl(String adFailUrl) {
        this.adFailUrl = adFailUrl;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public List<HomeDataItem> getGiveawayCodeList() {
        return giveawayCodeList;
    }
}
