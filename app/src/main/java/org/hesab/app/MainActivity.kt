package org.hesab.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd = findViewById(R.id.btnAddTransaction)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // بارگذاری تراکنش‌ها
        loadTransactions()

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    fun refreshTransactions() {
        loadTransactions()
    }

    private fun loadTransactions() {
        Thread {
            val transactions = db.transactionDao().getAll()
            runOnUiThread {
                adapter = TransactionAdapter(this, transactions, db)
                recyclerView.adapter = adapter
            }
        }.start()
    }
}
