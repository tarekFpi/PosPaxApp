package com.paymentsave.paymentsave.coreapp.roomdb.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;

@Entity(tableName = "transaction")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "request_id")
    private String requestID;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "trans_type")
    private String transType;

    @ColumnInfo(name = "uti")
    private String uti;

    @ColumnInfo(name = "tnx_note")
    private String tnxNote;

    @ColumnInfo(name = "existing_uti")
    private String existingUti;

    @ColumnInfo(name = "amount_trans")
    private String amountTrans;

    @ColumnInfo(name = "amount_gratuity")
    private String amountGratuity;

    @ColumnInfo(name = "amount_cashback")
    private String amountCashback;

    @ColumnInfo(name = "amount_discount")
    private String amountDiscount;

    @ColumnInfo(name = "trans_approved")
    private boolean transApproved;

    @ColumnInfo(name = "trans_cancelled")
    private boolean transCancelled;

    @ColumnInfo(name = "cvm_sig_required")
    private boolean cvmSigRequired;

    @ColumnInfo(name = "cvm_pin_verified")
    private boolean cvmPinVerified;

    @ColumnInfo(name = "trans_currency_code")
    private String transCurrencyCode;

    @ColumnInfo(name = "software_version")
    private String softwareVersion;

    @ColumnInfo(name = "terminal_id")
    private String terminalId;

    @ColumnInfo(name = "merchant_id")
    private String merchantId;

    @ColumnInfo(name = "receipt_number")
    private String receiptNumber;

    @ColumnInfo(name = "retrieval_reference_number")
    private String retrievalReferenceNumber;

    @ColumnInfo(name = "response_code")
    private String responseCode;

    @ColumnInfo(name = "is_created")
    private boolean isCreated;

    @ColumnInfo(name = "is_result_response")
    private boolean isResultResponse;

    @ColumnInfo(name = "stan")
    private String stan;

    @ColumnInfo(name = "authorisation_code")
    private String authorisationCode;

    @ColumnInfo(name = "merchant_token_id")
    private String merchantTokenId;

    @ColumnInfo(name = "card_type")
    private String cardType;

    @ColumnInfo(name = "emv_aid")
    private String emvAid;

    @ColumnInfo(name = "emv_tsi")
    private String emvTsi;

    @ColumnInfo(name = "emv_tvr")
    private String emvTvr;

    @ColumnInfo(name = "emv_cardholder_name")
    private String emvCardholderName;

    @ColumnInfo(name = "emv_cryptogram")
    private String emvCryptogram;

    @ColumnInfo(name = "emv_cryptogram_type")
    private String emvCryptogramType;

    @ColumnInfo(name = "card_pan")
    private String cardPan;

    @ColumnInfo(name = "card_expiry_date")
    private String cardExpiryDate;

    @ColumnInfo(name = "card_start_date")
    private String cardStartDate;

    @ColumnInfo(name = "card_scheme")
    private String cardScheme;

    @ColumnInfo(name = "error_text")
    private String errorText;

    @ColumnInfo(name = "card_pan_sequence_number")
    private String cardPanSequenceNumber;

    @ColumnInfo(name = "card_holder_receipt")
    private String[] cardHolderReceipt;

    @ColumnInfo(name = "merchant_receipt")
    private String[] merchantReceipt;

    @ColumnInfo(name = "sync")
    private boolean sync;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTnxNote() {
        return tnxNote;
    }

    public void setTnxNote(String tnxNote) {
        this.tnxNote = tnxNote;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
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

    public String getAmountTrans() {
        return amountTrans;
    }

    public void setAmountTrans(String amountTrans) {
        this.amountTrans = amountTrans;
    }

    public String getAmountGratuity() {
        return amountGratuity;
    }

    public void setAmountGratuity(String amountGratuity) {
        this.amountGratuity = amountGratuity;
    }

    public String getAmountCashback() {
        return amountCashback;
    }

    public void setAmountCashback(String amountCashback) {
        this.amountCashback = amountCashback;
    }

    public String getAmountDiscount() {
        return amountDiscount;
    }

    public void setAmountDiscount(String amountDiscount) {
        this.amountDiscount = amountDiscount;
    }

    public boolean isTransApproved() {
        return transApproved;
    }

    public void setTransApproved(boolean transApproved) {
        this.transApproved = transApproved;
    }

    public boolean isTransCancelled() {
        return transCancelled;
    }

    public void setTransCancelled(boolean transCancelled) {
        this.transCancelled = transCancelled;
    }

    public boolean isCvmSigRequired() {
        return cvmSigRequired;
    }

    public void setCvmSigRequired(boolean cvmSigRequired) {
        this.cvmSigRequired = cvmSigRequired;
    }

    public boolean isCvmPinVerified() {
        return cvmPinVerified;
    }

    public void setCvmPinVerified(boolean cvmPinVerified) {
        this.cvmPinVerified = cvmPinVerified;
    }

    public String getTransCurrencyCode() {
        return transCurrencyCode;
    }

    public void setTransCurrencyCode(String transCurrencyCode) {
        this.transCurrencyCode = transCurrencyCode;
    }

    public boolean isResultResponse() {
        return isResultResponse;
    }

    public void setResultResponse(boolean resultResponse) {
        isResultResponse = resultResponse;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getRetrievalReferenceNumber() {
        return retrievalReferenceNumber;
    }

    public void setRetrievalReferenceNumber(String retrievalReferenceNumber) {
        this.retrievalReferenceNumber = retrievalReferenceNumber;
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

    public String getAuthorisationCode() {
        return authorisationCode;
    }

    public void setAuthorisationCode(String authorisationCode) {
        this.authorisationCode = authorisationCode;
    }

    public String getMerchantTokenId() {
        return merchantTokenId;
    }

    public void setMerchantTokenId(String merchantTokenId) {
        this.merchantTokenId = merchantTokenId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getEmvAid() {
        return emvAid;
    }

    public void setEmvAid(String emvAid) {
        this.emvAid = emvAid;
    }

    public String getEmvTsi() {
        return emvTsi;
    }

    public void setEmvTsi(String emvTsi) {
        this.emvTsi = emvTsi;
    }

    public String getEmvTvr() {
        return emvTvr;
    }

    public void setEmvTvr(String emvTvr) {
        this.emvTvr = emvTvr;
    }

    public String getEmvCardholderName() {
        return emvCardholderName;
    }

    public void setEmvCardholderName(String emvCardholderName) {
        this.emvCardholderName = emvCardholderName;
    }

    public String getEmvCryptogram() {
        return emvCryptogram;
    }

    public void setEmvCryptogram(String emvCryptogram) {
        this.emvCryptogram = emvCryptogram;
    }

    public String getEmvCryptogramType() {
        return emvCryptogramType;
    }

    public void setEmvCryptogramType(String emvCryptogramType) {
        this.emvCryptogramType = emvCryptogramType;
    }

    public String getCardPan() {
        return cardPan;
    }

    public void setCardPan(String cardPan) {
        this.cardPan = cardPan;
    }

    public String getCardExpiryDate() {
        return cardExpiryDate;
    }

    public void setCardExpiryDate(String cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }

    public String getCardStartDate() {
        return cardStartDate;
    }

    public void setCardStartDate(String cardStartDate) {
        this.cardStartDate = cardStartDate;
    }

    public String getCardScheme() {
        return cardScheme;
    }

    public void setCardScheme(String cardScheme) {
        this.cardScheme = cardScheme;
    }

    public String getCardPanSequenceNumber() {
        return cardPanSequenceNumber;
    }

    public void setCardPanSequenceNumber(String cardPanSequenceNumber) {
        this.cardPanSequenceNumber = cardPanSequenceNumber;
    }

    public String[] getCardHolderReceipt() {
        return cardHolderReceipt;
    }

    public void setCardHolderReceipt(String[] cardHolderReceipt) {
        this.cardHolderReceipt = cardHolderReceipt;
    }

    public String[] getMerchantReceipt() {
        return merchantReceipt;
    }

    public void setMerchantReceipt(String[] merchantReceipt) {
        this.merchantReceipt = merchantReceipt;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", createdAt='" + createdAt + '\'' +
                ", transType='" + transType + '\'' +
                ", uti='" + uti + '\'' +
                ", existingUti='" + existingUti + '\'' +
                ", amountTrans='" + amountTrans + '\'' +
                ", amountGratuity='" + amountGratuity + '\'' +
                ", amountCashback='" + amountCashback + '\'' +
                ", amountDiscount='" + amountDiscount + '\'' +
                ", transApproved=" + transApproved +
                ", transCancelled=" + transCancelled +
                ", cvmSigRequired=" + cvmSigRequired +
                ", cvmPinVerified=" + cvmPinVerified +
                ", transCurrencyCode='" + transCurrencyCode + '\'' +
                ", softwareVersion='" + softwareVersion + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", receiptNumber='" + receiptNumber + '\'' +
                ", retrievalReferenceNumber='" + retrievalReferenceNumber + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", stan='" + stan + '\'' +
                ", authorisationCode='" + authorisationCode + '\'' +
                ", merchantTokenId='" + merchantTokenId + '\'' +
                ", cardType='" + cardType + '\'' +
                ", emvAid='" + emvAid + '\'' +
                ", emvTsi='" + emvTsi + '\'' +
                ", emvTvr='" + emvTvr + '\'' +
                ", emvCardholderName='" + emvCardholderName + '\'' +
                ", emvCryptogram='" + emvCryptogram + '\'' +
                ", emvCryptogramType='" + emvCryptogramType + '\'' +
                ", cardPan='" + cardPan + '\'' +
                ", cardExpiryDate='" + cardExpiryDate + '\'' +
                ", cardStartDate='" + cardStartDate + '\'' +
                ", cardScheme='" + cardScheme + '\'' +
                ", cardPanSequenceNumber='" + cardPanSequenceNumber + '\'' +
                ", cardHolderReceipt=" + Arrays.toString(cardHolderReceipt) +
                ", merchantReceipt=" + Arrays.toString(merchantReceipt) +
                ", sync=" + sync +
                ", isResultResponse=" + isResultResponse +
                '}';
    }
}
