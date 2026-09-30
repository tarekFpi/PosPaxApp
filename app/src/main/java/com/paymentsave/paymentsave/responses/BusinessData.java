package com.paymentsave.paymentsave.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessData {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("mid")
    @Expose
    private String mid;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("legal_name")
    @Expose
    private String legalName;
    @SerializedName("trading_name")
    @Expose
    private String tradingName;
    @SerializedName("slug")
    @Expose
    private String slug;
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
    @SerializedName("pbl_merchant_alias")
    @Expose
    private Object pblMerchantAlias;
    @SerializedName("pbl_secret_key")
    @Expose
    private Object pblSecretKey;
    @SerializedName("pbl_public_key")
    @Expose
    private Object pblPublicKey;
    @SerializedName("pbl_processor_id")
    @Expose
    private Object pblProcessorId;
    @SerializedName("merchant")
    @Expose
    private int merchant;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public boolean isLinkPaymentEnabled() {
        return linkPaymentEnabled;
    }

    public void setLinkPaymentEnabled(boolean linkPaymentEnabled) {
        this.linkPaymentEnabled = linkPaymentEnabled;
    }

    public Object getPblMerchantAlias() {
        return pblMerchantAlias;
    }

    public void setPblMerchantAlias(Object pblMerchantAlias) {
        this.pblMerchantAlias = pblMerchantAlias;
    }

    public Object getPblSecretKey() {
        return pblSecretKey;
    }

    public void setPblSecretKey(Object pblSecretKey) {
        this.pblSecretKey = pblSecretKey;
    }

    public Object getPblPublicKey() {
        return pblPublicKey;
    }

    public void setPblPublicKey(Object pblPublicKey) {
        this.pblPublicKey = pblPublicKey;
    }

    public Object getPblProcessorId() {
        return pblProcessorId;
    }

    public void setPblProcessorId(Object pblProcessorId) {
        this.pblProcessorId = pblProcessorId;
    }

    public int getMerchant() {
        return merchant;
    }

    public void setMerchant(int merchant) {
        this.merchant = merchant;
    }

}
