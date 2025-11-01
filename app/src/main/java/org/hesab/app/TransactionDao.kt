package org.hesab.app

import androidx.room.*

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY orderIndex ASC")
    fun getAll(): List<Transaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transaction: Transaction)

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Query("UPDATE transactions SET orderIndex = :newIndex WHERE id = :id")
    fun updateOrder(id: Int, newIndex: Int)

    @Update
    fun updateAll(transactions: List<Transaction>)
}
