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
    private lateinit var txtMoveBanner: TextView

    private var lastTapTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        txtMoveBanner = findViewById(R.id.txtMoveBanner)

        recyclerView.layoutManager = LinearLayoutManager(this)
        txtMoveBanner.text = "حالت جابجایی فعال است. برای خروج، روی لیست دابل‌کلیک کنید یا دکمه بازگشت را بزنید."
        txtMoveBanner.visibility = TextView.GONE

        btnAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val now = System.currentTimeMillis()
                if (now - lastTapTime < 300 && adapter.isMoveMode()) {
                    adapter.setMoveMode(false)
                    showMoveModeBanner(false)
                    Toast.makeText(this, "حالت جابجایی غیرفعال شد", Toast.LENGTH_SHORT).show()
                }
                lastTapTime = now
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun loadTransactions() {
        Thread {
            val transactions = db.transactionDao().getAll().toMutableList().asReversed()

            runOnUiThread {
                adapter = TransactionAdapter(
                    this,
                    transactions,
                    onEdit = { /* TODO: ویرایش */ },
                    onDelete = { /* TODO: حذف */ },
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
                        if (adapter.isMoveMode()) {
                            adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                        }
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
                })
                touchHelper.attachToRecyclerView(recyclerView)
                adapter.attachTouchHelper(touchHelper)
            }
        }.start()
    }

    fun showMoveModeBanner(show: Boolean) {
        txtMoveBanner.visibility = if (show) TextView.VISIBLE else TextView.GONE
    }

    private fun saveOrderToDatabase(updatedList: List<Transaction>) {
        Thread {
            updatedList.forEachIndexed { index, transaction ->
                db.transactionDao().updateOrder(transaction.id, index)
            }
        }.start()
    }

    override fun onBackPressed() {
        if (this::adapter.isInitialized && adapter.isMoveMode()) {
            adapter.setMoveMode(false)
            showMoveModeBanner(false)
            Toast.makeText(this, "حالت جابجایی غیرفعال شد", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }
}
