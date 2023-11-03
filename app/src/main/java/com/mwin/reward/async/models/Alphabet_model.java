package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class Alphabet_model implements Serializable {
    private String adFailUrl;
    private String gameText;
    @SerializedName("isDefaultAppluck")
    private String isDefaultAppluck;
    private String nextGameTime;
    @Expose
    private String userToken;
    private String totalGameCount;
    private String screenNo;

//    public String getGameText() {
//        return gameText;
//    }
    private String remainGameCount;
    private String makeTotal;
    @Expose
    private Long checkSpinNum;
    @SerializedName("daily_spinner_limit")
    private String dailySpinnerLimit;
    private String gameTime;
    private String winningPoints;
    @Expose
    private List<Alphabet_Item> data;
    @Expose
    private FloatingAds floatingAds;
    @Expose
    private String lastDate;
    private String total_sum;
    private String totalCount;
    @Expose
    private ExitDialog exitDialog;
    @Expose
    private String message;
    @SerializedName("remain_spin")
    private String remainSpin;
    @Expose
    private String spinTime;
    @Expose
    private String status;
    @Expose
    private String tigerInApp;
    @Expose
    private String todayDate;
    @Expose
    private TopAds topAds;
    @SerializedName("homeNote")
    private String homeNote;
    @Expose
    private String buttonImage;
    @Expose
    private String buttonTextColor;
    @Expose
    private String spinImage;
    @Expose
    private String backgroundImage;
    @SerializedName("timerTextColor")
    private String timerTextColor;
    @Expose
    private String labelBackgroundImage;
    private String points;
    @Expose
    private String earningPoint;
    @Expose
    private String creditPoint;
    @Expose
    private String isShowAds;
    @Expose
    private String isTodayTaskCompleted;
    @Expose
    private String taskNote;
    @Expose
    private String taskButton;
    @Expose
    private String taskId;
    @Expose
    private AppLuckAd appLuck;

    public AppLuckAd getAppLuck() {
        return appLuck;
    }
    public String getIsDefaultAppluck() {
        return isDefaultAppluck;
    }

    public String getNextGameTime() {
        return nextGameTime;
    }

    public void setNextGameTime(String nextGameTime) {
        this.nextGameTime = nextGameTime;
    }

    public String getGameText() {
        return gameText;
    }

    public void setGameText(String gameText) {
        this.gameText = gameText;
    }

    public String getTotalGameCount() {
        return totalGameCount;
    }

    public void setTotalGameCount(String totalGameCount) {
        this.totalGameCount = totalGameCount;
    }

    public String getScreenNo() {
        return screenNo;
    }

    public void setScreenNo(String screenNo) {
        this.screenNo = screenNo;
    }

    public String getMakeTotal() {
        return makeTotal;
    }

    public void setMakeTotal(String makeTotal) {
        this.makeTotal = makeTotal;
    }

    public String getGameTime() {
        return gameTime;
    }

    public void setGameTime(String gameTime) {
        this.gameTime = gameTime;
    }

    public String getWinningPoints() {
        return winningPoints;
    }

    public void setWinningPoints(String winningPoints) {
        this.winningPoints = winningPoints;
    }

    public String getTotal_sum() {
        return total_sum;
    }

    public void setTotal_sum(String total_sum) {
        this.total_sum = total_sum;
    }

    public String getRemainGameCount() {
        return remainGameCount;
    }

    public void setRemainGameCount(String remainGameCount) {
        this.remainGameCount = remainGameCount;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getAdFailUrl() {
        return adFailUrl;
    }

    public void setAdFailUrl(String adFailUrl) {
        this.adFailUrl = adFailUrl;
    }

    public Long getCheckSpinNum() {
        return checkSpinNum;
    }

    public void setCheckSpinNum(Long checkSpinNum) {
        this.checkSpinNum = checkSpinNum;
    }

    public String getDailySpinnerLimit() {
        return dailySpinnerLimit;
    }

    public void setDailySpinnerLimit(String dailySpinnerLimit) {
        this.dailySpinnerLimit = dailySpinnerLimit;
    }

    public List<Alphabet_Item> getData() {
        return data;
    }

    public void setData(List<Alphabet_Item> data) {
        this.data = data;
    }

    public FloatingAds getFloatingAds() {
        return floatingAds;
    }

    public void setFloatingAds(FloatingAds floatingAds) {
        this.floatingAds = floatingAds;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRemainSpin() {
        return remainSpin;
    }

    public void setRemainSpin(String remainSpin) {
        this.remainSpin = remainSpin;
    }

    public String getSpinTime() {
        return spinTime;
    }

    public void setSpinTime(String spinTime) {
        this.spinTime = spinTime;
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

    public String getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(String todayDate) {
        this.todayDate = todayDate;
    }

    public TopAds getTopAds() {
        return topAds;
    }

    public void setTopAds(TopAds topAds) {
        this.topAds = topAds;
    }

    public String getButtonImage() {
        return buttonImage;
    }

    public void setButtonImage(String buttonImage) {
        this.buttonImage = buttonImage;
    }

    public String getButtonTextColor() {
        return buttonTextColor;
    }

    public void setButtonTextColor(String buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }

    public String getSpinImage() {
        return spinImage;
    }

    public void setSpinImage(String spinImage) {
        this.spinImage = spinImage;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getLabelBackgroundImage() {
        return labelBackgroundImage;
    }

    public void setLabelBackgroundImage(String labelBackgroundImage) {
        this.labelBackgroundImage = labelBackgroundImage;
    }

    public String getEarningPoint() {
        return earningPoint;
    }

    public void setEarningPoint(String earningPoint) {
        this.earningPoint = earningPoint;
    }

    public String getCreditPoint() {
        return creditPoint;
    }

    public void setCreditPoint(String creditPoint) {
        this.creditPoint = creditPoint;
    }

    public String getTimerTextColor() {
        return timerTextColor;
    }

    public void setTimerTextColor(String timerTextColor) {
        this.timerTextColor = timerTextColor;
    }

    public String getHomeNote() {
        return homeNote;
    }

    public void setHomeNote(String homeNote) {
        this.homeNote = homeNote;
    }

    public ExitDialog getExitDialog() {
        return exitDialog;
    }

    public void setExitDialog(ExitDialog exitDialog) {
        this.exitDialog = exitDialog;
    }

    public String getIsShowAds() {
        return isShowAds;
    }

    public void setIsShowAds(String isShowAds) {
        this.isShowAds = isShowAds;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getIsTodayTaskCompleted() {
        return isTodayTaskCompleted;
    }

    public void setIsTodayTaskCompleted(String isTodayTaskCompleted) {
        this.isTodayTaskCompleted = isTodayTaskCompleted;
    }

    public String getTaskNote() {
        return taskNote;
    }

    public void setTaskNote(String taskNote) {
        this.taskNote = taskNote;
    }

    public String getTaskButton() {
        return taskButton;
    }

    public void setTaskButton(String taskButton) {
        this.taskButton = taskButton;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
