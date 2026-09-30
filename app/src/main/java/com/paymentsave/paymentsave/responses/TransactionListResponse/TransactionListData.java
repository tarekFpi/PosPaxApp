package com.paymentsave.paymentsave.responses.TransactionListResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionListData {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("next")
    @Expose
    private String next;
    @SerializedName("previous")
    @Expose
    private String previous;
    @SerializedName("results")
    @Expose
    private List<TransactionListResult> results;

    /**
     * No args constructor for use in serialization
     */
    public TransactionListData() {
    }

    /**
     * @param next
     * @param previous
     * @param count
     * @param results
     */
    public TransactionListData(int count,int totalPages, String next, String previous, List<TransactionListResult> results) {
        super();
        this.count = count;
        this.totalPages = totalPages;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<TransactionListResult> getResults() {
        return results;
    }

    public void setResults(List<TransactionListResult> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "TransactionListData{" +
                "count=" + count +
                ", totalPages=" + totalPages +
                ", next=" + next +
                ", previous=" + previous +
                ", results=" + results +
                '}';
    }
}
