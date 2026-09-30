package com.paymentsave.paymentsave.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionCreateData {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("business")
    @Expose
    private int business;
    @SerializedName("business_terminal")
    @Expose
    private String businessTerminal;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("transaction_type")
    @Expose
    private String transactionType;
    @SerializedName("pan")
    @Expose
    private String pan;
    @SerializedName("uti")
    @Expose
    private String uti;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("gratuity_amount")
    @Expose
    private String gratuityAmount;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("approved")
    @Expose
    private boolean approved;
    @SerializedName("cancelled")
    @Expose
    private boolean cancelled;
    @SerializedName("sig_required")
    @Expose
    private boolean sigRequired;
    @SerializedName("pin_verified")
    @Expose
    private boolean pinVerified;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("terminal_id")
    @Expose
    private String terminalId;
    @SerializedName("merchant_uid")
    @Expose
    private String merchantUid;
    @SerializedName("card_type")
    @Expose
    private String cardType;
    @SerializedName("merchant")
    @Expose
    private int merchant;
    @SerializedName("status")
    @Expose
    private int status;

    /**
     * No args constructor for use in serialization
     */

    public TransactionCreateData() {
    }

    public TransactionCreateData(int id, int business, String businessTerminal, String createdAt, String updatedAt, String transactionType, String pan, String uti, String amount, String discount, boolean approved, boolean cancelled, boolean sigRequired, boolean pinVerified, String currency, String terminalId, String merchantUid, String cardType, int merchant, int status) {
        this.id = id;
        this.business = business;
        this.businessTerminal = businessTerminal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.transactionType = transactionType;
        this.pan = pan;
        this.uti = uti;
        this.amount = amount;
        this.discount = discount;
        this.approved = approved;
        this.cancelled = cancelled;
        this.sigRequired = sigRequired;
        this.pinVerified = pinVerified;
        this.currency = currency;
        this.terminalId = terminalId;
        this.merchantUid = merchantUid;
        this.cardType = cardType;
        this.merchant = merchant;
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBusiness() {
        return business;
    }

    public void setBusiness(int business) {
        this.business = business;
    }

    public String getBusinessTerminal() {
        return businessTerminal;
    }

    public void setBusinessTerminal(String businessTerminal) {
        this.businessTerminal = businessTerminal;
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getUti() {
        return uti;
    }

    public void setUti(String uti) {
        this.uti = uti;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getGratuityAmount() {
        return gratuityAmount;
    }

    public void setGratuityAmount(String gratuityAmount) {
        this.gratuityAmount = gratuityAmount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isSigRequired() {
        return sigRequired;
    }

    public void setSigRequired(boolean sigRequired) {
        this.sigRequired = sigRequired;
    }

    public boolean isPinVerified() {
        return pinVerified;
    }

    public void setPinVerified(boolean pinVerified) {
        this.pinVerified = pinVerified;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getMerchantUid() {
        return merchantUid;
    }

    public void setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public int getMerchant() {
        return merchant;
    }

    public void setMerchant(int merchant) {
        this.merchant = merchant;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "TransactionDetailsData{" +
                "id=" + id +
                ", business=" + business +
                ", businessTerminal=" + businessTerminal +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", pan='" + pan + '\'' +
                ", uti='" + uti + '\'' +
                ", amount='" + amount + '\'' +
                ", discount='" + discount + '\'' +
                ", approved=" + approved +
                ", cancelled=" + cancelled +
                ", sigRequired=" + sigRequired +
                ", pinVerified=" + pinVerified +
                ", currency='" + currency + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", merchantUid='" + merchantUid + '\'' +
                ", cardType='" + cardType + '\'' +
                ", merchant=" + merchant +
                ", status=" + status +
                '}';
    }
}
