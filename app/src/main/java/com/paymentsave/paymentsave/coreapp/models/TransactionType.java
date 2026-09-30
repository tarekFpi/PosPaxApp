package com.paymentsave.paymentsave.coreapp.models;

public class TransactionType {
    private String title;
    private String value;
    private boolean isSelected;

    public TransactionType(String title, String value, boolean isSelected) {
        this.title = title;
        this.value = value;
        this.isSelected = isSelected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "TransactionType{" +
                "title='" + title + '\'' +
                ", value='" + value + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
