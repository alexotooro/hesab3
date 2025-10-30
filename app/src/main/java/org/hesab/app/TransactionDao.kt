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

    // 🆕 برای جابجایی (به‌روزرسانی ترتیب ردیف‌ها)
    @Query("UPDATE transactions SET orderIndex = :newOrder WHERE id = :id")
    fun updateOrder(id: Int, newOrder: Int)

    // 🆕 برای افزودن تراکنش جدید در انتهای لیست
    @Query("SELECT MAX(orderIndex) FROM transactions")
    fun getMaxOrderIndex(): Int?
}
