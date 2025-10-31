package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var transactionDao: TransactionDao
    private lateinit var btnMenu: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewTransactions)
        btnMenu = findViewById(R.id.btnMenu)
        transactionDao = AppDatabase.getDatabase(this).transactionDao()
        recyclerView.layoutManager = LinearLayoutManager(this)

        // بارگذاری لیست تراکنش‌ها
        loadTransactions()

        // دکمه افزودن تراکنش جدید
        findViewById<ImageButton?>(R.id.btnAddTransaction)?.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        // دکمه سه‌نقطه بالا سمت چپ
        btnMenu.setOnClickListener { showPopupMenu(it) }
    }

    private fun showPopupMenu(anchor: android.view.View) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_main, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val transactions = transactionDao.getAll()
            runOnUiThread {
                adapter = TransactionAdapter(transactions.toMutableList(), transactionDao)
                recyclerView.adapter = adapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }
}
