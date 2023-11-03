package com.mwin.reward.async.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HomeSliderItem implements Serializable {

    @SerializedName("image")
    private String image;

    @SerializedName("offerId")
    private String offerId;

    @SerializedName("screenNo")
    private String screenNo;

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    @SerializedName("matchId")
    private String matchId;

    @SerializedName("gameId")
    private String gameId;

    @SerializedName("isShowDetails")
    private String isShowDetails;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOfferId() {
        return offerId;
    }

    public String getScreenNo() {
        return screenNo;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getIsShowDetails() {
        return isShowDetails;
    }
}