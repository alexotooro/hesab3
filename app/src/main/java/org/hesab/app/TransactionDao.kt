package org.hesab.app

import androidx.room.*

@Dao
interface TransactionDao {

    // دریافت تمام تراکنش‌ها بر اساس ترتیب نمایش (جدیدترین بالا)
    @Query("SELECT * FROM transactions ORDER BY orderIndex DESC")
    fun getAll(): List<Transaction>

    // دریافت تراکنش با شناسه خاص
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getById(id: Int): Transaction?

    // افزودن تراکنش جدید
    @Insert
    fun insert(transaction: Transaction)

    // ویرایش تراکنش
    @Update
    fun update(transaction: Transaction)

    // حذف تراکنش
    @Delete
    fun delete(transaction: Transaction)

    // به‌روزرسانی موقعیت (ترتیب) در زمان جابجایی
    @Query("UPDATE transactions SET orderIndex = :newIndex WHERE id = :transactionId")
    fun updateOrder(transactionId: Int, newIndex: Int)
}
