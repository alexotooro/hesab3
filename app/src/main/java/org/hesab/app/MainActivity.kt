package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var btnAddTransaction: Button

    private var lastTapTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)

        recyclerView.layoutManager = LinearLayoutManager(this)
        loadTransactions()

        btnAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val now = System.currentTimeMillis()
                if (now - lastTapTime < 300) { // دابل کلیک
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

    override fun onBackPressed() {
        if (adapter.isMoveMode()) {
            adapter.setMoveMode(false)
            Toast.makeText(this, "حالت جابجایی غیرفعال شد", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun loadTransactions() {
        Thread {
            val transactions = db.transactionDao().getAll().toMutableList()
            runOnUiThread {
                adapter = TransactionAdapter(
                    this,
                    transactions,
                    onEdit = { /* ویرایش */ },
                    onDelete = { /* حذف */ }
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
            }
        }.start()
    }
}
