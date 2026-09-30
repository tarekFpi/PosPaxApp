package com.paymentsave.paymentsave.responses.DeviceConfigResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceConfigData {
    @SerializedName("terminal")
    @Expose
    private int terminal;
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("pre_auth_enabled")
    @Expose
    private boolean preAuthEnabled;
    @SerializedName("cnp")
    @Expose
    private boolean cnp;
    @SerializedName("auto_batch_enabled")
    @Expose
    private boolean autoBatchEnabled;
    @SerializedName("batch_time")
    @Expose
    private Object batchTime;
    @SerializedName("is_activated")
    @Expose
    private boolean isActivated;
    @SerializedName("is_live")
    @Expose
    private boolean isLive;

    @SerializedName("business")
    @Expose
    private BusinessInfo business;
    @SerializedName("marchant_alerts")
    @Expose
    private String marchantAlerts;
    @SerializedName("gratuity_type")
    @Expose
    private String gratuityType;
    @SerializedName("gratuity_suggestion_value")
    @Expose
    private List<Object> gratuityOptions;
    @SerializedName("cashback_suggestion_value")
    @Expose
    private List<Object> cashbackOptions;
    @SerializedName("contact_support_email")
    @Expose
    private String supportEmail;
    @SerializedName("contact_support_phone")
    @Expose
    private String supportPhone;

    @SerializedName("e_receipt")
    @Expose
    private boolean eReceipt;


    public boolean isEReceipt() {
        return eReceipt;
    }

    public void setEReceipt(boolean eReceipt) {
        this.eReceipt = eReceipt;
    }

    public int getTerminal() {
        return terminal;
    }

    public void setTerminal(int terminal) {
        this.terminal = terminal;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public boolean isPreAuthEnabled() {
        return preAuthEnabled;
    }

    public void setPreAuthEnabled(boolean preAuthEnabled) {
        this.preAuthEnabled = preAuthEnabled;
    }

    public boolean isCnp() {
        return cnp;
    }

    public void setCnp(boolean cnp) {
        this.cnp = cnp;
    }

    public boolean isAutoBatchEnabled() {
        return autoBatchEnabled;
    }

    public void setAutoBatchEnabled(boolean autoBatchEnabled) {
        this.autoBatchEnabled = autoBatchEnabled;
    }

    public Object getBatchTime() {
        return batchTime;
    }

    public void setBatchTime(Object batchTime) {
        this.batchTime = batchTime;
    }

    public boolean isIsActivated() {
        return isActivated;
    }

    public void setIsActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public boolean isIsLive() {
        return isLive;
    }

    public void setIsLive(boolean isLive) {
        this.isLive = isLive;
    }

    public BusinessInfo getBusiness() {
        return business;
    }

    public void setBusiness(BusinessInfo business) {
        this.business = business;
    }

    public String getGratuityType() {
        return gratuityType;
    }

    public void setGratuityType(String gratuityType) {
        this.gratuityType = gratuityType;
    }

    public List<Object> getGratuityOptions() {
        return gratuityOptions;
    }

    public void setGratuityOptions(List<Object> gratuityOptions) {
        this.gratuityOptions = gratuityOptions;
    }

    public List<Object> getCashbackOptions() {
        return cashbackOptions;
    }

    public void setCashbackOptions(List<Object> cashbackOptions) {
        this.cashbackOptions = cashbackOptions;
    }

    public String getMarchantAlerts() {
        return marchantAlerts;
    }

    public void setMarchantAlerts(String marchantAlerts) {
        this.marchantAlerts = marchantAlerts;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public String getSupportPhone() {
        return supportPhone;
    }

    public void setSupportPhone(String supportPhone) {
        this.supportPhone = supportPhone;
    }

    @Override
    public String toString() {
        return "DeviceConfigData{" +
                "terminal=" + terminal +
                ", tid='" + tid + '\'' +
                ", preAuthEnabled=" + preAuthEnabled +
                ", cnp=" + cnp +
                ", autoBatchEnabled=" + autoBatchEnabled +
                ", batchTime=" + batchTime +
                ", isActivated=" + isActivated +
                ", isLive=" + isLive +
                ", business=" + business +
                ", marchantAlerts='" + marchantAlerts + '\'' +
                ", gratuityType='" + gratuityType + '\'' +
                ", gratuityOptions=" + gratuityOptions +
                ", supportEmail='" + supportEmail + '\'' +
                ", supportPhone='" + supportPhone + '\'' +
                '}';
    }
}
