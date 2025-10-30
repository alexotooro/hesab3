package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var btnAddTransaction: Button
    private var isReorderMode = false
    private var lastClickTime = 0L
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        recyclerView.setOnClickListener {
            val now = System.currentTimeMillis()
            if (now - lastClickTime < 400 && isReorderMode) {
                disableReorderMode()
            }
            lastClickTime = now
        }

        loadTransactions()
    }

    override fun onBackPressed() {
        if (isReorderMode) disableReorderMode()
        else super.onBackPressed()
    }

    private fun enableReorderMode() {
        isReorderMode = true
    }

    private fun disableReorderMode() {
        isReorderMode = false
    }

    private fun loadTransactions() {
        Thread {
            val transactions = db.transactionDao().getAll().toMutableList()
            runOnUiThread {
                adapter = TransactionAdapter(
                    this,
                    transactions,
                    onEdit = { /* ویرایش */ },
                    onDelete = { /* حذف */ },
                    onReorderRequested = { enableReorderMode() }
                )

                recyclerView.adapter = adapter

                touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return if (isReorderMode) {
                            adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                            true
                        } else false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
                })

                touchHelper.attachToRecyclerView(recyclerView)
                adapter.attachTouchHelper(touchHelper)
            }
        }.start()
    }
}
