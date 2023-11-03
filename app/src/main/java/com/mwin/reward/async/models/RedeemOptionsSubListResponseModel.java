package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class RedeemOptionsSubListResponseModel {
    @Expose
    private String userToken;
    @Expose
    private String adFailUrl;
    @Expose
    private ExitDialog exitDialog;
    @Expose
    private String homeNote;
    @Expose
    private String message;
    @Expose
    private String status;
    @Expose
    private String tigerInApp;
    @Expose
    private TopAds topAds;
    @Expose
    private String country;
    @Expose
    private List<WithdrawList> withdrawList;
    @SerializedName("isDefaultAppluck")
    private String isDefaultAppluck;
    @Expose
    private AppLuckAd appLuck;

    public AppLuckAd getAppLuck() {
        return appLuck;
    }
    @Expose
    private String isRateus;

    public String getIsRateus() {
        return isRateus;
    }
    public String getIsDefaultAppluck() {
        return isDefaultAppluck;
    }

    public String getAdFailUrl() {
        return adFailUrl;
    }

    public void setAdFailUrl(String adFailUrl) {
        this.adFailUrl = adFailUrl;
    }

    public ExitDialog getExitDialog() {
        return exitDialog;
    }

    public void setExitDialog(ExitDialog exitDialog) {
        this.exitDialog = exitDialog;
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

    public String getTigerInApp() {
        return tigerInApp;
    }

    public void setTigerInApp(String tigerInApp) {
        this.tigerInApp = tigerInApp;
    }

    public TopAds getTopAds() {
        return topAds;
    }

    public void setTopAds(TopAds topAds) {
        this.topAds = topAds;
    }

    public List<WithdrawList> getWithdrawList() {
        return withdrawList;
    }

    public void setWithdrawList(List<WithdrawList> withdrawList) {
        this.withdrawList = withdrawList;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
