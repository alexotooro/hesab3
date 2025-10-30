package org.hesab.app

import androidx.room.*

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY orderIndex DESC")
    suspend fun getAll(): List<Transaction>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    // --- متدهای کمکی برای مرتب‌سازی و ویرایش ---

    @Query("SELECT MAX(orderIndex) FROM transactions")
    suspend fun getMaxOrderIndex(): Int?

    @Query("SELECT orderIndex FROM transactions WHERE id = :id")
    suspend fun getOrderIndexById(id: Int): Int?

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Transaction?

    @Query("UPDATE transactions SET orderIndex = :newIndex WHERE id = :id")
    suspend fun updateOrder(id: Int, newIndex: Int)
}
