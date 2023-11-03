package com.mwin.reward.async.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApisResponse implements Serializable {
    @SerializedName("encrypt")
    private String encrypt;

    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }
}