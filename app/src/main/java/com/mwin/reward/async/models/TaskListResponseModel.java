package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class TaskListResponseModel {
    @Expose
    private String userToken;
    @Expose
    private String adFailUrl;
    @Expose
    private String bgColor;
    @Expose
    private String currentPage;
    @Expose
    private String homeNote;

    @Expose
    private TopAds topAds;
    @Expose
    private List<HomeSliderItem> homeSlider;
    @Expose
    private String message;
    @Expose
    private String screenNo;
    @Expose
    private String status;
    @Expose
    private List<TaskListDataItem> taskOffers;
    @Expose
    private String tigerInApp;
    @Expose
    private Long totalIteam;
    @Expose
    private Long totalPage;
    @Expose
    private String url;

    @Expose
    private String earningPoint;

    @Expose
    private String horizontalTaskLabel;
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

    public String getAdFailUrl() {
        return adFailUrl;
    }

    public void setAdFailUrl(String adFailUrl) {
        this.adFailUrl = adFailUrl;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getHomeNote() {
        return homeNote;
    }

    public void setHomeNote(String homeNote) {
        this.homeNote = homeNote;
    }

    public List<HomeSliderItem> getHomeSlider() {
        return homeSlider;
    }

    public void setHomeSlider(List<HomeSliderItem> homeSlider) {
        this.homeSlider = homeSlider;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getScreenNo() {
        return screenNo;
    }

    public void setScreenNo(String screenNo) {
        this.screenNo = screenNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TaskListDataItem> getTaskOffers() {
        return taskOffers;
    }

    public void setTaskOffers(List<TaskListDataItem> taskOffers) {
        this.taskOffers = taskOffers;
    }

    public String getTigerInApp() {
        return tigerInApp;
    }

    public void setTigerInApp(String tigerInApp) {
        this.tigerInApp = tigerInApp;
    }

    public Long getTotalIteam() {
        return totalIteam;
    }

    public void setTotalIteam(Long totalIteam) {
        this.totalIteam = totalIteam;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getHorizontalTaskLabel() {
        return horizontalTaskLabel;
    }

    public void setHorizontalTaskLabel(String horizontalTaskLabel) {
        this.horizontalTaskLabel = horizontalTaskLabel;
    }
}
