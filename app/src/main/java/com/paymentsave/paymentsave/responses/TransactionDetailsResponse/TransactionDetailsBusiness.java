package com.paymentsave.paymentsave.responses.TransactionDetailsResponse;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionDetailsBusiness {
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "business_id")
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
    @SerializedName("trading_address")
    @Expose
    private String tradingAddress;
    @SerializedName("business_email")
    @Expose
    private String businessEmail;
    @SerializedName("business_phone_number")
    @Expose
    private String businessPhoneNumber;
    @SerializedName("merchant")
    @Expose
    @ColumnInfo(name = "business_merchant_id")
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TransactionDetailsBusiness.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(this.id);
        sb.append(',');
        sb.append("createdAt");
        sb.append('=');
        sb.append(((this.createdAt == null)?"<null>":this.createdAt));
        sb.append(',');
        sb.append("updatedAt");
        sb.append('=');
        sb.append(((this.updatedAt == null)?"<null>":this.updatedAt));
        sb.append(',');
        sb.append("mid");
        sb.append('=');
        sb.append(((this.mid == null)?"<null>":this.mid));
        sb.append(',');
        sb.append("logo");
        sb.append('=');
        sb.append(((this.logo == null)?"<null>":this.logo));
        sb.append(',');
        sb.append("legalName");
        sb.append('=');
        sb.append(((this.legalName == null)?"<null>":this.legalName));
        sb.append(',');
        sb.append("tradingName");
        sb.append('=');
        sb.append(((this.tradingName == null)?"<null>":this.tradingName));
        sb.append(',');
        sb.append("tradingAddress");
        sb.append('=');
        sb.append(((this.tradingAddress == null)?"<null>":this.tradingAddress));
        sb.append(',');
        sb.append("businessEmail");
        sb.append('=');
        sb.append(((this.businessEmail == null)?"<null>":this.businessEmail));
        sb.append(',');
        sb.append("businessPhoneNumber");
        sb.append('=');
        sb.append(((this.businessPhoneNumber == null)?"<null>":this.businessPhoneNumber));
        sb.append(',');
        sb.append("merchant");
        sb.append('=');
        sb.append(this.merchant);
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
