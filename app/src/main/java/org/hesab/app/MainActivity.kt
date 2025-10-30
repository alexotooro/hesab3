package org.hesab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.hesab.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        // تنظیم RecyclerView
        adapter = TransactionAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // کلیک دکمه +
        binding.fabAdd.setOnClickListener {
            startActivity(android.content.Intent(this, AddTransactionActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactionsAndBalance()
    }

    private fun loadTransactionsAndBalance() {
        val transactions = db.transactionDao().getAll()
        adapter.submitList(transactions)

        // محاسبه مانده
        var incomeTotal = 0.0
        var expenseTotal = 0.0

        for (t in transactions) {
            if (t.type == "درآمد") incomeTotal += t.amount
            else if (t.type == "هزینه") expenseTotal += t.amount
        }

        val balance = incomeTotal - expenseTotal
        val balanceText = "مانده: %, .0f ریال".format(balance)

        binding.tvBalance.text = balanceText
    }
}
