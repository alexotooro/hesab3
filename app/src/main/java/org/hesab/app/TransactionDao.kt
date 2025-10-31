package org.hesab.app

import androidx.room.*

@Dao
interface TransactionDao {

    // مرتب بر اساس id DESC => جدیدترین بالاست (ثبت‌شده‌ترین)
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getById(id: Long): Transaction?

    @Insert
    fun insert(transaction: Transaction): Long

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)
}
