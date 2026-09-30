package com.paymentsave.paymentsave.responses.AccountVerificationResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountVerificationResponse {
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private AccountData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AccountData getData() {
        return data;
    }

    public void setData(AccountData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AccountVerificationResponse{" +
                "code=" + code +
                ", message=" + message +
                ", status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
