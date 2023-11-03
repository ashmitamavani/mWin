package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class AdjoeLeaderboardItem {
    @Expose
    private String userId;
    @Expose
    private String points;
    @Expose
    private String firstName;
    @Expose
    private String lastName;
    @Expose
    private String profileImage;

    public String getUserId() {
        return userId;
    }

    public String getPoints() {
        return points;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
