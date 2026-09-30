package com.paymentsave.paymentsave.coreapp.models;

public class CashbackOption {
    private String label;
    private String amount;

    public CashbackOption(String label, String amount) {
        this.label = label;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public String getAmount() {
        return amount;
    }

}
