package com.paymentsave.paymentsave.coreapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.paymentsave.paymentsave.coreapp.roomdb.entity.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM `transaction`")
    List<Transaction> getAll();

//    @Query("SELECT * FROM conversation WHERE id = :id")
//    CrashlyticsReport.Session.User getById(int id);

    @Query("SELECT * FROM `transaction` WHERE uti = :uti")
    Transaction getTnxByUti(String uti);

    @Query("UPDATE `transaction` SET sync = :isSynced WHERE id = :id")
    void updateSyncStatus(int id, boolean isSynced);

    @Query("SELECT * FROM `transaction` WHERE sync = :isSynced")
    List<Transaction> getAllUnSyncedData(boolean isSynced);

    @Insert
    void insert(Transaction transaction);

    @Update
    int updateTransaction(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("DELETE FROM `transaction`")
    void deleteAll();

    @Query("DELETE FROM `transaction` WHERE strftime('%s', created_at) < strftime('%s', :twoDaysAgo)")
    void deleteRecordsOlderThanTwoDays(String twoDaysAgo);

}