package com.paymentsave.paymentsave.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionCreateResponse {

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
    private TransactionCreateData data;
//    private TransactionCreateData data;

    /**
     * No args constructor for use in serialization
     */
    public TransactionCreateResponse() {
    }

    /**
     * @param code
     * @param data
     * @param message
     * @param status
     */
    public TransactionCreateResponse(int code, Object message, String status, TransactionCreateData data) {
        super();
        this.code = code;
        this.message = message;
        this.status = status;
        this.data = data;
    }

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

    public TransactionCreateData getData() {
        return data;
    }

    public void setData(TransactionCreateData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TransactionCreateResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("code");
        sb.append('=');
        sb.append(this.code);
        sb.append(',');
        sb.append("message");
        sb.append('=');
        sb.append(((this.message == null) ? "<null>" : this.message));
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null) ? "<null>" : this.status));
        sb.append(',');
        sb.append("data");
        sb.append('=');
        sb.append(((this.data == null) ? "<null>" : this.data));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }


}
