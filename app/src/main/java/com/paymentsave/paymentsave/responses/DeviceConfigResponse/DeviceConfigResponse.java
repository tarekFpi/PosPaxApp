package com.paymentsave.paymentsave.responses.DeviceConfigResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceConfigResponse {
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private DeviceConfigData data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DeviceConfigData getData() {
        return data;
    }

    public void setData(DeviceConfigData data) {
        this.data = data;
    }



    @Override
    public String toString() {
        return "DeviceConfigResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
