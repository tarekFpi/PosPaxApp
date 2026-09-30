package com.paymentsave.paymentsave.coreapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.paymentsave.paymentsave.coreapp.roomdb.entity.Report;

import java.util.List;

@Dao
public interface ReportDao {
    @Query("SELECT * FROM Report")
    List<Report> getAll();

    @Insert
    void insert(Report report);

    @Query("DELETE FROM Report")
    void deleteAll();

    @Query("SELECT * FROM `Report` WHERE sync = :isSynced")
    List<Report> getAllUnSyncedData(boolean isSynced);

    @Query("UPDATE `Report` SET sync = :isSynced WHERE id = :id")
    void updateSyncStatus(int id, boolean isSynced);
}
