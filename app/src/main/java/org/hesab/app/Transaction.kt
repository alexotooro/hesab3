package org.hesab.app

import androidx.room.*

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY orderIndex DESC")
    fun getAll(): List<Transaction>

    @Insert
    fun insert(transaction: Transaction)

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    // --- متدهای کمکی برای مرتب‌سازی و ویرایش ---

    @Query("SELECT MAX(orderIndex) FROM transactions")
    fun getMaxOrderIndex(): Int?

    @Query("SELECT orderIndex FROM transactions WHERE id = :id")
    fun getOrderIndexById(id: Int): Int?

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getById(id: Int): Transaction?

    @Query("UPDATE transactions SET orderIndex = :newIndex WHERE id = :id")
    fun updateOrder(id: Int, newIndex: Int)
}
