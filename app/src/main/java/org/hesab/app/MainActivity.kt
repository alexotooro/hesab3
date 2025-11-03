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
