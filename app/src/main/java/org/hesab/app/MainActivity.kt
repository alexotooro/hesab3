package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Spinner
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
    private lateinit var btnAdd: Button
    private lateinit var spinnerBanks: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // toolbar
val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
if (toolbar != null) {
    setSupportActionBar(toolbar)
}



        btnAdd = findViewById(R.id.btnAddTransaction)
        spinnerBanks = findViewById(R.id.spinnerBanks)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // دیتابیس (از getInstance استفاده می‌شود)
        db = AppDatabase.getInstance(this)

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        loadTransactions()
    }

    override fun onResume() {
        super.onResume()
        // بعد از بازگشت از AddTransactionActivity لیست را تازه می‌کنیم
        loadTransactions()
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.transactionDao().getAll().toMutableList()
            launch(Dispatchers.Main) {
                adapter = TransactionAdapter(list, onEdit = { /* future */ }, onDelete = { txn ->
                    // حذف تراکنش (هم در DB و هم در لیست)
                    CoroutineScope(Dispatchers.IO).launch {
                        db.transactionDao().delete(txn)
                        val idx = list.indexOfFirst { it.id == txn.id }
                        if (idx >= 0) {
                            list.removeAt(idx)
                            launch(Dispatchers.Main) { adapter.notifyDataSetChanged() }
                        }
                    }
                })
                recyclerView.adapter = adapter
                updateBalance(list)
            }
        }
    }

    private fun updateBalance(list: List<Transaction>) {
        var balance = 0L
        for (t in list) {
            balance += if (t.isIncome) t.amount else -t.amount
        }
        val tv = findViewById<android.widget.TextView>(R.id.tvBalance)
        tv.text = "مانده: ${formatNumber(balance)} ریال"
    }

    private fun formatNumber(number: Long): String {
        return java.text.NumberFormat.getInstance().format(number)
    }

    // منو بالا (آیکن +)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            startActivity(Intent(this, AddTransactionActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
