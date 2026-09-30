package com.paymentsave.paymentsave.coreapp.roomdb.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.paymentsave.paymentsave.coreapp.roomdb.Converters;
import com.paymentsave.paymentsave.coreapp.roomdb.dao.ReportDao;
import com.paymentsave.paymentsave.coreapp.roomdb.dao.TransactionDao;
import com.paymentsave.paymentsave.coreapp.roomdb.entity.Report;
import com.paymentsave.paymentsave.coreapp.roomdb.entity.Transaction;

@Database(entities = {Transaction.class, Report.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class PaxDB extends RoomDatabase {
    public abstract TransactionDao transactionDao();

    public abstract ReportDao reportDao();
}
