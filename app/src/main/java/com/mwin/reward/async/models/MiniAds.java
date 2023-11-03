package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

@SuppressWarnings("unused")
public class MiniAds implements Serializable {

    @Expose
    private String id;
    @Expose
    private String image;
    @Expose
    private String screenNo;
    @Expose
    private String url;
    @Expose
    private String taskId;
    @Expose
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getScreenNo() {
        return screenNo;
    }

    public void setScreenNo(String screenNo) {
        this.screenNo = screenNo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
