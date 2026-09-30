package com.paymentsave.paymentsave.responses.TransactionDetailsResponse;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionDetailsBusinessTerminal {
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "business_terminal_id")
    private int id;
    @SerializedName("terminal")
    @Expose
    private int terminal;
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
    private String batchTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTerminal() {
        return terminal;
    }

    public void setTerminal(int terminal) {
        this.terminal = terminal;
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

    public String getBatchTime() {
        return batchTime;
    }

    public void setBatchTime(String batchTime) {
        this.batchTime = batchTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TransactionDetailsBusinessTerminal.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(this.id);
        sb.append(',');
        sb.append("terminal");
        sb.append('=');
        sb.append(this.terminal);
        sb.append(',');
        sb.append("preAuthEnabled");
        sb.append('=');
        sb.append(this.preAuthEnabled);
        sb.append(',');
        sb.append("cnp");
        sb.append('=');
        sb.append(this.cnp);
        sb.append(',');
        sb.append("autoBatchEnabled");
        sb.append('=');
        sb.append(this.autoBatchEnabled);
        sb.append(',');
        sb.append("batchTime");
        sb.append('=');
        sb.append(((this.batchTime == null)?"<null>":this.batchTime));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
