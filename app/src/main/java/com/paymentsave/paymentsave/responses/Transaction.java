package com.paymentsave.paymentsave.responses;



public class Transaction {

    private int mTransDrawable;

    private String mTransName;

    public Transaction(int mTransDrawable, String mTransName) {
        this.mTransDrawable = mTransDrawable;
        this.mTransName = mTransName;
    }

    public int getTransDrawable() {
        return mTransDrawable;
    }

    public String getTransName() {
        return mTransName;
    }
}
