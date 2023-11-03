package com.mwin.reward.async.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubMenuListItem implements Serializable {

    @SerializedName("isBlink")
    private String isBlink;
    @SerializedName("icon")
    private String icon;
    @SerializedName("label")
    private String label;
    @SerializedName("title")
    private String title;
    @SerializedName("url")
    private String url;
    @SerializedName("isLoginNeed")
    private String isLoginNeed;

    @SerializedName("titleColor")
    private String titleColor;

    @SerializedName("offerId")
    private String offerId;

    @SerializedName("gameId")
    private String gameId;

    @SerializedName("labelColor")
    private String labelColor;

    @SerializedName("labelBGColor")
    private String labelBGColor;

    @SerializedName("screenNo")
    private String screenNo;

    public String getIsBlink() {
        return isBlink;
    }

    public String getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getIsLoginNeed() {
        return isLoginNeed;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public String getOfferId() {
        return offerId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getLabelColor() {
        return labelColor;
    }

    public String getScreenNo() {
        return screenNo;
    }

    public String getLabelBGColor() {
        return labelBGColor;
    }
}