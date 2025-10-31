package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter(mutableListOf())
        recyclerView.adapter = adapter

        loadTransactions()
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@MainActivity)
            val prefs = getSharedPreferences("org.hesab.app_preferences", MODE_PRIVATE)
            val sortDescending = prefs.getBoolean("sort_descending", false)

            val transactions = db.transactionDao().getAll().let {
                if (sortDescending) it.reversed() else it
            }

            runOnUiThread {
                adapter.setData(transactions)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }
}
