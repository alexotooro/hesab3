package org.hesab.app

class TransactionRepository(private val dao: TransactionDao) {

    fun getAll(): List<Transaction> = dao.getAll()

    fun insert(transaction: Transaction) = dao.insert(transaction)

    fun update(transaction: Transaction) = dao.update(transaction)

    fun delete(transaction: Transaction) = dao.delete(transaction)

    fun updateOrder(id: Int, newIndex: Int) = dao.updateOrder(id, newIndex)

    fun updateAll(transactions: List<Transaction>) = dao.updateAll(transactions)
}
