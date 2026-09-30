package com.paymentsave.paymentsave.responses;

import android.os.Parcel;
import android.os.Parcelable;



public class TransactionResponse implements Parcelable {

    private String transResponseName;

    private String transResponseValue;


    protected TransactionResponse(Parcel in) {
        transResponseName = in.readString();
        transResponseValue = in.readString();
    }

    public String getTransResponseName() {
        return transResponseName;
    }

    public String getTransResponseValue() {
        return transResponseValue;
    }

    public static final Creator<TransactionResponse> CREATOR = new Creator<TransactionResponse>() {
        @Override
        public TransactionResponse createFromParcel(Parcel in) {
            return new TransactionResponse(in);
        }

        @Override
        public TransactionResponse[] newArray(int size) {
            return new TransactionResponse[size];
        }
    };

    public TransactionResponse(String transResponseName, String transResponseValue) {
        this.transResponseName = transResponseName;
        this.transResponseValue = transResponseValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transResponseName);
        dest.writeString(transResponseValue);
    }

    @Override
    public String toString() {
        return "TransactionResponse{" +
                "transResponseName='" + transResponseName + '\'' +
                ", transResponseValue='" + transResponseValue + '\'' +
                '}';
    }
}
