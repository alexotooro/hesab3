package org.hesab.app

import androidx.room.*

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY orderIndex ASC")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getById(id: Int): Transaction? // 🆕 اضافه شد

    @Query("SELECT MAX(orderIndex) FROM transactions")
    fun getMaxOrderIndex(): Int? // 🆕 برای افزودن ترتیب جدید

    @Insert
    fun insert(transaction: Transaction)

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Query("UPDATE transactions SET orderIndex = :newIndex WHERE id = :transactionId")
    fun updateOrder(transactionId: Int, newIndex: Int)
}
