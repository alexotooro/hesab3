package org.hesab.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), TransactionAdapter.OnTransactionMenuClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var db: AppDatabase
    private lateinit var transactions: MutableList<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            transactions = db.transactionDao().getAll().toMutableList()
            runOnUiThread {
                adapter = TransactionAdapter(transactions, this@MainActivity)
                recyclerView.adapter = adapter
            }
        }

        findViewById<android.widget.ImageButton>(R.id.btn_add).setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
    }

    override fun onEditClicked(transaction: Transaction) {
        val intent = Intent(this, AddTransactionActivity::class.java)
        intent.putExtra("transaction_id", transaction.id)
        startActivity(intent)
    }

    override fun onDeleteClicked(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            db.transactionDao().delete(transaction)
            transactions.remove(transaction)
            runOnUiThread { adapter.notifyDataSetChanged() }
        }
    }
}
