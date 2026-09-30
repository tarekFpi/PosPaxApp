package com.paymentsave.paymentsave.responses.PBLResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PBLCreateData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("business_ref")
    @Expose
    private int businessRef;
    @SerializedName("merchant_ref")
    @Expose
    private int merchantRef;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("pid")
    @Expose
    private String pid;
    @SerializedName("link_url")
    @Expose
    private String linkUrl;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("expires_duration")
    @Expose
    private String expiresDuration;
    @SerializedName("expires_at")
    @Expose
    private String expiresAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBusinessRef() {
        return businessRef;
    }

    public void setBusinessRef(int businessRef) {
        this.businessRef = businessRef;
    }

    public int getMerchantRef() {
        return merchantRef;
    }

    public void setMerchantRef(int merchantRef) {
        this.merchantRef = merchantRef;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpiresDuration() {
        return expiresDuration;
    }

    public void setExpiresDuration(String expiresDuration) {
        this.expiresDuration = expiresDuration;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PBLCreateData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("businessRef");
        sb.append('=');
        sb.append(this.businessRef);
        sb.append(',');
        sb.append("merchantRef");
        sb.append('=');
        sb.append(this.merchantRef);
        sb.append(',');
        sb.append("amount");
        sb.append('=');
        sb.append(((this.amount == null)?"<null>":this.amount));
        sb.append(',');
        sb.append("currency");
        sb.append('=');
        sb.append(((this.currency == null)?"<null>":this.currency));
        sb.append(',');
        sb.append("linkUrl");
        sb.append('=');
        sb.append(((this.linkUrl == null)?"<null>":this.linkUrl));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("expiresDuration");
        sb.append('=');
        sb.append(((this.expiresDuration == null)?"<null>":this.expiresDuration));
        sb.append(',');
        sb.append("expiresAt");
        sb.append('=');
        sb.append(((this.expiresAt == null)?"<null>":this.expiresAt));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
