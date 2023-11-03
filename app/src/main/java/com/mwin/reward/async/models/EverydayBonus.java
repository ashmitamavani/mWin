package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EverydayBonus implements Serializable {

    @SerializedName("lastClaimedDay")
    private String lastClaimedDay;

    @SerializedName("isTodayClaimed")
    private String isTodayClaimed;

    @SerializedName("data")
    private List<EverydayBonusItem> data;

    @Expose
    private TopAds topAds;

    @SerializedName("homeNote")
    private String homeNote;

    @Expose
    private String isWatchWebsite;
    @Expose
    private String watchWebsiteUrl;
    @Expose
    private String watchWebsiteTime;
    @SerializedName("isDefaultAppluck")
    private String isDefaultAppluck;

    public String getIsDefaultAppluck() {
        return isDefaultAppluck;
    }

    public String getLastClaimedDay() {
        return lastClaimedDay;
    }

    public void setLastClaimedDay(String lastClaimedDay) {
        this.lastClaimedDay = lastClaimedDay;
    }

    public List<EverydayBonusItem> getData() {
        return data;
    }

    public void setData(List<EverydayBonusItem> data) {
        this.data = data;
    }

    public String getIsTodayClaimed() {
        return isTodayClaimed;
    }

    public void setIsTodayClaimed(String isTodayClaimed) {
        this.isTodayClaimed = isTodayClaimed;
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

    public String getIsWatchWebsite() {
        return isWatchWebsite;
    }

    public void setIsWatchWebsite(String isWatchWebsite) {
        this.isWatchWebsite = isWatchWebsite;
    }

    public String getWatchWebsiteUrl() {
        return watchWebsiteUrl;
    }

    public void setWatchWebsiteUrl(String watchWebsiteUrl) {
        this.watchWebsiteUrl = watchWebsiteUrl;
    }

    public String getWatchWebsiteTime() {
        return watchWebsiteTime;
    }

    public void setWatchWebsiteTime(String watchWebsiteTime) {
        this.watchWebsiteTime = watchWebsiteTime;
    }
}
