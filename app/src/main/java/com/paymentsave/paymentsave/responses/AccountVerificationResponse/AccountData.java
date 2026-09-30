package com.paymentsave.paymentsave.responses.AccountVerificationResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountData {
    @SerializedName("account_id")
    @Expose
    private int accountId;
    @SerializedName("legal_name")
    @Expose
    private String legalName;
    @SerializedName("trading_name")
    @Expose
    private String tradingName;
    @SerializedName("business_phone_number")
    @Expose
    private String businessPhoneNumber;
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
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

    public String getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    public void setBusinessPhoneNumber(String businessPhoneNumber) {
        this.businessPhoneNumber = businessPhoneNumber;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "AccountData{" +
                "accountId=" + accountId +
                ", legalName='" + legalName + '\'' +
                ", tradingName='" + tradingName + '\'' +
                ", businessPhoneNumber='" + businessPhoneNumber + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
