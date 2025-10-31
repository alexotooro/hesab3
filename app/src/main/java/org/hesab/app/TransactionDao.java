package org.hesab.app;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY orderIndex DESC")
    List<Transaction> getAll();

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("DELETE FROM transactions")
    void deleteAll();

    @Query("SELECT SUM(CASE WHEN isIncome = 1 THEN amount ELSE -amount END) FROM transactions")
    long getBalance();
}
