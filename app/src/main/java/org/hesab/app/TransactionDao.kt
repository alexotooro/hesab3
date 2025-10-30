package org.hesab.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface TransactionDao {

    @Insert
    fun insert(transaction: Transaction)

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getById(id: Int): Transaction?
}
