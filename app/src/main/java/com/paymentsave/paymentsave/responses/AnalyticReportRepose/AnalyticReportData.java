package com.paymentsave.paymentsave.responses.AnalyticReportRepose;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnalyticReportData {
    @SerializedName("total_amount")
    @Expose
    private double totalAmount;
    @SerializedName("total_count")
    @Expose
    private int totalCount;
    @SerializedName("total_transaction_total")
    @Expose
    private double totalTransactionTotal;
    @SerializedName("total_transaction_count")
    @Expose
    private int totalTransactionCount;
    @SerializedName("sales_transaction_total")
    @Expose
    private double salesTransactionTotal;
    @SerializedName("sales_transaction_count")
    @Expose
    private int salesTransactionCount;
    @SerializedName("gratuity_transaction_total")
    @Expose
    private double gratuityTransactionTotal;
    @SerializedName("gratuity_transaction_count")
    @Expose
    private int gratuityTransactionCount;
    @SerializedName("refund_transaction_total")
    @Expose
    private double refundTransactionTotal;
    @SerializedName("refund_transaction_count")
    @Expose
    private int refundTransactionCount;

    @SerializedName("cashback_transaction_total")
    @Expose
    private double cashbackTransactionTotal;
    @SerializedName("cashback_transaction_count")
    @Expose
    private int cashbackTransactionCount;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public double getTotalTransactionTotal() {
        return totalTransactionTotal;
    }

    public void setTotalTransactionTotal(double totalTransactionTotal) {
        this.totalTransactionTotal = totalTransactionTotal;
    }

    public int getTotalTransactionCount() {
        return totalTransactionCount;
    }

    public void setTotalTransactionCount(int totalTransactionCount) {
        this.totalTransactionCount = totalTransactionCount;
    }

    public double getSalesTransactionTotal() {
        return salesTransactionTotal;
    }

    public void setSalesTransactionTotal(double salesTransactionTotal) {
        this.salesTransactionTotal = salesTransactionTotal;
    }

    public int getSalesTransactionCount() {
        return salesTransactionCount;
    }

    public void setSalesTransactionCount(int salesTransactionCount) {
        this.salesTransactionCount = salesTransactionCount;
    }

    public double getGratuityTransactionTotal() {
        return gratuityTransactionTotal;
    }

    public void setGratuityTransactionTotal(double gratuityTransactionTotal) {
        this.gratuityTransactionTotal = gratuityTransactionTotal;
    }

    public int getGratuityTransactionCount() {
        return gratuityTransactionCount;
    }

    public void setGratuityTransactionCount(int gratuityTransactionCount) {
        this.gratuityTransactionCount = gratuityTransactionCount;
    }

    public double getRefundTransactionTotal() {
        return refundTransactionTotal;
    }

    public void setRefundTransactionTotal(double refundTransactionTotal) {
        this.refundTransactionTotal = refundTransactionTotal;
    }

    public int getRefundTransactionCount() {
        return refundTransactionCount;
    }

    public void setRefundTransactionCount(int refundTransactionCount) {
        this.refundTransactionCount = refundTransactionCount;
    }

    public double getCashbackTransactionTotal() {
        return cashbackTransactionTotal;
    }

    public void setCashbackTransactionTotal(double cashbackTransactionTotal) {
        this.cashbackTransactionTotal = cashbackTransactionTotal;
    }

    public int getCashbackTransactionCount() {
        return cashbackTransactionCount;
    }

    public void setCashbackTransactionCount(int cashbackTransactionCount) {
        this.cashbackTransactionCount = cashbackTransactionCount;
    }

    @Override
    public String toString() {
        return "AnalyticReportData{" +
                "totalAmount=" + totalAmount +
                ", totalCount=" + totalCount +
                ", totalTransactionTotal=" + totalTransactionTotal +
                ", totalTransactionCount=" + totalTransactionCount +
                ", salesTransactionTotal=" + salesTransactionTotal +
                ", salesTransactionCount=" + salesTransactionCount +
                ", gratuityTransactionTotal=" + gratuityTransactionTotal +
                ", gratuityTransactionCount=" + gratuityTransactionCount +
                ", refundTransactionTotal=" + refundTransactionTotal +
                ", refundTransactionCount=" + refundTransactionCount +
                ", cashbackTransactionTotal=" + cashbackTransactionTotal +
                ", cashbackTransactionCount=" + cashbackTransactionCount +
                '}';
    }
}
