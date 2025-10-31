package org.hesab.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: TransactionAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter(mutableListOf())
        recyclerView.adapter = adapter

        loadTransactions()

        val fab: FloatingActionButton = findViewById(R.id.fabAdd)
        fab.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val transactions = db.transactionDao().getAll()
            withContext(Dispatchers.Main) {
                adapter.setTransactions(transactions)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }
}
