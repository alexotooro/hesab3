package org.hesab.app

import androidx.room.*

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY orderIndex ASC")
    fun getAll(): List<Transaction>

    @Insert
    fun insert(transaction: Transaction)

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    // برای ویرایش (getById)
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getById(id: Int): Transaction?

    // برای جابجایی و ترتیب نمایش
    @Query("SELECT MAX(orderIndex) FROM transactions")
    fun getMaxOrderIndex(): Int?

    @Query("UPDATE transactions SET orderIndex = :newOrderIndex WHERE id = :transactionId")
    fun updateOrder(transactionId: Int, newOrderIndex: Int)
}
