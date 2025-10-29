package org.hesab.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.hesab.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: TransactionAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        setupRecyclerView()
        loadTransactions()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun setupRecyclerView() {
        layoutManager = LinearLayoutManager(this)
        // قدیمی‌ترها بالا، جدیدترها پایین
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = true  // اسکرول خودکار به پایین
        binding.recyclerView.layoutManager = layoutManager

        adapter = TransactionAdapter()
        binding.recyclerView.adapter = adapter
    }

    private fun loadTransactions() {
        val transactions = db.transactionDao().getAll()
        adapter.setData(transactions)

        // اسکرول خودکار به آخرین آیتم (پایین لیست)
        if (transactions.isNotEmpty()) {
            binding.recyclerView.scrollToPosition(transactions.size - 1)
        }
    }
}
