package com.mwin.reward.async.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BlockItem implements Serializable {

    @SerializedName("block_points")
    private String blockPoints;

    @SerializedName("block_bg")
    private String blockBg;

    @SerializedName("icon")
    private String icon;

    @SerializedName("block_id")
    private String blockId;

    public String getBlockPoints() {
        return blockPoints;
    }

    public String getBlockBg() {
        return blockBg;
    }

    public String getIcon() {
        return icon;
    }

    public String getBlockId() {
        return blockId;
    }
}