package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
        adapter = TransactionAdapter(
            onEdit = { transaction ->
                Toast.makeText(this, "ویرایش: ${transaction.category}", Toast.LENGTH_SHORT).show()
            },
            onDelete = { transaction ->
                Toast.makeText(this, "حذف: ${transaction.category}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // دکمه افزودن تراکنش
        binding.btnAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactionsAndBalance()
    }

    private fun loadTransactionsAndBalance() {
        Thread {
            val transactions = db.transactionDao().getAll()
            var incomeTotal = 0.0
            var expenseTotal = 0.0

            for (t in transactions) {
                if (t.type == "درآمد") incomeTotal += t.amount
                else if (t.type == "هزینه") expenseTotal += t.amount
            }

            val balance = incomeTotal - expenseTotal
            val balanceText = "مانده: %,.0f ریال".format(balance)

            runOnUiThread {
                adapter.setData(transactions)
                binding.tvBalance.text = balanceText
            }
        }.start()
    }
}
