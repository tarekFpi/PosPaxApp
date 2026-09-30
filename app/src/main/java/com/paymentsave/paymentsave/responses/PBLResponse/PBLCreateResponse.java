package com.paymentsave.paymentsave.responses.PBLResponse;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PBLCreateResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("data")
    @Expose
    private PBLCreateData data;
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

    public PBLCreateData getData() {
        return data;
    }

    public void setData(PBLCreateData data) {
        this.data = data;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PBLCreateResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null)?"<null>":this.status));
        sb.append(',');
        sb.append("code");
        sb.append('=');
        sb.append(this.code);
        sb.append(',');
        sb.append("data");
        sb.append('=');
        sb.append(((this.data == null)?"<null>":this.data));
        sb.append(',');
        sb.append("message");
        sb.append('=');
        sb.append(((this.message == null)?"<null>":this.message));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
