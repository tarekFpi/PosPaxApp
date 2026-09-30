package com.paymentsave.paymentsave.responses.TransactionListResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.paymentsave.paymentsave.responses.TransactionDetailsResponse.TransactionDetailsBusiness;
import com.paymentsave.paymentsave.responses.TransactionDetailsResponse.TransactionDetailsBusinessTerminal;

public class TransactionListResult {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("business")
    @Expose
    private TransactionDetailsBusiness business;
    @SerializedName("business_terminal")
    @Expose
    private TransactionDetailsBusinessTerminal businessTerminal;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("split_bill_id")
    @Expose
    private String splitBillId;
    @SerializedName("receipt_id")
    @Expose
    private String receiptId;
    @SerializedName("transaction_type")
    @Expose
    private String transactionType;
    @SerializedName("pan")
    @Expose
    private String pan;
    @SerializedName("uti")
    @Expose
    private String uti;
    @SerializedName("rrn")
    @Expose
    private String rrn;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("error_text")
    @Expose
    private String errorText;
    @SerializedName("gratuity_amount")
    @Expose
    private String gratuityAmount;
    @SerializedName("cashback_amount")
    @Expose
    private String cashbackAmount = "0.00";
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("tnx_note")
    @Expose
    private String tnxNote;
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
    @SerializedName("auth_code")
    @Expose
    private String authCode;
    @SerializedName("response_code")
    @Expose
    private String responseCode;
    @SerializedName("merchant")
    @Expose
    private int merchant;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("is_submitted")
    @Expose
    private boolean isSubmitted;

    /**
     * No args constructor for use in serialization
     */

    public TransactionListResult() {
    }

    public TransactionListResult(int id, TransactionDetailsBusiness business, TransactionDetailsBusinessTerminal businessTerminal, String createdAt, String updatedAt, String receiptId, String transactionType, String pan, String uti, String amount, String discount, boolean approved, boolean cancelled, boolean sigRequired, boolean pinVerified, String currency, String terminalId, String merchantUid, String cardType, String authCode, String responseCode, int merchant, int status, boolean isSubmitted) {
        this.id = id;
        this.business = business;
        this.businessTerminal = businessTerminal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.receiptId = receiptId;
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
        this.authCode = authCode;
        this.responseCode = responseCode;
        this.merchant = merchant;
        this.status = status;
        this.isSubmitted = isSubmitted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TransactionDetailsBusiness getBusiness() {
        return business;
    }

    public void setBusiness(TransactionDetailsBusiness business) {
        this.business = business;
    }

    public TransactionDetailsBusinessTerminal getBusinessTerminal() {
        return businessTerminal;
    }

    public void setBusinessTerminal(TransactionDetailsBusinessTerminal businessTerminal) {
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

    public String getSplitBillId() {
        return splitBillId;
    }

    public void setSplitBillId(String splitBillId) {
        this.splitBillId = splitBillId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getTnxNote() {
        return tnxNote;
    }

    public void setTnxNote(String tnxNote) {
        this.tnxNote = tnxNote;
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

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
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

    public String getCashbackAmount() {
        return cashbackAmount;
    }

    public void setCashbackAmount(String cashbackAmount) {
        this.cashbackAmount = cashbackAmount;
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

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }


    public boolean isSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }

    @Override
    public String toString() {
        return "TransactionListResult{" +
                "id=" + id +
                ", business=" + business +
                ", businessTerminal=" + businessTerminal +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", splitBillId='" + splitBillId + '\'' +
                ", receiptId='" + receiptId + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", pan='" + pan + '\'' +
                ", uti='" + uti + '\'' +
                ", rrn='" + rrn + '\'' +
                ", amount='" + amount + '\'' +
                ", gratuityAmount='" + gratuityAmount + '\'' +
                ", cashbackAmount='" + cashbackAmount + '\'' +
                ", discount='" + discount + '\'' +
                ", approved=" + approved +
                ", cancelled=" + cancelled +
                ", sigRequired=" + sigRequired +
                ", pinVerified=" + pinVerified +
                ", currency='" + currency + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", merchantUid='" + merchantUid + '\'' +
                ", cardType='" + cardType + '\'' +
                ", authCode='" + authCode + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", merchant=" + merchant +
                ", status=" + status +
                ", isSubmitted=" + isSubmitted +
                '}';
    }
}
