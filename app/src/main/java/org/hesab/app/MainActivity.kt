package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var db: AppDatabase
    private lateinit var btnAdd: Button
    private lateinit var spinnerBanks: Spinner
    private lateinit var tvBalance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar ایمن‌تر
        findViewById<Toolbar?>(R.id.toolbar)?.let { setSupportActionBar(it) }

        // Bind views
        btnAdd = findViewById(R.id.btnAddTransaction)
        spinnerBanks = findViewById(R.id.spinnerBanks)
        recyclerView = findViewById(R.id.recyclerView)
        tvBalance = findViewById(R.id.tvBalance)

        recyclerView.layoutManager = LinearLayoutManager(this)
        db = AppDatabase.getInstance(this)

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        loadTransactions()
    }

    override fun onResume() {
        super.onResume()
        // وقتی از صفحه افزودن برگشتیم، لیست را به‌روزرسانی کن
        loadTransactions()
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.transactionDao().getAll().toMutableList()
            launch(Dispatchers.Main) {
                adapter = TransactionAdapter(
                    list,
                    onEdit = { /* بعداً اضافه می‌شود */ },
                    onDelete = { txn ->
                        CoroutineScope(Dispatchers.IO).launch {
                            db.transactionDao().delete(txn)
                            val idx = list.indexOfFirst { it.id == txn.id }
                            if (idx >= 0) {
                                list.removeAt(idx)
                                launch(Dispatchers.Main) { adapter.notifyDataSetChanged() }
                                updateBalance(list)
                            }
                        }
                    }
                )
                recyclerView.adapter = adapter
                updateBalance(list)
            }
        }
    }

    private fun updateBalance(list: List<Transaction>) {
        val balance = list.sumOf { if (it.isIncome) it.amount else -it.amount }
        tvBalance.text = "مانده: ${formatNumber(balance)} ریال"
    }

    private fun formatNumber(number: Long): String {
        return java.text.NumberFormat.getInstance().format(number)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(this, AddTransactionActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
