package com.paymentsave.paymentsave.responses.ActivationCodeResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivationCodeData {
    @SerializedName("activation_code")
    @Expose
    private String activationCode;
    @SerializedName("has_supervisor_password")
    @Expose
    private boolean hasSupervisorPassword;

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public boolean isHasSupervisorPassword() {
        return hasSupervisorPassword;
    }

    public void setHasSupervisorPassword(boolean hasSupervisorPassword) {
        this.hasSupervisorPassword = hasSupervisorPassword;
    }

    @Override
    public String toString() {
        return "ActivationCodeData{" +
                "activationCode='" + activationCode + '\'' +
                ", hasSupervisorPassword=" + hasSupervisorPassword +
                '}';
    }
}
