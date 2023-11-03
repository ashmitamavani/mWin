package com.mwin.reward.async.models;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class PushNotificationPayload {

    @SerializedName("my-data-item")
    private String mMyDataItem;

    public String getMyDataItem() {
        return mMyDataItem;
    }

    public void setMyDataItem(String myDataItem) {
        mMyDataItem = myDataItem;
    }

}
