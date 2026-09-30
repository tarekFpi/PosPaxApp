package com.paymentsave.paymentsave.responses.DeviceConfigResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessInfo {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("mid")
    @Expose
    private String mid;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("legal_name")
    @Expose
    private String legalName;
    @SerializedName("trading_name")
    @Expose
    private String tradingName;
    @SerializedName("trading_address")
    @Expose
    private String tradingAddress;
    @SerializedName("business_email")
    @Expose
    private String businessEmail;
    @SerializedName("business_phone_number")
    @Expose
    private String businessPhoneNumber;
    @SerializedName("link_payment_enabled")
    @Expose
    private boolean linkPaymentEnabled;
    @SerializedName("merchant")
    @Expose
    private int merchant;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getTradingName() {
        return tradingName;
    }

    public void setTradingName(String tradingName) {
        this.tradingName = tradingName;
    }

    public String getTradingAddress() {
        return tradingAddress;
    }

    public void setTradingAddress(String tradingAddress) {
        this.tradingAddress = tradingAddress;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    public void setBusinessPhoneNumber(String businessPhoneNumber) {
        this.businessPhoneNumber = businessPhoneNumber;
    }

    public int getMerchant() {
        return merchant;
    }

    public void setMerchant(int merchant) {
        this.merchant = merchant;
    }

    public boolean isLinkPaymentEnabled() {
        return linkPaymentEnabled;
    }

    public void setLinkPaymentEnabled(boolean linkPaymentEnabled) {
        this.linkPaymentEnabled = linkPaymentEnabled;
    }
}
