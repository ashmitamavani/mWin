package com.mwin.reward.async.models;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ScratchCardList {

    @SerializedName("entryDate")
    private String entryDate;
    @SerializedName("id")
    private String id;
    @SerializedName("isScratched")
    private String isScratched;
    @SerializedName("scratchCardPoints")
    private String scratchCardPoints;
    @SerializedName("scratchedDate")
    private String scratchedDate;
    @SerializedName("taskId")
    private String taskId;
    @SerializedName("taskTitle")
    private String taskTitle;
    @SerializedName("updateDate")
    private String updateDate;
    @SerializedName("userId")
    private String userId;

    public String getEntryDate() {
        return entryDate;
    }

    public String getId() {
        return id;
    }

    public String getIsScratched() {
        return isScratched;
    }

    public void setIsScratched(String isScratched) {
        this.isScratched = isScratched;
    }

    public String getScratchCardPoints() {
        return scratchCardPoints;
    }

    public String getScratchedDate() {
        return scratchedDate;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getUserId() {
        return userId;
    }
}
