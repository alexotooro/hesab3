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

    // هنگام جابجایی ترتیب‌ها
    @Update
    fun updateAll(transactions: List<Transaction>)

    // برای تعیین اندیسی که در انتها باید قرار گیرد
    @Query("SELECT MAX(orderIndex) FROM transactions")
    fun getMaxOrderIndex(): Int?
}
