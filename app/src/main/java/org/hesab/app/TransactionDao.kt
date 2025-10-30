package org.hesab.app

import androidx.room.*

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY orderIndex ASC")
    suspend fun getAll(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Int): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT MAX(orderIndex) FROM transactions")
    suspend fun getMaxOrderIndex(): Int?

    @Query("SELECT orderIndex FROM transactions WHERE id = :id")
    suspend fun getOrderIndexById(id: Int): Int?

    @Query("UPDATE transactions SET orderIndex = :newIndex WHERE id = :id")
    suspend fun updateOrder(id: Int, newIndex: Int)
}
