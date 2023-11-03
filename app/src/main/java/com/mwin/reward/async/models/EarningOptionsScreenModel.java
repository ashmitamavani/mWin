package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EarningOptionsScreenModel implements Serializable {

    @SerializedName("status")
    private String status;
    @SerializedName("userToken")
    private String userToken;
    @SerializedName("earningPoint")
    private String earningPoint;
    @SerializedName("dailyBonus")
    private EverydayBonus dailyBonus;

    @SerializedName("message")
    private String message;

    @SerializedName("homeNote")
    private String homeNote;

    @SerializedName("rewardDataList")
    private List<HomeDataListItem> rewardDataList;

    @SerializedName("adFailUrl")
    private String adFailUrl;
    @SerializedName("tigerInApp")
    private String tigerInApp;
    @SerializedName("todayCompletedTask")
    private String todayCompletedTask;
    @Expose
    private String isTodayTaskCompleted;
    @Expose
    private String taskNote;
    @Expose
    private String taskButton;
    @Expose
    private String taskId;
    @Expose
    private String screenNo;
    @Expose
    private List<HomeDataItem> taskList;
    @SerializedName("isShowGiveawayCode")
    private String isShowGiveawayCode;

    @SerializedName("giveawayCode")
    private String giveawayCode;
    @SerializedName("homeDialog")
    private HomeDialog homeDialog;
    @SerializedName("footerImage")
    private String footerImage;
    @SerializedName("isShowAdjoeLeaderboardIcon")
    private String isShowAdjoeLeaderboardIcon;
    @SerializedName("top_offers")
    private List<TaskListDataItem> top_offers;
    @SerializedName("isDefaultAppluck")
    private String isDefaultAppluck;
    @Expose
    private AppLuckAd appLuck;

    public AppLuckAd getAppLuck() {
        return appLuck;
    }
    public String getEarningPoint() {
        return earningPoint;
    }

    public String getIsDefaultAppluck() {
        return isDefaultAppluck;
    }

    public List<TaskListDataItem> getTop_offers() {
        return top_offers;
    }

    public String getIsShowAdjoeLeaderboardIcon() {
        return isShowAdjoeLeaderboardIcon;
    }

    public String getScreenNo() {
        return screenNo;
    }

    public void setScreenNo(String screenNo) {
        this.screenNo = screenNo;
    }

    public String getFooterImage() {
        return footerImage;
    }

    public void setFooterImage(String footerImage) {
        this.footerImage = footerImage;
    }

    public String getIsShowGiveawayCode() {
        return isShowGiveawayCode;
    }

    public void setIsShowGiveawayCode(String isShowGiveawayCode) {
        this.isShowGiveawayCode = isShowGiveawayCode;
    }

    public String getTigerInApp() {
        return tigerInApp;
    }

    public void setTigerInApp(String tigerInApp) {
        this.tigerInApp = tigerInApp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EverydayBonus getDailyBonus() {
        return dailyBonus;
    }

    public void setDailyBonus(EverydayBonus dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHomeNote() {
        return homeNote;
    }

    public void setHomeNote(String homeNote) {
        this.homeNote = homeNote;
    }

    public List<HomeDataListItem> getRewardDataList() {
        return rewardDataList;
    }

    public void setRewardDataList(List<HomeDataListItem> rewardDataList) {
        this.rewardDataList = rewardDataList;
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

    public String getTodayCompletedTask() {
        return todayCompletedTask;
    }

    public void setTodayCompletedTask(String todayCompletedTask) {
        this.todayCompletedTask = todayCompletedTask;
    }

    public List<HomeDataItem> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<HomeDataItem> taskList) {
        this.taskList = taskList;
    }

    public String getIsTodayTaskCompleted() {
        return isTodayTaskCompleted;
    }

    public String getTaskNote() {
        return taskNote;
    }

    public String getTaskButton() {
        return taskButton;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public HomeDialog getHomeDialog() {
        return homeDialog;
    }

    public void setHomeDialog(HomeDialog homeDialog) {
        this.homeDialog = homeDialog;
    }

    public String getGiveawayCode() {
        return giveawayCode;
    }
}