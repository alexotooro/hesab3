package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var btnAddTransaction: Button
    private lateinit var tvBalance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        tvBalance = findViewById(R.id.tvBalance)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // بارگذاری تراکنش‌ها
        loadTransactions()

        btnAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun loadTransactions() {
        Thread {
            val transactions = db.transactionDao().getAll()

            // ✅ محاسبه مانده (جمع درآمدها منهای جمع هزینه‌ها)
            var income = 0.0
            var expense = 0.0
            for (t in transactions) {
                if (t.type == "درآمد") income += t.amount
                else expense += t.amount
            }
            val balance = income - expense

            runOnUiThread {
                adapter = TransactionAdapter(this, transactions, db)
                recyclerView.adapter = adapter

                // ✅ نمایش مانده به صورت عددی با فرمت مناسب
                tvBalance.text = "مانده: %, .0f ریال".format(balance)
            }
        }.start()
    }
}
