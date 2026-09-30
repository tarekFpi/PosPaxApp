package com.paymentsave.paymentsave.responses.ReportListResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Report {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("epos_session_id")
    @Expose
    private String eposSessionId;
    @SerializedName("business")
    @Expose
    private int business;
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("report_type")
    @Expose
    private String reportType;
    @SerializedName("result_type")
    @Expose
    private String resultType;
    @SerializedName("completion_count")
    @Expose
    private int completionCount;
    @SerializedName("completion_amount")
    @Expose
    private String completionAmount;
    @SerializedName("cashback_count")
    @Expose
    private int cashbackCount;
    @SerializedName("cashback_amount")
    @Expose
    private String cashbackAmount;
    @SerializedName("gratuity_count")
    @Expose
    private int gratuityCount;
    @SerializedName("gratuity_amount")
    @Expose
    private String gratuityAmount;
    @SerializedName("refund_count")
    @Expose
    private int refundCount;
    @SerializedName("refund_amount")
    @Expose
    private String refundAmount;
    @SerializedName("sale_count")
    @Expose
    private int saleCount;
    @SerializedName("sale_amount")
    @Expose
    private String saleAmount;
    @SerializedName("is_report_response")
    @Expose
    private boolean isReportResponse;
    @SerializedName("merchant")
    @Expose
    private int merchant;
    @SerializedName("response_text")
    @Expose
    private String responseText;
    @SerializedName("report_error")
    @Expose
    private String reportError;
    @SerializedName("status")
    @Expose
    private String status;

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

    public String getEposSessionId() {
        return eposSessionId;
    }

    public void setEposSessionId(String eposSessionId) {
        this.eposSessionId = eposSessionId;
    }

    public int getBusiness() {
        return business;
    }

    public void setBusiness(int business) {
        this.business = business;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
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

    public String getCompletionAmount() {
        return completionAmount;
    }

    public void setCompletionAmount(String completionAmount) {
        this.completionAmount = completionAmount;
    }

    public int getCashbackCount() {
        return cashbackCount;
    }

    public void setCashbackCount(int cashbackCount) {
        this.cashbackCount = cashbackCount;
    }

    public String getCashbackAmount() {
        return cashbackAmount;
    }

    public void setCashbackAmount(String cashbackAmount) {
        this.cashbackAmount = cashbackAmount;
    }

    public int getGratuityCount() {
        return gratuityCount;
    }

    public void setGratuityCount(int gratuityCount) {
        this.gratuityCount = gratuityCount;
    }

    public String getGratuityAmount() {
        return gratuityAmount;
    }

    public void setGratuityAmount(String gratuityAmount) {
        this.gratuityAmount = gratuityAmount;
    }

    public int getRefundCount() {
        return refundCount;
    }

    public void setRefundCount(int refundCount) {
        this.refundCount = refundCount;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public String getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(String saleAmount) {
        this.saleAmount = saleAmount;
    }

    public boolean isIsReportResponse() {
        return isReportResponse;
    }

    public void setIsReportResponse(boolean isReportResponse) {
        this.isReportResponse = isReportResponse;
    }

    public int getMerchant() {
        return merchant;
    }

    public void setMerchant(int merchant) {
        this.merchant = merchant;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Report.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append("reportType");
        sb.append('=');
        sb.append(((this.reportType == null)?"<null>":this.reportType));
        sb.append(',');
        sb.append("resultType");
        sb.append('=');
        sb.append(((this.resultType == null)?"<null>":this.resultType));
        sb.append(',');
        sb.append("completionCount");
        sb.append('=');
        sb.append(this.completionCount);
        sb.append(',');
        sb.append("completionAmount");
        sb.append('=');
        sb.append(((this.completionAmount == null)?"<null>":this.completionAmount));
        sb.append(',');
        sb.append("cashbackCount");
        sb.append('=');
        sb.append(this.cashbackCount);
        sb.append(',');
        sb.append("cashbackAmount");
        sb.append('=');
        sb.append(((this.cashbackAmount == null)?"<null>":this.cashbackAmount));
        sb.append(',');
        sb.append("gratuityCount");
        sb.append('=');
        sb.append(this.gratuityCount);
        sb.append(',');
        sb.append("gratuityAmount");
        sb.append('=');
        sb.append(((this.gratuityAmount == null)?"<null>":this.gratuityAmount));
        sb.append(',');
        sb.append("refundCount");
        sb.append('=');
        sb.append(this.refundCount);
        sb.append(',');
        sb.append("refundAmount");
        sb.append('=');
        sb.append(((this.refundAmount == null)?"<null>":this.refundAmount));
        sb.append(',');
        sb.append("saleCount");
        sb.append('=');
        sb.append(this.saleCount);
        sb.append(',');
        sb.append("saleAmount");
        sb.append('=');
        sb.append(((this.saleAmount == null)?"<null>":this.saleAmount));
        sb.append(',');
        sb.append("isReportResponse");
        sb.append('=');
        sb.append(this.isReportResponse);
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
