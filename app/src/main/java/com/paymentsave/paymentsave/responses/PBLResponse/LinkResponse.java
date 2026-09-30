package com.paymentsave.paymentsave.responses.PBLResponse;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class LinkResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("data")
    @Expose
    private LinkResponseData data;
    @SerializedName("message")
    @Expose
    private Object message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public LinkResponseData getData() {
        return data;
    }

    public void setData(LinkResponseData data) {
        this.data = data;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

}