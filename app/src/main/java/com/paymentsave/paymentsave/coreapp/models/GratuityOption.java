package com.paymentsave.paymentsave.coreapp.models;

public class GratuityOption {
    private String label;
    private String amount;
    private float mainAmount = 0.0f;

    public GratuityOption(String label, String amount, Float mainAmount) {
        this.label = label;
        this.amount = amount;
        this.mainAmount = mainAmount;
    }

    public String getLabel() {
        return label;
    }

    public String getAmount() {
        return amount;
    }

    public float getMainAmount() {
        return mainAmount;
    }

}

