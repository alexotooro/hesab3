package org.hesab.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var db: AppDatabase
    private val transactions = mutableListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerView)

        adapter = TransactionAdapter(
            transactions,
            onEdit = { transaction -> editTransaction(transaction) },
            onDelete = { transaction -> deleteTransaction(transaction) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadTransactions()
        enableDragAndDrop()
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = db.transactionDao().getAll()
            transactions.clear()
            transactions.addAll(data)
            runOnUiThread { adapter.notifyDataSetChanged() }
        }
    }

    private fun editTransaction(transaction: Transaction) {
        val intent = Intent(this, AddTransactionActivity::class.java)
        intent.putExtra("transaction_id", transaction.id)
        startActivity(intent)
    }

    private fun deleteTransaction(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            db.transactionDao().delete(transaction)
            loadTransactions()
        }
    }

    private fun enableDragAndDrop() {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                adapter.moveItem(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }
}
