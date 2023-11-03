package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("unused")
public class EverydayBonusDataModel implements Serializable {
    @Expose
    private String adFailUrl;
    @Expose
    private String userToken;
    @Expose
    private String id;

    @Expose
    private String taskId;
    @Expose
    private String message;
    @Expose
    private String earningPoint;
    @Expose
    private String btnColor;
    @Expose
    private String btnName;
    @Expose
    private String description;
    @Expose
    private String image;
    @Expose
    private String isShowInterstitial;
    @Expose
    private String screenNo;
    @Expose
    private String title;
    @Expose
    private String url;

    @Expose
    private String status;

    @Expose
    private String lastClaimedDay;

    @Expose
    private String isTodayClaimed;
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

    public void setTigerInApp(String tigerInApp) {
        this.tigerInApp = tigerInApp;
    }

    public String getAdFailUrl() {
        return adFailUrl;
    }

    public void setAdFailUrl(String adFailUrl) {
        this.adFailUrl = adFailUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEarningPoint() {
        return earningPoint;
    }

    public void setEarningPoint(String earningPoint) {
        this.earningPoint = earningPoint;
    }

    public String getBtnColor() {
        return btnColor;
    }

    public void setBtnColor(String btnColor) {
        this.btnColor = btnColor;
    }

    public String getBtnName() {
        return btnName;
    }

    public void setBtnName(String btnName) {
        this.btnName = btnName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsShowInterstitial() {
        return isShowInterstitial;
    }

    public void setIsShowInterstitial(String isShowInterstitial) {
        this.isShowInterstitial = isShowInterstitial;
    }

    public String getScreenNo() {
        return screenNo;
    }

    public void setScreenNo(String screenNo) {
        this.screenNo = screenNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getLastClaimedDay() {
        return lastClaimedDay;
    }

    public void setLastClaimedDay(String lastClaimedDay) {
        this.lastClaimedDay = lastClaimedDay;
    }

    public String getIsTodayClaimed() {
        return isTodayClaimed;
    }

    public void setIsTodayClaimed(String isTodayClaimed) {
        this.isTodayClaimed = isTodayClaimed;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
