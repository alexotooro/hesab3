package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddTransaction: Button
    private lateinit var tvBalance: TextView
    private lateinit var db: AppDatabase
    private lateinit var transactionDao: TransactionDao
    private lateinit var adapter: TransactionAdapter

    private var transactions = mutableListOf<Transaction>()
    private var moveModeActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        tvBalance = findViewById(R.id.tvBalance)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "transactions-db"
        ).allowMainThreadQueries().build()
        transactionDao = db.transactionDao()

        transactions = transactionDao.getAll().toMutableList()

        adapter = TransactionAdapter(
            this,
            transactions,
            onEdit = { transaction ->
                val intent = Intent(this, AddTransactionActivity::class.java)
                intent.putExtra("transaction_id", transaction.id)
                startActivity(intent)
            },
            onDelete = { transaction ->
                transactionDao.delete(transaction)
                refreshData()
            },
            onOrderChanged = { newList ->
                // ذخیره‌ی ترتیب جدید در DB
                for ((index, item) in newList.withIndex()) {
                    item.orderIndex = index
                    transactionDao.update(item)
                }
            },
            onMoveModeChanged = { active ->
                moveModeActive = active
                if (active) {
                    Toast.makeText(
                        this,
                        "حالت جابجایی فعال است. برای خروج، روی لیست دابل‌کلیک کنید یا دکمه بازگشت را بزنید.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.attachToRecyclerView(recyclerView)

        // دکمه افزودن تراکنش
        btnAddTransaction.setOnClickListener {
            if (moveModeActive) {
                Toast.makeText(this, "ابتدا از حالت جابجایی خارج شوید.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        // واکنش به دکمه برگشت برای خروج از حالت جابجایی
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (moveModeActive) {
                    adapter.disableMoveMode()
                    moveModeActive = false
                } else {
                    finish()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        transactions.clear()
        transactions.addAll(transactionDao.getAll())
        adapter.notifyDataSetChanged()

        val balance = transactionDao.getAll().sumOf {
            if (it.type == "income") it.amount else -it.amount
        }
        tvBalance.text = "مانده: %,d ریال".format(balance)
    }
}
