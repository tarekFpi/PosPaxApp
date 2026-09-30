package com.paymentsave.paymentsave.coreapp.roomdb.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;

@Entity(tableName = "report")
public class Report {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "request_id")
    private String requestID;

    @ColumnInfo(name = "report_type")
    private String reportType;

    @ColumnInfo(name = "result_type")
    private String resultType;

    @ColumnInfo(name = "completion_count")
    private int completionCount;

    @ColumnInfo(name = "completion_amount")
    private double completionAmount;

    @ColumnInfo(name = "cashback_count")
    private int cashbackCount;

    @ColumnInfo(name = "cashback_amount")
    private double cashbackAmount;

    @ColumnInfo(name = "gratuity_count")
    private int gratuityCount;

    @ColumnInfo(name = "gratuity_amount")
    private double gratuityAmount;

    @ColumnInfo(name = "refund_count")
    private int refundCount;

    @ColumnInfo(name = "refund_amount")
    private double refundAmount;

    @ColumnInfo(name = "sale_count")
    private int saleCount;

    @ColumnInfo(name = "sale_amount")
    private double saleAmount;

    @ColumnInfo(name = "is_report_response")
    private boolean isReportResponse;

    @ColumnInfo(name = "receipt")
    private String[] receipt;

    @ColumnInfo(name = "response_text")
    private String responseText;

    @ColumnInfo(name = "report_error")
    private String reportError;

    @ColumnInfo(name = "created_at")
    private String createdAt;

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

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public int getCompletionCount() {
        return completionCount;
    }

    public void setCompletionCount(int completionCount) {
        this.completionCount = completionCount;
    }

    public double getCompletionAmount() {
        return completionAmount;
    }

    public void setCompletionAmount(double completionAmount) {
        this.completionAmount = completionAmount;
    }

    public int getCashbackCount() {
        return cashbackCount;
    }

    public void setCashbackCount(int cashbackCount) {
        this.cashbackCount = cashbackCount;
    }

    public double getCashbackAmount() {
        return cashbackAmount;
    }

    public void setCashbackAmount(double cashbackAmount) {
        this.cashbackAmount = cashbackAmount;
    }

    public int getGratuityCount() {
        return gratuityCount;
    }

    public void setGratuityCount(int gratuityCount) {
        this.gratuityCount = gratuityCount;
    }

    public double getGratuityAmount() {
        return gratuityAmount;
    }

    public void setGratuityAmount(double gratuityAmount) {
        this.gratuityAmount = gratuityAmount;
    }

    public int getRefundCount() {
        return refundCount;
    }

    public void setRefundCount(int refundCount) {
        this.refundCount = refundCount;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public double getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(double saleAmount) {
        this.saleAmount = saleAmount;
    }

    public boolean isReportResponse() {
        return isReportResponse;
    }

    public void setReportResponse(boolean reportResponse) {
        isReportResponse = reportResponse;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getReportError() {
        return reportError;
    }

    public void setReportError(String reportError) {
        this.reportError = reportError;
    }

    public String[] getReceipt() {
        return receipt;
    }

    public void setReceipt(String[] receipt) {
        this.receipt = receipt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", reportType='" + reportType + '\'' +
                ", resultType='" + resultType + '\'' +
                ", completionCount=" + completionCount +
                ", completionAmount=" + completionAmount +
                ", cashbackCount=" + cashbackCount +
                ", cashbackAmount=" + cashbackAmount +
                ", gratuityCount=" + gratuityCount +
                ", gratuityAmount=" + gratuityAmount +
                ", refundCount=" + refundCount +
                ", refundAmount=" + refundAmount +
                ", saleCount=" + saleCount +
                ", saleAmount=" + saleAmount +
                ", isReportResponse=" + isReportResponse +
                ", receipt=" + Arrays.toString(receipt) +
                ", responseText='" + responseText + '\'' +
                ", reportError='" + reportError + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", sync=" + sync +
                '}';
    }
}
