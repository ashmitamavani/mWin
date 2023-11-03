package com.mwin.reward.async.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EverydayBonusItem implements Serializable {

    @SerializedName("day_id")
    private String day_id;

    @SerializedName("day_points")
    private String day_points;

    public String getDay_id() {
        return day_id;
    }

    public String getDay_points() {
        return day_points;
    }
}
