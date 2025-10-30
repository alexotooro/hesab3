package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var db: AppDatabase
    private var transactions = mutableListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        db = AppDatabase.getInstance(this)

        adapter = TransactionAdapter(transactions, db) {
            loadTransactions()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 🟩 دکمه افزودن تراکنش
        val btnAddTransaction = findViewById<Button>(R.id.btnAddTransaction)
        btnAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        loadTransactions()
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.transactionDao().getAll()
            transactions.clear()
            transactions.addAll(list)
            runOnUiThread { adapter.notifyDataSetChanged() }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }
}
