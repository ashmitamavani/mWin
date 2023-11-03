package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

@SuppressWarnings("unused")
public class SigninResponseModel implements Serializable {
    @Expose
    private String userToken;
    @Expose
    private String message;
    @Expose
    private String status;
    @Expose
    private UserProfileDetails userDetails;
    @Expose
    private String adFailUrl;

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
}
