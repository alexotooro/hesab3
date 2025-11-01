package org.hesab.app

class TransactionRepository(private val db: AppDatabase) {

    fun getAll(): List<Transaction> {
        return db.transactionDao().getAll()
    }

    suspend fun insert(transaction: Transaction) {
        db.transactionDao().insert(transaction)
    }

    suspend fun delete(transaction: Transaction) {
        db.transactionDao().delete(transaction)
    }

    suspend fun update(transaction: Transaction) {
        db.transactionDao().update(transaction)
    }

    suspend fun updateOrder(transactions: List<Transaction>) {
        transactions.forEachIndexed { index, t ->
            t.orderIndex = index
        }
        db.transactionDao().updateAll(transactions)
    }
}
