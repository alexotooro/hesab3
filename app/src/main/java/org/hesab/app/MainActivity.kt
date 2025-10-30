package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var btnAddTransaction: Button
    private lateinit var tvBalance: TextView

    private var lastTapTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        tvBalance = findViewById(R.id.tvBalance)

        recyclerView.layoutManager = LinearLayoutManager(this)

        btnAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val now = System.currentTimeMillis()
                if (now - lastTapTime < 300) {
                    if (adapter.isMoveMode()) {
                        adapter.setMoveMode(false)
                        Toast.makeText(this, "حالت جابجایی غیرفعال شد", Toast.LENGTH_SHORT).show()
                    }
                }
                lastTapTime = now
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactionsAndScrollToTop()
    }

    private fun loadTransactionsAndScrollToTop() {
        Thread {
            val transactions = db.transactionDao().getAll().toMutableList()
            runOnUiThread {
                adapter = TransactionAdapter(
                    this,
                    transactions,
                    onEdit = { transaction ->
                        val intent = Intent(this, AddTransactionActivity::class.java)
                        intent.putExtra("edit_transaction_id", transaction.id)
                        startActivity(intent)
                    },
                    onDelete = { transaction ->
                        Thread {
                            db.transactionDao().delete(transaction)
                            runOnUiThread { loadTransactionsAndScrollToTop() }
                        }.start()
                    },
                    onOrderChanged = { updatedList -> saveOrderToDatabase(updatedList) }
                )

                recyclerView.adapter = adapter

                val touchHelper = ItemTouchHelper(object :
                    ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
                })
                touchHelper.attachToRecyclerView(recyclerView)
                adapter.attachTouchHelper(touchHelper)

                updateBalance(transactions)

                // 📌 بعد از بارگذاری، اسکرول کن بالا (تازه‌ترین تراکنش)
                if (transactions.isNotEmpty()) {
                    recyclerView.scrollToPosition(0)
                }
            }
        }.start()
    }

    private fun updateBalance(transactions: List<Transaction>) {
        var totalIncome = 0L
        var totalExpense = 0L
        for (t in transactions) {
            if (t.type == "درآمد") totalIncome += t.amount
            else totalExpense += t.amount
        }
        val balance = totalIncome - totalExpense
        tvBalance.text = "مانده: %,d ریال".format(balance)
    }

    private fun saveOrderToDatabase(updatedList: List<Transaction>) {
        Thread {
            updatedList.forEachIndexed { index, transaction ->
                db.transactionDao().updateOrder(transaction.id, index)
            }
        }.start()
    }
}
