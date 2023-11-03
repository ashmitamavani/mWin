package com.mwin.reward.async.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class AdjoeLeaderboardHistoryItem {
    @Expose
    private String id;
    @Expose
    private String declarationDate;

    @SerializedName("data")
    private List<AdjoeLeaderboardItem> data;

    public String getId() {
        return id;
    }

    public String getDeclarationDate() {
        return declarationDate;
    }

    public List<AdjoeLeaderboardItem> getData() {
        return data;
    }
}
