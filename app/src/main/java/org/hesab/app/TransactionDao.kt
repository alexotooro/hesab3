package org.hesab.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao {

    @Insert
    fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAll(): List<Transaction>
}
