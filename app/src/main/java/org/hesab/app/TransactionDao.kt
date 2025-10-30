package org.hesab.app

import androidx.room.*

@Dao
interface TransactionDao {

    // حالا ترتیب برعکس شد: آخرین تراکنش‌ها اول نمایش داده می‌شن
    @Query("SELECT * FROM transactions ORDER BY orderIndex DESC")
    fun getAll(): List<Transaction>

    @Insert
    fun insert(transaction: Transaction)

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Query("UPDATE transactions SET orderIndex = :newIndex WHERE id = :transactionId")
    fun updateOrder(transactionId: Int, newIndex: Int)
}
