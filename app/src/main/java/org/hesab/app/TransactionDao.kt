package org.hesab.app

import androidx.room.*

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY orderIndex ASC")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getById(id: Int): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transaction: Transaction)

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Query("SELECT MAX(orderIndex) FROM transactions")
    fun getMaxOrderIndex(): Int?

    @Query("SELECT orderIndex FROM transactions WHERE id = :id")
    fun getOrderIndexById(id: Int): Int?

    @Query("UPDATE transactions SET orderIndex = :newIndex WHERE id = :id")
    fun updateOrder(id: Int, newIndex: Int)
}
