package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class RedeemOptionsResponseModel {
    @SerializedName("homeSlider")
    private List<HomeSliderItem> homeSlider;
    @SerializedName("userToken")
    private String userToken;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private String status;
    @SerializedName("type")
    private List<WithdrawType> Type;
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

    public List<WithdrawType> getType() {
        return Type;
    }

    public void setType(List<WithdrawType> type) {
        Type = type;
    }

    public List<HomeSliderItem> getHomeSlider() {
        return homeSlider;
    }

    public void setHomeSlider(List<HomeSliderItem> homeSlider) {
        this.homeSlider = homeSlider;
    }

    public String getUserToken() {
        return userToken;
    }
}
