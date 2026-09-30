package com.paymentsave.paymentsave.request_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionRequest {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("business")
    @Expose
    private int business;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("split_bill_id")
    @Expose
    private String splitBillId;
    @SerializedName("epos_session_id")
    @Expose
    private String eposSessionId;
    @SerializedName("receipt_id")
    @Expose
    private String receiptId;
    @SerializedName("transaction_type")
    @Expose
    private String transactionType;
    @SerializedName("error_text")
    @Expose
    private String errorText;
    @SerializedName("pan")
    @Expose
    private String pan;
    @SerializedName("uti")
    @Expose
    private String uti;
    @SerializedName("existing_uti")
    @Expose
    private String existingUti;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("cashback_amount")
    @Expose
    private String cashbackAmount;
    @SerializedName("gratuity_amount")
    @Expose
    private String gratuityAmount;
    @SerializedName("tnx_note")
    @Expose
    private String tnxNote;
    @SerializedName("discount")
    @Expose
    private String discount= "0";
    @SerializedName("approved")
    @Expose
    private boolean approved = false;
    @SerializedName("cancelled")
    @Expose
    private boolean cancelled = false;
    @SerializedName("sig_required")
    @Expose
    private boolean sigRequired;
    @SerializedName("pin_verified")
    @Expose
    private boolean pinVerified;
    @SerializedName("currency")
    @Expose
    private String currency = "GBP";
    @SerializedName("terminal_id")
    @Expose
    private String terminalId;
    @SerializedName("merchant_uid")
    @Expose
    private String merchantUid;
    @SerializedName("psn_code")
    @Expose
    private String psnCode;
    @SerializedName("card_type")
    @Expose
    private Object cardType;
    @SerializedName("status")
    @Expose
    private int status = 5;
    @SerializedName("merchant")
    @Expose
    private int merchant;


    @SerializedName("response_code")
    @Expose
    private String responseCode;
    @SerializedName("stan")
    @Expose
    private String stan;
    @SerializedName("auth_code")
    @Expose
    private String authCode;
    @SerializedName("rrn")
    @Expose
    private String rrn;
    @SerializedName("card_scheme")
    @Expose
    private String cardScheme;
    @SerializedName("mid")
    @Expose
    private String mid;
    @SerializedName("card_holder")
    @Expose
    private String cardHolder;
    @SerializedName("merchant_token_id")
    @Expose
    private String merchantTokenId;
    @SerializedName("card_expiry_date")
    @Expose
    private String cardExpDate;
    @SerializedName("card_start_date")
    @Expose
    private String cardStartDate;
    @SerializedName("response_text")
    @Expose
    private String responseText;

    @SerializedName("card_holder_receipt")
    @Expose
    private String cardHolderReceipt;
    @SerializedName("merchant_receipt")
    @Expose
    private String merchantReceipt;
    private boolean isResultResponse = false;

    public TransactionRequest(int id, String createdAt, String updatedAt, String authCode, String responseCode, String errorText, String receiptId, String transactionType, String pan, String uti, String amount, String cashbackAmount, String gratuityAmount, String discount, boolean approved, boolean cancelled, boolean sigRequired, boolean pinVerified, String currency, String terminalId, String merchantUid, String psnCode, Object cardType, int status, int merchant) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.authCode = authCode;
        this.responseCode = responseCode;
        this.errorText = errorText;
        this.receiptId = receiptId;
        this.transactionType = transactionType;
        this.pan = pan;
        this.uti = uti;
        this.amount = amount;
        this.cashbackAmount = cashbackAmount;
        this.gratuityAmount = gratuityAmount;
        this.discount = discount;
        this.approved = approved;
        this.cancelled = cancelled;
        this.sigRequired = sigRequired;
        this.pinVerified = pinVerified;
        this.currency = currency;
        this.terminalId = terminalId;
        this.merchantUid = merchantUid;
        this.psnCode = psnCode;
        this.cardType = cardType;
        this.status = status;
        this.merchant = merchant;
    }

    public TransactionRequest(int id, String createdAt, String updatedAt, String receiptId, String transactionType, String pan, String uti, String amount, String discount, boolean approved, boolean cancelled, boolean sigRequired, boolean pinVerified, String currency, String terminalId, String merchantUid, String psnCode, Object cardType, int status, int merchant, String responseCode, String stan, String authCode, String rrn, String cardScheme, String mid, String cardHolder, String merchantTokenId, String cardExpDate, String cardStartDate, String responseText) {
        this.id = id;
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
        this.psnCode = psnCode;
        this.cardType = cardType;
        this.status = status;
        this.merchant = merchant;
        this.responseCode = responseCode;
        this.stan = stan;
        this.authCode = authCode;
        this.rrn = rrn;
        this.cardScheme = cardScheme;
        this.mid = mid;
        this.cardHolder = cardHolder;
        this.merchantTokenId = merchantTokenId;
        this.cardExpDate = cardExpDate;
        this.cardStartDate = cardStartDate;
        this.responseText = responseText;
    }

    public TransactionRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isResultResponse() {
        return isResultResponse;
    }

    public void setResultResponse(boolean resultResponse) {
        isResultResponse = resultResponse;
    }

    //    public int getBusiness() {
//        return business;
//    }
//
//    public void setBusiness(int business) {
//        this.business = business;
//    }


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

    public String getEposSessionId() {
        return eposSessionId;
    }

    public void setEposSessionId(String eposSessionId) {
        this.eposSessionId = eposSessionId;
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

    public String getExistingUti() {
        return existingUti;
    }

    public void setExistingUti(String existingUti) {
        this.existingUti = existingUti;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCashbackAmount() {
        return cashbackAmount;
    }

    public void setCashbackAmount(String cashbackAmount) {
        this.cashbackAmount = cashbackAmount;
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

    public String getPsnCode() {
        return psnCode;
    }

    public void setPsnCode(String psnCode) {
        this.psnCode = psnCode;
    }

    public Object getCardType() {
        return cardType;
    }

    public void setCardType(Object cardType) {
        this.cardType = cardType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMerchant() {
        return merchant;
    }

    public void setMerchant(int merchant) {
        this.merchant = merchant;
    }

    public int getBusiness() {
        return business;
    }

    public void setBusiness(int business) {
        this.business = business;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getCardScheme() {
        return cardScheme;
    }

    public void setCardScheme(String cardScheme) {
        this.cardScheme = cardScheme;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getMerchantTokenId() {
        return merchantTokenId;
    }

    public void setMerchantTokenId(String merchantTokenId) {
        this.merchantTokenId = merchantTokenId;
    }

    public String getCardExpDate() {
        return cardExpDate;
    }

    public void setCardExpDate(String cardExpDate) {
        this.cardExpDate = cardExpDate;
    }

    public String getCardStartDate() {
        return cardStartDate;
    }

    public void setCardStartDate(String cardStartDate) {
        this.cardStartDate = cardStartDate;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getCardHolderReceipt() {
        return cardHolderReceipt;
    }

    public void setCardHolderReceipt(String cardHolderReceipt) {
        this.cardHolderReceipt = cardHolderReceipt;
    }

    public String getMerchantReceipt() {
        return merchantReceipt;
    }

    public void setMerchantReceipt(String merchantReceipt) {
        this.merchantReceipt = merchantReceipt;
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "id=" + id +
                ", business=" + business +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", splitBillId='" + splitBillId + '\'' +
                ", eposSessionId='" + eposSessionId + '\'' +
                ", receiptId='" + receiptId + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", pan='" + pan + '\'' +
                ", uti='" + uti + '\'' +
                ", existingUti='" + existingUti + '\'' +
                ", amount='" + amount + '\'' +
                ", cashbackAmount='" + cashbackAmount + '\'' +
                ", gratuityAmount='" + gratuityAmount + '\'' +
                ", discount='" + discount + '\'' +
                ", approved=" + approved +
                ", cancelled=" + cancelled +
                ", sigRequired=" + sigRequired +
                ", pinVerified=" + pinVerified +
                ", currency='" + currency + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", merchantUid='" + merchantUid + '\'' +
                ", psnCode='" + psnCode + '\'' +
                ", cardType=" + cardType +
                ", status=" + status +
                ", merchant=" + merchant +
                ", responseCode='" + responseCode + '\'' +
                ", stan='" + stan + '\'' +
                ", authCode='" + authCode + '\'' +
                ", rrn='" + rrn + '\'' +
                ", cardScheme='" + cardScheme + '\'' +
                ", mid='" + mid + '\'' +
                ", cardHolder='" + cardHolder + '\'' +
                ", merchantTokenId='" + merchantTokenId + '\'' +
                ", cardExpDate='" + cardExpDate + '\'' +
                ", cardStartDate='" + cardStartDate + '\'' +
                ", responseText='" + responseText + '\'' +
                ", cardHolderReceipt='" + cardHolderReceipt + '\'' +
                ", merchantReceipt='" + merchantReceipt + '\'' +
                ", isResultResponse=" + isResultResponse +
                '}';
    }
}

//    @SerializedName("created_at")
//    @Expose
//    private String createdAt;
//    @SerializedName("updated_at")
//    @Expose
//    private String updatedAt;
//    @SerializedName("transaction_type")
//    @Expose
//    private String transactionType;
//    @SerializedName("pan")
//    @Expose
//    private String pan;
//    @SerializedName("uti")
//    @Expose
//    private String uti;
//    @SerializedName("amount")
//    @Expose
//    private String amount;
//    @SerializedName("discount")
//    @Expose
//    private String discount;
//    @SerializedName("approved")
//    @Expose
//    private boolean approved;
//    @SerializedName("cancelled")
//    @Expose
//    private boolean cancelled;
//    @SerializedName("sig_required")
//    @Expose
//    private boolean sigRequired;
//    @SerializedName("pin_verified")
//    @Expose
//    private boolean pinVerified;
//    @SerializedName("currency")
//    @Expose
//    private String currency;
//    @SerializedName("terminal_id")
//    @Expose
//    private String terminalId;
//    @SerializedName("merchant_uid")
//    @Expose
//    private String merchantUid;
//    @SerializedName("card_type")
//    @Expose
//    private Object cardType;
//    @SerializedName("merchant")
//    @Expose
//    private int merchant;
//    @SerializedName("psn_code")
//    @Expose
//    private String psnCode;
//    @SerializedName("status")
//    @Expose
//    private int status;
//
//
//    public TransactionRequest() {
//    }
//
//    public TransactionRequest(String createdAt, String updatedAt, String transactionType, String pan, String uti, String amount, String discount, boolean approved, boolean cancelled, boolean sigRequired, boolean pinVerified, String currency, String terminalId, String merchantUid, Object cardType, int merchant,String psnCode,int status) {
//        super();
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//        this.transactionType = transactionType;
//        this.pan = pan;
//        this.uti = uti;
//        this.amount = amount;
//        this.discount = discount;
//        this.approved = approved;
//        this.cancelled = cancelled;
//        this.sigRequired = sigRequired;
//        this.pinVerified = pinVerified;
//        this.currency = currency;
//        this.terminalId = terminalId;
//        this.merchantUid = merchantUid;
//        this.cardType = cardType;
//        this.merchant = merchant;
//        this.psnCode = psnCode;
//        this.status = status;
//    }
//
//    public String getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(String createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public String getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(String updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    public String getTransactionType() {
//        return transactionType;
//    }
//
//    public void setTransactionType(String transactionType) {
//        this.transactionType = transactionType;
//    }
//
//    public String getPan() {
//        return pan;
//    }
//
//    public void setPan(String pan) {
//        this.pan = pan;
//    }
//
//    public String getUti() {
//        return uti;
//    }
//
//    public void setUti(String uti) {
//        this.uti = uti;
//    }
//
//    public String getAmount() {
//        return amount;
//    }
//
//    public void setAmount(String amount) {
//        this.amount = amount;
//    }
//
//    public String getDiscount() {
//        return discount;
//    }
//
//    public void setDiscount(String discount) {
//        this.discount = discount;
//    }
//
//    public boolean isApproved() {
//        return approved;
//    }
//
//    public void setApproved(boolean approved) {
//        this.approved = approved;
//    }
//
//    public boolean isCancelled() {
//        return cancelled;
//    }
//
//    public void setCancelled(boolean cancelled) {
//        this.cancelled = cancelled;
//    }
//
//    public boolean isSigRequired() {
//        return sigRequired;
//    }
//
//    public void setSigRequired(boolean sigRequired) {
//        this.sigRequired = sigRequired;
//    }
//
//    public boolean isPinVerified() {
//        return pinVerified;
//    }
//
//    public void setPinVerified(boolean pinVerified) {
//        this.pinVerified = pinVerified;
//    }
//
//    public String getCurrency() {
//        return currency;
//    }
//
//    public void setCurrency(String currency) {
//        this.currency = currency;
//    }
//
//    public String getTerminalId() {
//        return terminalId;
//    }
//
//    public void setTerminalId(String terminalId) {
//        this.terminalId = terminalId;
//    }
//
//    public String getMerchantUid() {
//        return merchantUid;
//    }
//
//    public void setMerchantUid(String merchantUid) {
//        this.merchantUid = merchantUid;
//    }
//
//    public Object getCardType() {
//        return cardType;
//    }
//
//    public void setCardType(Object cardType) {
//        this.cardType = cardType;
//    }
//
//    public int getMerchant() {
//        return merchant;
//    }
//
//    public void setMerchant(int merchant) {
//        this.merchant = merchant;
//    }
//
//    public String getPsnCode() {
//        return psnCode;
//    }
//
//    public void setPsnCode(String psnCode) {
//        this.psnCode = psnCode;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(TransactionRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
//        sb.append("createdAt");
//        sb.append('=');
//        sb.append(((this.createdAt == null) ? "<null>" : this.createdAt));
//        sb.append(',');
//        sb.append("updatedAt");
//        sb.append('=');
//        sb.append(((this.updatedAt == null) ? "<null>" : this.updatedAt));
//        sb.append(',');
//        sb.append("transactionType");
//        sb.append('=');
//        sb.append(((this.transactionType == null) ? "<null>" : this.transactionType));
//        sb.append(',');
//        sb.append("pan");
//        sb.append('=');
//        sb.append(((this.pan == null) ? "<null>" : this.pan));
//        sb.append(',');
//        sb.append("uti");
//        sb.append('=');
//        sb.append(((this.uti == null) ? "<null>" : this.uti));
//        sb.append(',');
//        sb.append("amount");
//        sb.append('=');
//        sb.append(((this.amount == null) ? "<null>" : this.amount));
//        sb.append(',');
//        sb.append("discount");
//        sb.append('=');
//        sb.append(((this.discount == null) ? "<null>" : this.discount));
//        sb.append(',');
//        sb.append("approved");
//        sb.append('=');
//        sb.append(this.approved);
//        sb.append(',');
//        sb.append("cancelled");
//        sb.append('=');
//        sb.append(this.cancelled);
//        sb.append(',');
//        sb.append("sigRequired");
//        sb.append('=');
//        sb.append(this.sigRequired);
//        sb.append(',');
//        sb.append("pinVerified");
//        sb.append('=');
//        sb.append(this.pinVerified);
//        sb.append(',');
//        sb.append("currency");
//        sb.append('=');
//        sb.append(((this.currency == null) ? "<null>" : this.currency));
//        sb.append(',');
//        sb.append("terminalId");
//        sb.append('=');
//        sb.append(((this.terminalId == null) ? "<null>" : this.terminalId));
//        sb.append(',');
//        sb.append("merchantUid");
//        sb.append('=');
//        sb.append(((this.merchantUid == null) ? "<null>" : this.merchantUid));
//        sb.append(',');
//        sb.append("cardType");
//        sb.append('=');
//        sb.append(((this.cardType == null) ? "<null>" : this.cardType));
//        sb.append(',');
//        sb.append("merchant");
//        sb.append('=');
//        sb.append(this.merchant);
//        sb.append(',');
//        if (sb.charAt((sb.length() - 1)) == ',') {
//            sb.setCharAt((sb.length() - 1), ']');
//        } else {
//            sb.append(']');
//        }
//        return sb.toString();
//    }
//
//
//}
